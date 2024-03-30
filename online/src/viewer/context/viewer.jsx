
import { createContext, createSignal, useContext } from "solid-js";
import { createStore, reconcile } from "solid-js/store";

import { useWorkerClient } from "./worker.jsx";
import { useCamera } from "./camera.jsx";
import { decodeEntities, fetchDesign, selectScene } from "../util/actions.js";

const ViewerContext = createContext( { scene: ()=> { console.log( 'NO ViewerProvider' ); }, problem: ()=>null } );

const ViewerProvider = ( props ) =>
{
  const { url, labels: showLabels } = props.config || {};
  const [ scene, setScene ] = createStore( {} );
  const [ scenes, setScenes ] = createStore( [] );
  const [ source, setSource ] = createStore( {} );
  const [ problem, setProblem ] = createSignal( '' ); // cooperatively managed by both worker and client
  const [ waiting, setWaiting ] = createSignal( false );
  const [ labels, setLabels ] = createSignal( showLabels );
  const { postMessage, subscribeFor } = useWorkerClient();
  const { state, setCamera, setLighting } = useCamera();

  const requestDesign = ( url, config ) =>
  {
    setWaiting( true );
    postMessage( fetchDesign( url, config ) );
  }

  url && postMessage( fetchDesign( url, props.config ) );

  const addShape = ( shape ) =>
  {
    if ( ! scene .shapes ) {
      setScene( 'shapes', {} );
    }
    if ( ! scene ?.shapes[ shape.id ] ) {
      setScene( 'shapes', shape.id, shape );
      return true;
    }
    return false;
  }

  const updateShapes = ( shapes ) =>
  {
    for (const [id, shape] of Object.entries(shapes)) {
      if ( ! addShape( shape ) ) {
        // shape is not new, so just replace its instances
        setScene( 'shapes', id, 'instances', shape.instances );
      }
    }
    // clean up preview strut, which may be a shape otherwise not in the scene
    for ( const id of Object.keys( scene ?.shapes || {} ) ) {
      if ( ! (id in shapes) )
      setScene( 'shapes', id, 'instances', [] );
    }
  }

  const logShapes = () =>
  {
    console.log( 'SHAPES:' );
    Object.values( scene.shapes ) .forEach( shape => {
      console.log( `  ${shape.id} ${shape.zone} [${shape.instances.length}]` );
    })
  }

  subscribeFor( 'SCENES_DISCOVERED', ( payload ) => {
    const newScenes = payload .map( scene => {
      return { ...scene, title: decodeEntities( scene.title ) }
    });
    setScenes( newScenes );
  });

  subscribeFor( 'SCENE_RENDERED', ( { scene } ) => {
    setWaiting( false );
    if ( scene.camera ) {
      setCamera( scene.camera );
    }
    if ( scene.lighting ) {
      const { backgroundColor } = scene.lighting;
      setLighting( { ...state.lighting, backgroundColor } );
    }
    setScene( 'embedding', reconcile( scene.embedding ) );
    setScene( 'polygons', scene.polygons );
    updateShapes( scene.shapes );
    // logShapes();
  } );

  subscribeFor( 'SHAPE_DEFINED', ( shape ) => {
    addShape( shape );
    // logShapes();
  } );

  subscribeFor( 'INSTANCE_ADDED', ( instance ) => {
    const shape = scene.shapes[ instance.shapeId ];
    setScene( 'shapes', shape.id, 'instances', [ ...shape.instances, instance ] );
    // logShapes();
  } );
  
  subscribeFor( 'INSTANCE_REMOVED', ( { shapeId, id } ) => {
    const shape = scene.shapes[ shapeId ];
    const instances = shape.instances .filter( instance => instance.id != id );
    setScene( 'shapes', shape.id, 'instances', instances );
    // logShapes();
  } );
  
  subscribeFor( 'SELECTION_TOGGLED', ( { shapeId, id, selected } ) => {
    // TODO use nested signal
    const shape = scene.shapes[ shapeId ];
    const instances = shape .instances.map( inst => (
      inst.id !== id ? inst : { ...inst, selected }
    ));
    const shapes = { ...scene.shapes, [ shapeId ]: { ...shape, instances } };
    setScene( { ...scene, shapes } );
    // TODO lower ambient light if anything is selected
  } );
  
  subscribeFor( 'TEXT_FETCHED', ( source ) => {
    setSource( source );
  } );
  
  subscribeFor( 'ALERT_RAISED', ( problem ) => {
    setProblem( problem );
  } );
  
  const providerValue = {
    scene, setScene, requestDesign, scenes, source, problem, waiting, labels,
    clearProblem: () => setProblem( '' ),
    requestScene: ( name, config ) => postMessage( selectScene( name, config ) ),
    fetchPreview: ( url, config )  => postMessage( fetchDesign( url, config ) ),
  };

  return (
    <ViewerContext.Provider value={ providerValue }>
      {props.children}
    </ViewerContext.Provider>
  );
}

const useViewer = () => { return useContext( ViewerContext ); };

export { ViewerProvider, useViewer };
