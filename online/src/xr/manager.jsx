
import { createContext, useContext, createEffect, on, } from "solid-js";
import { Vector3, Quaternion, } from "three";
import { createText, XRControllerModelFactory, XRHandModelFactory, } from "three-stdlib";
import { useThree, useFrame, useXR, } from "solid-three";

// ──────────────────────────────────────────────────────────────────────────────
// Reads the current XR viewer pose from the GL context.  Only valid to call
// when xr.isPresenting is true (i.e. inside a contribution's isPresenting effect).
export const xrViewerPose = ( store ) => {
  const xrCamera = store.gl.xr.getCamera();
  const viewerPos  = xrCamera.getWorldPosition( new Vector3() );
  const viewerQuat = xrCamera.getWorldQuaternion( new Quaternion() );
  const forward    = new Vector3(0, 0, -1).applyQuaternion( viewerQuat );
  return { viewerPos, viewerQuat, forward };
};

// ──────────────────────────────────────────────────────────────────────────────
// Context shared between XRSessionManager and its contribution subcomponents

const XRSessionContext = createContext();
export const useXRSession = () => useContext(XRSessionContext);

const XRControllerContext = createContext();
export const useXRControllers = () => useContext(XRControllerContext);

// ──────────────────────────────────────────────────────────────────────────────
// XRSessionManager — generic AR session lifecycle manager.
// Saves/restores camera & scene state, dispatches onViewerStart/onViewerEnd
// to registered contributions, and renders props.children.

export const XRSessionManager = ( props ) =>
{
  const store = useThree();

  const viewerStartHandlers = [];
  const viewerEndHandlers   = [];

  const onViewerStart = ( fn ) => viewerStartHandlers.push( fn );
  const onViewerEnd   = ( fn ) => viewerEndHandlers.push( fn );

  let _origBackground, _origFog;
  let _origFov, _origAspect, _origNear, _origFar;
  let _origCameraPos, _origCameraQuat;
  let _origControlsTarget;
  let needsCameraRestore = false;
  let sessionActivated = false;
  let sessionInProgress = false;
  let endSessionPending = false;

  const startSession = () =>
  {
    // Toggle scene background/fog and orbit trackball for AR passthrough
    _origBackground = store.scene.background;
    _origFog        = store.scene.fog;
    _origFov        = store.camera.fov;
    _origAspect     = store.camera.aspect;
    _origNear       = store.camera.near;
    _origFar        = store.camera.far;
    _origCameraPos  = store.camera.position.clone();
    _origCameraQuat = store.camera.quaternion.clone();
    _origControlsTarget = props.trackball.target.clone();

    store.scene.background = null;
    store.scene.fog        = null;
    props.trackball.enabled = false;

    // XR controls FOV/pose from the headset, but near/far come from the Three.js camera.
    // The saved values reflect vZome's abstract coordinate scale; AR is real-world scale,
    // so override with values appropriate for meters (1 cm – 100 m).
    store.camera.near = 0.01;
    store.camera.far  = 100;
    store.camera.updateProjectionMatrix();
    sessionInProgress = true;
  };

  const endSession = () =>
  {
    store.scene.background = _origBackground;
    store.scene.fog        = _origFog;

    store.camera.fov    = _origFov;
    store.camera.aspect = _origAspect;
    store.camera.near   = _origNear;
    store.camera.far    = _origFar;

    store.camera.position.copy(_origCameraPos);
    store.camera.quaternion.copy(_origCameraQuat);

    props.trackball.target.copy(_origControlsTarget);
    props.trackball.enabled = true;

    sessionInProgress = false;
    for (const fn of viewerEndHandlers) fn();
    needsCameraRestore = true;
  };

  const { isPresenting } = useXR();

  createEffect( on( isPresenting, ( presenting ) => {
    if ( presenting ) {
      endSessionPending = false;       // cancel any deferred end — brief interruption, session resumed
      if ( !sessionInProgress ) {      // only start if not already in a session
        startSession();
        sessionActivated = true;
      }
    } else {
      endSessionPending = true;        // defer to next frame; if presenting resumes, it will be cancelled
    }
  }, { defer: true } ) );

  useFrame( () => {
    if (endSessionPending) {
      endSessionPending = false;
      endSession();
    }
    if (needsCameraRestore) {
      needsCameraRestore = false;
      store.camera.updateProjectionMatrix();
      store.camera.updateMatrixWorld(true);
      props.trackball.update();              // re-derives its internal state from camera
      store.gl.render( store.scene, store.camera ); // render a frame to ensure the restored camera state is visible immediately
    }
    if ( sessionActivated ) {
      sessionActivated = false;
      for (const fn of viewerStartHandlers) fn(); // xr camera pose is now available
    }
  });

  return (
    <XRSessionContext.Provider value={{ onViewerStart, onViewerEnd, getRootGroup: props.getRootGroup }}>
      <XRControllerManager>
        {props.children}
      </XRControllerManager>
    </XRSessionContext.Provider>
  );
};

