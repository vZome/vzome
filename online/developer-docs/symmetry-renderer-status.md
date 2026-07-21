# SymmetryGeometry Status — Phases 4/6/7 Complete; Phase 5 Deliberately Deferred

Branch: `webgpu-try`. This document is a handoff snapshot for continuing this work in a
fresh session. See also `symmetry-renderer-plan.md` (the original phase plan) and
`worker-client-scene-protocol.md` (a deep dive on the worker↔client wire protocol, written
partway through Phase 3 debugging — still accurate and worth reading before touching
scene-loading code).

## What's done

**Phase 1 (worker contract)** — `prepareSceneResponse`/`prepareEditSceneResponse` in
`online/src/worker/vzome-worker-static.js` return `orientations` alongside the existing
per-instance `rotation` (still computed too, redundantly — see "Known redundancy" below).

**Phase 2 (geometry extraction)** — `buildShapeGeometry`/`buildOutlineGeometry` extracted
from `ShapedGeometry`'s reactive memo bodies into standalone, exported functions in
`online/src/viewer/geometry.jsx`. Pure refactor, verified byte-identical logic.

**Phase 3 (rendering-only `SymmetryGeometry`)** — **complete and verified working** on every
canvas actually tested:
- Trackball preview (`camera.jsx`'s `CameraControlsUI`)
- Main classic editor canvas (`editor.jsx`)
- Standalone viewer / web-component embed (`SceneViewer` in `online/src/viewer/index.jsx`),
  including a named-scene-on-first-load case

New file: `online/src/viewer/symmetry-geometry.jsx`. Wired in as an opt-in prop:
`<SceneCanvas symmetryRenderer={true} ...>` in `online/src/viewer/scenecanvas.jsx` (default
`false`, still using `ShapedGeometry`). As of Phase 7: polygon-mode fill + outline (Phase 4),
selection highlight (Phase 6), and labels (Phase 7) are all done. Only picking/interaction
(Phase 5) is not — deliberately deferred at the user's direction, since it's only needed for
*editing* use cases (classic editor, `59icosahedra`), which stay on `ShapedGeometry` for now;
`SymmetryGeometry` is being scoped for *viewer-only* (non-editing) release first. See the
Phase 6/7 sections below for the full rationale and what (if anything) is still missing for
that release.

**Not yet tested**: the 59 Icosahedra stellation view (`online/src/app/59icosahedra/`), any
other `<SceneCanvas>` call sites (`selectors.jsx`, `scenes.jsx` dialog).

## Real bugs found and fixed along the way

These were not hypothetical — each cost significant debugging time and is documented inline
in the code with comments explaining the failure mode, specifically so they don't get
"simplified away" by a future edit. Read the comments in place; this list is just an index.

1. **Port bug**: `symmetry-renderer.js` (mechanically ported from `webxr-poc`) had a
   duplicated-line typo in `registerSymmetryGroup`'s `Euler`-vs-`Matrix4` branch. Fixed.

2. **Wrong import source**: `MeshBasicNodeMaterial`/`MeshPhongNodeMaterial` don't exist in
   plain `"three"`, only `"three/webgpu"`. Fixed the import; confirmed the other classes
   (`Matrix4`, `Vector3`, etc.) are the same class identities either way, so no
   `instanceof`-mismatch risk from mixing sources with the rest of the app.

3. **`orientations` missing on the wire in three separate client-side call sites** (a
   structural flaw — no single "apply scene response" function; see
   `worker-client-scene-protocol.md` for the full four-path breakdown):
   - `online/src/app/classic/components/camera.jsx` — the trackball preview's own
     `TRACKBALL_SCENE_LOADED` handler.
   - `online/src/viewer/context/scene.jsx`'s `SceneChangeListener` (`SCENE_RENDERED`
     subscription) — used by classic editor + buildplane apps.
   - `online/src/viewer/context/scene.jsx`'s `SceneIndexingProvider.showIndexedScene` — used
     by the standalone viewer / web-component path, which never mounts
     `SceneChangeListener` at all.

4. **`SceneTitlesProvider`'s `sceneTitle` signal frozen at `undefined` forever** — seeded
   from `scenes` (async, from the worker) before that data arrives; SolidJS signals don't
   re-run their initializer. `setSceneTitle` was exported but never called anywhere. Caused
   named-scene loads to silently fall back to scene index 0 on first paint. Fixed with a
   `createEffect` in `scene.jsx` that corrects it once `scenes` populates, guarded to not
   clobber a deliberately-set value. **This is a general SolidJS foot-gun pattern** — any
   other signal seeded from not-yet-loaded async `useViewer()` data is a candidate for the
   same bug; worth a grep if time allows.

