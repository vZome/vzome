
const LOG_LEVEL = 'FINE'

const JAVA_CODE_LOADED = 'JAVA_CODE_LOADED'

export const reducer = ( state = { javaReady: false }, action ) => {
  switch (action.type) {

    case JAVA_CODE_LOADED:
      return { javaReady: true }

    default:
      return state
  }
}

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
  // CheerpJ Global.jsCallS requires a global function to call
  //   from Java back into Javascript.  The simplest way to
  //   provide a global function that can dispatch is this,
  //   attaching it to window.
  window.dispatchToRedux = (s) => {
    store.dispatch( JSON.parse( s ) )
  }

  const resourcesToPreload = [
    "/lt/runtime/rt.jar.java.util.function.js",
    "/lt/runtime/rt.jar.jdk.js",
    "/lts/rt.jar",
    "/lts/rt.jar.c0.txt",
    "/lts/rt.jar.c1.txt",
    "/lts/rt.jar.c98.txt",
    "/lts/rt.jar.c97.txt",
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
    "/lt/runtime/rt.jar.sun.reflect.js",
    "/lt/runtime/rt.jar.java.lang.js",
    "/lt/runtime/rt.jar.java.nio.file.js",
    "/lt/runtime/rt.jar.java.util.concurrent.js",
    "/lt/runtime/rt.jar.com.js",
    "/lt/runtime/rt.jar.sun.net.js",
    "/lt/runtime/rt.jar.com.sun.org.apache.xerces.internal.impl.dv.js",
    "/lt/runtime/rt.jar.org.js",
    "/lt/runtime/rt.jar.sun.nio.js",
    "/lt/runtime/rt.jar.java.util.logging.js",
    "/lts/rt.jar.c2.txt",
    "/lt/runtime/rt.jar.java.util.concurrent.atomic.js",
    "/lt/runtime/rt.jar.java.util.concurrent.locks.js",
    "/lt/runtime/rt.jar.java.util.js",
    "/lt/cheerpj/lib/logging.properties",
    "/lt/runtime/rt.jar.sun.util.js",
    "/lts/meta-index",
    "/lts/meta-index.c0.txt",
    "/lts/rt.jar.c74.txt",
    "/lts/rt.jar.c75.txt",
    "/lts/rt.jar.c73.txt",
    "/lt/runtime/rt.jar.java.text.js",
    "/lt/runtime/rt.jar.java.beans.js",
    "/lt/runtime/rt.jar.java.awt.im.js",
    "/lt/runtime/rt.jar.javax.js",
    "/lt/runtime/rt.jar.sun.misc.js",
    "/lt/runtime/rt.jar.sun.awt.resources.js",
    "/lt/runtime/rt.jar.java.io.js",
    "/lt/cheerpj/lib/tzdb.dat",
    "/lt/runtime/rt.jar.java.awt.font.js",
    "/lt/runtime/rt.jar.java.js",
    "/lt/runtime/rt.jar.java.util.regex.js",
    "/lt/runtime/rt.jar.sun.awt.geom.js",
    "/lt/runtime/rt.jar.sun.util.locale.js",
    "/lt/runtime/rt.jar.java.util.spi.js",
    "/lt/cheerpj/lib/ext/localedata.jar",
    "/lt/runtime/rt.jar.java.net.js",
    "/lt/runtime/rt.jar.sun.text.js",
    "/lt/runtime/rt.jar.sun.awt.util.js",
    "/lt/cheerpj/lib/currency.data",
    "/lt/cheerpj/lib/currency.properties",
    "/lt/runtime/rt.jar.sun.awt.im.js",
    "/lt/cheerpj/lib/accessibility.properties",
    "/lt/runtime/rt.jar.java.lang.invoke.js",
    "/lt/runtime/rt.jar.sun.js",
    "/lt/runtime/rt.jar.jdk.internal.org.js",
    "/lts/rt.jar.c25.txt",
    "/lts/rt.jar.c26.txt",
    "/lts/rt.jar.c24.txt",
    "/lt/cheerpj/lib/security/local_policy.jar",
    "/lt/runtime/rt.jar.java.util.jar.js",
    "/lt/runtime/rt.jar.java.util.zip.js",
    "/lt/runtime/rt.jar.java.security.js",
    "/lts/rt.jar.c23.txt",
    "/lts/rt.jar.c22.txt",
    "/lt/runtime/rt.jar.java.awt.dnd.js",
    "/lt/runtime/rt.jar.javax.xml.js",
    "/lt/cheerpj/lib/jaxp.properties",
    "/lt/runtime/rt.jar.com.sun.org.apache.xerces.internal.jaxp.js",
    "/lt/runtime/rt.jar.com.sun.org.apache.xalan.internal.xsltc.js",
    "/lt/runtime/rt.jar.sun.net.www.js",
    "/lt/runtime/rt.jar.com.sun.org.apache.xalan.js",
    "/lt/runtime/rt.jar.com.sun.org.apache.xml.internal.utils.js",
    "/lt/runtime/rt.jar.com.sun.org.apache.xml.internal.serializer.js",
    "/lts/rt.jar.c79.txt",
    "/lts/rt.jar.c80.txt",
    "/lts/rt.jar.c78.txt",
    "/lt/runtime/rt.jar.com.sun.imageio.js",
    "/lt/runtime/rt.jar.com.sun.org.apache.xerces.internal.parsers.js",
    "/lt/runtime/rt.jar.com.sun.org.apache.xerces.internal.util.js",
    "/lt/runtime/rt.jar.com.sun.org.apache.xerces.internal.impl.js",
    "/lt/runtime/rt.jar.com.sun.org.apache.xerces.js",
    "/lt/runtime/rt.jar.com.sun.xml.internal.stream.js",
    "/lt/runtime/rt.jar.com.sun.org.apache.xerces.internal.impl.dtd.js",
    "/lt/runtime/rt.jar.com.sun.org.apache.xerces.internal.dom.js",
    "/lt/runtime/rt.jar.com.sun.java.js",
    "/lt/runtime/rt.jar.java.util.stream.js",
    "/lts/rt.jar.c21.txt",
    "/lts/rt.jar.c20.txt",
    "/lt/cheerpj/lib/security/java.security",
    "/lt/runtime/rt.jar.sun.security.provider.js",
    "/lt/runtime/rt.jar.javax.swing.js",
    "/lt/runtime/rt.jar.javax.swing.event.js",
    "/lt/cheerpj/lib/security/java.policy",
    "/lt/runtime/rt.jar.java.awt.event.js",
    "/lt/runtime/rt.jar.com.sun.org.apache.xml.js",
    "/lts/rt.jar.c38.txt",
    "/lts/rt.jar.c39.txt",
    "/lts/rt.jar.c37.txt",
    "/lts/rt.jar.c28.txt",
    "/lts/rt.jar.c29.txt",
    "/lts/rt.jar.c27.txt",
    "/lt/cheerpj/Arial.ttf",
    "/lt/runtime/rt.jar.java.nio.js"
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
    store.dispatch( {
      type: JAVA_CODE_LOADED
    } )
  } )
  
  // I get a failure later (after file open) if I don't do this.
  //  Something in the class loading triggers AWT, perhaps, so I see
  //  "Graphics system is initializing" before the crash.
  window.cheerpjCreateDisplay( 1,1 )
}
