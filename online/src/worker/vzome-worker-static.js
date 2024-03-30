
import { resourceIndex, importLegacy, importZomic } from '../revision.js';

const uniqueId = Math.random();

// support trampolining to work around worker CORS issue
//   see https://github.com/evanw/esbuild/issues/312#issuecomment-1025066671
export const WORKER_ENTRY_FILE_URL = import.meta.url;

const IDENTITY_MATRIX = [1,0,0,0,0,1,0,0,0,0,1,0,0,0,0,1];

let baseURL;
let scenes;
let snapshots;
let previewShapes;

const captureScenes = report => event =>
{
  const { type, payload } = event;
  if ( type === 'SCENES_DISCOVERED' ) {
    scenes = payload;
  }
  report( event );
}

const filterScene = ( report, load ) => event =>
{
  const { type, payload } = event;
  if ( type === 'SCENE_RENDERED' ) {
    const { camera, lighting, shapes, embedding, polygons } = payload.scene;
    event.payload.scene = { shapes, embedding, polygons };
    if ( load.camera )   event.payload.scene.camera   = camera;
    if ( load.lighting ) event.payload.scene.lighting = lighting;
  }
  report( event );
}

const getSceneIndex = ( title, list ) =>
{
  if ( !title )
    return 0;
  let index;
  if ( title.startsWith( '#' ) ) {
    const indexStr = title.substring( 1 );
    index = parseInt( indexStr );
    if ( isNaN( index ) || index < 0 || index > list.length ) {
      console.log( `WARNING: ${index} is not a scene index` );
      index = 0;
    }
  } else {
    index = list .map( s => s.title ) .indexOf( title );
    if ( index < 0 ) {
      console.log( `WARNING: no scene titled "${title}"` );
      index = 0;
    }
  }
  return index;
}

const convertPreviewCamera = camera =>
{
  const { lookAtPoint, upDirection, lookDirection, viewDistance, width, nearClipDistance, farClipDistance, perspective } = camera
  const lookAt = [ ...Object.values( lookAtPoint ) ]
  const up = [ ...Object.values( upDirection ) ]
  const lookDir = [ ...Object.values( lookDirection ) ];
  const near = nearClipDistance;
  const far = farClipDistance;
  const distance = viewDistance;
  return {
    near, far, width, distance,
    up, lookAt, lookDir,
    perspective
  }
}

const preparePreviewScene = index =>
{
  if ( index >= scenes.length ) {
    console.log( `WARNING: no preview scene index "${index}"` );
    return {};
  }
  const { snapshot, view } = scenes[ index ];
  const camera = convertPreviewCamera( view );
  const shapes = {};
  for (const [ id, shape ] of Object.entries( previewShapes )) {
    shapes[ id ] = { ...shape, instances: [] };
  }
  for (const instance of snapshots[ snapshot ]) {
    shapes[ instance.shapeId ].instances.push( instance );
  }
  return { shapes, camera };
}

const convertPreview = ( preview, sceneTitle ) =>
{
  const { polygons, lights, embedding, orientations, shapes, instances } = preview
  
  const dlights = lights.directionalLights.map( ({ direction, color }) => {
    const { x, y, z } = direction
    return { direction: [ x, y, z ], color }
  })
  const lighting = { ...lights, directionalLights: dlights };

  const convertInstances = ( instances, idPrefix ) =>
  {
    let i = 0;
    return instances.map( ({ position, orientation, color, shape, label, glow }) => {
      const id = idPrefix + i++;
      const { x, y, z } = position;
      const selected = !!glow && glow > 0.001;
      const rotation = [ ...( orientations[ orientation ] || IDENTITY_MATRIX ) ];
      const instance = { id, position: [ x, y, z ], rotation, color, selected, shapeId: shape, type: 'irrelevant', label };
      return instance;
    });
  }
  // Save the module global snapshots
  snapshots = preview.snapshots? [ ...preview.snapshots ] .map( (snapshot,i) => {
    return convertInstances( snapshot, `snap${i}_` );
  }) : [];
  snapshots .push( convertInstances( instances, "main_" ) );
  const defaultSnapshot = snapshots.length-1;

  // Convert the shapes array to the previewShapes object, module global for reuse with other scenes/snapshots
  previewShapes = {}
  shapes.map( shape => {
    previewShapes[ shape.id ] = shape;
  } );

  // Save the module global scenes
  const defaultScene = { title: 'default scene', view: preview.camera, snapshot: defaultSnapshot };
  scenes = preview.scenes? [ defaultScene, ...preview.scenes ] : [ defaultScene ];

  const sceneIndex = getSceneIndex( sceneTitle, scenes );

  return { lighting, embedding, ...preparePreviewScene( sceneIndex ), polygons };
}

