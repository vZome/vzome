
import { LightedTrackballCanvas } from './ltcanvas.jsx';
import { ShapedGeometry } from './geometry.jsx';
import { mergeProps } from 'solid-js';

const SceneCanvas = ( props ) =>
{
  props = mergeProps( { rotateSpeed: 4.5 }, props );
  return (
    <LightedTrackballCanvas sceneCamera={props.scene?.camera} lighting={props.scene?.lighting}
        height={props.height} width={props.width} rotationOnly={props.rotationOnly} rotateSpeed={props.rotateSpeed} >
      <Show when={ () => props.scene?.shapes }>
        <ShapedGeometry embedding={props.scene?.embedding} shapes={props.scene?.shapes} />
      </Show>
      {props.children}
    </LightedTrackballCanvas>
  );
}

export { SceneCanvas };
