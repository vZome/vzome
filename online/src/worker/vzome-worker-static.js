
import { JsProperties } from './legacy/jsweet2js.js';

// support trampolining to work around worker CORS issue
//   see https://github.com/evanw/esbuild/issues/312#issuecomment-1025066671
export const WORKER_ENTRY_FILE_URL = import.meta.url;

const IDENTITY_MATRIX = [1,0,0,0,0,1,0,0,0,0,1,0,0,0,0,1];

let renderHistory;
let scenes;
let snapshots;
let previewShapes;

const convertCamera = camera =>
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

const prepareScene = index =>
{
  const { snapshot, view } = scenes[ index ];
  const camera = convertCamera( view );
  const shapes = {};
  for (const [ id, shape ] of Object.entries( previewShapes )) {
    shapes[ id ] = { ...shape, instances: [] };
  }
  for (const instance of snapshots[ snapshot ]) {
    shapes[ instance.shapeId ].instances.push( instance );
  }
  return { shapes, camera };
}

const convertScene = preview =>
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
      const instance = { id, position: [ x, y, z ], rotation, color, shapeId: shape };
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

  return { lighting, embedding, ...prepareScene( 0 ) };
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
const propertyChanges = {};
const bookmarks = [];
const tools = [];

const getNamedController = controllerPath =>
{
  const controllerNames = controllerPath? controllerPath.split( ':' ) : [];
  const getSubController = ( controller, names ) => {
    if ( !names || names.length === 0 || ( names.length === 1 && !names[ 0 ] ) )
      return controller;
    else {
      controller = controller .getSubController( names[ 0 ] );
      if ( !controller )
        return undefined;
      return getSubController( controller, names.slice( 1 ) );
    }
  }
  return getSubController( designController, controllerNames );
}

const registerChangeListener = ( controller, controllerPath, changeName, propName, isList ) =>
{
  if ( !changeName )
    return;
  if ( ! propertyChanges[ controllerPath ] ) {
    controller .addPropertyListener( { propertyChange: pce => {
      const name = pce .getPropertyName();
      if ( name === changeName ) {
        for ( const [propName, isList] of Object.entries( propertyChanges[ controllerPath ][ changeName ] ) ) {
          const value = isList? controller .getCommandList( propName ) : controller .getProperty( propName );
          postMessage( { type: 'CONTROLLER_PROPERTY_CHANGED', payload: { controllerPath, name: propName, value } } );
        }
      }
    } } );
    propertyChanges[ controllerPath ] = {};
  }
  const change = propertyChanges[ controllerPath ][ changeName ] || {};
  if ( ! change[ propName ] )
    propertyChanges[ controllerPath ][ changeName ] = { ...change, [ propName ]: isList };
}

const resolveBuildPlanes = buildPlanes =>
{
  const resolveAV = av => {
    const { x, y, z } = av.toRealVector(); // does this need to be embedded?  I think not.
    return [ x, y, z ];
  }
  const planes = {};
  for ( const [ name, plane ] of Object.entries( buildPlanes ) ) {
    const zones = [];
    for ( const zone of plane.zones ) {
      const vectors = zone .vectors .map( ({ point }) => resolveAV( point ) );
      zones .push( { name: zone.name, color: zone.color, vectors, orientation: zone.orientation } );
    }
    const { x, y, z } = plane.normal.toRealVector() .normalize(); // does this need to be embedded?  I think not.
    planes[ name ] = { color: plane.color, normal: [ x, y, z ], zones, orientation: plane.orientation };
  }
  return planes;
}

const sceneReporter = report =>
{
  const sceneChanged = scene => report( { type: 'SCENE_RENDERED', payload: { scene, edit: '--START--' } } );

  const shapeDefined = shape => report( { type: 'SHAPE_DEFINED', payload: shape } );

  const instanceAdded = instance => report( { type: 'INSTANCE_ADDED', payload: instance } );

  const selectionToggled = ( shapeId, id, selected ) => report( { type: 'SELECTION_TOGGLED', payload: { shapeId, id, selected } } );

  return { sceneChanged, shapeDefined, instanceAdded, selectionToggled }
}

