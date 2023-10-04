
import { createContext, createSignal, useContext } from 'solid-js';
import { initialState } from '../../workerClient';

// We need to record the sourceCamera so we can make sure that trackball changes
//  don't try to drive the camera for the same scene

const defaultRotation = { cameraState: initialState().camera, sourceCamera: null };

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

export { useRotation, RotationProvider }