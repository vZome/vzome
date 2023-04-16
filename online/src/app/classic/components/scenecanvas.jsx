

import { LightedTrackballCanvas } from './ltcanvas.jsx';
import { ShapedGeometry } from './geometry.jsx';

const SceneCanvas = ( props ) =>
{
  return (
    <LightedTrackballCanvas toolActions={props.toolActions} height={props.height} width={props.width} >
      { props.scene?.shapes &&
        <ShapedGeometry embedding={props.scene.embedding} shapes={props.scene.shapes} toolActions={props.toolActions} />
      }
      {props.children3d}
    </LightedTrackballCanvas>
  );
}

export { SceneCanvas };
