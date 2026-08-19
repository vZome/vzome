
import { createEffect, createMemo } from 'solid-js';

import Stack from "@suid/material/Stack"
import Switch from "@suid/material/Switch";
import FormControlLabel from "@suid/material/FormControlLabel";

import { controllerProperty } from '../../framework/context/editor.jsx';
import { useSymmetry } from "../context/symmetry.jsx";
import { WorkerProvider } from '../../../viewer/context/worker.jsx';
import { ViewerProvider, useViewer } from '../../../viewer/context/viewer.jsx';
import { CameraProvider, useCamera } from '../../../viewer/context/camera.jsx';
import { InteractionToolProvider } from '../../../viewer/context/interaction.jsx';
import { SceneCanvas } from '../../../viewer/scenecanvas.jsx';

import { SnapCameraTool } from '../tools/snapcamera.jsx';
import { GltfExportProvider, ImageCaptureProvider } from '../../../viewer/context/export.jsx';
import { ZoomSlider } from './zoomslider.jsx';
import { SceneProvider, SceneChangeListener, useScene } from '../../../viewer/context/scene.jsx';
import { resourceUrl } from './length.jsx';


// Loads the trackball model as an ordinary vZome design on its OWN worker and scene, fully
// isolated from the editor's worker/scene. This replaced the old bespoke trackball path (worker
// fetchTrackballScene/connectTrackballScene + a TRACKBALL_SCENE_LOADED message hand-fed into the
// editor's scene store): that shared the editor's SymmetryGeometry keying and crossed the editor's
// (e.g. rootTwo) field with the golden-field trackball template, producing wrong/colliding
// orientations. A trackball model is just a fixed .vZome design; loading it on its own worker
// makes it always self-consistent in its own field, with zero trackball-specific worker code.
//
// The `url` is the current symmetry's model resource path (was wrapper.getTrackballUrl on the
// worker; now read client-side from the symmetry controller's modelResourcePath property).
//
// WORKER REUSE (was: remount per url): the whole provider stack -- worker, viewer, scene, canvas --
// is mounted ONCE and kept alive for the life of the camera panel. A symmetry/field switch no
// longer remounts anything; instead TrackballLoader (below) reloads a new trackball design into the
// SAME worker via requestDesign. The worker's expensive one-time init (fetch+parse all symmetry/
// shape VEFs, build the FieldApplications) is memoized and reused across loads, so a switch is just
// a parse+render of the new fixed .vZome, not a fresh worker cold-start. Hoisting WorkerProvider
// above any url gating also lets the worker begin that init immediately at panel mount, overlapping
// with the symmetry controller becoming ready -- so the first trackball appears sooner.
//
// No <Show> gate: loading is guarded solely by TrackballLoader's `if (!url) return`, and SceneCanvas
// already renders an empty canvas until the first SCENE_RENDERED arrives (it gates geometry on
// scene.shapes), which is also the steady state between reloads. So an un-gated mount is safe.
//
// config.camera:false tells ViewerProvider NOT to drive the camera or background from the trackball
// design (loadDesign emits SCENES_DISCOVERED with the trackball .vZome's own camera + lighting,
// which would otherwise clobber the main-view mirror). Fixed `distance` + `context`-driven rotation/
// background sync (via the MAIN camera context) make the trackball mirror the main view.
//
// preview:false because the trackball .vZome files have no .shapes.json preview export, so the
// worker interprets the XML directly. SceneChangeListener consumes each SCENE_RENDERED to populate
// this isolated SceneProvider's shapes/orientations.
const TrackballViewer = () =>
{
  const context = useCamera(); // the MAIN camera context, to mirror its rotation
  const { symmetryController } = useSymmetry();

  // Compute the trackball url HERE, in TrackballViewer's body -- crucially OUTSIDE the trackball's
  // own <WorkerProvider> below. controllerProperty() reads useWorkerClient() to send its initial
  // property request, resolving to the NEAREST WorkerProvider ancestor. Here that is the MAIN editor
  // worker (which owns the symmetry controller and modelResourcePath); if this ran inside the
  // trackball WorkerProvider, controllerProperty would target the trackball worker instead, which
  // has no such controller, so modelResourcePath would never arrive and nothing would ever load.
  // (The old <Show when={trackballUrl()}> worked for the same reason: it evaluated above the
  // trackball WorkerProvider.) modelResourcePath is reactive -- re-requested on symmetry change --
  // so this memo tracks the current symmetry/field; undefined until the symmetry controller is ready.
  const trackballUrl = createMemo( () => {
    const symm = symmetryController();
    const path = symm && controllerProperty( symm, 'modelResourcePath' );
    // Must be a FULLY-QUALIFIED url: the worker runs from a blob: origin and fetches this string
    // directly (fetchUrlText), so a root-relative "/app/classic/resources/..." would resolve
    // against the blob origin and 403. Resolve against window.location here (the client has it;
    // the worker doesn't, which is why the old trackball path passed baseURL to `new URL`).
    // resourceUrl() gives the root-relative path; new URL(..., window.location) makes it absolute.
    const path2 = path ? new URL( resourceUrl( path ), window.location ) .toString() : undefined;
    return path2;
  } );

  return (
    <CameraProvider name='trackball' outlines={false} context={context}>
      <WorkerProvider>
        {/* No `url` in this config: ViewerProvider's on-create auto-load is guarded on url (see
            `url && postMessage(...)` there), so omitting it leaves the provider in its generic
            "no design loaded yet" state. TrackballLoader then owns every load, uniformly. The one
            config the trackball still needs -- camera:false -- is a pre-existing generic prop. */}
        <ViewerProvider config={ { preview: false, debug: false, camera: false } }>
          <SceneProvider>
            <SceneChangeListener />
            <TrackballLoader url={ trackballUrl } />
            <InteractionToolProvider>
              <SnapCameraTool />
              <TrackballCanvas />
            </InteractionToolProvider>
          </SceneProvider>
        </ViewerProvider>
      </WorkerProvider>
    </CameraProvider>
  );
};

