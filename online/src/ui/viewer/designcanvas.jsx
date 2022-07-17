
import React, { useMemo, useState, useEffect } from 'react'
import { Canvas, useThree, useFrame } from '@react-three/fiber'
import { VRCanvas, DefaultXRControllers, useXR, RayGrab } from '@react-three/xr'
import * as THREE from 'three'
import { PerspectiveCamera, OrthographicCamera, Sky, TrackballControls } from '@react-three/drei'
import useMeasure from 'react-use-measure';

const Floor = () => (
  <mesh rotation={[-Math.PI / 2, 0, 0]}>
    <planeBufferGeometry attach="geometry" args={[40, 40]} />
    <meshStandardMaterial attach="material" color="#999" />
  </mesh>
)

const Lighting = ( { backgroundColor, ambientColor, directionalLights } ) => {
  const color = useMemo(() => new THREE.Color( backgroundColor ) .convertLinearToSRGB(), [backgroundColor]);
  useFrame( ({scene}) => { scene.background = color } )
  const {scene} = useThree();
  const centerObject = scene.getObjectByName('Center');
  return (
    <>
      <group name="Center" position={[0,0,0]} visible={false} />
      <ambientLight color={ambientColor} intensity={1.0} />
      { directionalLights.map( ( { color, direction } ) =>
        <directionalLight key={JSON.stringify(direction)} target={centerObject} intensity={1.0} color={color} position={direction.map( x => -x )} /> ) }
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

export const defaultInitialCamera = {
  fov: 0.75, // 0.44 in vZome
  position: [ 0, 0, 75 ],
  lookAt: [ 0, 0, 0 ],
  up: [ 0, 1, 0 ],
  far: 217.46,
  near: 0.271,
  perspective: true,
}


export const useVR = () =>
{
  const [ vrAvailable, setVrAvailable ] = useState( false );
  useEffect( () => {
    const xr = window.navigator.xr;
    xr && xr.isSessionSupported( "immersive-vr" ) .then( available => setVrAvailable( available ) );
  }, []);
  return vrAvailable;
}

// Thanks to Paul Henschel for this, to fix the camera.lookAt by adjusting the Controls target
//   https://github.com/react-spring/react-three-fiber/discussions/609

export const DesignCanvas = ( { lighting, camera, children, handleBackgroundClick=()=>{} } ) =>
{
  const [ ref, bounds ] = useMeasure();
  const { fov, position, up, lookAt, perspective } = camera || defaultInitialCamera;
  const { width, height } = bounds;
  const aspectRatio = width? height / width : 1;
  const fovY = useMemo( () => ( fov * aspectRatio ) * 180 / Math.PI, [ fov, aspectRatio ] );
  const lights = useMemo( () => ({
    ...defaultLighting,
    backgroundColor: (lighting && lighting.backgroundColor) || defaultLighting.backgroundColor,
  }));
  const vrAvailable = useVR();
  if ( vrAvailable ) {
    return (
      <VRCanvas dpr={ window.devicePixelRatio } gl={{ antialias: true, alpha: false }} >
        <DefaultXRControllers/>
        <Lighting {...(lights)} />
        <PerspectiveCamera makeDefault manual { ...{ fov: fovY, position, up } } />
        <TrackballControls staticMoving='true' rotateSpeed={6} zoomSpeed={3} panSpeed={1} target={lookAt} />
        <VREffects>
          {children}
        </VREffects>
      </VRCanvas> )
  } else {
    return (
      <Canvas ref={ref} dpr={ window.devicePixelRatio } gl={{ antialias: true, alpha: false }} onPointerMissed={handleBackgroundClick} >
        { perspective?
          <PerspectiveCamera makeDefault { ...{ fov: fovY, position, up } } >
            <Lighting {...(lights)} />
          </PerspectiveCamera>
        :
          <OrthographicCamera makeDefault { ...{ fov: fovY, position, up, zoom: 1 } } >
            <Lighting {...(lights)} />
          </OrthographicCamera>
        }
        <TrackballControls staticMoving='true' rotateSpeed={4.5} zoomSpeed={3} panSpeed={1} target={lookAt}
          // The interpretation of min/maxDistance here is just a mystery, when OrthographicCamera is in use 
          {...( !perspective && { minDistance: 0.3, maxDistance: 1.5} )}
        />
        {children}
      </Canvas> )
  }
}
