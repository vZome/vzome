
import { useFrame, Canvas } from "solid-three";
import { Color } from "three";
import { createEffect, createMemo, createRenderEffect, onMount } from "solid-js";
import { createElementSize } from "@solid-primitives/resize-observer";

import { PerspectiveCamera } from "./perspectivecamera.jsx";
import { TrackballControls } from "./trackballcontrols.jsx";
import { useInteractionTool } from "./interaction.jsx";
import { cameraFieldOfViewY, cameraPosition, useCameraState } from "./camera.jsx";

const Lighting = props =>
{
  const color = createMemo( () => new Color( props.backgroundColor ) );
  useFrame( ({scene}) => { scene.background = color() } )
  let centerObject;
  // The ambientLight has to be "invisible" so we don't get an empty node in glTF export.
  return (
    <>
      <object3D ref={centerObject} visible={false} />
      <ambientLight color={props.ambientColor} intensity={1.5} visible={false} />
      <For each={props.directionalLights}>{ ( { color, direction } ) =>
        <directionalLight target={centerObject} intensity={1.7} color={color} position={direction.map( x => -x )} />
      }</For>
    </>
  )
}

const defaultLighting = {
  // backgroundColor: '#8CC2E7',
  ambientColor: '#333333',
  directionalLights: [ // These are the vZome defaults, for consistency
    { direction: [ 1, -1, -0.3 ], color: '#FDFDFD' },
    { direction: [ -1, 0, -0.2 ], color: '#B5B5B5' },
    { direction: [ 0, 0, -1 ], color: '#303030' },
  ]
}

// Thanks to Paul Henschel for this, to fix the camera.lookAt by adjusting the Controls target
//   https://github.com/react-spring/react-three-fiber/discussions/609

const LightedCameraControls = (props) =>
{
  const { adjustFrustum, recordCamera } = useCameraState();
  // Here we can useThree, etc., which we could not in LightedTrackballCanvas

  const trackballChange = evt =>
  {
    const { object, target } = evt.target;
    adjustFrustum && adjustFrustum( object, target );
  }

  const trackballEnd = evt =>
  {
    const { object, target } = evt.target;
    recordCamera && recordCamera( object, target );
  }

  const position = createMemo( () => cameraPosition( props.sceneCamera ) );

  const fov = createMemo( () => cameraFieldOfViewY( props.sceneCamera, props.aspect ) );

  const lights = createMemo( () => {
    const backgroundColor = props.lighting?.backgroundColor || defaultLighting.backgroundColor;
    return { ...defaultLighting, backgroundColor };
  });

  const result = ( !!props.sceneCamera &&
    <>
      <PerspectiveCamera fov={fov()} aspect={props.aspect} position={position()} up={props.sceneCamera?.up}
          near={props.sceneCamera?.near} far={props.sceneCamera?.far} target={props.sceneCamera?.lookAt} >
        <Lighting {...(lights())} />
      </PerspectiveCamera>
      <TrackballControls onEnd={props.rotationOnly? undefined : trackballEnd} onChange={props.rotationOnly? undefined : trackballChange}
          rotationOnly={props.rotationOnly} rotateSpeed={props.rotateSpeed}
          staticMoving='true' zoomSpeed={3} panSpeed={1} target={props.sceneCamera?.lookAt} />
    </>
  );

  return result;
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

  const [ tool ] = useInteractionTool();

  const handlePointerMove = ( e ) =>
  {
    const handler = tool && tool() ?.onDrag;
    if ( isLeftMouseButton( e ) && handler ) {
      e.stopPropagation()
      handler();
    }
  }
  const handlePointerUp = ( e ) =>
  {
    const handler = tool && tool() ?.onDragEnd;
    if ( isLeftMouseButton( e ) && handler ) {
      // e.stopPropagation()
      handler();
    }
  }
  const handlePointerMissed = ( e ) =>
  {
    const handler = tool && tool() ?.bkgdClick;
    if ( isLeftMouseButton( e ) && handler ) {
      e.stopPropagation()
      handler();
    }
  }

  const canvas =
    <Canvas class='canvas3d' dpr={ window.devicePixelRatio } gl={{ antialias: true, alpha: false }}
        height={props.height ?? "100vh"} width={props.width ?? "100vw"}
        frameloop="always" onPointerMissed={handlePointerMissed} >
      <LightedCameraControls lighting={props.lighting} aspect={aspect()} rotationOnly={props.rotationOnly} rotateSpeed={props.rotateSpeed}
        sceneCamera={props.sceneCamera} />
      {props.children}
    </Canvas>;
  
  canvas.style.display = 'flex';
  size = createElementSize( canvas );
  createEffect( () => console.log( 'size', size.width, size.height ) );

  createRenderEffect( () => {
    canvas.style.cursor = (tool && tool().cursor) || 'auto';
  });
  
  onMount( () => {
    // canvas .addEventListener( 'pointermove', handlePointerMove );
    canvas .addEventListener( 'pointerup', handlePointerUp );
  });

  return canvas;
}
