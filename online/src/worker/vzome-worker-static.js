
import { createStore, applyMiddleware } from 'redux';
import { exposeStore } from 'redux-in-worker';

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
  const shapesDict = {}
  shapes.map( shape => {
    shapesDict[ shape.id ] = shape;
    shape.instances = [];
  } );
  // shapes.map( shape => sceneListener.shapeAdded( shape ) );

  let i = 0;
  const IDENTITY_MATRIX = [1,0,0,0,0,1,0,0,0,0,1,0,0,0,0,1]
  instances.map( ({ position, orientation, color, shape }) => {
    const id = "id_" + i++;
    const { x, y, z } = position;
    const rotation = [ ...( orientations[ orientation ] || IDENTITY_MATRIX ) ];
    const instance = { id, position: [ x, y, z ], rotation, color, shapeId: shape };
    shapesDict[ shape ].instances.push( instance );
    // sceneListener.instanceAdded( instance );
  });
  return { shapes: shapesDict }
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

export const initialState = {};

const reducer = ( state = initialState, event ) =>
{
  switch ( event.type ) {

    case 'ALERT_RAISED':
      return { ...state, problem: event.payload, waiting: false };

    case 'ALERT_DISMISSED':
      return { ...state, problem: '' };

    case 'FETCH_STARTED': {
      const { url, viewOnly } = event.payload;
      return { ...state, waiting: true, editing: !viewOnly };
    }

    case 'TEXT_FETCHED':
      return { ...state, source: event.payload };

    case 'SCENE_INITIALIZED': {
      const { scene } = state;
      return { ...state, scene: { ...scene, ...event.payload } };
      break;
    }

    case 'SCENE_COMPLETED': {
      const { scene } = state;
      return { ...state, scene: { ...scene, ...event.payload }, waiting: false };
      break;
    }

    default:
      return state;
  }
};

const parseAndInterpret = ( xmlLoading, report ) =>
{
  let legacyModule;
  return Promise.all( [ import( './legacy/dynamic.js' ), xmlLoading ] )

    .then( ([ module, xml ]) => {
      legacyModule = module;
      return module .parse( xml );
    } )

    .then( design => {
      const { renderer, camera, lighting } = design;
      const { embedding } = renderer;
      camera.fov = 0.33915263; // WORKAROUND
      report( { type: 'SCENE_INITIALIZED', payload: { lighting, camera, embedding, shapes: {} } } );
      // the next step may take several seconds, which is why we already reported SCENE_INITIALIZED
      report( { type: 'SCENE_COMPLETED', payload: legacyModule .interpretAndRender( design ) } );
      return true; // probably nobody should care about the return value
    } )

    .catch( error => {
      console.log( `parseAndInterpret failure: ${error.message}` );
      report( { type: 'ALERT_RAISED', payload: 'Failed to load vZome model.' } );
      return false; // probably nobody should care about the return value
     } );
}

const fileLoader = store => report => event =>
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

const urlLoader = store => report => event =>
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
        report( { type: 'SCENE_INITIALIZED', payload: convertScene( preview ) } );
        report( { type: 'SCENE_COMPLETED', payload: convertGeometry( preview ) } );
        return true; // probably nobody should care about the return value
      })
      .catch( error => {
        console.log( error.message );
        console.log( `Failed to load and parse preview: ${previewUrl}` );
        return parseAndInterpret( xmlLoading, report );
      } );
  }
  else {
    return parseAndInterpret( xmlLoading, report );
  }
}

const store = createStore( reducer, applyMiddleware( urlLoader, fileLoader ) );

exposeStore( store );
