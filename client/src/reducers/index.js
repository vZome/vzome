
import { ActionTypes } from "redux-simple-websocket"
import { OPEN_URL, CLOSE_VIEW } from '../actions'

const reducer = (state = {
  modelUrl: "",
  connectionLive: false,
  segments: [],
  balls: [],
  shapes: [],
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
        shapes: [],
        segments: [],
        balls: []
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
        shapes: [],
        segments: [],
        balls: []
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
				} else if ( parsed.render === 'ball' ) {
					return {
						...state,
						balls: [
							...state.balls,
							parsed
						]
					}
				} else if ( parsed.render === 'shape' ) {
					return {
						...state,
						shapes: [
							...state.shapes,
							parsed.shape
						]
					}
				} else if ( parsed.render === 'delete' ) {
				  let index = state.segments.findIndex( item => ( item.id === parsed.id ) )
				  if ( index >= 0 ) {
						return {
							...state,
							segments: [
								...state.segments.slice(0,index),
								...state.segments.slice(index+1)
							]
						}
					}
				  index = state.balls.findIndex( item => ( item.id === parsed.id ) )
				  if ( index >= 0 ) {
						return {
							...state,
							balls: [
								...state.balls.slice(0,index),
								...state.balls.slice(index+1)
							]
						}
					}
					return state
				} else {
					return state
				}
      } else {
      	console.log( parsed.info );
				return state
      }

    default:
      return state
  }
}

export default reducer

