
import { For, Show, createEffect, createMemo } from 'solid-js';
import { createStore, reconcile, unwrap } from 'solid-js/store';
import { DoubleSide, Matrix4, Quaternion, Vector3, CylinderGeometry, TorusGeometry } from 'three';

import { normalize, vlength, vscale } from './vectors.js';
import { reducer, initialState, doToggleDisk, doSetCenter, doStrutPreview, doSelectPlane, doSelectHinge, doToggleBuild } from './planes.js';
import { createStrut, joinBalls, newDesign, useWorkerClient } from '../../workerClient/index.js';
import { useInteractionTool } from '../../viewer/solid/interaction.jsx';
import { setHingeStrut } from '../../workerClient/actions.js';

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

const makeOrientation = ( buildPlanes, orientation ) =>
{
  if ( !buildPlanes )
    return new Quaternion();
  const array = buildPlanes .orientations[ orientation ];
  const matrix = new Matrix4() .fromArray( array ) .transpose();
  return new Quaternion() .setFromRotationMatrix( matrix );
}

const discSize = 35;
const cylinderSize = 1/8;
const TORUS_AXIS = [0,0,1];
const CYLINDER_AXIS = [0,1,0];

const StrutPreview = props =>
{
  const strutCenter = () => vscale( props.endPt, 1/2 );
  const strutLength = () => vlength( props.endPt );
  const strutQuaternion = () => makeRotation( CYLINDER_AXIS, props.endPt );

  return (
    <mesh position={strutCenter()} quaternion={strutQuaternion()}
        geometry={ new CylinderGeometry( cylinderSize, cylinderSize, strutLength(), 12, 1, true )} >
      <meshLambertMaterial color={"white"} side={DoubleSide} />
    </mesh>
  );
}

const BuildDot = props =>
{
  const handleHover = value => e =>
  {
    e.stopPropagation();
    if ( value )
      props.previewStrut( props.position );
    else
      props.previewStrut( null );
  }
  const handleClick = ( e ) =>
  {
    e.stopPropagation();
    props.createStrut();
  }

  return (
    <mesh position={props.position} quaternion={props.diskRotation} material={props.material}
        onPointerOver={ handleHover( true ) } onPointerOut={ handleHover( false ) } onClick={ handleClick }
        geometry={ new CylinderGeometry( 0.5, 0.5, 0.35, 20 )} />
  )
}

const BuildZone = props =>
{
  const handleClick = index => () => props.createStrut( index );

  const coneCenter = createMemo( () => vscale( normalize( props.zone.vectors[ 0 ] ), 1.7 ) );
  const zoneQuaternion = createMemo( () => makeRotation( CYLINDER_AXIS, props.zone.vectors[ 0 ] ) );
  let materialRef;

  return (
    <group>
      <meshLambertMaterial ref={materialRef} color={props.zone.color} side={DoubleSide} />
      <mesh position={coneCenter()} quaternion={zoneQuaternion()} material={materialRef}
          geometry={ new CylinderGeometry( 1/16, 1/6, 1, 12, 1, false )} />
        <For each={ props.zone.vectors }>{ ( v, i ) =>
          <BuildDot position={v} material={materialRef} diskRotation={props.diskRotation}
            previewStrut={props.previewStrut} createStrut={ handleClick( i() ) } />
        }</For>
    </group>
  );
}

const HingeOption = props =>
{
  const handleClick = e =>
  {
    e.stopPropagation();
    props.changeHinge( props.zone.name, props.zone.orientation );
  }

  const tubeCenter = createMemo( () => vscale( normalize( props.zone.vectors[ 0 ] ), 13 ) );
  const zoneQuaternion = createMemo( () => makeRotation( CYLINDER_AXIS, props.zone.vectors[ 0 ] ) );

  return (
    <group>
      <mesh position={tubeCenter()} quaternion={zoneQuaternion()} onClick={handleClick}
          geometry={ new CylinderGeometry( 0.6, 0.6, 3, 36 )} >
        <meshLambertMaterial color={props.zone.color} side={DoubleSide} />
      </mesh>
    </group>
  );
}

const PlaneOption = props =>
{
  const vector = props.zone.vectors[ 0 ];
  const quaternion = createMemo( () => makeRotation( CYLINDER_AXIS, vector ) );
  const handleClick = e =>
  {
    e.stopPropagation();
    props.changePlane( props.zone.name, props.zone.orientation );
  }
  return (
    <group>
      <mesh quaternion={quaternion()} onClick={handleClick}
          geometry={ new CylinderGeometry( 5, 5, 0.4, 48 )} >
        <meshLambertMaterial color={props.zone.color} transparent={true} opacity={0.5} />
      </mesh>
    </group>
  );
}

const Hinge = props =>
{
  const buildPlanes = () => props.state.buildPlanes;
  const plane = () => buildPlanes() .planes[ props.state.hingeZone.orbit ];
  const permutation = () => buildPlanes() .permutations[ props.state.hingeZone.orientation ];
  const doChangePlane = ( orbit, orientation ) => props.changePlane( orbit, permutation()[ orientation ] );
  const disksCenter = createMemo( () => vscale( normalize( plane().normal ), 6 ) );

  const hingeQuaternion = createMemo( () => makeRotation( CYLINDER_AXIS, plane().normal ) );
  const globalRotation = createMemo( () => makeOrientation( buildPlanes(), props.state.hingeZone.orientation ) );

  return (
    <group position={props.state.center.position} quaternion={globalRotation()}>
      <mesh quaternion={hingeQuaternion()}
          geometry={ new CylinderGeometry( 1/2, 1/2, 2*discSize, 12, 1, false ) } >
        <meshLambertMaterial attach="material" transparent={true} opacity={0.5} color={plane().color} />
      </mesh>

      <group position={disksCenter()}>
        <For each={ plane().zones }>{ ( zone ) =>
          <PlaneOption zone={zone} changePlane={doChangePlane} />
        }</For>
      </group>
    </group>
  )
}

