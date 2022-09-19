
import { useEffect } from 'react';
import { useDispatch, useSelector } from 'react-redux';
import { newDesign, doControllerAction, requestControllerProperty } from '../../ui/viewer/store.js';

export const useNewDesign = () =>
{
  const report = useDispatch();
  useEffect( () => report( newDesign() ), [] );
}

export const useControllerProperty = ( controllerPath, propName, changeName=null, isList=false ) =>
{
  const report = useDispatch();
  const fullName = controllerPath + '/' + propName;
  const controller = useSelector( state => state.controller );
  const value = useSelector( state => state.controller && state.controller[ fullName ] );
  useEffect( () => {
    if ( controller && value === undefined ) {
      // trigger the initial fetch
      report( requestControllerProperty( controllerPath, propName, changeName, isList ) );
    }
  }, [ controller, value ] );
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
