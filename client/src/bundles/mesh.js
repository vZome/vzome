
import goldenField from '../fields/golden'
import commands from '../commands'
import { legacyCommand } from './jsweet'

const OBJECT_SELECTED = 'OBJECT_SELECTED'
const OBJECT_DESELECTED = 'OBJECT_DESELECTED'
const COMMAND_TRIGGERED = 'COMMAND_TRIGGERED'
const CREATE_RANDOM = 'CREATE_RANDOM'
const ALL_SELECTED = 'ALL_SELECTED'
const ALL_DESELECTED = 'ALL_DESELECTED'

export const objectSelected = ( id ) =>
{
  return { type: OBJECT_SELECTED, payload: id }
}

export const objectDeselected = ( id ) =>
{
  return { type: OBJECT_DESELECTED, payload: id }
}

export const allSelected = () =>
{
  return { type: ALL_SELECTED }
}

export const allDeselected = () =>
{
  return { type: ALL_DESELECTED }
}

export const commandTriggered = ( cmd, config={} ) =>
{
  switch ( cmd ) {

    case 'random':
      return createRandom()
  
    case 'allSelected':
      return allSelected()
  
      case 'allDeselected':
        return allDeselected()
    
    default:
      return { type: COMMAND_TRIGGERED, payload: { cmd, config } }
  }
}

export const createRandom = () =>
{
  return { type: CREATE_RANDOM }
}

const initialState = {
  field: goldenField,
  shown: new Map(),
  selected: new Map(),
  hidden: new Map()
}

const canonicalize = ( vectors ) =>
{
  if ( vectors.length === 1 )
    return vectors

  const strings = vectors.map( n => JSON.stringify( n ) )
  let min = strings[ 0 ]
  let minIndex = 0
  for ( let i = 1; i < strings.length; i++ ) {
    if ( strings[ i ].localeCompare( min ) < 0 ) {
      minIndex = i
      min = strings[ i ]
    }
  }
  const sorted = []
  for ( let j = 0; j < vectors.length; j++ ) {
    sorted.push( vectors[ ( minIndex + j ) % vectors.length ] )
  }
  return sorted
}

export const createInstance = ( vectors ) =>
{
  const sorted = canonicalize( vectors )
  return { id: JSON.stringify( sorted ), vectors: sorted }
}

export const reducer = ( state = initialState, action ) =>
{
  switch (action.type) {

    case CREATE_RANDOM: {
      const location = state.field.randomVector()
      const instance = createInstance( [ location ] )
      const { id } = instance
      if ( state.selected.has( id ) || state.shown.has( id ) || state.hidden.has( id ) ) {
        return state
      }
      console.log( `random ball created at ${id}` )
      return {
        ...state,
        selected: new Map( state.selected ).set( id, instance ),
      }
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

    case COMMAND_TRIGGERED: {
      const { cmd, config } = action.payload
      const command = commands[ cmd ] || legacyCommand( cmd, config )
      return command( state )
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

const resolveShape = ( instance ) =>
{
  if ( instance.shapeId )
    return;
  // TODO: make this work for more than balls
  instance.shapeId = "unknown"
  instance.color = "#0088aa"
}

const renderableInstance = ( instance, selected, field ) =>
{
  resolveShape( instance ) // not pure
  const result = {
    ...instance,
    position: field.embedv( instance.vectors[0] ),
    selected
  }
  delete result.vectors
  if ( selected )
    result.color = "#ff4400"
  return result
}

export const supportsEdits = true

export const instanceSelector = ( { mesh } ) =>
{
  const instances = []
  Array.from( mesh.shown    ).map( ( [id, instance] ) => { instances.push( renderableInstance( instance, false, mesh.field ) ) } )
  Array.from( mesh.selected ).map( ( [id, instance] ) => { instances.push( renderableInstance( instance, true,  mesh.field ) ) } )
  return { shapes: [], instances }
}