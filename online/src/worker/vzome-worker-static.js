
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

const parseAndInterpret = ( xmlLoading, report ) =>
{
  let legacyModule;
  return Promise.all( [ import( './legacy/dynamic.js' ), xmlLoading ] )

    .then( ([ module, xml ]) => {
      legacyModule = module;
      return module .parse( xml );
    } )

    .then( design => {
      const { renderer, camera, lighting, xmlTree, targetEditId, snapshots } = design;
      const { embedding } = renderer;
      // the next step may take several seconds, which is why we already reported PARSE_COMPLETED
      renderHistory = legacyModule .interpretAndRender( design );
      const { shapes } = renderHistory .getScene( targetEditId, true );
      const scene = { lighting, camera, embedding, shapes };
      // TODO: massage xmlTree to make branches from BeginBlock ... EndBlock sequences
      report( { type: 'SCENE_RENDERED', payload: { scene } } );
      report( { type: 'DESIGN_INTERPRETED', payload: { xmlTree, snapshots } } );
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
  const file = event.payload;
  const { name } = file;
  report( { type: 'FETCH_STARTED', payload: { name, viewOnly: false } } );
  console.log( `%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%% editing ${name}` );
  const xmlLoading = fetchFileText( file );

  xmlLoading .then( text => report( { type: 'TEXT_FETCHED', payload: { name, text } } ) );

  return parseAndInterpret( xmlLoading, report );
}

const urlLoader = ( report, event ) =>
{
  if ( event.type !== 'URL_PROVIDED' ) {
    return report( event );
  }
  const { url, viewOnly } = event.payload;
  const name = url.split( '\\' ).pop().split( '/' ).pop()
  report( { type: 'FETCH_STARTED', payload: event.payload } );
  console.log( `%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%% ${viewOnly? "viewing" : "editing " } ${url}` );
  const xmlLoading = fetchUrlText( url );

  xmlLoading .then( text => report( { type: 'TEXT_FETCHED', payload: { name, text } } ) );

  if ( viewOnly ) {
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
        return false; // probably nobody should care about the return value
      } )
      .then( () => {
        // Even in viewOnly mode we want to see any article/lesson snapshots,
        //   so we can't avoid doing the full parse.  However, delaying it until
        //   after the preview scene is rendered gives a much better experience.
        return parseAndInterpret( xmlLoading, report );
      })
  }
  else {
    return parseAndInterpret( xmlLoading, report );
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
      postMessage( { type: 'SCENE_RENDERED', payload: { scene } } );
      break;
  
    default:
      break;
  }
}