
import { useEffect } from 'react';
import { useDispatch, useSelector } from 'react-redux';
import { newDesign, createStrut, doControllerAction, requestControllerProperty, subcontroller } from '../../ui/viewer/store.js';

export const useNewDesign = () =>
{
  const report = useDispatch();
  useEffect( () => report( newDesign() ), [] );
}

export const useControllerProperty = ( controllerPath, propName, changeName=null, isList=false ) =>
{
  const report = useDispatch();
  const fullName = subcontroller( controllerPath, propName );
  const controllerReady = useSelector( state => state.controller?.isReady );
  const value = useSelector( state => state.controller && state.controller[ fullName ] );
  useEffect( () => {
    if ( controllerReady ) {
      // This must be a one-time action for any given property.  The response
      // on the worker side will become the first change event coming back.
      report( requestControllerProperty( controllerPath, propName, changeName, isList ) );
    }
  }, [ controllerReady ] );
  return value;
}

export const useControllerAction = controllerPath =>
{
  const report = useDispatch();
  const controller = useSelector( state => state.controller );
  return action => {
    controller && report ( doControllerAction( controllerPath, action ) );
  }
}
