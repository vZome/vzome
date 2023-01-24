
// import Slider from '@suid/material/Slider';

const Slider = props => <div class='slider'></div>

import { controllerAction, controllerProperty, subController } from '../controllers-solid.js';

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

export const StrutLengthPanel = props =>
{
  const orbitController = () => subController( props.controller, 'currentLength' );
  const color = () => controllerProperty( orbitController(), 'color', 'selectedOrbit', false );
  const backgroundColor = () => ( color() && hexToWebColor( color() ) ) || 'whitesmoke';

  return (
    <div id='strut-length' class='grid-rows-fr-min' >
      <div id='change-size' class='grid-cols-2-1' >
        <div id='strut-length' class='grid-rows-min-1' style={{ 'min-height': '220px' }}>
          <div id='scale-factors' class='placeholder' style={{ 'min-height': '35px' }}>
            scale by
          </div>
          <div id='colored-panel' class='grid-cols-min-1 orbit-scale' style={{ 'background-color': backgroundColor() }}>
            <div id='up-down' class='grid-rows-1-1 pad-4px' >
              <button aria-label='scale-up' class='scale-button'>
                <img src='./icons/misc/scaleUp.gif'/>
              </button>
              <button aria-label='scale-down' class='scale-button'>
                <img src='./icons/misc/scaleDown.gif'/>
              </button>
            </div>
            <div id='scale-slider' class='scale-slider' >
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
        <div id='strut-length' class='placeholder' style={{ 'min-height': '50px' }}>
          predef buttons
        </div>
      </div>
      <div id='strut-length' class='placeholder' style={{ 'min-height': '50px' }}>
        custom length
      </div>
    </div>
  );
}