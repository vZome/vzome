import React, { useState } from 'react';
import { useMemo } from 'react';
import { DoubleSide, Matrix4, Quaternion, Vector3 } from 'three';

import { normalize, vlength, vscale } from './vectors.js';

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

const useCylinderQuaternion = vector => useMemo( () => makeRotation( [0,1,0], vector ), [ vector ] );

const useOrientation = ( buildPlanes, orientation ) =>
{
  return useMemo( () => {
    const array = buildPlanes .orientations[ orientation ];
    const matrix = new Matrix4() .fromArray( array ) .transpose();
    return new Quaternion() .setFromRotationMatrix( matrix );
  }, [ buildPlanes, orientation ] );
}

const discSize = 35;
const dotSize = 1/3;
const cylinderSize = 1/8;
const TORUS_AXIS = [0,0,1];

const StrutPreview = ( { endPt } ) =>
{
  const [ strutCenter, strutLength ] = useMemo( () => {
    const midpoint = vscale( endPt, 1/2 );
    const length = vlength( endPt );
    return [ midpoint, length ];
  }, [ endPt ] );
  const strutQuaternion = useCylinderQuaternion( endPt );

  return (
    <mesh position={strutCenter} quaternion={strutQuaternion}>
      <meshLambertMaterial attach="material" color={"white"} side={DoubleSide} />
      <cylinderBufferGeometry attach="geometry" args={[ cylinderSize, cylinderSize, strutLength, 12, 1, true ]} />
    </mesh>
  );
}

const BuildDot = ( { position, material, previewStrut, createStrut } ) =>
{
  const handleHover = value => e =>
  {
    e.stopPropagation();
    if ( value )
      previewStrut( position );
    else
      previewStrut( null );
  }
  const handleClick = ( e ) =>
  {
    e.stopPropagation();
    createStrut();
  }

  return (
    <mesh position={position} material={material}
        onPointerOver={ handleHover( true ) } onPointerOut={ handleHover( false ) } onClick={ handleClick }>
      <sphereBufferGeometry attach="geometry" args={[ dotSize, 12, 8 ]} />
    </mesh>
  )
}

const BuildZone = ( { zone, previewStrut, createStrut } ) =>
{
  const handleClick = index => () => createStrut( index );

  const [ material, materialRef ] = useState();

  const coneCenter = useMemo( () => vscale( normalize( zone.vectors[ 0 ] ), 1.7 ), [ zone ] );
  const zoneQuaternion = useCylinderQuaternion( zone.vectors[ 0 ] );

  return (
    <group>
      <meshLambertMaterial ref={materialRef} color={zone.color} side={DoubleSide} />
      <mesh position={coneCenter} quaternion={zoneQuaternion} material={material}>
        <cylinderBufferGeometry attach="geometry" args={[ 1/16, 1/6, 1, 12, 1, false ]} />
      </mesh>
      {zone.vectors.map( ( v, i ) =>
        <BuildDot key={i} position={v} material={material} previewStrut={previewStrut} createStrut={handleClick( i )} />
      )}
    </group>
  );
}

const HingeOption = ( { zone, changeHinge } ) =>
{
  const handleClick = e =>
  {
    e.stopPropagation();
    changeHinge( zone.name, zone.orientation );
  }

  const [ material, materialRef ] = useState();

  const tubeCenter = useMemo( () => vscale( normalize( zone.vectors[ 0 ] ), 9 ), [ zone ] );
  const zoneQuaternion = useCylinderQuaternion( zone.vectors[ 0 ] );

  return (
    <group>
      <meshLambertMaterial ref={materialRef} color={zone.color} side={DoubleSide} />
      <mesh position={tubeCenter} quaternion={zoneQuaternion} material={material} onClick={handleClick}>
        <cylinderBufferGeometry attach="geometry" args={[ 0.6, 0.6, 3, 36 ]} />
      </mesh>
    </group>
  );
}