const createDesign = ( report, fieldName ) =>
{
  return import( './legacy/dynamic.js' )

    .then( module => {
      const design = module .newDesign( fieldName, sceneReporter( report ) );
      const { controller, orbitSource } = design;
      designController = controller;
      report( { type: 'CONTROLLER_CREATED' } );
      const planes = resolveBuildPlanes( orbitSource .buildPlanes );
      const { orientations, symmetry, permutations } = orbitSource;
      const scalars = [ symmetry .getField() .getAffineScalar() .evaluate() ]; //TODO get them all!
      report( { type: 'WORKING_PLANE_GRID_DEFINED', payload: { orientations, permutations, scalars, planes } } );

      // Here we emulate the PCL of ModelPanel, since there is no list property
      //  on toolsController for either "tools" or "bookmarks".
      // NOTE: this is not tested yet!
      const toolsController = getNamedController( 'strutBuilder:tools' );
      toolsController .addPropertyListener( { propertyChange: pce => {
        const name = pce .getPropertyName();
        if ( name === 'tool.added' ) {
          const toolController = pce .getNewValue();
          const kind = toolController .getProperty( "kind" );
          if ( kind === 'bookmark' ) {
            const name = toolController .getProperty( "label" );
            bookmarks .append( name );
            report( { type: 'CONTROLLER_PROPERTY_CHANGED', payload: { controllerPath: 'strutBuilder:tools', name: 'bookmarks', value: bookmarks.slice() } } );
          }
        }
      } } );

      // const { shapes, edit } = renderHistory .getScene( '--START--', false );
      // const embedding = orbitSource .getEmbedding();
      // const scene = { embedding, shapes };
      // report( { type: 'SCENE_RENDERED', payload: { scene, edit } } );
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

const parseAndInterpret = ( xmlLoading, report, debug ) =>
{
  let legacyModule;
  return Promise.all( [ import( './legacy/dynamic.js' ), xmlLoading ] )

    .then( ([ module, xml ]) => {
      legacyModule = module;
      return module .parse( xml );
    } )

    .then( design => {

      // TODO: create controller
      
      const { orbitSource, camera, lighting, xmlTree, targetEditId, field } = design;
      scenes = design.scenes; // setting the module global, so we cannot use the destructuring above
      if ( field.unknown ) {
        throw new Error( `Field "${field.name}" is not supported.` );
      }
      // the next step may take several seconds, which is why we already reported PARSE_COMPLETED
      renderHistory = legacyModule .interpretAndRender( design, debug );
      // TODO: define a better contract for before/after.
      //  Here we are using before=false with targetEditId, which is meant to be the *next*
      //  edit to be executed, so this really should be before=true.
      //  However, the semantics of the HistoryInspector UI require the edit field to contain the "after" edit ID.
      //  Thus, we are too tightly coupled to the UI here!
      //  See also the 'EDIT_SELECTED' case in onmessage(), below.
      const { shapes, edit } = renderHistory .getScene( debug? '--START--' : targetEditId, false );
      const embedding = orbitSource .getEmbedding();
      const scene = { lighting, camera, embedding, shapes };
      report( { type: 'SCENE_RENDERED', payload: { scene, edit } } );
      report( { type: 'DESIGN_INTERPRETED', payload: xmlTree } );
      report( { type: 'SCENES_DISCOVERED', payload: scenes } );
      const error = renderHistory .getError();
      if ( !! error ) {
        throw error;
      } else
        return true; // probably nobody should care about the return value
    } )

    .catch( error => {
      console.log( `parseAndInterpret failure: ${error.message}` );
      report( { type: 'ALERT_RAISED', payload: 'Failed to load vZome model.' } );
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

  return parseAndInterpret( xmlLoading, report, debug );
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
        const scene = convertScene( preview ); // sets module global scenes as a side-effect
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
        return parseAndInterpret( xmlLoading, report, debug );
      } )
  }
  else {
    return parseAndInterpret( xmlLoading, report, debug );
  }
}

onmessage = ({ data }) =>
{
  // console.log( `Worker received: ${JSON.stringify( data, null, 2 )}` );
  const { type, payload } = data;

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
        scene = { camera, ...renderHistory .getScene( nodeId, true ) };
      } else // a preview JSON
        scene = prepareScene( index );
      postMessage( { type: 'SCENE_RENDERED', payload: { scene } } );
      break;
    }

    case 'EDIT_SELECTED': {
      const { before, after } = payload; // only one of these will have an edit ID
      const scene = before? renderHistory .getScene( before, true ) : renderHistory .getScene( after, false );
      const { edit } = scene;
      postMessage( { type: 'SCENE_RENDERED', payload: { scene, edit } } );
      const error = renderHistory .getError();
      if ( !!error ) {
        console.log( `getScene error: ${error.message}` );
        postMessage( { type: 'ALERT_RAISED', payload: 'Failed to interpret all edits.' } );
      }
      break;
    }

    case 'ACTION_TRIGGERED':
    {
      const { controllerPath, action, parameters } = payload;
      const controller = getNamedController( controllerPath );
      try {
        if ( parameters )
          controller .paramActionPerformed( null, action, new JsProperties( parameters ) );
        else
          controller .actionPerformed( null, action );
      } catch (error) {
        console.log( `${action} actionPerformed error: ${error.message}` );
        postMessage( { type: 'ALERT_RAISED', payload: `Failed to perform action: ${action}` } );
      }
      break;
    }

    case 'PROPERTY_REQUESTED':
    {
      const { controllerPath, propName, changeName, isList } = payload;
      const controller = getNamedController( controllerPath );
      registerChangeListener( controller, controllerPath, changeName, propName, isList );
      const value = isList? controller .getCommandList( propName ) : controller .getProperty( propName );
      postMessage( { type: 'CONTROLLER_PROPERTY_CHANGED', payload: { controllerPath, name: propName, value } } );
      break;
    }

    case 'NEW_DESIGN_STARTED':
      const { field } = payload;
      createDesign( postMessage, field );
      break;

    case 'STRUT_CREATION_TRIGGERED':
    case 'JOIN_BALLS_TRIGGERED':
    {
      const controller = getNamedController( 'editor:buildPlane' );
      controller .paramActionPerformed( null, type, new JsProperties( payload ) );
    }
  
    default:
      break;
  }
}