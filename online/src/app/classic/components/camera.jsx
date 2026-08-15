
import { Show } from 'solid-js';

import Stack from "@suid/material/Stack"
import Switch from "@suid/material/Switch";
import FormControlLabel from "@suid/material/FormControlLabel";

import { controllerProperty } from '../../framework/context/editor.jsx';
import { useSymmetry } from "../context/symmetry.jsx";
import { WorkerProvider } from '../../../viewer/context/worker.jsx';
import { ViewerProvider } from '../../../viewer/context/viewer.jsx';
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
// worker; now read client-side from the symmetry controller's modelResourcePath property). The
// whole stack is remounted per url via <Show keyed> so a symmetry change loads a fresh worker with
// the new trackball design -- matching how 59icosahedra's ModelWorker isolates one worker per
// fixed model (WorkerProvider now terminates its Worker on unmount, so remounting doesn't leak).
// Fixed `distance` + `context`-driven rotation/background sync make the trackball mirror the main
// view. config.camera:false tells this ViewerProvider NOT to drive the camera or background from
// the trackball design (loadDesign emits SCENES_DISCOVERED with the trackball .vZome's own camera
// + lighting, which would otherwise clobber the main-view mirror); see ViewerProvider.
//
// preview:false because the trackball .vZome files have no .shapes.json preview export, so the
// worker interprets the XML directly. SceneChangeListener (mounted below) consumes the resulting
// SCENE_RENDERED to populate this isolated SceneProvider's shapes/orientations.
const TrackballViewer = () =>
{
  const context = useCamera(); // the MAIN camera context, to mirror its rotation
  const { symmetryController } = useSymmetry();
  // modelResourcePath is reactive: it re-requests on symmetry change (controllerProperty), so
  // trackballUrl() tracks the current symmetry. undefined until the symmetry controller is ready.
  const trackballUrl = () => {
    const symm = symmetryController();
    const path = symm && controllerProperty( symm, 'modelResourcePath' );
    // Must be a FULLY-QUALIFIED url: the worker runs from a blob: origin and fetches this string
    // directly (fetchUrlText), so a root-relative "/app/classic/resources/..." would resolve
    // against the blob origin and 403. Resolve against window.location here (the client has it;
    // the worker doesn't, which is why the old trackball path passed baseURL to `new URL`).
    // resourceUrl() gives the root-relative path; new URL(..., window.location) makes it absolute
    // -- same construction as 59icosahedra's getModelURL.
    const url = path ? new URL( resourceUrl( path ), window.location ) .toString() : undefined;
    return url;
  };

  return (
    <CameraProvider name='trackball' outlines={false} context={context}>
      <Show when={ trackballUrl() } keyed>
        { url => (
          <WorkerProvider>
            <ViewerProvider config={ { url, preview: false, debug: false, camera: false } }>
              <SceneProvider>
                <SceneChangeListener />
                <InteractionToolProvider>
                  <SnapCameraTool />
                  <TrackballCanvas />
                </InteractionToolProvider>
              </SceneProvider>
            </ViewerProvider>
          </WorkerProvider>
        ) }
      </Show>
    </CameraProvider>
  );
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