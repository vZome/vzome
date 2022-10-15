
import React from 'react';

import Slider from '@material-ui/core/Slider';

import { useControllerProperty } from '../controller-hooks.js';
import { subcontroller } from '../../../ui/viewer/store.js';

const sliderMarks = Array.from( { length: 13 }, (_, i) => i-6 ) .map( i => ({ value: i }));
sliderMarks[ 6 ].label = 'unit';

export const hexToWebColor = colorHex =>
{
  const color = colorHex .substring( 2 ); // remove "0x"
  if ( color .length === 2 )
    return `#0000${color}`
  else if ( color .length === 4 )
    return `#00${color}`
  else
    return `#${color}`
}

export const StrutLengthPanel = ( { controller }) =>
{
  const orbitController = subcontroller( controller, 'currentLength' );
  const color = useControllerProperty( orbitController, 'color', 'selectedOrbit', false );
  const backgroundColor = ( color && hexToWebColor( color ) ) || 'whitesmoke';

  return (
    <div id='strut-length' className='grid-rows-fr-min' >
      <div id='change-size' className='grid-cols-2-1' >
        <div id='strut-length' className='grid-rows-min-1' style={{ minHeight: '220px' }}>
          <div id='scale-factors' className='placeholder' style={{ minHeight: '35px' }}>
            scale by
          </div>
          <div id='colored-panel' className='grid-cols-min-1 orbit-scale' style={{ backgroundColor }}>
            <div id='up-down' className='grid-rows-1-1 pad-4px' >
              <button aria-label='scale-up' className='scale-button'>
                <img src='./icons/misc/scaleUp.gif'/>
              </button>
              <button aria-label='scale-down' className='scale-button'>
                <img src='./icons/misc/scaleDown.gif'/>
              </button>
            </div>
            <div id='scale-slider' className='scale-slider' >
              <Slider orientation='vertical'
                defaultValue={0}
                // getAriaValueText='ariaValueText'
                aria-labelledby="strut-scale-slider"
                valueLabelDisplay="off"
                step={1}
                marks={sliderMarks} track={false}
                min={-6}
                max={6}
              />
            </div>
          </div>
        </div>
        <div id='strut-length' className='placeholder' style={{ minHeight: '50px' }}>
          predef buttons
        </div>
      </div>
      <div id='strut-length' className='placeholder' style={{ minHeight: '50px' }}>
        custom length
      </div>
    </div>
  );
}