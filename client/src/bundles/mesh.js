

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

export const meshChanged = ( shown, selected, hidden ) => ({ type: MESH_CHANGED, payload: { shown, selected, hidden } })

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

const canonicalizedId = ( vectors ) =>
{
  const strings = vectors.map( n => JSON.stringify( n ) )
  let min = strings[ 0 ]
  let minIndex = 0
  for ( let i = 1; i < strings.length; i++ ) {
    if ( strings[ i ].localeCompare( min ) < 0 ) {
      minIndex = i
      min = strings[ i ]
    }
  }
  // Since % does not actually perform a consistent sawtooth modulus function for negative numbers,
  //  we add strings.length to force a positive number.
  const get = i => strings[ ( strings.length + minIndex + i ) % strings.length ]

  // We have the minimum (get(0)), now figure out which way to go
  const incr = ( get( 1 ).localeCompare( get( -1 ) ) < 0 )? 1 : -1

  let sorted = ""
  for ( let j = 0; j < strings.length; j++ ) {
    const next = get( j * incr )
    sorted += next
  }
  return sorted
}

export const createInstance = ( vectors ) =>
{
  const id = canonicalizedId( vectors )
  // We cannot rearrange vectors, as that would break strut semantics for some legacy commands
  return { id, vectors }
}

export const reducer = ( state = initialState, action ) =>
{
  switch (action.type) {

    case MESH_CHANGED: {
      return { ...state, ...action.payload } // replace shown, selected, hidden, groups
    }

    case OBJECT_SELECTED: {
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

    case OBJECT_DESELECTED: {
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
