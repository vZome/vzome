# Worker ↔ Client Scene Rendering Protocol

This document describes how scene data (shapes, instances, orientations, embedding) flows
from the web worker (which owns the loaded design) to the client (which renders it via
Three.js), as of the `webgpu-try` branch's work adopting `SymmetryGeometry` alongside the
existing `ShapedGeometry`. It also records known redundancies and flaws surfaced while
debugging that renderer's rollout — several of which cost significant time to track down
because they fail *silently* (no console error, no thrown exception, just wrong or missing
rendering).

## Two transport mechanisms

`online/src/viewer/context/worker.jsx` exposes two ways for the client to receive data from
the worker:

- **Broadcast** (`subscribeFor(type, callback)`): the worker calls `report({ type, payload })`
  (via `postMessage`) whenever it wants; every subscribed client callback for that `type`
  fires. No correlation to a specific client action — purely event-driven.
- **Request/response** (`postRequest(action)` / `workerAction(...)`): the client sends a
  message tagged with a `requestId`; `onWorkerMessage` special-cases messages carrying a
  `requestId` and resolves a pending Promise instead of broadcasting to subscribers
  (`worker.jsx:77-85`). `SNAPSHOT_SELECTED` (used for scene switching) goes through this path.

These two mechanisms carry structurally identical payloads (`{ scene: {...} }` in most
cases) but are consumed by *completely different client-side code paths* — this is the root
of most of the redundancy described below.

## The core payload-building functions

Both live in `online/src/worker/vzome-worker-static.js` and are the single source of truth
for what a "scene response" contains:

```js
const prepareSceneResponse = ( design, snapshot ) =>
{
  const { embedding, polygons, snapshots, orientations, instances } = design.rendered;
  const snapshotInstances = ( snapshot === DEFAULT_SNAPSHOT )? instances : snapshots[ snapshot ];
  const shapes = {};
  for ( const instance of snapshotInstances ) {
    const rotation = [ ...( orientations[ instance.orientation ] || IDENTITY_MATRIX ) ];
    if ( ! shapes[ instance.shapeId ] ) {
      shapes[ instance.shapeId ] = { ...design.rendered.shapes[ instance.shapeId ], instances: [] };
    }
    shapes[ instance.shapeId ].instances.push( { ...instance, rotation } );
  }
  const parts = createPartsList( shapes, design.wrapper?.controller?.symmController );
  return { shapes, embedding, polygons, parts, orientations };
}
```

```js
const prepareEditSceneResponse = ( design, edit, before ) =>
{
  // ... resolves sceneInstances from design.wrapper.getScene(edit, before) ...
  const { polygons, orientations } = design.rendered;
  const shapes = {};
  for ( const instance of Object.values( sceneInstances ) ) {
    const rotation = [ ...( orientations[ instance.orientation ] || IDENTITY_MATRIX ) ];
    // ... builds shapes[shapeId].instances with { ...instance, rotation } ...
  }
  return { scene: { shapes, polygons, orientations }, edit };
}
```

Note both functions do the **same redundant computation**: for every instance, they look up
`orientations[instance.orientation]` and bake the result into a per-instance `rotation`
matrix — *and* they also return the raw `orientations` array itself, unmodified, alongside
the shapes. This is intentional, transitional redundancy (see "Redundancy" below), not an
oversight — `ShapedGeometry` needs `rotation`; `SymmetryGeometry` needs `orientations` +
`instance.orientation`. Both are sent on every response so either renderer can consume the
same wire payload.

## Four independent scene-loading paths, worker side

There is no single "load a scene" function. Four separate code paths in the worker build and
send scene data, and they don't share client-side handling:

1. **`openDesign` → legacy Java `loadDesign`/`newDesign`** — used by the classic editor when
   opening a design from XML. The legacy Java bridge (via the `clientEvents(report)` object
   passed into it) is the *only* thing that calls `symmetryChanged()`
   (`vzome-worker-static.js:114`), which fires `SYMMETRY_CHANGED`. This happens once, at
   design-load time, from Java code not visible in the JS source — the call site is opaque
   to static analysis of the JS files.

