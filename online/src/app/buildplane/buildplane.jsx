import React, { useState } from 'react';
import { useMemo } from 'react';
import { DoubleSide, Matrix4, Quaternion, Vector3 } from 'three';

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

const dotSize = 1/3;
const cylinderSize = 1/8;

const BuildDot = ( { position, material, buildingStruts, createStrut } ) =>
{
  const [ strutCenter, strutQuaternion, strutLength ] = useMemo( () => {
    const midpoint = position .map( x => x/2 );
    const length = Math.sqrt( position .reduce( (sum,x) => sum + x**2, 0 ) );
    return [ midpoint, makeRotation( [0,1,0], position ), length ];
  }, [ position ] );
  const [ hovered, setHovered ] = useState( false );
  const handleHover = value => e =>
  {
    e.stopPropagation();
    setHovered( value );
  }
  const handleClick = ( e ) =>
  {
    e.stopPropagation();
    setHovered( false );
    createStrut();
  }

  return (
    <group>
      <mesh position={position} material={material}
          onPointerOver={ handleHover( true ) } onPointerOut={ handleHover( false ) } onClick={ handleClick }>
        <sphereBufferGeometry attach="geometry" args={[ dotSize, 12, 8 ]} />
      </mesh>
      {buildingStruts && hovered &&
        <mesh position={strutCenter} quaternion={strutQuaternion} material={material}>
          <cylinderBufferGeometry attach="geometry" args={[ cylinderSize, cylinderSize, strutLength, 12, 1, true ]} />
        </mesh>
      }
    </group>
  )
}

const BuildZone = ( { zone, createStrut } ) =>
{
  const handleClick = index => () => createStrut( index );

  const [ material, materialRef ] = useState();

  const [ coneCenter, zoneQuaternion ] = useMemo( () => {
    const midpoint = zone.vectors[ 0 ] .map( x => 2*x/3 );
    return [ midpoint, makeRotation( [0,1,0], midpoint ) ];
  }, [ zone ] );

  return (
    <group>
      <meshLambertMaterial ref={materialRef} color={zone.color} side={DoubleSide} />
      <mesh position={coneCenter} quaternion={zoneQuaternion} material={material}>
        <coneBufferGeometry attach="geometry" args={[ 1/8, 1.5 ]} />
      </mesh>
      {zone.vectors.map( ( v, i ) =>
        <BuildDot key={i} position={v} material={material} buildingStruts={true} createStrut={handleClick( i )} />
      )}
    </group>
  );
}

export const BuildPlane = ( { buildPlanes, state, createStrut } ) =>
{
  const { position, quaternion, plane, focusId } = state;
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

  const createZoneStrut = ( zoneIndex ) => ( index ) => createStrut( plane, zoneIndex, index );

  /*
    Ideas:
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
      {grid.zones .map( ( zone, zoneIndex ) =>
        <BuildZone key={zoneIndex} {...{ zone, position }} createStrut={ createZoneStrut( zoneIndex ) } />
      )}
    </group>
  )
}
