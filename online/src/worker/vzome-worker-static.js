
import { resourceIndex, importLegacy, importZomic } from '../revision.js';
import { commitToGitHub, assemblePartsList, normalizePreview } from '../both-contexts.js';
import { createPartsList } from './legacy/partslist.js';

// const uniqueId = Math.random();

// support trampolining to work around worker CORS issue
//   see https://github.com/evanw/esbuild/issues/312#issuecomment-1025066671
export const WORKER_ENTRY_FILE_URL = import.meta.url;

let baseURL;

let design = {}

const IDENTITY_MATRIX = [1,0,0,0,0,1,0,0,0,0,1,0,0,0,0,1];

export const DEFAULT_SNAPSHOT = -1;

// Take a normalized scene and prepare it to send to the client, by
//   resolving the shapes and snapshot
// TODO: change the client contract to avoid sending shapes and rotation matrices all the time!
const prepareSceneResponse = ( design, snapshot ) =>
{
  const { embedding, polygons, snapshots, orientations, instances } = design.rendered;
  const snapshotInstances = ( snapshot === DEFAULT_SNAPSHOT )? instances : snapshots[ snapshot ];
  const shapes = {};
  for ( const instance of snapshotInstances ) {
    const rotation = [ ...( orientations[ instance.orientation ] || IDENTITY_MATRIX ) ];
    if ( ! shapes[ instance.shapeId ] ) {
      shapes[ instance.shapeId ] = { ...design.rendered.shapes[ instance.shapeId ], instances: [] };
    }
    shapes[ instance.shapeId ].instances.push( { ...instance, rotation } );
  }
  const parts = createPartsList( shapes, design.wrapper?.controller?.symmController );
  return { shapes, embedding, polygons, parts };
}

const prepareEditSceneResponse = ( design, edit, before ) =>
{
  let sceneInstances = design.wrapper .getScene( edit, before );

  if ( ! sceneInstances ) {
    // debugging, so we have to interpret more
    edit = design.wrapper .interpretToBreakpoint( edit, before );
    sceneInstances = design.wrapper .getScene( edit, before );
  }

  sceneInstances = { ...sceneInstances }; 
  const { polygons, orientations } = design.rendered;
  const shapes = {};
  for ( const instance of Object.values( sceneInstances ) ) {
    const rotation = [ ...( orientations[ instance.orientation ] || IDENTITY_MATRIX ) ];
    if ( ! shapes[ instance.shapeId ] ) {
      shapes[ instance.shapeId ] = { ...design.rendered.shapes[ instance.shapeId ], instances: [] };
    }
    shapes[ instance.shapeId ].instances.push( { ...instance, rotation } );
  }
  return { scene: { shapes, polygons }, edit };
}

const fetchUrlText = async ( url ) =>
{
  let response;
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

// Fetch the official Zometool colors
const parts_catalog_url = 'https://zometool.github.io/vzome-sharing/metadata/zometool-parts.json';
const partsPromise = fetch( parts_catalog_url ) .then( response => response.text() ) .then( text => JSON.parse( text ) );

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

  const snapshotCaptured = s => report( { type: 'SNAPSHOT_CAPTURED', payload: s } );

  const textExported = ( action, text ) => report( { type: 'TEXT_EXPORTED', payload: { action, text } } ) ;

  const buildPlaneSelected = ( center, diskZone, hingeZone ) => report( { type: 'PLANE_CHANGED', payload: { center, diskZone, hingeZone } } );

  return { sceneChanged, shapeDefined, instanceAdded, instanceRemoved, selectionToggled, symmetryChanged, latestBallAdded,
    xmlParsed, scenesDiscovered, snapshotCaptured, propertyChanged, errorReported, textExported, buildPlaneSelected, };
}

const trackballs = {};

