
import undoable from 'redux-undo'

const OBJECT_SELECTED = 'OBJECT_SELECTED'
const OBJECT_DESELECTED = 'OBJECT_DESELECTED'
const COMMANDS_DEFINED = 'COMMANDS_DEFINED'
const FIELD_DEFINED = 'FIELD_DEFINED'
const RESOLVER_DEFINED = 'RESOLVER_DEFINED'
const ALL_SELECTED = 'ALL_SELECTED'
const ALL_DESELECTED = 'ALL_DESELECTED'
const MESH_CHANGED = 'MESH_CHANGED'

export const objectSelected = ( id ) => ({ type: OBJECT_SELECTED, payload: id })

export const objectDeselected = ( id ) => ({ type: OBJECT_DESELECTED, payload: id })

export const selectionToggled = ( id, selected ) => selected? objectDeselected( id ) : objectSelected( id )

export const allSelected = () => ({ type: ALL_SELECTED })

export const allDeselected = () => ({ type: ALL_DESELECTED })

export const meshChanged = ( shown, selected, hidden ) => ({ type: MESH_CHANGED, payload: { shown, selected, hidden } })

export const createRandom = () => ( dispatch, getState ) =>
{
  let { shown, selected, hidden, field, resolver } = getState().mesh.present
  shown = new Map( shown )
  selected = new Map( selected )
  hidden = new Map( hidden )

  const location = field.randomVector()
  let instance = createInstance( [ location ] )
  const { id } = instance

  instance = shown.get( id ) || selected.get( id ) || hidden.get( id ) || instance
  shown.delete( id ) || selected.delete( id ) || hidden.delete( id )
  selected = selected.set( id, instance )

  dispatch( meshChanged( shown, selected, hidden ) )
  dispatch( resolver.resolve( [ instance ] ) )
}

export const fieldDefined = ( field ) => ({ type: FIELD_DEFINED, payload: field })

export const resolverDefined = ( resolver ) => ({ type: RESOLVER_DEFINED, payload: resolver })

export const commandsDefined = ( commands ) => ({ type: COMMANDS_DEFINED, payload: commands })

export const commandTriggered = ( cmd, config={} ) => ( dispatch, getState ) =>
{
  switch ( cmd ) {

    case 'random':
      dispatch( createRandom() )
      break;
  
    case 'allSelected':
      dispatch( allSelected() )
      break;
  
    case 'allDeselected':
      dispatch( allDeselected() )
      break;

    default:
      const state = getState().mesh.present
      const command = state.commands[ cmd ]
      dispatch( command( config ) )
  }
}

const initialState = {
  shown: new Map(),
  selected: new Map(), // This Map is especially important, so we iterate in insertion order
  hidden: new Map(),
  fields: {},
  field: undefined,
  resolver: undefined,
  commands: {},
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
  let sorted = ""
  for ( let j = 0; j < strings.length; j++ ) {
    sorted += strings[ ( minIndex + j ) % strings.length ]
  }
  return sorted
}

export const createInstance = ( vectors ) =>
{
  const id = canonicalizedId( vectors )
  // We cannot rearrange vectors, as that would break strut semantics for some legacy commands
  return { id, vectors }
}

export const reducer = undoable( ( state = initialState, action ) =>
{
  switch (action.type) {

    case MESH_CHANGED: {
      return { ...state, ...action.payload } // replace shown, selected, hidden
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

    case FIELD_DEFINED: {
      const newField = action.payload
      return {
        ...state,
        field: newField, // TODO use a different action to set this
        fields: { ...state.fields, [newField.name]: newField }
      }
    }

    case RESOLVER_DEFINED: {
      const resolver = action.payload
      return { ...state, resolver }
    }

    case COMMANDS_DEFINED: {
      const newCommands = action.payload
      return {
        ...state,
        commands: { ...state.commands, ...newCommands }
      }
    }
            
    default:
      return state
  }
} )
