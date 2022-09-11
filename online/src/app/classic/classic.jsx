
import React from 'react'
import { DesignViewer } from '../../ui/viewer/index.jsx'
import { useNewDesign, useController, useControllerList, useControllerProperty } from './controller-hooks.js';

import Grid from '@material-ui/core/Grid'


export const OrbitDot = ( { controllerPath, orbit, selectedOrbitNames } ) =>
{
  const selected = selectedOrbitNames && selectedOrbitNames .indexOf( orbit ) >= 0;
  const controller = useController( controllerPath );
  const orbitDetails = useControllerProperty( controller, `orbitDot.${orbit}` ) || "";
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

export const OrbitPanel = ( { controllerPath } ) =>
{
  const controller = useController( controllerPath );
  const orbitNames = useControllerList( controller, 'allOrbits' );
  const selectedOrbitNames = useControllerList( controller, 'orbits' );

  return (
    <svg viewBox="-0.2 -0.2 1.4 1.4" 
         stroke="black" strokeWidth={0.007} >
      <g>
        {/* TODO: reversed triangle per the controller */}
        <polygon fill="none" points={`0,1 1,1 0,0`}/>  { /* all dot X & Y values are in [0..1] */ }
        { orbitNames && orbitNames.map && orbitNames.map( orbit =>
          <OrbitDot { ...{ controllerPath, orbit, selectedOrbitNames } }/>
        ) }
      </g>
    </svg>
  )
}

export const ClassicEditor = () =>
{
  useNewDesign();

  const rightColumns = 3;
  const canvasColumns = 12 - rightColumns;

  return (
    <div style={{ flex: '1', height: '100%' }}>
      <Grid id='editor-main' container spacing={0} style={{ height: '100%' }}>        
        <Grid id='editor-drawer' item xs={canvasColumns}>
          <DesignViewer config={ { useSpinner: true } } />
        </Grid>
        <Grid id='editor-canvas' item xs={rightColumns} >
          <OrbitPanel controllerPath="" />
        </Grid>
      </Grid>
    </div>
  )
}
