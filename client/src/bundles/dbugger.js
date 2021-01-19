
import * as designs from '../bundles/designs'
import Adapter from './adapter'
// import { ActionCreators } from 'redux-undo';
import * as mesh from './mesh'
import { showAlert } from './alerts'

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

    // console.log( `target is ${targetEdit}, currentEdit is ${currentEdit}` )
    // while ( currentEdit > targetEdit ) {
    //   design = designs.designReducer( design, ActionCreators.undo() )
    //   --currentEdit
    // }
    // console.log( `target is ${targetEdit}, currentEdit is ${currentEdit}` )

const parse = ( action, parseAndPerform, adapter, editElement, stack=[], recordSnapshot ) =>
{
  const Stepped = { IN: 0, OVER: 1, OUT: 2, DONE: 3 }

  const step = () =>
  {
    if ( ! editElement )
      return Stepped.DONE
    if ( editElement.nodeName === "Branch" ) {
      const branchAdapter = adapter.clone()
      stack.push( { branch: editElement, adapter } )
      recordSnapshot && recordSnapshot( branchAdapter, editElement.firstElementChild, stack )
      editElement = editElement.firstElementChild // this assumes there are no empty branches
      adapter = branchAdapter
      return Stepped.IN
    } else {
      adapter = adapter.clone()  // each command builds on the last
      parseAndPerform( editElement, adapter )
      if ( editElement.nextElementSibling ) {
        recordSnapshot && recordSnapshot( adapter, editElement.nextElementSibling )
        editElement = editElement.nextElementSibling
        return Stepped.OVER
      } else {
        const top = stack.pop()
        if ( top ) {
          adapter = top.adapter.clone()  // overwrite and discard the prior value
          editElement = top.branch.nextElementSibling // This assumes there is always a next one, but that should be true
          recordSnapshot && recordSnapshot( adapter, editElement, stack )
          return Stepped.OUT
        } else {
          // at the end of the editHistory
          recordSnapshot && recordSnapshot( adapter, null )
          return Stepped.DONE
        }
      }
    }
  }

  const conTinue = () =>
  {
    let stepped
    do {
      stepped = stepOut()
    } while ( stepped !== Stepped.DONE );
  }

  const stepOver = () =>
  {
    const stepped = step()
    switch ( stepped ) {

      case Stepped.IN:
        stepOut()
        return Stepped.OVER
    
      default:
        return stepped
    }
  }

  const stepOut = () =>
  {
    let stepped
    do {
      stepped = stepOver()
    } while ( stepped !== Stepped.OUT && stepped !== Stepped.DONE )
    return stepped
  }

  switch ( action ) {

    case 'STEP_IN':
      step()
      break;
  
    case 'STEP_OVER':
      stepOver()
      break;
  
    case 'STEP_OUT':
      stepOut()
      break;
  
    case 'CONTINUE':
    default:
      conTinue()
      break;
  }
}

export const debug = ( designName, action ) => ( dispatch, getState ) =>
{
  let design = designs.selectDesign( getState(), designName ) // the starting point only
  const dbugger = designs.selectDebugger( getState(), designName )
  const { shown, selected, hidden, groups } = designs.selectMesh( getState(), designName )

  let adapter = new Adapter( shown, selected, hidden, groups )
  let editElement = dbugger.currentElement
  let stack = dbugger.branchStack.slice() // only modified when stepping in or out

  const recordSnapshot = ( adapter, editElement, stack ) =>
  {
    const { shown, selected, hidden, groups } = adapter
    design = designs.designReducer( design, mesh.meshChanged( shown, selected, hidden, groups ) )
    design = designs.designReducer( design, reachedEdit( editElement, stack ) )
  }

  try {
    parse( action, dbugger.parseAndPerformEdit, adapter, editElement, stack, recordSnapshot )
  } catch (error) {
    console.log( error )
    dispatch( showAlert( `Unable to parse vZome design file: ${designName};\n ${error.message}` ) )
    design.success = false
  }
  dispatch( designs.loadedDesign( designName, design ) )
}
