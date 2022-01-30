
import { configureStore } from '@reduxjs/toolkit'

export const initialState = {};

const reducer = ( state = initialState, event ) =>
{
  switch ( event.type ) {

    case 'ALERT_RAISED':
      console.log( `Alert from the worker: ${event.payload}` );
      return { ...state, problem: event.payload, waiting: false };

    case 'ALERT_DISMISSED':
      return { ...state, problem: '' };

    case 'FETCH_STARTED': {
      if ( state.waiting )
        return state; // the fetch was started already
      const { url, viewOnly } = event.payload;
      return { ...state, waiting: true, editing: !viewOnly };
    }

    case 'TEXT_FETCHED':
      return { ...state, source: event.payload };

    case 'DESIGN_RENDERED': {
      const { scene, xmlTree } = event.payload;
      const attributes = {};
      const index = node => node.children && node.children.map( child => {
        attributes[ child.id ] = child.attributes;
        index( child );
      })
      if ( xmlTree ) index( xmlTree );
      // may need to merge scene.shapes here, for incremental case
      return { ...state, scene: { ...state.scene, ...scene }, waiting: false, xmlTree, attributes };
    }

    case 'EDIT_RENDERED': {
      const { scene } = event.payload;
      // may need to merge scene.shapes here, for incremental case
      return { ...state, scene: { ...state.scene, ...scene }, waiting: false };
    }

    default:
      return state;
  }
};


export const createWorkerStore = customElement =>
{
  const worker = new Worker( new URL( '/modules/vzome-worker-static.js', import.meta.url ), { type: 'module' } );

  const workerSender = store => report => event =>
  {
    switch ( event.type ) {

      // These are all coming back from the worker
      case 'ALERT_RAISED':
      case 'ALERT_DISMISSED':
      case 'FETCH_STARTED':
      case 'TEXT_FETCHED':
      case 'DESIGN_RENDERED':
      case 'EDIT_RENDERED':
        report( event );
        break;

      // Anything else is to send to the worker
      default:
        // console.log( `Message sending to worker: ${JSON.stringify( event, null, 2 )}` );
        worker.postMessage( event );  // send them all, let the worker filter them out
        break;    
    }
  }

  const preloadedState = {}

  const store = configureStore( {
    reducer,
    preloadedState,
    middleware: getDefaultMiddleware => getDefaultMiddleware().concat( workerSender ),
    devTools: true,
  });

  worker .onmessage = ({ data }) => {
    // console.log( `Message received from worker: ${JSON.stringify( data.type, null, 2 )}` );
    store .dispatch( data );

    // Useful for supporting regression testing of the vzome-viewer web component
    if ( customElement ) {
      switch (data.type) {

        case 'DESIGN_RENDERED':
          customElement.dispatchEvent( new Event( 'vzome-design-rendered' ) );
          break;
      
        case 'ALERT_RAISED':
          customElement.dispatchEvent( new Event( 'vzome-design-failed' ) );
          break;
      
        default:
          break;
      }
    }
  }

  return store;
}
