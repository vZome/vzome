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

function BuildPlane( { config, buildNodeOrEdge } ) {

  const { position, orientation, buildingStruts } = config

  function handleClick( e, ptXY ) {
    e.stopPropagation()
    const vector = new THREE.Vector3().fromArray(ptXY).applyQuaternion( QUATERNIONS[orientation] ).toArray()
    const newPoint = position.map( ( xi, i ) => xi + ( vector[i] ) )
    buildNodeOrEdge( buildingStruts? position : undefined, newPoint )
  }
  const [ materialRef, material ] = useResource()
  const [ lineEnd, showLine ] = useState( undefined );
  const handleHover = pt => {
    const different = pt && ( pt.x !== 0 || pt.y !== 0 )
    showLine( different? pt : undefined )
  }
  return (
    <group position={position} quaternion={QUATERNIONS[orientation]}>
      <meshBasicMaterial ref={materialRef} transparent={true} opacity={0.2} color={COLORS[orientation]} side={THREE.DoubleSide} />
      <mesh material={material} >
        <planeGeometry attach="geometry" args={[6, 6]} />
      </mesh>
      { buildingStruts && lineEnd &&
        <Strut position={[0,0,0]} offset={[ lineEnd.x, lineEnd.y, 0 ]} radius={0.05} material={material} />
      }
      {GRID.map( ( [x,y] ) => (
        <mesh position={[x,y,0]} key={JSON.stringify( [x,y] )} material={material}
            onPointerOver={ e => handleHover( {x,y} ) }
            onPointerOut={ e => handleHover( undefined ) }
            onClick={ e => handleClick( e, [x,y,0] ) }>
          <boxBufferGeometry attach="geometry" args={[0.2, 0.2, 0.1]} />
        </mesh>
      ))}
    </group>
  )
}

export default BuildPlane
