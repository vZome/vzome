
import { JsProperties } from './legacy/jsweet2js.js';

// support trampolining to work around worker CORS issue
//   see https://github.com/evanw/esbuild/issues/312#issuecomment-1025066671
export const WORKER_ENTRY_FILE_URL = import.meta.url;

const IDENTITY_MATRIX = [1,0,0,0,0,1,0,0,0,0,1,0,0,0,0,1];

let scenes;
let snapshots;
let previewShapes;

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

const convertPreview = preview =>
{
  const { lights, embedding, orientations, shapes, instances } = preview
  
  const dlights = lights.directionalLights.map( ({ direction, color }) => {
    const { x, y, z } = direction
    return { direction: [ x, y, z ], color }
  })
  const lighting = { ...lights, directionalLights: dlights };

  const convertInstances = ( instances, idPrefix ) =>
  {
    let i = 0;
    return instances.map( ({ position, orientation, color, shape }) => {
      const id = idPrefix + i++;
      const { x, y, z } = position;
      const rotation = [ ...( orientations[ orientation ] || IDENTITY_MATRIX ) ];
      const instance = { id, position: [ x, y, z ], rotation, color, shapeId: shape, type: 'irrelevant' };
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

  return { lighting, embedding, ...preparePreviewScene( 0 ) };
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

let designController;

const clientEvents = report =>
{
  const sceneChanged = ( scene, edit='--START--' ) => report( { type: 'SCENE_RENDERED', payload: { scene, edit } } );

  const shapeDefined = shape => report( { type: 'SHAPE_DEFINED', payload: shape } );

  const instanceAdded = instance => report( { type: 'INSTANCE_ADDED', payload: instance } );

  const selectionToggled = ( shapeId, id, selected ) => report( { type: 'SELECTION_TOGGLED', payload: { shapeId, id, selected } } );

  const symmetryChanged = details => report( { type: 'PLANES_DEFINED', payload: details } );

  const xmlParsed = xmlTree => report( { type: 'DESIGN_XML_PARSED', payload: xmlTree } );

  const propertyChanged = ( controllerPath, name, value ) => report( { type: 'CONTROLLER_PROPERTY_CHANGED', payload: { controllerPath, name, value } } );

  const errorReported = message => report( { type: 'ALERT_RAISED', payload: message } );

  const scenesDiscovered = s => {
    scenes = s; // TODO fix this horrible hack
    report( { type: 'SCENES_DISCOVERED', payload: s } );
  }

  const designSerialized = xml => report( { type: 'DESIGN_XML_SAVED', payload: xml } );

  const textExported = ( action, text ) => report( { type: 'TEXT_EXPORTED', payload: { action, text } } ) ;

  return { sceneChanged, shapeDefined, instanceAdded, selectionToggled, symmetryChanged,
    xmlParsed, scenesDiscovered, designSerialized, propertyChanged, errorReported, textExported, };
}

const createDesign = ( report, fieldName ) =>
{
  report( { type: 'FETCH_STARTED', payload: { name: 'untitled.vZome', preview: false } } );
  return import( './legacy/dynamic.js' )

    .then( module => {
      designController = module .newDesign( fieldName, clientEvents( report ) );
      report( { type: 'CONTROLLER_CREATED' } );
    } )

    .catch( error => {
      console.log( `createDesign failure: ${error.message}` );
      report( { type: 'ALERT_RAISED', payload: 'Failed to create vZome model.' } );
      return false; // probably nobody should care about the return value
     } );
}

const getField = name =>
{
  return import( './legacy/dynamic.js' )
    .then( module => {
      return module .getField( name );
    } );
}

const loadDesign = ( xmlLoading, report, debug ) =>
{
  return Promise.all( [ import( './legacy/dynamic.js' ), xmlLoading ] )

    .then( ([ module, xml ]) => {
      designController = module .loadDesign( xml, debug, clientEvents( report ) );
      report( { type: 'CONTROLLER_CREATED' } );
    } )

    .catch( error => {
      console.log( `loadDesign failure: ${error.message}` );
      report( { type: 'ALERT_RAISED', payload: `Failed to load vZome model.  ${error.message}` } );
      return false; // probably nobody should care about the return value
     } );
}

const fileLoader = ( report, event ) =>
{
  if ( event.type !== 'FILE_PROVIDED' ) {
    return report( event );
  }
  const { file, debug=false } = event.payload;
  const { name } = file;
  report( { type: 'FETCH_STARTED', payload: { name, preview: false } } );
  console.log( `%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%% editing ${name}` );
  const xmlLoading = fetchFileText( file );

  xmlLoading .then( text => report( { type: 'TEXT_FETCHED', payload: { name, text } } ) );

  return loadDesign( xmlLoading, report, debug );
}

const urlLoader = ( report, event ) =>
{
  if ( event.type !== 'URL_PROVIDED' ) {
    return report( event );
  }
  const { url, config } = event.payload;
  const { preview=false, debug=false, showScenes=false } = config;
  if ( !url ) {
    throw new Error( "No url field in URL_PROVIDED event payload" );
  }
  const name = url.split( '\\' ).pop().split( '/' ).pop()
  report( { type: 'FETCH_STARTED', payload: event.payload } );

  console.log( `%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%% ${preview? "previewing" : "interpreting " } ${url}` );
  const xmlLoading = fetchUrlText( url );

  xmlLoading .then( text => report( { type: 'TEXT_FETCHED', payload: { name, text, url } } ) );

  if ( preview ) {
    // The client prefers to show a preview if possible.
    const previewUrl = url.substring( 0, url.length-6 ).concat( ".shapes.json" );
    return fetchUrlText( previewUrl )
      .then( text => JSON.parse( text ) )
      .then( preview => {
        const scene = convertPreview( preview ); // sets module global scenes as a side-effect
        if ( showScenes && scenes.length < 2 )
          // The client expects scenes, but this preview JSON predates the scenes export,
          //  so fall back on XML.
          throw new Error( `No scenes in preview ${previewUrl}` );
        report( { type: 'SCENE_RENDERED', payload: { scene } } );
        if ( scenes )
          report( { type: 'SCENES_DISCOVERED', payload: scenes } );
        return true; // probably nobody should care about the return value
      } )
      .catch( error => {
        console.log( error.message );
        console.log( 'Preview failed, falling back to vZome XML' );
        return loadDesign( xmlLoading, report, debug );
      } )
  }
  else {
    return loadDesign( xmlLoading, report, debug );
  }
}

onmessage = ({ data }) =>
{
  // console.log( `Worker received: ${JSON.stringify( data, null, 2 )}` );
  const { type, payload } = data;

  try {
    
  switch (type) {

    case 'URL_PROVIDED':
      urlLoader( postMessage, data );
      break;
  
    case 'FILE_PROVIDED':
      fileLoader( postMessage, data );
      break;

    case 'SCENE_SELECTED': {
      const index = payload;
      const { nodeId, camera } = scenes[ index ];
      let scene;
      if ( nodeId ) { // XML was parsed by the legacy module
        scene = { camera, ...designController .getScene( nodeId, true ) };
      } else // a preview JSON
        scene = preparePreviewScene( index );
      postMessage( { type: 'SCENE_RENDERED', payload: { scene } } );
      break;
    }

    case 'EDIT_SELECTED': {
      const { before, after } = payload; // only one of these will have an edit ID
      const scene = designController .getScene( before || after, !!before );
      const { edit } = scene;
      postMessage( { type: 'SCENE_RENDERED', payload: { scene, edit } } );
      break;
    }

    case 'ACTION_TRIGGERED':
    {
      const { controllerPath, action, parameters } = payload;
      try {
        designController .doAction( controllerPath, action, parameters );
        const scene = designController .getScene( '--END--', true );
        postMessage( { type: 'SCENE_RENDERED', payload: { scene } } );
      } catch (error) {
        console.log( `${action} actionPerformed error: ${error.message}` );
        postMessage( { type: 'ALERT_RAISED', payload: `Failed to perform action: ${action}` } );
      }
      break;
    }

    case 'PROPERTY_REQUESTED':
    {
      const { controllerPath, propName, changeName, isList } = payload;
      designController .registerPropertyInterest( controllerPath, propName, changeName, isList );
      break;
    }

    case 'NEW_DESIGN_STARTED':
      const { field } = payload;
      createDesign( postMessage, field );
      break;

    case 'STRUT_CREATION_TRIGGERED':
    case 'JOIN_BALLS_TRIGGERED':
    {
      designController .doAction( 'buildPlane', type, payload );

      const scene = designController .getScene( '--END--', true );
      postMessage( { type: 'SCENE_RENDERED', payload: { scene } } );
    }
  
    default:
      break;
  }
  } catch (error) {
    console.log( `${type} onmessage error: ${error.message}` );
    postMessage( { type: 'ALERT_RAISED', payload: `Failed to perform action: ${type}` } );
  }
}