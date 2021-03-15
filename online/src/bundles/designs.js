
import { combineReducers } from 'redux'
import undoable, { ActionCreators as Undo } from 'redux-undo'

import { goldenField, vZomeJava } from '@vzome/react-vzome'
import * as mesh from './mesh'
import { reducer as cameraReducer, initialState as cameraDefault, cameraDefined } from './camera'
import * as dbugger from './dbugger'
import * as shapers from './shapers'
import { showAlert } from './alerts'

export const designReducer = combineReducers( {
  mesh: undoable( mesh.reducer ),
  dbugger: undoable( dbugger.reducer ),
  camera: cameraReducer,
  name: ( state="", action ) => state, // name cannot be changed (YET), so a constant reducer is fine
  text: ( state="", action ) => state, // text cannot be changed (YET), so a constant reducer is fine
  success: ( state="", action ) => state, // success cannot be changed, so a constant reducer is fine
  fieldName: ( state="", action ) => state, // fieldName cannot be changed, so a constant reducer is fine
  shaperName: ( state="", action ) => state, // shaperName cannot be changed (YET!), so a constant reducer is fine
})

export const initializeDesign = ( field, name, shaperName, text ) => ({
  mesh: {
    past: [],
    present: mesh.justOrigin( field ),
    future: [],
  },
  // TODO: initialize dbugger?
  success: true,
  fieldName: field.name,
  camera: cameraDefault,
  shaperName,
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

export const selectDebugger = ( state, id ) => selectDesign( state, id ).dbugger.present

export const selectCamera = ( state, id ) => selectDesign( state, id ).camera

export const selectSource = ( state, id ) => selectDebugger( state, id ).source

export const selectField = ( state, id ) => state.fields[ selectDesign( state, id ).fieldName ]

export const selectShaper = ( state, id ) =>
{
  const shaperName = selectDesign( state, id ).shaperName || "solid connectors"
  return state.shapers[ shaperName ]
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
  const { parser } = await vZomeJava.coreState  // Must wait for the vZome code to initialize
  try {
    const text = await textPromise
    const { edits, camera, field, parseAndPerformEdit, targetEdit, shaper } = parser( text ) || {}
    if ( !edits ) {
      throw new Error( `XML parsing failed` )
    }
    const name = decodeURI( url.split( '\\' ).pop().split( '/' ).pop() )

    console.log( `Opening ${url}`)
    console.log( text )

    // We don't want to dispatch all the edits, which can trigger tons of
    //  overhead and re-rendering.  Instead, we'll build up a design locally
    //  by calling the designReducer manually.
    let design = initializeDesign( field, name, shaper.shapesName, text )
    // Each call to designReducer may create an element in the history
    //  (if it has any changes to the mesh),
    //  so we want to be judicious in when we do it.
    // Each call to dispatch, on the other hand, triggers rendering, so we want to be even
    //  more careful about that.
    
    // TODO: skip this dispatch if we already have a shaper for shapesName, in getState().shapers
    dispatch( shapers.shaperDefined( shaper.shapesName, shaper ) ) // outside the design

    design = designReducer( design, cameraDefined( camera ) )
    design = designReducer( design, dbugger.sourceLoaded( edits, parseAndPerformEdit, targetEdit ) ) // recorded in history
    design = designReducer( design, Undo.clearHistory() )  // kind of a hack so both histories are in sync, with no past

    dispatch( loadingDesign( name, design ) )

    if ( ! getState().dbuggerEnabled ) {
      dispatch( dbugger.debug( name, vZomeJava.Step.DONE ) )
    }
    else {
      dispatch( loadedDesign( name, design ) )
    }
  } catch (error) {
    const message = `Unable to parse vZome design file: ${url};\n ${error.message}`
    console.log( message )
    dispatch( showAlert( `Unable to parse vZome design file: ${url};\n ${error.message}` ) )
  }
}