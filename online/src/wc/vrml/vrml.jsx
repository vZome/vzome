
import { createEffect, createResource } from "solid-js";
import { VRMLLoader } from "./VRMLLoader.js";
import { useCamera } from "../../viewer/context/camera.jsx";
import { useWebXRClient } from "../../viewer/webxr.jsx";

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
  
  createEffect( () => {
    const { setRootScene } = useWebXRClient();
    const loadedScene = data();
    if ( !!loadedScene ) {
      setRootScene( loadedScene );
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
