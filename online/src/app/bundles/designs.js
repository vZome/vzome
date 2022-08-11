
import { combineReducers } from 'redux'
import undoable, { ActionCreators as Undo } from 'redux-undo'

import goldenField from '../../worker/fields/golden.js'
import * as mesh from './mesh'
import { reducer as cameraReducer, initialState as cameraDefault } from './camera.js'
import * as dbugger from './dbugger.js'
import { showAlert } from './alerts.js'

const identityReducer = ( state="", action ) => state

export const designReducer = combineReducers( {
  dbugger: undoable( dbugger.reducer ),
  // These cannot be changed (YET), so a constant reducer is fine
  name: identityReducer,
  text: identityReducer,
  field: identityReducer,
  rendererName: identityReducer,
})

export const initializeDesign = ( field, name, rendererName, text ) => ({
  mesh: {
    past: [],
    present: field && mesh.justOrigin( field ),
    future: [],
  },
  // TODO: initialize dbugger?
  scene: {
    camera: cameraDefault,
  },
  rendererName,
  field,
  name,
  text,
})

const emptyState = {
  nextUntitledIndex: 0,
  data: {},
}

const addNewModel = ( state, field ) =>
{
  const name = `untitled ${field.name} ${state.nextUntitledIndex}`
  return {
    nextUntitledIndex: state.nextUntitledIndex + 1,
    current: name,
    data: {
      ...state.data,
      [ name ]: initializeDesign( field, name )
    }
  }
}

export const newModel = field => ( { type: 'NEW_MODEL', payload: field } )

export const switchModel = id => ( { type: 'SWITCH_MODEL', payload: id } )

export const renameModel = name => ( { type: 'RENAME_MODEL', payload: name } )

export const loadingDesign = ( id, design ) => ( { type: 'LOADING_DESIGN', payload: { id, design } } )

export const loadedDesign = ( id, design ) => ( { type: 'LOADED_DESIGN', payload: { id, design } } )

export const selectDesign = ( state, id ) => state.designs.data[ id ] || state.designs.data[ state.designs.current ]

export const selectDesignName = ( state, id ) => selectDesign( state, id ).name

export const selectText = ( state, id ) => selectDesign( state, id ).text

export const selectMesh = ( state, id ) => selectDesign( state, id ).mesh.present

export const selectDebugger = ( state, id ) => {
  const design = selectDesign( state, id )
  return design && design.dbugger && design.dbugger.present // No dbugger if the field was unknown
}

export const selectScene = ( state, id ) => selectDesign( state, id ).scene

export const selectSource = ( state, id ) => selectDebugger( state, id ).source

export const selectField = ( state, id ) => selectDesign( state, id ).field

export const selectRenderer = ( state, id ) =>
{
  const rendererName = selectDesign( state, id ).rendererName || "default"
  return state.renderers[ rendererName ]
}

export const reducer = ( state = addNewModel( emptyState, goldenField ), action ) =>
{
  switch ( action.type ) {

    case 'NEW_MODEL':
      return addNewModel( action.payload )
  
    case 'CLOSE_MODEL':
      return state;

    case 'SWITCH_MODEL': {
      const name = action.payload
      return {
        ...state,
        current: name,
      }
    }

    case 'LOADING_DESIGN': {
      const { id, design } = action.payload
      return {
        ...state,
        data: {
          ...state.data,
          [ id ]: design
        }
      }
    }

    case 'LOADED_DESIGN': {
      const { id, design } = action.payload
      return {
        ...state,
        current: id,
        data: {
          ...state.data,
          [ id ]: design
        }
      }
    }

    case 'RENAME_MODEL': {
      const oldName = state.current
      const name = action.payload
      const model = state.data[ oldName ]
      delete state.data[ oldName ]
      return {
        ...state,
        current: name,
        data: {
          ...state.data,
          [ name ]: model
        }
      }
    }

    default:
      // All other actions are funneled only to the current design; we don't
      //  want any changes to affect all designs simultaneously.
      const modelState = state.data[ state.current ]
      if ( modelState ) {
        return {
          ...state,
          data: {
            ...state.data,
            [ state.current ]: designReducer( modelState, action )
          }
        }
      } else {
        return state
      }
  }
}

export const openDesign = ( textPromise, url ) => async ( dispatch, getState ) =>
{
  const name = decodeURI( url.split( '\\' ).pop().split( '/' ).pop() )
  try {
    const { text, error, scene, parsed } = await fetchUrlDesign( textPromise, url )
    let design = initializeDesign( null, name, null, text )
    if ( error ) {
      console.log( error )
      dispatch( loadingDesign( name, design ) )
      dispatch( loadedDesign( name, design ) )
      dispatch( showAlert( error + ' Use the download button to save this file, then try opening it with desktop vZome.' ) )
    } else if ( scene ) {
      design.scene = scene
      dispatch( loadingDesign( name, design ) )
      dispatch( loadedDesign( name, design ) )
    } else {
      const { field, targetEdit, firstEdit, renderer, camera, lighting } = parsed

      // We don't want to dispatch all the edits, which can trigger tons of
      //  overhead and re-rendering.  Instead, we'll build up a design locally
      //  by calling the designReducer manually.
      let design = initializeDesign( field, name, renderer.name, text )
      design.scene = { camera: { ...camera, fov: 0.75 }, lighting, embedding: renderer.embedding }
      // Each call to designReducer may create an element in the history
      //  (if it has any changes to the mesh),
      //  so we want to be judicious in when we do it.
      // Each call to dispatch, on the other hand, triggers rendering, so we want to be even
      //  more careful about that.
      
      design = designReducer( design, dbugger.sourceLoaded( firstEdit, targetEdit ) ) // recorded in history
      design = designReducer( design, Undo.clearHistory() )  // kind of a hack so both histories are in sync, with no past

      dispatch( loadingDesign( name, design ) )

      if ( ! getState().dbuggerEnabled ) {
        dispatch( dbugger.stepper.done( name ) )
      }
      else {
        dispatch( loadedDesign( name, design ) )
      }
    }
  } catch (error) {
    console.log( error.message )
    dispatch( showAlert( error.message ) )
  }
}

export const serialize = ( { camera, field, rendererName, dbugger } ) =>
{
  const serializeEdit = dbugr => dbugr.nextEdit && dbugr.nextEdit.serialize()
  let edits = dbugger.past.map( serializeEdit )
  edits.push( { ...serializeEdit( dbugger.present ), targetEdit: true } )
  edits = edits.concat( dbugger.future.map( serializeEdit ) )
  return JSON.stringify( { edits, camera, field: field.name, rendererName }, null, '  ' )
}