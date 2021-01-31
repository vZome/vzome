
import { createStore, applyMiddleware, combineReducers } from 'redux'

import * as alerts from './alerts'
import * as camera from './camera'
import * as lighting from './lighting'
import * as progress from './progress'
import * as commands from '../commands'
import * as workingPlane from './planes'
import * as designs from './designs'
import * as shapers from './shapers'

const createBundleStore = ( profile, middleware ) =>
{
  let bundles = { lighting, camera }
  switch ( profile ) {

    case "plane":
      bundles = { ...bundles, designs, shapers, workingPlane }
      break;

    default:
      bundles = { ...bundles, designs, shapers, commands, alerts, progress }
      break;
  }
  const names = Object.keys( bundles )

  const reducers = names.reduce( ( obj, key ) => {
    const reducer = bundles[key].reducer
    if ( reducer )
      obj[ key ] = reducer
    return obj
  }, {} )

  console.log( `bundle reducers: ${JSON.stringify( Object.keys( reducers ) )}` )

  const rootReducer = combineReducers( reducers )
  
  const store = createStore( rootReducer, undefined, applyMiddleware( ...middleware ) );
  
  // TODO: is there a better pattern than these inits?
  names.map( key => {
    const init = bundles[key].init
    if ( init ) {
      console.log( `bundle init: ${key}` )
      init( window, store )
    }
    return null
  } )

  return store
}

export default createBundleStore
