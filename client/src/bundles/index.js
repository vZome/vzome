
import { createStore, applyMiddleware, combineReducers } from 'redux'

import * as jre from './jre'
import * as files from './files'
import * as alerts from './alerts'
import * as vZomeJava from './vzomejava'
import * as camera from './camera'
import * as lighting from './lighting'
import * as progress from './progress'
import * as jsweet from './jsweet'
import * as mesh from './mesh'
import * as commands from '../commands'
import * as goldenField from '../fields/golden'
import * as workingPlane from './planes'

const requiredBundles = { camera, lighting, goldenField }

let bundles
const urlParams = new URLSearchParams( window.location.search );
if ( urlParams.has( "editMode" ) ) {
  switch ( urlParams.get( "editMode" ) ) {

    case "plane":
      bundles = { ...requiredBundles, jsweet, mesh, commands, workingPlane }
      break;
  
    default:
      bundles = { ...requiredBundles, jsweet, mesh, commands, files, alerts, progress }
      break;
  }
} else {
  bundles = { ...requiredBundles, jre, files, alerts, vZomeJava, progress }
}


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