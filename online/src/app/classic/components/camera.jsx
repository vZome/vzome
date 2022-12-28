
import { controllerProperty } from '../controllers-solid.js';
import { Canvas } from './canvas.jsx';
import { solidify } from '../solid-react.jsx';
import { icosahedralScene } from '../icosahedral-vef.js';

const SymmetryCanvas = solidify( Canvas );

export const CameraControls = props =>
{
  // TODO: use modelResourcePath to look up the scene to use, somehow, rather than hardcoding icosahedralScene
  const modelResourcePath = () =>
    controllerProperty( props.symmController, 'modelResourcePath' );

  // Why isn't this reactive when props.bkgdColor changes for a loaded model?
  const scene = () => ({ ...icosahedralScene, lighting: { ...icosahedralScene.lighting, backgroundColor: props.bkgdColor } } )

  return (
    <div id='camera-controls' style={{ display: 'grid', 'grid-template-rows': 'min-content min-content' }}>
      <div id='camera-buttons' class='placeholder' style={{ 'min-height': '60px' }} >perspective | snap | outlines</div>
      <div id="ball-and-slider" style={{ display: 'grid', 'grid-template-columns': 'min-content 1fr' }}>
        <div id="camera-trackball" style={{ border: '1px solid' }}>
          <SymmetryCanvas scene={scene()} trackball={false} style={{ position: 'relative', height: '200px', 'max-width': '240px' }} />
        </div>
        <div id='zoom-slider' class='placeholder' style={{ 'min-height': '100px', 'min-width': '60px' }} >zoom</div>
      </div>
    </div>
  )
}
