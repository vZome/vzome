import React, { useState } from 'react';
import { useMemo } from 'react';
import { DoubleSide, Matrix4, Quaternion, Vector3 } from 'three';

const BuildZone = ( { zone, position, startGridHover, stopGridHover, createStrut } ) =>
{
  const dotSize = 1/3;

  const makeAbsolute = ( gridPt ) =>
  {
    // let vector3d = field.quatTransform( quaternion, [ ...gridPt, field.zero ] )
    return position .map( (x,i) => x + gridPt[ i ] );
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
  const handleClick = ( e, index ) =>
  {
    e.stopPropagation();
    createStrut( index );
  }

  const [ material, materialRef ] = useState();
  return (
    <group>
      <meshLambertMaterial ref={materialRef} color={zone.color} side={DoubleSide} />
      {zone.vectors.map( ( v, i ) =>
        <mesh position={v} key={JSON.stringify( v )} material={material}
            onPointerOver={ e => handleHoverIn( e, v ) }
            onPointerOut={ e => handleHoverOut( e, v ) }
            onClick={ e => handleClick( e, i ) }>
          <icosahedronBufferGeometry attach="geometry" args={[dotSize]} />
        </mesh>
      )}
    </group>
  );
}

const makeRotation = ( from, to ) =>
{
  const fromV = new Vector3() .fromArray( from ) .normalize();
  const toV = new Vector3() .fromArray( to ) .normalize();
  const axis = new Vector3() .copy( fromV ) .cross( toV ) .normalize();
  if ( axis .equals( new Vector3() ) )
    return new Quaternion();
  const angle = Math.acos( fromV .dot( toV ) );
  const matrix = new Matrix4() .makeRotationAxis( axis, angle );
  return new Quaternion() .setFromRotationMatrix( matrix );
}

export const BuildPlane = ( { buildPlanes, state, startGridHover, stopGridHover, createStrut } ) =>
{
  const { position, quaternion, plane } = state;
  const [ planeMaterial, planeMaterialRef ] = useState()
  const discSize = 35;
  const wlast = q =>
  {
    const [ w, x, y, z ] = q
    return [ x, y, z, w ]
  }

  const grid = buildPlanes .planes[ plane ];
  const diskRotation = useMemo( () => makeRotation( [0,1,0], grid.normal ), [ grid ] );
  const hoopRotation = useMemo( () => makeRotation( [0,0,1], grid.normal ), [ grid ] );

  const createZoneStrut = ( zone ) => ( index ) => createStrut( 'IGNORED', plane, zone, index );

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
    <group position={position} quaternion={wlast( quaternion )}>
      <meshLambertMaterial ref={planeMaterialRef} transparent={true} opacity={0.5} color={grid.color} />
      <mesh quaternion={diskRotation} material={planeMaterial}>
        <cylinderBufferGeometry attach="geometry" args={[ discSize, discSize, 0.05, 60 ]} />
      </mesh>
      <mesh quaternion={hoopRotation} material={planeMaterial}>
        <torusBufferGeometry attach="geometry" args={[ discSize, 0.5, 15, 60 ]} />
      </mesh>
      {grid.zones .map( ( zone, index ) =>
        <BuildZone key={index} {...{ zone, position, startGridHover, stopGridHover }} createStrut={ createZoneStrut( index ) } />
      )}
    </group>
  )
}
