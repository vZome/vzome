
import * as designs from '../bundles/designs.js'
// import { ActionCreators } from 'redux-undo';
import * as mesh from './mesh.js'
import { showAlert } from './alerts.js'
import { vZomeJava, Adapter } from '@vzome/react-vzome'
import { ActionCreators as UndoActionCreators } from 'redux-undo'

export const reducer = ( state = {}, action ) =>
{
  switch (action.type) {

    case 'SOURCE_LOADED':
      const { source, parseAndPerformEdit, targetEdit } = action.payload
      return {
        ...state,
        source,
        parseAndPerformEdit,
        targetEdit,
        currentElement: source.firstElementChild,
        branchStack: []
      }

    case 'EDIT_REACHED':
      const { element, branchStack } = action.payload
      return {
        ...state,
        currentElement: element,
        branchStack: branchStack || state.branchStack
      }

    default:
      return state
  }
}

export const sourceLoaded = ( source, parseAndPerformEdit, targetEdit ) => ({ type: 'SOURCE_LOADED', payload: { source, parseAndPerformEdit, targetEdit } })

export const reachedEdit = ( element, branchStack ) => ( { type: 'EDIT_REACHED', payload: { element, branchStack } } )

export const debug = ( designName, action ) => ( dispatch, getState ) =>
{
  let design = designs.selectDesign( getState(), designName ) // the starting point only
  const dbugger = designs.selectDebugger( getState(), designName )
  const { shown, selected, hidden, groups } = designs.selectMesh( getState(), designName )

  let adapter = new Adapter( shown, selected, hidden, groups )
  let editElement = dbugger.currentElement
  let stack = dbugger.branchStack.slice() // only modified when stepping in or out

  let targetPast = -1   // if we reach the targetEdit, this will get set to at least zero

  const recordSnapshot = ( adapter, id, editElement, stack ) =>
  {
    const { shown, selected, hidden, groups } = adapter
    if ( id === dbugger.targetEdit ) {
      targetPast = design.dbugger.past.length
      console.log( targetPast )
      // This is where we will undo back to
    }
    design = designs.designReducer( design, mesh.meshChanged( shown, selected, hidden, groups ) )
    design = designs.designReducer( design, reachedEdit( editElement, stack ) )
  }

  try {
    vZomeJava.interpret( action, dbugger.parseAndPerformEdit, adapter, editElement, stack, recordSnapshot )
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
