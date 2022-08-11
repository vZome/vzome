
export const configureLogging = loggingPkg =>
{
  const { Level, Logger } = loggingPkg;
  const loggers = {

    "" : Level.SEVERE,

    // "com.vzome.core.editor"           : Level.FINEST,

    // "com.vzome.core.editor.selection" : Level.FINEST,

    // "com.vzome.core.editor.ApplyTool" : Level.FINEST,
  }

  Object.entries( loggers ) .forEach( ( [ key, value ] ) => {
    const logger = Logger.getLogger( key );
    logger .setLevel( value );
    console.log( `LOGGING ${key} at ${value}` );
  });
}
