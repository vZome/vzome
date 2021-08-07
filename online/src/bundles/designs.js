
import { combineReducers } from 'redux'
import undoable, { ActionCreators as Undo } from 'redux-undo'

import { goldenField, parse } from '@vzome/react-vzome'
import * as mesh from './mesh'
import { reducer as cameraReducer, initialState as cameraDefault, cameraDefined } from './camera.js'
import * as dbugger from './dbugger.js'
import * as renderers from './renderers.js'
import { showAlert } from './alerts.js'
import { fetchUrlText } from '../bundles/files.js'
import { convertFOV } from './camera.js'

const identityReducer = ( state="", action ) => state

export const designReducer = combineReducers( {
  mesh: undoable( mesh.reducer ),
  dbugger: undoable( dbugger.reducer ),
  camera: cameraReducer,
  // These cannot be changed (YET), so a constant reducer is fine
  lighting: identityReducer, 
  embedding: identityReducer, 
  name: identityReducer,
  text: identityReducer,
  field: identityReducer,
  rendererName: identityReducer,
  preview: identityReducer,
})

export const initializeDesign = ( field, name, rendererName, text ) => ({
  mesh: {
    past: [],
    present: field && mesh.justOrigin( field ),
    future: [],
  },
  // TODO: initialize dbugger?
  camera: cameraDefault,
  rendererName,
  field,
  name,
  text,
})

const emptyState = {
  nextUntitledIndex: 0,
  data: {},
}

const addNewModel = ( state, field ) =>
{
  const name = `untitled ${field.name} ${state.nextUntitledIndex}`
  return {
    nextUntitledIndex: state.nextUntitledIndex + 1,
    current: name,
    data: {
      ...state.data,
      [ name ]: initializeDesign( field, name )
    }
  }
}

export const newModel = field => ( { type: 'NEW_MODEL', payload: field } )

export const switchModel = id => ( { type: 'SWITCH_MODEL', payload: id } )

export const renameModel = name => ( { type: 'RENAME_MODEL', payload: name } )

export const loadingDesign = ( id, design ) => ( { type: 'LOADING_DESIGN', payload: { id, design } } )

export const loadedDesign = ( id, design ) => ( { type: 'LOADED_DESIGN', payload: { id, design } } )

export const selectDesign = ( state, id ) => state.designs.data[ id ] || state.designs.data[ state.designs.current ]

export const selectDesignName = ( state, id ) => selectDesign( state, id ).name

export const selectText = ( state, id ) => selectDesign( state, id ).text

export const selectPreview = ( state, id ) => selectDesign( state, id ).preview

export const selectMesh = ( state, id ) => selectDesign( state, id ).mesh.present

export const selectDebugger = ( state, id ) => {
  const design = selectDesign( state, id )
  return design && design.dbugger && design.dbugger.present // No dbugger if the field was unknown
}

export const selectCamera = ( state, id ) => selectDesign( state, id ).camera

export const selectLighting = ( state, id ) => selectDesign( state, id ).lighting

export const selectEmbedding = ( state, id ) => selectDesign( state, id ).embedding

export const selectSource = ( state, id ) => selectDebugger( state, id ).source

export const selectField = ( state, id ) => selectDesign( state, id ).field

export const selectRenderer = ( state, id ) =>
{
  const rendererName = selectDesign( state, id ).rendererName || "default"
  return state.renderers[ rendererName ]
}

export const reducer = ( state = addNewModel( emptyState, goldenField ), action ) =>
{
  switch ( action.type ) {

    case 'NEW_MODEL':
      return addNewModel( action.payload )
  
    case 'CLOSE_MODEL':
      return state;

    case 'SWITCH_MODEL': {
      const name = action.payload
      return {
        ...state,
        current: name,
      }
    }

    case 'LOADING_DESIGN': {
      const { id, design } = action.payload
      return {
        ...state,
        data: {
          ...state.data,
          [ id ]: design
        }
      }
    }

    case 'LOADED_DESIGN': {
      const { id, design } = action.payload
      return {
        ...state,
        current: id,
        data: {
          ...state.data,
          [ id ]: design
        }
      }
    }

    case 'RENAME_MODEL': {
      const oldName = state.current
      const name = action.payload
      const model = state.data[ oldName ]
      delete state.data[ oldName ]
      return {
        ...state,
        current: name,
        data: {
          ...state.data,
          [ name ]: model
        }
      }
    }

    default:
      // All other actions are funneled only to the current design; we don't
      //  want any changes to affect all designs simultaneously.
      const modelState = state.data[ state.current ]
      if ( modelState ) {
        return {
          ...state,
          data: {
            ...state.data,
            [ state.current ]: designReducer( modelState, action )
          }
        }
      } else {
        return state
      }
  }
}

const IDENTITY_MATRIX = [1,0,0,0,0,1,0,0,0,0,1,0,0,0,0,1]

const q2m = ({ x, y, z, w }) =>
{
  const q = [ w, x, y, z ]
  const q01 = q[0]*q[1], q02 = q[0]*q[2], q03 = q[0]*q[3]
  const q11 = q[1]*q[1], q12 = q[1]*q[2], q13 = q[1]*q[3]
  const q21 = q12, q22 = q[2]*q[2], q23 = q[2]*q[3]
  const q31 = q13, q32 = q23, q33 = q[3]*q[3]
  
  return [1-2*(q22+q33), 2*(q12-q03), 2*(q13+q02), 0,
          2*(q21+q03), 1-2*(q33+q11), 2*(q23-q01), 0,
          2*(q31-q02), 2*(q32+q01), 1-2*(q11+q22), 0,
          0, 0, 0, 1 ]
};

