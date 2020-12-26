
import { fetchModel, download } from './files'
import DEFAULT_MODEL from '../models/logo'
import { startProgress, stopProgress } from './progress'

// These are dispatched from Java
const SHAPE_DEFINED    = 'SHAPE_DEFINED'
const INSTANCE_ADDED   = 'INSTANCE_ADDED'
const INSTANCE_COLORED = 'INSTANCE_COLORED'
const INSTANCE_REMOVED = 'INSTANCE_REMOVED'
const MODEL_LOAD_STARTED = 'MODEL_LOAD_STARTED'
export const MODEL_LOADED     = 'MODEL_LOADED'
const LOAD_FAILED      = 'LOAD_FAILED'

const CONTROLLER_RETURNED = 'CONTROLLER_RETURNED'

const JAVA_CODE_LOADED = 'JAVA_CODE_LOADED'
export const FILE_EXPORTED = 'FILE_EXPORTED'

export const exportTriggered = ( extension, message ) => async (dispatch, getState) =>
{
  dispatch( startProgress( message ) )
  const controller = getState().java.controller
  const path = "/" + getState().java.fileName.replace( ".vZome", "." + extension )
  const file = await createWriteableFile( path )
  callObjectMethod( controller, "doFileAction", "export." + extension, file ).then( () =>
  {
    dispatch( stopProgress() )
  })
}

const normalize = ( instance ) =>
{
  const pos = instance.position
  const quat = instance.rotation
  const rotation = quat? [ quat.x, quat.y, quat.z, quat.w ] : [ 1, 0, 0, 0 ]
  return { ...instance, shapeId: instance.shape, position: [ pos.x, pos.y, pos.z ], rotation }
}

const initialState = {
  javaReady: false,
  parser: undefined,
  readOnly: true,
  renderingOn: true,
  controller: undefined,
  fileName: undefined,
  shapes: DEFAULT_MODEL.shapes,
  instances: DEFAULT_MODEL.instances.map( normalize ),
  previous: DEFAULT_MODEL.instances.map( normalize )
}

