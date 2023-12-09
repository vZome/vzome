
import { createContext, useContext } from "solid-js";

import { createWorker } from "./client.js";
import { createWorkerStore } from "./controllers-solid.js";
import { fetchDesign, selectScene } from "./actions.js";
import { createStore, reconcile } from "solid-js/store";


const WorkerStateContext = createContext( {} );

const WorkerStateProvider = ( props ) =>
{
  const workerClient = props.store || createWorkerStore( createWorker() );
  const { url } = props.config || {};
  url && workerClient.postMessage( fetchDesign( url, props.config ) );
  
  return (
    <WorkerStateContext.Provider value={ { ...workerClient } }>
      {props.children}
    </WorkerStateContext.Provider>
  );
}

const useWorkerClient = () => { return useContext( WorkerStateContext ); };


const SceneContext = createContext( { scene: ()=> { console.log( 'NO SceneProvider' ); } } );

const SceneProvider = ( props ) =>
{
  const [ scene, setScene ] = createStore( {} );
  const { postMessage, subscribeFor } = useWorkerClient();
  console.log( 'creating SceneProvider' );

  const requestScene = ( name, config ) =>
  {
    postMessage( selectScene( name, config ) );
  }

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

  subscribeFor( 'SCENE_RENDERED', ( { scene } ) => {
    setScene( 'embedding', reconcile( scene.embedding ) );
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
  
  return (
    <SceneContext.Provider value={ { scene, setScene, requestScene } }>
      {props.children}
    </SceneContext.Provider>
  );
}

const useScene = () => { return useContext( SceneContext ); };

export { WorkerStateProvider, useWorkerClient, SceneProvider, useScene };