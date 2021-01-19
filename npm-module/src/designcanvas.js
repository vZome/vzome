
import React, { useRef, useMemo } from 'react'
import { Canvas, useThree, extend, useFrame } from 'react-three-fiber'
import * as THREE from 'three'
import { PerspectiveCamera } from 'drei'
import { TrackballControls } from 'three/examples/jsm/controls/TrackballControls'
import BuildPlane from './buildplane'
import Geometry from './geometry'

extend({ TrackballControls })
const Controls = props => {
  const { gl, camera } = useThree()
  const controls = useRef()
  useFrame(() => controls.current.update())
  return <trackballControls ref={controls} args={[camera, gl.domElement]} {...props} />
}

// Found this trick for getting the DL.target into the scene here:
//   https://spectrum.chat/react-three-fiber/general/how-to-set-spotlight-target~823340ea-433e-426a-a0dc-b9a333fc3f94
const DirLight = ( { direction, color } ) =>
{
  const position = direction.map( x => -x )
  const light = useMemo(() => new THREE.DirectionalLight(), [])
  return (
    <>
      <primitive object={light} position={position} color={color} />
      <primitive object={light.target} position={[0,0,0]}  />
    </>
  )
}

const Lighting = ( { backgroundColor, ambientColor, directionalLights } ) => {
  const color = useMemo(() => new THREE.Color( backgroundColor ), [backgroundColor])
  useFrame( ({scene}) => { scene.background = color } )
  return (
    <>
      <ambientLight color={ambientColor} />
      { directionalLights.map( ( config ) => <DirLight {...config} /> ) }
    </>
  )
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

/*
TODO:
  - lighting component extract (Redux context tunneling)
*/

// Thanks to Paul Henschel for this, to fix the camera.lookAt by adjusting the Controls target
//   https://github.com/react-spring/react-three-fiber/discussions/609

const DesignCanvas = ( { lighting, camera, mesh, resolver, preResolved, clickable, selectionToggler,
                        startGridHover, stopGridHover, startBallHover, stopBallHover, shapeClick, bkgdClick, workingPlane } ) =>
{
  const { fov, position, up, lookAt } = camera
  const focus = workingPlane && workingPlane.enabled && workingPlane.buildingStruts && workingPlane.position
  const atFocus = id => focus && ( id === JSON.stringify(focus) )
  const handleClick = clickable && (( id, vectors, selected ) =>
  {
    if ( workingPlane ) {
      if ( vectors.length === 1 )
        shapeClick( focus, vectors[ 0 ] )
    }
    else {
      selectionToggler( id, selected )
    }
  })
  const handleBackgroundClick = ( e ) =>
  {
    workingPlane && isLeftMouseButton( e ) && bkgdClick()
  }
  const onHover = ( vectors, inbound ) =>
  {
    if ( workingPlane && vectors.length === 1 ) {
      const position = vectors[ 0 ]
      inbound? startBallHover( position ) : stopBallHover( position )
    }
  }
  return(
      <Canvas gl={{ antialias: true, alpha: false }} onPointerMissed={handleBackgroundClick} >
        <PerspectiveCamera makeDefault {...{fov, position, up}}>
          <Lighting {...lighting} />
        </PerspectiveCamera>
        <Controls staticMoving='true' rotateSpeed={6} zoomSpeed={3} panSpeed={1} target={lookAt} />
        <Geometry {...{ mesh, resolver, preResolved, handleClick, onHover, highlightBall: atFocus }} />
        {workingPlane && workingPlane.enabled &&
          <BuildPlane config={workingPlane} { ...{ startGridHover, stopGridHover } } />}
      </Canvas>
  )
}

export default DesignCanvas
