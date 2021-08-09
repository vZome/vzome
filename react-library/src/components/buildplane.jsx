import React, { useState } from 'react'
import * as THREE from 'three'

function BuildPlane( { config, startGridHover, stopGridHover } )
{
  const { position, quaternion, grid, color, size, field } = config
  const [ material, materialRef ] = useState()
  const [ planeMaterial, planeMaterialRef ] = useState()
  const rsize = field.embed( size )
  const dotSize = rsize / 24
  const discSize = rsize * 4
  
  const makeAbsolute = ( gridPt ) =>
  {
    let vector3d = field.quatTransform( quaternion, [ ...gridPt, field.zero ] )
    return field.vectoradd( position, vector3d )
  }
  const handleHoverIn = ( e, gridPt ) =>
  {
    e.stopPropagation()
    startGridHover( makeAbsolute( gridPt ) )
  }
  const handleHoverOut = ( e, gridPt ) =>
  {
    e.stopPropagation()
    stopGridHover( makeAbsolute( gridPt ) )
  }
  const handleClick = ( e, gridPt ) =>
  {
    e.stopPropagation()
    console.log( "handle grid click: " + JSON.stringify( gridPt ) )
  }
  const wlast = q =>
  {
    const [ w, x, y, z ] = q
    return [ x, y, z, w ]
  }

  /*
    Ideas:
      - when building struts, render the colored rays
      - always render the dots in colors

      - always render the hinge cylinder
      - hover on the hinge should show the two control handles:
         - a ball plus arc for the hinge angle around the center
         - a small torus for the plane angle around the hinge
  */
  
  return (
    <group position={field.embedv( position )} quaternion={field.embedv( wlast( quaternion ) )}>
      <meshLambertMaterial ref={planeMaterialRef} transparent={true} opacity={0.5} color={color} />
      <mesh rotation={[ Math.PI / 2, 0, 0 ]} material={planeMaterial}>
        <cylinderBufferGeometry attach="geometry" args={[ discSize, discSize, 0.05, 60 ]} />
      </mesh>
      <mesh material={planeMaterial}>
        <torusBufferGeometry attach="geometry" args={[ discSize, 0.5, 15, 60 ]} />
      </mesh>
      <meshLambertMaterial ref={materialRef} color='black' side={THREE.DoubleSide} />
      {grid.map( ( gv ) => {
        const [ x, y, z ] = field.embedv( gv ) 
        return (
          <mesh position={[x,y,z]} key={JSON.stringify( gv )} material={material}
              onPointerOver={ e => handleHoverIn( e, gv ) }
              onPointerOut={ e => handleHoverOut( e, gv ) }
              onClick={ e => handleClick( e, gv ) }>
            <icosahedronBufferGeometry attach="geometry" args={[dotSize]} />
          </mesh>
        )}) }
    </group>
  )
}

export default BuildPlane
