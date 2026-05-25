
import { createContext, useContext, } from "solid-js";
import { Vector3, Quaternion, } from "three";
import { ARButton, createText, } from "three-stdlib";
import { useThree, useFrame, } from "../viewer/util/solid-three.js";

// ──────────────────────────────────────────────────────────────────────────────
// Reads the current XR viewer pose from the GL context.  Only valid to call
// when xr.isPresenting is true (i.e. inside a contribution's isPresenting effect).
const xrViewerPose = ( store ) => {
  const xrCamera = store.gl.xr.getCamera();
  const viewerPos  = xrCamera.getWorldPosition( new Vector3() );
  const viewerQuat = xrCamera.getWorldQuaternion( new Quaternion() );
  const forward    = new Vector3(0, 0, -1).applyQuaternion( viewerQuat );
  return { viewerPos, viewerQuat, forward };
};

// ──────────────────────────────────────────────────────────────────────────────
// Context shared between XRSessionManager and its contribution subcomponents

const XRContext = createContext();
export const useXR = () => useContext(XRContext);

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
  let sessionStarted = false;

  store.canvas.parentElement.appendChild( ARButton.createButton(store.gl, { optionalFeatures: ['local-floor'] }));

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

    sessionStarted = true; // useFrame will dispatch onViewerStart once xr.isPresenting is confirmed
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

    sessionStarted = false;
    for (const fn of viewerEndHandlers) fn();
    needsCameraRestore = true;
  };

  store.gl.xr.addEventListener( 'sessionstart', startSession );
  store.gl.xr.addEventListener( 'sessionend',   endSession   );

  useFrame( () => {
    if (needsCameraRestore) {
      needsCameraRestore = false;
      store.camera.updateProjectionMatrix();
      store.camera.updateMatrixWorld(true);
      props.trackball.update();              // re-derives its internal state from camera
      store.gl.render( store.scene, store.camera ); // render a frame to ensure the restored camera state is visible immediately
    }
    if ( sessionStarted && store?.gl?.xr?.isPresenting ) {
      sessionStarted = false;
      for (const fn of viewerStartHandlers) fn(); // xr camera pose is now available
    }
  });

  return (
    <XRContext.Provider value={{ onViewerStart, onViewerEnd, getRootGroup: props.getRootGroup }}>
      <XRControllerManager>
        {props.children}
      </XRControllerManager>
    </XRContext.Provider>
  );
};

// ──────────────────────────────────────────────────────────────────────────────
// XRControllerManager — manages both XR controllers for the session lifetime.
// Exposes onGripStart / onGripEnd registration via context so contributions
// only declare what they want to happen on each squeeze event.

export const XRControllerManager = ( props ) =>
{
  const { onViewerStart, onViewerEnd } = useXR();
  const store = useThree();

  const gripStartHandlers = [];
  const gripEndHandlers   = [];

  const onGripStart = ( fn ) => gripStartHandlers.push( fn );
  const onGripEnd   = ( fn ) => gripEndHandlers.push( fn );

  let controllers = [];

  onViewerStart( () => {
    controllers = [];
    for (let i = 0; i < 2; i++) {
      const controller = store.gl.xr.getController( i );

      const handleSqueezeStart = () => { for (const fn of gripStartHandlers) fn( controller ); };
      const handleSqueezeEnd   = () => { for (const fn of gripEndHandlers)   fn( controller ); };

      controller.addEventListener( 'squeezestart', handleSqueezeStart );
      controller.addEventListener( 'squeezeend',   handleSqueezeEnd   );
      store.scene.add( controller );
      controllers.push({ controller, handleSqueezeStart, handleSqueezeEnd });
    }
  });

  onViewerEnd( () => {
    gripStartHandlers.length = 0;
    gripEndHandlers.length   = 0;
    for (const { controller, handleSqueezeStart, handleSqueezeEnd } of controllers) {
      controller.removeEventListener( 'squeezestart', handleSqueezeStart );
      controller.removeEventListener( 'squeezeend',   handleSqueezeEnd   );
      store.scene.remove( controller );
    }
    controllers = [];
  });

  return (
    <XRControllerContext.Provider value={{ onGripStart, onGripEnd }}>
      {props.children}
    </XRControllerContext.Provider>
  );
};

// ──────────────────────────────────────────────────────────────────────────────
// XRInstructionText — contribution that displays a floating text label when a
// session starts, positioned in front of the viewer.  Hides when the user grips.

export const XRInstructionText = ( props ) =>
{
  const { onViewerStart, onViewerEnd } = useXR();
  const { onGripStart } = useXRControllers();
  const store = useThree();

  let instructionText = null;

  onViewerStart( () => {
    instructionText = createText( props.text ?? 'Grip to move the model', 0.06 );
    store.scene.add( instructionText );

    const { viewerPos, viewerQuat, forward } = xrViewerPose( store );
    instructionText.position.copy(viewerPos).addScaledVector(forward, 0.8);
    instructionText.quaternion.copy(viewerQuat);

    onGripStart( () => { instructionText.visible = false; } );
  });

  onViewerEnd( () => {
    store.scene.remove( instructionText );
    instructionText = null;
  });

  return null;
};

// ──────────────────────────────────────────────────────────────────────────────
// XRGripToMove — contribution that lets the user grip and drag the model.
// Positions the model in front of the viewer at session start and resets it on end.

export const XRGripToMove = () =>
{
  const { onViewerStart, onViewerEnd, getRootGroup } = useXR();
  const { onGripStart, onGripEnd } = useXRControllers();
  const store = useThree();

  onViewerStart( () => {
    const { viewerPos, forward } = xrViewerPose( store );
    getRootGroup().position
      .copy(viewerPos)
      .addScaledVector(forward, 0.7)
      .add( new Vector3(0, -0.2, 0) );

    onGripStart( ( controller ) => controller.attach( getRootGroup() ) );
    onGripEnd(   ( controller ) => store.scene.attach( getRootGroup() ) );
  });

  onViewerEnd( () => getRootGroup().position.copy( new Vector3(0, 0, 0) ) );

  return null;
};

// ──────────────────────────────────────────────────────────────────────────────
// StartXRButton — pre-composed default: session manager + instruction text + grip-to-move.
// Preserves the existing public API so callers (webxr.jsx) need no changes.

const StartXRButton = ( props ) => (
  <XRSessionManager trackball={props.trackball} getRootGroup={props.getRootGroup}>
    <XRInstructionText text="Grip to move the model" />
    <XRGripToMove />
  </XRSessionManager>
);

export default StartXRButton;