
import { FILE_LOADED } from './files'
import DEFAULT_MODEL from '../models/logo'
import { writeTextFile, callStaticMethod, callObjectMethod, createWriteableFile } from './jre'

// These are dispatched from Java
const BACKGROUND_SET   = 'BACKGROUND_SET'
const SHAPE_DEFINED    = 'SHAPE_DEFINED'
const INSTANCE_ADDED   = 'INSTANCE_ADDED'
const INSTANCE_COLORED = 'INSTANCE_COLORED'
const INSTANCE_REMOVED = 'INSTANCE_REMOVED'
const MODEL_LOADED     = 'MODEL_LOADED'
const LOAD_FAILED      = 'LOAD_FAILED'

const CONTROLLER_RETURNED = 'CONTROLLER_RETURNED'

export const actionTriggered = (actionString) => async (dispatch, getState) =>
{
  const controller = getState().vzomejava.controller
  const path = "/out.dae"
  const file = await createWriteableFile( path )
  callObjectMethod( controller, "doFileAction", actionString, file )  
}

const initialState = {
  renderingOn: true,
  controller: undefined,
  background: '#99ccff',
  instances: DEFAULT_MODEL.instances,
  shapes: DEFAULT_MODEL.shapes
}

export const reducer = ( state = initialState, action ) => {
  switch (action.type) {

    case FILE_LOADED:
      return {
        background: state.background,
        renderingOn: false,
        controller: undefined,
        instances: [],
        shapes: []
      }
    
    case BACKGROUND_SET:
      return {
        ...state,
        background: action.payload
      }

    case SHAPE_DEFINED:
      // note, we don't need to map the vertices any more
      return {
        ...state,
        shapes: [
          ...state.shapes,
          action.payload
        ]
      }
  
    case INSTANCE_ADDED:
      return {
        ...state,
        instances: [
          ...state.instances,
          action.payload
        ]
      }

    case INSTANCE_COLORED: {
      let index = state.instances.findIndex( item => ( item.id === action.payload.id ) )
      if ( index >= 0 ) {
        return {
          ...state,
          instances: [
            ...state.instances.slice(0,index),
            {
              ...state.instances[ index ],
              color: action.payload.color
            },
            ...state.instances.slice(index+1)
          ]
        }
      }
      return state
    }

    case INSTANCE_REMOVED: {
      let index = state.instances.findIndex( item => ( item.id === action.payload.id ) )
      if ( index >= 0 ) {
        return {
          ...state,
          instances: [
            ...state.instances.slice(0,index),
            ...state.instances.slice(index+1)
          ]
        }
      }
      return state
    }

    case MODEL_LOADED:
      return {
        ...state,
        renderingOn : true
      }

    case CONTROLLER_RETURNED:
      return {
        ...state,
        controller: action.payload
      }
  
    case LOAD_FAILED:
      return {
        ...initialState
      }
  
    default:
      return state
  }
}

export const middleware = store => next => async action => 
{
  if ( action.type === FILE_LOADED ) {
    const path = "/str/" + action.payload.name
    writeTextFile( path, action.payload.text )
    callStaticMethod( "com.vzome.cheerpj.JavascriptClientShim", "openFile", path ).then( (controller) =>
    {
      store.dispatch( {
        type: CONTROLLER_RETURNED,
        payload: controller
      } )
    })
  }
  return next( action )
}