export const reducer = ( state = initialState, action ) => {
  switch (action.type) {

    case JAVA_CODE_LOADED:
      return { ...state, javaReady: true, parser: action.payload }

    case MODEL_LOAD_STARTED:
      return {
        ...state,
        fileName: action.payload,
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
          normalize( action.payload )
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

export const parseModelFile = ( name, xmlText, dispatch, getState ) =>
{
  dispatch( {type: MODEL_LOAD_STARTED, payload: name } )
  dispatch( startProgress( "Parsing vZome model, please be patient..." ) )
  const path = "/str/" + name
  writeTextFile( path, xmlText )
  callStaticMethod( "com.vzome.cheerpj.JavascriptClientShim", "openFile", path )
    .then( (controller) =>
    {
      dispatch( { type: CONTROLLER_RETURNED, payload: controller } )
      dispatch( stopProgress() )
    })
}


const LOG_LEVEL = 'INFO'

export const callObjectMethod = ( object, methodname, ...args ) =>
{
  return window.cjCall( object, methodname, ...args )
}

export const callStaticMethod = ( classname, methodname, ...args ) =>
{
  return window.cjCall( classname, methodname, ...args )
}

export const createWriteableFile = ( path ) =>
{
  return window.cjNew( "java.io.File", `/files${path}` )
}

export const writeTextFile = ( path, text ) =>
{
  return window.cheerpjAddStringFile( path, text )
}

export const init = ( window, store ) =>
{
  // CheerpJ Global.jsCallS requires global functions to call
  //   from Java back into Javascript.  The simplest way to
  //   provide global functions is to attach them to window.
  window.dispatchToRedux = (s) => {
    store.dispatch( JSON.parse( s ) )
  }
  window.fileExported = download

  const resourcesToPreload = [
    "/lt/runtime/rt.jar.java.nio.js",
    "/lt/cheerpj/Arial.ttf",
    "/lts/rt.jar.c27.txt",
    "/lt/runtime/rt.jar.jdk.js",
    "/lts/rt.jar",
    "/lts/rt.jar.c0.txt",
    "/lts/rt.jar.c1.txt",
    "/lt/runtime/rt.jar.java.util.function.js",
    "/lts/rt.jar.c29.txt",
    "/lts/rt.jar.c28.txt",
    "/lts/rt.jar.c98.txt",
    "/lts/rt.jar.c97.txt",
    "/lts/rt.jar.c37.txt",
    "/lts/rt.jar.c39.txt",
    "/lts/rt.jar.c38.txt",
    "/lts/rt.jar.c82.txt",
    "/lts/rt.jar.c83.txt",
    "/lts/rt.jar.c84.txt",
    "/lts/rt.jar.c85.txt",
    "/lts/rt.jar.c86.txt",
    "/lts/rt.jar.c87.txt",
    "/lts/rt.jar.c88.txt",
    "/lts/rt.jar.c89.txt",
    "/lts/rt.jar.c90.txt",
    "/lts/rt.jar.c91.txt",
    "/lts/rt.jar.c92.txt",
    "/lts/rt.jar.c93.txt",
    "/lts/rt.jar.c94.txt",
    "/lts/rt.jar.c95.txt",
    "/lts/rt.jar.c96.txt",
    "/lts/rt.jar.c81.txt",
    "/lt/runtime/rt.jar.com.sun.org.apache.xml.js",
    "/lt/runtime/rt.jar.java.awt.event.js",
    "/lt/cheerpj/lib/security/java.policy",
    "/lt/runtime/rt.jar.sun.reflect.js",
    "/lt/runtime/rt.jar.javax.swing.event.js",
    "/lt/runtime/rt.jar.javax.swing.js",
    "/lt/runtime/rt.jar.sun.security.provider.js",
    "/lt/cheerpj/lib/security/java.security",
    "/lt/runtime/rt.jar.java.lang.js",
    "/lts/rt.jar.c20.txt",
    "/lts/rt.jar.c21.txt",
    "/lt/runtime/rt.jar.java.nio.file.js",
    "/lt/runtime/rt.jar.java.util.stream.js",
    "/lt/runtime/rt.jar.com.sun.java.js",
    "/lt/runtime/rt.jar.com.sun.org.apache.xerces.internal.dom.js",
    "/lt/runtime/rt.jar.com.sun.org.apache.xerces.internal.impl.dtd.js",
    "/lt/runtime/rt.jar.java.util.concurrent.js",
    "/lt/runtime/rt.jar.com.sun.xml.internal.stream.js",
    "/lt/runtime/rt.jar.com.sun.org.apache.xerces.js",
    "/lt/runtime/rt.jar.com.sun.org.apache.xerces.internal.impl.js",
    "/lt/runtime/rt.jar.com.sun.org.apache.xerces.internal.util.js",
    "/lt/runtime/rt.jar.com.sun.org.apache.xerces.internal.parsers.js",
    "/lt/runtime/rt.jar.com.sun.imageio.js",
    "/lt/runtime/rt.jar.com.js",
    "/lts/rt.jar.c78.txt",
    "/lts/rt.jar.c80.txt",
    "/lts/rt.jar.c79.txt",
    "/lt/runtime/rt.jar.sun.net.js",
    "/lt/runtime/rt.jar.com.sun.org.apache.xml.internal.serializer.js",
    "/lt/runtime/rt.jar.com.sun.org.apache.xml.internal.utils.js",
    "/lt/runtime/rt.jar.com.sun.org.apache.xalan.js",
    "/lt/runtime/rt.jar.sun.net.www.js",
    "/lt/runtime/rt.jar.com.sun.org.apache.xalan.internal.xsltc.js",
    "/lt/runtime/rt.jar.com.sun.org.apache.xerces.internal.jaxp.js",
    "/lt/cheerpj/lib/jaxp.properties",
    "/lt/runtime/rt.jar.javax.xml.js",
    "/lt/runtime/rt.jar.java.awt.dnd.js",
    "/lts/rt.jar.c22.txt",
    "/lts/rt.jar.c23.txt",
    "/lt/runtime/rt.jar.java.security.js",
    "/lt/runtime/rt.jar.java.util.zip.js",
    "/lt/runtime/rt.jar.java.util.jar.js",
    "/lt/cheerpj/lib/security/local_policy.jar",
    "/lts/rt.jar.c24.txt",
    "/lts/rt.jar.c26.txt",
    "/lts/rt.jar.c25.txt",
    "/lt/runtime/rt.jar.jdk.internal.org.js",
    "/lt/runtime/rt.jar.sun.js",
    "/lt/runtime/rt.jar.java.lang.invoke.js",
    "/lt/cheerpj/lib/accessibility.properties",
    "/lt/runtime/rt.jar.sun.awt.im.js",
    "/lt/cheerpj/lib/currency.properties",
    "/lt/cheerpj/lib/currency.data",
    "/lt/runtime/rt.jar.sun.awt.util.js",
    "/lt/runtime/rt.jar.sun.nio.js",
    "/lt/runtime/rt.jar.sun.text.js",
    "/lt/runtime/rt.jar.java.net.js",
    "/lt/cheerpj/lib/ext/localedata.jar",
    "/lt/runtime/rt.jar.java.util.spi.js",
    "/lt/runtime/rt.jar.sun.util.locale.js",
    "/lt/runtime/rt.jar.sun.awt.geom.js",
    "/lt/runtime/rt.jar.java.util.regex.js",
    "/lt/runtime/rt.jar.java.js",
    "/lt/runtime/rt.jar.java.awt.font.js",
    "/lt/cheerpj/lib/tzdb.dat",
    "/lt/runtime/rt.jar.java.io.js",
    "/lt/runtime/rt.jar.sun.awt.resources.js",
    "/lt/runtime/rt.jar.sun.misc.js",
    "/lt/runtime/rt.jar.javax.js",
    "/lt/runtime/rt.jar.java.util.logging.js",
    "/lt/runtime/rt.jar.java.awt.im.js",
    "/lt/runtime/rt.jar.java.beans.js",
    "/lt/runtime/rt.jar.java.text.js",
    "/lts/rt.jar.c2.txt",
    "/lt/runtime/rt.jar.java.util.concurrent.locks.js",
    "/lt/runtime/rt.jar.java.util.concurrent.atomic.js",
    "/lts/rt.jar.c73.txt",
    "/lts/rt.jar.c75.txt",
    "/lts/rt.jar.c74.txt",
    "/lt/runtime/rt.jar.java.util.js",
    "/lts/meta-index.c0.txt",
    "/lts/meta-index",
    "/lt/runtime/rt.jar.sun.util.js",
    "/lt/cheerpj/lib/logging.properties",
    "/lt/runtime/rt.jar.org.js",
    "/lt/runtime/rt.jar.com.sun.org.apache.xerces.internal.impl.dv.js",
    "/lts/rt.jar.c19.txt",
    "/lts/rt.jar.c18.txt",
    "/lt/runtime/rt.jar.java.awt.js",
    "/lt/runtime/rt.jar.sun.font.js",
    "/lt/runtime/rt.jar.java.awt.geom.js",
    "/lt/runtime/rt.jar.sun.awt.js"
 ]

  window.cheerpjInit( {
    preloadResources: resourcesToPreload,
    status: "splash",
    javaProperties: [ "java.protocol.handler.pkgs=com.leaningtech.handlers" ]
  } )
  
  const pp = "/app" + window.location.pathname + "/"
  
  const classpath = `${pp}desktop-7.0.jar:${pp}cheerpj-dom.jar:${pp}core-7.0.jar:${pp}jackson-annotations-2.9.3.jar:${pp}jackson-core-2.9.5.jar:${pp}jackson-databind-2.9.5.jar:${pp}javax.json-1.0.4.jar:${pp}vecmath-1.6.0-final.jar`
  
  window.cheerpjRunMain( "com.vzome.cheerpj.JavascriptClientShim", classpath, LOG_LEVEL ).then( () =>
  {
    store.dispatch( { type: JAVA_CODE_LOADED, payload: parseModelFile })

    let url = "/app/models/vZomeLogo.vZome"
    const urlParams = new URLSearchParams( window.location.search );
    if ( urlParams.has( "url" ) ) {
      url = decodeURI( urlParams.get( "url" ) )
    }
    store.dispatch( fetchModel( url ) )
  } )
}
