
import * as designs from '../bundles/designs'
import { JavaDomElement } from './wrappers'
import Adapter from './adapter'
import { ActionCreators } from 'redux-undo';
import * as mesh from './mesh'
import { showAlert } from './alerts'

export const initialState = { currentEditStack: [ 0 ], targetEdit: ':' }

export const reducer = ( state = initialState, action ) =>
{
  switch (action.type) {

    case 'SOURCE_LOADED':
      const { source, parseAndPerformEdit, targetEdit } = action.payload
      return {
        ...state,
        source,
        parseAndPerformEdit,
        targetEdit,
      }

    case 'EDIT_REACHED':
      const currentEditStack = action.payload
      return {
        ...state,
        currentEditStack,
      }

    default:
      return state
  }
}

export const sourceLoaded = ( source, parseAndPerformEdit, targetEdit ) => ({ type: 'SOURCE_LOADED', payload: { source, parseAndPerformEdit, targetEdit } })

export const reachedEdit = editStack => ( { type: 'EDIT_REACHED', payload: editStack } )

const recordEdit = ( { shown, selected, hidden } ) => mesh.meshChanged( shown, selected, hidden )

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
    const { shown, selected, hidden } = designs.selectMesh( getState(), designName )
    const adapter = new Adapter( shown, selected, hidden )
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

export const stepOver = designName => ( dispatch, getState ) =>
{
  const dbugger = designs.selectDebugger( getState(), designName )
  let design = designs.selectDesign( getState(), designName ) // the starting point only
  const { shown, selected, hidden } = designs.selectMesh( getState(), designName )
  const adapter = new Adapter( shown, selected, hidden )

  try {
    // if current is branch then...
    // else step
    if ( dbugger.parseAndPerformEdit( dbugger.sourceNode, adapter ) ) {
      design = designs.designReducer( design, recordEdit( adapter ) )
      // design = designs.designReducer( design, reachedEdit( [ currentEdit+1 ] ) )
    }
  } catch (error) {
    console.log( error )
    dispatch( showAlert( `Unable to parse vZome design file: ${designName};\n ${error.message}` ) )
    design.success = false
  }
  dispatch( designs.loadedDesign( designName, design ) )
}

export const stepIn = designName => ( dispatch, getState ) =>
{
  // if current is branch then...
  // else step
}

export const stepOut = designName => ( dispatch, getState ) =>
{
  // stepOver until the frame ends
}

class StackFrame
{
  constructor( parentNode, parentFrame )
  {
    this.sourceNode = parentNode.firstElementChild
    this.index = 0
    this.parentFrame = parentFrame
  }

  getSourceId()
  {
    const parentId = this.parentFrame? this.parentFrame.getSourceId() : ""
    return parentId + ':' + this.index
  }
}