2. **`urlLoader`'s `preview: true` branch** — used by the standalone viewer / web-component
   embed (`UrlViewer`, since `index.jsx:184` passes `preview: true` by default). This fetches
   a pre-baked `.shapes.json` file and calls `events.sceneChanged(prepareSceneResponse(...))`
   directly — it **never calls `symmetryChanged()`**, so `SYMMETRY_CHANGED` never fires on
   this path, even though `prepareSceneResponse`'s returned `orientations` field is fully
   populated. This asymmetry — one loading path fires `SYMMETRY_CHANGED`, the other doesn't,
   while both send `orientations` in-band on `SCENE_RENDERED` regardless — was the single
   biggest source of "why is this blank" bugs while integrating `SymmetryGeometry` (see
   Debugging Pain Points below).

3. **`SNAPSHOT_SELECTED`** (request/response) — used by both apps for scene switching
   (previous/next scene, or programmatic scene selection):
   ```js
   case 'SNAPSHOT_SELECTED': {
     if ( !design?.rendered?.snapshots ) break;
     const { snapshot } = payload;
     const scene = prepareSceneResponse( design, snapshot );
     sendToClient( { type: 'SCENE_RENDERED', payload: { scene } } );
     break;
   }
   ```
   This reuses `prepareSceneResponse`, so `orientations` is present — but this response is
   consumed via the request/response mechanism (resolving a `postRequest` Promise), not via
   `subscribeFor('SCENE_RENDERED', ...)`. A client component that only subscribes to the
   broadcast `SCENE_RENDERED` event will never see this data; it has to be the caller of
   `postRequest` itself.

4. **`TRACKBALL_SCENE_LOADED`** — a *fully separate* mechanism used only by the classic
   editor's small trackball orientation preview widget (`camera.jsx`'s `CameraControlsUI`).
   `connectTrackballScene`/`fetchTrackballScene` (`vzome-worker-static.js:134-162`) load an
   **independent design object** (`trackballDesign`, distinct from the main `design`) via a
   fresh `legacy.loadDesign(xml, false, polygons, clientEvents(()=>{}))` call whose `report`
   callback is a no-op — so any `SYMMETRY_CHANGED`/`SHAPE_DEFINED`/etc. events the Java loader
   fires internally for *this* design are silently swallowed. The trackball design's
   `prepareSceneResponse(...)` result is sent as one single `TRACKBALL_SCENE_LOADED` message,
   picked up by `subscribeFor('TRACKBALL_SCENE_LOADED', ...)` in `camera.jsx`, into its own
   `passive={true}` `SceneProvider` (an isolated scene store, not the app's main one).

## Client-side consumers — three independent places set `scene.orientations`

Because of the four worker-side paths above, and because two different apps use different
subsets of them, there end up being **three separate client-side call sites** that
independently read `orientations` off a response and call `setScene('orientations', ...)`.
All three were found and fixed over the course of adding `SymmetryGeometry` — each was an
instance of the same class of bug (a call site added before `SymmetryGeometry` existed, when
nothing needed `orientations` client-side, so nobody had reason to plumb it through):

| # | File:line | Component | Fires from | Notes |
|---|-----------|-----------|-------------|-------|
| 1 | `scene.jsx:182-184` | `SceneChangeListener` | `SYMMETRY_CHANGED` broadcast | The "intended" source — fires once at design load, only on the legacy Java path |
| 2 | `scene.jsx:198-199` | `SceneChangeListener` | `SCENE_RENDERED` broadcast | Guarded (`if (scene.orientations)`) so it doesn't clobber #1 with `undefined` on paths where `SYMMETRY_CHANGED` already set it correctly |
| 3 | `scene.jsx:83-84` | `SceneIndexingProvider.showIndexedScene` | `SNAPSHOT_SELECTED` response | Needed because this whole component is never reached by `SceneChangeListener` at all (see below) |
| 4 | `camera.jsx:46` | `CameraControlsUI`'s `TRACKBALL_SCENE_LOADED` handler | `TRACKBALL_SCENE_LOADED` broadcast, isolated `passive` scene store | A fourth, fully separate scene store from the other three |

(`camera.jsx`'s trackball handler and `SceneIndexingProvider`'s handler are *not* mutually
redundant with each other or with `SceneChangeListener` — they operate on different `scene`
stores entirely, per the app-mounting differences below. They're each other's closest analog,
not duplicates of the same store.)

**This is a real structural flaw, not just incidental duplication**: there is no single
function like `applySceneResponse(setScene, updateShapes, sceneResponse)` that all four
call sites funnel through. Each was written by hand, slightly differently, at a different
time, for a different app. Adding a new field to the wire protocol (as `orientations`
effectively was, retroactively, for `SymmetryGeometry`) means finding and fixing every one
of these by hand, with no compiler or lint rule to catch a missed site — the failure mode is
a silently-blank canvas, not an error.

## Classic editor vs. viewer/preview: which listener is mounted where

`SceneChangeListener` — the component with the richest event handling (incremental
`SHAPE_DEFINED`/`INSTANCE_ADDED`/`INSTANCE_REMOVED`/`SELECTION_TOGGLED` subscriptions, in
addition to `SYMMETRY_CHANGED`/`SCENE_RENDERED`) — is mounted in exactly two places:

- `online/src/app/classic/index.jsx:81` — the classic editor
- `online/src/app/buildplane/index.jsx:56` — the buildplane app

It is **never mounted** by `online/src/viewer/index.jsx`'s `SceneViewer` / `DesignViewer` /
`UrlViewer` tree — the standalone viewer and web-component embed path. That tree instead
relies entirely on `SceneIndexingProvider`'s `showIndexedScene` (itself driven by
`InitializeScene` calling `showTitledScene`/`showIndexedScene` once at mount, and again on
any subsequent scene navigation).

