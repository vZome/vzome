
import { Color, Scene, WebGLRenderer, } from "three";
import { createRenderEffect, onMount, For, Show } from "solid-js";
import { createElementSize } from "@solid-primitives/resize-observer";

import { Canvas, T, useFrame } from "./util/solid-three.js";

import { TrackballControls } from "./trackballcontrols.jsx";
import { PerspectiveCamera } from "./perspectivecamera.jsx";
import { OrthographicCamera } from "./orthographiccamera.jsx";
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
      <OrthographicCamera aspect={props.aspect}>
        {props.children}
      </OrthographicCamera>
    }>
      <PerspectiveCamera aspect={props.aspect}>
        {props.children}
      </PerspectiveCamera>
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
    const renderer = new WebGLRenderer({
      powerPreference: "high-performance",
      canvas,
      antialias: true,
      alpha: true,
      preserveDrawingBuffer: true,
    });
    // renderer.xr.enabled = true;
    return renderer;
  }

  const canvas =
    <Canvas class='canvas3d' dpr={ window.devicePixelRatio } gl={makeCustomRenderer}
        style={{
          height: props.height ?? "100%",
          width: props.width ?? "100%",
          display: 'flex',
        }}
        frameloop="always" onClickMissed={handlePointerMissed} >
      <WebXRSupport>

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
    </Canvas>;
  
  size = createElementSize( canvas );

  createRenderEffect( () => {
    canvas.style.cursor = (tool ?.cursor()) || 'auto';
  });
  
  onMount( () => {
    // canvas .addEventListener( 'pointermove', handlePointerMove );
    canvas .addEventListener( 'pointerup', handlePointerUp );
    canvas .addEventListener( 'wheel', handleWheel );
  });

  return canvas;
}
