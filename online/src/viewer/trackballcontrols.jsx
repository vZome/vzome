
// modified copy from https://github.com/devinxi/vinxi/blob/1514f966d9cdcc2c19e2733a8c7bf03831f7ecf3/packages/solid-drei/src/OrbitControls.tsx

import { createEffect, createMemo, onCleanup } from "solid-js";
import { useFrame, useThree } from "solid-three";
import { TrackballControls as TrackballControlsImpl } from "three-stdlib";

export const TrackballControls = (props) =>
{
  const gl = useThree(({ gl }) => gl);
  const set = useThree(({ set }) => set);
  const get = useThree(({ get }) => get);
  const size = useThree(({ size }) => size);

  createEffect(() => {
    // SV: This effect is necessary so that we get correctly connected to the domElement
    //   *after* it has been connected to the document and assigned a valid size.
    if ( size().height < 0 ) // should never happen, just making a dependency
      console.log( 'height is', size().height ); // This is the change we care about.
    trackballControls() .connect( gl().domElement );
  });

  const trackballControls = createMemo( () => {
    return new TrackballControlsImpl( props.camera, gl().domElement );
  } );

  useFrame(() => {
    let controls = trackballControls();
    if ( controls.enabled ) {
      controls.update();
    }
  });

  createEffect( () => {
    trackballControls() .enabled = props.enabled;
  });

  createEffect(() =>
  {
    const controls = trackballControls();

    controls.staticMoving = true;
    if ( props.rotationOnly ) {
      controls.noZoom = true;
      controls.noPan = true;
    } else {
      controls.noZoom = props.zoomSpeed===0;
      controls.zoomSpeed = props.zoomSpeed;
      controls.noPan = props.panSpeed===0;
      controls.panSpeed = props.panSpeed;
    }
    controls.rotateSpeed = props.rotateSpeed;

    controls.connect( gl().domElement );

    const onChange = () => props.sync( trackballControls() .target );
    controls .addEventListener( "change", onChange );

    onCleanup(() => {
      controls .removeEventListener( "change", onChange );
      controls .dispose();
    });
  });

  createEffect(() => {
    const [ x, y, z ] = props.target;
    trackballControls() .target .set( x, y, z );
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