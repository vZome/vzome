
import { createContext, createSignal, useContext } from 'solid-js';
import { Vector3 } from "three";

import { NEAR_FACTOR, FAR_FACTOR } from "../../workerClient/actions.js";
import { initialState } from '../../workerClient/index.js';
import { initialScenes } from '../../workerClient/controllers-solid.js';
import { createStore } from 'solid-js/store';

// We need to record the sourceCamera so we can make sure that trackball changes
//  don't try to drive the camera for the same scene

const defaultRotation = { cameraState: initialState().camera, sourceCamera: null };

// TODO: combine RotationContext and CameraStateContext?
const RotationContext = createContext( {} );

const RotationProvider = (props) =>
{
  const [ lastRotation, setLastRotation ] = createSignal( defaultRotation );
  const publishRotation = ( cameraState, sourceCamera ) => setLastRotation( { cameraState, sourceCamera } );

  const value = {
    lastRotation, publishRotation,
  };

  return (
    <RotationContext.Provider value={value}>
      {props.children}
    </RotationContext.Provider>
  );
}

function useRotation() { return useContext( RotationContext ); }


const toVector = vector3 =>
{
  const { x, y, z } = vector3;
  return [ x, y, z ];
}

export const extractCameraState = ( camera, target ) =>
{
  const up = toVector( camera.up );
  const position = toVector( camera.position );
  const lookAt = toVector( target );
  const [ x, y, z ] = lookAt.map( (e,i) => e - position[ i ] );
  const distance = Math.sqrt( x*x + y*y + z*z );
  const lookDir = [ x/distance, y/distance, z/distance ];

  // This was missing, and vZome reads width to set FOV
  const fovX = camera.fov * (Math.PI/180) * camera.aspect; // Switch from Y-based FOV degrees to X-based radians
  const width = 2 * distance * Math.tan( fovX / 2 );
  // This is needed to keep the fog depth correct in desktop.
  const far = camera.far;
  const near = camera.near;

  return { lookAt, up, lookDir, distance, width, far, near };
}

export const cameraPosition = ( cameraState ) =>
{
  const { distance, lookAt, lookDir } = cameraState;
  return lookAt .map( (e,i) => e - distance * lookDir[ i ] );
}

export const cameraFieldOfViewY = ( cameraState, aspectWtoH ) =>
{
  const { width, distance } = cameraState;
  const halfX = width / 2;
  const halfY = halfX / aspectWtoH;
  return 360 * Math.atan( halfY / distance ) / Math.PI;
}

const _offset = new Vector3(), _target = new Vector3();

export const injectCameraOrientation = ( cameraState, target, camera ) =>
{
  const { up, lookDir } = cameraState; // ignore distance, lookAt, etc.
  _target.set( ...target );
  _offset .copy( camera.position ) .sub( _target );
  const distance = _offset .length();
  _offset .set( ...lookDir ) .multiplyScalar( -distance );
  camera.up.set( ...up );
  camera.position.addVectors( _target, _offset );
  camera.lookAt( _target );
}

// TODO: combine RotationContext and CameraStateContext?
const CameraStateContext = createContext( {} );

const CameraStateProvider = ( props ) =>
{
  const [ state, setState ] = createStore( { ...initialScenes() } );

  const adjustFrustum = ( camera, target ) =>
  {
    const { distance } = extractCameraState( camera, target );
    // Keep the view frustum at a constant shape, adjusting near & far to track distance
    const near = distance * NEAR_FACTOR;
    const far = distance * FAR_FACTOR;
    setState( 'scene', 'camera', { far, near } );
  }

  // TODO: find a more appropriate place to implement this
  const recordCamera = ( camera, target ) =>
    setState( 'liveCamera', extractCameraState( camera, target ) );
  
  return (
    <CameraStateContext.Provider value={ { adjustFrustum, recordCamera, state } }>
      {props.children}
    </CameraStateContext.Provider>
  );
}

const useCameraState = () => { return useContext( CameraStateContext ); };

// TODO: combine RotationContext and CameraStateContext?
export { useCameraState, CameraStateProvider };
export { useRotation,    RotationProvider };