const fetchTrackballScene = ( url, report, polygons ) =>
{
  const reportTrackballScene = scene => report( { type: 'TRACKBALL_SCENE_LOADED', payload: scene } );
  const cachedScene = trackballs[ url ];
  if ( !!cachedScene ) {
    reportTrackballScene( cachedScene );
    return;
  }
  Promise.all( [ importLegacy(), fetchUrlText( new URL( `/app/classic/resources/${url}`, baseURL ) ) ] )
    .then( async ([ legacy, xml ]) => {
      const trackballDesign = await legacy .loadDesign( xml, false, polygons, clientEvents( ()=>{} ) );
      const scene = prepareSceneResponse( trackballDesign, DEFAULT_SNAPSHOT );
      trackballs[ url ] = scene;
      reportTrackballScene( scene );
    } );
}

const connectTrackballScene = ( report, polygons ) =>
{
  // console.log( "call", uniqueId );
  const trackballUpdater = () => fetchTrackballScene( design.wrapper .getTrackballUrl(), report, polygons );
  trackballUpdater();
  design.wrapper.controller .addPropertyListener( { propertyChange: pce =>
  {
    if ( 'symmetry' === pce.getPropertyName() ) { trackballUpdater(); }
  } });
}

const createDesign = async ( report, fieldName ) =>
{
  report( { type: 'FETCH_STARTED', payload: { name: 'untitled.vZome', preview: false } } );
  try {
    const legacy = await importLegacy();
    design = await legacy .newDesign( fieldName, clientEvents( report ) );
    report({ type: 'CONTROLLER_CREATED' }); // do we really need this for previewing?
    reportDefaultScene( report );
  } catch (error) {
    console.log(`createDesign failure: ${error.message}`);
    report({ type: 'ALERT_RAISED', payload: 'Failed to create vZome model.' });
    return false;
  }
}

const openDesign = async ( xmlLoading, name, report, debug, polygons, shapshot=DEFAULT_SNAPSHOT ) =>
{
  const events = clientEvents( report );
  return Promise.all( [ importLegacy(), xmlLoading ] )

    .then( async ([ legacy, xml ]) => {
      if ( !xml ) {
        report( { type: 'ALERT_RAISED', payload: 'Unable to load .vZome content' } );
        return;
      }
      const doLoad = async () => {
        design = await legacy .loadDesign( xml, debug, polygons, events );
        report( { type: 'CONTROLLER_CREATED' } ); // do we really need this for previewing?

        events .sceneChanged( prepareSceneResponse( design, shapshot ) );

        report( { type: 'TEXT_FETCHED', payload: { text: xml, name } } ); // NOW it is safe to send the name
      }
      if ( xml .includes( 'Zomic' ) ) {
        importZomic() .then( async (zomic) => {
          const api = await legacy .initialize();
          const field = api .getField( 'golden' );
          field .setInterpreterModule( zomic, legacy .vzomePkg );
          doLoad();
        })
        .catch( error => {
          console.log( `openDesign failure: ${error.message}` );
          report( { type: 'ALERT_RAISED', payload: `Failed to load vZome model: ${error.message}` } );
         } );
      }
      else
        doLoad()
          .catch( error => {
            console.log( `openDesign failure: ${error.message}` );
            report( { type: 'ALERT_RAISED', payload: `Failed to load vZome model: ${error.message}` } );
          })
    } )

    .catch( error => {
      console.log( `openDesign failure: ${error.message}` );
      report( { type: 'ALERT_RAISED', payload: `Failed to load vZome model: ${error.message}` } );
     } );
}

const exportPreview = ( camera, lighting, scenes ) =>
{
  const { snapshots: allShapshots, ...rest } = design.rendered;
  const usedInScene = snapshot => scenes .some( scene => Number(scene.snapshot) === snapshot );
  const snapshots = allShapshots .map( ( snapshot, i ) => usedInScene( i )? snapshot : [] );
  scenes[ 0 ] .camera = camera;
  const rendered = { format: 'online', ...rest, snapshots, scenes, lighting };
  return JSON.stringify( rendered, null, 2 );
}

