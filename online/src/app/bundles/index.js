
import { createStore, applyMiddleware, combineReducers } from 'redux'

import * as alerts from './alerts.js'
import * as camera from './camera.js'
import * as lighting from './lighting.js'
import * as progress from './progress.js'
import * as commands from '../commands/index.js'
import * as workingPlane from './planes.js'
import * as designs from './designs.js'

const createBundleStore = ( profile, middleware ) =>
{
  let bundles = { lighting, camera }
  switch ( profile ) {

    case "plane":
      bundles = { ...bundles, designs, workingPlane }
      break;

    default:
      bundles = { ...bundles, designs, commands, alerts, progress }
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
