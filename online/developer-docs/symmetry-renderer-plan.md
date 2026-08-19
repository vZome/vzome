# Plan: Replace `ShapedGeometry` with `symmetry-renderer.js`

## Background: What Each System Does

**`ShapedGeometry` (current)**
- A SolidJS reactive component using solid-three
- Per-shape, creates a `BufferGeometry` from `{vertices, faces}`
- Per-instance, renders a `<T.Mesh>` with individual material + raycasting event handlers
- No GPU instancing — every ball/strut is a separate Three.js object
- `MeshLambertMaterial`, selection via emissive color
- Interaction via solid-three raycasting events (hover, click, drag, contextMenu)

**`createSymmetryRenderer` (target)**
- Imperative Three.js API using `InstancedMesh` (GPU instancing)
- Requires: symmetry groups with pre-registered `Matrix4[]` orientations
- Instances reference an `orientationIndex` + `colorIndex` (not per-instance materials)
- Uses WebGPU TSL materials (`MeshPhongNodeMaterial`)
- GPU-based picking via offscreen rendering pass (`pickAt()`)
- Highlight via `setInstanceHighlight()`

---

## Data Contract Gap (Worker → Client)

Currently `prepareSceneResponse` sends each instance with a `rotation` (full 16-element matrix)
but does **not** send the `orientations` array. The symmetry-renderer needs the orientations array
upfront plus an index per instance.

Each instance already carries `orientation` (index) in the raw data, so the only missing piece is
the `orientations` array in the response payload.

---

## Migration Plan

### Phase 1 — Extend the worker contract
In `vzome-worker-static.js`, add `orientations` to the `prepareSceneResponse` return value
(alongside `shapes`, `embedding`, `polygons`). The `rotation` matrix per instance can be dropped
once the new renderer is proven, but keep it for now to avoid breaking anything.

### Phase 2 — Extract geometry builder
Move the `BufferGeometry`-building logic from `InstancedShape` in `geometry.jsx` into a standalone
utility function `buildShapeGeometry(shape)`. This is the same logic, just decoupled from SolidJS.
The outline geometry builder likewise.

### Phase 3 — Create a `SymmetryGeometry` component
A new SolidJS component (in `geometry.jsx` or a new file) that:
1. Gets the Three.js `scene` via `useThree()`
2. Calls `createSymmetryRenderer(scene)` on mount, stores it in a ref
3. Registers one symmetry group from the `orientations` array (passed as a prop or from the scene store)
4. Registers one style (`"default"`)
5. For each shape: calls `registerShape(groupId, styleId, shapeId, geometry)`
6. Calls `registerColor(color)` for each unique color seen across instances, builds a Map from CSS string → colorIndex
7. Calls `switchSymmetryGroup(groupId)`
8. Adds all instances via `addInstance(styleId, shapeId, { position, orientationIndex, colorIndex })`
9. Reacts to instance changes (via `createEffect`) to call `removeAllInstances` + re-add

**Embedding matrix**: Apply it directly to `symmetryRenderer.originGroup.matrix` using the same
imperative `applyMatrix4` pattern already in `ShapedGeometry`.

### Phase 4 — Handle polygon mode (styles)
Register two styles: `"solid"` and `"polygons"`. Build both geometries per shape (current
triangulated-fan vs. the existing polygon-mode path). React to `scene.polygons` signal by calling
`switchStyle()`.

### Phase 5 — Wire GPU picking for interaction
This is the most architectural change. The `useInteractionTool` hook expects
`(id, position, type, selected, label)` callbacks. Bridge:
1. Maintain a Map from symmetry-renderer's integer `instanceId` → instance metadata `{id, position, type, selected, label}`
2. On canvas `pointerdown`/`click`/`contextmenu`: call `pickAt(x, y, renderer, camera)` to get
   `{shapeId, instanceId}` → look up metadata → call the appropriate tool handler
3. Hover: call `pickAt` on `pointermove` (debounced) to get enter/leave events
4. Drag: on `pointerdown` hit, start a drag tracking sequence

This replaces all the per-mesh solid-three event handlers in `Instance`.

### Phase 6 — Handle selected/highlight state
React to `instance.selected` changes: call `setInstanceHighlight(shapeId, instanceId, 0.5)` or `0`
accordingly.

### Phase 7 — Labels
Labels are currently rendered by `<Label>` positioned at `geometry.shapeCentroid`. This still
works independently — the label system is in `labels.jsx` and doesn't depend on how geometry is
rendered. We'd need to keep/replicate label placement for instances that have labels.

### Phase 8 — Replace usages
Once `SymmetryGeometry` is proven, replace `<ShapedGeometry>` in:
- `online/src/viewer/scenecanvas.jsx`
- `online/src/app/59icosahedra/stellation.jsx`

---

## Risk Areas / Open Questions

| Issue | Difficulty | Notes |
|-------|-----------|-------|
| Worker contract change (add `orientations`) | Low | Additive only |
| Geometry building (reuse existing logic) | Low | Already works |
| Color palette registration | Low | Collect unique colors across all instances |
| Embedding matrix on `originGroup` | Medium | `setOrigin` only does position; need to set `originGroup.matrix` directly like current code does |
| Polygon mode via `switchStyle` | Medium | Requires two geometry builds per shape |
| GPU picking vs. raycasting | High | Async API, requires canvas event wiring, replaces all per-instance events |
| Drag interaction | High | Drag requires knowing what was hit at `pointerdown`, then tracking position |
| Labels | Medium | Need alternate positioning mechanism not tied to per-mesh refs |
| `stellation.jsx` (59 Icosahedra) — many scenes | Medium | Multiple `SceneProvider` instances, each needing its own renderer |

