
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


export const CameraControls = (props) =>
{
  const context = useCamera();
  const { isWorkerReady, subscribeFor } = useWorkerClient();
  const { rootController, controllerAction } = useEditor();
  const { state, setCamera, togglePerspective, toggleOutlines } = useCamera();
  const { snapping, toggleSnapping } = useSymmetry();
  const [ scene, setScene ] = createStore( null );

  const isPerspective = () => state.camera.perspective;

  // TODO: encapsulate these and createStore() in a new createScene()... 
  //  OR...
  //  Now that worker and canvas are decoupled, we could just use a separate worker for the trackball scene?!
  const addShape = ( shape ) =>
  {
    if ( ! scene .shapes ) {
      setScene( 'shapes', {} );
    }
    if ( ! scene ?.shapes[ shape.id ] ) {
      setScene( 'shapes', shape.id, shape );
      return true;
    }
    return false;
  }
  const updateShapes = ( shapes ) =>
  {
    for (const [id, shape] of Object.entries(shapes)) {
      if ( ! addShape( shape ) ) {
        // shape is not new, so just replace its instances
        setScene( 'shapes', id, 'instances', shape.instances );
      }
    }
    // clean up preview strut, which may be a shape otherwise not in the scene
    for ( const id of Object.keys( scene ?.shapes || {} ) ) {
      if ( ! (id in shapes) )
      setScene( 'shapes', id, 'instances', [] );
    }
  }

  subscribeFor( 'TRACKBALL_SCENE_LOADED', ( scene ) => {
    if ( scene.camera ) {
      const { lookAt, distance, near, far, width } = state.camera;  // This looks circular, but it is not reactive code.
      // Ignore the rotation from the loaded scene.
      setCamera( { lookAt, distance, near, far, width } );
    }
    setScene( 'embedding', scene.embedding );
    updateShapes( scene.shapes );
  });

  // const scene = () => {
  //   let { camera, lighting, ...other } = state.trackballScene;
  //   const { camera: { lookDir, up }, lighting: { backgroundColor } } = state.scene;
  //   camera = { ...camera, lookDir, up }; // override just the orientation
  //   lighting = { ...lighting, backgroundColor }; // override just the background
  //   return ( { ...other, camera, lighting } );
  // }

  // A special action that will result in TRACKBALL_SCENE_LOADED being sent
  createEffect( () => isWorkerReady() && controllerAction( rootController(), 'connectTrackballScene' ) );

  return (
    <CameraProvider name='trackball' outlines={false} context={context}>
    <ImageCaptureProvider> {/* We need this just so we don't set the main capturer from this GL context */}
    <InteractionToolProvider>
      {/* provider and CameraTool just to get the desired cursor */}
      <SnapCameraTool/>
      <div id='camera-controls' style={{ display: 'grid', 'grid-template-rows': 'min-content min-content' }}>
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

        <div id="ball-and-slider" style={{ display: 'grid', 'grid-template-columns': 'min-content 1fr' }}>
          <div id="camera-trackball" style={{ border: '1px solid' }}>
            <SceneCanvas scene={scene} height="200px" width="240px" rotationOnly={true} rotateSpeed={0.7}/>
          </div>
          <div id='zoom-slider' class='placeholder' style={{ 'min-height': '100px', 'min-width': '60px' }} >zoom</div>
        </div>
      </div>
    </InteractionToolProvider>
    </ImageCaptureProvider>
    </CameraProvider>
  )
}
