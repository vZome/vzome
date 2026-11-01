
import { onMount } from "solid-js";

import { useInteractionTool, useCameraControl, NO_PICKS } from "../../../viewer/context/interaction.jsx";
import { useSymmetry } from "../context/symmetry.jsx";

// Camera mode: every drag moves the camera, and nothing else.  Left-drag rotates (snapping the
// camera at the end, when snapping is on), right-drag or shift-drag pans, middle-drag and the
// wheel zoom.  The canvas's camera control decides what it allows; the camera panel's trackball,
// for example, only rotates.
export const CameraTool = props =>
{
  const { snapCamera } = useSymmetry();
  const camera = useCameraControl();

  const [ _, setTool ] = useInteractionTool();
  const cursor = 'url(/app/classic/rotate.svg) 9 9, url(/app/classic/rotate.png) 9 9, grab';
  onMount( () => setTool( {

    picks: NO_PICKS,

    cursor,

    suppressContextMenu: true, // the right button pans

    hint: {
      title: 'Camera mode (C)',
      lines: [
        'Drag: rotate',
        'Right-drag or shift-drag: pan',
        'Wheel or middle-drag: zoom',
      ],
    },

    onWheel: camera.onWheel,

    onDragStart: e => {
      switch ( e.button ) {
        case 0:  return e.shiftKey? camera.pan( e ) : camera.rotate( e, { onEnd: snapCamera } );
        case 1:  return camera.zoom( e );
        case 2:  return camera.pan( e );
        default: return null;
      }
    },
  } ) );

  return null;
}
