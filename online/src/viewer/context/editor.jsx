
import { createContext, createEffect, useContext } from "solid-js";
import { createStore } from "solid-js/store";

import * as actions from '../util/actions.js';
import { defaultCamera, useCamera } from "./camera.jsx";
import { useWorkerClient } from "./worker.jsx";

const initialState = () => ( {
  copiedCamera: defaultCamera(), // TODO: this is probably too static, not related to useCamera
} );

const EditorContext = createContext( {} );

const useEditor = () => { return useContext( EditorContext ); };

const EditorProvider = props =>
{
  const workerClient = useWorkerClient();
  const { resetCamera } = useCamera();
  // Beware, createStore does not make a copy, shallow or deep!
  const [ state, setState ] = createStore( { ...initialState() } );

  const exportPromises = {};

  const onWorkerMessage = ( data ) =>
  {
    // console.log( `FROM worker: ${JSON.stringify( data.type, null, 2 )}` );

    switch ( data.type ) {

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
      case 'DESIGN_XML_SAVED':
        // TODO these are not implemented yet!
        break;
    
      default:
        console.log( 'UNRECOGNIZED message from worker:', data.type );
        break;
    }
  }

  const onWorkerError = () =>
  {
    console.log( error );   // TODO: handle the errors in a way the user can see
  }

  workerClient .subscribe( { onWorkerMessage, onWorkerError } );

  const expectResponse = ( controllerPath, parameters ) =>
  {
    return new Promise( ( resolve, reject ) => {
      const action = "exportText";
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

  const providerValue = {
    ...store,
    rootController,
    indexResources,
    controllerAction,
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
  return controller.__store .expectResponse( controllerPath, parameters );
}

export { EditorProvider, useEditor, subController, controllerProperty, controllerExportAction };