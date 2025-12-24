
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
  const { postMessage, subscribeFor } = useWorkerClient();
  const { state, setTweenDuration, tweenCamera, setLighting, mapViewToWorld } = useCamera();

  const indexResources = () => postMessage( { type: 'WINDOW_LOCATION', payload: window.location.toString() } );
  
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
    exportAs, indexResources,
  };

  return (
    <ViewerContext.Provider value={ providerValue }>
      {props.children}
    </ViewerContext.Provider>
  );
}

const useViewer = () => { return useContext( ViewerContext ); };

export const EXPORT_FORMATS = {

  // Key values are as defined in desktop vZome code.

  // 3D Rendering
  dae:       { label: 'Collada (DAE)',     mime: 'model/vnd.collada+xml' },
  gltf:      { label: 'glTF',              mime: 'model/gltf+json' },
  pov:       { label: 'POV-Ray',           mime: 'text/plain' },
  shapes:    { label: 'vZome Shapes JSON', mime: 'application/json',           ext: "shapes.json" },
  vrml:      { label: 'VRML',              mime: 'model/vrml' },

  // 3D Panels
  stl:       { label: 'StL (mm)',          mime: 'model/stl' },
  off:       { label: 'OFF',               mime: 'text/plain' },
  ply:       { label: 'PLY',               mime: 'text/plain' },
  step:      { label: 'STEP',              mime: 'application/STEP' },

  // 3D Points & Lines
  mesh:      { label: 'Simple Mesh JSON',  mime: 'application/json',     ext: "mesh.json" },
  cmesh:     { label: 'Colored Mesh JSON', mime: 'application/json',     ext: "cmesh.json" },
  dxf:       { label: 'AutoCAD DXF',       mime: 'application/dxf' },

  // 3D Balls & Sticks
  scad:      { label: 'OpenSCAD',          mime: 'text/plain' },
  build123d: { label: 'Build123d Python',  mime: 'text/x-python',        ext: "py" },

  // 2D Vector Drawing
  svg:       { label: 'SVG',          mime: 'image/svg+xml' },
  pdf:       { label: 'PDF',          mime: 'application/pdf' },
  ps:        { label: 'Postscript',   mime: 'application/postscript' },

  // Image
  png:       { image: true, label: 'PNG',  mime: 'image/png' },
  jpg:       { image: true, label: 'JPG',  mime: 'image/jpeg' },
  webp:      { image: true, label: 'WEBP', mime: 'image/webp' },
  bmp:       { image: true, label: 'BMP',  mime: 'image/bmp' },
}

export { ViewerProvider, useViewer };
