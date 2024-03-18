package com.vzome.desktop.awt;

import java.io.File;
import java.util.Properties;
import java.util.logging.ConsoleHandler;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

import org.vorthmann.j3d.J3dComponentFactory;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.vzome.core.algebra.AlgebraicMatrix;
import com.vzome.core.render.JsonMapper;
import com.vzome.core.render.RenderedManifestation;
import com.vzome.core.render.RenderedModel;
import com.vzome.core.render.Scene;
import com.vzome.core.viewing.Camera;
import com.vzome.core.viewing.Lights;
import com.vzome.desktop.api.Controller;
import com.vzome.desktop.controller.CameraController;
import com.vzome.desktop.controller.JsonClientRendering;

public abstract class JsonClientShim implements JsonClientRendering.EventDispatcher
{
    protected final ApplicationController applicationController;
    private final ObjectMapper objectMapper = new ObjectMapper();
    protected final ObjectWriter objectWriter = objectMapper .writer();
    private final JsonMapper mapper = new JsonMapper();

    private final static Logger rootLogger = Logger .getLogger("");
    
    public JsonClientShim( String logLevel )
    {
        System .setProperty( "java.util.logging.SimpleFormatter.format", "%4$s: %5$s%n" );
        Level minLevel = Level.parse( logLevel );
        if ( rootLogger .getLevel() .intValue() > minLevel.intValue() )
        {
            rootLogger.setLevel( minLevel );  // Set minimum logging level
        }

        // If a ConsoleHandler is already pre-configured by the logging.properties file then just use it as-is.
        ConsoleHandler consoleHandler = null;
        for ( Handler handler : rootLogger.getHandlers() ) {
            if  (handler.getClass().isAssignableFrom( ConsoleHandler.class ) ) {
                consoleHandler = (ConsoleHandler) handler;
                break;
            }
        }

        // If no ConsoleHandler was pre-configured, then initialze our own default
        if ( consoleHandler == null )
        {
            try {
                consoleHandler = new ConsoleHandler();
            } catch (Exception e1) {
                rootLogger.log( Level.WARNING, "unable to set up syserr console log handler", e1 );
            }
            if (consoleHandler != null) {
                consoleHandler .setFormatter( new SimpleFormatter() );
                rootLogger .addHandler( consoleHandler );
            }
        }
        if (consoleHandler != null)
            consoleHandler .setLevel( minLevel );

        Properties props = new Properties();
        props .setProperty( "entitlement.model.edit", "true" );
        props .setProperty( "keep.alive", "true" );
        props .setProperty( "headless.open", "true" );

        this .applicationController = new ApplicationController( new ApplicationController.UI()
        {   
            @Override
            public void doAction( String action )
            {
                rootLogger .warning( "Unhandled UI event: " + action );
                new RuntimeException( "WARNING: Unhandled UI event" ) .printStackTrace();
            }

            @Override
            public void runScript( String script, File file )
            {
                rootLogger .warning( "runScript: " + script );
            }

            @Override
            public void openApplication( File file )
            {
                JsonClientShim.this .openApplication( file );
            }
        }, props, new J3dComponentFactory()
        {
            @Override
            public RenderingViewer createRenderingViewer( Scene scene, boolean lightweight )
            {
                // Can't happen, because of "headless.open" property setting above.
                return null;
            }
        });
        this .applicationController .setErrorChannel( new Controller.ErrorChannel()
        {
            @Override
            public void reportError(String errorCode, Object[] arguments)
            {
                rootLogger .severe( errorCode );
                dispatchEvent( "GENERAL_ERROR", errorCode );
                if ( arguments .length > 0 ) {
                    Object arg = arguments[ 0 ];
                    if ( arg instanceof Throwable ) {
                        ((Throwable) arg) .printStackTrace();
                    }
                }
            }

            @Override
            public void clearError() {}
        });
        if ( rootLogger .isLoggable( Level.INFO ) ) rootLogger .info( "ApplicationController created" );
    }

