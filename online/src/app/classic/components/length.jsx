
import { Show, createEffect, createSignal, Switch as Case, Match, createMemo } from 'solid-js';

import Button from '@suid/material/Button';
import Switch from '@suid/material/Switch';
import Select from "@suid/material/Select";
import MenuItem from "@suid/material/MenuItem";
import Radio from '@suid/material/Radio';
import RadioGroup from '@suid/material/RadioGroup';
import FormControlLabel from '@suid/material/FormControlLabel';
import FormControl from '@suid/material/FormControl';
import FormLabel from '@suid/material/FormLabel';
// import Slider from '@suid/material/Slider';

import { controllerAction, controllerProperty, subController } from '../../../viewer/util/controllers-solid.js';
import { useWorkerClient } from '../../../viewer/context/worker.jsx';

const sliderLimit = 6;
const sliderMarks = Array.from( { length: 2*sliderLimit+1 }, (_, i) => i-sliderLimit ) .map( i => ({ value: i }));
sliderMarks[ sliderLimit ].label = 'unit';

const Slider = props =>
  <div class='slider'>
    <div class='slider-indicator' style={{
      'background-color': 'black',
      position: 'absolute',
      top: `${ 50 - 7 * props.defaultValue - 2 }%`,
      left: '25%',
      height: '6px',
      width: '18px',
    }}></div>
    <For each={sliderMarks}>{ ({ value, label }) =>
      <div style={{
        'background-color': 'black',
        position: 'absolute',
        top: `${ 50 - 7 * value }%`,
        left: '50%',
        height: '1px',
        width: '12px',
      }}></div>
    }</For>
  </div>

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

const ScaleBy = props =>
{
  const multipliers = () => controllerProperty( props.controller, 'field.multipliers', 'field.multipliers', true ) || [ ' ' ];
  const number = () => multipliers() .length;
  const setMultiplier = evt => {
    const multiplier = evt.target.value;
    controllerAction( props.controller, `setMultiplier.${multiplier}` );
  }

  return (
    <div id='scale-factors' style={{ 'background-color': 'whitesmoke', padding: '0.4em', margin: '0.4em' }}>
      <FormControl sx={{ m: 1, minWidth: 90 }} size="small" >
        <FormLabel id="scale-by-label">Scale by</FormLabel>

        <Case
          fallback={
            <span>{multipliers()[0]}</span>
          }>
          <Match when={ [2,3] .includes( number() )}>
            <RadioGroup row name="scale-radio-group" aria-labelledby="scale-by-label" defaultValue={0} onChange={setMultiplier} >
              <For each={multipliers()}>{ (m,i) => { console.log( 'mult', m, i() );
                return <FormControlLabel value={i()} control={<Radio />} label={m} /> }
              }</For>
            </RadioGroup>
          </Match>
          <Match when={number() > 3}>
            <Select id="multiplier" label="multiplier" defaultValue={0} onChange={setMultiplier} >
              <For each={multipliers()}>{ (m,i) =>
                <MenuItem value={i()}>{m}</MenuItem>
              }</For>
            </Select>
          </Match>
        </Case>

      </FormControl>
    </div>
  );
}