---

## Recommended Starting Point

Start with Phase 1–3 rendering-only (no interaction), verified by visual inspection. Temporarily
disable interaction for the new component (the existing `Instance` component with all its event
handlers can stay in `geometry.jsx` as a fallback). Once you confirm the rendering matches, tackle
Phase 5 (picking) as a self-contained effort.

---

## Viewer vs. Editor Integration

### The Core Difference: Symmetry is Static vs. Dynamic

**Viewer**: One symmetry group, fixed for the life of the scene. `SYMMETRY_CHANGED` fires once
during file load (stored as `scene.orientations`), then never again.

**Editor**: The user can switch symmetry at runtime (e.g. icosahedral → octahedral).
`SYMMETRY_CHANGED` fires again with a completely new `orientations` array. After a switch,
`SCENE_RENDERED` fires to replace all instances — the new instances reference orientation indices
into the *new* orientations array.

### What `SYMMETRY_CHANGED` Carries (That the Viewer Ignores)

`SceneChangeListener` currently discards most of the `SYMMETRY_CHANGED` payload:
```js
subscribeFor( 'SYMMETRY_CHANGED', ( { orientations } ) => {
  setScene( 'orientations', orientations );
  // resourcePath, permutations, scalars, planes — all discarded
});
```
The `resourcePath` (e.g. `"icosahedral"`, `"octahedral"`) uniquely identifies the symmetry
perspective and is the natural **`groupId`** for `registerSymmetryGroup()`. It is currently lost.

### The Update Pattern Difference

| Event | Viewer uses | Editor uses |
|-------|-------------|-------------|
| `SCENE_RENDERED` | Always (full replace) | After load + after symmetry switch |
| `INSTANCE_ADDED` / `INSTANCE_REMOVED` | Never (snapshot-based) | Every user edit |
| `SELECTION_TOGGLED` | Never | Every selection change |
| `SYMMETRY_CHANGED` | Once (orientation matrix) | On every symmetry switch |
| `SHAPE_DEFINED` | Never | When a new shape type first appears |

The editor's incremental events (`INSTANCE_ADDED`/`INSTANCE_REMOVED`) map **directly** to
`addInstance()`/`removeInstance()` on the renderer — no need to rebuild the whole scene on each
edit. This is the main performance win the editor can exploit that the viewer cannot.

### `SELECTION_TOGGLED` → `setInstanceHighlight()`

Currently `SELECTION_TOGGLED` rebuilds the entire instances array reactively. With the renderer,
it maps directly to `setInstanceHighlight(shapeId, instanceId, intensity)` — no geometry rebuild,
just a buffer update.

---

## Proposed Integration Architecture

### No Canvas Nesting Conflict

A naive design might put a bridge component *outside* `<Canvas>` that calls the renderer API
created *inside* `<Canvas>`. That would fail because the renderer object never escapes Canvas
scope.

The resolution: **`useWorkerClient()` is just a regular SolidJS context. It can be called from
inside Canvas components, exactly like `useScene()` already is in `ShapedGeometry`.** There is
no Canvas boundary for SolidJS contexts — only for `useThree()` (which requires a Three.js
Canvas provider). The two kinds of context are independent.

Therefore `SymmetryGeometry` is a single component (inside Canvas) that calls both:
- `useThree()` — to get the Three.js scene and create the renderer
- `useWorkerClient()` + `subscribeFor()` — to subscribe to worker events directly

All event wiring is co-located inside `SymmetryGeometry`. No external bridge component is needed.

### Two Behavioural Modes Within One Component

`SymmetryGeometry` selects its event-handling strategy via a `mode` prop (`"viewer"` or
`"editor"`):

**Viewer mode (simple)**
1. On mount: register one group from `scene.orientations`, call `switchSymmetryGroup()`
2. React to `scene.shapes` changes (full replace): clear + re-add all instances

**Editor mode (incremental)**
Subscribe to raw worker events and call the renderer API directly:

| Worker event        | Renderer call |
|---------------------|---------------|
| `SHAPE_DEFINED`     | `buildShapeGeometry()` + `registerShape()` |
| `INSTANCE_ADDED`    | `addInstance()` (also store metadata for picking) |
| `INSTANCE_REMOVED`  | `removeInstance()` |
| `SELECTION_TOGGLED` | `setInstanceHighlight()` |
| `SYMMETRY_CHANGED`  | `registerSymmetryGroup()` (if new) + `switchSymmetryGroup(resourcePath)` |
| `SCENE_RENDERED`    | `clearActiveInstances()` + re-add all |

This also maintains the metadata map `(rendererInstanceId → {id, position, type, selected, label})`
needed for GPU picking.

### What Stays the Same in Both Modes
- Geometry building (`buildShapeGeometry`) is identical
- Embedding matrix application to `originGroup` is identical
- Polygon mode (`switchStyle()`) works the same way

---

## Revised Phase Order

**Phase 0 (editor-only prep — before Phase 3)**:
- Pass `resourcePath` through `SYMMETRY_CHANGED` in the worker so `SymmetryGeometry` can use it
  as `groupId` for `registerSymmetryGroup()`
- Optionally: add a `SYMMETRIES_AVAILABLE` worker event at design load so all groups can be
  pre-registered upfront (enables instant symmetry switching with no GPU recompilation mid-session)

The viewer path (Phases 1–8) remains valid and simpler — it uses `scene.shapes` reactively since
it always does full scene replaces.
