
import { createContext, createEffect, createSignal, useContext } from "solid-js";
import { createStore, reconcile, unwrap } from "solid-js/store";

import { useWorkerClient } from "./worker.jsx";
import { useCamera } from "./camera.jsx";
import { encodeEntities, selectSnapshot } from "../util/actions.js";
import { useViewer } from "./viewer.jsx";

const SceneContext = createContext( { scene: ()=> { console.log( 'NO SceneProvider' ); } } );

const SceneProvider = ( props ) =>
{
  const { labels: showLabels } = props.config || {};
  const [ scene, setScene ] = createStore( {} );
  const [ labels, setLabels ] = createSignal( showLabels );

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

  const apiObject = { scene, setScene, labels, updateShapes, addShape, useViewer, }

  return (
    <SceneContext.Provider value={ apiObject }>
      {props.children}
    </SceneContext.Provider>
  );
}

const useScene = () => { return useContext( SceneContext ); };

const SceneIndexingContext = createContext( { showIndexedScene: ()=> { console.log( 'NO SceneIndexingProvider' ); } } );

const useSceneIndexing = () => { return useContext( SceneIndexingContext ); };

const SceneIndexingProvider = ( props ) =>
{
  const { updateShapes, setScene } = useScene();
  const { postRequest } = useWorkerClient();
  const { tweenCamera } = useCamera();
  const { scenes } = useViewer();
  const [ lastSceneIndex, setLastSceneIndex ] = createSignal( null );

  const showIndexedScene = ( sceneIndex, config ) =>
  {
    if ( scenes.length === 0 )
      return;
    if ( sceneIndex >= scenes.length )
      sceneIndex = 0;
    postRequest( selectSnapshot( scenes[ sceneIndex ] .snapshot ) )
    .then( ( { payload: { scene } } ) => {
      setLastSceneIndex( sceneIndex );
      setScene( 'embedding', reconcile( scene.embedding ) );
      setScene( 'polygons', scene.polygons );
      const { camera } = config || { camera: true };
      setTimeout( () => {
        ( camera? tweenCamera( scenes[ sceneIndex ] .camera ) : Promise.resolve() )
          .then( () => updateShapes( scene.shapes ) );
      } );
    });
  }

  createEffect( () => {
    if ( props.index !== undefined ) {
      // if index is given, show that scene
      console.log( `SceneIndexingProvider effect showing ${props.index}` );     
      showIndexedScene( props.index );
    }
  } );
  
  return (
    <SceneIndexingContext.Provider value={ { showIndexedScene, lastSceneIndex } }>
      {props.children}
    </SceneIndexingContext.Provider>
  );
}

const SceneTitlesContext = createContext( { showTitledScene: ()=> { console.log( 'NO SceneTitlesProvider' ); } } );

const useSceneTitles = () => { return useContext( SceneTitlesContext ); };

const unnamedScene = ( scene, index ) => !scene.title?.trim() || ( index===0 && 'default scene' === scene.title );

const SceneTitlesProvider = (props) =>
{
  const { scenes } = useViewer();
  const { showIndexedScene } = useSceneIndexing();
  const namedScenes = () => scenes .filter( ( scene, index ) => !unnamedScene( scene, index ) ) .map( scene => scene.title );
  const sceneTitles = () =>
    ( props.show === 'given' )? []
    : ( props.show === 'titled' )? namedScenes()
    : scenes .map( (scene,index) => scene.title?.trim() || (( index === 0 )? "default scene" : `#${index}`) );
  const [ sceneTitle, setSceneTitle ] = createSignal(( props.show === 'given' )? props.title : sceneTitles()[0] );
  
  const getSceneIndex = ( title ) =>
  {
    if ( !title )
      return 0;
    title = encodeEntities( title ); // was happening in actions.js... still necessary?
    let index;
    if ( title.startsWith( '#' ) ) {
      const indexStr = title.substring( 1 );
      index = parseInt( indexStr );
      if ( isNaN( index ) || index < 0 || index > scenes.length ) {
        console.log( `WARNING: ${index} is not a scene index` );
        index = 0;
      }
    } else {
      index = scenes .map( s => s.title ) .indexOf( title );
      if ( index < 0 ) {
        console.log( `WARNING: no scene titled "${title}"` );
        index = 0;
      }
    }
    return index;
  }

  const showTitledScene = ( name, config ) =>
  {
    const index = getSceneIndex( name );
    if ( index < scenes.length ) {
      showIndexedScene( index, config );
    }
  }

  return (
    <SceneTitlesContext.Provider value={ { showTitledScene, sceneTitle, setSceneTitle, sceneTitles, } }>
      {props.children}
    </SceneTitlesContext.Provider>
  );
}

const SceneChangeListener = () =>
{
  const { scene, updateShapes, addShape, setScene } = useScene();
  const { subscribeFor } = useWorkerClient();

  subscribeFor( 'SYMMETRY_CHANGED', ( { orientations } ) => {
    setScene( 'orientations', orientations );
  });

  subscribeFor( 'SCENE_RENDERED', ( { scene } ) => {
    setScene( 'embedding', reconcile( scene.embedding ) );
    setScene( 'polygons', scene.polygons );
    updateShapes( scene.shapes );
    // logShapes();
  } );

  subscribeFor( 'SHAPE_DEFINED', ( shape ) => {
    addShape( { ...shape, instances: [] } );
    // logShapes();
  } );

  subscribeFor( 'INSTANCE_ADDED', ( instance ) => {
    const { shapeId, orientation } = instance;
    const shape = scene.shapes[ shapeId ];
    const rotation = unwrap( scene.orientations[ (orientation < 0)? 0 : orientation ] );
    setScene( 'shapes', shape.id, 'instances', [ ...shape.instances, { ...instance, rotation } ] );
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

  return null;
}

export {
  SceneProvider, useScene,
  SceneIndexingProvider, useSceneIndexing, 
  SceneTitlesProvider, useSceneTitles,
  SceneChangeListener,
};
