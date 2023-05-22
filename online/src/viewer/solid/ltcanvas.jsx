
import { useFrame, useThree, Canvas } from "solid-three";
import { Color } from "three";
import { createMemo } from "solid-js";
import { createElementSize } from "@solid-primitives/resize-observer";
import { PerspectiveCamera } from "./perspectivecamera.jsx";
import { TrackballControls } from "./trackballcontrols.jsx";

const Lighting = props =>
{
  const color = createMemo( () => new Color( props.backgroundColor ) .convertLinearToSRGB() );
  useFrame( ({scene}) => { scene.background = color() } )
  // const { scene } = useThree();
  // const centerObject = () => scene.getObjectByName('Center');
  let centerObject;
  return (
    <>
      <group ref={centerObject} position={[0,0,0]} visible={false} />
      <ambientLight color={props.ambientColor} intensity={1.5} />
      <For each={props.directionalLights}>{ ( { color, direction } ) =>
        <directionalLight target={centerObject} intensity={1.7} color={color} position={direction.map( x => -x )} />
      }</For>
    </>
  )
}

const defaultLighting = {
  backgroundColor: '#88C0ED',
  ambientColor: '#555555',
  directionalLights: [ // These are the vZome defaults, for consistency
    { direction: [ 1, -1, -0.3 ], color: '#FDFDFD' },
    { direction: [ -1, 0, -0.2 ], color: '#B5B5B5' },
    { direction: [ 0, 0, -1 ], color: '#303030' },
  ]
}

const toVector = vector3 =>
{
  const { x, y, z } = vector3;
  return [ x, y, z ];
}

// Thanks to Paul Henschel for this, to fix the camera.lookAt by adjusting the Controls target
//   https://github.com/react-spring/react-three-fiber/discussions/609

const LightedCameraControls = (props) =>
{
  // Here we can useThree, etc., which we could not in LightedTrackballCanvas

  const trackballEnd = evt =>
  {
    const trackball = evt.target;
    const camera = trackball.object;
    const up = toVector( camera.up );
    const position = toVector( camera.position );
    const lookAt = toVector( trackball.target );
    const [ x, y, z ] = lookAt.map( (e,i) => e - position[ i ] );
    const distance = Math.sqrt( x*x + y*y + z*z );
    const lookDir = [ x/distance, y/distance, z/distance ];

    // This was missing, and vZome reads width to set FOV
    const fovX = camera.fov * (Math.PI/180) * camera.aspect; // Switch from Y-based FOV degrees to X-based radians
    const width = 2 * distance * Math.tan( fovX / 2 );
    // This is needed to keep the fog depth correct in desktop.
    //  See the reducer, where the width/distance ratio is maintained.
    const far = camera.far;
    const near = camera.near;

    props.syncCamera( { lookAt, up, lookDir, distance, width, far, near } );
    // setNeedsRender( 20 );
  }

  const position = createMemo( () => {
    const dist = props.sceneCamera?.distance;
    const lookDir = props.sceneCamera?.lookDir;
    const result = props.sceneCamera?.lookAt.map( (e,i) => e - props.sceneCamera?.distance * props.sceneCamera?.lookDir[ i ] );
    return result;
  } );
  const fov = createMemo( () => {
    const halfX = props.sceneCamera?.width / 2;
    const halfY = halfX / props.aspect;
    return 360 * Math.atan( halfY / props.sceneCamera?.distance ) / Math.PI;
  } );

  const lights = createMemo( () => ({
    ...defaultLighting,
    backgroundColor: (props.lighting?.backgroundColor) || defaultLighting.backgroundColor,
  }));

  const result = (
    <>
      <PerspectiveCamera fov={fov()} aspect={props.aspect} position={position()} up={props.sceneCamera?.up} >
        <Lighting {...(lights())} />
      </PerspectiveCamera>
      {props.trackball && <TrackballControls onEnd={trackballEnd} staticMoving='true' rotateSpeed={4.5} zoomSpeed={3} panSpeed={1} target={props.sceneCamera?.lookAt} />}
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

  const handlePointerMissed = ( e ) =>
  {
    if ( isLeftMouseButton( e ) && props.toolActions?.bkgdClick ) {
      e.stopPropagation()
      props.toolActions?.bkgdClick();
    }
  }

  const canvas =
    <Canvas dpr={ window.devicePixelRatio } gl={{ antialias: true, alpha: false }}
        height={props.height ?? "100vh"} width={props.width ?? "100vw"}
        frameloop="always"
        onPointerMove={props.toolActions?.onDrag} onPointerUp={props.toolActions?.onDragEnd} onPointerMissed={handlePointerMissed} >
      <LightedCameraControls lighting={props.lighting} aspect={aspect()}
        sceneCamera={props.sceneCamera} syncCamera={props.syncCamera} trackball={props.trackball} />
      {props.children}
    </Canvas>;
  
  size = createElementSize( canvas );
  
  return canvas;
}
