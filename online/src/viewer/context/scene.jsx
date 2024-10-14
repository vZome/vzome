
import { createContext, createEffect, createSignal, useContext } from "solid-js";
import { createStore, reconcile } from "solid-js/store";

import { useWorkerClient } from "./worker.jsx";
import { useCamera } from "./camera.jsx";
import { selectScene } from "../util/actions.js";

const SceneContext = createContext( { scene: ()=> { console.log( 'NO SceneProvider' ); } } );

const SceneProvider = ( props ) =>
{
  const { labels: showLabels } = props.config || {};
  const [ scene, setScene ] = createStore( {} );
  const [ labels, setLabels ] = createSignal( showLabels );
  const { postRequest } = useWorkerClient();
  const { state, tweenCamera, setLighting } = useCamera();

  const addShape = ( shape ) =>
  {
    if ( ! scene .shapes ) {
      setScene( 'shapes', {} );
    }
    if ( ! scene ?.shapes[ shape.id ] ) {
      setScene( 'shapes', shape.id, shape );
      return true;
    }
    return false;
  }

  const updateShapes = ( shapes ) =>
  {
    for (const [id, shape] of Object.entries(shapes)) {
      if ( ! addShape( shape ) ) {
        // shape is not new, so just replace its instances
        setScene( 'shapes', id, 'instances', shape.instances );
      }
    }
    // clean up preview strut, which may be a shape otherwise not in the scene
    for ( const id of Object.keys( scene ?.shapes || {} ) ) {
      if ( ! (id in shapes) )
      setScene( 'shapes', id, 'instances', [] );
    }
  }

  createEffect( () => {
    postRequest( selectScene( props.name ) )
      .then( ( { payload: { scene } } ) => {
        if ( scene.camera ) {
          tweenCamera( scene.camera );
        }
        if ( scene.lighting ) {
          const { backgroundColor } = scene.lighting;
          setLighting( { ...state.lighting, backgroundColor } );
        }
        setScene( 'embedding', reconcile( scene.embedding ) );
        setScene( 'polygons', scene.polygons );
        updateShapes( scene.shapes );    
      });
  });

  return (
    <SceneContext.Provider value={ { scene, labels, } }>
      {props.children}
    </SceneContext.Provider>
  );
}

const useScene = () => { return useContext( SceneContext ); };

export { SceneProvider, useScene };
