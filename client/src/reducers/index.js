
import { ActionTypes } from "redux-simple-websocket"
import { OPEN_URL, CLOSE_VIEW, JAVA_MESSAGE } from '../actions'
import { DEFAULT_MODEL } from '../models/dodecahedron'

const handleMessage = ( state, parsed ) => {
  if ( parsed.render ) {
    console.log( parsed );
    if ( parsed.render === 'instance' ) {
      return {
        ...state,
        instances: [
          ...state.instances,
          parsed
        ]
      }
    } else if ( parsed.render === 'shape' ) {
      // note, we don't need to map the vertices any more
      return {
        ...state,
        shapes: [
          ...state.shapes,
          parsed
        ]
      }
    } else if ( parsed.render === 'delete' ) {
      let index = state.instances.findIndex( item => ( item.id === parsed.id ) )
      if ( index >= 0 ) {
        console.log( 'deleting instance' );
        return {
          ...state,
          instances: [
            ...state.instances.slice(0,index),
            ...state.instances.slice(index+1)
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
}

const reducer = (state = {
  modelUrl: "",
  connectionLive: false,
  instances: DEFAULT_MODEL.instances,
  shapes: DEFAULT_MODEL.shapes,
  lastError: null
}, action) => {
  switch (action.type) {

    case OPEN_URL:
      return {
        ...state,
        modelUrl: action.payload
      }

    case CLOSE_VIEW:
      return {
        ...state,
        modelUrl: "",
        connectionLive: false,
        instances: DEFAULT_MODEL.instances,
        shapes: DEFAULT_MODEL.shapes
      }

    case ActionTypes.WEBSOCKET_CONNECTED:
      return {
        ...state,
        connectionLive: true,
        instances: [],
        shapes: []
      }

    case ActionTypes.WEBSOCKET_ERROR:
      return {
        ...state,
        lastError: action.error
      }

    case ActionTypes.WEBSOCKET_DISCONNECTED:
      return {
        ...state,
        connectionLive: false
      }

    case ActionTypes.SEND_DATA_TO_WEBSOCKET:
      return {
        ...state
      }

    case ActionTypes.RECEIVED_WEBSOCKET_DATA:
      return handleMessage( state, action.payload );

    case JAVA_MESSAGE:
      return handleMessage( state, action.payload );
      
    default:
      return state
  }
}

export default reducer

