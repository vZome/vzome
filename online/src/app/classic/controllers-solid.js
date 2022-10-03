
import { createEffect } from "solid-js";
import { createStore } from "solid-js/store";

import { postWorkerMessage, addWorkerSubscriber } from '../../ui/viewer/store.js';

const [ state, setState ] = createStore({ controller: {} });

addWorkerSubscriber( ( data ) => {
  // console.log( 'Message received from worker: ', JSON.stringify( e.data, null, 2 ));
  switch ( data.type ) {

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
        const __path = names.slice( 0, index+1 ) .join( ':' );
        setState.apply( null, [ 'controller', ...prefix, controllerName, { __path } ] );
      }
      setState.apply( null, [ 'controller', ...names, name, value ] );
      break;
  
    default:
      break;
  }
} );

// TODO: make this more automatic, wrapped in the property accesses somehow
export const requireProperty = ( controllerPath, propName, changeName, isList ) =>
{
  createEffect( () => {
    if ( state.workerReady ) {
      postWorkerMessage( { type: 'PROPERTY_REQUESTED', payload: { controllerPath, propName, changeName, isList } } );
    }
  })
}

export const controllerAction = ( controller, action, parameters={} ) =>
{
  const controllerPath = controller.__path;
  postWorkerMessage( { type: 'ACTION_TRIGGERED', payload: { controllerPath, action, parameters } } );
}

createEffect( () => {
  console.log( JSON.stringify( state, null, 2 ));
});

export const rootController = () => state.controller;
