import React from 'react'
import * as THREE from 'three'

function offsetToQuaternion(offset) {
  const cylinderDefaultAxis = new THREE.Vector3(0,1,0)
  const offsetV = new THREE.Vector3().fromArray(offset).normalize()
  return new THREE.Quaternion().setFromUnitVectors(cylinderDefaultAxis,offsetV)
}

function strutCenter(position,offset) {
  const positionV = new THREE.Vector3().fromArray(position)
  const offsetV = new THREE.Vector3().fromArray(offset)
  return positionV.addScaledVector(offsetV,0.5).toArray()
}

function length(v) {
  return new THREE.Vector3().fromArray(v).length()
}

function Strut( { material, position, offset, radius=0.1 } ) {
  return (
    <mesh material={material}
      quaternion={offsetToQuaternion(offset)}
      position={strutCenter(position,offset)}>
      <cylinderBufferGeometry attach="geometry" args={[ radius, radius, length(offset),12,1]}/>
    </mesh>
  )
}

export default Strut
