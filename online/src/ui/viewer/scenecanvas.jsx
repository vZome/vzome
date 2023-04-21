
import React, { forwardRef } from 'react';

import { LightedTrackballCanvas } from './ltcanvas.jsx';
import { ShapedGeometry } from './geometry.jsx';

const SceneCanvasRef = ( { scene, toolActions, trackball, syncCamera, children3d }, exporterRef ) =>
{
  return (
    <LightedTrackballCanvas lighting={scene.lighting} sceneCamera={ { ...scene.camera } } trackball={trackball}
        syncCamera={syncCamera} toolActions={toolActions} >
      { scene.shapes &&
        <ShapedGeometry ref={exporterRef} embedding={scene.embedding} shapes={scene.shapes} toolActions={toolActions} />
      }
      {children3d}
    </LightedTrackballCanvas>
  );
}

export const SceneCanvas = forwardRef( SceneCanvasRef );
