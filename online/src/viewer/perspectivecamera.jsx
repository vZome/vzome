
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

  createEffect( () => {
    setCamera( cam );
    // I don't know why this is necessary... I guess a camera is not added automatically
    scene .add( cam );
    onCleanup( () => scene .remove( cam ) );
  } );

  return (
    <T.PerspectiveCamera ref={cam} position={perspectiveProps .position.map( e => e * globalScale )} up={perspectiveProps .up} >
      {props.children}
    </T.PerspectiveCamera>
  );
}
