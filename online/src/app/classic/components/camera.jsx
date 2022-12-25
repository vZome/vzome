
export const CameraControls = props =>
{
  return (
    <div id='camera-controls' style={{ display: 'grid', 'grid-template-rows': 'min-content min-content' }}>
      <div id='camera-buttons' class='placeholder' style={{ 'min-height': '60px' }} >perspective | snap | outlines</div>
      <div id='ball-and-slider' style={{ display: 'grid', 'grid-template-columns': '1fr min-content', 'min-height': 'min-content' }}>
        <div id='camera-trackball' class='placeholder' style={{ 'min-height': '200px', 'min-width': '200px' }} >symmetry trackball</div>
        <div id='zoom-slider' class='placeholder' style={{ 'min-height': '100px', 'min-width': '70px' }} >zoom</div>
      </div>
    </div>
  )
}

