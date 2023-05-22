
// Copied from https://github.com/nksaraf/react-three-fiber/commit/581d02376d4304fb3bab5445435a61c53cc5cdc2

import { createEffect, untrack, onCleanup } from 'solid-js';
import { useThree } from 'solid-three';

export const PerspectiveCamera = (props) =>
{
  const set = useThree(({ set }) => set);
  const camera = useThree(({ camera }) => camera);
  const size = useThree(({ size }) => size);

  let cam;

  createEffect(() => {
    cam.aspect = size().width / size().height;
    cam.updateProjectionMatrix();
  });

  createEffect(() => {
    const oldCam = untrack(() => camera());
    set()({ camera: cam });
    onCleanup(() => set()({ camera: oldCam }));
  });

  return <perspectiveCamera ref={cam} far={2000} near={0.1} fov={props.fov} position={props.position} {...props} />
}
