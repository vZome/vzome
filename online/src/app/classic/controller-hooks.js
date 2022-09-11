
import { useEffect } from 'react';
import { useDispatch, useSelector, useStore } from 'react-redux';
import { newDesign, doControllerAction, requestControllerList, requestControllerProperty } from '../../ui/viewer/store.js';

export const useNewDesign = () =>
{
  const report = useDispatch();
  useEffect( () => report( newDesign() ), [] );
}

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

export const controllerAction = ( controller, action ) => evt =>
{
  if ( controller.ready )
    controller .doAction( action );
}
