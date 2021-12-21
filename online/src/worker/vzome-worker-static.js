

const promises = {};

const convertPreview = ( preview, sceneListener ) =>
{
  let { lights, camera, shapes, instances, embedding, orientations } = preview
  
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
  
  sceneListener.initialized( { lighting, camera, embedding } );

  shapes.map( shape => sceneListener.shapeAdded( shape ) );

  let i = 0;
  const IDENTITY_MATRIX = [1,0,0,0,0,1,0,0,0,0,1,0,0,0,0,1]
  instances.map( ({ position, orientation, color, shape }) => {
    const id = "id_" + i++;
    const { x, y, z } = position;
    const rotation = orientations[ orientation ] || IDENTITY_MATRIX;
    sceneListener.instanceAdded( { id, position: [ x, y, z ], rotation, color, shapeId: shape } );
  });
}

onmessage = async function( e )
{
  const sceneListener = {
    initialized: payload => this.postMessage( { type: "SCENE_INITIALIZED", payload } ),
    shapeAdded: payload => this.postMessage( { type: "SHAPE_ADDED", payload } ),
    instanceAdded: payload => this.postMessage( { type: "INSTANCE_ADDED", payload } ),
    instanceRemoved: payload => this.postMessage( { type: "INSTANCE_REMOVED", payload } ),
  }

  console.log( `Message received from main script: ${e.data.type}` );
  switch ( e.data.type ) {

    case "URL_PROVIDED": {
      const url = e.data.payload;
      // TODO: think about failure cases!  What is the contract for the worker?
      promises.text = fetchUrlText( url ); // save the promise
      const previewUrl = url.substring( 0, url.length-6 ).concat( ".shapes.json" );
      promises.preview = fetchUrlText( previewUrl )
        .then( text => JSON.parse( text ) )
        .catch( () => {
          import( './legacy/dynamic.js' )
            .then( module => {
              promises.xml = promises.text .then( xml => module .parse( xml ) );
            })
        })
      break;
    }
  
    case "RENDERER_PREPARED": {
      promises.text
        .then( text => this.postMessage( { type: "TEXT_FETCHED", payload: text } ) );
      promises.preview
        .then( preview => convertPreview( preview, sceneListener ) )
        .catch( () => {
          console.log( 'Preview promise was rejected.' );
          promises.xml .then( design => {            
            import( './legacy/dynamic.js' )
              .then( module => module .interpretAndRender( design, sceneListener ) );
          } );
         } );
      break;
    }
  
    default:
      console.log( `Unknown message type ignored: ${e.data.type}` );
      break;
  }
}

export const fetchUrlText = async ( url ) =>
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

// console.log( 'The worker loaded!' );