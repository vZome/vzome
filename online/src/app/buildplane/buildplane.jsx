
import { For, Show, createContext, createEffect, createMemo, onCleanup, useContext } from 'solid-js';
import { createStore, reconcile, unwrap } from 'solid-js/store';
import { DoubleSide, Matrix4, Quaternion, Vector3, CylinderGeometry, TorusGeometry, Mesh, MeshLambertMaterial, Group } from 'three';

import { normalize, vlength, vscale } from './vectors.js';
import { reducer, initialState, doToggleDisk, doSetCenter, doStrutPreview, doSelectPlane, doSelectHinge, doToggleBuild } from './planes.js';
import { useWorkerClient } from '../../viewer/context/worker.jsx';
import { createStrut, joinBalls, newDesign } from '../../viewer/util/actions.js';
import { useInteractionTool, useInteractionSurface, useCameraControl } from '../../viewer/context/interaction.jsx';
import { createRaycastHitSource } from '../../viewer/raycasting.js';
import { setHingeStrut } from '../../viewer/util/actions.js';
import { createT, useThree } from 'solid-three';
const T = createT({ Mesh, MeshLambertMaterial, Group });

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

// The tool's widgets form one raycasting hit source for the InteractionToolProvider, asked only
// after the scene: the big translucent disk covers most balls on screen, so if widgets came first
// every ball seen through it would be unclickable.  The scene's GPU pick reports no depth, so
// "nearest wins" across the two isn't possible; among the widgets themselves, the nearest wins.
// A widget's hit record carries its own actions: click(), and optionally hover( entering ).
const WidgetsContext = createContext();
let widgetCount = 0;

// Returns a ref callback that registers the widget's mesh with a hit source.
const widgetRef = ( source, actions ) =>
{
  const id = `widget:${++widgetCount}`; // distinct ids, so hovering from widget to widget is a change
  let remove;
  onCleanup( () => remove ?.() );
  return mesh => {
    remove ?.();
    remove = source ?.add( mesh, () => ( { source: 'widget', id, ...actions } ) );
  };
}

// For widget components below the tool, which provides the hit source through WidgetsContext.
const useWidget = ( actions ) => widgetRef( useContext( WidgetsContext ), actions );

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
    <T.Mesh position={strutCenter()} quaternion={strutQuaternion()}
        geometry={ new CylinderGeometry( cylinderSize, cylinderSize, strutLength(), 12, 1, true )} >
      <T.MeshLambertMaterial attach="material" color={"white"} side={DoubleSide} />
    </T.Mesh>
  );
}

const BuildDot = props =>
{
  const widget = useWidget( {
    click: () => props.createStrut(),
    hover: entering => props.previewStrut( entering? props.position : null ),
  } );

  return (
    <T.Mesh ref={widget} position={props.position} quaternion={props.diskRotation} material={props.material}
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
    <T.Group>
      <T.MeshLambertMaterial attach="material" ref={materialRef} color={props.zone.color} side={DoubleSide} />
      <T.Mesh position={coneCenter()} quaternion={zoneQuaternion()} material={materialRef}
          geometry={ new CylinderGeometry( 1/16, 1/6, 1, 12, 1, false )} />
        <For each={ props.zone.vectors }>{ ( v, i ) =>
          <BuildDot position={v} material={materialRef} diskRotation={props.diskRotation}
            previewStrut={props.previewStrut} createStrut={ handleClick( i() ) } />
        }</For>
    </T.Group>
  );
}

const HingeOption = props =>
{
  const widget = useWidget( { click: () => props.changeHinge( props.zone.name, props.zone.orientation ) } );

  const tubeCenter = createMemo( () => vscale( normalize( props.zone.vectors[ 0 ] ), 13 ) );
  const zoneQuaternion = createMemo( () => makeRotation( CYLINDER_AXIS, props.zone.vectors[ 0 ] ) );

  return (
    <T.Group>
      <T.Mesh ref={widget} position={tubeCenter()} quaternion={zoneQuaternion()}
          geometry={ new CylinderGeometry( 0.6, 0.6, 3, 36 )} >
        <T.MeshLambertMaterial attach="material" color={props.zone.color} side={DoubleSide} />
      </T.Mesh>
    </T.Group>
  );
}

