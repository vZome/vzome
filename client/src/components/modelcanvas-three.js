
import React, { useRef, useMemo } from 'react'
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

const ModelCanvas = ( { lighting, instances, shapes, camera, doAction } ) => {
  const { fov, position, up, lookAt } = camera
  return(
    <>
      <Canvas gl={{ antialias: true, alpha: false }} >
        <PerspectiveCamera makeDefault {...{fov, position, up}}>
          <Lighting {...lighting} />
        </PerspectiveCamera>
        <Controls staticMoving='true' rotateSpeed={6} zoomSpeed={3} panSpeed={1} target={lookAt} />
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

const select = ( { camera, lighting, vzomejava } ) => ({
  camera,
  lighting,
  shapes: vzomejava.shapes.reduce( (result, item) => { result[ item.id ] = item; return result }, {} ),
  instances: vzomejava.renderingOn? vzomejava.instances : vzomejava.previous
})

export default connect( select, boundEventActions )( ModelCanvas )
