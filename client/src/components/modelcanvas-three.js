
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

const shapes = {}

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

const Instance = ( { position, rotation, shapeId, color, onClick } ) => {
  const quaternion = rotation? [ rotation.x, rotation.y, rotation.z, rotation.w ] : [1,0,0,0];
  return (
    <group position={ position } quaternion={ quaternion }>
      <mesh geometry={shapes[ shapeId ]} onClick={onClick}>
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
  2. lighting component extract (Redux context tunneling)
  3. auto-load real logo model, internal
  3. UI overlay
  4. geometry cache
*/

// Thanks to Paul Henschel for this, to fix the camera.lookAt by adjusting the Controls target
//   https://github.com/react-spring/react-three-fiber/discussions/609

const ModelCanvas = ( { lighting, instances, camera, shown, selected, selectId, deselectId } ) => {
  const { fov, position, up, lookAt } = camera
  return(
    <>
      <Canvas gl={{ antialias: true, alpha: false }} >
        <PerspectiveCamera makeDefault {...{fov, position, up}}>
          <Lighting {...lighting} />
        </PerspectiveCamera>
        <Controls staticMoving='true' rotateSpeed={6} zoomSpeed={3} panSpeed={1} target={lookAt} />
        { instances.map( ( { id, position, color, rotation, shape } ) => 
          <Instance key={id} position={[ position.x, position.y, position.z ]} color={color} rotation={rotation} shapeId={shape} /> ) }
        { shown.map( ( { id, position, shapeId } ) =>
          <Instance key={id} position={position} color={"#0088aa"} shapeId={shapeId} onClick={(e) => {e.stopPropagation(); selectId(id)} } /> ) }
        { selected.map( ( { id, position, shapeId } ) =>
          <Instance key={id} position={position} color={"#ff4400"} shapeId={shapeId} onClick={(e) => {e.stopPropagation(); deselectId(id)} } /> ) }
      </Canvas>
    </>
  )
}

const assignShape = ( id, vector ) =>
{
  const shapeIds = Object.getOwnPropertyNames( shapes )
  const shapeId = shapeIds[ 0 ]
  return { id, shapeId, position: vector }
}

const select = ( { camera, lighting, vzomejava, mesh } ) => ({
  camera,
  lighting,
  shown: Array.from( mesh.shown ).map( ( [id, vector] ) => assignShape( id, mesh.field.embedv( vector[0] ) ) ),
  selected: Array.from( mesh.selected ).map( ( [id, vector] ) => assignShape( id, mesh.field.embedv( vector[0] ) ) ),
  shapes: updateShapes( vzomejava.shapes ), // not used as a property, just updating as a side-effect
  instances: vzomejava.renderingOn? vzomejava.instances : vzomejava.previous
})

const boundEventActions = {
  selectId : objectSelected,
  deselectId : objectDeselected
}

export default connect( select, boundEventActions )( ModelCanvas )
