
import { combineReducers } from 'redux'
import undoable from 'redux-undo'

import goldenField from '../fields/golden'
import * as mesh from './mesh'

const modelReducer = undoable( combineReducers({
  mesh: mesh.reducer,
  fieldName: ( state="", action ) => state // fieldName cannot be changed, so a constant reducer is fine
}) )

const initializeModel = field => ({
  past: [],
  present: {
    mesh: mesh.justOrigin( field ),
    fieldName: field.name
  },
  future: []
})

const emptyState = {
  nextUntitledIndex: 0,
  models: {},
}

const addNewModel = ( state, field ) =>
{
  const name = `untitled ${field.name} ${state.nextUntitledIndex}`
  return {
    nextUntitledIndex: state.nextUntitledIndex + 1,
    current: name,
    displayed: name,
    models: {
      ...state.models,
      [ name ]: modelReducer( initializeModel( field ) )
    }
  }
}

export const newModel = field => ( { type: 'NEW_MODEL', payload: field } )

export const switchModel = name => ( { type: 'SWITCH_MODEL', payload: name } )

export const renameModel = name => ( { type: 'RENAME_MODEL', payload: name } )

export const startLoadingModel = ( name, field ) => ( { type: 'START_LOADING_MODEL', payload: { name, field } } )

export const finishLoadingModel = () => ( { type: 'FINISH_LOADING_MODEL' } )

export const selectCurrentModel = state =>
{
  const model = state.models.models[ state.models.current ]
  return model.present
}

export const selectDisplayedModel = state =>
{
  const model = state.models.models[ state.models.displayed ]
  return model.present
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
        displayed: name,
      }
    }

    case 'START_LOADING_MODEL': {
      const { name, field } = action.payload
      return {
        ...state,
        current: name,
        models: {
          ...state.models,
          [ name ]: modelReducer( initializeModel( field ) )
        }
      }
    }

    case 'FINISH_LOADING_MODEL': {
      return {
        ...state,
        displayed: state.current,
      }
    }

    case 'RENAME_MODEL': {
      const oldName = state.current
      const name = action.payload
      const model = state.models[ oldName ]
      delete state.models[ oldName ]
      return {
        ...state,
        current: name,
        displayed: name,
        models: {
          ...state.models,
          [ name ]: model
        }
      }
    }

    default:
      const modelState = state.models[ state.current ]
      if ( modelState ) {
        return { ...state,
                  models: {
                    ...state.models,
                    [ state.current ]: modelReducer( modelState, action )
                  }
                }
      } else {
        return state
      }
  }
}