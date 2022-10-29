
import { createEffect } from "solid-js";
import { createStore } from "solid-js/store";

import { initialState, newDesign, requestControllerProperty, doControllerAction } from '../../ui/viewer/store.js';

export const createWorkerStore = () =>
{
  const [ state, setState ] = createStore( initialState );

  const onWorkerMessage = ({ data }) => {
    // console.log( `Message received from worker: ${JSON.stringify( data.type, null, 2 )}` );

    switch ( data.type ) {

      case 'SCENE_RENDERED': {
        // TODO: I wish I had a better before/after contract with the worker
        const { scene, edit } = data.payload;
        setState( { edit, scene: { ...state.scene, ...scene }, waiting: false } );
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
        setState( { workerReady: true } );
        break;
    
      case 'CONTROLLER_PROPERTY_CHANGED':
        // console.log( JSON.stringify( e.data, null, 2 ) );
        const { controllerPath, name, value } = data.payload;
        const names = controllerPath .split( ':' );
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

    // Useful for supporting regression testing of the vzome-viewer web component
    // if ( customElement ) {
    //   switch ( data.type ) {

    //     case 'DESIGN_INTERPRETED':
    //       customElement.dispatchEvent( new Event( 'vzome-design-rendered' ) );
    //       break;
      
    //     case 'ALERT_RAISED':
    //       customElement.dispatchEvent( new Event( 'vzome-design-failed' ) );
    //       break;
      
    //     default:
    //       break;
    //   }
    // }
  }

  // trampolining to work around worker CORS issue
  // see https://github.com/evanw/esbuild/issues/312#issuecomment-1025066671
  const workerPromise = import( "../../worker/vzome-worker-static.js" )
    .then( module => {
      const blob = new Blob( [ `import "${module.WORKER_ENTRY_FILE_URL}";` ], { type: "text/javascript" } );
      const worker = new Worker( URL.createObjectURL( blob ), { type: "module" } );
      worker.onmessage = onWorkerMessage;
      return worker;
    } );

  const postMessage = event =>
  {
    if ( navigator.userAgent.indexOf( "Firefox" ) > -1 ) {
      console.log( "The worker is not available in Firefox" );
      // report( { type: 'ALERT_RAISED', payload: 'Module workers are not yet supported in Firefox.  Please try another browser.' } );
    }
    else {
      workerPromise.then( worker => {
        // console.log( `Message sending to worker: ${JSON.stringify( event, null, 2 )}` );
        worker.postMessage( event );  // send them all, let the worker filter them out
      } )
      .catch( error => {
        console.log( "The worker is not available" );
        // report( { type: 'ALERT_RAISED', payload: 'The worker is not available.  Module workers are supported in newer versions of most browsers.  Please update your browser.' } );
      } );
    }
  }

  const isWorkerReady = () => state.workerReady;

  const store = { postMessage, isWorkerReady, setState }; // needed for every subcontroller

  const rootController = () =>
  {
    if ( ! state.controller ) {
      setState( 'controller', { __store: store, __path: [] } ); // empower every subcontroller to access this store
      postMessage( newDesign() );
    }
    return state.controller;
  };

  const getScene = () => state.scene;

  return { ...store, rootController, getScene };
}

// createEffect( () => {
//   console.log( JSON.stringify( state, null, 2 ));
// });


export const subController = ( parent, key ) =>
{
  if ( ! parent[ key ] ) {
    // The subcontroller does not exist; create it and set its __path array and getStore function
    const __path = [ ...parent.__path, key ];
    parent.__store.setState.apply( null, [ 'controller', ...__path, { __path, __store: parent.__store } ] );
  }
  return parent[ key ];
}

export const controllerProperty = ( controller, propName, changeName, isList ) =>
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

export const controllerAction = ( controller, action, parameters ) =>
{
  const controllerPath = controller.__path .join( ':' );
  controller.__store.postMessage( doControllerAction( controllerPath, action, parameters ) );
}
