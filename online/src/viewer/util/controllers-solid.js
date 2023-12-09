
import { createEffect } from "solid-js";
import { createStore } from "solid-js/store";

import { newDesign, requestControllerProperty, doControllerAction, setControllerProperty, decodeEntities } from './actions.js';
import { defaultCamera } from "../context/camera.jsx";

const initialState = () => ( {
  copiedCamera: defaultCamera(), // TODO: this is probably too static, not related to useCamera
} );

const createWorkerStore = ( worker ) =>
{
  // Beware, createStore does not make a copy, shallow or deep!
  const [ state, setState ] = createStore( { ...initialState(), uuid: worker.uuid } );

  const exportPromises = {};

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
        const { edit } = data.payload;
        setState( 'edit', edit );
        setState( 'waiting', false );
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