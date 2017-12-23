
import { ActionTypes } from "redux-simple-websocket"
import { OPEN_URL, CLOSE_VIEW } from '../actions'

const reducer = (state = {
  modelUrl: "",
  connectionLive: false,
  segments: [],
  lastError: null
}, action) => {
  switch (action.type) {

    case OPEN_URL:
      return {
        ...state,
        modelUrl: action.url
      }

    case CLOSE_VIEW:
      return {
        ...state,
				modelUrl: "",
        connectionLive: false,
        segments: []
      }

    case ActionTypes.WEBSOCKET_CONNECTED:
      return {
        ...state,
        connectionLive: true,
      }

		case ActionTypes.WEBSOCKET_ERROR:
			return {
				...state,
				lastError: action.error
			}

		case ActionTypes.WEBSOCKET_DISCONNECTED:
			return {
				...state,
        connectionLive: false,
        segments: []
			}

		case ActionTypes.SEND_DATA_TO_WEBSOCKET:
			return {
				...state
			}

		case ActionTypes.RECEIVED_WEBSOCKET_DATA:
      const parsed = action.payload;
      if ( parsed.render ) {
				if ( parsed.render === 'segment' ) {
					return {
						...state,
						segments: [
							...state.segments,
							parsed
						]
					}
				} else if ( parsed.render === 'delete' ) {
				  const index = state.segments.findIndex( item => ( item.id === parsed.id ) )
					return {
						...state,
						segments: [
							...state.segments.slice(0,index),
							...state.segments.slice(index+1)
						]
					}
				} else {
					return state
				}
      } else {
				return state
      }

    default:
      return state
  }
}

export default reducer

