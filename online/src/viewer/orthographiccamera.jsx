
// Adapted from https://github.com/nksaraf/react-three-fiber/commit/581d02376d4304fb3bab5445435a61c53cc5cdc2

import { createEffect, onCleanup } from 'solid-js';
import { useThree, createT } from 'solid-three';
import { OrthographicCamera } from "three";
const T = createT({ OrthographicCamera });

import { useCamera, } from "../viewer/context/camera.jsx";

export const ControlledOrthographicCamera = (props) =>
{
  const { perspectiveProps, state, globalScale } = useCamera();
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
    cam.near = perspectiveProps .near * globalScale;
    cam.far = perspectiveProps .far * globalScale;
    cam.left = -halfWidth() * globalScale;
    cam.right = halfWidth() * globalScale;
    const halfHeight = halfWidth() / props.aspect;
    cam.top = halfHeight * globalScale;
    cam.bottom = -halfHeight * globalScale;
    cam.updateProjectionMatrix();
  });

  createEffect( () => {
    const [ x, y, z ] = perspectiveProps .target;
    cam .lookAt( x * globalScale, y * globalScale, z * globalScale );
  });

  createEffect( () => {
    setCamera( cam );
    // I don't know why this is necessary... I guess a camera is not added automatically
    scene .add( cam );
    onCleanup( () => scene .remove( cam ) );
  } );

  return (
    <T.OrthographicCamera ref={cam} position={perspectiveProps .position.map( e => e * globalScale )} up={perspectiveProps .up} >
      {props.children}
    </T.OrthographicCamera>
  );
}
