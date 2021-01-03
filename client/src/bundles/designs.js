
import { combineReducers } from 'redux'
import undoable from 'redux-undo'

import goldenField from '../fields/golden'
import * as mesh from './mesh'
import * as camera from './camera'
import * as dbugger from './dbugger'

/*

There are a few odd things happening here.

The first is that all actions (except those defined here) are funneled to the current design;
see the default case in the main reducer below.

Next, each design has its own undoable history, as well as reducers that are NOT undoable, like the camera.

Finally, I'm trying to collect dbugger and mesh changes together into atomic undoable steps.
Later, I'll do the same thing to record commands for save.  This is the reason for editStepReducer.

*/

export const editStepReducer = combineReducers( {
  mesh: mesh.reducer,
  dbugger: dbugger.reducer,
  // TODO: record last command
} )

export const designReducer = combineReducers( {
  history: undoable( editStepReducer ),
  camera: camera.reducer,
  success: ( state="", action ) => state, // success cannot be changed, so a constant reducer is fine
  fieldName: ( state="", action ) => state, // fieldName cannot be changed, so a constant reducer is fine
  shaperName: ( state="", action ) => state, // shaperName cannot be changed (YET!), so a constant reducer is fine
})

export const initializeDesign = ( field, shaperName ) => ({
  history: {
    past: [],
    present: {
      mesh: mesh.justOrigin( field ),
      dbugger: [ 0 ],
    },
    future: [],
  },
  success: true,
  camera: camera.initialState,
  fieldName: field.name,
  shaperName,
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

export const loadingDesign = ( name, design ) => ( { type: 'LOADING_DESIGN', payload: { name, design } } )

export const loadedDesign = ( name, design ) => ( { type: 'LOADED_DESIGN', payload: { name, design } } )

export const selectDesign = ( state, name ) => state.designs.data[ name || state.designs.current ]

export const selectMesh = ( state, name ) => selectDesign( state, name ).history.present.mesh

export const selectDebugger = ( state, name ) => selectDesign( state, name ).history.present.dbugger

export const selectCamera = ( state, name ) => selectDesign( state, name ).camera

export const selectSource = ( state, name ) => selectDebugger( state, name ).source

export const selectField = ( state, name ) => state.fields[ selectDesign( state, name ).fieldName ]

export const selectShaper = ( state, name ) =>
{
  const shaperName = selectDesign( state, name ).shaperName || "solid connectors"
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
      const { name, design } = action.payload
      return {
        ...state,
        data: {
          ...state.data,
          [ name ]: design
        }
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