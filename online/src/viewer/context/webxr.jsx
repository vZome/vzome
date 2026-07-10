import { lazy, Suspense, Show, createResource, createContext, useContext, } from "solid-js";

import { createT } from 'solid-three';
import { Group } from "three";
const T = createT({ Group });
import { useCamera } from "./camera.jsx";

export const WebXRSupport = (props) =>
{
  const { globalScale } = useCamera();
  let originGroup = null;
  let trackball = null;

  const setTrackball = (tb) => { 
    trackball = tb;
  };

  const StartXRButton = lazy(() => import( "../../xr/index.jsx" ));

  const checkXRSupport = async () => typeof navigator.xr === "object" && navigator.xr && "isSessionSupported" in navigator.xr && await navigator.xr.isSessionSupported( 'immersive-ar' );

  const [xrSupported] = createResource( checkXRSupport );

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
    <>
      <T.Group ref={originGroup} scale={globalScale} >
        <WebXRContext.Provider value={ { setRootScene, setTrackball, } }>
          {props.children}
        </WebXRContext.Provider>
      </T.Group>
      {/* Having this Suspense inside the T.Group was causing a weird scaling issue when XR is supported */}
      <Suspense>
        <Show when={xrSupported()}>
          <StartXRButton getRootGroup={() => originGroup} trackball={trackball} />
        </Show>
      </Suspense>
    </>
  );
}

const WebXRContext = createContext( {} );

export const useWebXRClient = () => { return useContext( WebXRContext ); };

