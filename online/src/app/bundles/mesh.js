
import { createInstance } from '../../wc/legacy/adapter.js'

const OBJECT_SELECTED = 'OBJECT_SELECTED'
const OBJECT_DESELECTED = 'OBJECT_DESELECTED'
const ALL_SELECTED = 'ALL_SELECTED'
const ALL_DESELECTED = 'ALL_DESELECTED'
const MESH_CHANGED = 'MESH_CHANGED'

export const objectSelected = ( id ) => ({ type: OBJECT_SELECTED, payload: id })

export const objectDeselected = ( id ) => ({ type: OBJECT_DESELECTED, payload: id })

export const selectionToggled = ( id, selected ) => selected? objectDeselected( id ) : objectSelected( id )

export const allSelected = () => ({ type: ALL_SELECTED })

export const allDeselected = () => ({ type: ALL_DESELECTED })

export const meshChanged = ( shown, selected, hidden, groups ) => ({ type: MESH_CHANGED, payload: { shown, selected, hidden, groups } })

export const justOrigin = field => {
  const originBall = createInstance( [ field.origin( 3 ) ] )
  const shown = new Map().set( originBall.id, originBall )
  return {
    shown,
    selected: new Map(),
    hidden: new Map(),
    groups: []
  }
}

const initialState = {
  shown: new Map(),
  selected: new Map(), // This Map is especially important, so we iterate in insertion order
  hidden: new Map(),
  groups: []
}

export const reducer = ( state = initialState, action ) =>
{
  switch (action.type) {

    case MESH_CHANGED: {
      return { ...state, ...action.payload } // replace shown, selected, hidden, groups
    }

    case OBJECT_SELECTED: { // TODO: use groups here
      const id = action.payload
      const obj = state.shown.get( id )
      if ( typeof obj === "undefined" ) {
        throw new Error( "something went wrong" )
      }
      const shown = new Map( state.shown )
      shown.delete( id )
      return {
        ...state, shown,
        selected: new Map( state.selected ).set( id, obj ),
      }
    }

    case OBJECT_DESELECTED: { // TODO: use groups here
      const id = action.payload
      const obj = state.selected.get( id )
      if ( typeof obj === "undefined" ) {
        throw new Error( "something went wrong" )
      }
      const selected = new Map( state.selected )
      selected.delete( id )
      return {
        ...state, selected,
        shown: new Map( state.shown ).set( id, obj ),
      }
    }

    case ALL_SELECTED: {
      return {
        ...state,
        shown: new Map(),
        selected: new Map( [ ...state.selected, ...state.shown ] )
      }
    }

    case ALL_DESELECTED: {
      return {
        ...state,
        selected: new Map(),
        shown: new Map( [ ...state.selected, ...state.shown ] )
      }
    }
            
    default:
      return state
  }
}
