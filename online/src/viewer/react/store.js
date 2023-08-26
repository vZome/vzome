
import { configureStore } from '@reduxjs/toolkit'
import { createWorker, initialState } from '../../workerClient/index.js';
 
export const defineCamera = camera => ({ type: 'CAMERA_DEFINED', payload: camera });
export const setPerspective = value => ({ type: 'PERSPECTIVE_SET', payload: value });

export const whilePerspective = ( perspective, doSetter ) => async dispatch =>
{
  const wait = ms => new Promise( (resolve) => setTimeout(resolve, 100) );
  dispatch( setPerspective( true ) );
  await wait( 10 );
  doSetter();
  await wait( 10 );
  dispatch( setPerspective( perspective ) );
}

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
      const { url, preview } = event.payload;
      return { ...state, waiting: true, editing: !preview };
    }

    case 'TEXT_FETCHED':
      return { ...state, source: event.payload };

    case 'DESIGN_XML_SAVED': {
      return { ...state, source: { ...state.source, changedText: event.payload } };
    }

    case 'DESIGN_XML_PARSED': {
      let xmlTree = event.payload;
      const attributes = {};
      const indexAttributes = node => node.children && node.children.map( child => {
        attributes[ child.id ] = child.attributes;
        indexAttributes( child );
      })
      if ( xmlTree ) {
        indexAttributes( xmlTree );
        xmlTree = branchSelectionBlocks( xmlTree );
      }
      return { ...state, waiting: false, xmlTree, attributes };
    }

    case 'SCENES_DISCOVERED': {
      return { ...state, scenes: event.payload };
    }

    case 'SCENE_RENDERED': {
      // TODO: I wish I had a better before/after contract with the worker
      const { scene, edit } = event.payload;
      const camera = scene.camera || state.scene.camera;
      // may need to merge scene.shapes here, if we ever have an incremental case
      return { ...state, edit, scene: { ...state.scene, ...scene, camera }, waiting: false };
    }

    case 'SHAPE_DEFINED': {
      const shape = event.payload;
      const shapes = { ...state.scene.shapes, [ shape.id ]: shape };
      return { ...state, scene: { ...state.scene, shapes }, waiting: false };
      break;
    }

    case 'INSTANCE_ADDED': {
      let instance = event.payload;
      //  TODO: put this granularity in when I've switched to SolidJS for the rendering
      // const [ selected, setSelected ] = createSignal( instance.selected );
      // const [ color, setColor ] = createSignal( instance.color );
      // instance = { ...instance, color, setColor, selected, setSelected };
      const shape = state.scene.shapes[ instance.shapeId ];
      const shapes = { ...state.scene.shapes, [ shape.id ]: { ...shape, instances: [ ...shape.instances, instance ] } };
      return { ...state, scene: { ...state.scene, shapes }, waiting: false, lastInstance: instance };
      break;
    }

    case 'SELECTION_TOGGLED': {
      const { shapeId, id, selected } = event.payload;
      const shape = state.scene.shapes[ shapeId ];
      const instances = shape .instances.map( inst => (
        inst.id !== id ? inst : { ...inst, selected }
      ));
      const shapes = { ...state.scene.shapes, [ shapeId ]: { ...shape, instances } };
      return { ...state, scene: { ...state.scene, shapes }, waiting: false };
    }

    case 'CAMERA_DEFINED': {
      const camera = event.payload;
      return { ...state, scene: { ...state.scene, camera } };
    }

    case 'PERSPECTIVE_SET': {
      const perspective = event.payload;
      return { ...state, scene: { ...state.scene, camera: { ...state.scene.camera, ...state.scene.trackball, perspective } } };
    }

    case 'TRACKBALL_MOVED': {
      const liveCamera = { ...state.scene.camera, ...event.payload };
      return { ...state, scene: { ...state.scene, liveCamera } };
    }

    case 'CONTROLLER_CREATED': {
      return { ...state, controller: { isReady: true } };
    }

    default:
      return state;
  }
};

const branchSelectionBlocks = node =>
{
  if ( node.children && node.children.length > 1 ) {
    const newChildren = [];
    let block = null;
    for (const child of node.children) {
      if ( child.tagName === 'BeginBlock' ) {
        block = [];
        // discard this BeginBlock node (don't push it)
      } else if ( child.tagName === 'EndBlock' ) {
        const newNode = { tagName: 'ChangeSelection', id: child.id, children: block, attributes: {} };
        newChildren.push( newNode );
        block = null;
      } else if ( block ) {
        block.push( child ); // child can't be a branch inside a block
      } else {
        newChildren.push( branchSelectionBlocks( child ) );
      }
    }
    node.children = newChildren;
    return node;
  }
  else
    return node;
}

// TODO: Honestly, we don't really need to use the store to dispatch to the worker any more,
//   if we replace all those uses of dispatch() with sendToWorker().
//   All we need is the store to subscribe to the worker.
export const createWorkerStore = ( worker ) =>
{
  const workerSender = store => report =>
  {
    const onWorkerError = error =>
      report( { type: 'ALERT_RAISED', payload: error } );
    const onWorkerMessage = message =>
      report( message );
    const workerClient = worker || createWorker();
    workerClient .subscribe( { onWorkerMessage, onWorkerError } );

    const handleEvent = event =>
    {
      if ( event.meta && event.meta === 'WORKER' ) {
        workerClient .sendToWorker( event );
      }
      else
          report( event );
    }

    return handleEvent;
  }

  const preloadedState = initialState;

  const store = configureStore( {
    reducer,
    preloadedState,
    middleware: getDefaultMiddleware => getDefaultMiddleware( { immutableCheck: false } ).concat( workerSender ),
    devTools: true,
  });

  return store;
}
