
import React, { useRef, useMemo } from 'react'
import { connect } from 'react-redux'
import { Canvas, useThree, extend, useFrame } from 'react-three-fiber'
import * as THREE from 'three'
import { PerspectiveCamera } from 'drei'
import { TrackballControls } from 'three/examples/jsm/controls/TrackballControls'
import { objectSelected, objectDeselected } from '../bundles/mesh'

extend({ TrackballControls })
const Controls = props => {
  const { gl, camera } = useThree()
  const controls = useRef()
  useFrame(() => controls.current.update())
  return <trackballControls ref={controls} args={[camera, gl.domElement]} {...props} />
}

const computeNormal = ( vertices, face ) => {
  const v0 = vertices[ face.vertices[0] ]
  const v1 = vertices[ face.vertices[1] ]
  const v2 = vertices[ face.vertices[2] ]
  const e1 = new THREE.Vector3().subVectors( v1, v0 )
  const e2 = new THREE.Vector3().subVectors( v2, v0 )
  return new THREE.Vector3().crossVectors( e1, e2 )
}

const shapes = { unknown: new THREE.DodecahedronBufferGeometry() }

const createGeometry = ( { vertices, faces } ) =>
{
  var geometry = new THREE.Geometry();
  geometry.vertices = vertices.map( v => new THREE.Vector3( v.x, v.y, v.z ) )
  geometry.faces = faces.map( f => new THREE.Face3( f.vertices[0], f.vertices[1], f.vertices[2], computeNormal( vertices, f ) ) )
  geometry.computeBoundingSphere();
  return geometry
}

const updateShapes = ( stateShapes ) =>
{
  if ( ! stateShapes || Object.getOwnPropertyNames( stateShapes ).length === 0 )
    return shapes
  for ( let shape of stateShapes )
  {
    const key = shape.id
    // doing our own bit of reconciliation, here
    if ( ! shapes[ key ] )
      // must have a new shape
      shapes[ key ] = createGeometry( shape )
  }
  return shapes
}

const Instance = ( { position, rotation, shapeId, color, clickable, selected, id, selectId, deselectId } ) => {
  const quaternion = rotation? [ rotation.x, rotation.y, rotation.z, rotation.w ] : [1,0,0,0];
  const handleClick = ( e ) =>
  {
    if ( clickable ) {
      e.stopPropagation()
      if ( selected )
        deselectId( id )
      else
        selectId( id )
    }
  }
  return (
    <group position={ position } quaternion={ quaternion }>
      <mesh geometry={shapes[ shapeId ]} onClick={handleClick}>
        <meshLambertMaterial attach="material" color={color} />
      </mesh>
    </group>
  )
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

/*
TODO:
  - lighting component extract (Redux context tunneling)
*/

// Thanks to Paul Henschel for this, to fix the camera.lookAt by adjusting the Controls target
//   https://github.com/react-spring/react-three-fiber/discussions/609

const ModelCanvas = ( { lighting, instances, camera, selectId, deselectId } ) => {
  const { fov, position, up, lookAt } = camera
  return(
    <>
      <Canvas gl={{ antialias: true, alpha: false }} >
        <PerspectiveCamera makeDefault {...{fov, position, up}}>
          <Lighting {...lighting} />
        </PerspectiveCamera>
        <Controls staticMoving='true' rotateSpeed={6} zoomSpeed={3} panSpeed={1} target={lookAt} />
        { instances.map( instance => 
          <Instance key={instance.id} {...instance} {...{ selectId, deselectId }} /> ) }
      </Canvas>
    </>
  )
}

const select = ( state ) =>
{
  const { camera, lighting, implementations } = state
  const { shapes, instances } = implementations.instanceSelector( state )
  updateShapes( shapes )
  return {
    camera,
    lighting,
    instances,
  }
}

const boundEventActions = {
  selectId : objectSelected,
  deselectId : objectDeselected
}

export default connect( select, boundEventActions )( ModelCanvas )
