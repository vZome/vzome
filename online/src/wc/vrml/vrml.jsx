
import { createEffect, createResource } from "solid-js";
import { useThree } from "solid-three";
import { VRMLLoader } from "./VRMLLoader.js";
import { useCamera } from "../../viewer/context/camera.jsx";

const loader = new VRMLLoader();

const fetchVrml = async ( url ) =>
{
  // if ( ! url .endsWith( '.vrml' ) )
  //   return null;

  return loader .loadAsync( url );
}

export const VrmlModel = (props) =>
{
  const [ data ] = createResource( () => props.url, fetchVrml );
  const { tweenCamera, setTweenDuration } = useCamera();

  let priorScene;
  
  const scene = useThree(({ scene }) => scene);
  createEffect( () => {
    const loadedScene = data();
    if ( !!loadedScene ) {
      if ( !! priorScene ) scene() .remove( priorScene );
      scene() .add( loadedScene );
      priorScene = loadedScene;

      if ( props.tweening?.duration ) {
        setTimeout( () => {
          setTweenDuration( props.tweening.duration );
          tweenCamera( props.camera );
        } );
      }
    }
  } );
  return null;
}
