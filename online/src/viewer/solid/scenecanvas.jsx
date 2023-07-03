
import { LightedTrackballCanvas } from './ltcanvas.jsx';
import { ShapedGeometry } from './geometry.jsx';

const SceneCanvas = ( props ) =>
{
  return (
    <LightedTrackballCanvas sceneCamera={props.scene?.camera} lighting={props.scene?.lighting}
        height={props.height} width={props.width} >
      <Show when={ () => props.scene?.shapes }>
        <ShapedGeometry embedding={props.scene?.embedding} shapes={props.scene?.shapes} />
      </Show>
      {props.children}
    </LightedTrackballCanvas>
  );
}

export { SceneCanvas };