export const StrutLengthPanel = props =>
{
  const orbit = () => controllerProperty( props.controller, 'selectedOrbit' );
  const halfSizes = () => controllerProperty( props.controller, 'halfSizes', 'selectedOrbit', false ) === 'true';
  const color = () => controllerProperty( props.controller, 'color', 'selectedOrbit', false );
  const backgroundColor = () => ( color() && hexToWebColor( color() ) ) || 'whitesmoke';
  const longName = () => controllerProperty( props.controller, 'scaleName.long', 'selectedOrbit', false );
  const mediumName = () => controllerProperty( props.controller, 'scaleName.medium', 'selectedOrbit', false );
  const shortName = () => controllerProperty( props.controller, 'scaleName.short', 'selectedOrbit', false );
  const supershortName = () => controllerProperty( props.controller, 'scaleName.superShort', 'selectedOrbit', false );

  const lengthController = () => subController( props.controller, `length.${orbit()}` );
  const realScale = () => controllerProperty( lengthController(), 'scale', 'length', false );
  const unitText = () => controllerProperty( lengthController(), 'unitText', 'length', false );
  const scaleFactorHtml = () => controllerProperty( lengthController(), 'scaleFactorHtml', 'length', false );
  const lengthText = () => controllerProperty( lengthController(), 'lengthMathML', 'length', false );
  const mathML = createMemo( () => {
    const el = <math></math>;
    el .innerHTML = lengthText();
    return el;
  })

  const [ scale, setScale ] = createSignal(0); // TODO should be realScale()?
  createEffect( () => {
    if ( orbit() ) {
      const value = realScale()
      value && setScale( value );
    }
  });

  const changeScale = (change) => (evt) =>
  {
    setScale( change );
    controllerAction( lengthController(), 'setProperty', { name: 'scale', value: scale() } );
  }
  const scaleUp = () =>
  {
    setScale( s=>++s );
    controllerAction( lengthController(), 'scaleUp' );
  }
  const scaleDown = () =>
  {
    setScale( s=>--s );
    controllerAction( lengthController(), 'scaleDown' );
  }
  const predefinedScale = ( scale, action ) => (evt) =>
  {
    setScale( scale );
    controllerAction( lengthController(), action );
  }

  const half = () => controllerProperty( lengthController(), 'half', 'length', false ) === 'true';
  const toggleHalf = () => controllerAction( lengthController(), 'toggleHalf' );

  const PredefButton = props =>
    <Button variant="outlined" sx={{
        padding: '2px 9px',
        color: 'black',
        border: '1px solid black',
        margin: '4px',
        backgroundColor: 'whitesmoke',
      }} onClick={ predefinedScale( props.scale, props.action ) } >{props.label}</Button>;

  return (
    <Show when={orbit()}>
    <div id='strut-length' class='grid-rows-fr-min' >
      <div id='change-size' class='grid-cols-2-1' >
        <div id='scales-and-slider' class='grid-rows-min-1' style={{ 'background-color': backgroundColor(), 'border-radius': '5px' }}>
          <ScaleBy controller={lengthController()} />
          <div id='up-down-slider' class='grid-cols-min-1 orbit-scale'>
            <div id='up-down' class='grid-rows-1-1 pad-4px' >
              <button aria-label='scale-up' class='scale-button' onClick={scaleUp}>
                <img src='./resources/org/vorthmann/zome/ui/scaleUp.gif'/>
              </button>
              <button aria-label='scale-down' class='scale-button' onClick={scaleDown}>
                <img src='./resources/org/vorthmann/zome/ui/scaleDown.gif'/>
              </button>
            </div>
            <div id='scale-slider' class='scale-slider' >
              <Slider orientation='vertical'
                defaultValue={realScale()}
                // getAriaValueText='ariaValueText'
                aria-labelledby="strut-scale-slider"
                valueLabelDisplay="off"
                step={1}
                marks={sliderMarks} track={false}
                min={-sliderLimit}
                max={sliderLimit}
              />
            </div>
          </div>
        </div>
        <div id='scale-predefs'
            style={{ 'grid-template-rows': '1fr 1fr 1fr 1fr 1fr',
              'margin-block-start': 'auto',
              'align-content': 'space-around',
              'display': 'grid'}}>
          <Show when={halfSizes()} fallback={<div></div>}>
            <FormControlLabel label="half" sx={{ margin: 'auto' }}
              control={
                <Switch checked={half()} onChange={ toggleHalf } size='small' inputProps={{ "aria-label": "half-sizes" }} />
              }/>
          </Show>
          <PredefButton scale={3} action='long'       label={longName()} />
          <PredefButton scale={2} action='medium'     label={mediumName()} />
          <PredefButton scale={1} action='short'      label={shortName()} />
          <PredefButton scale={0} action='supershort' label={supershortName()} />
        </div>
      </div>
      <div id='length-display' style={{ 'background-color': 'whitesmoke', 'margin-left': '1em' }}>
        <div style={{ 'min-height': '22px' }}><span>unit  = </span><span class='bold'>{unitText()}</span></div>
        <div style={{ 'min-height': '22px' }}>{scaleFactorHtml()} unit</div>
        <div style={{ 'min-height': '30px', 'margin-left': '1em' }}>=  <span class='bold'>{mathML()}</span></div>
      </div>
    </div>
    </Show>
  );
}