    public void dispatchEventNode( ObjectNode event )
    {
        try {
            String eventStr = this .objectWriter .writeValueAsString( event );
            dispatchSerializedJson( eventStr );
        } catch (JsonProcessingException e) {
            e .printStackTrace();
            rootLogger .severe( "dispatchEventNode failed: " + e .getMessage() );
        }
    }

    public abstract void dispatchSerializedJson( String eventStr );
    
    public abstract void openApplication( File file );

    public void dispatchEvent( String type, JsonNode payload )
    {
        ObjectNode event = this .objectMapper .createObjectNode();
        event .put( "type", type );
        event .set( "payload", payload );
        dispatchEventNode( event );
    }

    private void dispatchEvent( String type, String payload )
    {
        ObjectNode event = this .objectMapper .createObjectNode();
        event .put( "type", type );
        event .put( "payload", payload );
        dispatchEventNode( event );
    }

    protected DocumentController renderDocument( String path )
    {
        DocumentController docController = (DocumentController) this .applicationController .getSubController( path );
        if ( docController == null ) {
            rootLogger .severe( "Document load FAILURE: " + path );
            dispatchEvent( "LOAD_FAILED", "Document load FAILURE: " + path );
            return null;
        }

        Camera camera = ((CameraController) docController .getSubController( "camera" )) .getView();
        JsonNode cameraJson = this .objectMapper .valueToTree( camera );
        if ( cameraJson != null )
            dispatchEvent( "CAMERA_DEFINED", cameraJson );
        
        Lights lights = docController .getScene() .getLighting();
        JsonNode lightsJson = this .objectMapper .valueToTree( lights );
        if ( lightsJson != null )
            dispatchEvent( "LIGHTS_DEFINED", lightsJson );

        // TODO: define a callback to support the ControllerWebSocket case?
        //        consumer.start();
        JsonClientRendering clientRendering = new JsonClientRendering( this, false );
        RenderedModel renderedModel = docController .getModel() .getRenderedModel();
        renderedModel .addListener( clientRendering );
        RenderedModel .renderChange( new RenderedModel( null, null ), renderedModel, clientRendering ); // get the origin ball
        try {
            docController .actionPerformed( this, "finish.load" );
            rootLogger .info( "Document load success: " + path );
            for ( RenderedManifestation rm : renderedModel ) {
                ObjectNode instance = this .objectMapper .valueToTree( rm );
                AlgebraicMatrix orientation = rm .getOrientation();
                if ( orientation != null ) {
                    ObjectNode quaternion = this .mapper .getQuaternionNode( rm .getOrientation() );
                    instance .set( "rotation", quaternion );
                }
                dispatchEvent( "INSTANCE_ADDED", instance );            
            }
            clientRendering .enableInstanceStream( true );
            dispatchEvent( "MODEL_LOADED", "" );            
        } catch ( Exception e ) {
            e.printStackTrace();
            rootLogger .severe( "Document load unknown FAILURE: " + path );
            dispatchEvent( "LOAD_FAILED", "Document load unknown FAILURE: " + path );
        }
        return docController;
    }

    public static void main( String[] args )
    {
        try {
            JsonClientShim shim = new JsonClientShim( "FINE" )
            {
                @Override
                public void dispatchSerializedJson( String eventStr )
                {
                    rootLogger .fine( eventStr );
                }

                @Override
                public void openApplication( File file )
                {
                    rootLogger .fine( "openApplication for " + file .getAbsolutePath() );
                }
            };
            
            String path = "/Users/vorth/Dropbox/vZome/attachments/2020/08-Aug/02-Scott-vZome-logo/vZomeLogo.vZome";
            shim .applicationController .doFileAction( "open", new File( path ) );

            DocumentController documentController = shim .renderDocument( path );
            documentController .doFileAction( "export.cmesh", new File( "/Users/vorth/Downloads/vZomeLogo.cmesh.json" ) );
        } catch (Throwable t) {
            t .printStackTrace();
        }
    }
}