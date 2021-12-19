

const IDENTITY_MATRIX = [1,0,0,0,0,1,0,0,0,0,1,0,0,0,0,1]

const texts = {}

const previews = {}

onmessage = async function( e )
{
  const convertPreview = preview =>
  {
    let { lights, camera, shapes, instances, embedding, orientations } = preview
    
    const dlights = lights.directionalLights.map( ({ direction, color }) => {
      const { x, y, z } = direction
      return { direction: [ x, y, z ], color }
    })
    const { lookAtPoint, upDirection, lookDirection, viewDistance, fieldOfView, near, far } = camera
    const lookAt = [ ...Object.values( lookAtPoint ) ]
    const up = [ ...Object.values( upDirection ) ]
    const lookDir = [ ...Object.values( lookDirection ) ]
    camera = {
      near, far, up, lookAt,
      fov: fieldOfView,
      position: lookAt.map( (e,i) => e - viewDistance * lookDir[ i ] ),
    }
    
    this.postMessage( { type: "scene", scene: { lighting: { ...lights, directionalLights: dlights }, camera, embedding } } );

    shapes.map( shape => this.postMessage( { type: "shape", shape } ) );

    let i = 0;
    instances.map( ({ position, orientation, color, shape }) => {
      const id = "id_" + i++;
      const { x, y, z } = position;
      const rotation = orientations[ orientation ] || IDENTITY_MATRIX;
      this.postMessage( { type: "instance", instance: { id, position: [ x, y, z ], rotation, color, shapeId: shape } } );
    });
  }

  console.log( `Message received from main script: ${JSON.stringify( e.data, null, 2 )}` );
  switch ( e.data.type ) {

    case "fetchShapesAndText": {
      const url = e.data.url;
      // TODO: think about failure cases!  What is the contract for the worker?
      texts[ url ] = fetchUrlText( url ); // save the promise
      const previewUrl = url.substring( 0, url.length-6 ).concat( ".shapes.json" );
      previews[ url ] = fetchUrlText( previewUrl ) .then( text => JSON.parse( text ) );
      break;
    }
  
    case "returnShapesAndText": {
      const url = e.data.url;
      const text = await texts[ url ];
      this.postMessage( { type: "text", text } );
      const preview = await previews[ url ];
      convertPreview( preview );
      break;
    }
  
    default:
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

console.log( 'The worker loaded!' );