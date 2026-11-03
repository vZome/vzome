
import { createBooleanSetting, createSetting } from '../../framework/settings.js';

// The Rendering settings (see the Settings dialog, classic/settings.jsx).  Each is only the
// starting value for a new window: the camera panel's switches change the session, not these.
// The projection is also where a new design starts; opening a design then takes its own
// projection (its `parallel` attribute), so opening one never changes it.
//
//   projection     the main CameraProvider's `perspective` (app/index.jsx)
//   showOutlines   the main CameraProvider's `outlines` (app/index.jsx)
//   snapCamera     SymmetryProvider's snapping signal (context/symmetry.jsx)

export const PROJECTIONS = {
  orthographic: { label: 'Orthographic' },
  perspective:  { label: 'Perspective' },
};

export const projection = createSetting( 'vzome.projection', PROJECTIONS, 'perspective' );
export const showOutlines = createBooleanSetting( 'vzome.showOutlines', false );
export const snapCamera = createBooleanSetting( 'vzome.snapCamera', false );
