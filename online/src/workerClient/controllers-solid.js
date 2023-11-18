
import { createEffect } from "solid-js";
import { createStore, reconcile } from "solid-js/store";

import { newDesign, requestControllerProperty, doControllerAction, setControllerProperty, decodeEntities } from './actions.js';
import { useCameraState, defaultCamera } from "./camera.jsx";

const initialState = () => ( {
  scene: {}, 
  trackballScene: {},
  copiedCamera: defaultCamera(), // TODO: this is probably too static, not related to useCameraState
} );

const createWorkerStore = ( worker ) =>
{
  // Beware, createStore does not make a copy, shallow or deep!
  const [ state, setState ] = createStore( { ...initialState(), uuid: worker.uuid } );

  const exportPromises = {};

  const addShape = ( shape, which='scene' ) =>
  {
    if ( ! state[ which ] .shapes ) {
      setState( which, 'shapes', {} );
    }
    if ( ! state[ which ] ?.shapes[ shape.id ] ) {
      setState( which, 'shapes', shape.id, shape );
      return true;
    }
    return false;
  }

  const updateShapes = ( shapes, which='scene' ) =>
  {
    for (const [id, shape] of Object.entries(shapes)) {
      if ( ! addShape( shape, which ) ) {
        // shape is not new, so just replace its instances
        setState( which, 'shapes', id, 'instances', shape.instances );
      }
    }
    // clean up preview strut, which may be a shape otherwise not in the scene
    for ( const id of Object.keys( state[ which ] ?.shapes || {} ) ) {
      if ( ! (id in shapes) )
        setState( which, 'shapes', id, 'instances', [] );
    }
  }

  const logShapes = () =>
  {
    console.log( 'SHAPES:' );
    Object.values( state.scene.shapes ) .forEach( shape => {
      console.log( `  ${shape.id} ${shape.zone} [${shape.instances.length}]` );
    })
  }

  const onWorkerMessage = ( data ) =>
  {
    // console.log( `FROM worker: ${JSON.stringify( data.type, null, 2 )}` );

    switch ( data.type ) {

      case 'ALERT_RAISED': {
        console.log( `Alert from the worker: ${data.payload}` );
        setState( 'problem', data.payload ); // cooperatively managed by both worker and client
        setState( 'waiting', false );
        break;
      }
  
      case 'FETCH_STARTED': {
        setState( 'waiting', true );
        break;
      }
  
      case 'TEXT_EXPORTED': {
        const { action, text } = data.payload;
        exportPromises[ action ] .resolve( text );
        break;
      }

      case 'TEXT_FETCHED': {
        let { name } = data.payload;
        if ( name && name .endsWith( '.vZome' ) ){
          name = name .substring( 0, name .length - 6 );
        }
        setState( 'designName', name ); // cooperatively managed by both worker and client
        setState( 'source', data.payload );
        break;
      }

      case 'SCENES_DISCOVERED': {
        const scenes = data.payload .map( scene => {
          return { ...scene, title: decodeEntities( scene.title ) }
        });
        setState( 'scenes', scenes );
        break;
      }
  
      case 'SCENE_RENDERED': {
        // TODO: I wish I had a better before/after contract with the worker
        const { scene, edit } = data.payload;
        setState( 'edit', edit );
        setState( 'waiting', false );
        setState( 'scene', 'embedding', reconcile( scene.embedding ) );
        updateShapes( scene.shapes );
        // logShapes();
        break;
      }

      case 'SHAPE_DEFINED': {
        setState( 'waiting', false );
        addShape( data.payload );
        // logShapes();
        break;
      }

      case 'INSTANCE_ADDED': {
        let instance = data.payload;
        const shape = state.scene.shapes[ instance.shapeId ];
        setState( 'scene', 'shapes', shape.id, 'instances', [ ...shape.instances, instance ] );
        // logShapes();
        break;
      }

      case 'INSTANCE_REMOVED': {
        let { shapeId, id } = data.payload;
        const shape = state.scene.shapes[ shapeId ];
        const instances = shape.instances .filter( instance => instance.id != id );
        setState( 'scene', 'shapes', shape.id, 'instances', instances );
        // logShapes();
        break;
      }

      case 'SELECTION_TOGGLED': {
        const { shapeId, id, selected } = data.payload;
        // TODO use nested signal
        const shape = state.scene.shapes[ shapeId ];
        const instances = shape .instances.map( inst => (
          inst.id !== id ? inst : { ...inst, selected }
        ));
        const shapes = { ...state.scene.shapes, [ shapeId ]: { ...shape, instances } };
        setState( { scene: { ...state.scene, shapes }, waiting: false } );
        // TODO lower ambient light if anything is selected
        break;
      }

      case 'CONTROLLER_CREATED':
        setState( {
          ...initialState(),
          workerReady: true,
          controller: { __store: store, __path: [] }
        } );
        break;
    
      case 'CONTROLLER_PROPERTY_CHANGED':
        // console.log( JSON.stringify( data.payload, null, 2 ) );
        const { controllerPath, name, value } = data.payload;
        const names = controllerPath? controllerPath .split( ':' ) : []; // for an empty string, split gives ['']
        // This is awesome; we trivially go from the flat model keyed by controllerPath,
        //   to the nested store model.
        for (let index = 0; index < names.length; index++) { // create the parent controllers
          const prefix = names.slice( 0, index );
          const controllerName = names[ index ];
          const __path = names.slice( 0, index+1 );
          setState.apply( null, [ 'controller', ...prefix, controllerName, { __path } ] );
        }
        setState.apply( null, [ 'controller', ...names, name, value ] );
        break;

      case 'SYMMETRY_CHANGED':
      case 'DESIGN_XML_PARSED':
      case 'LAST_BALL_CREATED':
      case 'DESIGN_XML_SAVED':
        // TODO these are not implemented yet!
        break;
    
      default:
        console.log( 'UNRECOGNIZED message from worker:', data.type );
        break;
    }
  }

  const onWorkerError = error => { console.log( error ); };  // TODO: handle the errors in a way the user can see

  worker .subscribe( { onWorkerMessage, onWorkerError } );

  const isWorkerReady = () => state.workerReady;

  const expectResponse = ( controllerPath, parameters ) =>
  {
    return new Promise( ( resolve, reject ) => {
      const action = "exportText";
      exportPromises[ action ] = { resolve, reject };
      worker .sendToWorker( doControllerAction( controllerPath, action, parameters ) );
    } );
  }

  const subscribeFor = ( type, callback ) =>
  {
    const subscriber = {
      onWorkerError,
      onWorkerMessage: data => {
        if ( type === data.type )
          callback( data.payload );
      }
    }
    worker .subscribe( subscriber )
  }

  const store = {
    postMessage: worker .sendToWorker,
    subscribe: worker .subscribe, subscribeFor,
    isWorkerReady, state, setState, expectResponse,
  }; // needed for every subcontroller

  const rootController = () =>
  {
    if ( ! state.controller ) {
      setState( 'controller', { __store: store, __path: [] } ); // empower every subcontroller to access this store
      worker .sendToWorker( newDesign() );
    }
    return state.controller;
  };

  return { ...store, rootController };
}

