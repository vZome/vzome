
// Adapted from https://github.com/nksaraf/react-three-fiber/commit/581d02376d4304fb3bab5445435a61c53cc5cdc2

import { createEffect, onCleanup } from 'solid-js';
import { useThree, createT } from 'solid-three';
import { PerspectiveCamera } from "three";
const T = createT({ PerspectiveCamera });

import { useCamera, } from "../viewer/context/camera.jsx";

export const ControlledPerspectiveCamera = (props) =>
{
  const { perspectiveProps, state : cameraConfig, globalScale, } = useCamera();
  let cam;
  const { setCamera, scene } = useThree();

  createEffect( () => {
    if ( cameraConfig.outlines )
      cam.layers .enable( 4 );
    else
      cam.layers .disable( 4 );
  });

  // This must run before the lookAt effect below.  solid-three first parents the camera under
  //  WebXRSupport's scaled originGroup, and lookAt() aims from the camera's WORLD position, so a
  //  camera created after mount (e.g. toggling perspective) would aim wrong for any off-origin target.
  createEffect( () => {
    setCamera( cam );
    // I don't know why this is necessary... I guess a camera is not added automatically
    scene .add( cam );
    onCleanup( () => scene .remove( cam ) );
  } );

  createEffect( () => {
    const [ x, y, z ] = perspectiveProps .target;
    cam .lookAt( x * globalScale, y * globalScale, z * globalScale );
  });

  createEffect(() => {
    cam.near = perspectiveProps .near * globalScale;
    cam.far = perspectiveProps .far * globalScale;
    cam.fov = perspectiveProps .fov( props.aspect );
    cam.aspect = props.aspect;
    cam.updateProjectionMatrix();
  });

  return (
    <T.PerspectiveCamera ref={cam} position={perspectiveProps .position.map( e => e * globalScale )} up={perspectiveProps .up} >
      {props.children}
    </T.PerspectiveCamera>
  );
}