Practical consequence: the incremental edit events
(`SHAPE_DEFINED`/`INSTANCE_ADDED`/`INSTANCE_REMOVED`/`SELECTION_TOGGLED`) are **editor-only
concepts** at the client level — the viewer path has no equivalent and doesn't need one,
since it never mutates a design, only ever swaps between whole, pre-rendered snapshots via
`SNAPSHOT_SELECTED`. The plan doc for `SymmetryGeometry`
(`online/developer-docs/symmetry-renderer-plan.md`) frames this as "viewer is static, editor
is dynamic" — that's accurate at the level of *what the renderer needs to support*, but the
plumbing difference actually goes one level deeper: the viewer and editor don't share a
scene-loading code path at all, worker or client side, except for the pure data-shaping
functions (`prepareSceneResponse`/`prepareEditSceneResponse`).

## The `rotation` vs `orientations`+index redundancy

`ShapedGeometry` (`online/src/viewer/geometry.jsx`) renders one `THREE.Mesh` per instance and
reads `instance.rotation` directly — a full, pre-resolved 16-element matrix, ready to feed
into `Matrix4.set(...)`. It never looks at `orientations` or `instance.orientation` at all.

`SymmetryGeometry` (`online/src/viewer/symmetry-geometry.jsx`) renders via GPU instancing
(`createSymmetryRenderer`) and does the opposite: it registers the *shared* `orientations`
array once, as a symmetry group (`registerSymmetryGroup`), then references each instance by
its `orientation` **index** into that shared array (`addInstance({ orientationIndex: ... })`).
It never reads `instance.rotation`.

Both fields are present on every instance in every response, all the time, regardless of
which renderer (if either) is actually mounted for a given canvas — there's no negotiation of
"which shape does this client want." A `TODO` comment already in the worker code
(`vzome-worker-static.js:22-23`) flags this as known, deliberate, and temporary:

```js
// TODO: once SymmetryGeometry replaces ShapedGeometry, stop sending rotation
//   matrices per-instance and have the client resolve them from orientations.
```

This is the right call for now — it kept `ShapedGeometry` completely unmodified and working
throughout `SymmetryGeometry`'s development — but it's worth being explicit that it is a
**real, measurable bandwidth cost**: every instance carries a redundant 16-float matrix that
is fully derivable from its own `orientation` index plus the shared table already present in
the same payload. For designs with many instances this roughly doubles the per-instance
transform payload size for no reason once `SymmetryGeometry` is the only consumer.

## Async-handling issues found while debugging (from this session)

These aren't purely protocol-shape issues, but they're squarely in "client-side async
handling of scene data" and cost real debugging time, so they belong in this document:

1. **`showIndexedScene`'s two-phase update is not atomic.** `scene.jsx:68-91` sets
   `embedding`/`polygons`/`orientations` synchronously in the `postRequest(...).then(...)`
   callback, but defers `updateShapes(scene.shapes)` — the call that actually populates
   `scene.shapes` and makes `SymmetryGeometry`'s `hasInstances` check pass — behind an
   additional `setTimeout` *and* a `tweenCamera(...).then(...)` chain. This ordering is
   intentional (so the camera can finish animating before geometry pops in), but it means
   `orientations` and `shapes` can be visibly out of sync for a nontrivial span of wall-clock
   time. `SymmetryGeometry` is written defensively against this (it gates registration on
   *both* being present before doing anything), but a naive consumer reading the store
   mid-transition would see a `shapes` store from the *previous* scene alongside the *new*
   scene's `orientations` (or vice versa) for one or more frames.

