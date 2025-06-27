
import { LightedTrackballCanvas } from './ltcanvas.jsx';
import { ShapedGeometry } from './geometry.jsx';
import { mergeProps } from 'solid-js';
import { useScene } from './context/scene.jsx';

const SceneCanvas = ( props ) =>
{
  props = mergeProps( { rotateSpeed: 4.5, zoomSpeed: 3, panSpeed: 1 }, props );

  const { scene } = useScene();

  return (
    <LightedTrackballCanvas
        height={props.height} width={props.width} rotationOnly={props.rotationOnly}
        rotateSpeed={props.rotateSpeed} zoomSpeed={props.zoomSpeed} panSpeed={props.panSpeed} >
      <Show when={ () => props.scene?.shapes }>
        <ShapedGeometry embedding={scene?.embedding} shapes={scene?.shapes} />
      </Show>
      {props.children}
    </LightedTrackballCanvas>
  );
}

export { SceneCanvas };
