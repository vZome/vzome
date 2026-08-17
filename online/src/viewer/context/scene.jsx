
import { batch, createContext, createEffect, createSignal, useContext } from "solid-js";
import { createStore, reconcile, unwrap } from "solid-js/store";

import { useWorkerClient } from "./worker.jsx";
import { useCamera } from "./camera.jsx";
import { encodeEntities, selectSnapshot } from "../util/actions.js";
import { useViewer } from "./viewer.jsx";

// Stub used when there is no SceneProvider ancestor (e.g. the gltf-viewer / vrml-viewer web
// components, which mount SceneViewer -> SceneCanvas -> SymmetryGeometry directly under only a
// CameraProvider). SymmetryGeometry reads setSelectionHighlighter from here, so it must exist as a
// no-op; without it, `setSelectionHighlighter is not a function` throws on mount. (Those viewers
// have no vZome shapes anyway -- their content is added to the three.js scene via children3d.)
const SceneContext = createContext( {
  scene: () => { console.log( 'NO SceneProvider' ); },
  setSelectionHighlighter: () => {},
  highlightSelection: () => {},
} );

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

  // Optional imperative selection-highlight hook. SymmetryGeometry registers a callback here
  // (id, selected) so the SELECTION_TOGGLED handler can update the one flipped instance's GPU
  // highlight in O(1) -- an id-keyed lookup inside the renderer -- instead of the previous
  // reactive effect that re-scanned every instance of every shape (through slow store proxies)
  // on every toggle just to rediscover which id the message already named. That scan was the
  // dominant per-click cost on large models; select-all/deselect-all (one SELECTION_TOGGLED per
  // manifestation) made it O(N^2). The store's `selected` flag is still written surgically
  // below regardless, so non-symmetry readers (ShapedGeometry, context menu) stay correct; this
  // hook is purely the fast highlight path and is a no-op when unset (e.g. the ShapedGeometry
  // path, which highlights reactively per-<Instance>).
  let selectionHighlighter = null;
  const setSelectionHighlighter = fn => { selectionHighlighter = fn; };
  const highlightSelection = ( shapeId, id, selected ) => selectionHighlighter?.( shapeId, id, selected );

  const apiObject = { scene, setScene, labels, updateShapes, addShape, useViewer, setSelectionHighlighter, highlightSelection, }

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
      // Needed by SymmetryGeometry -- see the longer comment on the SCENE_RENDERED
      // subscription below. This is the SceneViewer/DesignViewer/UrlViewer scene-loading
      // path (via InitializeScene -> showIndexedScene), which never mounts
      // SceneChangeListener at all, so that comment's fix doesn't reach this path either.
      if ( scene.orientations )
        setScene( 'orientations', scene.orientations );
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
    <SceneIndexingContext.Provider value={ { showIndexedScene, lastSceneIndex, setLastSceneIndex } }>
      {props.children}
    </SceneIndexingContext.Provider>
  );
}

const SceneTitlesContext = createContext( { showTitledScene: ()=> { console.log( 'NO SceneTitlesProvider' ); } } );

const useSceneTitles = () => { return useContext( SceneTitlesContext ); };

const unnamedScene = ( scene, index ) => !scene.title?.trim() || ( index===0 && 'default scene' === scene.title );

