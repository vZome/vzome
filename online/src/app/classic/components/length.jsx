
import React from 'react';

import Slider from '@material-ui/core/Slider';

const sliderMarks = Array.from( { length: 13 }, (_, i) => i-6 ) .map( i => ({ value: i }));
sliderMarks[ 6 ].label = 'unit';

export const StrutLengthPanel = () =>
{
  const color = '#8412a0'
  return (
    <div id='strut-length' className='grid-rows-fr-min' >
      <div id='change-size' className='grid-cols-2-1' >
        <div id='strut-length' className='grid-rows-min-1' style={{ minHeight: '220px' }}>
          <div id='scale-factors' className='placeholder' style={{ minHeight: '35px' }}>
            scale by
          </div>
          <div id='colored-panel' className='grid-cols-min-1 orbit-scale' style={{ backgroundColor: color }}>
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