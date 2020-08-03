
import React, { useRef } from 'react'
import { connect } from 'react-redux'
import { Canvas, useThree, extend, useFrame } from 'react-three-fiber'
import * as THREE from 'three'
import { PerspectiveCamera } from 'drei'
import { TrackballControls } from 'three/examples/jsm/controls/TrackballControls'
import styled, { css } from 'styled-components'
import { actionTriggered } from '../bundles/vzomejava'

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

const Shape = ( { vertices, faces } ) => (
  <geometry attach="geometry"
    vertices={ vertices.map( v => new THREE.Vector3( v.x, v.y, v.z ) ) }
    faces={ faces.map( f => new THREE.Face3( f.vertices[0], f.vertices[1], f.vertices[2], computeNormal( vertices, f ) ) ) }
    onUpdate={ self => (self.verticesNeedUpdate = true) }
  />
)

const Instance = ( { position, rotation, shape, color } ) => {
  const quaternion = rotation? [ rotation.x, rotation.y, rotation.z, rotation.w ] : [1,0,0,0];
  return (
    <group position={ [ position.x, position.y, position.z ] } quaternion={ quaternion }>
      <mesh>
        <Shape vertices={shape.vertices} faces={shape.faces} />
        <meshLambertMaterial attach="material" color={color} />
      </mesh>
    </group>
  )
}

const Scene = ( { background, ambientColor, directionalLights } ) => {
  useFrame( ({scene}) => { scene.background = new THREE.Color( background ) } )
  return (
    <>
      <ambientLight color={ambientColor} />
      { directionalLights.map( ( { direction, color } ) => 
          <directionalLight position={direction} color={color} lookAt={[0,0,0]} /> ) }
    </>
  )
}

const ModelCanvas = ( { background, ambientColor, directionalLights, instances, shapes, fov, position, up, lookAt, doAction } ) => {
  return(
  <>
    <Canvas
        gl={{ antialias: true, alpha: false }}
        camera={{ position, fov, up }}
        onCreated={({ camera }) => camera.lookAt( lookAt )} >
      <PerspectiveCamera/>
      <Scene background={background} ambientColor={ambientColor} directionalLights={directionalLights} />
      <Controls staticMoving='true' rotateSpeed={6} zoomSpeed={3} panSpeed={1} />
      { instances.map( ( { id, position, color, rotation, shape } ) => 
          <Instance key={id} position={position} color={color} rotation={rotation} shape={shapes[shape]} /> ) }
    </Canvas>
    {/* <UpperLeft onClick={ ()=>doAction("export.dae") }>
      export DAE
    </UpperLeft> */}
  </>
  )
}

const base = css`
  font-family: 'Teko', sans-serif;
  position: absolute;
  text-transform: uppercase;
  font-weight: 900;
  font-variant-numeric: slashed-zero tabular-nums;
  line-height: 1em;
  pointer-events: none;
  color: blue;
`

const UpperLeft = styled.div`
  ${base}
  top: 40px;
  left: 50px;
  font-size: 2em;
  pointer-events: all;
  cursor: pointer;
  @media only screen and (max-width: 900px) {
    font-size: 1.5em;
  }
`

const boundEventActions = {
  doAction : actionTriggered
}

const select = (state) => ({
  fov: state.camera.fov,
  position: state.camera.position,
  lookAt: state.camera.lookAt,
  up: state.camera.up,
  background: state.lighting.backgroundColor,
  ambientColor: state.lighting.ambientColor,
  directionalLights: state.lighting.directionalLights,
  shapes: state.vzomejava.shapes.reduce( (result, item) => { result[ item.id ] = item; return result }, {} ),
  instances: state.vzomejava.renderingOn? state.vzomejava.instances : []
})

export default connect( select, boundEventActions )( ModelCanvas )
