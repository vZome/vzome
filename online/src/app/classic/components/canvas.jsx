
import React from 'react';

import { DesignCanvas } from '../../../ui/viewer/designcanvas.jsx';
import { ShapedGeometry } from '../../../ui/viewer/geometry.jsx';

export const Canvas = ( { scene, callbacks={}, trackball } ) => (
  <DesignCanvas lighting={scene.lighting} sceneCamera={scene.camera} syncCamera={()=>{}} handleBackgroundClick={callbacks.bkgdClick} trackball={trackball} >
    { scene.shapes &&
      <ShapedGeometry embedding={scene.embedding} shapes={scene.shapes} callbacks={callbacks} />
    }
  </DesignCanvas>
);