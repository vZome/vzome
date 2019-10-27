
import React, { useRef } from 'react';
import { connect } from 'react-redux'
import { Canvas, useThree, useRender, extend } from 'react-three-fiber';
import * as THREE from 'three';
import { TrackballControls } from 'three/examples/jsm/controls/TrackballControls'

extend({ TrackballControls })
const Controls = props => {
  const { gl, camera } = useThree()
  const ref = useRef()
  useRender(() => ref.current.update())
  return <trackballControls ref={ref} args={[camera, gl.domElement]} {...props} />
}

const Shape = ( { vertices, faces } ) => (
  <geometry attach="geometry"
    vertices={ vertices.map( v => new THREE.Vector3( v.x, v.y, v.z ) ) }
    faces={ faces.map( f => new THREE.Face3( f.vertices[0], f.vertices[1], f.vertices[2], new THREE.Vector3( f.normal.x, f.normal.y, f.normal.z ) ) ) }
    onUpdate={self => (self.verticesNeedUpdate = true)}
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

const ModelCanvas = ( { instances, shapes } ) => {
  return(
    <Canvas
        gl={{ antialias: true, alpha: false }}
        camera={{ position: [0, 0, 50], fov: 45 }}
        onCreated={({ scene }) => { scene.background = new THREE.Color('#99ccff') }}>
      <ambientLight intensity={0.4} />
      <directionalLight color={0xffffff} intensity={0.5} position={[0,0,20]} lookAt={[0,0,0]} />
      <Controls staticMoving='true' rotateSpeed={6} zoomSpeed={3} panSpeed={1} />
      { instances.map( ( { id, position, color, rotation, shape } ) => 
          <Instance key={id} position={position} color={color} rotation={rotation} shape={shapes[shape]} /> ) }
    </Canvas>
  )
}

const select = (state) => ({
  shapes: state.shapes.reduce( (result, item) => { result[ item.id ] = item; return result }, {} ),
  instances: state.instances
})

export default connect(select)(ModelCanvas)