const shareToGitHub = async ( target, config, data, report ) =>
{
  const { orgName, repoName, branchName } = target;
  const { title, description, blog, publish, style, originalDate } = config;
  const { name, camera, lighting, image, scenes } = data;
  const preview = exportPreview( camera, lighting, scenes );
  importLegacy()
    .then( module => {
      const xml = design.wrapper .serializeVZomeXml( lighting, camera, scenes );
      const creation = ( originalDate || new Date() ) .toISOString();
      const date = creation .substring( 0, 10 );
      const time = creation .substring( 11 ) .replaceAll( ':', '-' ) .replaceAll( '.', '-' );
      const shareData = new module .vzomePkg.core.exporters.GitHubShare( title, date, time, xml, image, preview );

      const uploads = [];
      shareData .setEntryHandler( { addEntry: (path, data, encoding) => {
        data && uploads .push( { path, encoding, data } );
      } } );
      const gitUrl = shareData .generateContent( orgName, repoName, branchName, title, description, blog, publish, style );

      commitToGitHub( target, uploads, `Online share - ${name}` )
      .then( () => {
        report( { type: 'SHARE_SUCCESS', payload: gitUrl } );
      })
      .catch(( { message, status } ) => {
        report( { type: 'SHARE_FAILURE', payload: { message, status } } );
      });
    });
}

const textLoader = ( report, payload ) =>
{
  const { name, contents } = payload;
  const xmlLoading = new Promise( (resolve) => resolve( contents ) );
  openDesign( xmlLoading, name, report, false, true );
}

const fileLoader = ( report, payload ) =>
{
  const { file, debug=false, polygons=true } = payload;
  const { name } = file;
  report( { type: 'FETCH_STARTED', payload: { name, preview: false } } );
  console.log( `%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%% editing ${name}` );
  const xmlLoading = fetchFileText( file );

  xmlLoading .then( text => report( { type: 'TEXT_FETCHED', payload: { text } } ) ); // Don't send the name yet, parse/interpret may fail

  openDesign( xmlLoading, name, report, debug, polygons );
}

