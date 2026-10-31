# Design Note: Scene Thumbnails in Online vZome

Status: Tier 1 (section 2) implemented, without eager generation (2026-10-31).  See
"Implementation" at the end.

Desktop vZome renders thumbnail images of each scene (lesson page) in the background.
Online vZome has no equivalent.  This note covers when thumbnails should be generated,
which existing mechanisms can do the work, and whether a dedicated rendering worker is
warranted.  **Recommendation: build it on the main thread with existing pieces; a worker
is not needed now.**

---

## How desktop does it

- `LessonModel.updateThumbnail()` (core) runs for any page whose thumbnail is marked
  stale.  Pages are marked stale after load (`firePropertyChanges`), and by `setView` /
  `setViewAll`.
- It resolves the page's snapshot and hands it, with the page camera, to
  `ThumbnailRendererImpl` (desktop), which builds a throwaway Java3D `Scene` and captures
  a 120px image.
- The only consumer is `PagelistPanel`.  Thumbnails are never persisted in the file.

Online has no consumer yet: `ScenesList` in `app/classic/dialogs/scenes.jsx` shows only
an index and a title.

**Decision: thumbnails display only in the Scenes dialog's scene list.**  They are not
part of GitHub share output, the `<vzome-viewer>` scene menus, or the saved file.  So
thumbnails are needed only while the Scenes dialog is open, and nothing has to be
generated when the dialog is closed.

---

## 1. Triggers

A thumbnail depends only on **(snapshot, camera, lighting)**.  Snapshots never change:
the worker's `captureSnapshot` action pushes a copy of `design.rendered.instances`.

| Event | Thumbnails affected |
|---|---|
| Capture Scene (main view), Add Scene (dialog) | the new scene |
| Save Camera (dialog) | that scene |
| Design load with existing scenes | all (none are stored) |
| Lighting / background change | all |
| Reorder, retitle, edit notes, delete | none |

### Key the cache by content, not index

Cache under a key such as `${snapshot}:${hash(camera)}:${lightingGeneration}`.  This
removes the need for any invalidation logic: Save Camera produces a new key, reordering
keeps existing keys, and unreferenced entries can be dropped.

Keep the cache **out of the viewer's `scenes` store**.  That store is passed to
`serializeVZomeXml` and `exportPreview`, and `copyScenes` recreates every scene object on
each mutation, so neither identity nor extra fields are safe there.

### Generate on demand

Unlike desktop, don't generate eagerly on load.  Generate only while the Scenes dialog
is open, visible rows first.  One exception: the main view's "Capture Scene" opens the
dialog right away (`doOpen()` in `doCapture`), so capturing from the main canvas at that
moment is still worthwhile.

---

## 2. Capturing at scene-capture time

Every *interactive* trigger already has a canvas showing exactly the right image:

- **Main "Capture Scene"** (`app/classic/components/scenecontrols.jsx`, `doCapture`):
  the main canvas shows the current model through the current camera.  When
  `captureSnapshot` resolves, call `captureImage()` and downscale.
- **Dialog "Add Scene" / "Save Camera"**: `ScenesDialog` already wraps its canvas in its
  own `ImageCaptureProvider`, so a capture there targets the dialog canvas showing that
  scene with that camera.
