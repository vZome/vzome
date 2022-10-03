
import { Show, For } from 'solid-js';
import Stack from "@suid/material/Stack"
import Button from "@suid/material/Button"
import Checkbox from "@suid/material/Checkbox";
import FormControlLabel from "@suid/material/FormControlLabel";

import { controllerAction, controllerProperty, rootController, subController } from '../controllers-solid.js';

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
  const availableOrbits = () => subController( props.controller, 'availableOrbits' );
  const buildOrbits = () => subController( props.controller, 'buildOrbits' );
  const propName = () => `orbitDot.${props.orbit}`;
  const orbits = () => controllerProperty( buildOrbits(), 'orbits', 'orbits', true ) || [];
  const selectedOrbit = () => controllerProperty( buildOrbits(), 'selectedOrbit', 'orbits', false );
  const selected = () => orbits() .indexOf( props.orbit ) >= 0;
  const isLastSelected = () => props.orbit === selectedOrbit();
  const details = () => {
    const value = controllerProperty( availableOrbits(), propName(), 'orbits', false ) || "";
    const [ colorHex, x=0, y=0 ] = value .split( '/' );
    const color = colorHex && hexToWebColor( colorHex );
    return { color, x, y };
  }
  const r = 0.05;
  const color = () => details().color;
  const x = () => details().x;
  const y = () => props.relativeHeight * ( 1 - details().y );

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
  const strutBuilder = () => subController( rootController(), 'strutBuilder' );
  const symmetry = () => subController( strutBuilder(), 'symmetry' );
  const availableOrbits = () => subController( symmetry(), 'availableOrbits' );
  const buildOrbits = () => subController( symmetry(), 'buildOrbits' );

  const orbits = () => controllerProperty( availableOrbits(), 'orbits', 'orbits', true );
  const oneAtATime = () => controllerProperty( buildOrbits(), 'oneAtATime', 'orbits', false );

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
        {/* <Show when={props.lastSelected}>
          <FormControlLabel style={marginedStyle}
            control={<Checkbox checked={oneAtATime()} color="primary" onChange={ singleAction } />}
            label="Single"
          />
        </Show> */}
      </Stack>
      <div style={{ position: 'relative', 'background-color': 'whitesmoke' }}>
        <svg viewBox={viewBox} stroke="black" stroke-width={0.005} >
          <g>
            {/* TODO: reversed triangle per the controller */}
            <polygon fill="none" points={triangleCorners}/>  { /* all dot X & Y values are in [0..1] */ }
            <For each={orbits()}>{ orbit =>
              <OrbitDot orbit={orbit} relativeHeight={relativeHeight} controller={symmetry()} />
            }</For>
          </g>
        </svg>
        {/* { children } */}
      </div>
    </div>
  )
}
