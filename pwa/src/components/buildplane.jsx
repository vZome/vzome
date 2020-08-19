import React from 'react'
import * as THREE from 'three'

const QUATERNIONS = [
  new THREE.Quaternion(1,0,0,0),
  new THREE.Quaternion(0,1,1,0).normalize(),
  new THREE.Quaternion(1,0,1,0).normalize()
]

const COLORS = [ "#00aacc", "#cc00aa", "#aacc00" ]

const GRID = []
for (const x of [-2,-1,0,1,2]) {
  for (const y of [-2,-1,0,1,2]) {
    GRID.push( [x,y] )
  }
}

function BuildPlane( {config, buildFn} ) {

  const {position,orientation} = config

  function doAddPoint(e,ptXY) {
    e.stopPropagation()
    console.log(position)
    console.log(orientation)
    console.log(ptXY)
    const vector = new THREE.Vector3().fromArray(ptXY).applyQuaternion( QUATERNIONS[orientation] ).toArray()
    const newPoint = position.map( ( xi, i ) => xi + ( vector[i] ) )
    buildFn(position,newPoint)
  }

  return (
    <group position={position} quaternion={QUATERNIONS[orientation]}>
      <mesh>
        <planeGeometry attach="geometry" args={[6, 6]} />
        <meshBasicMaterial attach="material" transparent={true} opacity={0.2} color={COLORS[orientation]} side={THREE.DoubleSide} />
      </mesh>
      {GRID.map( pt => (
        <mesh position={[pt[0],pt[1],0]} key={JSON.stringify(pt)} onClick={e => doAddPoint(e,[pt[0],pt[1],0])}>
          <boxBufferGeometry attach="geometry" args={[0.2, 0.2, 0.2]} />
          <meshBasicMaterial attach="material" transparent={true} opacity={0.4} color={COLORS[orientation]} />
        </mesh>
      ))}
    </group>
  )
}

export default BuildPlane
