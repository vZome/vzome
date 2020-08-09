
import { FILE_LOADED, fetchModel } from './files'
import DEFAULT_MODEL from '../models/logo'
import { writeTextFile, callStaticMethod, callObjectMethod, createWriteableFile, JAVA_CODE_LOADED } from './jre'
import { startProgress, stopProgress } from './progress'

// These are dispatched from Java
const SHAPE_DEFINED    = 'SHAPE_DEFINED'
const INSTANCE_ADDED   = 'INSTANCE_ADDED'
const INSTANCE_COLORED = 'INSTANCE_COLORED'
const INSTANCE_REMOVED = 'INSTANCE_REMOVED'
export const MODEL_LOADED     = 'MODEL_LOADED'
const LOAD_FAILED      = 'LOAD_FAILED'

const CONTROLLER_RETURNED = 'CONTROLLER_RETURNED'

export const actionTriggered = ( actionString, message ) => async (dispatch, getState) =>
{
  dispatch( startProgress( message ) )
  const controller = getState().vzomejava.controller
  const path = "/out.dae"
  const file = await createWriteableFile( path )
  callObjectMethod( controller, "doFileAction", actionString, file ).then( () =>
  {
    dispatch( stopProgress() )
  })
}

const initialState = {
  renderingOn: true,
  controller: undefined,
  shapes: DEFAULT_MODEL.shapes,
  instances: DEFAULT_MODEL.instances,
  previous: DEFAULT_MODEL.instances,
}

export const reducer = ( state = initialState, action ) => {
  switch (action.type) {

    case FILE_LOADED:
      return {
        ...state,
        renderingOn: false,
        controller: undefined,
        instances: [],
        previous: state.instances
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
        renderingOn : true,
        previous: []
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
  if ( action.type === JAVA_CODE_LOADED ) {
    store.dispatch( fetchModel( "/app/models/vZomeLogo.vZome" ) )
  }

  if ( action.type === FILE_LOADED ) {
    store.dispatch( startProgress( "Parsing vZome model..." ) )
    const path = "/str/" + action.payload.name
    writeTextFile( path, action.payload.text )
    callStaticMethod( "com.vzome.cheerpj.JavascriptClientShim", "openFile", path )
      .then( (controller) =>
      {
        store.dispatch( {
          type: CONTROLLER_RETURNED,
          payload: controller
        } )
        store.dispatch( stopProgress() )
      })
  }
  
  return next( action )
}
