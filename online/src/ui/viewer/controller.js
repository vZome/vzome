
export const createController = ( setWaiting, { viewOnly=true } = {} ) =>
{
  const worker = new Worker( '/modules/vzome-worker-static.js', { type: "module" } );

  let source = {};
  let scene = {};
  let setCanRedo, setCanUndo;

  const connectHistory = ( canUndoSetter, canRedoSetter ) =>
  {
    setCanUndo = canUndoSetter;
    setCanRedo = canRedoSetter;
  }

  const connectView = ( sourceChanged, sceneChanged ) =>
  {
    worker.onmessage = function(e) {
      console.log( `Message received from worker: ${e.data.type}` );
      // console.log( `payload: ${JSON.stringify( e.data.payload, null, 2 )}` );
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

        case "INSTANCE_REMOVED": {
          const { id, shapeId } = e.data.payload;
          const shape = scene.shapes[ shapeId ]
          const instances = shape.instances.filter( instance => instance.id !== id )
          const updatedShape = { ...shape, instances };
          const shapes = { ...scene.shapes, [shapeId]: updatedShape };  // make a copy
          scene = { ...scene, shapes }
          sceneChanged( scene );
          break;
        }

        case "SCENE_UPDATED":
          setWaiting( false ); // see "event cycle" in components/folder.jsx
          break;
        
        case "PROPERTIES_CHANGED": {
          const { canUndo, canRedo } = e.data.payload;
          setCanUndo( canUndo );
          setCanRedo( canRedo );
          break;
        }
      
        default:
          console.log( `Unknown message type received from worker: ${JSON.stringify(e.data, null, 2)}` );
          break;
      }
    }
    worker.postMessage( { type: "VIEW_CONNECTED" } );
  }

  const enableView = () =>
  {
    worker.postMessage( { type: "VIEW_CONNECTED" } );
  }

  const fetchDesignUrl = url =>
  {
    if ( url ) {
      source.url = url;
      console.log( `!!!!!!!!!!!!!!!!!!! fetching ${url}` );
      worker.postMessage( { type: "URL_PROVIDED", payload: { url, viewOnly } } );
    }
  }

  const fetchDesignFile = file =>
  {
    source.url = "file://";
    worker.postMessage( { type: "FILE_PROVIDED", payload: file } );
  }

  const doAction = action =>
  {
    worker.postMessage( { type: "ACTION_TRIGGERED", payload: action } );
  }

  return { fetchDesignUrl, fetchDesignFile, connectHistory, connectView, enableView, doAction };
}
