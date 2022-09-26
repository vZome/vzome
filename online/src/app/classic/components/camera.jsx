
import React from 'react';

export const CameraControls = ( { controller }) =>
{
  return (
    <div id='camera-controls' style={{ display: 'grid', gridTemplateRows: 'min-content min-content' }}>
      <div id='camera-buttons' className='placeholder' style={{ minHeight: '60px' }} >perspective | snap | outlines</div>
      <div id='ball-and-slider' style={{ display: 'grid', gridTemplateColumns: '1fr min-content', minHeight: 'min-content' }}>
        <div id='camera-trackball' className='placeholder' style={{ minHeight: '200px', minWidth: '200px' }} >symmetry trackball</div>
        <div id='zoom-slider' className='placeholder' style={{ minHeight: '100px', minWidth: '70px' }} >zoom</div>
      </div>
    </div>
  )
}

