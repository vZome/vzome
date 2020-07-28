
import { ActionTypes } from "redux-simple-websocket"
import { URL_PROVIDED, FILE_SELECTED, VIEW_CLOSED, JAVA_MESSAGE_RECEIVED } from '../action-events'
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
  selectedFile: "",
  connectionLive: false,
  instances: DEFAULT_MODEL.instances,
  shapes: DEFAULT_MODEL.shapes,
  lastError: null
}, action) => {
  switch (action.type) {

    case URL_PROVIDED:
      return {
        ...state,
        modelUrl: action.payload
      }

    case FILE_SELECTED:
      return {
        ...state,
        selectedFile: action.payload,
        instances: [],
        shapes: []
      }
  
    case VIEW_CLOSED:
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

    case JAVA_MESSAGE_RECEIVED:
      return handleMessage( state, action.payload );
      
    default:
      return state
  }
}

export default reducer

