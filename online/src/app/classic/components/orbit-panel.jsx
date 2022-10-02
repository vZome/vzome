
import React, { useState } from 'react';
import IconButton from '@material-ui/core/IconButton'
import SettingsIcon from '@material-ui/icons/Settings'
import Menu from '@material-ui/core/Menu';
import MenuItem from '@material-ui/core/MenuItem';
import Button from '@material-ui/core/Button';
import FormGroup from '@material-ui/core/FormGroup';
import FormControlLabel from '@material-ui/core/FormControlLabel';
import Checkbox from '@material-ui/core/Checkbox';

import { useControllerProperty, useControllerAction } from '../controller-hooks.js';
import { subcontroller } from '../../../ui/viewer/store.js';
import { hexToWebColor, StrutLengthPanel } from './length.jsx';

export const OrbitDot = ( { controller, orbit, selectedOrbitNames, relativeHeight, isLastSelected } ) =>
{
  const orbitSetAction = useControllerAction( controller );
  const selected = selectedOrbitNames && selectedOrbitNames .indexOf( orbit ) >= 0;
  const orbitDetails = useControllerProperty( controller, `orbitDot.${orbit}` ) || "";
  const [ colorHex, x, y ] = orbitDetails.split( '/' );
  const color = colorHex && hexToWebColor( colorHex );
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

export const OrbitPanel = ( { controller, orbitNames, lastSelected, children } ) =>
{
  const orbitSetAction = useControllerAction( controller );
  const selectedOrbitNames = useControllerProperty( controller, 'orbits', 'orbits', true );
  const oneAtATime = useControllerProperty( controller, 'oneAtATime', 'orbits', false ) === 'true';

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
          { lastSelected && <FormControlLabel style={marginedStyle}
            control={<Checkbox checked={oneAtATime} color="primary" onChange={ () => orbitSetAction( 'oneAtATime' ) } />}
            label="Single"
          /> }
        </FormGroup>
      </div>
      <div style={{ position: 'relative', backgroundColor: 'white' }}>
        <svg viewBox={viewBox} stroke="black" strokeWidth={0.005} >
          <g>
            {/* TODO: reversed triangle per the controller */}
            <polygon fill="none" points={triangleCorners}/>  { /* all dot X & Y values are in [0..1] */ }
            { orbitNames && orbitNames.map && orbitNames.map( orbit =>
              <OrbitDot key={orbit} { ...{ controller, orbit, selectedOrbitNames, relativeHeight } } isLastSelected={ orbit === lastSelected }/>
            ) }
          </g>
        </svg>
        { children }
      </div>
    </div>
  )
}

export const StrutBuildPanel = ( { symmController } ) =>
{
  const availableOrbits = subcontroller( symmController, 'availableOrbits' );
  const orbitNames = useControllerProperty( availableOrbits, 'orbits', 'orbits', true );
  const setOrbitNames = useControllerAction( availableOrbits );

  const buildOrbits = subcontroller( symmController, 'buildOrbits' );
  const lastSelected = useControllerProperty( buildOrbits, 'selectedOrbit', 'orbits', false );

  const [ anchorEl, setAnchorEl ] = useState( null );
  const revealSettings = evt => setAnchorEl( evt.currentTarget );
  const setAvailableOrbits = action => () => {
    setAnchorEl( null );
    setOrbitNames( action );
  }

  return(
    <div id="build" style={{ display: 'grid', gridTemplateRows: '1fr min-content', height: '100%' }}>
      <OrbitPanel controller={buildOrbits} { ...{ orbitNames, lastSelected } } style={{ height: '100%' }} >
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
          <MenuItem onClick={ setAvailableOrbits( 'rZomeOrbits' ) }>real Zome</MenuItem>
          <MenuItem onClick={ setAvailableOrbits( 'predefinedOrbits' ) }>predefined</MenuItem>
          <MenuItem onClick={ setAvailableOrbits( 'setAllDirections' ) }>all</MenuItem>
        </Menu>
      </OrbitPanel>
      <StrutLengthPanel controller={buildOrbits} />
    </div>
  );
}