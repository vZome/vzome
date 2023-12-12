
import { createEffect } from 'solid-js';
import { createStore } from 'solid-js/store';

import { useWorkerClient } from '../../../viewer/context/worker.jsx';
import { useEditor } from '../../../viewer/context/editor.jsx';
import { CameraProvider, useCamera } from '../../../viewer/context/camera.jsx';
import { CameraTool, InteractionToolProvider } from '../../../viewer/context/interaction.jsx';

import { SceneCanvas } from '../../../viewer/scenecanvas.jsx';


export const CameraControls = (props) =>
{
  const context = useCamera();
  const { isWorkerReady, subscribeFor } = useWorkerClient();
  const { rootController, controllerAction } = useEditor();
  const { state, setCamera } = useCamera();
  const [ scene, setScene ] = createStore( null );

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

  subscribeFor( 'TRACKBALL_SCENE_LOADED', ( { scene } ) => {
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
    <CameraProvider name='trackball' context={context}>
    <InteractionToolProvider>
      {/* provider and CameraTool just to get the desired cursor */}
      <CameraTool/>
      <div id='camera-controls' style={{ display: 'grid', 'grid-template-rows': 'min-content min-content' }}>
        <div id='camera-buttons' class='placeholder' style={{ 'min-height': '60px' }} >perspective | snap | outlines</div>
        <div id="ball-and-slider" style={{ display: 'grid', 'grid-template-columns': 'min-content 1fr' }}>
          <div id="camera-trackball" style={{ border: '1px solid' }}>
            <SceneCanvas scene={scene} height="200px" width="240px" rotationOnly={true} rotateSpeed={0.7}/>
          </div>
          <div id='zoom-slider' class='placeholder' style={{ 'min-height': '100px', 'min-width': '60px' }} >zoom</div>
        </div>
      </div>
    </InteractionToolProvider>
    </CameraProvider>
  )
}
