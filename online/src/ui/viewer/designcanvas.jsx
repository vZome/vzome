
import React, { useMemo, useState } from 'react'
import { Canvas, useThree, useFrame } from '@react-three/fiber'
import { VRCanvas, DefaultXRControllers, useXR, RayGrab } from '@react-three/xr'
import * as THREE from 'three'
import { PerspectiveCamera, OrthographicCamera, Sky, TrackballControls } from '@react-three/drei'
import useMeasure from 'react-use-measure';
import { useVR } from './hooks'

const Floor = () => (
  <mesh rotation={[-Math.PI / 2, 0, 0]}>
    <planeBufferGeometry attach="geometry" args={[40, 40]} />
    <meshStandardMaterial attach="material" color="#999" />
  </mesh>
)

const Lighting = ( { backgroundColor, ambientColor, directionalLights } ) =>
{
  const color = useMemo(() => new THREE.Color( backgroundColor ) .convertLinearToSRGB(), [backgroundColor]);
  useFrame( ({scene}) => { scene.background = color } )
  const { scene } = useThree();
  const centerObject = scene.getObjectByName('Center');
  return (
    <>
      <group name="Center" position={[0,0,0]} visible={false} />
      <ambientLight color={ambientColor} intensity={1.5} />
      { directionalLights.map( ( { color, direction } ) =>
        <directionalLight key={JSON.stringify(direction)} target={centerObject} intensity={1.7} color={color} position={direction.map( x => -x )} /> ) }
    </>
  )
}

const VREffects = ({ children }) =>
{
  const { controllers, isPresenting, player } = useXR();
  return (
    <>
      { isPresenting && <Sky sunPosition={[0, 1, 0]} />}
      { isPresenting && <Floor /> }
      <RayGrab>
        <group scale={isPresenting? 0.025 : 1.0} position={isPresenting? [0,0,0] : [ 0, 1, -1 ]} >
          {children}
        </group>
      </RayGrab>
    </>
  );
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

const LightedCameraControls = ({ forVR, lighting, aspect, sceneCamera, syncCamera, trackball }) =>
{
  // Here we can useThree, etc., which we could not in DesignCanvas

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
    const { target } = evt;
    const camera = target.object;
    const up = toVector( camera.up );
    const position = toVector( camera.position );
    const lookAt = toVector( target.target );
    const offset = lookAt.map( (e,i) => e - position[ i ] );
    const distance = Math.sqrt( offset.reduce( (sum,e) => sum + (e*e) ), 0 );
    const lookDir = offset.map( (e) => e / distance );
    syncCamera( { lookAt, up, lookDir, distance } );
    setNeedsRender( 20 );
  }

  const { near, far, width, distance, up, lookAt, lookDir, perspective } = sceneCamera;
  const halfX = width / 2;
  const halfY = halfX / aspect;
  const position = useMemo( () => lookAt.map( (e,i) => e - distance * lookDir[ i ] ), [ lookAt, lookDir, distance ] );
  const fov = useMemo( () => 360 * Math.atan( halfY / distance ) / Math.PI, [ halfX, aspect, distance ] );

  const lights = useMemo( () => ({
    ...defaultLighting,
    backgroundColor: (lighting && lighting.backgroundColor) || defaultLighting.backgroundColor,
  }));

  return (
    <>
      <PerspectiveCamera makeDefault { ...{ fov, position, up } } >
        <Lighting {...(lights)} />
      </PerspectiveCamera>
      {trackball && <TrackballControls onChange={trackballChange} onEnd={trackballEnd} staticMoving='true' rotateSpeed={4.5} zoomSpeed={3} panSpeed={1} target={lookAt} />}
    </>
  );
  // return forVR? (
  //   <>
  //     <Lighting {...(lights)} />
  //     <PerspectiveCamera makeDefault manual { ...{ fov, position, up, near, far } } />
  //     <TrackballControls staticMoving='true' rotateSpeed={6} zoomSpeed={3} panSpeed={1} target={lookAt} />
  //   </>
  // )
  // : (
  //   <>
  //     { perspective?
  //       <PerspectiveCamera makeDefault { ...{ fov, position, up } } >
  //         <Lighting {...(lights)} />
  //       </PerspectiveCamera>
  //     :
  //       <OrthographicCamera makeDefault { ...{ position, up, near, far, left: -halfX, right: halfX, top: halfY, bottom: -halfY } } >
  //         <Lighting {...(lights)} />
  //       </OrthographicCamera>
  //     }
  //     <TrackballControls onChange={trackballChange} onEnd={trackballEnd} staticMoving='true' rotateSpeed={4.5} zoomSpeed={3} panSpeed={1} target={lookAt}
  //       // The interpretation of min/maxDistance here is just a mystery, when OrthographicCamera is in use 
  //       {...( !perspective && { minDistance: 0.3, maxDistance: 1.5} )}
  //     />
  //   </>
  // );
}

export const DesignCanvas = ( { lighting, children, sceneCamera, syncCamera, handleBackgroundClick=()=>{}, trackball=true } ) =>
{
  const [ measured, bounds ] = useMeasure();
  const aspect = ( bounds && bounds.height )? bounds.width / bounds.height : 1;

  const vrAvailable = useVR();
  if ( vrAvailable ) {
    return (
      <VRCanvas dpr={ window.devicePixelRatio } gl={{ antialias: true, alpha: false }} >
        <DefaultXRControllers/>
        <LightedCameraControls forVR {...{ lighting, aspect, sceneCamera, syncCamera }} />
        <VREffects>
          {children}
        </VREffects>
      </VRCanvas> )
  } else {
    return (
      <Canvas ref={measured} dpr={ window.devicePixelRatio } gl={{ antialias: true, alpha: false }} onPointerMissed={handleBackgroundClick} >
        <LightedCameraControls {...{ lighting, aspect, sceneCamera, syncCamera, trackball }} />
        {children}
      </Canvas> )
  }
}
