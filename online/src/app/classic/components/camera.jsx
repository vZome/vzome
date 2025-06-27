
import { createEffect } from 'solid-js';
import { createStore } from 'solid-js/store';

import Stack from "@suid/material/Stack"
import Switch from "@suid/material/Switch";
import FormControlLabel from "@suid/material/FormControlLabel";

import { useEditor } from '../../framework/context/editor.jsx';
import { useSymmetry } from "../context/symmetry.jsx";
import { useWorkerClient } from '../../../viewer/context/worker.jsx';
import { CameraProvider, useCamera } from '../../../viewer/context/camera.jsx';
import { InteractionToolProvider } from '../../../viewer/context/interaction.jsx';
import { SceneCanvas } from '../../../viewer/scenecanvas.jsx';

import { SnapCameraTool } from '../tools/snapcamera.jsx';
import { ImageCaptureProvider } from '../../../viewer/context/export.jsx';
import { ZoomSlider } from './zoomslider.jsx';
import { SceneProvider, useScene } from '../../../viewer/context/scene.jsx';


export const CameraControlsUI = (props) =>
{
  const context = useCamera(); // access the main camera context, not the trackball one
  const { isWorkerReady, subscribeFor } = useWorkerClient();
  const { rootController, controllerAction } = useEditor();
  const { state, setCamera, togglePerspective, toggleOutlines } = useCamera();
  const { snapping, toggleSnapping } = useSymmetry();
  const { setScene, updateShapes } = useScene();

  const isPerspective = () => state.camera.perspective;

  //  Now that worker and canvas are decoupled, we could just use a separate worker for the trackball scene?!
  subscribeFor( 'TRACKBALL_SCENE_LOADED', ( scene ) => {
    if ( scene.camera ) {
      const { lookAt, distance, near, far, width } = state.camera;  // This looks circular, but it is not reactive code.
      // Ignore the rotation from the loaded scene.
      setCamera( { lookAt, distance, near, far, width } );
    }
    setScene( 'embedding', scene.embedding );
    setScene( 'polygons', scene.polygons );
    updateShapes( scene.shapes );
  });

  // A special action that will result in TRACKBALL_SCENE_LOADED being sent
  createEffect( () => isWorkerReady() && controllerAction( rootController(), 'connectTrackballScene' ) );

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
          <CameraProvider name='trackball' outlines={false} context={context}>
            <SceneCanvas height="200px" width="240px" rotationOnly={true} rotateSpeed={0.7}/>
          </CameraProvider>
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
    <ImageCaptureProvider> {/* We need this just so we don't set the main capturer from this GL context */}
      <InteractionToolProvider>
        {/* provider and CameraTool just to get the desired cursor */}
        <SnapCameraTool />
        <SceneProvider passive={true}> {/* passive so it doesn't listen for the main scene */}
          <CameraControlsUI />
        </SceneProvider>
      </InteractionToolProvider>
    </ImageCaptureProvider>
  );
}