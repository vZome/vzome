import { createContext, onMount, useContext, } from "solid-js";

import { Vector3, Quaternion, } from "three";
import { T, useThree, useFrame, } from "../util/solid-three.js";
import { GLTFExporter, ARButton, createText, } from "three-stdlib";
import { useCamera } from "./camera.jsx";

export const WebXRSupport = (props) =>
{
  const store = useThree(); // we need this so we get the live camera later at mount time
  const { scene, canvas, gl } = store;
  const { globalScale } = useCamera();
  // let symmetrygl;
  let instructionText = null;
  let needsInitialPlacement = false;
  let originGroup = null;
  let trackball = null;

  let _origBackground;
  let _origFog;
  let _origFov, _origAspect, _origNear, _origFar;
  let _origCameraPos, _origCameraQuat;
  let _origControlsTarget;
  let needsCameraRestore = false;

  const setTrackball = (tb) => { trackball = tb; };

  onMount( () =>{
    canvas.parentElement.appendChild(ARButton.createButton(gl, {
      optionalFeatures: ['local-floor']
    }));

    instructionText = createText( 'Grip to move the model', 0.03 );
    scene.add( instructionText );
    instructionText.visible = false;

    gl.xr.addEventListener('sessionstart', () => {
      // Toggle scene background/fog and orbit trackball for AR passthrough
      _origBackground = scene.background;
      _origFog = scene.fog;
      _origFov = store.camera.fov;
      _origAspect = store.camera.aspect;
      _origNear = store.camera.near;
      _origFar = store.camera.far;
      _origCameraPos = store.camera.position.clone();
      _origCameraQuat = store.camera.quaternion.clone();
      _origControlsTarget = trackball.target.clone();

      scene.background = null;
      scene.fog = null;
      trackball.enabled = false;
      instructionText.visible = true;
      needsInitialPlacement = true;
    });

    gl.xr.addEventListener('sessionend', () => {
      scene.background = _origBackground;
      scene.fog = _origFog;

      instructionText.visible = false;
      originGroup.position.copy( new Vector3(0, 0, 0) );

      // 1. Restore your saved camera parameters
      store.camera.fov = _origFov;
      store.camera.aspect = _origAspect;
      store.camera.near   = _origNear;
      store.camera.far    = _origFar;

      // 3. Restore position/rotation/scale if you saved them
      store.camera.position.copy(_origCameraPos);
      store.camera.quaternion.copy(_origCameraQuat);

      // 5. If you use OrbitControls, re-sync it to the restored camera state
      trackball.target.copy( _origControlsTarget );
      trackball.enabled = true;

      needsCameraRestore = true;
    });

    for (let i = 0; i < 2; i++) {
      const controller = gl.xr.getController( i );
      controller.addEventListener( 'squeezestart', () => {
        controller.attach( originGroup );
      });
      controller.addEventListener( 'squeezeend', () => {
        scene.attach( originGroup );
        if (instructionText) instructionText.visible = false;
      });
      scene.add( controller );
    }
  } );

  const _forward = new Vector3();
  const _viewerPos = new Vector3();
  const _viewerQuat = new Quaternion();
  useFrame( () => {
    if (needsCameraRestore) {
      needsCameraRestore = false;
      store.camera.updateProjectionMatrix();
      store.camera.updateMatrixWorld(true);
      trackball.update();              // re-derives its internal state from camera
      gl.render( scene, store.camera ); // render a frame to ensure the restored camera state is visible immediately
    }
    if (needsInitialPlacement && gl.xr.isPresenting) {
      const xrCamera = gl.xr.getCamera();
      xrCamera.getWorldPosition(_viewerPos);
      xrCamera.getWorldQuaternion(_viewerQuat);
      _forward.set(0, 0, -1).applyQuaternion(_viewerQuat);
      originGroup.position.copy(
        _viewerPos.clone()
          .addScaledVector(_forward, 0.7)
          .add(new Vector3(0, -0.2, 0))
      );
      if (instructionText) {
        instructionText.position.copy(_viewerPos).addScaledVector(_forward, 0.6);
        instructionText.quaternion.copy(_viewerQuat);
      }
      needsInitialPlacement = false;
    }
  } );

  const setRootScene = ( scene ) =>
  {
    originGroup.clear();
    originGroup .add( scene );
    // Snapshot the array first, then move each child
    const children = [...scene.children];
    for (const child of children) {
      originGroup.add(child); // automatically removes from scene
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

