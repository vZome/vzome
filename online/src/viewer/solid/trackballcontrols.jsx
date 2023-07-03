
// modified copy from https://github.com/devinxi/vinxi/blob/1514f966d9cdcc2c19e2733a8c7bf03831f7ecf3/packages/solid-drei/src/OrbitControls.tsx

import { createEffect, createMemo, onCleanup } from "solid-js";
import { useFrame, useThree } from "solid-three";
import { TrackballControls as TrackballControlsImpl } from "three-stdlib";

export const TrackballControls = (props) => {
  const invalidate = useThree(({ invalidate }) => invalidate);
  const defaultCamera = useThree(({ camera }) => camera);
  const gl = useThree(({ gl }) => gl);
  const set = useThree(({ set }) => set);
  const get = useThree(({ get }) => get);
  const size = useThree(({ size }) => size);

  createEffect(() => {
    // SV: This effect is necessary so that we get correctly connected to the domElement
    //   *after* it has been connected to the document and assigned a valid size.
    if ( size().height < 0 ) // should never happen, just making a dependency
      console.log( 'height is', size().height ); // This is the change we care about.
    trackballControls().connect(gl().domElement);
  });

  const trackballControls = createMemo( () => {
    const camera = props.camera || defaultCamera();
    return new TrackballControlsImpl( camera, gl().domElement );
  } );

  useFrame(() => {
    let controls = trackballControls();
    if (controls.enabled) controls.update();
  });

  createEffect(() => {

    const callback = (e) => {
      invalidate();
      props.onChange?.(e);
    };

    // SV: these five added
    trackballControls().staticMoving = true;
    trackballControls().zoomSpeed = props.zoomSpeed;
    trackballControls().rotateSpeed = props.rotateSpeed;
    trackballControls().panSpeed = props.panSpeed;
    const [ x, y, z ] = props.target;
    trackballControls().target.set( x, y, z );

    trackballControls().connect(gl().domElement);
    trackballControls().addEventListener("change", callback);

    if (props.onStart) trackballControls().addEventListener("start", props.onStart);
    if (props.onEnd) trackballControls().addEventListener("end", props.onEnd);

    onCleanup(() => {
      trackballControls().removeEventListener("change", callback);
      if (props.onStart)
        trackballControls().removeEventListener("start", props.onStart);
      if (props.onEnd)
        trackballControls().removeEventListener("end", props.onEnd);
      trackballControls().dispose();
    });
  });

  createEffect(() => {
    if (props.makeDefault) {
      const old = get()().controls;
      set()({ controls: trackballControls() });
      onCleanup(() => set()({ controls: old }));
    }
  });

  return null;
};