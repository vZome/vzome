
import { createEffect } from "solid-js";
import { createStore } from "solid-js/store";

import { initialState, newDesign, requestControllerProperty, doControllerAction } from './actions.js';

const createWorkerStore = ( worker ) =>
{
  const [ state, setState ] = createStore( initialState );

  const exportPromises = {};

  const onWorkerMessage = ( data ) => {
    // console.log( `Message received from worker: ${JSON.stringify( data.type, null, 2 )}` );

    switch ( data.type ) {

      case 'TEXT_EXPORTED': {
        const { action, text } = data.payload;
        exportPromises[ action ] .resolve( text );
        break;
      }

      case 'TEXT_FETCHED': {
        setState( { controller: { ...state.controller, source: data.payload } } );
        break;
      }

      case 'SCENE_RENDERED': {
        // TODO: I wish I had a better before/after contract with the worker
        const { scene, edit } = data.payload;
        const camera = scene.camera || state.scene.camera;
        setState( { edit, scene: { ...state.scene, ...scene, camera }, waiting: false } );
        break;
      }

      case 'SHAPE_DEFINED': {
        const shape = data.payload;
        const shapes = { ...state.scene.shapes, [ shape.id ]: shape };
        setState( { scene: { ...state.scene, shapes }, waiting: false } );
        break;
      }

      case 'INSTANCE_ADDED': {
        let instance = data.payload;
        //  TODO: put this granularity in when I've switched to SolidJS for the rendering
        // const [ selected, setSelected ] = createSignal( instance.selected );
        // const [ color, setColor ] = createSignal( instance.color );
        // instance = { ...instance, color, setColor, selected, setSelected };
        const shape = state.scene.shapes[ instance.shapeId ];
        const shapes = { ...state.scene.shapes, [ shape.id ]: { ...shape, instances: [ ...shape.instances, instance ] } };
        setState( { scene: { ...state.scene, shapes }, waiting: false } );
        break;
      }

      case 'SELECTION_TOGGLED': {
        const { shapeId, id, selected } = data.payload;
        const shape = state.scene.shapes[ shapeId ];
        const instances = shape .instances.map( inst => (
          inst.id !== id ? inst : { ...inst, selected }
        ));
        const shapes = { ...state.scene.shapes, [ shapeId ]: { ...shape, instances } };
        setState( { scene: { ...state.scene, shapes }, waiting: false } );
      }

      case 'CONTROLLER_CREATED':
        setState( { workerReady: true, controller: { __store: store, __path: [] } } );
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
    
      default:
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

  const store = { postMessage: worker .sendToWorker, isWorkerReady, setState, expectResponse }; // needed for every subcontroller

  const rootController = () =>
  {
    if ( ! state.controller ) {
      setState( 'controller', { __store: store, __path: [] } ); // empower every subcontroller to access this store
      worker .sendToWorker( newDesign() );
    }
    return state.controller;
  };

  const getScene = () => state.scene;

  return { ...store, rootController, getScene };
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
  if ( ! controller[ propName ] ) {
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
  controller.__store.postMessage( doControllerAction( controllerPath, action, parameters ) );
}

const controllerExportAction = ( controller, format, parameters={} ) =>
{
  const controllerPath = controller.__path .join( ':' );
  parameters.format = format;
  return controller.__store .expectResponse( controllerPath, parameters );
}

export { createWorkerStore, subController, controllerProperty, controllerAction, controllerExportAction };