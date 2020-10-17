import React, { useState } from 'react'
import * as THREE from 'three'
import { useResource } from 'react-three-fiber'

function BuildPlane( { config, buildNodeOrEdge } )
{
  const { position, quaternion, grid, color, size, field, buildingStruts } = config
  const [ materialRef, material ] = useResource()
  const rsize = field.embed( size )
  const dotSize = rsize / 25
  
  const makeAbsolute = ( gridPt ) =>
  {
    let vector3d = field.quatTransform( quaternion, [ ...gridPt, field.zero ] )
    return field.vectoradd( position, vector3d )
  }
  const handleHoverIn = ( e, gridPt ) =>
  {
    e.stopPropagation()
  }
  const handleHoverOut = ( e, gridPt ) =>
  {
    e.stopPropagation()
  }
  const handleClick = ( e, gridPt ) =>
  {
    e.stopPropagation()
    console.log( "handle grid click: " + JSON.stringify( gridPt ) )
    buildNodeOrEdge( buildingStruts? position : undefined, makeAbsolute( gridPt ) )
  }
  const wlast = q =>
  {
    const [ w, x, y, z ] = q
    return [ x, y, z, w ]
  }
  
  return (
    <group position={field.embedv( position )} quaternion={field.embedv( wlast( quaternion ) )}>
      <meshBasicMaterial ref={materialRef} transparent={true} opacity={0.2} color={color} side={THREE.DoubleSide} />
      <mesh material={material} >
        <planeGeometry attach="geometry" args={[ rsize, rsize ]} />
      </mesh>
      {grid.map( ( gv ) => {
        const [ x, y ] = field.embedv( gv ) 
        return (
          <mesh position={[x,y,0]} key={JSON.stringify( gv )} material={material}
              onPointerOver={ e => handleHoverIn( e, gv ) }
              onPointerOut={ e => handleHoverOut( e, gv ) }
              onClick={ e => handleClick( e, gv ) }>
            <boxBufferGeometry attach="geometry" args={[dotSize,dotSize,dotSize]} />
          </mesh>
        )}) }
    </group>
  )
}

export default BuildPlane
