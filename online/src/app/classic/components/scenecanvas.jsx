

import { createEffect } from 'solid-js';
import { LightedTrackballCanvas } from './ltcanvas.jsx';
import { ShapedGeometry } from './geometry.jsx';

const SceneCanvas = ( props ) =>
{
  return (
    <Show when={ () => props.scene?.shapes }>
      <LightedTrackballCanvas toolActions={props.toolActions} height={props.height} width={props.width} >
        <ShapedGeometry embedding={props.scene.embedding} shapes={props.scene?.shapes} toolActions={props.toolActions} />
        {props.children3d}
      </LightedTrackballCanvas>
    </Show> );
}

export { SceneCanvas };
