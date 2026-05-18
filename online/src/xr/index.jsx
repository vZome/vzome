
import { Vector3, Quaternion, } from "three";
import { ARButton, createText, } from "three-stdlib";

export const initXR = ( getRootGroup, store, trackball ) =>
{
  const { scene, canvas, gl } = store;

  let instructionText = null;
  let needsInitialPlacement = false;
  let _origBackground;
  let _origFog;
  let _origFov, _origAspect, _origNear, _origFar;
  let _origCameraPos, _origCameraQuat;
  let _origControlsTarget;
  let needsCameraRestore = false;

  canvas.parentElement.appendChild( ARButton.createButton(gl, { optionalFeatures: ['local-floor'] }));

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

    // XR controls FOV/pose from the headset, but near/far come from the Three.js camera.
    // The saved values reflect vZome's abstract coordinate scale; AR is real-world scale,
    // so override with values appropriate for meters (1 cm – 100 m).
    store.camera.near = 0.01;
    store.camera.far = 100;
    store.camera.updateProjectionMatrix();

    needsInitialPlacement = true;
  });

  gl.xr.addEventListener('sessionend', () => {
    scene.background = _origBackground;
    scene.fog = _origFog;

    instructionText.visible = false;
    getRootGroup().position.copy( new Vector3(0, 0, 0) );

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

    if ( instructionText )
      scene.remove( instructionText );

    needsCameraRestore = true;
  });

  for (let i = 0; i < 2; i++) {
    const controller = gl.xr.getController( i );
    controller.addEventListener( 'squeezestart', () => {
      if (instructionText) {
        instructionText.visible = false;
      }
      controller.attach( getRootGroup() );
    });
    controller.addEventListener( 'squeezeend', () => {
      scene.attach( getRootGroup() );
    });
    scene.add( controller );
  }

  const _forward = new Vector3();
  const _viewerPos = new Vector3();
  const _viewerQuat = new Quaternion();
  const eachFrame = () => {
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
      getRootGroup().position.copy(
        _viewerPos.clone()
          .addScaledVector(_forward, 0.7)
          .add(new Vector3(0, -0.2, 0))
      );
      instructionText = createText( 'Grip to move the model', 0.06 );
      scene.add( instructionText );
      instructionText.position.copy(_viewerPos).addScaledVector(_forward, 0.8);
      instructionText.quaternion.copy(_viewerQuat);
      needsInitialPlacement = false;
    }
  }

  return { eachFrame, };
}