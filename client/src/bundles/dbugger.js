
import * as designs from '../bundles/designs'
import { JavaDomElement } from './wrappers'
import Adapter from './adapter'
import { ActionCreators } from 'redux-undo';
import * as mesh from './mesh'
import { showAlert } from './alerts'

export const initialState = { currentEditStack: [ 0 ], targetEdit: 0 }

export const reducer = ( state = initialState, action ) =>
{
  switch (action.type) {

    case 'SOURCE_LOADED':
      const { source, editFactory, targetEdit } = action.payload
      return {
        ...state,
        source,
        editFactory,
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

export const sourceLoaded = ( source, editFactory, targetEdit ) => ({ type: 'SOURCE_LOADED', payload: { source, editFactory, targetEdit } })

export const reachedEdit = editStack => ( { type: 'EDIT_REACHED', payload: editStack } )

const createCursor = ( element, currentEditStack ) =>
{
  let dbugger
  let editElement
  dbugger.currentEditStack.array.forEach( editNum => {
    let i = 0
    editElement = editElement.firstElementChild
    while ( i < editNum ) {
      editElement = editElement.nextElementSibling
    }
  });
  console.log( `starting from ${dbugger.currentEditStack} ${editElement.outerHTML}`)


}

export const run = designName => ( dispatch, getState ) =>
{
  let design = designs.selectDesign( getState(), designName ) // the starting point only
  const dbugger = designs.selectDebugger( getState(), designName )

  let currentEdit = 0

  const performEdits = ( editElement, adapter, recordEdit, increment ) =>
  {
    do {
      console.log( editElement.outerHTML )
      if ( editElement.nodeName === "Branch" ) {
        const sandbox = adapter.clone()
        performEdits( editElement.firstElementChild, sandbox, () => ({}), 0 ) // don't record mesh changes for branches, don't increment currentEdit
        // we discard the cloned sandbox adapter
      } else {
        const wrappedElement = new JavaDomElement( editElement )
        adapter = adapter.clone()  // each command builds on the last
        // Note that we do not do editor.setAdapter( adapter ) yet.  This means that we cannot
        //  deal with edits that have side-effects in their constructors!
        const edit = dbugger.editFactory( wrappedElement )
        // null edit only happens for expected cases (e.g. "Shapshot"); others become CommandEdit
        if ( edit ) {
          edit.deserializeAndPerform( adapter )
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
    // TODO: move this to Adapter itself?
    const recordEdit = ( { shown, selected, hidden } ) => mesh.meshChanged( shown, selected, hidden )

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

export const stepOver = () => ( dispatch, getState ) =>
{

}

export const stepIn = () => ( dispatch, getState ) =>
{
  // if current is branch then...
  // else stepOver
}

export const stepOut = () => ( dispatch, getState ) =>
{

}

