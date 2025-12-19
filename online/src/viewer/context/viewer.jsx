
import { createContext, createSignal, useContext } from "solid-js";
import { createStore, reconcile, unwrap } from "solid-js/store";

import { useWorkerClient } from "./worker.jsx";
import { INITIAL_DISTANCE, useCamera } from "./camera.jsx";
import { decodeEntities, fetchDesign, requestExport, openTextContent } from "../util/actions.js";

const ViewerContext = createContext( { scene: ()=> { console.log( 'NO ViewerProvider' ); }, problem: ()=>null } );

const ViewerProvider = ( props ) =>
{
  const { url, labels: showLabels } = props.config || {};
  const [ reload, setReload ] = createSignal( true );
  const [ scenes, setScenes ] = createStore( [] );
  const [ source, setSource ] = createStore( {} );
  const [ problem, setProblem ] = createSignal( '' ); // cooperatively managed by both worker and client
  const [ waiting, setWaiting ] = createSignal( false );
  const [ labels, setLabels ] = createSignal( showLabels );
  const { postMessage, subscribeFor, postRequest } = useWorkerClient();
  const { state, setTweenDuration, tweenCamera, setLighting, mapViewToWorld } = useCamera();

  const requestDesign = ( url, config ) =>
  {
    setWaiting( true );
    config.snapshot = -1;
    postMessage( fetchDesign( url, config ) );
  }

  const openText = ( name, contents ) =>
  {
    setWaiting( true );
    postMessage( openTextContent( name, contents ) );
  }

  url && postMessage( fetchDesign( url, props.config ) );

  let exportPromise = null;

  const exportAs = async ( format, params={} ) =>
  {
    const camera = unwrap( state.camera );
    camera .magnification = Math.log( camera.distance / INITIAL_DISTANCE );

    const lighting = unwrap( state.lighting );
    lighting .directionalLights .forEach( light => light .worldDirection = mapViewToWorld( light.direction ) );

    return new Promise( ( resolve, reject ) => {
      exportPromise = { resolve, reject };
      postMessage( requestExport( format, { ...params, camera, lighting, scenes: unwrap( scenes ), } ) );
    } );
  }
  
  subscribeFor( 'TEXT_EXPORTED', ( { text } ) => {
    if ( exportPromise ) {
      exportPromise .resolve( text );
      exportPromise = null;
    }
  } );

  subscribeFor( 'SCENES_DISCOVERED', ( { lighting, scenes } ) => {
    setWaiting( false );
    const newScenes = scenes .map( scene => ({ ...scene, title: decodeEntities( scene.title ), content: decodeEntities( scene.content || ' ' ) }));
    setScenes( newScenes );
    if ( !! lighting ) { // lighting only present in loaded designs
      const { backgroundColor } = lighting;
      setLighting( { ...state.lighting, backgroundColor } ); // no per-scene lighting yet
    }
    const camera = scenes[ 0 ].camera;
    if ( !! camera ) // camera only present in loaded designs
      tweenCamera( camera );
  });
  
  subscribeFor( 'TEXT_FETCHED', ( source ) => {
    setSource( source );
  } );
  
  subscribeFor( 'ALERT_RAISED', ( problem ) => {
    if ( exportPromise ) {
      exportPromise .reject( problem );
      exportPromise = null;
    } else {
      setProblem( problem );
    }
    setWaiting( false );
  } );
      
  const providerValue = {
    requestDesign, openText, scenes, source, setSource, problem, waiting, setWaiting, labels,
    reload, setReload,
    setScenes,
    resetScenes: () => setScenes( [] ),
    setProblem,
    clearProblem: () => setProblem( '' ),
    requestBOM: () => postMessage( { type: 'BOM_REQUESTED' } ),
    setTweenDuration,
    useWorker: useWorkerClient,
    exportAs,
  };

  return (
    <ViewerContext.Provider value={ providerValue }>
      {props.children}
    </ViewerContext.Provider>
  );
}

const useViewer = () => { return useContext( ViewerContext ); };

export { ViewerProvider, useViewer };
