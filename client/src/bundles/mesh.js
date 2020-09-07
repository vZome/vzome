
import goldenField from '../fields/golden'
import commands from '../commands'
import { legacyCommand } from './jsweet'

const OBJECT_SELECTED = 'OBJECT_SELECTED'
const OBJECT_DESELECTED = 'OBJECT_DESELECTED'
const COMMAND_TRIGGERED = 'COMMAND_TRIGGERED'
const CREATE_RANDOM = 'CREATE_RANDOM'

export const objectSelected = ( id ) =>
{
  return { type: OBJECT_SELECTED, payload: id }
}

export const objectDeselected = ( id ) =>
{
  return { type: OBJECT_DESELECTED, payload: id }
}

export const commandTriggered = ( cmd, config={} ) =>
{
  return { type: COMMAND_TRIGGERED, payload: { cmd, config } }
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

export const reducer = ( state = initialState, action ) =>
{
  switch (action.type) {

    case CREATE_RANDOM: {
      const location = state.field.randomVector()
      const id = JSON.stringify( [ location ] )
      if ( state.selected.has( id ) || state.shown.has( id ) || state.hidden.has( id ) ) {
        return state
      }
      console.log( `random ball created at ${id}` )
      return {
        ...state,
        selected: new Map( state.selected ).set( id, [ location ] ),
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

    default:
      return state
  }
}

export const middleware = store => next => async action => 
{
  return next( action )
}

export const init = ( window, store ) =>
{
  store.dispatch( createRandom() )
  store.dispatch( createRandom() )
  store.dispatch( createRandom() )
  store.dispatch( createRandom() )
}