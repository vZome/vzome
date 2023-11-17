
// modified copy from https://github.com/devinxi/vinxi/blob/1514f966d9cdcc2c19e2733a8c7bf03831f7ecf3/packages/solid-drei/src/OrbitControls.tsx

import { createEffect, createMemo, onCleanup } from "solid-js";
import { useFrame, useThree } from "solid-three";
import { TrackballControls as TrackballControlsImpl } from "three-stdlib";

import { useRotation } from "./camera.jsx";
import { useInteractionTool } from "./interaction.jsx";
import { extractCameraState, injectCameraOrientation, useCameraState } from "./camera.jsx";

export const TrackballControls = (props) =>
{
  const [ tool ] = useInteractionTool();
  const enabled = () => ( tool === undefined ) || tool().allowTrackball;
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
    trackballControls() .connect( gl().domElement );
  });

  const thisCamera = () => props.camera || defaultCamera();

  const trackballControls = createMemo( () => {
    return new TrackballControlsImpl( thisCamera(), gl().domElement );
  } );

  useFrame(() => {
    let controls = trackballControls();
    if ( controls.enabled ) {
      controls.update();
    }
  });

  createEffect( () => {
    trackballControls() .enabled = enabled();
  });

  const { lastRotation, publishRotation } = useRotation();
  const { lookAt } = useCameraState();

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

    controls.addEventListener( 'change', (evt) => {
      if ( ! publishRotation ) return;
      // filter out pan and zoom changes
      // HACK! Assumes knowledge of TrackballControls internals
      if ( controls._state !== 0 && controls._state !== 3 ) // ROTATE, TOUCH_ROTATE
        return;
      publishRotation( extractCameraState( thisCamera(), controls.target ), thisCamera() );
    } );

    if (props.onStart) controls.addEventListener("start", props.onStart);
    if (props.onChange) controls.addEventListener("change", props.onChange);
    if (props.onEnd)   controls.addEventListener("end", props.onEnd);

    onCleanup(() => {
      if (props.onStart) controls.removeEventListener("start", props.onStart);
      if (props.onChange) controls.removeEventListener("change", props.onChange);
      if (props.onEnd)   controls.removeEventListener("end", props.onEnd);
      controls.dispose();
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

  createEffect( () => {
    if ( ! lastRotation ) return;
    let { cameraState, sourceCamera } = lastRotation();
    const camera = thisCamera();
    if ( sourceCamera && sourceCamera !== camera ) {
      // console.log( `${camera.vzomeName} receiving rotation from ${sourceCamera.vzomeName}` );
      // Can't use props.target here, since it is not up-to-date in some cases
      injectCameraOrientation( cameraState, lookAt(), camera );
    }
  });

  return null;
};