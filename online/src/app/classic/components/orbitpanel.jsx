
import { Show, For } from 'solid-js';
import Stack from "@suid/material/Stack"
import Button from "@suid/material/Button"
import Checkbox from "@suid/material/Checkbox";
import FormControlLabel from "@suid/material/FormControlLabel";

import { controllerAction, requireProperty, rootController } from '../controllers-solid.js';

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

export const OrbitDot = props =>
{
  const buildOrbits = () => props.controller.buildOrbits;
  const availableOrbits = () => props.controller.availableOrbits;
  const propName = () => `orbitDot.${props.orbit}`;
  const selected = () => buildOrbits() .orbits .indexOf( props.orbit ) >= 0;
  const isLastSelected = () => props.orbit === buildOrbits() .selectedOrbit;
  const details = () => {
    const value = availableOrbits()[ propName() ] || "";
    const [ colorHex, x=0, y=0 ] = value .split( '/' );
    const color = colorHex && hexToWebColor( colorHex );
    return { color, x, y };
  }
  const r = 0.05;
  const color = () => details().color;
  const x = () => details().x;
  const y = () => props.relativeHeight * ( 1 - details().y );

  requireProperty( 'strutBuilder:symmetry:availableOrbits', propName(), 'orbits', false );

  const toggleDot = evt => controllerAction( buildOrbits(), `toggleDirection.${props.orbit}` );

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
  console.log( 'OrbitPanel: ', JSON.stringify( props, null, 2 ) );
  requireProperty( 'strutBuilder:symmetry:availableOrbits', 'orbits', 'orbits', true );
  requireProperty( 'strutBuilder:symmetry:buildOrbits', 'orbits', 'orbits', true );
  requireProperty( 'strutBuilder:symmetry:buildOrbits', 'oneAtATime', 'orbits', false );
  requireProperty( 'strutBuilder:symmetry:buildOrbits', 'selectedOrbit', 'orbits', false );

  const controller = () => {
    console.log( 'OrbitPanel.controller' );
    const root = rootController();
    return root?.strutBuilder?.symmetry;
  }
  const buildOrbits = () => ( controller() || {} ) .buildOrbits;
  const availableOrbits = () => ( controller() || {} ) .availableOrbits;
  const oneAtATime = () => ( buildOrbits() || {} ) .oneAtATime || false;

  const selectNone = () => controllerAction( buildOrbits(), 'setNoDirections' );
  const selectAll = () => controllerAction( buildOrbits(), 'setAllDirections' );
  const singleAction = () => controllerAction( buildOrbits(), 'oneAtATime' );

  const marginedStyle = { margin: '8px' }
  const relativeHeight = 0.6;
  const triangleCorners = `0,${relativeHeight} 1,${relativeHeight} 0,0`
  const viewBox = `-0.1 -0.1 1.2 ${relativeHeight * 1.4}`;

  return (
    <div style={marginedStyle}>
      <Stack spacing={2} direction="row">
        <Button variant="outlined" style={marginedStyle} onClick={ selectNone } >None</Button>
        <Button variant="outlined" style={marginedStyle} onClick={ selectAll } >All</Button>
        <Show when={props.lastSelected}>
          <FormControlLabel style={marginedStyle}
            control={<Checkbox checked={oneAtATime()} color="primary" onChange={ singleAction } />}
            label="Single"
          />
        </Show>
      </Stack>
      <div style={{ position: 'relative', 'background-color': 'whitesmoke' }}>
        <svg viewBox={viewBox} stroke="black" stroke-width={0.005} >
          <g>
            {/* TODO: reversed triangle per the controller */}
            <polygon fill="none" points={triangleCorners}/>  { /* all dot X & Y values are in [0..1] */ }
            <Show when={availableOrbits()} >
              <For each={availableOrbits().orbits}>{ orbit =>
                <OrbitDot orbit={orbit} relativeHeight={relativeHeight} controller={controller()} />
              }</For>
            </Show>
          </g>
        </svg>
        {/* { children } */}
      </div>
    </div>
  )
}
