
import goldenField from '../fields/golden'
import commands from '../commands'
import { legacyCommand } from './jsweet'

const OBJECT_PICKED = 'OBJECT_PICKED'
const COMMAND_TRIGGERED = 'COMMAND_TRIGGERED'
const CREATE_RANDOM = 'CREATE_RANDOM'

export const objectPicked = ( id ) =>
{
  return { type: OBJECT_PICKED, payload: id }
}

export const commandTriggered = ( cmd ) =>
{
  return { type: COMMAND_TRIGGERED, payload: cmd }
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

    case OBJECT_PICKED: {
      const id = action.payload
      const obj = state.shown.get( id )
      if ( typeof obj === "undefined" ) {
        throw new Error( "something went wrong" )
      }
      return {
        ...state,
        shown: new Map( state.shown ).delete( id ),
        selected: new Map( state.selected ).set( id, obj ),
      }
    }

    case COMMAND_TRIGGERED: {
      let command = commands[ action.payload ] || legacyCommand( action.payload )
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