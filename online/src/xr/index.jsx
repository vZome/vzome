
import { Vector3, Quaternion, } from "three";
import { ARButton, createText, } from "three-stdlib";
import { useThree, useFrame, T, } from "../viewer/util/solid-three.js";

const StartXRButton = ( props ) =>
{
  const store = useThree(); // we need this so we get the live camera later at mount time

  let instructionText = null;
  let needsInitialPlacement = false;
  let _origBackground;
  let _origFog;
  let _origFov, _origAspect, _origNear, _origFar;
  let _origCameraPos, _origCameraQuat;
  let _origControlsTarget;
  let needsCameraRestore = false;

  store.canvas.parentElement.appendChild( ARButton.createButton(store.gl, { optionalFeatures: ['local-floor'] }));

  const startSession = () =>
  {
    // Toggle scene background/fog and orbit trackball for AR passthrough
    _origBackground = store.scene.background;
    _origFog = store.scene.fog;
    _origFov = store.camera.fov;
    _origAspect = store.camera.aspect;
    _origNear = store.camera.near;
    _origFar = store.camera.far;
    _origCameraPos = store.camera.position.clone();
    _origCameraQuat = store.camera.quaternion.clone();
    _origControlsTarget = props.trackball.target.clone();

    store.scene.background = null;
    store.scene.fog = null;
    props.trackball.enabled = false;

    // XR controls FOV/pose from the headset, but near/far come from the Three.js camera.
    // The saved values reflect vZome's abstract coordinate scale; AR is real-world scale,
    // so override with values appropriate for meters (1 cm – 100 m).
    store.camera.near = 0.01;
    store.camera.far = 100;
    store.camera.updateProjectionMatrix();

    instructionText = createText( 'Grip to move the model', 0.06 );
    store.scene.add( instructionText );

    needsInitialPlacement = true;
  }

  const endSession = () =>
  {
    store.scene.background = _origBackground;
    store.scene.fog = _origFog;

    instructionText.visible = false;
    props.getRootGroup().position.copy( new Vector3(0, 0, 0) );

    store.camera.fov    = _origFov;
    store.camera.aspect = _origAspect;
    store.camera.near   = _origNear;
    store.camera.far    = _origFar;

    store.camera.position.copy(_origCameraPos);
    store.camera.quaternion.copy(_origCameraQuat);

    props.trackball.target.copy( _origControlsTarget );
    props.trackball.enabled = true;

    if ( instructionText )
      store.scene.remove( instructionText );

    needsCameraRestore = true;
  }

  store.gl.xr.addEventListener( 'sessionstart', startSession );
  store.gl.xr.addEventListener( 'sessionend',   endSession   );

  for (let i = 0; i < 2; i++) {
    const controller = store.gl.xr.getController( i );
    controller.addEventListener( 'squeezestart', () => {
      if (instructionText) {
        instructionText.visible = false;
      }
      controller.attach( props.getRootGroup() );
    });
    controller.addEventListener( 'squeezeend', () => {
      store.scene.attach( props.getRootGroup() );
    });
    store.scene.add( controller );
  }

  const _forward = new Vector3();
  const _viewerPos = new Vector3();
  const _viewerQuat = new Quaternion();
  useFrame( () => {
    if (needsCameraRestore) {
      needsCameraRestore = false;
      store.camera.updateProjectionMatrix();
      store.camera.updateMatrixWorld(true);
      props.trackball.update();              // re-derives its internal state from camera
      store.gl.render( store.scene, store.camera ); // render a frame to ensure the restored camera state is visible immediately
    }
    if (needsInitialPlacement && store?.gl?.xr?.isPresenting) {
      const xrCamera = store.gl.xr.getCamera();
      xrCamera.getWorldPosition(_viewerPos);
      xrCamera.getWorldQuaternion(_viewerQuat);
      _forward.set(0, 0, -1).applyQuaternion(_viewerQuat);

      instructionText.position.copy(_viewerPos).addScaledVector(_forward, 0.8);
      instructionText.quaternion.copy(_viewerQuat);
      needsInitialPlacement = false;

      props.getRootGroup().position.copy(
        _viewerPos.clone()
          .addScaledVector(_forward, 0.7)
          .add(new Vector3(0, -0.2, 0))
      );
    }
  } );

  return null;
}

export default StartXRButton;