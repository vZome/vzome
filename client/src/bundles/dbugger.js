
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
      const element = action.payload
      return {
        ...state,
        currentElement: element,
      }

    case 'ENTERING_BRANCH': {
      const { element, adapter } = action.payload
      const branchStack = state.branchStack.slice()
      branchStack.push( { branch: element, adapter } )
      return {
        ...state,
        currentElement: element.firstElementChild,
        branchStack,
      }
    }

    default:
      return state
  }
}

export const sourceLoaded = ( source, parseAndPerformEdit, targetEdit ) => ({ type: 'SOURCE_LOADED', payload: { source, parseAndPerformEdit, targetEdit } })

export const reachedEdit = element => ( { type: 'EDIT_REACHED', payload: element } )

export const enteringBranch = ( element, adapter ) => ( { type: 'ENTERING_BRANCH', payload: { element, adapter } } )

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
  let adapter = new Adapter( shown, selected, hidden, groups )
  let editElement = dbugger.currentElement

  const conTinue = () =>
  {
    while ( editElement ) {
      const nextElement = stepOver()
      if ( nextElement ) {
        editElement = nextElement
      } else {
        return
      }
    }
  }

  const stepIn = () =>
  {
    if ( editElement.nodeName === "Branch" ) {
      const branchAdapter = adapter.clone()
      design = designs.designReducer( design, recordEdit( branchAdapter ) )
      design = designs.designReducer( design, enteringBranch( editElement, adapter ) )
      editElement = editElement.firstElementChild
      adapter = branchAdapter
    } else {
      stepOver()
    }
  }

  const stepOver = () =>
  {
    if ( editElement.nodeName === "Branch" ) {
      editElement = stepIn()
      return stepOut()
    } else {
      adapter = adapter.clone()  // each command builds on the last
      dbugger.parseAndPerformEdit( editElement, adapter )
      // if ( !nextElement ) {
      //   const parent = editElement.parentElement
      //   nextElement = ( parent.nodeName === 'EditHistory' )? null : parent.nextElementSibling
      // }
      design = designs.designReducer( design, recordEdit( adapter ) )
      design = designs.designReducer( design, reachedEdit( editElement.nextElementSibling ) )
      return editElement.nextElementSibling
    }
  }

  const stepOut = () =>
  {
    while ( editElement ) {
      const nextElement = stepOver()
      if ( nextElement ) {
        editElement = nextElement
      } else {
        editElement = editElement.parentElement
        if ( editElement.nodeName === "Branch" ) {

        } else {

        }
        return
      }
    }
  }

  try {
    switch ( action ) {

      case 'STEP_IN':
        stepIn()
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
