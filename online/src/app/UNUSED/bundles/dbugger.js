
import * as designs from '../bundles/designs.js'
import * as meshes from './mesh.js'
import { showAlert } from './alerts.js'
import { ActionCreators as UndoActionCreators } from 'redux-undo'

export const reducer = ( state = {}, action ) =>
{
  switch (action.type) {

    case 'SOURCE_LOADED':
      const { firstEdit, targetEdit } = action.payload
      return {
        ...state,
        source: firstEdit,
        targetEdit,
        nextEdit: firstEdit,
        branchStack: []
      }

    case 'EDIT_REACHED':
      const { edit, branchStack } = action.payload
      return {
        ...state,
        nextEdit: edit,
        branchStack: branchStack || state.branchStack
      }

    default:
      return state
  }
}

export const sourceLoaded = ( firstEdit, targetEdit ) => ({ type: 'SOURCE_LOADED', payload: { firstEdit, targetEdit } })

export const reachedEdit = ( edit, branchStack ) => ( { type: 'EDIT_REACHED', payload: { edit, branchStack } } )

const debug = action => designName => async ( dispatch, getState ) =>
{
  let design = designs.selectDesign( getState(), designName ) // the starting point only
  const dbugger = designs.selectDebugger( getState(), designName )
  const mesh = designs.selectMesh( getState(), designName )

  let edit = dbugger.nextEdit
  let stack = dbugger.branchStack.slice() // only modified when stepping in or out

  let targetPast = -1   // if we reach the targetEdit, this will get set to at least zero

  const recordSnapshot = ( mesh, id, edit, stack ) =>
  {
    const { shown, selected, hidden, groups } = mesh
    if ( id === dbugger.targetEdit ) {
      targetPast = design.dbugger.past.length
      // This is where we will undo back to
    }
    design = designs.designReducer( design, meshes.meshChanged( shown, selected, hidden, groups ) )
    design = designs.designReducer( design, reachedEdit( edit, stack ) )
  }

  try {
    // TODO this is outdated now, since we now need an abstraction of the state, not a mesh
    await interpret( action, mesh, edit, stack, recordSnapshot )
  } catch (error) {
    console.log( error )
    dispatch( showAlert( `Failure interpreting ${designName}: ${error.message}` ) )
    design.success = false
  }
  if ( targetPast >= 0 ) {
    design = designs.designReducer( design, UndoActionCreators.jumpToPast( targetPast ) )
  }
  dispatch( designs.loadedDesign( designName, design ) )
}

export const stepper = {
  in: debug( Step.IN ),
  over: debug( Step.OVER ),
  out: debug( Step.OUT ),
  done: debug( Step.DONE ),
}