// Straight from the SolidJS tutorial
const useReducer = (reducer, state) =>
{
  const [ store, setStore ] = createStore(state);
  const dispatch = (action) => {
    state = reducer( state, action );
    setStore( reconcile( state ) );
  }
  return [ store, dispatch ];
};

export const BuildPlaneTool = props =>
{
  const { postMessage, subscribe } = useWorkerClient();

  const [ state, dispatch ] = useReducer( reducer, initialState ); // dedicated local store
  createEffect( () => {
    // Connect the worker store to the local store, to listen to worker events
    subscribe( {
      onWorkerError: error => console.log( error ), // TODO show the user!
      onWorkerMessage: msg => dispatch( msg ),
    } );
    postMessage( newDesign() );
  } );

  const handlers = {

    allowTrackball: true,

    cursor: 'cell',

    onDrag: () => {},
    onDragStart: () => {},
    onDragEnd: () => {},

    bkgdClick: () => dispatch( doToggleDisk() ),

    onClick: ( id, position, type, selected ) => {
      if ( type === 'ball' ) {
        if ( state.preview ) {
          postMessage( joinBalls( state.center.id, id ) );
        }
        dispatch( doSetCenter( id, position ) );
      } else {
        postMessage( setHingeStrut( id, unwrap(state.center.id), unwrap(state.diskZone), unwrap(state.hingeZone) ) );
      }
    },
    
    onHover: ( id, position, type, starting ) => {
      if ( starting && state.buildingStruts && state.center ?.position ) {
        if ( type === 'ball' ) {
          const [ x0, y0, z0 ] = state.center.position;
          const [ x1, y1, z1 ] = position;
          const endPt = [ x1-x0, y1-y0, z1-z0 ];
          dispatch( doStrutPreview( { endPt, forBall: true } ) );
        }
      } else
        dispatch( doStrutPreview() );
    },
  };

  const [ _, setTool ] = useInteractionTool();
  createEffect( () => setTool( handlers ) );

  const actions = {
    createStrut: ( plane, zone, index, orientation ) => postMessage( createStrut( state.center.id, plane, zone, index, orientation ) ),
    previewStrut: endPt => dispatch( doStrutPreview( !!endPt? { endPt, forBall: false } : undefined ) ),
    changePlane: ( name, orientation ) => dispatch( doSelectPlane( name, orientation ) ),
    changeHinge: ( name, orientation ) => dispatch( doSelectHinge( name, orientation ) ),
    toggleBuild: endPt => dispatch( doToggleBuild( endPt ) ),
  }

  const plane = () => state.buildPlanes?.planes[ state.diskZone.orbit ];
  const permutation = () => state.buildPlanes?.permutations[ state.diskZone.orientation ];
  const doChangeHinge = ( orbit, orientation ) => actions.changeHinge( orbit, permutation()[ orientation ] );
  const diskRotation = createMemo( () => makeRotation( CYLINDER_AXIS, plane()?.normal ) );
  const hoopRotation = createMemo( () => makeRotation( TORUS_AXIS, plane()?.normal ) );
  const globalRotation = createMemo( () => makeOrientation( state.buildPlanes, state.diskZone.orientation ) );

  const createZoneStrut = zoneIndex => index => actions.createStrut( state.diskZone.orbit, zoneIndex, index, state.diskZone.orientation );

  const diskClick = e => {
    if ( e.delta < 5 ) {
      e.stopPropagation();
      actions.toggleBuild();
    }
  }
  
  return (
    <group>
    <Show when={state.buildPlanes && state.enabled && state.center}>
      <group position={state.center.position}>
        <group quaternion={globalRotation()}>
          <mesh quaternion={diskRotation()} onClick={diskClick}
              geometry={ new CylinderGeometry( discSize, discSize, 0.05, 60 ) } >
            <meshLambertMaterial transparent={true} opacity={0.5} color={plane().color} />
          </mesh>
          <mesh quaternion={hoopRotation()}
              geometry={ new TorusGeometry( discSize, 0.5, 15, 60 ) } >
            <meshLambertMaterial transparent={true} opacity={0.5} color={plane().color} />
          </mesh>
          <For each={ plane().zones }>{ ( zone, zoneIndex ) =>
            <Show when={state.buildingStruts} fallback={
              <HingeOption zone={zone} changeHinge={doChangeHinge} />
            }>
              <BuildZone zone={zone} diskRotation={diskRotation()}
                previewStrut={actions.previewStrut} createStrut={ createZoneStrut( zoneIndex() ) } />
            </Show>
          }</For>
          <group>
            {/* This one is relative to the state.center AND oriented the global rotation */}
            <Show when={ state.preview && ! state.preview?.forBall }>
              <StrutPreview endPt={state.preview.endPt} />
            </Show>
          </group>
        </group>
        <group>
          {/* This one is relative to the state.center only */}
          <Show when={ state.preview?.forBall }>
            <StrutPreview endPt={state.preview.endPt} />
          </Show>
        </group>
      </group>
      <group>
        <Show when={ !state.buildingStruts }>
          <Hinge state={state} changePlane={actions.changePlane} />
        </Show>
      </group>
    </Show>
    </group>
  )
}
