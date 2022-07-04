
import React, { useRef, useMemo } from 'react'
import { Canvas, useThree, extend, useFrame } from '@react-three/fiber'
import { VRCanvas, DefaultXRControllers } from '@react-three/xr'
import * as THREE from 'three'
import { PerspectiveCamera } from '@react-three/drei'
import { TrackballControls } from 'three-stdlib/controls/TrackballControls'
import useMeasure from 'react-use-measure';

extend({ TrackballControls })
const Controls = props => {
  const { gl, camera } = useThree()
  const controls = useRef()
  useFrame(() => controls.current.update())
  return <trackballControls ref={controls} args={[camera, gl.domElement]} {...props} />
}

// // Found this trick for getting the DL.target into the scene here:
// //   https://spectrum.chat/react-three-fiber/general/how-to-set-spotlight-target~823340ea-433e-426a-a0dc-b9a333fc3f94
// const DirLight = ( { direction, color } ) =>
// {
//   const position = direction.map( x => -x )
//   const light = useMemo(() => new THREE.DirectionalLight(), [])
//   return (
//     <>
//       <primitive object={light} position={position} color={color} />
//       <primitive object={light.target} position={[0,0,0]}  />
//     </>
//   )
// }

const Lighting = ( { backgroundColor, ambientColor, directionalLights } ) => {
  const color = useMemo(() => new THREE.Color( backgroundColor ), [backgroundColor])
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
}

// Thanks to Paul Henschel for this, to fix the camera.lookAt by adjusting the Controls target
//   https://github.com/react-spring/react-three-fiber/discussions/609

export const DesignCanvas = ( { lighting, camera, children, handleBackgroundClick=()=>{} } ) =>
{
  const [ ref, bounds ] = useMeasure();
  const { fov, position, up, lookAt } = camera || defaultInitialCamera
  const fovY = useMemo( () => ( fov * bounds.height / bounds.width ) * 180 / Math.PI, [ fov, bounds ] );
  const lights = useMemo( () => ({
    ...defaultLighting,
    backgroundColor: (lighting && lighting.backgroundColor) || defaultLighting.backgroundColor,
  }));
  return(
    <VRCanvas ref={ref} dpr={ window.devicePixelRatio } gl={{ antialias: true, alpha: false }} onPointerMissed={handleBackgroundClick} >
      <DefaultXRControllers/>
      <PerspectiveCamera makeDefault manual { ...{ fov: fovY, position, up } }>
        <Lighting {...(lights)} />
      </PerspectiveCamera>
      <Controls staticMoving='true' rotateSpeed={6} zoomSpeed={3} panSpeed={1} target={lookAt} />
      {children}
    </VRCanvas>
  )
}