const fileImporter = ( report, payload ) =>
{
  const IMPORT_ACTIONS = {
    'mesh' : 'ImportSimpleMeshJson',
    'cmesh': 'ImportColoredMeshJson',
    'vef'  : 'LoadVEF',
  }
  const { file, format } = payload;
  const action = IMPORT_ACTIONS[ format ];
  fetchFileText( file )

    .then( text => {
      try {
        design.wrapper .doAction( '', action, { vef: text } );
        reportDefaultScene( report );
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

const defaultLoad = { design: true, bom: false, };

const urlLoader = async ( report, payload ) =>
{
  const { url, config } = payload;
  const { preview=false, debug=false, polygons=true, showScenes='none', snapshot=DEFAULT_SNAPSHOT, load=defaultLoad, source=true } = config;
  if ( !url ) {
    throw new Error( "No url field in URL_PROVIDED event payload" );
  }
  const name = url.split( '\\' ).pop().split( '/' ).pop()
  report( { type: 'FETCH_STARTED', payload } );

  console.log( `%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%% ${preview? "previewing" : "interpreting " } ${url}` );
  const xmlLoading = source && fetchUrlText( url );
  source && xmlLoading .then( text => report( { type: 'TEXT_FETCHED', payload: { text, url } } ) ); // Don't send the name yet, parse/interpret may fail

  const events = clientEvents( report );
  if ( preview ) {
    // The client prefers to show a preview if possible.
    const previewUrl = url.substring( 0, url.length-6 ).concat( ".shapes.json" );
    return fetchUrlText( previewUrl )
      .then( text => JSON.parse( text ) )
      .then( preview => {
        // .shapes.json can hold two formats: legacy (the default) or online
        const { lighting, scenes, ...rendered } = ( preview.format === 'online' )? preview : normalizePreview( preview );

        if ( ( showScenes !== 'none' || snapshot >= 0 ) && rendered.snapshots.length === 0 ) {
          // The client expects scenes, but this preview JSON predates the scenes export,
          //  so fall back on XML.
          console.log( `No snapshots in preview ${previewUrl}` );
          console.log( 'Preview failed, falling back to vZome XML' );
          openDesign( xmlLoading, name, report, debug, polygons, snapshot );
          return;
        }

        events .scenesDiscovered( { lighting, scenes } ); // send to client, where the state will be managed
        design.rendered = { ...rendered };
        events .sceneChanged( prepareSceneResponse( design, snapshot ) );
      } )
      .catch( error => {
        console.log( error.message );
        console.log( 'Preview failed, falling back to vZome XML' );
        openDesign( xmlLoading, name, report, debug, polygons, snapshot );
      } )
  }
  else {
    openDesign( xmlLoading, name, report, debug, polygons, snapshot );
  }
}

const reportDefaultScene = report =>
{
  const { shapes, embedding, parts } = prepareSceneResponse( design, DEFAULT_SNAPSHOT );
  clientEvents( report ) .sceneChanged( { shapes, embedding, polygons: true, parts } );

  // Now compute an updated parts list
  // const bom = assemblePartsList( shapes );
}

onmessage = ({ data }) =>
{
  // console.log( `TO worker: ${JSON.stringify( data.type, null, 2 )}` );
  const { type, payload, requestId, isInspector } = data;
  if ( typeof payload === 'object' ) {
    payload.polygons = !isInspector;
    if ( typeof payload.config === 'object' ) {
      payload.config.polygons = !isInspector;
    }
  }

  const responseLogger = message => {
    console.dir( message );
    postMessage( message );
  }
  let sendToClient = postMessage; // change to responseLogger for debugging
  if ( !! requestId ) {
    sendToClient = message => {
      message.requestId = requestId;
      postMessage( message ); // change to responseLogger for debugging
    }
  }

  try {
    
  switch (type) {

    case 'WORKER_PROBE':
      sendToClient( { type: 'WORKER_READY' } );
      break;

    case 'WINDOW_LOCATION':
      baseURL = payload;
      importLegacy()
        .then( legacy => Promise.all( resourceIndex .map( path => legacy.loadAndInjectResource( path, new URL( `/app/classic/resources/${path}`, baseURL ) ) ) ) );
      break;

    case 'URL_PROVIDED':
      urlLoader( sendToClient, payload );
      break;
  
    case 'TEXT_PROVIDED':
      textLoader( sendToClient, payload );
      break;
    
    case 'FILE_PROVIDED':
      fileLoader( sendToClient, payload );
      break;

    case 'MESH_FILE_PROVIDED':
      fileImporter( sendToClient, payload );
      break;

    case 'SNAPSHOT_SELECTED': {
      if ( !design?.rendered?.snapshots )
        break;
      const { snapshot } = payload;
      const scene = prepareSceneResponse( design, snapshot );
      sendToClient( { type: 'SCENE_RENDERED', payload: { scene } } );
      break;
    }

    case 'BOM_REQUESTED': {
      if ( !design?.rendered?.scenes )
        break;
      partsPromise .then( ({ colors }) => {
        const bom = assemblePartsList( design.rendered, colors );
        sendToClient( { type: 'BOM_CHANGED', payload: bom } );
      });
      break;
    }

    case 'EDIT_SELECTED': {
      const { before, after } = payload; // only one of these will have an edit ID
      const edit = before || after;
      const response = prepareEditSceneResponse( design, edit, !!before );
      sendToClient( { type: 'SCENE_RENDERED', payload: response } );
      break;
    }

    case 'MACRO_TRIGGERED':
    {
      try {
        for (const actionData of payload) {
          const { controllerPath, action, parameters } = actionData;
          design.wrapper .doAction( controllerPath, action, parameters );
        }
        reportDefaultScene( sendToClient );
      } catch (error) {
        console.log( `Macro error: ${error.message}` );
        sendToClient( { type: 'ALERT_RAISED', payload: `Failed to complete macro.` } );
      }
      break;
    }

    case 'ACTION_TRIGGERED':
    {
      const { controllerPath, action, parameters, polygons } = payload;
      try {
        if ( action === 'connectTrackballScene' ) {
          connectTrackballScene( sendToClient, polygons );
          return;
        }
        if ( action === 'shareToGitHub' ) {
          const { target, config, data } = parameters;
          shareToGitHub( target, config, data, sendToClient );
          return;
        }
        if ( action === 'captureSnapshot' ) {
          const { snapshots, instances } = design.rendered;
          const snapshot = snapshots.length;
          snapshots .push( [ ...instances ] );
          design.wrapper .doAction( controllerPath, 'Snapshot', { id: snapshot } ); // no-op, but the edit must be in the history
          clientEvents( sendToClient ) .snapshotCaptured( snapshot );
          return;
        }
        if ( action === 'snapCamera' ) {
          const symmController = design.wrapper .getControllerByPath( controllerPath );
          const { up, lookDir } = parameters;
          const result = design.wrapper .snapCamera( symmController.controller, up, lookDir );
          sendToClient( { type: 'CAMERA_SNAPPED', payload: { up: result.up, lookDir: result.lookDir } } );
          return;
        }
        if ( action === 'exportText' && parameters.format === 'shapes' ) {
          const { camera, lighting, scenes } = parameters;
          const preview = exportPreview( camera, lighting, scenes );
          clientEvents( sendToClient ) .textExported( action, preview );
          return;
        }
        if ( action === 'exportText' && parameters.format === 'vZome' ) {
          const { camera, lighting, scenes } = parameters;
          const xml = design.wrapper .serializeVZomeXml( lighting, camera, scenes );
          clientEvents( sendToClient ) .textExported( action, xml );
          return;
        }
        // console.log( "action", uniqueId );
        design.wrapper .doAction( controllerPath, action, parameters );
        reportDefaultScene( sendToClient );
      } catch (error) {
        console.log( `${action} actionPerformed error: ${error.message}` );
        sendToClient( { type: 'ALERT_RAISED', payload: `Failed to perform action: ${action}` } );
      }
      break;
    }

    case 'PROPERTY_SET':
    {
      const { controllerPath, name, value } = payload;
      try {
        design.wrapper .setProperty( controllerPath, name, value );
      } catch (error) {
        console.log( `${action} setProperty error: ${error.message}` );
        sendToClient( { type: 'ALERT_RAISED', payload: `Failed to set property: ${name}` } );
      }
      break;
    }

    case 'PROPERTY_REQUESTED':
    {
      const { controllerPath, propName, changeName, isList } = payload;
      design.wrapper .registerPropertyInterest( controllerPath, propName, changeName, isList );
      break;
    }

    case 'NEW_DESIGN_STARTED':
      const { field } = payload;
      createDesign( sendToClient, field );
      break;

    case 'STRUT_CREATION_TRIGGERED':
    case 'JOIN_BALLS_TRIGGERED':
    {
      design.wrapper .doAction( 'buildPlane', type, payload );
      reportDefaultScene( sendToClient );
      break;
    }

    case 'HINGE_STRUT_SELECTED':
    {
      design.wrapper .doAction( 'buildPlane', type, payload );
      break;
    }

    case 'PREVIEW_STRUT_START':
    {
      const { ballId, direction } = payload;
      design.wrapper .startPreviewStrut( ballId, direction );
      break;
    }

    case 'PREVIEW_STRUT_MOVE':
    {
      const { direction } = payload;
      design.wrapper .movePreviewStrut( direction );
      break;
    }

    case 'PREVIEW_STRUT_SCALE':
    {
      const { increment } = payload;
      design.wrapper .scalePreviewStrut( increment );
      break;
    }
  
    case 'PREVIEW_STRUT_END':
    {
      design.wrapper .endPreviewStrut();
      reportDefaultScene( sendToClient );
      break;
    }
  
    default:
      console.log( 'action not handled:', type, payload );
  }
  } catch (error) {
    console.log( `${type} onmessage error: ${error.message}` );
    sendToClient( { type: 'ALERT_RAISED', payload: `Failed to perform action: ${type}` } );
  }
}