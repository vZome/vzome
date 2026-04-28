
// Adapted from https://github.com/nksaraf/react-three-fiber/commit/581d02376d4304fb3bab5445435a61c53cc5cdc2

import { createEffect, onCleanup } from 'solid-js';
import { useThree, T } from "./util/solid-three.js";

import { useCamera } from "../viewer/context/camera.jsx";

export const OrthographicCamera = (props) =>
{
  const { perspectiveProps, state } = useCamera();
  const halfWidth = () => perspectiveProps.width / 2;
  let cam;
  const { scene, setCamera } = useThree();

  createEffect( () => {
    if ( state.outlines )
      cam.layers .enable( 4 );
    else
      cam.layers .disable( 4 );
  });

  createEffect(() => {
    cam.near = perspectiveProps .near;
    cam.far = perspectiveProps .far;
    cam.left = -halfWidth();
    cam.right = halfWidth();
    const halfHeight = halfWidth() / props.aspect;
    cam.top = halfHeight;
    cam.bottom = -halfHeight;
    cam.updateProjectionMatrix();
  });

  createEffect( () => {
    const [ x, y, z ] = perspectiveProps .target;
    cam .lookAt( x, y, z );
  });

  createEffect( () => {
    setCamera( cam );
    // I don't know why this is necessary... I guess a camera is not added automatically
    scene .add( cam );
    onCleanup( () => scene .remove( cam ) );
  } );

  return (
    <T.OrthographicCamera ref={cam} position={perspectiveProps .position} up={perspectiveProps .up} >
      {props.children}
    </T.OrthographicCamera>
  );
}
