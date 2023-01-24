import React, { useState, useEffect, useMemo, useReducer, forwardRef, useImperativeHandle } from 'react';
import { DoubleSide, Matrix4, Quaternion, Vector3 } from 'three';
import { useFrame } from '@react-three/fiber'

import { normalize, vlength, vscale } from './vectors.js';
import { reducer, initialState, doToggleDisk, doSetCenter, doStrutPreview, doSelectPlane, doSelectHinge, doToggleBuild } from './planes.js';
import { createStrut, joinBalls, newDesign } from '../../ui/viewer/store.js';

const makeRotation = ( from, to ) =>
{
  if ( !to )
    return new Quaternion();
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
    if ( !buildPlanes )
      return new Quaternion();
    const array = buildPlanes .orientations[ orientation ];
    const matrix = new Matrix4() .fromArray( array ) .transpose();
    return new Quaternion() .setFromRotationMatrix( matrix );
  }, [ buildPlanes, orientation ] );
}

const discSize = 35;
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

const BuildDot = ( { position, diskRotation, material, previewStrut, createStrut } ) =>
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
    <mesh position={position} quaternion={diskRotation} material={material}
        onPointerOver={ handleHover( true ) } onPointerOut={ handleHover( false ) } onClick={ handleClick }>
      <cylinderBufferGeometry attach="geometry" args={[ 0.5, 0.5, 0.35, 20 ]} />
    </mesh>
  )
}

const BuildZone = ( { zone, previewStrut, createStrut, diskRotation } ) =>
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
        <BuildDot key={i} position={v} material={material} diskRotation={diskRotation}
          previewStrut={previewStrut} createStrut={handleClick( i )} />
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

  const tubeCenter = useMemo( () => vscale( normalize( zone.vectors[ 0 ] ), 13 ), [ zone ] );
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
        <cylinderBufferGeometry attach="geometry" args={[ 5, 5, 0.4, 48 ]} />
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
  const disksCenter = useMemo( () => vscale( normalize( plane.normal ), 6 ), [ plane ] );

  const hingeQuaternion = useCylinderQuaternion( plane.normal );
  const globalRotation = useOrientation( buildPlanes, orientation );

  return (
    <group position={center.position} quaternion={globalRotation}>
      <mesh quaternion={hingeQuaternion}>
        <meshLambertMaterial attach="material" transparent={true} opacity={0.5} color={plane.color} />
        <cylinderBufferGeometry attach="geometry" args={[ 1/2, 1/2, 2*discSize, 12, 1, false ]} />
      </mesh>

      <group position={disksCenter}>
        {plane.zones .map( ( zone, zoneIndex ) =>
          <PlaneOption key={zoneIndex} zone={zone} changePlane={doChangePlane} />
        )}
      </group>
    </group>
  )
}

const BuildPlane = ( { worker }, toolRef ) =>
{
  const { sendToWorker, subscribe } = worker;
  const [ state, dispatch ] = useReducer( reducer, initialState ); // dedicated local store
  useEffect( () => {
    // Connect the worker store to the local store, to listen to worker events
    subscribe( {
      onWorkerError: error => console.log( error ), // TODO show the user!
      onWorkerMessage: msg => dispatch( msg ),
    } );
    sendToWorker( newDesign() );
  }, [] );

  // This enables the parent component to connect this tool to the scene
  useImperativeHandle( toolRef, () => ({
    bkgdClick: () => dispatch( doToggleDisk() ),
    onClick: ( id, position, type, selected ) => {
      if ( type === 'ball' ) {
        if ( state.endPt ) {
          sendToWorker( joinBalls( state.center.id, id ) );
        }
        dispatch( doSetCenter( id, position ) );
      }
    },
    onHover: ( id, position, type, starting ) => {
      if ( starting && state.buildingStruts && state.center ?.position ) {
        if ( type === 'ball' ) {
          const [ x0, y0, z0 ] = state.center.position;
          const [ x1, y1, z1 ] = position;
          const vector = [ x1-x0, y1-y0, z1-z0 ];
          dispatch( doStrutPreview( vector ) );
        }
      } else
        dispatch( doStrutPreview() );
    },
  }));
  const actions = {
    createStrut: ( plane, zone, index, orientation ) => sendToWorker( createStrut( state.center.id, plane, zone, index, orientation ) ),
    previewStrut: endPt => dispatch( doStrutPreview( endPt ) ),
    changePlane: ( name, orientation ) => dispatch( doSelectPlane( name, orientation ) ),
    changeHinge: ( name, orientation ) => dispatch( doSelectHinge( name, orientation ) ),
    toggleBuild: endPt => dispatch( doToggleBuild( endPt ) ),
  }

  const { center, diskZone, buildPlanes } = state;
  const { orbit, orientation } = diskZone;
  const [ planeMaterial, planeMaterialRef ] = useState()

  const plane = buildPlanes?.planes[ orbit ];
  const permutation = buildPlanes?.permutations[ orientation ];
  const doChangeHinge = ( orbit, orientation ) => actions.changeHinge( orbit, permutation[ orientation ] );
  const diskRotation = useCylinderQuaternion( plane?.normal );
  const hoopRotation = useMemo( () => makeRotation( TORUS_AXIS, plane?.normal ), [ plane ] );
  const globalRotation = useOrientation( buildPlanes, orientation );

  const createZoneStrut = ( zoneIndex ) => ( index ) => actions.createStrut( orbit, zoneIndex, index, orientation );

  const [ needsRender, setNeedsRender ] = useState( 20 );
  useFrame( ({ gl, scene, camera }) => {
    if ( needsRender > 0 ) {
      gl.render( scene, camera );
      setNeedsRender( needsRender-1 );
    }
  }, 1 );
  const previewStrut = position =>
  {
    actions.previewStrut( position );
    setNeedsRender( 10 );
  }

  const diskClick = e => {
    if ( e.delta < 5 ) {
      e.stopPropagation();
      actions.toggleBuild();
    }
  }
  
  return ( state.buildPlanes && state.enabled && state.center &&
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
            <BuildZone key={zoneIndex} zone={zone} diskRotation={diskRotation}
              previewStrut={previewStrut} createStrut={ createZoneStrut( zoneIndex ) } />
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

export const BuildPlaneTool = forwardRef( BuildPlane );