// ──────────────────────────────────────────────────────────────────────────────
// XRControllerManager — manages both XR controllers for the session lifetime.
// Exposes onGripStart / onGripEnd registration via context so contributions
// only declare what they want to happen on each squeeze event.

export const XRControllerManager = ( props ) =>
{
  const { onViewerStart, onViewerEnd } = useXRSession();
  const store = useThree();

  const controllerConnectedHandlers = [];
  const gripStartHandlers = [];
  const gripEndHandlers   = [];
  const scaleStartHandlers = [];
  const scaleEndHandlers   = [];

  const onControllerConnected = ( fn ) => controllerConnectedHandlers.push( fn );
  const onGripStart = ( fn ) => gripStartHandlers.push( fn );
  const onGripEnd   = ( fn ) => gripEndHandlers.push( fn );
  const onScaleStart = ( fn ) => scaleStartHandlers.push( fn );
  const onScaleEnd   = ( fn ) => scaleEndHandlers.push( fn );

  const posA = new Vector3();
  const posB = new Vector3();
  const getControllerSeparation = () => {
    const controllerA = store.gl.xr.getController( 0 );
    const controllerB = store.gl.xr.getController( 1 );
    return controllerA.getWorldPosition( posA ).distanceTo( controllerB.getWorldPosition( posB ) );
  };

  let controllers = [];
  let squeezedControllers = new Set();
  let scalingActive = false;

  const controllerModelFactory = new XRControllerModelFactory();
  const handModelFactory = new XRHandModelFactory();

  onViewerStart( () => {
    controllers = [];
    squeezedControllers.clear();
    scalingActive = false;
    for (let i = 0; i < 2; i++) {
      const controller = store.gl.xr.getController( i );
      const controllerGrip = store.gl.xr.getControllerGrip( i );
      controllerGrip.add( controllerModelFactory.createControllerModel( controllerGrip ) );

      const hand = store.gl.xr.getHand( i );
      hand.add(handModelFactory.createHandModel(hand, 'mesh'));
      // const handPointer = new OculusHandPointerModel( hand, controller );
      // hand.add( handPointer );

      const handleControllerConnected = ( event ) => {
        for (const fn of controllerConnectedHandlers) fn( controller, event.data.handedness, hand );
      };
      const handleSqueezeStart = () => {
        squeezedControllers.add( controller );
        for (const fn of gripStartHandlers) fn( controller );
        if ( !scalingActive && squeezedControllers.size === 2 ) {
          scalingActive = true;
          const [ controllerA, controllerB ] = Array.from( squeezedControllers );
          for (const fn of scaleStartHandlers) fn( controllerA, controllerB );
        }
      };
      const handleSqueezeEnd   = () => {
        squeezedControllers.delete( controller );
        for (const fn of gripEndHandlers) fn( controller );
        if ( scalingActive && squeezedControllers.size < 2 ) {
          scalingActive = false;
          for (const fn of scaleEndHandlers) fn();
        }
      };

      controller.addEventListener( 'connected', handleControllerConnected );
      controller.addEventListener( 'squeezestart', handleSqueezeStart );
      controller.addEventListener( 'squeezeend', handleSqueezeEnd   );

      store.scene.add( hand );
      store.scene.add( controller );
      store.scene.add( controllerGrip );

      controllers.push({ controller, controllerGrip, hand, handleControllerConnected, handleSqueezeStart, handleSqueezeEnd });
    }
  });

  onViewerEnd( () => {
    if ( scalingActive ) {
      scalingActive = false;
      for (const fn of scaleEndHandlers) fn();
    }
    squeezedControllers.clear();
    gripStartHandlers.length = 0;
    gripEndHandlers.length   = 0;
    scaleStartHandlers.length = 0;
    scaleEndHandlers.length   = 0;
    for (const { controller, controllerGrip, hand, handleControllerConnected, handleSqueezeStart, handleSqueezeEnd } of controllers) {
      controller.removeEventListener( 'connected', handleControllerConnected );
      controller.removeEventListener( 'squeezestart', handleSqueezeStart );
      controller.removeEventListener( 'squeezeend', handleSqueezeEnd   );
      store.scene.remove( controller );
      store.scene.remove( controllerGrip );
      store.scene.remove( hand );
    }
    controllers = [];
  });

  return (
    <XRControllerContext.Provider value={{ onControllerConnected, onGripStart, onGripEnd, onScaleStart, onScaleEnd, getControllerSeparation }}>
      {props.children}
    </XRControllerContext.Provider>
  );
};