5. **`originGroup.matrixAutoUpdate` needed to be `false`** — `createSymmetryRenderer`'s
   `originGroup` is a plain `THREE.Group()`; without disabling auto-update, Three's per-frame
   matrix recompute silently discards any `applyMatrix4`/`.matrix.copy()` we do for the
   embedding transform. `ShapedGeometry`'s equivalent wrapper (`<T.Group
   matrixAutoUpdate={false}>` in `geometry.jsx`) already does this — `symmetry-renderer.js`
   didn't, and needed to.

6. **The big one — `globalScale` not applied, everything silently clipped by the far
   plane**: `ShapedGeometry`'s content gets scaled by `globalScale` (≈0.0088,
   meters-per-vZome-unit) *implicitly*, as a side effect of being JSX-nested inside
   `WebXRSupport`'s `<T.Group scale={globalScale}>` (`online/src/viewer/context/webxr.jsx`).
   `SymmetryGeometry` instead attaches its `originGroup` straight to the raw Three.js
   `scene` via `createSymmetryRenderer(scene) → scene.add(originGroup)`, bypassing that
   wrapper entirely. Without compensating, vZome-unit-scale content (tens of units) sat far
   outside the camera's meters-scaled near/far planes and was silently far-plane-clipped —
   no error, no warning, correct-looking draw calls, compiled shaders, real buffer data,
   just nothing on screen. **This single bug consumed the majority of the Phase 3
   debugging session.** Fixed in `symmetry-geometry.jsx` by building `originGroup.matrix`
   explicitly (required per #5): `globalScale * embeddingMatrix`, matching the same
   effective transform order `ShapedGeometry` gets for free from its ancestor group. A
   *first* attempt at this fix (`originGroup.scale.setScalar(globalScale)`) silently did
   nothing at all, precisely because of #5 — matrixAutoUpdate being off means `.scale` is
   never read. Both mistakes (and the working fix) are documented in
   `symmetry-geometry.jsx`'s comments.

7. **Frustum culling per shape class** — `InstancedMesh`'s default frustum culling computes
   its bounding sphere from the un-instanced base geometry only (e.g. one ball near the
   local origin), with no knowledge of the per-instance `instanceTranslation` scattering
   applied in the `positionNode` vertex shader. Zooming in could tighten the frustum past
   that tiny local bounding sphere well before the actual (scattered) instances left view,
   culling **every instance of that one shape at once** — "all the balls disappear,
   struts remain," at some zoom threshold, per shape class independently. Fixed by setting
   `frustumCulled = false` on every mesh (and picking mesh) in both `createShapeEntry` and
   `ensureShapeCapacity` (the capacity-growth path, whose replacement meshes don't inherit
   the flag). **This fix has also been back-ported to `webxr-poc`** (the standalone POC,
   easier to test in real XR) and confirmed fixed there by deployment/testing.

## Known-open items — not yet fixed, flagged for later

- **Labels don't appear in XR** — confirmed by the user. `labels.jsx`'s `Labels`/`Label`
  render via `CSS2DRenderer` (`three-stdlib`), which positions plain DOM elements over the
  canvas using CSS transforms computed from each object's projected screen position. This is
  fundamentally a 2D-overlay technique tied to the regular (non-XR) canvas/DOM layer — it has
  no meaning once a WebXR immersive session takes over the display, since there is no
  screen-space DOM overlay to position elements within. Not a bug to fix in `labels.jsx`
  itself; a real XR label solution would need to render label geometry directly in the 3D
  scene (e.g. `CSS2DObject`'s sibling `CSS3DObject` doesn't help either, same limitation —
  actual WebGL/WebGPU-rendered text/billboards, likely via `three-stdlib`'s `Text`/SDF-font
  helpers or a custom textured-quad approach, would be needed). Out of scope for the current
  viewer-only `SymmetryGeometry` release; flagged for whenever XR + labels together matters.

- **Outline thickness is wrong at both zoom extremes** — confirmed by the user after Phase 4
  outline toggling was verified working. `symmetry-renderer.js`'s outline mesh
  (`makeOutlineMesh`, `isLineSegments`-forced `InstancedMesh`) renders as native GL lines,
  which draw at a fixed **screen-space** pixel width, constant regardless of zoom or model
  scale. User confirmed this is exactly the symptom: outlines look hairline/messy against a
  zoomed-in (large on-screen) face, and look like a thick dominating smear against a
  zoomed-out (small on-screen) face — i.e. the line's on-screen size never changes while the
  polygon's does, so the *ratio* between them swings across the usable zoom range. Not a bug
  in the sense of wrong output, just the wrong rendering technique for this use case — a
  world-space-scaling ("model space") line was wanted, not a screen-space one, so it grows
  and shrinks with the geometry the same way the fill does.

  Two solution directions were discussed, not yet decided or implemented:
  1. **World-space thickened outline** (leaning direction, not yet committed to). Replace the
     bare `LineSegments`-mode geometry with an actual thin 3D ribbon/strip geometry along
     each edge — built alongside or instead of `buildOutlineGeometry` in `geometry.jsx` —
     sized as a fraction of the shape's own dimensions. Renders through the normal triangle
     `InstancedMesh` pipeline like the fill does, so it would also retire the
     `isLineSegments`-forcing hack in `makeOutlineMesh` entirely (see bug-3 writeup below for
     why that hack exists) since it's no longer needed. More implementation work: new
     geometry-building logic (ribbon/tube around each edge, care needed with mitering at
     vertices), more triangles per shape, and the ribbon width itself becomes a new tunable
     (probably relative to the shape's bounding sphere or a global outline-width setting).
  2. **Fat screen-space lines via `three-stdlib`'s `Line2`/`LineMaterial`** (already a project
     dependency — used elsewhere for `GLTFExporter` in `geometry.jsx`). This is the standard
     three.js "fat line" technique (`LineSegmentsGeometry` + a screen-space-width shader) and
     would make the pixel width at least *controllable*, but it is still fundamentally
     screen-space — it would not fix the underlying complaint (ratio to polygon size swinging
     with zoom), only let you pick a better-tuned fixed pixel width. Also not obviously
     compatible with per-instance GPU instancing as currently architected (`Line2` is built
     around a single non-instanced `LineSegmentsGeometry`); adapting it to
     `symmetry-renderer.js`'s per-shape `InstancedMesh` model would need investigation, not
     assumed to be straightforward.

  Direction 1 is the more likely fix if/when this gets picked up, since it addresses the
  actual complaint rather than a narrower version of it, but no decision has been made yet —
  ask the user before implementing either.

- **59 Icosahedra (`stellation.jsx`) `ShapedGeometry` renders nothing**, found while testing
  Phase 3 on unrelated views. `stellation.jsx` and its dependency `state.jsx` are unmodified
  relative to `main` (`git diff main -- online/src/app/59icosahedra/` is empty), and the
  `SymmetryGeometry` work never touches this file. Strong suspicion: fallout from the
  pre-existing `3bdfe6c98` ("Forcing WebGL") commit, which significantly restructured
  `LightedTrackballCanvas`/`ltcanvas.jsx` (new `solid-three` XR context API,
  `xr.Provider`/`createXR`) — `stellation.jsx` uses `LightedTrackballCanvas` directly. **Not
  yet root-caused.** Set aside deliberately, at the user's direction, to keep Phase 3 scoped.

- **Trackball preview drag gesture not working** under `symmetryRenderer={true}` — mentioned
  once in passing during debugging, never followed up. Unclear if still an issue after later
  fixes (in particular the frustum-culling fix landed after this was mentioned).

- **`>180` `GL_INVALID_FRAMEBUFFER_OPERATION` warnings under `ShapedGeometry`** on the
  trackball canvas specifically (confirmed absent under `SymmetryGeometry`) — noted once,
  never investigated. Possibly related to the same `3bdfe6c98` WebGL-forcing changes.

- **`worker-client-scene-protocol.md`'s own recommendations**, not yet acted on:
  - No consolidated "apply scene response to store" helper — four independent call sites
    (three plus the trackball's) each hand-write the same `setScene('embedding'/'polygons'/
    'orientations', ...)` + `updateShapes(...)` sequence. A future wire-protocol field
    addition means finding and fixing every one by hand again, exactly as `orientations`
    did.
  - No dev-mode assertion/warning for a mounted `SymmetryGeometry` observing populated
    `shapes` but empty/undefined `orientations` after a reasonable delay — this is precisely
    the failure signature that took longest to diagnose (silent, looks like a rendering bug,
    is actually a data bug).
  - `rotation` is still sent redundantly alongside `orientations` on every instance, per an
    existing worker-code `TODO` (`vzome-worker-static.js`) — correct to keep for now since
    `ShapedGeometry` still needs it, but real payload bloat once `ShapedGeometry` retires.
  - The `SceneTitlesProvider` frozen-signal pattern (item 4 above) may have siblings
    elsewhere in the codebase — not grepped for yet.

## Phase 4 — polygon-mode outline overlay — COMPLETE

**Status: done. User tested edge cases (capacity growth, rapid toggling) and is satisfied.**
Fill geometry renders correctly, outline toggle (Settings dialog "Outlines" switch, gated on
the scene actually having polygon data) shows/hides correctly. Three real bugs were found
and fixed across three rounds of real-browser testing to get here; see the detailed writeup
below for what they were, in case similar mistakes recur elsewhere in this codebase. One
cosmetic issue remains, deliberately deferred rather than blocking Phase 4: outline line
thickness (see "Known-open items" above) — wrong technique (screen-space GL lines) for the
desired effect (thickness that scales with the model), not incorrect in the sense of broken
output. Move on to Phase 5 next.

**Scope grew beyond the original plan doc.** The plan's Phase 4 section called for two fill
styles (`"solid"`/`"polygons"`) switched via `switchStyle()`. Actual `ShapedGeometry`
(`InstancedShape` in `geometry.jsx`) does more in polygon mode, though: it also overlays a
black wireframe outline (`buildOutlineGeometry`, true face-edge topology) on top of the fill.
User confirmed (via `AskUserQuestion`) they wanted pixel-fidelity with `ShapedGeometry`, not
just fill-switching, so outline support was added too.

**Key technical finding, still believed correct**: `InstancedMesh` can't draw as
`LineSegments` through any public Three.js API — there's no constructor for an instanced
line-list mesh. But both render backends (`WebGPUUtils.getPrimitiveTopology` and the WebGL
fallback's equivalent dispatch in `WebGLBackend.js`) select line-list topology by checking
`object.isLineSegments` *before* `object.isMesh`, and `InstancedMesh extends Mesh` without
setting `isLineSegments` itself — so forcing `mesh.isLineSegments = true` on an otherwise-
normal `InstancedMesh` is sufficient to make it draw as instanced `GL_LINES`/`LineList`.
Verified by reading both backends' dispatch source
(`node_modules/three/src/renderers/webgpu/utils/WebGPUUtils.js:195`,
`node_modules/three/src/renderers/webgl-fallback/WebGLBackend.js:1137`). This part held up —
outlines rendered with correct edge topology on first real test, no diagonal-edge artifacts.

**Bug 1 (crash on load)**: `registerShape` unconditionally did
`entry.outlineMesh.visible = ...` right after `createShapeEntry`, but `entry.outlineMesh` is
`null` whenever a shape's outline geometry hasn't been registered yet (the original two-loop
structure in `symmetry-geometry.jsx` called `registerShape` for a shape *before*
`registerOutline` for that same shape). `TypeError: Cannot set properties of null`. Fixed by
guarding with `if (entry.outlineMesh)`.

**Bug 2 (much bigger — bad initial design, not just a typo)**: the original Phase 4 design
pre-built **two fill geometries** per shape — `buildShapeGeometry(shape, false)` for a
`"solid"` style and `buildShapeGeometry(shape, true)` for a `"polygons"` style — on the
assumption that these were two legitimate alternate looks, matching what the plan doc implied.
They are not. Reading `buildShapeGeometry` in `geometry.jsx` closely: the `polygons=false`
branch (`corners.forEach(addVertex)`) pushes each face's raw corner list straight into a flat
`TRIANGLES`-mode position buffer with **no triangulation at all**. For a triangular face this
happens to produce the right 3 vertices. For any face with more than 3 vertices (pentagons,
hexagons — i.e. most real vZome part shapes), it produces garbage: wrong vertex groupings,
wrong winding, visually "scattered triangles connecting the wrong vertices" (the user's exact
words on the second test). `ShapedGeometry` never hits this because `InstancedShape` builds
exactly *one* geometry, always from the live `scene.polygons` value — it only ever takes the
`false` branch when the whole scene is genuinely in non-polygon mode, and apparently no shape
in easy manual-testing reach has non-triangular faces in that mode, or this would have been
caught long ago. The fan triangulation (`polygons=true`) is geometrically identical to the
non-fan path for already-triangular faces and correct for every other case, so it is always
safe to use unconditionally — **there is no legitimate reason for `SymmetryGeometry` to ever
build the `polygons=false` geometry.** What actually varies with `scene.polygons` is only the
outline overlay's visibility.

Fixed by removing the two-style design entirely:
- `online/src/viewer/symmetry-geometry.jsx` now registers a single style (`STYLE_ID =
  "default"`, same as pre-Phase-4) and builds exactly one fill geometry per shape, always
  `buildShapeGeometry(shape, true)`. `addInstance` always uses that one style, so the
  `ensureStyleIsActiveForInstances`-side-effect footgun that motivated an earlier draft's
  `currentStyleId` computation no longer exists — there's nothing to fight over.
- `online/src/viewer/context/symmetry-renderer.js`: removed the now-pointless
  `OUTLINE_STYLE_ID` export and the outline-visibility-follows-active-style logic in
  `swapGeometriesForStyle`/`attachOutlineMeshToEntry`/`registerShape`. Added a small
  dedicated `setOutlinesVisible(visible)` — loops the active group's `shapeEntries` and sets
  `outlineMesh.visible` directly, independent of style. New outline meshes default to
  `visible = false` at creation (in `makeOutlineMesh`); the three call sites that need
  otherwise (capacity regrowth carrying over the previous mesh's visibility) set it
  explicitly right after.
- The pre-existing `registerStyle`/`switchStyle`/`swapGeometriesForStyle`/multi-style
  machinery (built in Phase 3, general-purpose) was left in place, just unused by
  `symmetry-geometry.jsx` now — it's still there for any future case where two fill styles
  might legitimately diverge.

**Remaining outline implementation notes (still accurate, unaffected by the fixes above)**:
- `registerOutline(groupId, slotId, outlineGeometry)` registers one outline geometry per
  shape, independent of style. Handles both call orderings: after `registerShape` (attaches
  to the existing entry immediately via `attachOutlineMeshToEntry`) and before any GPU state
  exists (stashed in `group.pendingOutlineGeometries`, picked up by `ensureGroupGpu`).
- `makeOutlineMesh(...)` builds the forced-line-mode `InstancedMesh`. Its geometry is a
  separate `BufferGeometry` from the fill mesh's (different vertex/index topology) but its
  instanced attributes (`orientationIndex`, `instanceTranslation`, `highlightIntensity`,
  `pickingId`; deliberately no `colorIndex` — outline is fixed black) wrap the *same*
  underlying typed arrays as the fill mesh, so `syncShapeInstances` never writes instance
  data twice, only flags `needsUpdate` on both geometries' attributes.
- `createShapeEntry`, `ensureShapeCapacity` (capacity-growth path), `addGroupMeshesToScene`,
  `setGroupMeshesCount`, and `disposeGroupGpu` are all threaded through to keep
  `entry.outlineMesh` in sync (added to scene, capacity-grown, count-synced, disposed)
  alongside the existing `mesh`/`pickingMesh` handling.
- `createMaterialForGroup` also returns `outlineMaterial` — `MeshBasicNodeMaterial`, flat
  black `colorNode`, sharing the same `rotatedPositionNode` transform as the other two
  materials. Note: `Material.linewidth` has no effect on regular (non-`Line2`) line
  rendering in WebGL or WebGPU, so outlines are a fixed ~1px regardless of `ShapedGeometry`'s
  `linewidth={4.4}` — an already-existing limitation being carried over, not a regression.

**Bug 3 (outlines never appear, even when the user asks for them)**: after fixing bug 2,
`symmetry-geometry.jsx` called `renderer.setOutlinesVisible(!!props.polygons)` — outline
visibility driven by `scene.polygons` alone, matching `InstancedShape`'s
`showOutlines = () => scene.polygons` in `geometry.jsx` exactly. This looked right (it
matches `ShapedGeometry`'s current code verbatim) but is semantically wrong. Per the user:
`scene.polygons` is **only a capability flag** — whether this scene's face data is real
polygon geometry at all (modern files) vs. pre-triangulated with no recoverable face
structure (older files, `.shapes.json`'s `polygons` field). It is not the user-facing
show/hide state. Confirmed by tracing the worker: `design.rendered.polygons` (decided at
load time by `realizeShape`/`legacy.loadDesign`) flows unmodified through
`prepareSceneResponse`/`prepareEditSceneResponse` into the client `scene` store — a
per-file-format property, not a live toggle. The *actual* end-user preference is
`state.outlines` in `online/src/viewer/context/camera.jsx` (default `false`,
`toggleOutlines()`), surfaced as the "Outlines" switch in `online/src/viewer/settings.jsx`
— which is itself gated on `scene.polygons` for whether to even show the switch
(`showOutlines={scene?.polygons && dynConfig().showOutlines}` in `viewer/index.jsx`). Two
independent conditions, both required: can we (capability) AND does the user want to
(preference, default off).

Notably, `ShapedGeometry`/`InstancedShape` does **not** currently read `state.outlines` at
all — grepped for it, the only consumers of that state are SVG export
(`app/classic/menus/filemenu.jsx`, `app/classic/dialogs/svgpreview.jsx`), not the live 3D
view. So the "Outlines" switch in the Settings dialog currently does nothing for on-screen
rendering in either `ShapedGeometry` or (pre-fix) `SymmetryGeometry` — a pre-existing gap.
Per explicit user direction, `SymmetryGeometry` was fixed to actually honor it (a real
behavior improvement, not just parity with `ShapedGeometry`'s current incomplete wiring):
`online/src/viewer/symmetry-geometry.jsx` now destructures `state: cameraState` from
`useCamera()` (alongside the already-used `globalScale`) and calls
`renderer.setOutlinesVisible(!!props.polygons && !!cameraState.outlines)`. `state` is a
SolidJS store (`createStore` in `camera.jsx`), so property access inside `createEffect`
tracks reactively — toggling `state.outlines` via the Settings dialog now live-updates
outline visibility without needing to touch `symmetry-renderer.js` at all.

**Worth doing later, out of scope for this pass**: consider wiring `ShapedGeometry`'s
`InstancedShape.showOutlines` (`geometry.jsx:182`) to `state.outlines` too, so the classic
editor's non-`SymmetryGeometry` path gets the same fix and the Settings switch isn't
silently inert there.

**Remaining open items**: Phases 5-8 (picking/interaction, highlight, labels, replacing
`ShapedGeometry` call sites) — unchanged from before, see below.

## XR fix — `SymmetryGeometry` content wasn't visible/reachable in a real headset

**Status: implemented (two rounds — first attempt broke the page entirely), not yet
re-tested on-device.** Found by the user testing on a Meta Quest 3 (entering XR via the
existing "Enter XR" button showed no model at all — a console warning about
`menu_pressed_min` was a red herring, an unrelated controller-model-loader complaint).
Root-caused by code reading, not yet confirmed fixed on the headset.

**Root cause**: `createSymmetryRenderer`'s `originGroup` (in `symmetry-renderer.js`) attached
directly to the raw Three.js `scene` (`scene.add(originGroup)`), bypassing
`WebXRSupport`'s own `originGroup` (`online/src/viewer/context/webxr.jsx`, a `<T.Group
scale={globalScale}>`) entirely — this was the Phase 3 fix for bug #6 above (`globalScale`
needing to be manually baked into `SymmetryGeometry`'s matrix to compensate for not being
nested inside that wrapper). What Phase 3 didn't account for: `WebXRSupport`'s `originGroup`
is not just a scale wrapper — it's also the group that `online/src/xr/grip2move.jsx`
(`XRGripToMove`) and `online/src/xr/scaling.jsx` (`XRScaling`) actively reposition and
rescale during an XR session: `XRGripToMove.onViewerStart` places it ~0.7m in front of the
headset at session start and lets the user grab-and-drag it via controller grip; `XRScaling`
lets a two-handed squeeze resize it. Neither of those ever touches `SymmetryGeometry`'s
separate `originGroup`, since they only know about `WebXRSupport`'s. So on entering XR, the
model was very likely still rendering — just left at whatever position the desktop trackball
camera's embedding transform put it, with no relationship to the headset's own tracking
origin, almost certainly outside the visible frustum. `ShapedGeometry` never had this problem
because it's JSX-nested directly inside `WebXRSupport`'s `<T.Group ref={originGroup}>`, so it
moves for free whenever that group's transform changes.

Confirmed `WebXRContext`/`setRootScene`/`useWebXRClient` (the mechanism that might have
already solved this) has zero call sites anywhere in the codebase — dead code, comment says
"only used by the glTF and VRML viewers," which don't appear to exist in this tree currently.

**Fix**: parent `SymmetryGeometry`'s `originGroup` under `WebXRSupport`'s `originGroup`
instead of the raw scene, so the exact same repositioning/rescaling `ShapedGeometry` gets for
free now also applies here.
- `online/src/viewer/context/symmetry-renderer.js`: `createSymmetryRenderer`'s parameter
  renamed `scene` → `parent` (only ever used for one `.add()` call — any `Object3D` works,
  not specifically a `Scene`).
- `online/src/viewer/symmetry-geometry.jsx`: the matrix-building effect for
  `renderer.originGroup.matrix` **no longer premultiplies by `globalScale`** — the parent
  group (`WebXRSupport`'s) already supplies that via its own ordinary (auto-updating)
  `.matrix`, computed fresh each frame from its `.position`/`.quaternion`/`.scale`, which is
  exactly what `XRGripToMove`/`XRScaling` write to. `SymmetryGeometry`'s own `originGroup`
  now only needs to encode the embedding transform.

**First attempt at getting the parent group reference broke the page entirely** — worth
recording since it's a second instance of the same "solid-three ref timing" class of
mistake, distinct from bug #6's transform-composition mistake but easy to conflate with it.
First attempt: exposed `getOriginGroup: () => originGroup` from `WebXRContext` (a closure
over a plain `let originGroup` in `webxr.jsx`, assigned via `ref={originGroup}` on
`WebXRSupport`'s `<T.Group>`) and called it **eagerly, synchronously**, at
`SymmetryGeometry`'s own component-setup time: `createSymmetryRenderer(getOriginGroup())`.
This crashed on load: `TypeError: Cannot read properties of null (reading 'add')` — deep
inside `createSymmetryRenderer`'s `parent.add(originGroup)`. Root cause: solid-three's
`T.Group` instance is constructed lazily behind an internal `createMemo`
(`createEntity`/`useProps` in `solid-three`'s source), only forced once something reads it —
which happens inside a `createRenderEffect`, not synchronously during the parent component's
own body execution. So `ref={originGroup}` (a plain-variable ref) is **not** guaranteed
assigned by the time a descendant component's top-level setup code runs, even though that
descendant is JSX-nested inside the group and the group is its direct ancestor in the tree.
This is exactly why `getRootGroup={() => originGroup}` already worked correctly for
`StartXRButton` elsewhere in the same file — that closure is only ever *called* later, inside
`onViewerStart`/frame callbacks, well after mount, never at setup time.

**Fix for the fix**: `webxr.jsx` now also exposes a reactive `originGroupReady` **signal**
(`createSignal(null)`), set via a `captureOriginGroup(g)` callback-form ref (`ref=
{captureOriginGroup}`) that both assigns the existing plain `originGroup` variable (unchanged
uses: `setRootScene`, `getRootGroup` for `StartXRButton`) and calls `setOriginGroupReady(g)`.
`symmetry-geometry.jsx` was restructured into two components: the exported `SymmetryGeometry`
now does nothing but `<Show when={originGroupReady()}>{parent => <SymmetryGeometryImpl
{...props} parent={parent()} />}</Show>` — deferring everything else (creating the renderer,
registering all effects) until the group signal actually resolves non-null. All the actual
logic moved unchanged into a new internal `SymmetryGeometryImpl`, taking `props.parent`
instead of calling `getOriginGroup()` itself.

**Why this should be safe for the desktop (non-XR) path**: `WebXRSupport`'s `originGroup` has
`position`/`quaternion` at identity and `scale = globalScale` outside of an XR session
(`XRGripToMove`/`XRScaling`'s `onViewerEnd` handlers explicitly reset position and quaternion
back; scale during a session is relative to a `sessionStartScale` snapshot, also restored).
So the net transform `SymmetryGeometry`'s content sees outside XR should be identical to
before this fix — `globalScale` still gets applied, just one level up in the hierarchy
instead of baked into the leaf's own matrix. This has not yet been re-verified on the desktop
canvases (trackball preview, classic editor, standalone viewer) after this change — do that
before assuming no regression, the same way Phase 3's bug #6 fix needed careful before/after
checking of exactly this kind of transform composition.

**Not yet done**: actual on-device re-test on the Quest 3 to confirm the model now appears
and is grip-movable/scalable. The user reported the original symptom but hasn't yet tried
this fix.

## Design decision: keep `solid-three`, but stay wary of its declarative/imperative seam

After the two ref-timing crashes above (the `null.add()` crash and its fix), the user asked
a good architectural question worth recording so it doesn't get re-litigated from scratch:
is `solid-three` — specifically its declarative JSX scene graph (`<T.Group>`/`<T.Mesh>`/etc.,
via `createT`) — actually worth its cost here, given how much trouble the imperative/
declarative boundary has caused (this session alone: bug #6's transform-composition mistake,
plus both `originGroup` ref-timing crashes)?

**Considered and rejected: move rendering into the worker with `OffscreenCanvas`.** Would not
actually remove the imperative/declarative tension (the imperative GPU-instancing core in
`symmetry-renderer.js` is load-bearing for performance, not accidental complexity — it would
still be imperative in a worker) and would forfeit XR entirely: `navigator.xr` / WebXR
sessions require a window/main-thread context, there is no API for driving an XR session
from inside a Worker. Since XR support is an active, tested requirement, this would mean
maintaining a second full renderer for non-XR views only — more total complexity, not less.
Not pursued.

**Audited what `solid-three` is actually earning across the codebase** (grepped every file
importing it, ~22 total): the framework genuinely earns its keep for `<Canvas>` itself
(WebGPU/WebGL context creation, resize, the render loop), `useThree()`/`useFrame()`, and
`xr.Provider`/`createXR()` (WebXR session lifecycle) — all expensive to hand-roll, all kept.
It also genuinely earns its keep in `geometry.jsx`'s `Instance` component (`ShapedGeometry`'s
per-mesh pointer event handlers — `onClick`/`onPointerEnter`/etc. — real raycasting +
event-dispatch, would need hand-rolling otherwise; notably this is exactly what Phase 5's
`pickAt`-based picking will hand-roll for `SymmetryGeometry`, since GPU-instanced meshes have
no per-instance object for solid-three to raycast against).

Everywhere else the declarative `<T.*>` layer is used (`webxr.jsx`'s `originGroup`,
`perspectivecamera.jsx`, `orthographiccamera.jsx`), the actual pattern is: one or two
reactive prop bindings (`scale=`, `position=`) on an object whose real behavior is driven
entirely by hand-written imperative `createEffect`s reaching into a `ref` — not composed
JSX children, not automatic resource lifecycle (every file that disposes anything, e.g.
`geometry.jsx`'s `onCleanup(() => geometry.dispose())`, does so manually, not via
solid-three). `webxr.jsx`'s `originGroup` is the sharpest example: the *only* thing `<T.Group
scale={globalScale}>` contributes there is one reactive scale binding — in exchange, its
lazy-construction-behind-a-memo timing model directly caused both crashes above, plus the
`WebXRContext.Provider` that exists purely to smuggle the imperatively-needed group reference
back out to code (`SymmetryGeometry`) that isn't part of that JSX tree.

**Conclusion, not yet acted on beyond the `originGroupReady` signal fix already in place**:
`solid-three`'s declarative scene graph is not, on the evidence in this codebase, earning
enough to justify fighting its construction-timing model at every seam where imperative code
needs to reach into a JSX-declared object. But `<T.Group originGroup>` specifically can't be
fully eliminated either — solid-three has no working "wrap an externally-constructed
instance and still let JSX children nest under it" escape hatch (`object` prop is declared
in the type definitions but not actually implemented/read in the installed version — checked
directly in `node_modules/solid-three/dist/index.js`), and `originGroup` genuinely needs
both: real JSX children (camera, `TrackballControls`, `Labels`, `ShapedGeometry` all nest
inside it) AND imperative reachability (`XRGripToMove`/`XRScaling`, `SymmetryGeometry`). So
the `originGroupReady` signal + `Show`-gated `SymmetryGeometryImpl` split (implemented above)
is the correct, minimal shape of the fix given that constraint — not a workaround to later
replace, the actual right answer once the constraint is understood. If a similar seam comes
up again (Phase 5's picking will add more imperative code reaching into solid-three-managed
objects — the picking scene/camera sync in `pickAt`, for instance), expect the same tension
and reach for the same pattern (a ready-signal gate) rather than assuming synchronous ref
availability.

## Shading fix — `MeshPhongNodeMaterial` → `MeshLambertNodeMaterial`

**Status: done, user-confirmed ("Looks great").** User reported the Phong specular highlight
(shininess 55, a fairly bright bluish specular color) was too strong — distracting up close,
overwhelming from a distance. Checked what `ShapedGeometry` actually uses
(`geometry.jsx`'s `InstancedShape`): plain `MeshLambertMaterial`, no specular term at all.
The Phong material was a carry-over from the `webxr-poc` port this file started from, never
actually meant to match the app's look. Fixed in `online/src/viewer/context/symmetry-renderer.js`'s
`createMaterialForGroup`: swapped to `MeshLambertNodeMaterial` (import changed too), dropped
`shininess`/`specular` entirely (Lambert has no specular term), kept `flatShading: true` and
the same `positionNode`/`colorNode`/`emissiveNode` TSL hooks — all still supported. Simple,
low-risk, exact-parity fix.

## Phase 6 — selection highlight — COMPLETE

**Status: implemented, not yet manually verified on-screen** (syntax-checked only as of this
writing). Done ahead of Phase 5 at the user's explicit direction: Phase 5 (GPU picking) is
only needed for *editing* use cases (the classic editor, `59icosahedra`), which are staying
on `ShapedGeometry` for now; Phase 6 (highlight) and would-be Phase 7 (labels, **not** done —
see below) are needed to release `SymmetryGeometry` for *viewer-only* (non-editing) use
cases, which don't need picking at all. No architectural reason 6 had to follow 5 — checked
`setInstanceHighlight(shapeId, instanceId, intensity)` in `symmetry-renderer.js` and
confirmed it takes the renderer's own ids directly, nothing picking-specific.

**What changed** (`online/src/viewer/symmetry-geometry.jsx`):
- New `SELECTED_HIGHLIGHT = 0xc8/0xff` constant (≈0.784) — matches `ShapedGeometry`'s
  `InstancedShape` exactly, which shows selection as a binary emissive swap between
  `"#c8c8c8"` and `"black"` (not the plan doc's placeholder suggestion of `0.5` — that was
  never based on the actual color, just a rough guess). `symmetry-renderer.js`'s
  `highlightIntensity` attribute feeds `emissiveNode = white * highlightIntensity`, so this
  value reproduces the same visual result.
- New `instanceRefById` (vZome instance `id` → `{shapeId, instanceId}`) and
  `lastSelectedById` (vZome instance `id` → last-seen `boolean`) maps, both rebuilt from
  scratch every time the existing instance-registration effect runs (not updated
  incrementally — see the "known limitation" below for why).
- New dedicated `createEffect` that diffs `instance.selected` against `lastSelectedById` and
  calls `renderer.setInstanceHighlight(shapeId, instanceId, ...)` **only** for instances
  whose selection actually changed — this is the O(changed instances) behavior discussed and
  chosen over just folding `highlight: instance.selected ? ... : 0` into the existing
  `addInstance` call (which would be correct but silently ride along on that effect's
  existing full-rebuild-every-time behavior, discussed next).
- The instance-registration effect's `addInstance` call also now sets the *initial*
  `highlight` value directly, so a freshly-registered selected instance never flashes
  unhighlighted for a frame before the diff effect catches up.

**Known limitation, deliberately not fixed in this pass**: `online/src/viewer/context/scene.jsx`'s
`SELECTION_TOGGLED` handler replaces `scene.shapes` with an entirely new object/array tree on
every single toggle (`setScene({...scene, shapes})` with new identities all the way down —
not a fine-grained per-instance SolidJS store mutation). Since `symmetry-geometry.jsx`'s
instance-registration effect depends on `props.shapes` as a whole, **every selection click
still triggers that effect's full `removeAllInstances` + re-add loop across every shape** —
this was true before Phase 6 and is not something Phase 6 introduced or attempts to fix. The
new targeted highlight effect avoids *adding* a second full rebuild on top of that, and is
correct/complete on its own terms, but doesn't make selection toggling cheap in the way
`ShapedGeometry` gets for free (its `<Instance>` components are individually fine-grained
SolidJS components, so only the one instance whose `selected` prop changed re-renders).
Fixing this properly would mean giving `scene.jsx`'s store finer granularity (e.g. a nested
per-instance signal instead of replacing the whole `shapes` tree) — flagged as a real
follow-up item, out of scope here since it touches shared worker-client state, not just
`SymmetryGeometry`. Worth measuring whether this is an actual problem on realistically large
models before spending time on it.

**Phase 7 (labels) was done in a follow-up pass — see its own section below.**

## Phase 7 — labels — COMPLETE AND VERIFIED

**Status: user-confirmed working on-screen, appear and disappear both verified correct.**
Done directly after Phase 6, completing the feature set needed to release `SymmetryGeometry`
for viewer-only (non-editing) use cases — Phase 5 (picking) remains the only
deliberately-deferred phase, needed only for editing use cases. Verification took much
longer than expected because it uncovered two independent, real, pre-existing bugs in
shared label infrastructure (`labels.jsx`) that had nothing to do with `SymmetryGeometry`
itself — see the two "shared bug" subsections below. Both are fixed and confirmed: a label
now appears correctly when its scene is shown, and disappears correctly (rather than
freezing in place) when switching away from that scene.

**Key finding while scoping this**: `online/src/viewer/labels.jsx`'s `Label` component is
*not* a `<T.*>` solid-three element — no JSX-child-nesting requirement, unlike `webxr.jsx`'s
`originGroup` (see the "Design decision" section above). It's a plain component that
imperatively does `props.parent.add(label)` inside `onMount`, so it can be used directly by
`SymmetryGeometry`'s already-imperative code without fighting the declarative/imperative
seam that caused trouble elsewhere in this migration. In the end, `symmetry-geometry.jsx`
doesn't actually render `<Label>` (the JSX component) at all — it builds `CSS2DObject`s
directly, for the same reason the instance-registration effect already does everything else
imperatively: labels need to be rebuilt in the same full-teardown-and-rebuild cycle as
instances (see Phase 6's `instanceRefById` comment for why), and there's no meaningful
benefit to wrapping that in a `<For>`/JSX layer just to immediately imperatively mutate the
result.

**The actual problem to solve**: `ShapedGeometry` gets a label's position for free via real
Three.js parenting — `<Label parent={meshRef} position={shapeCentroid}>` in `geometry.jsx`,
where `shapeCentroid` is a *local*-space offset within that one instance's own mesh, and the
label inherits the mesh's rotation/translation automatically by being its scene-graph child.
`SymmetryGeometry` has no per-instance mesh (everything is GPU-instanced into a handful of
shared `InstancedMesh`es), so there's no scene-graph node to hang a label off and get its
position "for free." Fixed by computing each label's position explicitly in JS, replicating
what the GPU vertex shader already does per-vertex (`rotatedPositionNode` in
`symmetry-renderer.js`'s `createMaterialForGroup`: `orientation * localVertex +
instanceTranslation`) — just once per label, using the shape's centroid as `localVertex`,
on the CPU instead of per-vertex on the GPU. All labels are parented directly to
`renderer.originGroup`, which is exactly the space instance positions are already expressed
in, so no further transform juggling is needed.

**What changed** (`online/src/viewer/symmetry-geometry.jsx`):
- New import: `CSS2DObject` from `three-stdlib` (same class `labels.jsx`'s `Label` uses).
- `orientationMatrices` (the `Matrix4[]` built from `props.orientations`) is now retained
  across the group-registration effect instead of being a throwaway local — needed for the
  CPU-side position math above. Minor simplification as a side effect: it's computed once
  and reused for `registerSymmetryGroup`, rather than being recomputed a second time.
- New `shapeCentroidById` map (`shapeId` → `{x,y,z}`), populated at shape-registration time
  from `geometry.shapeCentroid` (set by `buildShapeGeometry` in `geometry.jsx`, already
  existed, just never previously retained by `symmetry-geometry.jsx`).
- New `labelById` map (vZome instance `id` → `CSS2DObject`), rebuilt in the same pass as
  `instanceRefById`/`lastSelectedById` inside the instance-registration effect: for each
  instance with a truthy `label` field, reuse the existing `CSS2DObject` if one already
  exists for that instance id (avoids DOM element churn on every rebuild), otherwise create
  one (`className='vzome-label'`, `id='vzome-label-${label}'`, matching `Label`'s own DOM
  conventions exactly for CSS/testing compatibility), update its `.position`, and add it to
  `renderer.originGroup` if new. Whatever's left in the *previous* `labelById` after the pass
  (instances that were removed, or lost their label) gets `renderer.originGroup.remove(...)`
  — `CSS2DObject`'s own `"removed"` event listener (see `three-stdlib`'s
  `CSS2DRenderer.js`) handles detaching the DOM element, no manual DOM cleanup needed.

**Update: the "`Label` has no `onCleanup`" gap noted above turned out to be a real,
user-visible bug, not just a theoretical leak — see "Second shared bug" below.** It's now
fixed. The remaining item — `SymmetryGeometryImpl` overall has no `onCleanup` disposing
`renderer`'s GPU resources or `originGroup`'s children on unmount (e.g. switching
`symmetryRenderer` off, navigating away) — is still unfixed, out of scope, pre-existing from
before Phase 6/7. Worth fixing eventually if `SymmetryGeometry` instances are ever
mounted/unmounted repeatedly in the same session (e.g. switching between scenes with
different `<SceneCanvas>` instances) rather than once per page load.

### Second shared bug found during verification — `Label` never removed its `CSS2DObject`

**Status: fixed, user-confirmed reproduced then fixed.** Found immediately after fixing the
stale-camera bug above, while re-verifying: switching from the default scene (which has the
one labeled instance) to a different named scene made the label **freeze in place** instead
of disappearing — still visible, just no longer tracking the camera. This is a second,
independent bug in the same shared `labels.jsx` file, affecting `ShapedGeometry` (this is
its own component, `Label`, used directly) — and would have affected `SymmetryGeometry`'s
own scene-switch behavior too, had its Phase 7 code not already handled removal explicitly
(see `labelById`'s stale-entry cleanup above, added independently, before this bug was known).

**Root cause**: `Label`'s `CSS2DObject` is added to its parent mesh imperatively
(`props.parent.add(label)` inside `onMount`) — this happens entirely outside solid-three's
own scene-graph reconciliation (`useSceneGraph` in `solid-three`'s source), which only
tracks and auto-removes objects that appear as *declared JSX children* of a `<T.*>`
element. Confirmed by tracing `<Instance>`'s unmount sequence directly: `<Instance>` (and
its `<T.Mesh>`) *does* correctly unmount and get detached from its own parent group when a
scene switch removes that instance — but the `CSS2DObject` label, still a child of that now
newly-orphaned mesh, was never told to leave. It keeps existing, with a `matrixWorld` that's
simply never recomputed or traversed again, because `CSS2DRenderer.render()`'s traversal
starts from the *live* scene and can no longer reach an orphaned subtree — so it never
touches (or hides) that DOM element's `transform`/`display` again. Net visible effect: the
label freezes at its last on-screen position instead of disappearing.

**Fix**: `Label` now has `onCleanup(() => label?.parent?.remove(label))`. Removing the
`CSS2DObject` from its parent also fires its own `"removed"` event listener (see
`three-stdlib`'s `CSS2DRenderer.js`, read directly during the first bug's investigation),
which automatically detaches the DOM element too — no manual DOM cleanup needed, matching
the pattern `SymmetryGeometry`'s own Phase 7 label code already used for exactly this reason.

**Duplicate-label-text caveat, matched intentionally rather than fixed**: both `Label` and
this new code use `label` text as part of the DOM element's `id` attribute
(`vzome-label-${label}`) — if two instances share the same label text, they'd collide on DOM
id. This is pre-existing behavior in `ShapedGeometry`'s `Label` usage, not something Phase 7
introduces; matched for consistency rather than silently diverging, but worth knowing if it
ever surfaces as a real bug report.

### Shared bug found during verification — `Labels` used a stale, one-time-destructured camera

**Status: fixed, user-confirmed working.** Fixed in `online/src/viewer/labels.jsx`, affects
**both** `ShapedGeometry` and `SymmetryGeometry` equally — this was in shared rendering
infrastructure, not either renderer's own code, so it's recorded here in detail since it cost
by far the most time in this pass and the failure mode is worth recognizing quickly if it
(or something shaped like it) recurs.

**Symptom, exactly as first reported**: a labeled instance's DOM element (`.vzome-label`)
existed in the `.labels` container div, with a plausible-looking `transform` positioning it,
but nothing was visible on screen. No console error anywhere. This is what made it hard to
find — every individual piece looked correct in isolation:
- `Label`'s `onMount` fired, `props.parent` (`meshRef`) was already a real, valid `Mesh`
  (not the ref-timing-null bug from `webxr.jsx`'s history — that specific failure mode was
  the leading hypothesis at first, and was directly ruled out by observing no thrown error).
- The `CSS2DObject` was correctly `.add()`'d to the mesh, and the mesh's own ancestor chain,
  walked by hand, terminated at a real `Scene` with a matching `uuid` to the one `Labels`
  itself held.
- `scene.traverse()` from `Labels`' own `useFrame` found the `CSS2DObject` — proving it was
  a genuine, connected descendant of the live scene graph, not orphaned.
- `CSS2DRenderer`'s own per-object visibility check (`object.visible`, `layers.test`) passed.

**The actual, easy-to-miss cause**: `CSS2DRenderer.render()`'s remaining check —
`_vector.z >= -1 && _vector.z <= 1` after projecting the object's world position through
`camera.projectionMatrix * camera.matrixWorldInverse` — was failing. Projecting the label's
*parent mesh's* world position through the same matrices produced `NaN`/`-Infinity`, and
`camera.position` read out as `[0, 0, 0]` — which looked like "the camera is at the origin,
coincident with the geometry," a real and plausible-sounding bug on its own. **This was a
red herring**, caught only because the 3D scene visibly rendered correctly (a camera
genuinely at the origin, inside/on top of the geometry, would look obviously wrong — extreme
distortion or clipping — and it didn't). The user caught this distinction and pushed back
correctly rather than accepting the origin-camera explanation.

**Root cause**: `online/src/viewer/context/camera.jsx`'s `ControlledPerspectiveCamera`
builds its own `PerspectiveCamera` instance and registers it as the active render camera via
solid-three's `setCamera(cam)` (which pushes onto an internal `cameraStack`) — but only
*after* that component itself mounts. `Labels` (`labels.jsx`) did `const { scene, camera,
canvas } = useThree();` at its own setup time — a **destructure**, which evaluates
`useThree().camera` exactly once, capturing whatever it returns *at that instant*. But
`.camera` is a **live getter** on the object `useThree()` returns
(`get camera() { return cameraStack.peek() ?? camera(); }` — confirmed by reading
`node_modules/solid-three/dist/index.js` directly). If `Labels` mounts and destructures
before `ControlledPerspectiveCamera`'s `setCamera(cam)` call has run, the destructured
`camera` binding is permanently pinned to solid-three's internal fallback default camera
(sitting at the origin) for the rest of `Labels`' lifetime — while the actual WebGL render
path, which re-reads the same getter fresh via solid-three's own internals every frame, sees
the correctly-registered, correctly-positioned camera. Net effect: the 3D scene renders
perfectly (real camera), but `CSS2DRenderer.render(scene, camera)` inside `Labels`' own
`useFrame` used the stale one — degenerate projection math against a camera at/near the
scene's own geometry, every label failing the clip-space z-range check, `display: none`,
silently, forever, no error.

This is precisely the same *class* of mistake already called out in this document's
"Debugging technique notes" section below (`useThree().camera` being a getter, destructuring
== staleness) — but that note was about reading a stale camera *within a single frame's
logic*; this instance is subtler, because the staleness is locked in permanently at
component-setup time, not just momentarily within one callback.

**Fix**: keep the full `useThree()` result (`const three = useThree()`) instead of
destructuring `camera` out of it, and read `three.camera` fresh inside the `useFrame`
callback each frame (`labelRenderer.render(scene, three.camera)`). `scene`/`canvas` were left
destructured — `canvas` is a plain property (not a getter, confirmed in source, genuinely
constant for the component's lifetime), and `scene` was never observed to actually go stale
in practice here, though the same risk technically exists for it if it's ever swapped later
in some other code path.

**How this was actually found**: not by more static code reading, but by adding temporary,
targeted `console.log`s at each hypothesis in sequence (parent validity → scene-graph
connectivity → traversal reachability → per-object visibility flags → the projected z/x/y
values and camera position themselves) and letting the *data* rule hypotheses out one at a
time, rather than continuing to reason abstractly about solid-three's internals. The
projected-position numbers were what finally made the real cause legible — everything before
that was consistent with several different explanations.

## Debugging technique notes for whoever continues this

The Phase 3 debugging session (item 6 above especially) went through a long sequence of
GPU-level diagnostics — shader source dumps via a patched `gl.linkProgram`, draw-call
interception, frustum-containment tests, `useFrame`-based live camera sampling — before
finding that the actual defect was a missing scale factor, not anything GPU/shader-related.
In hindsight, the fastest diagnostic that actually worked was: temporarily strip the
material down to something with zero dependencies (no `positionNode`, or a `MeshBasicMaterial`
with no custom nodes at all) and confirm *something* renders, establishing that the camera/
geometry/draw-call pipeline is sound — then reintroduce one piece of the custom shader logic
at a time. Camera-position/orientation diagnostics captured via `useThree()` destructured
once at component setup are unreliable — `useThree().camera` is a getter, and code that reads
it *after* destructuring gets a stale/wrong value if the underlying camera object is replaced
later (as happens during normal camera-provider setup). Re-fetch it fresh (`three.camera`,
not a destructured `camera`) or sample from inside `useFrame`, which always sees the live
value.
