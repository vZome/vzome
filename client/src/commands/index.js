
import centroid from './centroid'
import shortred from './shortred'
import buildstrut from './buildstrut'
import { allSelected, allDeselected } from '../bundles/mesh'

const COMMANDS_DEFINED = 'COMMANDS_DEFINED'

export const init = ( window, store ) =>
{
  store.dispatch( commandsDefined( { centroid, shortred, buildstrut } ) )
}

export const commandsDefined = ( commands ) => ({ type: COMMANDS_DEFINED, payload: commands })

export const commandTriggered = ( cmd, config={} ) => ( dispatch, getState ) =>
{
  switch ( cmd ) {
  
    case 'allSelected':
      dispatch( allSelected() )
      break;
  
    case 'allDeselected':
      dispatch( allDeselected() )
      break;

    default:
      const state = getState().commands
      const command = state[ cmd ]
      dispatch( command( config ) )
  }
}

export const reducer = ( state = {}, action ) =>
{
  switch (action.type) {

    case COMMANDS_DEFINED: {
      const newCommands = action.payload
      return {
        ...state,
        ...newCommands
      }
    }
            
    default:
      return state
  }
}
