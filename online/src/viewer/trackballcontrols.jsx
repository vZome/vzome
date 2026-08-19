
// modified copy from https://github.com/devinxi/vinxi/blob/1514f966d9cdcc2c19e2733a8c7bf03831f7ecf3/packages/solid-drei/src/OrbitControls.tsx

import { createEffect, createMemo, onCleanup, mergeProps } from "solid-js";
import { useFrame, useThree } from 'solid-three';
import { TrackballControls as TrackballControlsImpl } from "three-stdlib";

import { useCamera } from "../viewer/context/camera.jsx";
import { useInteractionTool } from "../viewer/context/interaction.jsx";
import { useWebXRClient } from "../viewer/context/webxr.jsx";

export const TrackballControls = (props) =>
{
  props = mergeProps( { rotateSpeed: 4.5, zoomSpeed: 3, panSpeed: 1 }, props );
  const { perspectiveProps, trackballProps, name, cancelTweens } = useCamera();
  const [ tool ] = useInteractionTool();
  const { canvas, bounds } = useThree();
  const { setTrackball } = useWebXRClient();

  createEffect(() => {
    // SV: This effect is necessary so that we get correctly connected to the domElement
    //   *after* it has been connected to the document and assigned a valid size.
    if ( bounds.height < 0 ) // should never happen, just making a dependency
      console.log( 'height is', bounds.height ); // This is the change we care about.
    trackballControls() .connect( canvas );
  });

  const trackballControls = createMemo( () => {
    const controls = new TrackballControlsImpl( trackballProps .camera );
    setTrackball(controls);
    return controls;
  } );

  useFrame(() => {
    let controls = trackballControls();
    // If we want to coordinate with Interaction tools, we should avoid this update
    //  when doing a drag.
    if ( controls.enabled ) {
      controls.update();
    }
  });

  createEffect( () => {
    trackballControls() .enabled = ( tool === undefined ) || tool .allowTrackball();
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

    controls.connect( canvas );

    // These listeners are now tightly coupled to the camera and tool contexts.
    //   Earlier, the listeners were injected as props.
    const onStart = () => cancelTweens();
    const onChange = () => trackballProps .sync( controls .target, name );
    const onEnd = () => ( tool !== undefined ) && tool .onTrackballEnd();
    controls .addEventListener( "start", onStart );
    controls .addEventListener( "change", onChange );
    controls .addEventListener( "end", onEnd );
    onCleanup(() => {
      controls .removeEventListener( "start", onStart );
      controls .removeEventListener( "change", onChange );
      controls .removeEventListener( "end", onEnd );
      controls .dispose();
    });
  });

  createEffect(() => {
    const [ x, y, z ] = perspectiveProps .target;
    trackballControls() .target .set( x, y, z );
  });

  // createEffect(() => {
  //   if (props.makeDefault) {
  //     const old = get()().controls;
  //     set()({ controls: trackballControls() });
  //     onCleanup(() => set()({ controls: old }));
  //   }
  // });

  return null;
};