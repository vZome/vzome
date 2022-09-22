
import React, { useState } from 'react';
import IconButton from '@material-ui/core/IconButton'
import SettingsIcon from '@material-ui/icons/Settings'
import Menu from '@material-ui/core/Menu';
import MenuItem from '@material-ui/core/MenuItem';
import Button from '@material-ui/core/Button';
import FormGroup from '@material-ui/core/FormGroup';
import FormControlLabel from '@material-ui/core/FormControlLabel';
import Checkbox from '@material-ui/core/Checkbox';

import { useControllerProperty, useControllerAction } from './controller-hooks.js';
import { subcontroller } from '../../ui/viewer/store.js';

export const OrbitDot = ( { controllerPath, orbit, selectedOrbitNames, relativeHeight, isLastSelected } ) =>
{
  const orbitSetAction = useControllerAction( controllerPath );
  const selected = selectedOrbitNames && selectedOrbitNames .indexOf( orbit ) >= 0;
  const orbitDetails = useControllerProperty( controllerPath, `orbitDot.${orbit}` ) || "";
  const [ colorHex, x, y ] = orbitDetails.split( '/' );
  let color;
  if ( colorHex ) {
    color = colorHex .substring( 2 ); // remove "0x"
    if ( color .length === 2 )
      color = `#0000${color}`
    else if ( color .length === 4 )
      color = `#00${color}`
    else
      color = `#${color}`
  }
  const r = 0.05;
  const relY = relativeHeight * ( 1-y );

  const toggleDot = evt => orbitSetAction( `toggleDirection.${orbit}` );

  return color && ( <>
    <circle key={orbit} cx={x} cy={relY} r={r} fill={color} onClick={toggleDot} />
    { selected &&
      <circle key={orbit+'DOT'} cx={x} cy={relY} r={0.37*r} fill="black" onClick={toggleDot} />
    }
    { isLastSelected &&
      <circle key={orbit+'RING'} cx={x} cy={relY} r={1.57*r} fill="none" />
    }
  </> )
}

export const OrbitPanel = ( { symmController, orbitSet, showLastOrbit=true } ) =>
{
  const symmetryAction = useControllerAction( symmController );
  const controllerPath = subcontroller( symmController, orbitSet );
  const orbitSetAction = useControllerAction( controllerPath );
  const orbitNames = useControllerProperty( controllerPath, 'allOrbits', 'orbits', true );
  const selectedOrbitNames = useControllerProperty( controllerPath, 'orbits', 'orbits', true );
  const oneAtATime = useControllerProperty( controllerPath, 'oneAtATime', 'orbits', false ) === 'true';
  const selectedOrbit = useControllerProperty( controllerPath, 'selectedOrbit', 'orbits', false );
  const lastSelected = showLastOrbit && selectedOrbit;
  const [ anchorEl, setAnchorEl ] = useState( null );

  const revealSettings = evt => setAnchorEl( evt.currentTarget );
  const hideSettingsAnd = action => () => {
    setAnchorEl( null );
    symmetryAction( action );
  }

  const marginedStyle = { margin: '8px' }
  const relativeHeight = 0.6;
  const triangleCorners = `0,${relativeHeight} 1,${relativeHeight} 0,0`
  const viewBox = `-0.1 -0.1 1.2 ${relativeHeight + 0.2 }`;

  return (
    <div style={{ margin: '8px' }}>
      <div>
        <FormGroup row>
          <Button variant="outlined" style={marginedStyle} onClick={ () => orbitSetAction( 'setNoDirections' ) } >None</Button>
          <Button variant="outlined" style={marginedStyle} onClick={ () => orbitSetAction( 'setAllDirections' ) } >All</Button>
          <FormControlLabel style={marginedStyle}
            control={<Checkbox checked={oneAtATime} color="primary" onChange={ () => orbitSetAction( 'oneAtATime' ) } />}
            label="Single"
          />
        </FormGroup>
      </div>
      <div style={{ position: 'relative', backgroundColor: 'white' }}>
        <svg viewBox={viewBox} stroke="black" strokeWidth={0.005} >
          <g>
            {/* TODO: reversed triangle per the controller */}
            <polygon fill="none" points={triangleCorners}/>  { /* all dot X & Y values are in [0..1] */ }
            { orbitNames && orbitNames.map && orbitNames.map( orbit =>
              <OrbitDot { ...{ controllerPath, orbit, selectedOrbitNames, relativeHeight } } isLastSelected={ orbit === lastSelected }/>
            ) }
          </g>
        </svg>
        <IconButton color="inherit" aria-label="settings"
            style={ { position: 'absolute', top: '8px', right: '8px' } }
            onClick={revealSettings} >
          <SettingsIcon fontSize='medium'/>
        </IconButton>
        <Menu
          id="orbit-settings-menu"
          anchorEl={anchorEl}
          keepMounted
          open={Boolean(anchorEl)}
          onClose={ () => setAnchorEl( null ) }
        >
          <MenuItem onClick={ hideSettingsAnd( 'rZomeOrbits' ) }>real Zome</MenuItem>
          <MenuItem onClick={ hideSettingsAnd( 'predefinedOrbits' ) }>predefined</MenuItem>
          <MenuItem onClick={ hideSettingsAnd( 'setAllDirections' ) }>all</MenuItem>
        </Menu>
      </div>
    </div>
  )
}
