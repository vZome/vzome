
// support trampolining to work around worker CORS issue
//   see https://github.com/evanw/esbuild/issues/312#issuecomment-1025066671
export const WORKER_ENTRY_FILE_URL = import.meta.url;

const convertScene = preview =>
{
  let { lights, camera, embedding } = preview
  
  const dlights = lights.directionalLights.map( ({ direction, color }) => {
    const { x, y, z } = direction
    return { direction: [ x, y, z ], color }
  })
  const lighting = { ...lights, directionalLights: dlights };

  const { lookAtPoint, upDirection, lookDirection, viewDistance, fieldOfView, near, far } = camera
  const lookAt = [ ...Object.values( lookAtPoint ) ]
  const up = [ ...Object.values( upDirection ) ]
  const lookDir = [ ...Object.values( lookDirection ) ]
  camera = {
    near, far, up, lookAt,
    fov: fieldOfView,
    position: lookAt.map( (e,i) => e - viewDistance * lookDir[ i ] ),
  }
  return { lighting, camera, embedding };
}

const convertGeometry = preview =>
{
  let { shapes, instances, orientations } = preview
  const shapesOut = {}
  shapes.map( shape => {
    shapesOut[ shape.id ] = shape;
    shape.instances = [];
  } );

  let i = 0;
  const IDENTITY_MATRIX = [1,0,0,0,0,1,0,0,0,0,1,0,0,0,0,1]
  instances.map( ({ position, orientation, color, shape }) => {
    const id = "id_" + i++;
    const { x, y, z } = position;
    const rotation = [ ...( orientations[ orientation ] || IDENTITY_MATRIX ) ];
    const instance = { id, position: [ x, y, z ], rotation, color, shapeId: shape };
    shapesOut[ shape ].instances.push( instance );
  });
  return shapesOut
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

let renderHistory;

const parseAndInterpret = ( xmlLoading, report, debug ) =>
{
  let legacyModule;
  return Promise.all( [ import( './legacy/dynamic.js' ), xmlLoading ] )

    .then( ([ module, xml ]) => {
      legacyModule = module;
      return module .parse( xml );
    } )

    .then( design => {
      const { renderer, camera, lighting, xmlTree, targetEditId, snapshots, field } = design;
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
      const { embedding } = renderer;
      const scene = { lighting, camera, embedding, shapes };
      report( { type: 'SCENE_RENDERED', payload: { scene, edit } } );
      report( { type: 'DESIGN_INTERPRETED', payload: { xmlTree, snapshots } } );
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
  const { url, preview=false, debug=false } = event.payload;
  if ( !url ) {
    throw new Error( "No url field in URL_PROVIDED event payload" );
  }
  const name = url.split( '\\' ).pop().split( '/' ).pop()
  report( { type: 'FETCH_STARTED', payload: event.payload } );

  console.log( `%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%% ${preview? "previewing" : "interpreting " } ${url}` );
  const xmlLoading = fetchUrlText( url );

  xmlLoading .then( text => report( { type: 'TEXT_FETCHED', payload: { name, text } } ) );

  if ( preview ) {
    const previewUrl = url.substring( 0, url.length-6 ).concat( ".shapes.json" );
    return fetchUrlText( previewUrl )
      .then( text => JSON.parse( text ) )
      .then( preview => {
        const scene = { ...convertScene( preview ), shapes: convertGeometry( preview ) };
        report( { type: 'SCENE_RENDERED', payload: { scene } } );
        return true; // probably nobody should care about the return value
      } )
      .catch( error => {
        console.log( error.message );
        console.log( `Failed to load and parse preview: ${previewUrl}` );
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

    case 'EDIT_SELECTED':
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
  
    default:
      break;
  }
}