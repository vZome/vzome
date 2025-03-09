
import { createContext, createEffect, useContext } from 'solid-js';
import { PerspectiveCamera, Vector3, Quaternion } from "three";
import { Group, Tween, Easing } from '@tweenjs/tween.js';

import { createStore } from 'solid-js/store';

export const INITIAL_DISTANCE = 108;
const NEAR_FACTOR = 0.1 / INITIAL_DISTANCE;
const FAR_FACTOR = 2.0;
const WIDTH_FACTOR = 0.45;

const defaultCamera = () => ({
  // zoom
  distance: INITIAL_DISTANCE,
  near: INITIAL_DISTANCE * NEAR_FACTOR,
  far: INITIAL_DISTANCE * FAR_FACTOR,
  width: INITIAL_DISTANCE * WIDTH_FACTOR,
  // pan
  lookAt: [ 0, 0, 0 ],
  // rotation
  up: [ 0, 1, 0 ],
  lookDir: [ 0, 0, -1 ],
  // other
  perspective: true,
  default: true,
});

const defaultLighting = () => ({
  backgroundColor: '#8CC2E7',
  ambientColor: '#333333',
  directionalLights: [ // These are the vZome defaults, for consistency
    { direction: [ 1, -1, -0.3 ], color: '#FDFDFD' },
    { direction: [ -1, 0, -0.2 ], color: '#B5B5B5' },
    { direction: [ 0, 0, -1 ], color: '#303030' },
  ]
});

const defaultScene = () => ({
  camera: defaultCamera(),
  lighting: defaultLighting(),
  outlines: false,
  tweening: {
    duration: 500,
  }
});

const toVector = vector3 =>
{
  const { x, y, z } = vector3;
  return [ x, y, z ];
}
  
export const fixedFrustum = distance =>
{
  const near = distance * NEAR_FACTOR;
  const far = distance * FAR_FACTOR;
  const width = distance * WIDTH_FACTOR;
  return { distance, far, near, width };
}

/**
 * Extract Three.js camera data to our canonical state representation
 * @param {*} camera, a Three.js Camera
 * @param {*} target, a Three.js Object with a position
 * @returns our canonical camera state
 */
const extractCameraState = ( camera, target ) =>
{
  const up = toVector( camera.up );
  const position = toVector( camera.position );
  const lookAt = toVector( target );
  const [ x, y, z ] = lookAt.map( (e,i) => e - position[ i ] );
  const distance = Math.sqrt( x*x + y*y + z*z );
  const lookDir = [ x/distance, y/distance, z/distance ];

  // This was missing, and vZome reads width to set FOV
  // const fovX = camera.fov * (Math.PI/180) * camera.aspect; // Switch from Y-based FOV degrees to X-based radians
  // const width = 2 * distance * Math.tan( fovX / 2 );
  // This is needed to keep the fog depth correct in desktop.
  const { near, far, width } = fixedFrustum( distance );

  return { lookAt, up, lookDir, distance, width, far, near };
}

const cameraPosition = ( cameraState ) =>
{
  const { distance, lookAt, lookDir } = cameraState;
  return lookAt .map( (e,i) => e - distance * lookDir[ i ] );
}

const cameraFieldOfViewY = ( cameraState ) => ( aspectWtoH ) =>
{
  const { width, distance } = cameraState;
  const halfX = width / 2;
  const halfY = halfX / aspectWtoH;
  return 360 * Math.atan( halfY / distance ) / Math.PI;
}

/**
 * Inject our canonical camera state representation into a Three.js Camera
 * @param {*} cameraState 
 * @param {*} camera, the destination Three.js Camera
 */
const injectCameraState = ( cameraState, camera ) =>
{
  const { up, lookAt } = cameraState;
  camera.up .set( ...up );
  camera.position .set( ...cameraPosition( cameraState ) );
  camera .lookAt( ...lookAt );
  camera.fov = cameraFieldOfViewY( cameraState )( 1.0 );
}

export const createDefaultCameraStore = () => createStore( { ...defaultScene() } );

const CameraContext = createContext( {} );

