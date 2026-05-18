import { createContext, onMount, useContext, } from "solid-js";

import { T, useThree, useFrame, } from "../util/solid-three.js";
import { useCamera } from "./camera.jsx";

export const WebXRSupport = (props) =>
{
  const store = useThree(); // we need this so we get the live camera later at mount time
  const { scene, canvas, gl } = store;
  const { globalScale } = useCamera();
  let originGroup = null;
  let xrManager = null;
  let trackball = null;

  const setTrackball = (tb) => { trackball = tb; };

  useFrame( () => {
    if ( xrManager ) {
      xrManager .eachFrame();
    }
  } );

  onMount( () => {
    if ( navigator.xr?.isSessionSupported )
      navigator.xr.isSessionSupported('immersive-ar' ).then( ( supported ) =>
    {
      if ( !supported ) {
        console.warn( 'WebXR AR not supported' );
        return;
      }

      // avoid loading all the code and assets for WebXR if it's not supported, but load it dynamically when we know we can use it.
      import( '../../xr/index.jsx' ).then( module => { xrManager = module.initXR( () => originGroup, store, trackball ); } );
    } );
  } );

  const setRootScene = ( scene ) =>
  {
    // This is very heavy handed, and is only used by the glTF and VRML viewers.
    originGroup .clear();
    originGroup .add( scene );
    // Snapshot the array first, then move each child
    const children = [...scene.children];
    for (const child of children) {
      originGroup .add(child); // automatically removes from scene
    }
  };

  return (
    <T.Group ref={originGroup} scale={globalScale} >
      <WebXRContext.Provider value={ { setTrackball, setRootScene, } }>
        {props.children}
      </WebXRContext.Provider>
    </T.Group>
  );
}

const WebXRContext = createContext( {} );

export const useWebXRClient = () => { return useContext( WebXRContext ); };