const fetchUrlText = async ( url ) =>
{
  let response
  try {
    response = await fetch( url )
  } catch ( error ) {
    console.log( `Fetching ${url} failed with "${error}"; trying cors-anywhere` )
    // TODO: I should really deploy my own copy of this proxy on Heroku
    response = await fetch( 'https://cors-anywhere.herokuapp.com/' + url )
  }
  if ( !response.ok ) {
    throw new Error( `Failed to fetch "${url}": ${response.statusText}` )
  }
  return response.text()
}

const fetchFileText = selected =>
{
  const temporaryFileReader = new FileReader()

  return new Promise( (resolve, reject) => {
    temporaryFileReader.onerror = () => {
      temporaryFileReader.abort()
      reject( temporaryFileReader.error )
    }

    temporaryFileReader.onload = () => {
      resolve( temporaryFileReader.result )
    }
    temporaryFileReader.readAsText( selected )
  })
}

let designWrapper;

const clientEvents = report =>
{
  const sceneChanged = ( scene, edit='--START--' ) => report( { type: 'SCENE_RENDERED', payload: { scene, edit } } );

  const shapeDefined = shape => report( { type: 'SHAPE_DEFINED', payload: shape } );

  const instanceAdded = instance => report( { type: 'INSTANCE_ADDED', payload: instance } );

  const latestBallAdded = instance => report( { type: 'LAST_BALL_CREATED', payload: instance } );

  const instanceRemoved = ( shapeId, id ) => report( { type: 'INSTANCE_REMOVED', payload: { shapeId, id } } );

  const selectionToggled = ( shapeId, id, selected ) => report( { type: 'SELECTION_TOGGLED', payload: { shapeId, id, selected } } );

  const symmetryChanged = details => report( { type: 'SYMMETRY_CHANGED', payload: details } );

  const xmlParsed = xmlTree => report( { type: 'DESIGN_XML_PARSED', payload: xmlTree } );

  const propertyChanged = ( controllerPath, name, value ) => report( { type: 'CONTROLLER_PROPERTY_CHANGED', payload: { controllerPath, name, value } } );

  const errorReported = message => report( { type: 'ALERT_RAISED', payload: message } );

  const scenesDiscovered = s => report( { type: 'SCENES_DISCOVERED', payload: s } );

  const designSerialized = xml => report( { type: 'DESIGN_XML_SAVED', payload: xml } );

  const textExported = ( action, text ) => report( { type: 'TEXT_EXPORTED', payload: { action, text } } ) ;

  const buildPlaneSelected = ( center, diskZone, hingeZone ) => report( { type: 'PLANE_CHANGED', payload: { center, diskZone, hingeZone } } );

  return { sceneChanged, shapeDefined, instanceAdded, instanceRemoved, selectionToggled, symmetryChanged, latestBallAdded,
    xmlParsed, scenesDiscovered, designSerialized, propertyChanged, errorReported, textExported, buildPlaneSelected, };
}

const trackballScenes = {};