2. **`sceneTitle` is a signal frozen at its own initializer, permanently, on the
   viewer/web-component path.** `SceneTitlesProvider` (`scene.jsx:137-146`) computes its
   initial `sceneTitle` value from `sceneTitles()[0]`, which itself depends on `scenes` (from
   `useViewer()`) — a list that is populated asynchronously by a `SCENES_DISCOVERED` message
   from the worker. SolidJS's `createSignal(initialValue)` evaluates `initialValue` exactly
   once, at signal-creation time; it does **not** re-run if whatever the initializer read
   later changes. Since `SceneTitlesProvider` mounts well before `scenes` has any data
   (that's an inherent race between component mount and worker roundtrip, not something
   fixable by reordering local code), `sceneTitle()` was `undefined` forever, and
   `setSceneTitle` — the only way to correct it — was exported from the context but never
   called anywhere in the codebase. This meant `InitializeScene`'s
   `showTitledScene(sceneTitle())` call always resolved to `undefined`, which
   `getSceneTitleIndex` treats as "index 0" (`scene.jsx:116-117`: `if (!title) return 0`) —
   silently loading the *wrong* named scene on first paint, correcting itself only once the
   user navigated to a different scene and back (which calls `showTitledScene`/
   `showIndexedScene` with an explicit, non-stale argument, bypassing the frozen signal
   entirely). This is a general SolidJS foot-gun (a signal seeded from a value that depends
   on not-yet-arrived async data) with a straightforward fix — a `createEffect` that corrects
   `sceneTitle` once `scenes` actually has content, guarded so it doesn't clobber a
   deliberately-set later value — but the bug itself predates this session and affects
   `ShapedGeometry` identically; it was simply much harder to notice there, since one scene's
   geometry can look superficially plausible in place of another's.

3. **Symptom shape common to both bugs above: silent, not loud.** Neither issue produces a
   console error, a warning, or a rejected Promise anywhere in the chain — both manifest as
   "the wrong (or no) content is on screen," which is exactly the failure mode that's hardest
   to distinguish from a genuine rendering bug (shader, GPU state, camera math, scale) without
   deliberately checking the data layer first. Concretely, this repo's debugging session for
   `SymmetryGeometry` spent a long time inspecting shader compilation, draw calls, GL state,
   frustum culling, and camera transforms on `webgpu-try` *before* discovering that the actual
   defect was a request racing past a guard clause and an `undefined` scene title — because
   every one of those lower-level checks passed individually, and the render pipeline really
   was working correctly on the data it was given; the data itself was simply wrong or absent.
   There is no structural way, currently, to distinguish "the renderer is broken" from "the
   data feeding the renderer is stale/missing/wrong" without manual, per-layer instrumentation
   — no assertion or dev-mode warning fires when `scene.orientations` is `undefined` while a
   consumer that needs it is mounted, for instance.

## Summary of concrete recommendations

- Consolidate the four "apply a scene response to the store" call sites (`scene.jsx` ×3,
  `camera.jsx` ×1) into one shared function once the third one (`SceneIndexingProvider`) is
  confirmed stable, so future wire-protocol fields only need to be added in one place.
- Consider a dev-mode assertion/warning when a mounted `SymmetryGeometry` observes
  `props.shapes` with instances but `props.orientations` still empty/undefined after some
  reasonable delay — surfacing exactly the failure mode that took the longest to diagnose.
- When `ShapedGeometry` is fully retired, drop `rotation` from `prepareSceneResponse` /
  `prepareEditSceneResponse` per the existing `TODO`, and have any remaining consumer resolve
  `rotation` from `orientations[instance.orientation]` client-side if still needed anywhere
  (e.g. for BOM/export tooling, if any exists outside the renderers).
- Fix (or at least flag) the `SceneTitlesProvider` frozen-signal pattern generally — any other
  signal in this codebase seeded from `useViewer().scenes` before that data has arrived is a
  candidate for the same bug.
