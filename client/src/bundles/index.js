
import { createStore, applyMiddleware, combineReducers } from 'redux'

import * as jre from './jre'
import * as files from './files'
import * as alerts from './alerts'
import * as vzomejava from './vzomejava'
import * as camera from './camera'
import * as lighting from './lighting'
import * as progress from './progress'
import * as jsweet from './jsweet'
import * as mesh from './mesh'
import * as implementations from './implementations'
import * as commands from '../commands'
import * as goldenField from '../fields/golden'

const bundles = { jre, files, alerts, vzomejava, camera, lighting, progress, jsweet, mesh, implementations, commands, goldenField }


export default ( middleware ) =>
{
  const names = Object.keys( bundles )

  const reducers = names.reduce( ( obj, key ) => {
    const reducer = bundles[key].reducer
    if ( reducer )
      obj[ key ] = reducer
    return obj
  }, {} )

  console.log( `bundle reducers: ${JSON.stringify( Object.keys( reducers ) )}` )

  const rootReducer = combineReducers( reducers )
  
  const allMiddleware = names.reduce( ( arr, key ) => {
    const newMiddleware = bundles[key].middleware
    if ( newMiddleware ) {
      console.log( `bundle middleware: ${key}` )
      arr.push( newMiddleware )
    }
    return arr
  }, middleware )

  const store = createStore( rootReducer, applyMiddleware( ...allMiddleware ) );
  
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