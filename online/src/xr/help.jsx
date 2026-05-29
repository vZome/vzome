
import { useThree, } from "solid-three";
import { createText } from "three-stdlib";

import { useXRSession, useXRControllers, xrViewerPose, } from "./manager.jsx";

// ──────────────────────────────────────────────────────────────────────────────
// XRInstructionText — contribution that displays a floating text label when a
// session starts, positioned in front of each controller.  Hides when the user grips.

export const XRInstructionText = ( props ) =>
{
  const { onViewerStart, onViewerEnd } = useXRSession();
  const { onControllerConnected, onGripStart } = useXRControllers();
  const store = useThree();

  let instructionTextL = null;
  let instructionTextR = null;

  onViewerStart( () => {
    onGripStart( () =>
    {
      if ( instructionTextL ) instructionTextL.visible = false;
      if ( instructionTextR ) instructionTextR.visible = false;
    } );
  });

  onControllerConnected( ( controller, handedness, hand ) =>
  {
    if (handedness === 'left') {
      instructionTextL = createText( props.text, 0.03 );
      controller .add( instructionTextL );
      instructionTextL.position.set( 0, 0.1, 0 );
      instructionTextL.rotation.set( -Math.PI / 6, Math.PI / 6, 0 );
    } else {
      instructionTextR = createText( props.text, 0.03 );
      controller .add( instructionTextR );
      instructionTextR.position.set( 0, 0.1, 0 );
      instructionTextR.rotation.set( -Math.PI / 6, -Math.PI / 6, 0 );
    }
  } );

  onViewerEnd( () =>
  {
    store.scene.remove( instructionTextL );
    store.scene.remove( instructionTextR );
    instructionTextL = null;
    instructionTextR = null;
  });

  return null;
};