const PlaneOption = props =>
{
  const vector = props.zone.vectors[ 0 ];
  const quaternion = createMemo( () => makeRotation( CYLINDER_AXIS, vector ) );
  const widget = useWidget( { click: () => props.changePlane( props.zone.name, props.zone.orientation ) } );
  return (
    <T.Group>
      <T.Mesh ref={widget} quaternion={quaternion()}
          geometry={ new CylinderGeometry( 5, 5, 0.4, 48 )} >
        <T.MeshLambertMaterial attach="material" color={props.zone.color} transparent={true} opacity={0.5} />
      </T.Mesh>
    </T.Group>
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
    <T.Group position={props.state.center.position} quaternion={globalRotation()}>
      <T.Mesh quaternion={hingeQuaternion()}
          geometry={ new CylinderGeometry( 1/2, 1/2, 2*discSize, 12, 1, false ) } >
        <T.MeshLambertMaterial attach="material" transparent={true} opacity={0.5} color={plane().color} />
      </T.Mesh>

      <T.Group position={disksCenter()}>
        <For each={ plane().zones }>{ ( zone ) =>
          <PlaneOption zone={zone} changePlane={doChangePlane} />
        }</For>
      </T.Group>
    </T.Group>
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

  // The widgets' hit source (see useWidget), asked after the scene.
  const surface = useInteractionSurface();
  const widgets = createRaycastHitSource( useThree() );
  if ( surface )
    onCleanup( surface .addHitSource( { pickAt: widgets.pickAt, priority: -1 } ) );

  const camera = useCameraControl();
  let hovered = null;

  const handlers = {

    picks: { click: true, drag: false, hover: true },

    cursor: 'cell',

    suppressContextMenu: true, // the right button pans

    // Every drag moves the camera: this app has no separate camera mode.
    onDragStart: e => {
      switch ( e.button ) {
        case 0:  return e.shiftKey? camera.pan( e ) : camera.rotate( e );
        case 1:  return camera.zoom( e );
        case 2:  return camera.pan( e );
        default: return null;
      }
    },

    onWheel: camera.onWheel,

    bkgdClick: () => dispatch( doToggleDisk() ),

    onClick: ( e, hit ) => {
      if ( hit.source === 'widget' ) {
        hit.click ?.();
        return;
      }
      const { id, position, type } = hit;
      if ( type === 'ball' ) {
        if ( state.preview ) {
          postMessage( joinBalls( state.center.id, id ) );
        }
        dispatch( doSetCenter( id, position ) );
      } else {
        postMessage( setHingeStrut( id, unwrap(state.center.id), unwrap(state.diskZone), unwrap(state.hingeZone) ) );
      }
    },
    
    onHover: ( e, hit ) => {
      if ( hovered ?.source === 'widget' )
        hovered.hover ?.( false );
      hovered = hit;
      if ( hit ?.source === 'widget' && hit.hover ) {
        hit.hover( true );
        return;
      }
      if ( hit ?.source !== 'scene' )
        hit = null; // e.g. the disk: nothing in the scene under the pointer
      if ( hit && state.buildingStruts && state.center ?.position ) {
        const { position, type } = hit;
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

  const disk = widgetRef( widgets, { click: () => actions.toggleBuild() } );

  return (
    <WidgetsContext.Provider value={widgets}>
    <T.Group>
    <Show when={state.buildPlanes && state.enabled && state.center}>
      <T.Group position={state.center.position}>
        <T.Group quaternion={globalRotation()}>
          <T.Mesh ref={disk} quaternion={diskRotation()}
              geometry={ new CylinderGeometry( discSize, discSize, 0.05, 60 ) } >
            <T.MeshLambertMaterial attach="material" transparent={true} opacity={0.5} color={plane().color} />
          </T.Mesh>
          <T.Mesh quaternion={hoopRotation()}
              geometry={ new TorusGeometry( discSize, 0.5, 15, 60 ) } >
            <T.MeshLambertMaterial attach="material" transparent={true} opacity={0.5} color={plane().color} />
          </T.Mesh>
          <For each={ plane().zones }>{ ( zone, zoneIndex ) =>
            <Show when={state.buildingStruts} fallback={
              <HingeOption zone={zone} changeHinge={doChangeHinge} />
            }>
              <BuildZone zone={zone} diskRotation={diskRotation()}
                previewStrut={actions.previewStrut} createStrut={ createZoneStrut( zoneIndex() ) } />
            </Show>
          }</For>
          <T.Group>
            {/* This one is relative to the state.center AND oriented the global rotation */}
            <Show when={ state.preview && ! state.preview?.forBall }>
              <StrutPreview endPt={state.preview.endPt} />
            </Show>
          </T.Group>
        </T.Group>
        <T.Group>
          {/* This one is relative to the state.center only */}
          <Show when={ state.preview?.forBall }>
            <StrutPreview endPt={state.preview.endPt} />
          </Show>
        </T.Group>
      </T.Group>
      <T.Group>
        <Show when={ !state.buildingStruts }>
          <Hinge state={state} changePlane={actions.changePlane} />
        </Show>
      </T.Group>
    </Show>
    </T.Group>
    </WidgetsContext.Provider>
  )
}
