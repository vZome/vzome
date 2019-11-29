import React from 'react'
import * as THREE from 'three'

function Plane() {
  return (
    <mesh
      position={[0,0,0]}
      onClick={e => console.log('click')}
      onPointerOver={e => console.log('plane hover')}
      onPointerOut={e => console.log('plane unhover')}>
      <planeGeometry attach="geometry" args={[6, 6]} />
      <meshBasicMaterial attach="material" transparent={true} opacity={0.4} color={"#00aacc"} side={THREE.DoubleSide} />
    </mesh>
  )
}

export default Plane