const PlaneOption = ({ zone, changePlane }) =>
{
  const [ diskMaterial, diskMaterialRef ] = useState();
  const vector = zone.vectors[ 0 ];
  const quaternion = useCylinderQuaternion( vector );
  const handleClick = e =>
  {
    e.stopPropagation();
    changePlane( zone.name, zone.orientation );
  }
  return (
    <group>
      <meshLambertMaterial ref={diskMaterialRef} color={zone.color} transparent={true} opacity={0.5} />
      <mesh quaternion={quaternion} material={diskMaterial} onClick={handleClick}>
        <cylinderBufferGeometry attach="geometry" args={[ 6, 6, 0.4, 48 ]} />
      </mesh>
    </group>
  );
}

const Hinge = ( { state, buildPlanes, actions } ) =>
{
  const { center, hingeZone } = state;
  const { orbit, orientation } = hingeZone;
  const plane = buildPlanes .planes[ orbit ];
  const permutation = buildPlanes .permutations[ orientation ];
  const doChangePlane = ( orbit, orientation ) => actions.changePlane( orbit, permutation[ orientation ] );

  const hingeQuaternion = useCylinderQuaternion( plane.normal );
  const globalRotation = useOrientation( buildPlanes, orientation );

  return (
    <group position={center.position} quaternion={globalRotation}>
      <mesh quaternion={hingeQuaternion}>
        <meshLambertMaterial attach="material" transparent={true} opacity={0.5} color={plane.color} />
        <cylinderBufferGeometry attach="geometry" args={[ 1/2, 1/2, 2*discSize, 12, 1, false ]} />
      </mesh>

      {plane.zones .map( ( zone, zoneIndex ) =>
        <PlaneOption key={zoneIndex} zone={zone} changePlane={doChangePlane} />
      )}
    </group>
  )
}

export const BuildPlane = ( { buildPlanes, state, actions } ) =>
{
  const { center, diskZone } = state;
  const { orbit, orientation } = diskZone;
  const [ planeMaterial, planeMaterialRef ] = useState()

  const plane = buildPlanes .planes[ orbit ];
  const permutation = buildPlanes .permutations[ orientation ];
  const doChangeHinge = ( orbit, orientation ) => actions.changeHinge( orbit, permutation[ orientation ] );
  const diskRotation = useCylinderQuaternion( plane.normal );
  const hoopRotation = useMemo( () => makeRotation( TORUS_AXIS, plane.normal ), [ plane ] );
  const globalRotation = useOrientation( buildPlanes, orientation );

  const createZoneStrut = ( zoneIndex ) => ( index ) => actions.createStrut( orbit, zoneIndex, index, orientation );

  const diskClick = e => {
    e.stopPropagation();
    actions.toggleBuild();
  }
  
  return (
    <group>
      <group position={center.position} quaternion={globalRotation}>
        <meshLambertMaterial ref={planeMaterialRef} transparent={true} opacity={0.5} color={plane.color} />
        <mesh quaternion={diskRotation} material={planeMaterial} onClick={diskClick}>
          <cylinderBufferGeometry attach="geometry" args={[ discSize, discSize, 0.05, 60 ]} />
        </mesh>
        <mesh quaternion={hoopRotation} material={planeMaterial}>
          <torusBufferGeometry attach="geometry" args={[ discSize, 0.5, 15, 60 ]} />
        </mesh>
        {plane.zones .map( ( zone, zoneIndex ) =>
          state.buildingStruts?
            <BuildZone key={zoneIndex} zone={zone}
              previewStrut={actions.previewStrut} createStrut={ createZoneStrut( zoneIndex ) } />
          :
            <HingeOption key={zoneIndex} zone={zone} changeHinge={doChangeHinge} />
          ) }
        {state.endPt &&
          <StrutPreview endPt={state.endPt} />}
      </group>
      { !state.buildingStruts &&
        <Hinge state={state} buildPlanes={buildPlanes} actions={actions} />
      }
    </group>
  )
}
