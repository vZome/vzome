
import { combineReducers } from 'redux'
import undoable from 'redux-undo'

import goldenField from '../fields/golden'
import * as mesh from './mesh'
import * as camera from './camera'

export const designReducer = combineReducers( {
  history: undoable( combineReducers( {
    mesh: mesh.reducer,
    // TODO: add current command
  } ) ),
  fieldName: ( state="", action ) => state, // fieldName cannot be changed, so a constant reducer is fine
  camera: camera.reducer,
})

export const initializeDesign = field => ({
  history: {
    past: [],
    present: {
      mesh: mesh.justOrigin( field ),
    },
    future: [],
  },
  fieldName: field.name,
  camera: camera.initialState,
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
      [ name ]: initializeDesign( field )
    }
  }
}

export const newModel = field => ( { type: 'NEW_MODEL', payload: field } )

export const switchModel = name => ( { type: 'SWITCH_MODEL', payload: name } )

export const renameModel = name => ( { type: 'RENAME_MODEL', payload: name } )

export const loadedDesign = ( name, design ) => ( { type: 'LOADED_DESIGN', payload: { name, design } } )

export const selectCurrentDesign = state => state.models.data[ state.models.current ]

export const selectCurrentMesh = state => selectCurrentDesign( state ).history.present.mesh

export const selectCurrentCamera = state => selectCurrentDesign( state ).camera

export const selectCurrentField = state => state.fields[ selectCurrentDesign( state ).fieldName ]

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

    case 'LOADED_DESIGN': {
      const { name, design } = action.payload
      return {
        ...state,
        current: name,
        data: {
          ...state.data,
          [ name ]: design
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