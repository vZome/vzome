
// Adapted from https://github.com/nksaraf/react-three-fiber/commit/581d02376d4304fb3bab5445435a61c53cc5cdc2

import { createEffect, untrack, onCleanup } from 'solid-js';
import { useThree } from 'solid-three';

export const PerspectiveCamera = (props) =>
{
  const set = useThree(({ set }) => set);
  const camera = useThree(({ camera }) => camera);
  const size = useThree(({ size }) => size);
  // console.log( 'PerspectiveCamera sees', props.name );

  let cam;

  createEffect( () => {
    // console.log( props.name, 'PerspectiveCamera lookAt' );
    const [ x, y, z ] = props.target;
    cam .lookAt( x, y, z );

    // {
    //   console.log( props.name, 'look at', JSON.stringify( props.target ) );
    //   console.log( props.name, 'position', JSON.stringify( cam.position ) );
    // }
  });

  createEffect(() => {
    // console.log( props.name, 'PerspectiveCamera updateProjectionMatrix' );
    cam.near = props.near;
    cam.far = props.far;
    cam.fov = props.fov;
    cam.aspect = size().width / size().height;
    cam.updateProjectionMatrix();

    // {
    //   const { near, far, fov, aspect } = cam;
    //   console.log( props.name, 'zoom', JSON.stringify( { near, far, fov, aspect }, null, 2 ) );
    //   console.log( props.name, 'position', JSON.stringify( cam.position ) );
    // }
  });

  createEffect(() => {
    const oldCam = untrack(() => camera());
    set()({ camera: cam });
    onCleanup(() => set()({ camera: oldCam }));
  });

  return <perspectiveCamera ref={cam} position={props.position} {...props} />
}
