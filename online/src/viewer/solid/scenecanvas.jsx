
import { LightedTrackballCanvas } from './ltcanvas.jsx';
import { ShapedGeometry } from './geometry.jsx';

const SceneCanvas = ( props ) =>
{
  return (
    <LightedTrackballCanvas toolActions={props.toolActions} sceneCamera={props.scene?.camera}
        trackball={props.trackball}
        height={props.height} width={props.width} >
      <Show when={ () => props.scene?.shapes }>
        <ShapedGeometry embedding={props.scene?.embedding} shapes={props.scene?.shapes} toolActions={props.toolActions} />
      </Show>
      {props.children3d}
    </LightedTrackballCanvas>
  );
}

export { SceneCanvas };
