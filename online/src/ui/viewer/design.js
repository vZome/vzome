
export const loadDesign = url =>
{
  let source = { url };
  let scene = {};

  const worker = new Worker( '/modules/vzome-worker-static.js' );
  if ( url ) {
    worker.postMessage( { type: "URL_PROVIDED", payload: url } );
  }

  const connectListeners = ( sourceChanged, sceneChanged ) =>
  {
    worker.onmessage = function(e) {
      console.log( `Message received from worker: ${e.data.type}` );
      switch ( e.data.type ) {

        case "TEXT_FETCHED":
          source.text = e.data.payload;
          sourceChanged( source );
          break;
      
        case "SCENE_INITIALIZED":
          scene = { ...scene, ...e.data.payload }
          sceneChanged( scene );
          break;
      
        case "SHAPE_ADDED": {
          const shape = e.data.payload;
          const shapes = { ...scene.shapes, [shape.id]: { ...shape, instances: [] } };
          scene = { ...scene, shapes }
          sceneChanged( scene );
          break;
        }

        case "INSTANCE_ADDED": {
          const { shapeId } = e.data.payload;
          const shape = scene.shapes[ shapeId ]
          const updatedShape = { ...shape, instances: [ ...shape.instances, e.data.payload ] };
          const shapes = { ...scene.shapes, [shapeId]: updatedShape };  // make a copy
          scene = { ...scene, shapes }
          sceneChanged( scene );
          break;
        }
      
        default:
          console.log( `Unknown message type received from worker: ${JSON.stringify(e.data, null, 2)}` );
          break;
      }
    }
    worker.postMessage( { type: "RENDERER_PREPARED" } );
  }

  return {
    connectListeners,
  };
}