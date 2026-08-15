
import { LightedTrackballCanvas } from './ltcanvas.jsx';
import { ShapedGeometry } from './geometry.jsx';
import { SymmetryGeometry } from './symmetry-geometry.jsx';
import { mergeProps, createResource, Show } from 'solid-js';
import { useScene } from './context/scene.jsx';
import { canUseWebGPURenderer } from './renderer-support.js';

const SceneCanvas = ( props ) =>
{
  props = mergeProps( { rotateSpeed: 4.5, zoomSpeed: 3, panSpeed: 1 }, props );

  const { scene } = useScene();

  // Probe (once, cached) whether WebGPURenderer can initialize on this machine. Until it
  // resolves, webgpuOk() is undefined; we treat that as "not yet WebGL" so we don't force the
  // fallback prematurely, and gate the actual Canvas mount on it having resolved below.
  const [ webgpuOk ] = createResource( canUseWebGPURenderer );
  const useWebGL = () => webgpuOk() === false;
  // The classic WebGLRenderer can't consume the symmetry renderer's TSL node materials, so the
  // fallback rides a combined toggle: WebGLRenderer + ShapedGeometry (symmetryRenderer off).
  const useSymmetry = () => props.symmetryRenderer && ! useWebGL();

  return (
    <Show when={ webgpuOk() !== undefined }>
      <LightedTrackballCanvas
          height={props.height} width={props.width} rotationOnly={props.rotationOnly}
          rotateSpeed={props.rotateSpeed} zoomSpeed={props.zoomSpeed} panSpeed={props.panSpeed}
          useWebGL={useWebGL()} symmetryRenderer={useSymmetry()} >
        <Show when={ () => props.scene?.shapes }>
          { useSymmetry()
            ? <SymmetryGeometry embedding={scene?.embedding} shapes={scene?.shapes} orientations={scene?.orientations} polygons={scene?.polygons} symmetryId={scene?.symmetryId} />
            : <ShapedGeometry embedding={scene?.embedding} shapes={scene?.shapes} />
          }
        </Show>
        {props.children}
      </LightedTrackballCanvas>
    </Show>
  );
}

export { SceneCanvas };