const subController = ( parent, key ) =>
{
  if ( ! parent[ key ] ) {
    // The subcontroller does not exist; create it and set its __path array and getStore function
    const __path = [ ...parent.__path, key ];
    parent.__store.setState.apply( null, [ 'controller', ...__path, { __path, __store: parent.__store } ] );
  }
  return parent[ key ];
}

const controllerProperty = ( controller, propName, changeName=propName, isList=false ) =>
{
  // We don't want this to happen just because the property is already defined but false
  if ( typeof controller[ propName ] === 'undefined' ) {
    // The property has never been requested, so we have to make the initial request
    const controllerPath = controller.__path .join( ':' );
    createEffect( () => {
      // This is reactive code, so it should get recomputed
      if ( controller.__store.isWorkerReady() ) {
        controller.__store.postMessage( requestControllerProperty( controllerPath, propName, changeName, isList ) );
      }
    });
  }
  if ( isList )
    return controller[ propName ] || [];
  return controller[ propName ];
}

const controllerAction = ( controller, action, parameters ) =>
{
  const controllerPath = controller.__path .join( ':' );
  if ( action === 'setProperty' ) {
    const { name, value } = parameters;
    controller.__store.postMessage( setControllerProperty( controllerPath, name, value ) );
  }
  else
    controller.__store.postMessage( doControllerAction( controllerPath, action, parameters ) );
}

const controllerExportAction = ( controller, format, parameters={} ) =>
{
  const controllerPath = controller.__path .join( ':' );
  parameters.format = format;
  return controller.__store .expectResponse( controllerPath, parameters );
}

export { createWorkerStore, subController, controllerProperty, controllerAction, controllerExportAction };