const fetchTrackballScene = ( url, report ) =>
{
  const reportTrackballScene = scene => report( { type: 'TRACKBALL_SCENE_LOADED', payload: scene } );
  const cachedScene = trackballScenes[ url ];
  if ( !!cachedScene ) {
    reportTrackballScene( cachedScene );
    return;
  }
  const justTheScene = event =>
  {
    const { type, payload } = event;
    if ( type === 'SCENE_RENDERED' ) {
      trackballScenes[ url ] = payload;
      reportTrackballScene( payload );
    }
  }
  Promise.all( [ importLegacy(), fetchUrlText( new URL( `./resources/${url}`, baseURL ) ) ] )
    .then( ([ module, xml ]) => {
      module .loadDesign( xml, false, clientEvents( justTheScene ) );
    } )
}

const connectTrackballScene = ( report ) =>
{
  // console.log( "call", uniqueId );
  const trackballUpdater = () => fetchTrackballScene( designWrapper .getTrackballUrl(), report );
  trackballUpdater();
  designWrapper.controller .addPropertyListener( { propertyChange: pce =>
  {
    if ( 'symmetry' === pce.getPropertyName() ) { trackballUpdater(); }
  } });
}

const createDesign = async ( report, fieldName ) =>
{
  report( { type: 'FETCH_STARTED', payload: { name: 'untitled.vZome', preview: false } } );
  try {
    const module = await importLegacy();
    report({ type: 'CONTROLLER_CREATED' }); // do we really need this for previewing?
    designWrapper = module .newDesign( fieldName, clientEvents( report ) );
  } catch (error) {
    console.log(`createDesign failure: ${error.message}`);
    report({ type: 'ALERT_RAISED', payload: 'Failed to create vZome model.' });
    return false;
  }
}

const openDesign = async ( xmlLoading, name, report, debug, sceneTitle ) =>
{
  return Promise.all( [ importLegacy(), xmlLoading ] )

    .then( ([ module, xml ]) => {
      if ( !xml ) {
        report( { type: 'ALERT_RAISED', payload: 'The file is empty' } );
        return;
      }
      const doLoad = () => {
        report( { type: 'CONTROLLER_CREATED' } ); // do we really need this for previewing?
        designWrapper = module .loadDesign( xml, debug, clientEvents( captureScenes( report ) ), sceneTitle );
        report( { type: 'TEXT_FETCHED', payload: { text: xml, name } } ); // NOW it is safe to send the name
      }
      if ( xml .includes( 'Zomic' ) ) {
        importZomic() .then( (zomic) => {
          const field = module .getField( 'golden' );
          field .setInterpreterModule( zomic, module .vzomePkg );
          doLoad();
        })
        .catch( error => {
          console.log( `openDesign failure: ${error.message}` );
          report( { type: 'ALERT_RAISED', payload: `Failed to load vZome model: ${error.message}` } );
          return false; // probably nobody should care about the return value
         } );
      }
      else
        doLoad();
    } )

    .catch( error => {
      console.log( `openDesign failure: ${error.message}` );
      report( { type: 'ALERT_RAISED', payload: `Failed to load vZome model: ${error.message}` } );
      return false; // probably nobody should care about the return value
     } );
}

const fileLoader = ( report, event ) =>
{
  const { file, debug=false } = event.payload;
  const { name } = file;
  report( { type: 'FETCH_STARTED', payload: { name, preview: false } } );
  console.log( `%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%% editing ${name}` );
  const xmlLoading = fetchFileText( file );

  xmlLoading .then( text => report( { type: 'TEXT_FETCHED', payload: { text } } ) ); // Don't send the name yet, parse/interpret may fail

  return openDesign( xmlLoading, name, report, debug );
}

const fileImporter = ( report, event ) =>
{
  const IMPORT_ACTIONS = {
    'mesh' : 'ImportSimpleMeshJson',
    'cmesh': 'ImportColoredMeshJson',
    'vef'  : 'LoadVEF',
  }
  const { file, format } = event.payload;
  const action = IMPORT_ACTIONS[ format ];
  fetchFileText( file )

    .then( text => {
      try {
        designWrapper .doAction( '', action, { vef: text } );
        const { shapes, embedding } = designWrapper .getScene( '--END--', true ); // never send camera or lighting!
        report( { type: 'SCENE_RENDERED', payload: { scene: { shapes, embedding } } } );
      } catch (error) {
        console.log( `${action} actionPerformed error: ${error.message}` );
        report( { type: 'ALERT_RAISED', payload: `Failed to perform action: ${action}` } );
      }
    } )
    .catch( error => {
      console.log( error.message );
      console.log( 'Import failed' );
    } )
}