- **Dialog navigation**: whenever the dialog shows scene *i* and its key isn't cached,
  capture it.  Loaded designs fill in incrementally as the user browses (similar to
  desktop's "thumbnails don't fill in until you've clicked once").

Issues to handle:

- **Timing.**  `showIndexedScene` (`viewer/context/scene.jsx`) tweens the camera, then
  calls `updateShapes` inside a `setTimeout`, and returns nothing.  It would need to
  return a "scene settled" promise, and capture should wait one further frame.
- **Aspect ratio.**  The main canvas and the dialog canvas have different aspects; crop
  to a fixed thumbnail aspect.
- **Selection highlights.**  These appear in a main-view capture.  Not yet checked:
  whether snapshot playback also shows selection.
- **Background override.**  The main view may show an overridden background color rather
  than the design's lighting.  Decide which a thumbnail should reflect.

This tier is small: a capture call at three sites, a downscale, the keyed cache, and an
`<img>` in `DraggableScene`.  It leaves two gaps: filling every row at once for a freshly
loaded design, and regenerating after a lighting change.

---

## 3. Existing mechanisms

| Need | Existing piece |
|---|---|
| Scene data for any snapshot | `SNAPSHOT_SELECTED` via `postRequest( selectSnapshot(...) )` returns a fully prepared scene |
| Image capture | `CanvasExportBindings` / `ImageCaptureProvider` (`viewer/geometry.jsx`, `viewer/context/export.jsx`), plus the provider-isolation pattern so a second canvas doesn't take over File > Export |
| Second renderer drawing the same geometry off the page | The picking renderer in `viewer/context/symmetry-renderer.js` (`getPickingRenderer`): a separate `WebGPURenderer` on a never-attached canvas, sharing `BufferGeometry` with the main renderer |
| Isolated viewer stacks | `ScenesDialog` and `TrackballViewer` (`app/classic/components/camera.jsx`) mount full Camera/Scene/Viewer provider stacks; the trackball even has its own `WorkerProvider` |

For the two remaining gaps, two main-thread options:

### 2a. Hidden viewer stack (least code)

Mount `SceneProvider` + `SceneIndexingProvider` + `SceneCanvas` off-screen, step it
through the queued scenes, capture each, and **unmount when the queue is empty**.
Mounting it permanently would add another `frameloop="always"` canvas, the known
multi-viewer frame-loop cost.  It needs a non-zero layout size, so position it
off-screen rather than using `display:none`.

### 2b. Plain three.js thumbnail renderer (cleaner, moderate refactor)

Use `createSymmetryRenderer` directly with one small renderer and `canvas.toBlob`.  This
requires extracting from JSX into plain functions, which the components then call:

- camera math (`perspectivecamera.jsx`, `orthographiccamera.jsx`)
- lighting (`Lighting` in `ltcanvas.jsx`)
- the embedding and shape-registration effects in `symmetry-geometry.jsx`

Extracted that way, the components and thumbnails share the code rather than duplicating
it.

### Cost

Main-thread cost is modest either way: interpreting the design and building shapes
already happens in the design worker.  Per thumbnail, what remains is filling instance
buffers, one draw and an image encode.  Run jobs one at a time under
`requestIdleCallback`, with the newest request for a key replacing older ones.

---

## 4. A dedicated rendering worker

Not recommended for now:

- **It doesn't relieve the GPU.**  The multi-viewer scroll hangs were traced to a backlog
  in the browser's GPU process, which main-thread and worker contexts share.  Thumbnails
  need throttling regardless of where they run.
- **All rendering components are DOM-bound** (solid-three's `Canvas`).  A worker needs
  exactly the plain-function refactor from 2b.  Doing 2b on the main thread first makes a
  later move into a worker mostly packaging.
- **More fallback paths.**  WebGL on an `OffscreenCanvas` in a worker needs Safari 17+.
  The Intel-Mac `WebGPURenderer` init failure (see `viewer/renderer-support.js`) would
  need its own probe inside the worker.  Worker WebGL contexts also count against the
  per-page context limit.
- **Not inside the existing design worker.**  Strut-drag lag is already traced to the
  Java core running there; thumbnail rendering would compete with editing.

### If a worker is built later

- **The main thread decides what to render.**  It owns scenes, cameras and lighting, so
  it sends jobs `{ key, snapshot, camera, lighting, size }` and receives a transferable
  `ImageBitmap` or `Blob`.
- **Scene data flows worker to worker.**  Create a `MessageChannel` and transfer one port
  to each worker, so snapshot data goes design worker → thumbnail worker without the main
  thread copying it.
- **Cache shape geometry by `shapeId`.**  It is stable per design, so after the first job
  only instance lists cross.
- **Drop results from an old design.**  Stamp each load with a generation ID, as
  `symmetryId` is stamped in `prepareSceneResponse` today, and discard mismatched results.
- **Render to `new OffscreenCanvas( w, h )`** created in the worker;
  `transferControlToOffscreen` is unnecessary since nothing is displayed.
- **Lifetime follows the editor's `WorkerProvider`**, as with the trackball worker's
  `terminate()`.

---

## Suggested order

1. Opportunistic capture (section 2), the keyed cache, and an `<img>` in each
   `ScenesList` row.
2. If filling every row as soon as the dialog opens matters: 2a as a quick version, or 2b
   for the long term.  Run either only while the dialog is open.
3. Move to a worker only if profiling shows main-thread cost from 2b.

---

## Implementation (Tier 1)

- `app/classic/context/thumbnails.jsx`:
  - `ThumbnailsProvider` holds the cache (key -> object URL) and clears it on every
    `SCENES_DISCOVERED` (design load or new design).  It is mounted in `SceneControls`,
    around `ScenesDialog`, so it outlives the dialog.
  - `ThumbnailCapturer` is a behavioral component mounted inside the dialog's own
    `ImageCaptureProvider` / `CameraProvider` / `SceneIndexingProvider`.  When the dialog
    has finished showing a scene whose key isn't cached, it waits two frames, checks that
    the same scene is still showing, then captures, center-crops to 4:3 and scales down to
    200x150 (displayed at 100px wide).
- `SceneIndexingProvider` (`viewer/context/scene.jsx`) now exposes `settledSceneIndex`:
  null while a scene change is in progress, set once the camera tween and `updateShapes`
  have both finished.  Only the most recent `showIndexedScene` call can set it.
- `SceneThumbnail` in `dialogs/scenes.jsx` shows the cached image in each list row.
- The cache key includes the dialog's lighting.  Note that the dialog has its own
  `CameraProvider` with default lighting, so its canvas (and thus every thumbnail) shows
  the default background, not the design's.  That is existing dialog behavior.

**Change from the plan:** there is no capture from the main canvas on "Capture Scene".
That button opens the dialog on the new scene, which the dialog then captures.  Capturing
only from the dialog means every thumbnail matches what scene playback shows: no
selection highlights, no background override, one aspect ratio.

Not covered, by design: scenes the user never shows in the dialog have no thumbnail.