const CameraProvider = ( props ) =>
{
  const [ state, setState ] = props.cameraStore || createDefaultCameraStore();

  if ( !! props.distance ) {
    setState( 'camera', fixedFrustum( props.distance ) );
  }

  if ( props.outlines !== undefined ) {
    setState( 'outlines', props.outlines );
  }

  const setCamera = loadedCamera =>
  {
    setState( 'camera', loadedCamera );
    // important: update trackballCamera synchronously, NOT as an effect.
    injectCameraState( state.camera, trackballCamera );
  }
  
  if ( !!props.context ) {
    // Sync background from the context
    createEffect( () => {
      const { backgroundColor } = props.context.state.lighting;
      setLighting( { backgroundColor } );
    });
    // Sync rotation from the context
    createEffect( () => {
      const { up, lookDir } = props.context.state.camera;
      setCamera( { up, lookDir } );
    });
  }

  const fov = cameraFieldOfViewY( state.camera );
  const [ perspectiveProps, setPerspectiveProps ] = createStore( { fov } );  
  // This effect keeps the perspectiveProps in sync with the recorded camera state,
  //   thus propagating to all client LightedCameraControls.
  createEffect( () => {
    const position = cameraPosition( state.camera );
    const { near, far, up, lookAt, width } = state.camera;
    // I had a nasty bug for days because I used lookAt by reference, causing the CameraControls canvas
    //  to respond very oddly to shared rotations.
    setPerspectiveProps( { position, up, target: [ ...lookAt ], near, far, width } );
  })

  const trackballCamera = new PerspectiveCamera(); // for the TrackballControls only, never used to render
  injectCameraState( state.camera, trackballCamera );
  const sync = ( target, name ) =>
  {
    // This gets hooked up to TrackballControls changes, and updates the main camera state
    //   from the captive trackballCamera in response.
    const extractedCamera = extractCameraState( trackballCamera, target );
    // console.log( `trackball sync ${name}, distance=${extractedCamera.distance}` );
    const { up, lookDir } = extractedCamera;
    if ( !! props.context ) {
      props.context.setCamera( { up, lookDir } );
    } else {
      // console.log( `trackball sync, distance=${extractedCamera.distance}` );

      // NOTE: We get this for any camera update, not just trackball moves, so we can't kill the tweens;
      //         ... the tween itself triggers these sync() callbacks.
      // tweens .getAll() .map( tween => tween.stop() );

      setState( 'camera', extractedCamera );
    }
  }
  const trackballProps = { camera: trackballCamera, sync }; // no need (or desire) for reactivity here

  const mapViewToWorld = ( [ x, y, z ] ) =>
  {
    const vec = new Vector3( x, y, z );
    vec .transformDirection( trackballCamera.matrixWorldInverse );
    return [ vec.x, vec.y, vec.z ];
  }

  const setDistance = distance =>
  {
    setCamera( fixedFrustum( distance ) );
  }

  const setLighting = lighting => setState( 'lighting', lighting );
  const togglePerspective = () => setState( 'camera', 'perspective', val => !val );
  const toggleOutlines = () => setState( 'outlines', val => !val );

  const resetCamera = () =>
  {
    setState( 'camera', defaultCamera() );
    setState( 'lighting', defaultLighting() );
  }

  const tweens = new Group();
  const animate = () =>
  {
    requestAnimationFrame( animate );
    tweens .update();
  }
  animate();
    
  const cancelTweens = () =>
  {
    tweens .getAll() .map( tween => tween.stop() );
    tweens .removeAll();
  }
  
  const tweenCamera = ( goalCamera ) =>
  {
    const { duration=0 } = state.tweening;

    if ( duration <= 0 ) {
      setCamera( goalCamera );
      return Promise.resolve();
    }

    cancelTweens();
    
    const { distance, lookAt: [x,y,z] } = goalCamera; // target values

    const tweenZoom = new Tween( { distance: state.camera.distance } ); // start with current value
    tweens .add( tweenZoom );
    tweenZoom
      .easing( Easing.Quadratic.InOut )
      .to( { distance }, duration )
      .onUpdate( ({ distance }) => setDistance( distance ) );

    const [ cx, cy, cz ] = state.camera.lookAt;
    const tweenPan = new Tween( { x: cx, y: cy, z: cz } ); // start with current value
    tweens .add( tweenPan );
    tweenPan
      .easing( Easing.Quadratic.InOut )
      .to( { x, y, z }, duration )
      .onUpdate( ({ x, y, z }) => setCamera( { lookAt: toVector( { x, y, z } ) } ) );

    // HEAVILY adapted from https://stackoverflow.com/questions/28091876/tween-camera-position-while-rotation-with-slerp-three-js

    const sourceObject3D = new PerspectiveCamera(); // could be any Object3D
    injectCameraState( state.camera, sourceObject3D );

    const goalObject3D = new PerspectiveCamera(); // could be any Object3D
    injectCameraState( goalCamera, goalObject3D );
    const goalQuaternion = new Quaternion() .copy( goalObject3D.quaternion ); // goal quaternion

    const tweenRotate = new Tween( { t: 0 } );
    tweens .add( tweenRotate );
    return new Promise( resolve => {
      tweenRotate
      .easing( Easing.Quadratic.InOut )
      .to( { t: 1.0 }, duration )
      .onUpdate( ({ t }) => {
        const sourceQuaternion = new Quaternion() .copy( sourceObject3D.quaternion ); // src quaternion
        sourceQuaternion .slerp( goalQuaternion, t );
        const up = toVector( new Vector3(0,1,0) .applyQuaternion( sourceQuaternion ) );
        const lookDir = toVector( new Vector3(0,0,-1) .applyQuaternion( sourceQuaternion ) );
        setCamera( { up, lookDir } );
      })
      .onComplete( () => {
        resolve();
      })
      .onStop( () => {
        resolve();
      });
    
      // We don't want to do this sync, since the tween duration may expire before subsequent
      //  code in the caller finishes.
      requestAnimationFrame( () => tweens .getAll() .map( tween => tween.start() ) );
    });
  }
  
  const providerValue = {
    name: props.name,
    perspectiveProps, trackballProps, state,
    resetCamera, setCamera, tweenCamera, cancelTweens, setLighting, togglePerspective, toggleOutlines, setDistance, mapViewToWorld,
  };
  
  // The perspectiveProps is used to initialize PerspectiveCamera in clients.
  // The trackballProps is used to initialize TrackballControls in clients.
  // The perspectiveProps reacts to changes in state, which reacts to changes in the trackballCamera.
  return (
    <CameraContext.Provider value={ providerValue }>
      {props.children}
    </CameraContext.Provider>
  );
}

const useCamera = () => { return useContext( CameraContext ); };

const copyOfCamera = camera =>
{
  const { up, lookAt, lookDir, ...rest } = camera; // don't want copy-by-reference for the arrays
  return { ...rest, up: [...up], lookAt: [...lookAt], lookDir: [...lookDir] };
}

export { defaultCamera, useCamera, CameraProvider, copyOfCamera };
