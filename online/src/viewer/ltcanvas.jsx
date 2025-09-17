
import { useFrame, Canvas } from "solid-three";
import { Color } from "three";
import { createMemo, createRenderEffect, mergeProps, onMount } from "solid-js";
import { createElementSize } from "@solid-primitives/resize-observer";

import { PerspectiveCamera } from "./perspectivecamera.jsx";
import { OrthographicCamera } from "./orthographiccamera.jsx";
import { TrackballControls } from "./trackballcontrols.jsx";
import { useInteractionTool } from "../viewer/context/interaction.jsx";
import { useCamera } from "../viewer/context/camera.jsx";
import { Labels } from "./labels.jsx";
import { EnhancedLabels, OrientedLabels } from "./panellabels.jsx";
import { useViewer } from "./context/viewer.jsx";

const Lighting = () =>
{
  const { state } = useCamera();
  const color = createMemo( () => new Color( state.lighting.backgroundColor ) );
  useFrame( ({scene}) => { scene.background = color() } )
  let centerObject;
  // The ambientLight has to be "invisible" so we don't get an empty node in glTF export.

  return (
    <>
      <object3D ref={centerObject} visible={false} />
      <ambientLight color={state.lighting.ambientColor} />
      <For each={state.lighting.directionalLights}>{ ( { color, direction } ) =>
        <directionalLight target={centerObject} intensity={1.7} color={color} position={direction.map( x => -x )} />
      }</For>
    </>
  )
}

const LightedCameraControls = (props) =>
{
  const { perspectiveProps, trackballProps, name, state, cancelTweens } = useCamera();
  const [ tool ] = useInteractionTool();
  const enableTrackball = () => ( tool === undefined ) || tool .allowTrackball();
  props = mergeProps( { rotateSpeed: 4.5, zoomSpeed: 3, panSpeed: 1 }, props );
  const halfWidth = () => perspectiveProps.width / 2;

  const onTrackballEnd = () => ( tool !== undefined ) && tool .onTrackballEnd();

  return (
    <>
      <Show when={state.camera.perspective} fallback={
        <OrthographicCamera aspect={props.aspect} name={name} outlines={state.outlines}
            position={perspectiveProps.position} up={perspectiveProps.up} halfWidth={halfWidth()}
            near={perspectiveProps.near} far={perspectiveProps.far} target={perspectiveProps.target} >
          <Lighting />
        </OrthographicCamera>
      }>
        <PerspectiveCamera aspect={props.aspect} name={name} outlines={state.outlines}
            position={perspectiveProps.position} up={perspectiveProps.up} fov={perspectiveProps.fov( props.aspect )}
            near={perspectiveProps.near} far={perspectiveProps.far} target={perspectiveProps.target} >
          <Lighting />
        </PerspectiveCamera>
      </Show>
      <TrackballControls enabled={enableTrackball()} rotationOnly={props.rotationOnly} name={name}
        camera={trackballProps.camera} target={perspectiveProps.target} sync={trackballProps.sync}
        trackballStart={cancelTweens} trackballEnd={onTrackballEnd}
        rotateSpeed={props.rotateSpeed} zoomSpeed={props.zoomSpeed} panSpeed={props.panSpeed} />
    </>
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
    const handler = tool ?.bkgdClick;
    if ( isLeftMouseButton( e ) && handler ) {
      e.stopPropagation();
      handler( e );
    }
  }

  const canvas =
    <Canvas class='canvas3d' dpr={ window.devicePixelRatio } gl={{ antialias: true, alpha: false, preserveDrawingBuffer: true }}
        height={props.height ?? "100vh"} width={props.width ?? "100vw"}
        frameloop="always" onPointerMissed={handlePointerMissed} >
      <LightedCameraControls aspect={aspect()}
        rotationOnly={props.rotationOnly} rotateSpeed={props.rotateSpeed} zoomSpeed={props.zoomSpeed} panSpeed={props.panSpeed} />

      {props.children}

      {labels && labels() && <>
        <OrientedLabels size={canvasSize()} />
        <EnhancedLabels size={canvasSize()} />
      </>}
    </Canvas>;
  
  canvas.style.display = 'flex';
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
