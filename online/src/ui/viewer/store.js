
export const initialState = {
  controller: { __path: [] },
  scene: {
    camera: {
      near: 0.1,
      far: 80,
      width: 18,
      distance: 40,
      up: [ 0, 1, 0 ],
      lookAt: [ 0, 0, 0 ],
      lookDir: [ 0, 0, -1 ],
      perspective: true,
    }
  }
};

 
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

const workerAction = ( type, payload ) => ({ type, payload, meta: 'WORKER' } );

export const selectScene = index => workerAction( 'SCENE_SELECTED', index );
export const selectEditBefore = nodeId => workerAction( 'EDIT_SELECTED', { before: nodeId } );
export const selectEditAfter = nodeId => workerAction( 'EDIT_SELECTED', { after: nodeId } );
export const fetchDesign = ( url, config={ preview: false, debug: false, showScenes: false } ) => workerAction( 'URL_PROVIDED', { url, config } );
export const openDesignFile = ( file, debug=false ) => workerAction( 'FILE_PROVIDED', { file, debug } );
export const newDesign = () => workerAction( 'NEW_DESIGN_STARTED', { field: 'golden' } );
export const doControllerAction = ( controllerPath='', action, parameters={} ) => workerAction( 'ACTION_TRIGGERED', { controllerPath, action, parameters } );
export const requestControllerProperty = ( controllerPath='', propName, changeName, isList ) => workerAction( 'PROPERTY_REQUESTED', { controllerPath, propName, changeName, isList } );

export const subcontroller = ( controllerPath, subName ) => controllerPath + ':' + subName;

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

    case 'DESIGN_INTERPRETED': {
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
      // may need to merge scene.shapes here, if we ever have an incremental case
      return { ...state, edit, scene: { ...state.scene, ...scene }, waiting: false };
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
      const trackball = event.payload;
      return { ...state, scene: { ...state.scene, trackball } };
    }

    case 'CONTROLLER_CREATED': {
      return { ...state, controller: { isReady: true } };
    }

    case 'CONTROLLER_PROPERTY_CHANGED': {
      const { controllerPath, name, value } = event.payload;
      return { ...state, controller: { ...state.controller, [ subcontroller( controllerPath, name ) ]: value } };
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

let workerPromise;
const workerSubscribers = [];

export const postWorkerMessage = msg =>
{
  workerPromise .then( worker => {
    worker.postMessage( msg );
  } );
}

export const addWorkerSubscriber = subscriber => workerSubscribers .push( subscriber );

export const createWorkerStore = customElement =>
{
  // trampolining to work around worker CORS issue
  // see https://github.com/evanw/esbuild/issues/312#issuecomment-1025066671
  workerPromise = import( "../../worker/vzome-worker-static.js" )
    .then( module => {
      const blob = new Blob( [ `import "${module.WORKER_ENTRY_FILE_URL}";` ], { type: "text/javascript" } );
      const worker = new Worker( URL.createObjectURL( blob ), { type: "module" } );
      worker.onmessage = onWorkerMessage;
      return worker;
    } );

  const workerSender = store => report => event =>
  {
    if ( event.meta && event.meta === 'WORKER' ) {
      if ( navigator.userAgent.indexOf( "Firefox" ) > -1 ) {
          console.log( "The worker is not available in Firefox" );
          report( { type: 'ALERT_RAISED', payload: 'Module workers are not yet supported in Firefox.  Please try another browser.' } );
      }
      else {
        workerPromise.then( worker => {
          // console.log( `Message sending to worker: ${JSON.stringify( event, null, 2 )}` );
          worker.postMessage( event );  // send them all, let the worker filter them out
        } )
        .catch( error => {
          console.log( "The worker is not available" );
          report( { type: 'ALERT_RAISED', payload: 'The worker is not available.  Module workers are supported in newer versions of most browsers.  Please update your browser.' } );
        } );
      }
    }
    else
        report( event );
  }

  const preloadedState = initialState

  const store = configureStore( {
    reducer,
    preloadedState,
    middleware: getDefaultMiddleware => getDefaultMiddleware().concat( workerSender ),
    devTools: true,
  });

  const onWorkerMessage = ({ data }) => {
    // console.log( `Message received from worker: ${JSON.stringify( data.type, null, 2 )}` );

    store .dispatch( data );  // forward to the reducer(s)

    for (const subscriber of workerSubscribers) {
      subscriber( data );
    }

    // Useful for supporting regression testing of the vzome-viewer web component
    if ( customElement ) {
      switch ( data.type ) {

        case 'DESIGN_INTERPRETED':
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
