import { lazy, Suspense, Show, createContext, createSignal, useContext, } from "solid-js";

import { createT } from 'solid-three';
import { Group } from "three";
const T = createT({ Group });
import { useCamera } from "./camera.jsx";

export const WebXRSupport = (props) =>
{
  const { globalScale } = useCamera();
  // originGroup needs to be BOTH a real JSX-nested <T.Group> (so children declared here --
  // the camera, TrackballControls, Labels, ShapedGeometry -- compose into it the normal
  // solid-three way) AND imperatively reachable by code that isn't part of that JSX tree
  // (XRGripToMove/XRScaling in src/xr/, and SymmetryGeometry, which builds its own Three.js
  // objects outside solid-three's reconciler). Those two requirements are in tension:
  // solid-three constructs a <T.*> element's underlying instance lazily, behind an internal
  // memo forced only when something reads it (inside a createRenderEffect) -- so there is no
  // point at which "the group exists" is synchronously true relative to a sibling or
  // descendant component's own setup code. `originGroupReady`, a signal set from the group's
  // ref callback, is the correct way to bridge that: consumers that need the group at their
  // own setup time (SymmetryGeometry) gate on the signal; consumers that only need it lazily,
  // well after mount (XRGripToMove etc., inside onViewerStart/frame callbacks -- see
  // getRootGroup below) can keep reading the plain `originGroup` variable directly.
  let originGroup = null;
  let trackball = null;

  const [ originGroupReady, setOriginGroupReady ] = createSignal( null );
  const captureOriginGroup = ( g ) => {
    originGroup = g;
    setOriginGroupReady( g );
  };

  const setTrackball = (tb) => {
    trackball = tb;
  };

  const StartXRButton = lazy(() => import( "../../xr/index.jsx" ));

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
      <T.Group ref={captureOriginGroup} scale={globalScale} >
        <WebXRContext.Provider value={ { setRootScene, setTrackball, originGroupReady, } }>
          {props.children}
        </WebXRContext.Provider>
      </T.Group>
      {/* Having this Suspense inside the T.Group was causing a weird scaling issue when XR is supported */}
      <Suspense>
        <Show when={props.xrSupported && props.xrSupported()}>
          <StartXRButton getRootGroup={() => originGroup} trackball={trackball} />
        </Show>
      </Suspense>
    </>
  );
}

const WebXRContext = createContext( {} );

export const useWebXRClient = () => { return useContext( WebXRContext ); };

