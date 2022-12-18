
import React, { forwardRef } from 'react';

import { LightedTrackballCanvas } from './ltcanvas.jsx';
import { ShapedGeometry } from './geometry.jsx';

const isLeftMouseButton = e =>
{
  e = e || window.event;
  if ( "which" in e )  // Gecko (Firefox), WebKit (Safari/Chrome) & Opera
    return e.which === 1
  else if ( "button" in e )  // IE, Opera 
    return e.button === 0
  return false
}

const SceneCanvasRef = ( { scene, toolRef, trackball, syncCamera, children3d }, exporterRef ) =>
{
  const handleBackgroundClick = ( e ) =>
  {
    if ( isLeftMouseButton( e ) && toolRef.current?.bkgdClick ) {
      e.stopPropagation()
      toolRef.current .bkgdClick();
    }
  }

  return (
    <LightedTrackballCanvas lighting={scene.lighting} sceneCamera={ { ...scene.camera } } trackball={trackball}
        syncCamera={syncCamera} handleBackgroundClick={handleBackgroundClick} >
      { scene.shapes &&
        <ShapedGeometry ref={exporterRef} embedding={scene.embedding} shapes={scene.shapes} toolRef={toolRef} />
      }
      {children3d}
    </LightedTrackballCanvas>
  );
}

export const SceneCanvas = forwardRef( SceneCanvasRef );
