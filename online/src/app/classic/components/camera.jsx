
import { SceneCanvas } from '../../../viewer/solid/scenecanvas.jsx';
import { useWorkerClient } from '../../../workerClient/index.js';
import { controllerProperty } from '../../../workerClient/controllers-solid.js';
import { icosahedralScene } from '../icosahedral-vef.js';
import { octahedralScene } from '../octahedral-vef.js';

const scenes = {
  icosahedral: icosahedralScene,
  octahedral: octahedralScene,
};

export const CameraControls = props =>
{
  const { state, rootController } = useWorkerClient();
  const symmetry = () => controllerProperty( rootController(), 'symmetry' );
  const bkgdColor = () => state.scene ?.lighting ?.backgroundColor;

  // TODO: use symmetry to look up the scene to use, somehow, rather than hardcoding icosahedralScene
  // Why isn't this reactive when props.bkgdColor changes for a loaded model?
  const scene = () => {
    const symmScene = scenes[ symmetry() || 'icosahedral' ];
    return ({ ...symmScene, lighting: { ...symmScene.lighting, backgroundColor: bkgdColor() } } );
  }

  return (
    <div id='camera-controls' style={{ display: 'grid', 'grid-template-rows': 'min-content min-content' }}>
      <div id='camera-buttons' class='placeholder' style={{ 'min-height': '60px' }} >perspective | snap | outlines</div>
      <div id="ball-and-slider" style={{ display: 'grid', 'grid-template-columns': 'min-content 1fr' }}>
        <div id="camera-trackball" style={{ border: '1px solid' }}>
          <SceneCanvas scene={scene()} trackball={false} height="200px" width="240px" />
        </div>
        <div id='zoom-slider' class='placeholder' style={{ 'min-height': '100px', 'min-width': '60px' }} >zoom</div>
      </div>
    </div>
  )
}
