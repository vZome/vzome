
import { combineReducers } from 'redux'
import undoable from 'redux-undo'

import { goldenField } from 'react-vzome'
import * as mesh from './mesh'
import * as camera from './camera'
import * as dbugger from './dbugger'


export const designReducer = combineReducers( {
  mesh: undoable( mesh.reducer ),
  dbugger: undoable( dbugger.reducer ),
  camera: camera.reducer,
  name: ( state="", action ) => state, // name cannot be changed (YET), so a constant reducer is fine
  success: ( state="", action ) => state, // success cannot be changed, so a constant reducer is fine
  fieldName: ( state="", action ) => state, // fieldName cannot be changed, so a constant reducer is fine
  shaperName: ( state="", action ) => state, // shaperName cannot be changed (YET!), so a constant reducer is fine
})

export const initializeDesign = ( field, name, shaperName ) => ({
  mesh: {
    past: [],
    present: mesh.justOrigin( field ),
    future: [],
  },
  // TODO: initialize dbugger?
  success: true,
  camera: camera.initialState,
  fieldName: field.name,
  shaperName,
  name,
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