const getSceneTitleIndex = ( scenes, title ) =>
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
    index = scenes .map( s => s.title?.trim() ) .indexOf( title );
    if ( index < 0 ) {
      console.log( `WARNING: no scene titled "${title}"` );
      index = 0;
    }
  }
  return index;
}

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

  // `scenes` (from useViewer()) loads asynchronously from the worker, so it's almost
  // always still empty at the createSignal() call above, freezing sceneTitle at
  // sceneTitles()[0] === undefined forever (signals don't recompute their initializer).
  // setSceneTitle was never called anywhere to correct this once scenes actually arrive,
  // so InitializeScene's showTitledScene(sceneTitle()) call would silently fall back to
  // scene index 0 ("default scene") on first paint instead of the scene actually wanted.
  if ( props.show !== 'given' ) {
    createEffect( () => {
      if ( sceneTitle() === undefined && scenes.length > 0 ) {
        setSceneTitle( sceneTitles()[0] );
      }
    } );
  }

  const showTitledScene = ( name, config ) =>
  {
    const index = getSceneTitleIndex( scenes, name );
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
  const { scene, updateShapes, addShape, setScene, highlightSelection } = useScene();
  const { subscribeFor } = useWorkerClient();

  subscribeFor( 'SYMMETRY_CHANGED', ( { orientations, fieldName, symmetryName } ) => {
    // fieldName + symmetryName (present for vZome files; see EditorController.setSymmetryController)
    // give SymmetryGeometry a STABLE renderer-group key. Keying on the orientation float matrices
    // instead collides across algebraic fields -- the symmetry rotations are the same reals in
    // every field -- which reused one field's group (and shapes) for another, corrupting non-
    // golden-field orientations. Undefined on the preview/viewer path, where SymmetryGeometry
    // falls back to hashing the orientations.
    const nextSymmetryId = ( fieldName && symmetryName ) ? `${fieldName}:${symmetryName}` : undefined;

    // batch() is REQUIRED, not just an optimization: this handler runs from the worker's async
    // onmessage, OUTSIDE any reactive context, so without batch each setScene flushes effects
    // synchronously on its own. SymmetryGeometry's group-switch effect keys on symmetryId but
    // REGISTERS the renderer group from orientations -- so an unbatched `setScene('symmetryId')`
    // (before the `setScene('orientations')` below) fired that effect with the NEW symmetryId but
    // the OLD orientations, registering e.g. the golden:octahedral group with the 60 icosahedral
    // matrices. The later orientations update re-ran the effect, but the group was already
    // registered (same key) so it kept the wrong matrices -> every octahedral strut rendered with
    // an icosahedral rotation. batch() flushes all three writes together, so the effect sees
    // symmetryId and orientations consistent, in one run.
    batch( () => {
      // Clear the OLD symmetry's shapes on a real symmetry change. SYMMETRY_CHANGED fires (with
      // the new orientations) BEFORE the following scene render repopulates shapes, so without this
      // the group-switch effect activates the new group while props.shapes still holds the previous
      // symmetry's shapes -- and SymmetryGeometry then tries to register those stale instances,
      // whose orientation indices belong to the OLD orientation set, against the NEW (possibly
      // smaller) group ("orientationIndex out of range for group"). Clearing to {} means the effect
      // has nothing stale to register until the new scene arrives. SYMMETRY_CHANGED only fires on
      // load or a genuine symmetry switch (never routine edits), always followed by a scene render,
      // so this clear is safe. Guard on an actual change so a redundant same-symmetry event doesn't
      // needlessly drop and rebuild all shapes.
      const symmetryChanged = nextSymmetryId === undefined || nextSymmetryId !== scene.symmetryId;
      if ( symmetryChanged )
        setScene( 'shapes', reconcile( {} ) );

      if ( nextSymmetryId )
        setScene( 'symmetryId', nextSymmetryId );
      setScene( 'orientations', orientations );
    } );
  });

  subscribeFor( 'SCENE_RENDERED', ( { scene } ) => {
    setScene( 'embedding', reconcile( scene.embedding ) );
    setScene( 'polygons', scene.polygons );
    // Needed by SymmetryGeometry. SYMMETRY_CHANGED (above) is the usual source of
    // scene.orientations, but the legacy Java loadDesign/newDesign path is the only one
    // that fires it. SCENE_RENDERED always carries `orientations` too though (see
    // prepareSceneResponse in vzome-worker-static.js), so read it here as well. Note:
    // this SceneChangeListener component is only mounted by the classic editor and
    // buildplane apps (see their index.jsx) -- the SceneViewer/DesignViewer/UrlViewer
    // path (the "viewer"/web-component embed) never mounts it at all, and gets its scene
    // data through SceneIndexingProvider's showIndexedScene() instead, which needed the
    // identical fix separately (see below).
    if ( scene.orientations )
      setScene( 'orientations', scene.orientations );
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
    // Fast path: update the flipped instance's GPU highlight directly, id-keyed and O(1), via
    // SymmetryGeometry's registered hook (no-op on other render paths). This is what keeps a
    // single click's highlight cheap on a big model -- see setSelectionHighlighter in
    // SceneProvider for why the previous reactive-scan approach was O(N) per toggle.
    highlightSelection( shapeId, id, selected );

    // Surgical nested store update: flip ONLY this one instance's `selected` flag, leaving the
    // shapes object, the shape, and the instances array all at their existing identities. This
    // keeps the store correct for readers that don't use the fast hook (ShapedGeometry, the
    // context menu, pick metadata) while NOT triggering SymmetryGeometry's expensive instance-
    // registration effect (which rebuilds whole GPU buffers) -- that effect untracks its
    // `selected` reads precisely so a selection toggle doesn't re-run it.
    const shape = scene.shapes[ shapeId ];
    const index = shape ?.instances.findIndex( inst => inst.id === id );
    if ( index === undefined || index < 0 )
      return;
    setScene( 'shapes', shapeId, 'instances', index, 'selected', selected );
    // TODO lower ambient light if anything is selected
  } );

  return null;
}

export {
  SceneProvider, useScene,
  SceneIndexingProvider, useSceneIndexing, 
  SceneTitlesProvider, useSceneTitles,
  SceneChangeListener,
  getSceneTitleIndex,
};
