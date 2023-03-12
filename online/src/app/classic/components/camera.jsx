
import { controllerProperty } from '../controllers-solid.js';
import { solidify } from '../solid-react.jsx';
import { icosahedralScene } from '../icosahedral-vef.js';
import { octahedralScene } from '../octahedral-vef.js';
import { SceneCanvas } from '../../../ui/viewer/scenecanvas.jsx';
import { createEffect } from 'solid-js';

const scenes = {
  icosahedral: icosahedralScene,
  octahedral: octahedralScene,
};

const SolidSceneCanvas = solidify( SceneCanvas );

export const CameraControls = props =>
{
  // TODO: use symmetry to look up the scene to use, somehow, rather than hardcoding icosahedralScene
  const symmetry = () => controllerProperty( props.controller, 'symmetry' );

  // Why isn't this reactive when props.bkgdColor changes for a loaded model?
  const scene = () => {
    const symmScene = scenes[ symmetry() || 'icosahedral' ];
    return ({ ...symmScene, lighting: { ...symmScene.lighting, backgroundColor: props.bkgdColor } } );
  }

  // createEffect( () => {
  //   console.log( "backgroundColor is ", scene() .lighting .backgroundColor );
  // } );

  return (
    <div id='camera-controls' style={{ display: 'grid', 'grid-template-rows': 'min-content min-content' }}>
      <div id='camera-buttons' class='placeholder' style={{ 'min-height': '60px' }} >perspective | snap | outlines</div>
      <div id="ball-and-slider" style={{ display: 'grid', 'grid-template-columns': 'min-content 1fr' }}>
        <div id="camera-trackball" style={{ border: '1px solid' }}>
          <SolidSceneCanvas scene={scene()} trackball={false} style={{ position: 'relative', height: '200px', 'max-width': '240px' }} />
        </div>
        <div id='zoom-slider' class='placeholder' style={{ 'min-height': '100px', 'min-width': '60px' }} >zoom</div>
      </div>
    </div>
  )
}
