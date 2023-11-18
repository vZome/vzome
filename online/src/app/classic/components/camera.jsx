
import { createEffect } from 'solid-js';
import { SceneCanvas } from '../../../viewer/solid/scenecanvas.jsx';
import { useWorkerClient } from '../../../workerClient/index.js';
import { controllerAction } from '../../../workerClient/controllers-solid.js';
import { CameraTool, InteractionToolProvider } from '../../../viewer/solid/interaction.jsx';
import { createStore } from 'solid-js/store';
import { useCameraState } from '../../../workerClient/camera.jsx';

export const CameraControls = () =>
{
  const { subscribeFor, rootController, isWorkerReady } = useWorkerClient();
  const { state, setCamera } = useCameraState();
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
      const { distance, near, far, width } = state.camera;  // This looks circular, but it is not reactive code.
      // Use the camera from the loaded scene, except for the zoom.
      setCamera( { ...scene.camera, distance, near, far, width } );
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
  )
}
