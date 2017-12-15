
import { WEBSOCKET_OPEN, WEBSOCKET_CLOSED, WEBSOCKET_MESSAGE } from 'redux-websocket'

import { OPEN_URL, CLOSE_VIEW } from '../actions'

const reducer = (state = {
  modelUrl: "",
  connectionLive: false,
  segments: []
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
        connectionLive: false,
        segments: []
      }

    case WEBSOCKET_OPEN:
      return {
        ...state,
        connectionLive: true
      }

    case WEBSOCKET_MESSAGE:
      const parsed = JSON.parse( action.payload.data );
      if ( parsed.render ) {
        return {
          ...state,
          segments: [
            ...state.segments,
            parsed
          ]
        }
      } else {
        console.log( "server info: " + parsed.info );
        return state
      }

    case WEBSOCKET_CLOSED:
      return {
        ...state,
        connectionLive: false,
        segments: []
      }

    default:
      return state
  }
}

export default reducer

