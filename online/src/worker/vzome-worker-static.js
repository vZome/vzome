

const promises = {};

let editing = false;

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
    const rotation = orientations[ orientation ] || IDENTITY_MATRIX;
    const instance = { id, position: [ x, y, z ], rotation, color, shapeId: shape };
    shapesDict[ shape ].instances.push( instance );
    // sceneListener.instanceAdded( instance );
  });
  return { shapes: shapesDict }
}

onmessage = function( e )
{
  const sceneListener = {
    initialized: payload => this.postMessage( { type: "SCENE_INITIALIZED", payload } ),
    shapeAdded: payload => this.postMessage( { type: "SHAPE_ADDED", payload } ),
    instanceAdded: payload => this.postMessage( { type: "INSTANCE_ADDED", payload } ),
    instanceRemoved: payload => this.postMessage( { type: "INSTANCE_REMOVED", payload } ),
    sceneUpdated: () => this.postMessage( { type: "SCENE_UPDATED" } ),
  }

  console.log( `Message received from main script: ${e.data.type}` );
  switch ( e.data.type ) {

    case "URL_PROVIDED": {
      const { url, viewOnly } = e.data.payload;
      // TODO: think about failure cases!  What is the contract for the worker?
      editing = !viewOnly;
      console.log( `%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%% ${editing? "editing " : "viewing "} ${url}` );
      promises.text = fetchUrlText( url ); // save the promise

      if ( viewOnly ) {
        const previewUrl = url.substring( 0, url.length-6 ).concat( ".shapes.json" );
        promises.json = fetchUrlText( previewUrl )
          .then( text => JSON.parse( text ) );
        // The resolved json promise will be used after the view is connected.
      } else { // editing
        promises.json = Promise.reject( 'Editing, so no .shapes.json needed.' );
      }

      // If it is rejected (for either reason), we want to immediately start parsing the xml.
      promises.json
        .catch( () => {
          console.log( 'Parsing xml.' );
          return import( './legacy/dynamic.js' )
        })
        .then( module => {
          promises.xml = promises.text .then( xml => module .parse( xml ) );
        });
      break;
    }

    case "FILE_PROVIDED": {
      const file = e.data.payload;
      editing = true;
      console.log( `%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%% editing file ${file.name}` );
      // TODO: think about failure cases!  What is the contract for the worker?
      promises.text = fetchFileText( file ); // save the promise
      promises.json = Promise.reject();
      import( './legacy/dynamic.js' )
        .then( module => {
          promises.xml = promises.text .then( xml => module .parse( xml ) );
        })
      break;
    }
  
    case "VIEW_CONNECTED": {
      promises.text
        .then( text => this.postMessage( { type: "TEXT_FETCHED", payload: text } ) );

      promises.json
        .then( preview => {
          sceneListener.initialized( convertScene( preview ) );
          sceneListener.initialized( convertGeometry( preview ) );
          sceneListener.sceneUpdated();
        } );

      promises.json
        .catch( () => {
          import( './legacy/dynamic.js' )
            .then( module => {
              promises.xml
                .then( design => {
                  const { renderer, camera, lighting } = design;
                  const { embedding } = renderer;
                  camera.fov = 0.33915263; // WORKAROUND
                  sceneListener.initialized( { lighting, camera, embedding, shapes: {} } );
                  sceneListener.initialized( module .interpretAndRender( design ) );
                  sceneListener.sceneUpdated();
                } )
            } )
         } );
      break;
    }
  
    default:
      console.log( `Unknown message type ignored: ${e.data.type}` );
      break;
  }
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


// console.log( 'The worker loaded!' );