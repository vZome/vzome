
import React, { useState } from 'react';
import Grid from '@material-ui/core/Grid'
import IconButton from '@material-ui/core/IconButton'
import SettingsIcon from '@material-ui/icons/Settings'
import Menu from '@material-ui/core/Menu';
import MenuItem from '@material-ui/core/MenuItem';

import { DesignViewer } from '../../ui/viewer/index.jsx'
import { useNewDesign, useControllerProperty, useControllerAction } from './controller-hooks.js';

export const OrbitDot = ( { controllerPath, orbit, selectedOrbitNames } ) =>
{
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
  const r = 0.08;
  return color && ( <>
    <circle key={orbit} cx={x} cy={1-y} r={r} fill={color} />
    { selected &&
      <circle key={orbit+'DOT'} cx={x} cy={1-y} r={0.37*r} fill="black" />
    }
  </> )
}

export const OrbitPanel = ( { controllerPath, handleClick } ) =>
{
  const rZomeOrbits = useControllerAction( 'strutBuilder/symmetry', 'rZomeOrbits' );
  const predefOrbits = useControllerAction( 'strutBuilder/symmetry', 'predefinedOrbits' );
  const allOrbits = useControllerAction( 'strutBuilder/symmetry', 'setAllDirections' );
  const orbitNames = useControllerProperty( controllerPath, 'allOrbits', 'orbits', true );
  const selectedOrbitNames = useControllerProperty( controllerPath, 'orbits', 'orbits', true );
  const [ anchorEl, setAnchorEl ] = useState( null );

  const revealSettings = evt => setAnchorEl( evt.currentTarget );
  const hideSettingsAnd = action => () => {
    setAnchorEl( null );
    action();
  }

  return (
    <>
      <svg viewBox="-0.2 -0.2 1.4 1.4" stroke="black" strokeWidth={0.007} onClick={handleClick}>
        <g>
          {/* TODO: reversed triangle per the controller */}
          <polygon fill="none" points={`0,1 1,1 0,0`}/>  { /* all dot X & Y values are in [0..1] */ }
          { orbitNames && orbitNames.map && orbitNames.map( orbit =>
            <OrbitDot { ...{ controllerPath, orbit, selectedOrbitNames } }/>
          ) }
        </g>
      </svg>
      <IconButton color="inherit" aria-label="settings"
          style={ { position: 'absolute', top: '5px', right: '5px' } }
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
        <MenuItem onClick={hideSettingsAnd(rZomeOrbits)}>real Zome</MenuItem>
        <MenuItem onClick={hideSettingsAnd(predefOrbits)}>predefined</MenuItem>
        <MenuItem onClick={hideSettingsAnd(allOrbits)}>all</MenuItem>
      </Menu>
    </>
  )
}

export const ClassicEditor = () =>
{
  useNewDesign();
  // const makeHyperdo = useControllerAction( '', 'Polytope4d' );

  const rightColumns = 3;
  const canvasColumns = 12 - rightColumns;

  return (
    <div style={{ flex: '1', height: '100%' }}>
      <Grid id='editor-main' container spacing={0} style={{ height: '100%' }}>        
        <Grid id='editor-canvas' item xs={canvasColumns}>
          <DesignViewer config={ { useSpinner: true } } />
        </Grid>
        <Grid id='editor-drawer' item xs={rightColumns} style={{ position: 'relative' }}>
          <OrbitPanel controllerPath='strutBuilder/symmetry/buildOrbits'/>
          {/* <Button variant="contained" color="primary" onClick={makeHyperdo} >
            Hyperdo
          </Button> */}
        </Grid>
      </Grid>
    </div>
  )
}
