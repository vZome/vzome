
import { Color, Scene, Group, Mesh, AmbientLight, DirectionalLight } from "three";
import { WebGPURenderer } from 'three/webgpu';
import { createRenderEffect, createResource, createSignal, onMount, For, Show, Suspense } from "solid-js";
import { createElementSize } from "@solid-primitives/resize-observer";

import { Canvas, createT, createXR, useFrame } from 'solid-three';
const T = createT({ Group, Mesh, AmbientLight, DirectionalLight });

import { TrackballControls } from "./trackballcontrols.jsx";
import { ControlledPerspectiveCamera } from "./perspectivecamera.jsx";
import { ControlledOrthographicCamera } from "./orthographiccamera.jsx";
import { useInteractionTool } from "./context/interaction.jsx";
import { useCamera } from "./context/camera.jsx";
import { Labels } from "./labels.jsx";
import { useViewer } from "./context/viewer.jsx";
import { WebXRSupport } from "./context/webxr.jsx";

const Lighting = () =>
{
  const { state, perspectiveProps } = useCamera();
  // Adopting changes as required by https://discourse.threejs.org/t/updates-to-color-management-in-three-js-r152/50791
  //   and https://discourse.threejs.org/t/updates-to-lighting-in-three-js-r155/53733
  const mapColor = color => new Color() .setStyle( color ) .multiplyScalar( Math.PI );
  useFrame( ({scene}) => { scene.background = new Color() .setStyle( state.lighting.backgroundColor ) } ); // NOT converting to Linear!
  let centerObject;
  // The ambientLight has to be "invisible" so we don't get an empty node in glTF export.

  return (
    <T.Group position={perspectiveProps.position} up={perspectiveProps.up} target={perspectiveProps.target}>
      <T.Mesh ref={centerObject} visible={false} />
      <T.AmbientLight color={mapColor( state.lighting.ambientColor )} />
      <For each={state.lighting.directionalLights}>{ ( { direction, color } ) =>
        <T.DirectionalLight target={centerObject} intensity={1.4}
          color={mapColor( color )} position={direction.map( x => -x )} />
      }</For>
    </T.Group>
  )
}

const ControlledCamera = (props) =>
{
  const { state } = useCamera();

  return (
    <Show when={state.camera.perspective} fallback={
      <ControlledOrthographicCamera aspect={props.aspect}>
        {props.children}
      </ControlledOrthographicCamera>
    }>
      <ControlledPerspectiveCamera aspect={props.aspect}>
        {props.children}
      </ControlledPerspectiveCamera>
    </Show>
  );
}

const isLeftMouseButton = e =>
{
  e = e || window.event;
  if ( "which" in e )  // Gecko (Firefox), WebKit (Safari/Chrome) & Opera
    return e.which === 1
  else if ( "button" in e )  // IE, Opera 
    return e.button === 0
  return false
}

export const LightedTrackballCanvas = ( props ) =>
{
  let size;
  const aspect = () => ( size && size.height )? size.width / size.height : 1;
  const canvasSize = () => size;
  const { labels } = useViewer();

  const xr = createXR();
  const [xrSupported] = createResource( () => xr.isSupported( 'immersive-ar' ) );

  // connectRef captures the canvas DOM element (via ctx.gl.domElement) and wires createXR.
  // Canvas must be rendered inside <xr.Provider> (not pre-assigned) so that useXR() in
  // Canvas children can find the provider context.
  const [canvasEl, setCanvasEl] = createSignal( null );
  size = createElementSize( canvasEl );
  const connectRef = ( ctx ) => {
    setCanvasEl( ctx.gl.domElement );
    return xr.connect( ctx );
  };

  const [ tool ] = useInteractionTool();

  const handlePointerMove = ( e ) =>
  {
    const handler = tool ?.onDrag;
    if ( isLeftMouseButton( e ) && handler ) {
      e.stopPropagation()
      handler( e );
    }
  }
  const handlePointerUp = ( e ) =>
  {
    const handler = tool ?.onDragEnd;
    if ( isLeftMouseButton( e ) && handler ) {
      // e.stopPropagation()
      handler( e );
    }
  }
  const handleWheel = ( e ) =>
  {
    const handler = tool ?.onWheel;
    if ( handler ) {
      e.preventDefault();
      handler( e.deltaY );
    }
  }
  const handlePointerMissed = ( e ) =>
  {
    console.log("canvas onClickMissed");
    const handler = tool ?.bkgdClick;

    // NOTE: this is a solid-three handler, so the event is wrapped in a synthetic event,
    //  and the native event is in e.nativeEvent.  We have to check the button on the native event,
    //  but we can stop propagation on the synthetic event.
    if ( isLeftMouseButton( e.nativeEvent ) && handler ) {
      // e.stopPropagation();  // NOT available!  Is this a problem?
      handler( e );
    }
  }

  const makeCustomRenderer = ( canvas ) =>
  {
    const renderer = new WebGPURenderer({
      powerPreference: "high-performance",
      canvas,
      antialias: true,
      alpha: true,
      preserveDrawingBuffer: true,
      forceWebGL: true,
    });
    // renderer.xr.enabled = true;
    return renderer;
  }

  createRenderEffect( () => {
    const el = canvasEl();
    if ( el ) el.style.cursor = (tool ?.cursor()) || 'auto';
  });

  onMount( () => {
    const el = canvasEl();
    if ( el ) {
      // el.addEventListener( 'pointermove', handlePointerMove );
      el.addEventListener( 'pointerup', handlePointerUp );
      el.addEventListener( 'wheel', handleWheel );
    }
  });

  return (
    <xr.Provider>
      <div style={{ position: 'relative', height: props.height ?? "100%", width: props.width ?? "100%" }}>
        <Canvas ref={connectRef} class='canvas3d' dpr={ window.devicePixelRatio } gl={makeCustomRenderer}
            style={{
              height: "100%",
              width: "100%",
              display: 'flex',
            }}
            frameloop="always" onClickMissed={handlePointerMissed} >
          <WebXRSupport xrSupported={xrSupported}>

            { /* This should add the camera to the scene so that the lights move with the camera,
                  but it apparently does not. */ }
            <ControlledCamera aspect={aspect()} >
              <Lighting />
            </ControlledCamera>

            <TrackballControls rotationOnly={props.rotationOnly}
              rotateSpeed={props.rotateSpeed} zoomSpeed={props.zoomSpeed} panSpeed={props.panSpeed} />

            {props.children}

            {labels && labels() && <Labels size={canvasSize()} />}

          </WebXRSupport>
        </Canvas>
        <Suspense>
          <Show when={xrSupported()}>
            <button
              style={{
                position: 'absolute', bottom: '20px', left: '50%', transform: 'translateX(-50%)',
                padding: '12px 24px', 'border-radius': '4px', border: 'none',
                background: 'rgba(0,0,0,0.7)', color: 'white', cursor: 'pointer',
                'font-size': '13px', 'font-family': 'sans-serif',
              }}
              onClick={() => xr.enter( 'immersive-ar', { optionalFeatures: ['local-floor', 'hand-tracking'] } )}
            >
              START AR
            </button>
          </Show>
        </Suspense>
      </div>
    </xr.Provider>
  );
}
