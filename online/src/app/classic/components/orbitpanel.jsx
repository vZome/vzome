
import { Show, For, createEffect, createSignal } from 'solid-js';
import Stack from "@suid/material/Stack"
import Button from "@suid/material/Button"
import Switch from "@suid/material/Switch";
import FormControlLabel from "@suid/material/FormControlLabel";
import InputLabel from "@suid/material/InputLabel";

import { controllerAction, controllerProperty } from '../../../workerClient/controllers-solid.js';
import { hexToWebColor } from './length.jsx';

export const OrbitDot = props =>
{
  const propName = () => `orbitDot.${props.orbit}`;
  const orbits = () => controllerProperty( props.controller, 'orbits', 'orbits', true ) || [];
  const selectedOrbit = () => controllerProperty( props.controller, 'selectedOrbit', 'orbits', false );
  const selected = () => orbits() .indexOf( props.orbit ) >= 0;
  const isLastSelected = () => props.orbit === selectedOrbit();
  const details = () => {
    const value = controllerProperty( props.controller, propName(), 'orbits', false ) || "";
    const [ colorHex, x=0, y=0 ] = value .split( '/' );
    const color = colorHex && hexToWebColor( colorHex );
    return { color, x, y };
  }
  const r = 0.05;
  const color = () => details().color;
  const x = () => details().x;
  const y = () => props.relativeHeight * ( 1 - details().y );

  const toggleDot = evt => controllerAction( props.controller, `toggleDirection.${props.orbit}` );

  return ( <Show when={color()} >
    <circle cx={x()} cy={y()} r={r} fill={color()} onClick={toggleDot} />
    { selected() &&
      <circle cx={x()} cy={y()} r={0.37*r} fill="black" onClick={toggleDot} />
    }
    { isLastSelected() &&
      <circle cx={x()} cy={y()} r={1.57*r} fill="none" />
    }
  </Show> )
}

export const OrbitPanel = props =>
{
  const oneAtATime = () => controllerProperty( props.controller, 'oneAtATime', 'orbits', false ) === 'true';

  const selectNone = () => controllerAction( props.controller, 'setNoDirections' );
  const selectAll = () => controllerAction( props.controller, 'setAllDirections' );
  const singleAction = (evt,value) => controllerAction( props.controller, `setSingleOrbit.${value}` );

  const marginedStyle = { margin: '8px' }
  const relativeHeight = 0.6;
  const triangleCorners = `0,${relativeHeight} 1,${relativeHeight} 0,0`
  const viewBox = `-0.1 -0.1 1.2 ${relativeHeight * 1.4}`;

  return (
    <div style={marginedStyle}>
      <InputLabel id="orbits-label">{props.label}</InputLabel>
      <Stack spacing={2} direction="row">
        <Button variant="outlined" style={marginedStyle} onClick={ selectNone } >None</Button>
        <Button variant="outlined" style={marginedStyle} onClick={ selectAll } >All</Button>
        <Show when={props.lastSelected}>
          <FormControlLabel style={marginedStyle} label="single"
            control={
            <Switch checked={oneAtATime()} onChange={singleAction} size='small' inputProps={{ "aria-label": "controlled" }} />
          }/>
        </Show>
      </Stack>
      <div style={{ position: 'relative', 'background-color': 'white' }}>
        <svg viewBox={viewBox} stroke="black" stroke-width={0.005} >
          <g>
            {/* TODO: reversed triangle per the controller */}
            <polygon fill="none" points={triangleCorners}/>  { /* all dot X & Y values are in [0..1] */ }
            <For each={props.orbits}>{ orbit =>
              <OrbitDot orbit={orbit} relativeHeight={relativeHeight} controller={ props.controller } />
            }</For>
          </g>
        </svg>
        { props.children }
      </div>
    </div>
  )
}
