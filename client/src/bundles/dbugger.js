
import * as designs from '../bundles/designs'
// import { ActionCreators } from 'redux-undo';
import * as mesh from './mesh'
import { showAlert } from './alerts'
import { vZomeJava, Adapter } from 'react-vzome'

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
    vZomeJava.parse( action, dbugger.parseAndPerformEdit, adapter, editElement, stack, recordSnapshot )
  } catch (error) {
    console.log( error )
    dispatch( showAlert( `Unable to parse vZome design file: ${designName};\n ${error.message}` ) )
    design.success = false
  }
  dispatch( designs.loadedDesign( designName, design ) )
}
