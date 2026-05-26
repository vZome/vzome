
import { useThree, } from "solid-three";
import { createText } from "three-stdlib";

import { useXR, useXRControllers, xrViewerPose, } from "./manager.jsx";

// ──────────────────────────────────────────────────────────────────────────────
// XRInstructionText — contribution that displays a floating text label when a
// session starts, positioned in front of the viewer.  Hides when the user grips.

export const XRInstructionText = ( props ) =>
{
  const { onViewerStart, onViewerEnd } = useXR();
  const { onGripStart } = useXRControllers();
  const store = useThree();

  let instructionText = null;

  onViewerStart( () => {
    instructionText = createText( props.text ?? 'Grip to move the model', 0.06 );
    store.scene.add( instructionText );

    const { viewerPos, viewerQuat, forward } = xrViewerPose( store );
    instructionText.position.copy(viewerPos).addScaledVector(forward, 0.8);
    instructionText.quaternion.copy(viewerQuat);

    onGripStart( () => { instructionText.visible = false; } );
  });

  onViewerEnd( () => {
    store.scene.remove( instructionText );
    instructionText = null;
  });

  return null;
};
