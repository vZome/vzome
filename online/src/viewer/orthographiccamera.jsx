
// Adapted from https://github.com/nksaraf/react-three-fiber/commit/581d02376d4304fb3bab5445435a61c53cc5cdc2

import { createEffect, untrack, onCleanup } from 'solid-js';
import { useThree } from 'solid-three';

export const OrthographicCamera = (props) =>
{
  const set = useThree(({ set }) => set);
  const scene = useThree(({ scene }) => scene);
  const camera = useThree(({ camera }) => camera);

  let cam;

  createEffect(() => {
    cam.near = props.near;
    cam.far = props.far;
    cam.left = -props.halfWidth;
    cam.right = props.halfWidth;
    const halfHeight = props.halfWidth / props.aspect;
    cam.top = halfHeight;
    cam.bottom = -halfHeight;
    cam.updateProjectionMatrix();
  });

  createEffect( () => {
    const [ x, y, z ] = props.target;
    cam .lookAt( x, y, z );
  });

  createEffect(() => {
    const oldCam = untrack(() => camera());
    set()({ camera: cam });
    scene() .add( cam ); // The camera will work without this, but the *lights* won't!
    onCleanup(() => {
      set()({ camera: oldCam });
      scene() .remove( cam );
    });
  });

  return <orthographicCamera ref={cam} position={props.position} {...props} />
}
