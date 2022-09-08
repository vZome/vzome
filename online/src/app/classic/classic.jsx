
import React from 'react'
import { useDispatch, useSelector, useStore } from 'react-redux';
import { doControllerAction, newDesign, requestControllerList, requestControllerProperty } from '../../ui/viewer/store.js';

import Grid from '@material-ui/core/Grid'
import Button from '@material-ui/core/Button';
import { useEffect } from 'react';

const controllerSelector = ( path, name ) => state =>
{
  const elements = path.split( '/' );
  if ( name )
    elements .push( name );
  let result = state.controller;
  for ( const element of elements ) {
    if ( !result )
      return result;
    if ( !element )
      continue;
    result = result[ element ];
  }
  return result;
}

export const useController = ( path ) =>
{
  const report = useDispatch();
  // let WorkerController do this
  // useEffect( () => report( newDesign() ), [] );

  const controller = useSelector( controllerSelector( path ) );
  const selector = name => controllerSelector( path, name );
  if ( controller ) {
    const doAction = action => report( doControllerAction( path, action ) );
    const getList = listName => report( requestControllerList( path, listName ) );
    const getProperty = propName => report( requestControllerProperty( path, propName ) );
    return { ready: true, doAction, getList, getProperty, selector };
  }
  else
    return { selector };
}

export const useControllerProperty = ( controller, propName ) =>
{
  const store = useStore();
  useEffect( () => {
    if ( controller.ready && ! controller.selector( propName )( store .getState() ) ) {
      // trigger the initial fetch
      controller.getProperty( propName );
    }
  }, [ controller ] );
  return useSelector( controller.selector( propName ) );
}

export const useControllerList = ( controller, listName ) =>
{
  const store = useStore();
  useEffect( () => {
    if ( controller.ready && ! controller.selector( listName )( store .getState() ) ) {
      // trigger the initial fetch
      controller.getList( listName );
    }
  }, [ controller ] );
  return useSelector( controller.selector( listName ) );
}

const controllerAction = ( controller, action ) => evt =>
{
  if ( controller.ready )
    controller .doAction( action );
}

export const OrbitDot = ( { controllerPath, orbit, selectedOrbitNames, maxX, maxY, strokeWidth } ) =>
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
  return color && ( <>
    <circle key={orbit} cx={x * maxX} cy={y * maxY} r="0.024" fill={color} stroke="black" strokeWidth={strokeWidth}/>
    { selected &&
      <circle key={orbit+'DOT'} cx={x * maxX} cy={y * maxY} r="0.010" fill="black" stroke="black" strokeWidth={strokeWidth}/>
    }
  </> )
}

export const OrbitPanel = ( { controllerPath } ) =>
{
  const controller = useController( controllerPath );
  const orbitNames = useControllerList( controller, 'allOrbits' );
  const selectedOrbitNames = useControllerList( controller, 'orbits' );
  const maxX = 0.6180339;
  const maxY = -0.3819660;
  const strokeWidth = 0.003;

  return (
    <svg xmlns="http://www.w3.org/2000/svg" 
      viewBox="-0.15450849718747373 -0.47745751406263137 0.9270509831248424 0.5729490168751576"
      width="628" height="400">
      <g>
        <polygon fill="none" stroke="black" strokeWidth={strokeWidth} points={`0,0 ${maxX},0 0,${maxY}`}/>
        { orbitNames && orbitNames.map && orbitNames.map( orbit =>
          <OrbitDot { ...{ controllerPath, orbit, selectedOrbitNames, maxX, maxY, strokeWidth } }/>
        ) }
      </g>
    </svg>
  )
}

export const ClassicEditor = ( props ) =>
{
  const report = useDispatch();
  useEffect( () => report( newDesign() ), [] );
  const controller = useController( '' );

  const drawerColumns = 5;
  const canvasColumns = 12 - drawerColumns;

  return (
    <div style={{ flex: '1', height: '100%' }}>
      <Grid id='editor-main' container spacing={0} style={{ height: '100%' }}>        
        <Grid id='editor-drawer' item xs={drawerColumns}>
          <Button variant="contained" color="primary" onClick={controllerAction( controller, 'rZomeOrbits' )}>
            Real Zome Orbits
          </Button>
        </Grid>
        <Grid id='editor-canvas' item xs={canvasColumns} >
          <OrbitPanel controllerPath="" />
        </Grid>
      </Grid>
    </div>
  )
}
