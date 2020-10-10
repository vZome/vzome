import React, { useState } from 'react'
import * as THREE from 'three'
import { useResource } from 'react-three-fiber'
import Strut from './strut'

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

function BuildPlane( {config, buildFn, defocus} ) {

  const { position, orientation } = config

  function doAddPoint(e,ptXY) {
    e.stopPropagation()
    console.log(position)
    console.log(orientation)
    console.log(ptXY)
    const vector = new THREE.Vector3().fromArray(ptXY).applyQuaternion( QUATERNIONS[orientation] ).toArray()
    const newPoint = position.map( ( xi, i ) => xi + ( vector[i] ) )
    buildFn(position,newPoint)
  }
  const [materialRef, material] = useResource()
  const [lineEnd, showLine] = useState(undefined);
  const gridHover = pt => {
    showLine( pt )
    console.log( `gridHover( ${JSON.stringify(pt)} )`)
  }
  return (
    <group position={position} quaternion={QUATERNIONS[orientation]}>
      <meshBasicMaterial ref={materialRef} transparent={true} opacity={0.2} color={COLORS[orientation]} side={THREE.DoubleSide} />
      <mesh material={material} >
        <planeGeometry attach="geometry" args={[6, 6]} />
      </mesh>
      { lineEnd &&
        <Strut position={[0,0,0]} offset={[ lineEnd.x, lineEnd.y, 0 ]} radius={0.05} material={material} />
      }
      {GRID.map( ( [x,y] ) => (
        <mesh position={[x,y,0]} key={JSON.stringify( [x,y] )} material={material}
            onPointerOver={e => gridHover( {x,y} )}
            onPointerOut={e => gridHover(undefined)}
            onClick={e => doAddPoint(e,[x,y,0])}>
          <boxBufferGeometry attach="geometry" args={[0.2, 0.2, 0.1]} />
        </mesh>
      ))}
    </group>
  )
}

export default BuildPlane
