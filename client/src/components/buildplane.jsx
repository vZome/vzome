import React from 'react'
import * as THREE from 'three'
import { useResource } from 'react-three-fiber'

function BuildPlane( { config, buildNodeOrEdge, eraseNodeOrEdge } )
{
  const { position, quaternion, grid, color, size, field, buildingStruts } = config
  const [ materialRef, material ] = useResource()
  const rsize = field.embed( size )
  const dotSize = rsize / 24
  
  const makeAbsolute = ( gridPt ) =>
  {
    let vector3d = field.quatTransform( quaternion, [ ...gridPt, field.zero ] )
    return field.vectoradd( position, vector3d )
  }
  const handleHoverIn = ( e, gridPt ) =>
  {
    e.stopPropagation()
    buildNodeOrEdge( buildingStruts? position : undefined, makeAbsolute( gridPt ), false, true )
  }
  const handleHoverOut = ( e ) =>
  {
    e.stopPropagation()
    eraseNodeOrEdge()
  }
  const handleClick = ( e, gridPt ) =>
  {
    e.stopPropagation()
    console.log( "handle grid click: " + JSON.stringify( gridPt ) )
    buildNodeOrEdge( buildingStruts? position : undefined, makeAbsolute( gridPt ), true, false )
  }
  const wlast = q =>
  {
    const [ w, x, y, z ] = q
    return [ x, y, z, w ]
  }
  
  return (
    <group position={field.embedv( position )} quaternion={field.embedv( wlast( quaternion ) )}>
      <meshLambertMaterial ref={materialRef} transparent={true} opacity={0.7} color={color} side={THREE.DoubleSide} />
      {grid.map( ( gv ) => {
        const [ x, y, z ] = field.embedv( gv ) 
        return (
          <mesh position={[x,y,z]} key={JSON.stringify( gv )} material={material}
              onPointerOver={ e => handleHoverIn( e, gv ) }
              onPointerOut={ e => handleHoverOut( e ) }
              onClick={ e => handleClick( e, gv ) }>
            <icosahedronBufferGeometry attach="geometry" args={[dotSize]} />
          </mesh>
        )}) }
    </group>
  )
}

export default BuildPlane