const defaultLoad = { camera: true, lighting: true, design: true, };

const urlLoader = async ( report, event ) =>
{
  const { url, config } = event.payload;
  const { preview=false, debug=false, showScenes='none', sceneTitle, load=defaultLoad } = config;
  if ( !url ) {
    throw new Error( "No url field in URL_PROVIDED event payload" );
  }
  const name = url.split( '\\' ).pop().split( '/' ).pop()
  report( { type: 'FETCH_STARTED', payload: event.payload } );

  console.log( `%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%% ${preview? "previewing" : "interpreting " } ${url}` );
  const xmlLoading = fetchUrlText( url );

  xmlLoading .then( text => report( { type: 'TEXT_FETCHED', payload: { text, url } } ) ); // Don't send the name yet, parse/interpret may fail

  report = filterScene( report, load );
  if ( preview ) {
    // The client prefers to show a preview if possible.
    const previewUrl = url.substring( 0, url.length-6 ).concat( ".shapes.json" );
    return fetchUrlText( previewUrl )
      .then( text => JSON.parse( text ) )
      .then( preview => {
        const scene = convertPreview( preview, sceneTitle ); // sets module global scenes as a side-effect
        if ( ( showScenes !== 'none' || sceneTitle ) && scenes.length < 2 ) {
          // The client expects scenes, but this preview JSON predates the scenes export,
          //  so fall back on XML.
          console.log( `No scenes in preview ${previewUrl}` );
          console.log( 'Preview failed, falling back to vZome XML' );
          return openDesign( xmlLoading, name, report, debug, sceneTitle );
        }
        const events = clientEvents( report );
        events .sceneChanged( scene );
        if ( scenes )
          events .scenesDiscovered( scenes );
        return true; // probably nobody should care about the return value
      } )
      .catch( error => {
        console.log( error.message );
        console.log( 'Preview failed, falling back to vZome XML' );
        return openDesign( xmlLoading, name, report, debug, sceneTitle );
      } )
  }
  else {
    return openDesign( xmlLoading, name, report, debug, sceneTitle );
  }
}

