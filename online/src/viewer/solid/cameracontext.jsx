
import { createContext, createSignal, useContext } from 'solid-js';
import { Quaternion } from 'three';
import { NEAR_FACTOR, FAR_FACTOR } from '../../workerClient/actions.js';
import { useWorkerClient } from '../../workerClient/context.jsx';

const toVector = vector3 =>
{
  const { x, y, z } = vector3;
  return [ x, y, z ];
}

const extractCanonicalCamera = ( camera, target ) =>
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

// We need to record the sourceCamera so we can make sure that trackball changes
//  don't try to drive the camera for the same scene

const defaultRotation = { quaternion: new Quaternion(), sourceCamera: null };

const CameraContext = createContext( [] );

const CameraProvider = (props) =>
{
  const { setState } = useWorkerClient();
  const [ lastRotation, setLastRotation ] = createSignal( defaultRotation );
  const publishRotation = ( quaternion, sourceCamera ) => setLastRotation( { quaternion, sourceCamera } );

  const adjustFrustum = ( camera, target ) =>
  {
    const { distance } = extractCanonicalCamera( camera, target );
    // Keep the view frustum at a constant shape, adjusting near & far to track distance
    const near = distance * NEAR_FACTOR;
    const far = distance * FAR_FACTOR;
    setState( 'scene', 'camera', { far, near } );
  }

  const recordCamera = ( camera, target ) =>
  {
    setState( 'liveCamera', extractCanonicalCamera( camera, target ) );
  }

  const value = {
    lastRotation, publishRotation,
    adjustFrustum, recordCamera,
  };

  return (
    <CameraContext.Provider value={value}>
      {props.children}
    </CameraContext.Provider>
  );
}

function useCamera() { return useContext( CameraContext ); }

export { useCamera, CameraProvider }