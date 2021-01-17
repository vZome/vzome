
import * as designs from '../bundles/designs'
import Adapter from './adapter'
import { ActionCreators } from 'redux-undo';
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

const recordEdit = ( { shown, selected, hidden, groups } ) => mesh.meshChanged( shown, selected, hidden, groups )

export const run = designName => ( dispatch, getState ) =>
{
  let design = designs.selectDesign( getState(), designName ) // the starting point only
  const dbugger = designs.selectDebugger( getState(), designName )

  let currentEdit = 0

  const performEdits = ( editElement, adapter, recordEdit, increment ) =>
  {
    do {
      if ( editElement.nodeName === "Branch" ) {
        console.log( '<Branch>' )
        const sandbox = adapter.clone()
        performEdits( editElement.firstElementChild, sandbox, () => ({}), 0 ) // don't record mesh changes for branches, don't increment currentEdit
        // we discard the cloned sandbox adapter
        console.log( '</Branch>' )
      } else {
        console.log( editElement.outerHTML )
        adapter = adapter.clone()  // each command builds on the last
        if ( dbugger.parseAndPerformEdit( editElement, adapter ) ) {
          design = designs.designReducer( design, recordEdit( adapter ) )
          design = designs.designReducer( design, reachedEdit( [ currentEdit+1 ] ) )
        }
      }
      currentEdit += increment
      editElement = editElement.nextElementSibling
    }
    while ( editElement );
  }

  // createCursor with currentEditStack and source
  // stepOver
  let editElement = dbugger.source.firstElementChild
  const targetEdit = dbugger.targetEdit
  try {
    const { shown, selected, hidden, groups } = designs.selectMesh( getState(), designName )
    const adapter = new Adapter( shown, selected, hidden, groups )
    performEdits( editElement, adapter, recordEdit, 1 )

    console.log( `target is ${targetEdit}, currentEdit is ${currentEdit}` )
    while ( currentEdit > targetEdit ) {
      design = designs.designReducer( design, ActionCreators.undo() )
      --currentEdit
    }
    console.log( `target is ${targetEdit}, currentEdit is ${currentEdit}` )
    
  } catch (error) {
    console.log( error )
    dispatch( showAlert( `Unable to parse vZome design file: ${designName};\n ${error.message}` ) )
    design.success = false
  }
  dispatch( designs.loadedDesign( designName, design ) )
}

export const debug = ( designName, action ) => ( dispatch, getState ) =>
{
  let design = designs.selectDesign( getState(), designName ) // the starting point only
  const dbugger = designs.selectDebugger( getState(), designName )
  const { shown, selected, hidden, groups } = designs.selectMesh( getState(), designName )
  
  // All the inner functions here will adjust these, and record changes to them.
  let adapter = new Adapter( shown, selected, hidden, groups )
  let editElement = dbugger.currentElement
  let stack = dbugger.branchStack.slice() // only modified when stepping in or out

  const Stepped = { IN: 0, OVER: 1, OUT: 2, DONE: 3 }

  const step = () =>
  {
    if ( ! editElement )
      return Stepped.DONE
    if ( editElement.nodeName === "Branch" ) {
      const branchAdapter = adapter.clone()
      stack.push( { branch: editElement, adapter } )
      design = designs.designReducer( design, recordEdit( branchAdapter ) )
      design = designs.designReducer( design, reachedEdit( editElement.firstElementChild, stack ) )
      editElement = editElement.firstElementChild // this assumes there are no empty branches
      adapter = branchAdapter
      return Stepped.IN
    } else {
      adapter = adapter.clone()  // each command builds on the last
      dbugger.parseAndPerformEdit( editElement, adapter )
      if ( editElement.nextElementSibling ) {
        design = designs.designReducer( design, recordEdit( adapter ) )
        design = designs.designReducer( design, reachedEdit( editElement.nextElementSibling ) )
        editElement = editElement.nextElementSibling
        return Stepped.OVER
      } else {
        const top = stack.pop()
        if ( top ) {
          adapter = top.adapter.clone()  // overwrite and discard the prior value
          design = designs.designReducer( design, recordEdit( adapter ) )
          editElement = top.branch.nextElementSibling // This assumes there is always a next one, but that should be true
          design = designs.designReducer( design, reachedEdit( editElement, stack ) )
          return Stepped.OUT
        } else {
          // at the end of the editHistory
          design = designs.designReducer( design, recordEdit( adapter ) )
          design = designs.designReducer( design, reachedEdit( null ) )
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

  try {
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
  } catch (error) {
    console.log( error )
    dispatch( showAlert( `Unable to parse vZome design file: ${designName};\n ${error.message}` ) )
    design.success = false
  }
  dispatch( designs.loadedDesign( designName, design ) )
}
