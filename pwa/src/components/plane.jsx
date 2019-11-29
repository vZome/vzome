import React from 'react'
import * as THREE from 'three'

const QUATERNIONS = [
  new THREE.Quaternion(1,0,0,0),
  new THREE.Quaternion(0,1,1,0).normalize(),
  new THREE.Quaternion(1,0,1,0).normalize()
]

const COLORS = [ "#00aacc", "#cc00aa", "#aacc00" ]

function Plane( {config} ) {
  const {position,orientation} = config
  return (
    <mesh
      position={position}
      quaternion={QUATERNIONS[orientation]}
      onClick={e => console.log('click')}
      onPointerOver={e => console.log('plane hover')}
      onPointerOut={e => console.log('plane unhover')}>
      <planeGeometry attach="geometry" args={[6, 6]} />
      <meshBasicMaterial attach="material" transparent={true} opacity={0.4} color={COLORS[orientation]} side={THREE.DoubleSide} />
    </mesh>
  )
}

export default Plane
