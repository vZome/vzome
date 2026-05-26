
import { Vector3, } from "three";
import { useThree, } from "solid-three";

import { useXR, useXRControllers, xrViewerPose, } from "./manager.jsx";

// ──────────────────────────────────────────────────────────────────────────────
// XRGripToMove — contribution that lets the user grip and drag the model.
// Positions the model in front of the viewer at session start and resets it on end.

export const XRGripToMove = () =>
{
  const { onViewerStart, onViewerEnd, getRootGroup } = useXR();
  const { onGripStart, onGripEnd } = useXRControllers();
  const store = useThree();

  onViewerStart( () => {
    const { viewerPos, forward } = xrViewerPose( store );
    getRootGroup().position
      .copy(viewerPos)
      .addScaledVector(forward, 0.7)
      .add( new Vector3(0, -0.2, 0) );

    onGripStart( ( controller ) => controller.attach( getRootGroup() ) );
    onGripEnd(   ( controller ) => store.scene.attach( getRootGroup() ) );
  });

  onViewerEnd( () => getRootGroup().position.copy( new Vector3(0, 0, 0) ) );

  return null;
};