// Owns ALL trackball design loads (first and subsequent), reactively, into the reused trackball
// worker/scene. Receives the url accessor from TrackballViewer (computed against the MAIN editor
// worker -- see the memo comment above); here we are inside the trackball WorkerProvider, so
// requestDesign/resetScene act on the trackball's own worker and scene, which is what we want.
const TrackballLoader = ( props ) =>
{
  const { requestDesign } = useViewer();
  const { resetScene } = useScene();

  // The url last actually loaded into the trackball worker. Opening/creating a document rebuilds
  // the editor's controller store, so symmetryController() yields a NEW controller object and
  // modelResourcePath momentarily reads undefined before being re-requested -- so props.url()
  // transitions url -> undefined -> url even when the new document uses the SAME symmetry/field,
  // and thus the same trackball. Without this guard that round-trip would trigger a needless
  // resetScene()+reload, blanking and re-fetching an identical trackball. By remembering what we
  // last loaded and skipping when the (defined) url is unchanged, we keep the existing trackball
  // rendered until we KNOW a different one is needed.
  let loadedUrl;

  createEffect( () => {
    const url = props.url();
    if ( ! url )
      return; // symmetry controller not ready yet (or transiently undefined during a doc rebuild)
    if ( url === loadedUrl )
      return; // same trackball as currently loaded -- leave it rendered, no reload
    loadedUrl = url;
    // Clear the previous design's shapes/orientations/symmetryId before loading the next, so a
    // field switch starts from a truly empty store (see SceneProvider.resetScene). requestDesign
    // posts URL_PROVIDED to the same worker, which reuses the memoized field/shape init. Only
    // preview/debug are read from this per-request config by the worker; the camera:false that
    // protects the main-view mirror is a ViewerProvider-level prop (driveViewFromDesign), set above.
    resetScene();
    requestDesign( url, { preview: false, debug: false } );
  } );

  return null;
};

// Renders the trackball scene, reading the isolated SceneProvider that SceneChangeListener above
// populates from the worker's SCENE_RENDERED. Split out so it runs INSIDE that SceneProvider.
const TrackballCanvas = () =>
{
  const { scene } = useScene();

  return (
    <SceneCanvas symmetryRenderer={true} scene={scene}
      height="200px" width="240px" rotationOnly={true} rotateSpeed={0.7} />
  );
};

export const CameraControlsUI = (props) =>
{
  const { state, togglePerspective, toggleOutlines } = useCamera();
  const { snapping, toggleSnapping } = useSymmetry();

  const isPerspective = () => state.camera.perspective;

  return (
    <div id='camera-controls'>
      <Stack spacing={1} direction="row" style={{ padding: '8px' }}>
        <FormControlLabel label="perspective" style={{ 'margin-right': '0' }}
          control={
            <Switch checked={isPerspective()} onChange={togglePerspective} size='small' inputProps={{ "aria-label": "controlled" }} />
        }/>
        <FormControlLabel label="snap"
          control={
            <Switch checked={snapping()} onChange={toggleSnapping} size='small' inputProps={{ "aria-label": "controlled" }} />
        }/>
        <FormControlLabel label="outlines"
          control={
            <Switch checked={state.outlines} onChange={toggleOutlines} size='small' inputProps={{ "aria-label": "controlled" }} />
        }/>
      </Stack>

      <div id="ball-and-slider">
        <div id="camera-trackball">
          <TrackballViewer />
        </div>
        <div id='zoom-slider' >
          <ZoomSlider/>
        </div>
      </div>
    </div>
  )
}

export const CameraControls = () =>
{
  return (
    // These two providers isolate this component's own GL context (the trackball canvas, mounted
    // deeper via CameraControlsUI -> TrackballViewer) so it doesn't overwrite the MAIN view's image
    // capturer / glTF exporter. The trackball now owns its worker/scene/interaction providers
    // itself (see TrackballViewer), so they are no longer wired up here.
    <ImageCaptureProvider>
    <GltfExportProvider>
      <CameraControlsUI />
    </GltfExportProvider>
    </ImageCaptureProvider>
  );
}