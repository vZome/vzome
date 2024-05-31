
import { createContext, createEffect, createSignal, useContext } from "solid-js";
import { createStore, unwrap } from "solid-js/store";

import * as actions from '../util/actions.js';
import { Guardrail } from "../guardrail.jsx";
import { defaultCamera, useCamera } from "./camera.jsx";
import { useWorkerClient } from "./worker.jsx";
import { serializeVZomeXml } from "../util/serializer.js";
import { useImageCapture } from "./export.jsx";

const initialState = () => ( {
  copiedCamera: defaultCamera(), // TODO: this is probably too static, not related to useCamera
} );

const EditorContext = createContext( {} );

const useEditor = () => { return useContext( EditorContext ); };

const EditorProvider = props =>
{
  const workerClient = useWorkerClient();
  const { resetCamera, state: cameraState } = useCamera();
  const { capturer } = useImageCapture();
  // Beware, createStore does not make a copy, shallow or deep!
  const [ state, setState ] = createStore( { ...initialState() } );

  const [ showGuardrail, setShowGuardrail ] = createSignal( false );
  let continuation;
  const guard = guardedAction =>
  {
    if ( edited() ) {
      continuation = guardedAction;
      setShowGuardrail( true );
    }
    else
      guardedAction();
  }
  const closeGuardrail = continued =>
  {
    setShowGuardrail( false );
    if ( continued )
      continuation();
    continuation = undefined;
  }

  const exportPromises = {};

  const onWorkerMessage = ( data ) =>
  {
    // console.log( `FROM worker: ${JSON.stringify( data.type, null, 2 )}` );

    switch ( data.type ) {

      case 'SHARE_SUCCESS': {
        exportPromises[ 'shareToGitHub' ] .resolve( data.payload );
        break;
      }

      case 'SHARE_FAILURE': {
        exportPromises[ 'shareToGitHub' ] .reject( data.payload );
        break;
      }

      case 'TEXT_EXPORTED': {
        const { action, text } = data.payload;
        exportPromises[ action ] .resolve( text );
        break;
      }

      case 'TEXT_FETCHED': { // we receive this event twice per fetch
        let { name } = data.payload;
        if ( name && name .endsWith( '.vZome' ) ) {
          if ( !state.ignoreDesignName ) {
            name = name .substring( 0, name.length - 6 );
            setState( 'designName', name ); // cooperatively managed by both worker and client
            setState( 'ignoreDesignName', false );  // always reset to use the next name; see app/classic/menus/help.jsx
          }
        }
        break;
      }

      case 'SCENE_RENDERED': {
        // TODO: I wish I had a better before/after contract with the worker
        const { edit } = data.payload;
        setState( 'edit', edit );
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
      case 'INSTANCE_ADDED':
      case 'INSTANCE_REMOVED':
      case 'FETCH_STARTED':
      case 'TRACKBALL_SCENE_LOADED':
      case 'SCENES_DISCOVERED':
      case 'CAMERA_SNAPPED':
        // TODO: do these require any state changes?
        break;
    
      default:
        console.log( 'UNRECOGNIZED message from worker for EditorProvider:', data.type );
        break;
    }
  }

  const onWorkerError = (error) =>
  {
    console.log( error );   // TODO: handle the errors in a way the user can see
  }

  workerClient .subscribe( { onWorkerMessage, onWorkerError } );

  const expectResponse = ( controllerPath, action, parameters ) =>
  {
    return new Promise( ( resolve, reject ) => {
      exportPromises[ action ] = { resolve, reject };
      workerClient .postMessage( actions.doControllerAction( controllerPath, action, parameters ) );
    } );
  }

  const store = { state, setState, expectResponse, }; // needed for every subcontroller

  const rootController = () =>
  {
    if ( ! state.controller ) {
      setState( 'controller', { __store: store, __path: [] } ); // empower every subcontroller to access this store
      workerClient .postMessage( actions.newDesign() );
    }
    return state.controller;
  };

  const controllerAction = ( controller, action, parameters ) =>
  {
    const controllerPath = controller.__path .join( ':' );
    if ( action === 'setProperty' ) {
      const { name, value } = parameters;
      workerClient.postMessage( actions.setControllerProperty( controllerPath, name, value ) );
    }
    else
      workerClient.postMessage( actions.doControllerAction( controllerPath, action, parameters ) );
  }

  const createDesign = ( field ) =>
  {
    resetCamera();
    workerClient .postMessage( actions.newDesign( field ) );
  }

  const indexResources = () => workerClient .postMessage( { type: 'WINDOW_LOCATION', payload: window.location.toString() } );

  const edited = () => controllerProperty( rootController(), 'edited' ) === 'true';
  
  const shareToGitHub = ( token ) =>
  {
    const { capture } = capturer();  
    const name = state?.designName || 'untitled';
    return new Promise( ( resolve, reject ) =>
    {
      Promise.all( [
        controllerExportAction( rootController(), 'vZome' ),
        new Promise((resolve, _) => {
          const reader = new FileReader();
          reader .onloadend = () => resolve( reader.result );
          capture( 'image/png', blob => reader .readAsDataURL( blob ) );
        })
      ] )
        .then( ([ coreXml, imageDataUrl ]) => {
          const image = imageDataUrl .substring( 22 ); // remove "data:image/png;base64,"
          const { camera, lighting } = unwrap( cameraState );
          const text = serializeVZomeXml( coreXml, lighting, camera );
          expectResponse( '', 'shareToGitHub', { token, name, text, image, camera, lighting } )
            .then( url => resolve( url ) )
            .catch( error => reject( error ) );
        })
        .catch( error => reject( error ) );
    } );
  }

  const providerValue = {
    ...store,
    guard, edited,
    rootController,
    indexResources,
    controllerAction,
    shareToGitHub,
    createDesign,
    openDesignFile: ( file, debug )  => workerClient .postMessage( actions.openDesignFile( file, debug ) ),
    fetchDesignUrl: ( url, config )  => workerClient .postMessage( actions.fetchDesign( url, config ) ),
    importMeshFile: ( file, format ) => workerClient .postMessage( actions.importMeshFile( file, format ) ),
    startPreviewStrut: ( id, dir )   => workerClient .postMessage( actions.startPreviewStrut( id, dir ) ),
    movePreviewStrut:  ( direction ) => workerClient .postMessage( actions.movePreviewStrut( direction ) ),
    endPreviewStrut:   ()            => workerClient .postMessage( actions.endPreviewStrut() ),
  };

  return (
    <EditorContext.Provider value={ providerValue } >
      <Guardrail show={showGuardrail()} close={closeGuardrail} />
      {props.children}
    </EditorContext.Provider>
  );
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
    const workerClient = useWorkerClient();
    createEffect( () => {
      // This is reactive code, so it should get recomputed
      if ( workerClient.isWorkerReady() ) {
        workerClient.postMessage( actions.requestControllerProperty( controllerPath, propName, changeName, isList ) );
      }
    });
  }
  if ( isList )
    return controller[ propName ] || [];
  return controller[ propName ];
}

const controllerExportAction = ( controller, format, parameters={} ) =>
{
  const controllerPath = controller.__path .join( ':' );
  parameters.format = format;
  return controller.__store .expectResponse( controllerPath, "exportText", parameters );
}

export { EditorProvider, useEditor, subController, controllerProperty, controllerExportAction };