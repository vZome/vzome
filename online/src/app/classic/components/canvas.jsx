
import React from 'react';

import { DesignCanvas } from '../../../ui/viewer/designcanvas.jsx';
import { ShapedGeometry } from '../../../ui/viewer/geometry.jsx';

export const Canvas = ( { scene, callbacks={} } ) => (
  <DesignCanvas lighting={scene.lighting} sceneCamera={scene.camera} syncCamera={()=>{}} >
    { scene.shapes &&
      <ShapedGeometry embedding={scene.embedding} shapes={scene.shapes} callbacks={callbacks} />
    }
  </DesignCanvas>
);