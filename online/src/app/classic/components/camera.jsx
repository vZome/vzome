
import { SceneCanvas } from '../../../viewer/solid/scenecanvas.jsx';
import { useWorkerClient } from '../../../workerClient/index.js';

export const CameraControls = props =>
{
  const { state } = useWorkerClient();
  const bkgdColor = () => state.scene ?.lighting ?.backgroundColor;

  const scene = () => {
    const symmScene = state.trackballScene;
    return ({ ...symmScene, lighting: { ...symmScene?.lighting, backgroundColor: bkgdColor() } } );
  }

  return (
    <div id='camera-controls' style={{ display: 'grid', 'grid-template-rows': 'min-content min-content' }}>
      <div id='camera-buttons' class='placeholder' style={{ 'min-height': '60px' }} >perspective | snap | outlines</div>
      <div id="ball-and-slider" style={{ display: 'grid', 'grid-template-columns': 'min-content 1fr' }}>
        <div id="camera-trackball" style={{ border: '1px solid' }}>
          <SceneCanvas scene={scene()} trackball={false} height="200px" width="240px" rotationOnly={true} />
        </div>
        <div id='zoom-slider' class='placeholder' style={{ 'min-height': '100px', 'min-width': '60px' }} >zoom</div>
      </div>
    </div>
  )
}