const convertPreview = preview =>
{
  let i = 0
  let { lights, camera, shapes, instances, embedding, orientations } = preview
  instances = instances.map( ({ position, orientation, color, shape }) => {
    const id = "id_" + i++
    const { x, y, z } = position
    const rotation = orientations[ orientation ] || IDENTITY_MATRIX
    return { id, position: [ x, y, z ], rotation, color, shapeId: shape }
  })
  const dlights = lights.directionalLights.map( ({ direction, color }) => {
    const { x, y, z } = direction
    return { direction: [ x, y, z ], color }
  })
  const { lookAtPoint, upDirection, lookDirection, viewDistance, fieldOfView, near, far } = camera
  const lookAt = [ ...Object.values( lookAtPoint ) ]
  const up = [ ...Object.values( upDirection ) ]
  const lookDir = [ ...Object.values( lookDirection ) ]
  camera = {
    near, far, up, lookAt,
    fov: convertFOV( fieldOfView ),
    position: lookAt.map( (e,i) => e - viewDistance * lookDir[ i ] ),
  }
  return { lighting: { ...lights, directionalLights: dlights }, camera, shapes, instances, embedding }
}

export const openDesign = ( textPromise, url ) => async ( dispatch, getState ) =>
{
  try {
    const name = decodeURI( url.split( '\\' ).pop().split( '/' ).pop() )
    if ( ! name.endsWith( ".vZome" ) ) {
      const message = `Unrecognized file name: ${url}`
      console.log( message )
      dispatch( showAlert( message ) )
      return
    }

    const text = await textPromise
    if ( !text ) {
      const message = `Unable to retrieve XML from ${url}`
      console.log( message )
      dispatch( showAlert( message ) )
      return
    }

    const previewUrl = url.substring( 0, url.length-6 ).concat( ".shapes.json" )
    fetchUrlText( previewUrl )
    .then( previewText => {
      let design = initializeDesign( null, name, null, text )
      design.preview = convertPreview( JSON.parse( previewText ) )
      design.camera = design.preview.camera
      design.lighting = design.preview.lighting
      design.embedding = design.preview.embedding
      dispatch( loadingDesign( name, design ) )
      dispatch( loadedDesign( name, design ) )
    })
    .catch( async error => {
      console.log( `Preview load of "${previewUrl}" failed due to error: ${error}` )

      const failure = message => {
        console.log( message )
        let design = initializeDesign( null, name, null, text )
        dispatch( loadingDesign( name, design ) )
        dispatch( loadedDesign( name, design ) )
        dispatch( showAlert( message + ' Use the download button to save this file, then try opening it with desktop vZome.' ) )
      }

      const { firstEdit, camera, field, targetEdit, renderer, lighting } = await parse( text ) || {}

      if ( field.unknown ) {
        failure( `Field "${field.name}" is not implemented.` )
        return
      }
      if ( !firstEdit ) {
        failure( `Unable to parse XML from ${url}` )
        return
      }

      console.log( `Opening ${url}`)

      // We don't want to dispatch all the edits, which can trigger tons of
      //  overhead and re-rendering.  Instead, we'll build up a design locally
      //  by calling the designReducer manually.
      let design = initializeDesign( field, name, renderer.name, text )
      design.lighting = lighting
      design.embedding = renderer.embedding
      // Each call to designReducer may create an element in the history
      //  (if it has any changes to the mesh),
      //  so we want to be judicious in when we do it.
      // Each call to dispatch, on the other hand, triggers rendering, so we want to be even
      //  more careful about that.
      
      // TODO: skip this dispatch if we already have a renderer for shapesName, in getState().renderers
      dispatch( renderers.rendererDefined( renderer.name, renderer ) ) // outside the design

      design = designReducer( design, cameraDefined( camera ) )
      design = designReducer( design, dbugger.sourceLoaded( firstEdit, targetEdit ) ) // recorded in history
      design = designReducer( design, Undo.clearHistory() )  // kind of a hack so both histories are in sync, with no past

      dispatch( loadingDesign( name, design ) )

      if ( ! getState().dbuggerEnabled ) {
        dispatch( dbugger.stepper.done( name ) )
      }
      else {
        dispatch( loadedDesign( name, design ) )
      }
    })
  } catch (error) {
    const message = `Unable to parse vZome design file: ${url};\n ${error.message}`
    console.log( message )
    dispatch( showAlert( message ) )
  }
}

export const serialize = ( { camera, field, rendererName, dbugger } ) =>
{
  const serializeEdit = dbugr => dbugr.nextEdit && dbugr.nextEdit.serialize()
  let edits = dbugger.past.map( serializeEdit )
  edits.push( { ...serializeEdit( dbugger.present ), targetEdit: true } )
  edits = edits.concat( dbugger.future.map( serializeEdit ) )
  return JSON.stringify( { edits, camera, field: field.name, rendererName }, null, '  ' )
}