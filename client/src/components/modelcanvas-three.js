
import React, { useRef } from 'react';
import { connect } from 'react-redux'
import { Canvas, useThree, extend, useFrame } from 'react-three-fiber';
import * as THREE from 'three';
import { TrackballControls } from 'three/examples/jsm/controls/TrackballControls'

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

const Scene = ( { background } ) => {
  useFrame( ({scene}) => { scene.background = new THREE.Color( background ) } )
  console.log( background )
  return (
    <>
      <ambientLight intensity={0.4} />
      <directionalLight color={0xffffff} intensity={0.5} position={[0,0,20]} lookAt={[0,0,0]} />
    </>
  )
}

const ModelCanvas = ( { background, instances, shapes } ) => {
  return(
    <Canvas
        gl={{ antialias: true, alpha: false }}
        camera={{ position: [0, 0, 50], fov: 45 }}>
      <Scene background={background} />
      <Controls staticMoving='true' rotateSpeed={6} zoomSpeed={3} panSpeed={1} />
      { instances.map( ( { id, position, color, rotation, shape } ) => 
          <Instance key={id} position={position} color={color} rotation={rotation} shape={shapes[shape]} /> ) }
    </Canvas>
  )
}

const select = (state) => ({
  background: state.vzomejava.background,
  shapes: state.vzomejava.shapes.reduce( (result, item) => { result[ item.id ] = item; return result }, {} ),
  instances: state.vzomejava.renderingOn? state.vzomejava.instances : []
})

export default connect(select)(ModelCanvas)
