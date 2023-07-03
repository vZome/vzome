
import { createEffect } from "solid-js";
import { createStore, reconcile } from "solid-js/store";

import { initialState, newDesign, requestControllerProperty, doControllerAction } from './actions.js';

const createWorkerStore = ( worker ) =>
{
  const { camera, lighting } = initialState.scene;
  // Beware, createStore does not make a copy, shallow or deep!
  const [ state, setState ] = createStore( { scene: { camera: { ...camera }, lighting: { ...lighting } }, uuid: worker.uuid } );

  const exportPromises = {};

  const addShape = shape =>
  {
    if ( ! state.scene.shapes ) {
      setState( 'scene', 'shapes', {} );
    }
    if ( ! state.scene?.shapes[ shape.id ] ) {
      setState( 'scene', 'shapes', shape.id, shape );
      return true;
    }
    return false;
  }

  const updateShapes = shapes =>
  {
    for (const [id, shape] of Object.entries(shapes)) {
      if ( ! addShape( shape ) ) {
        // shape is not new, so just replace its instances
        setState( 'scene', 'shapes', id, 'instances', shape.instances );
      }
    }
    // clean up preview strut, which may be a shape otherwise not in the scene
    for ( const id of Object.keys( state.scene.shapes ) ) {
      if ( ! (id in shapes) )
        setState( 'scene', 'shapes', id, 'instances', [] );
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
        setState( 'problem', data.payload );
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
        setState( 'source', data.payload );
        break;
      }

      case 'SCENES_DISCOVERED': {
        setState( 'scenes', data.payload );
        break;
      }
  
      case 'SCENE_RENDERED': {
        // TODO: I wish I had a better before/after contract with the worker
        const { scene, edit } = data.payload;

        if ( scene.camera ) {
          // NOTE: if there is a camera in this scene (first load of existing design, or we requested an article scene,
          //   or we are previewing), it will replace the existing camera, and rendering will reflect it.
          //   For edit responses, there should never be a camera.  This all goes for lighting as well.
          console.log( 'CAMERA FROM WORKER' );
          setState( 'scene', 'camera', scene.camera );
          setState( 'scene', 'lighting', scene.lighting );
        }
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
        break;
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

  const store = { postMessage: worker .sendToWorker, isWorkerReady, state, setState, expectResponse }; // needed for every subcontroller

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