onmessage = ({ data }) =>
{
  // console.log( `TO worker: ${JSON.stringify( data.type, null, 2 )}` );
  const { type, payload } = data;

  try {
    
  switch (type) {

    case 'WINDOW_LOCATION':
      baseURL = payload;
      importLegacy()
        .then( module => Promise.all( resourceIndex .map( path => module.loadAndInjectResource( path, new URL( `./resources/${path}`, baseURL ) ) ) ) );
      break;

    case 'URL_PROVIDED':
      urlLoader( postMessage, data );
      break;
  
    case 'FILE_PROVIDED':
      fileLoader( postMessage, data );
      break;

    case 'MESH_FILE_PROVIDED':
      fileImporter( postMessage, data );
      break;

    case 'SCENE_SELECTED': {
      if ( !scenes )
        break;
      const { title, load } = payload;
      const index = getSceneIndex( title, scenes );
      const { nodeId, camera } = scenes[ index ];
      let scene;
      if ( nodeId ) { // XML was parsed by the legacy module
        scene = { camera, ...designWrapper .getScene( nodeId, true ) };
      } else // a preview JSON
        scene = preparePreviewScene( index );
      filterScene( postMessage, load )( { type: 'SCENE_RENDERED', payload: { scene } } );
      break;
    }

    case 'EDIT_SELECTED': {
      const { before, after } = payload; // only one of these will have an edit ID
      const scene = designWrapper .getScene( before || after, !!before );
      const { edit } = scene;
      postMessage( { type: 'SCENE_RENDERED', payload: { scene, edit } } );
      break;
    }

    case 'MACRO_TRIGGERED':
    {
      try {
        for (const actionData of payload) {
          const { controllerPath, action, parameters } = actionData;
          designWrapper .doAction( controllerPath, action, parameters );
        }
        const { shapes, embedding } = designWrapper .getScene( '--END--', true ); // never send camera or lighting!
        postMessage( { type: 'SCENE_RENDERED', payload: { scene: { shapes, embedding } } } );
      } catch (error) {
        console.log( `Macro error: ${error.message}` );
        postMessage( { type: 'ALERT_RAISED', payload: `Failed to complete macro.` } );
      }
      break;
    }

    case 'ACTION_TRIGGERED':
    {
      const { controllerPath, action, parameters } = payload;
      if ( action === 'connectTrackballScene' ) {
        connectTrackballScene( postMessage );
        return;
      }
      if ( action === 'snapCamera' ) {
        const symmController = designWrapper .getControllerByPath( controllerPath );
        const { up, lookDir } = parameters;
        const result = designWrapper .snapCamera( symmController.controller, up, lookDir );
        postMessage( { type: 'CAMERA_SNAPPED', payload: { up: result.up, lookDir: result.lookDir } } );
        return;
      }
      try {
        // console.log( "action", uniqueId );
        designWrapper .doAction( controllerPath, action, parameters );
        const { shapes, embedding } = designWrapper .getScene( '--END--', true ); // never send camera or lighting!
        postMessage( { type: 'SCENE_RENDERED', payload: { scene: { shapes, embedding, polygons: true } } } );
      } catch (error) {
        console.log( `${action} actionPerformed error: ${error.message}` );
        postMessage( { type: 'ALERT_RAISED', payload: `Failed to perform action: ${action}` } );
      }
      break;
    }

    case 'PROPERTY_SET':
    {
      const { controllerPath, name, value } = payload;
      try {
        designWrapper .setProperty( controllerPath, name, value );
      } catch (error) {
        console.log( `${action} setProperty error: ${error.message}` );
        postMessage( { type: 'ALERT_RAISED', payload: `Failed to set property: ${name}` } );
      }
      break;
    }

    case 'PROPERTY_REQUESTED':
    {
      const { controllerPath, propName, changeName, isList } = payload;
      designWrapper .registerPropertyInterest( controllerPath, propName, changeName, isList );
      break;
    }

    case 'NEW_DESIGN_STARTED':
      const { field } = payload;
      createDesign( postMessage, field );
      break;

    case 'STRUT_CREATION_TRIGGERED':
    case 'JOIN_BALLS_TRIGGERED':
    {
      designWrapper .doAction( 'buildPlane', type, payload );

      const scene = designWrapper .getScene( '--END--', true );
      postMessage( { type: 'SCENE_RENDERED', payload: { scene } } );
      break;
    }

    case 'HINGE_STRUT_SELECTED':
    {
      designWrapper .doAction( 'buildPlane', type, payload );
      break;
    }

    case 'PREVIEW_STRUT_START':
    {
      const { ballId, direction } = payload;
      designWrapper .startPreviewStrut( ballId, direction );
      break;
    }

    case 'PREVIEW_STRUT_MOVE':
    {
      const { direction } = payload;
      designWrapper .movePreviewStrut( direction );
      break;
    }
  
    case 'PREVIEW_STRUT_END':
    {
      designWrapper .endPreviewStrut();
      const scene = designWrapper .getScene( '--END--', true );
      postMessage( { type: 'SCENE_RENDERED', payload: { scene } } );
      break;
    }
  
    default:
      console.log( 'action not handled:', type, payload );
  }
  } catch (error) {
    console.log( `${type} onmessage error: ${error.message}` );
    postMessage( { type: 'ALERT_RAISED', payload: `Failed to perform action: ${type}` } );
  }
}