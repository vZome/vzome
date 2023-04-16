
import { Canvas } from "solid-three";
import { Color } from "three";
import { createEffect, createMemo, createSignal } from "solid-js";
import { useFrame, useThree } from 'solid-three';

const Lighting = props =>
{
  const color = createMemo( () => new Color( props.backgroundColor ) .convertLinearToSRGB() );
  useFrame( ({scene}) => { scene.background = color } )
  const { scene } = useThree();
  const centerObject = () => scene.getObjectByName('Center');
  return (
    <>
      <group name="Center" position={[0,0,0]} visible={false} />
      <ambientLight color={props.ambientColor} intensity={1.5} />
      { props.directionalLights.map( ( { color, direction } ) =>
        <directionalLight key={JSON.stringify(direction)} target={centerObject()} intensity={1.7} color={color} position={direction.map( x => -x )} /> ) }
    </>
  )
}

const defaultLighting = {
  backgroundColor: '#BBDAED',
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

const LightedCameraControls = ({ sceneCamera, syncCamera, trackball }) =>
{
  // Here we can useThree, etc., which we could not in LightedTrackballCanvas

  const [ needsRender, setNeedsRender ] = useState( 20 );
  const trackballChange = evt => {
    setNeedsRender( 20 );
  };
  useFrame( ({ gl, scene, camera }) => {
    if ( needsRender > 0 ) {
      gl.render( scene, camera );
      setNeedsRender( needsRender-1 );
    }
  }, 1 );

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

    syncCamera( { lookAt, up, lookDir, distance, width, far, near } );
    setNeedsRender( 20 );
  }

  const { near, far, width, distance, up, lookAt, lookDir, perspective } = sceneCamera;
  const halfX = width / 2;
  const halfY = halfX / props.aspect;
  const position = useMemo( () => lookAt.map( (e,i) => e - distance * lookDir[ i ] ), [ lookAt, lookDir, distance ] );
  const fov = useMemo( () =>
    360 * Math.atan( halfY / distance ) / Math.PI, [ halfY, distance ] );

  const lights = useMemo( () => ({
    ...defaultLighting,
    backgroundColor: (props.lighting?.backgroundColor) || defaultLighting.backgroundColor,
  }));

  return (
    <>
      <PerspectiveCamera makeDefault { ...{ fov, position, up } } >
        <Lighting {...(lights)} />
      </PerspectiveCamera>
      {trackball && <TrackballControls onChange={trackballChange} onEnd={trackballEnd} staticMoving='true' rotateSpeed={4.5} zoomSpeed={3} panSpeed={1} target={lookAt} />}
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
  let measured;
  // const [ measured, bounds ] = useMeasure();
  // const aspect = ( bounds && bounds.height )? bounds.width / bounds.height : 1;

  const handlePointerMissed = ( e ) =>
  {
    if ( isLeftMouseButton( e ) && props.toolActions?.bkgdClick ) {
      e.stopPropagation()
      props.toolActions?.bkgdClick();
    }
  }

  return (
    <Canvas ref={measured} dpr={ window.devicePixelRatio } gl={{ antialias: true, alpha: false }}
      height={props.height ?? "100vh"} width={props.width ?? "100vw"}
      camera={{
        position: [0, 0, 144],
        fov: 20
      }}
      onPointerMove={props.toolActions?.onDrag} onPointerUp={props.toolActions?.onDragEnd} onPointerMissed={handlePointerMissed} >

      {/* <LightedCameraControls {...{ lighting, aspect, sceneCamera, syncCamera, trackball }} /> */}

      {props.children}
      <ambientLight intensity={0.2} />
      <spotLight position={[100, 100, 100]} intensity={0.6} />
    </Canvas> )
}
