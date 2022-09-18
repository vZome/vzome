
import { useEffect } from 'react';
import { useDispatch, useSelector } from 'react-redux';
import { newDesign, doControllerAction, requestControllerList, requestControllerProperty } from '../../ui/viewer/store.js';

export const useNewDesign = () =>
{
  const report = useDispatch();
  useEffect( () => report( newDesign() ), [] );
}

export const useControllerProperty = ( controllerPath, propName ) =>
{
  const report = useDispatch();
  const fullName = controllerPath + '/' + propName;
  const controller = useSelector( state => state.controller );
  const value = useSelector( state => state.controller && state.controller[ fullName ] );
  useEffect( () => {
    if ( controller && value === undefined ) {
      // trigger the initial fetch
      report( requestControllerProperty( controllerPath, propName ) );
    }
  }, [ controller, value ] );
  return value;
}

export const useControllerList = ( controllerPath, listName ) =>
{
  const report = useDispatch();
  const fullName = controllerPath + '/' + listName;
  const controller = useSelector( state => state.controller );
  const value = useSelector( state => state.controller && state.controller[ fullName ] );
  useEffect( () => {
    if ( controller && value === undefined ) {
      // trigger the initial fetch
      report( requestControllerList( controllerPath, listName ) );
    }
  }, [ controller, value ] );
  return value;
}

export const controllerAction = ( controller, action ) => evt =>
{
  if ( controller.ready )
    controller .doAction( action );
}
