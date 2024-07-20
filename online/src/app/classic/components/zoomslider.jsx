
import { Slider } from "@kobalte/core/slider";

import { INITIAL_DISTANCE, useCamera } from "../../../viewer/context/camera";

/*
  NOTE: the term "magnification" is used here, but it is really not a good term
    for the quantity, which is the log of distance.  It was used in desktop vZome, so I have continued here.
*/

const MAX_MAG = 3;
const MIN_MAG = -3;
const MAX_TICKS = 100; // 100 ticks in the slider by default
const MAG_PER_TICKS = ( MIN_MAG - MAX_MAG ) / MAX_TICKS;

export const ZoomSlider = () =>
{
  const { state, setDistance } = useCamera();
  const magnification = () => Math.log( state.camera.distance / INITIAL_DISTANCE );

  const ticks = () =>
  {
    let mag = magnification();
    mag = Math.max( MIN_MAG, Math.min( MAX_MAG, mag ) ); // zoom can go outside of the slider, but the slider can't
    const ticks = Math.round( ( mag - MAX_MAG ) / MAG_PER_TICKS );
    return [ ticks ];
  }

  const setTicks = ( v ) =>
  {
    const [ ticks ] = v;
    const magnification = ( ticks * MAG_PER_TICKS ) + MAX_MAG;
    const distance = INITIAL_DISTANCE * Math.pow( Math.E, magnification );
    setDistance( distance );
  }

  return (
    <Slider class="SliderRoot" orientation="vertical" 
        value={ ticks() } onChange={ setTicks }>
      <div class="SliderLabel">
        <Slider.Label>Distance</Slider.Label>
      </div>
      <div class='near-label'>Near</div>
      <div class='far-label'>Far</div>
      <Slider.Track class="SliderTrack">
        <Slider.Thumb class="SliderThumb">
          <Slider.Input />
        </Slider.Thumb>
      </Slider.Track>
    </Slider>
  );
}
