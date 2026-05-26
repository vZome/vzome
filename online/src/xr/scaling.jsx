import { useThree, useFrame, } from "solid-three";

import { useXR, useXRControllers, } from "./manager.jsx";

// ──────────────────────────────────────────────────────────────────────────────
// XRScaling — contribution that scales the model while both grips are squeezed.
// Scale factor is proportional to the controllers' world-space distance.

export const XRScaling = () =>
{
  const { onViewerStart, onViewerEnd, getRootGroup } = useXR();
  const { onScaleStart, onScaleEnd, getControllerSeparation } = useXRControllers();
  const store = useThree();

  let scalingActive = false;
  let baselineDistance = 1;
  let baselineScale = 1;
  let sessionStartScale = null;

  onViewerStart( () => {
    sessionStartScale = getRootGroup().scale.clone();

    onScaleStart( () => {
      const distance = getControllerSeparation();
      if ( distance <= 0 ) return;

      baselineDistance = distance;
      baselineScale = getRootGroup().scale.x;
      scalingActive = true;
    } );

    onScaleEnd( () => {
      scalingActive = false;
    } );
  } );

  onViewerEnd( () => {
    scalingActive = false;
    if ( sessionStartScale ) {
      getRootGroup().scale.copy( sessionStartScale );
      sessionStartScale = null;
    }
  } );

  useFrame( () => {
    if ( !scalingActive || !store?.gl?.xr?.isPresenting ) return;

    const distance = getControllerSeparation();
    if ( baselineDistance <= 0 || distance <= 0 ) return;

    const nextScale = Math.max( 1e-4, baselineScale * ( distance / baselineDistance ) );
    getRootGroup().scale.setScalar( nextScale );
  } );

  return null;
};
