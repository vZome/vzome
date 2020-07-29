package com.vzome.cheerpj;

import java.io.File;
import java.util.Properties;

import org.vorthmann.j3d.J3dComponentFactory;
import org.vorthmann.ui.Controller;
import org.vorthmann.zome.app.impl.ApplicationController;
import org.vorthmann.zome.app.impl.DocumentController;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.leaningtech.client.Global;
import com.vzome.core.render.Scene;
import com.vzome.desktop.controller.RemoteClientRendering;
import com.vzome.desktop.controller.RenderingViewer;

public class RemoteClientShim implements RemoteClientRendering.EventDispatcher
{
    private static RemoteClientShim SHIM;

    private final ApplicationController applicationController;
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final ObjectWriter objectWriter = objectMapper .writer();

    public RemoteClientShim()
    {
        Properties props = new Properties();
        props .setProperty( "entitlement.model.edit", "true" );
        props .setProperty( "keep.alive", "true" );

        this .applicationController = new ApplicationController( new ApplicationController.UI()
        {   
            @Override
            public void doAction( String action )
            {
                System .err .println( "WARNING: Unhandled UI event: " + action );
            }
        }, props, new J3dComponentFactory()
        {
            @Override
            public RenderingViewer createRenderingViewer( Scene scene )
            {
                System .err .println( "WARNING: createRenderingViewer called" );
                return null;
            }
        });
        this .applicationController .setErrorChannel( new Controller.ErrorChannel() {

            @Override
            public void reportError(String errorCode, Object[] arguments)
            {
                System .err .println( errorCode );
            }

            @Override
            public void clearError() {}
        });
    }
        
    private void sendToJavascript( JsonNode event )
    {
        try {
            String eventStr = this .objectWriter .writeValueAsString( event );
            Global .jsCallS( "dispatchToRedux", Global.JSString( eventStr ) );
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
    }

    public void dispatchEvent( String type, JsonNode payload )
    {
        ObjectNode event = this .objectMapper .createObjectNode();
        event .put( "type", type );
        event .set( "payload", payload );
        sendToJavascript( event );
    }

    private void dispatchEvent( String type, String payload )
    {
        ObjectNode event = this .objectMapper .createObjectNode();
        event .put( "type", type );
        event .put( "payload", payload );
        sendToJavascript( event );
    }
    
    private void renderDocument( String path )
    {
        DocumentController docController = (DocumentController) SHIM .applicationController .getSubController( path );
        if ( docController == null ) {
            dispatchEvent( "LOAD_FAILED", "Document load FAILURE: " + path );
            return;
        }
        String bkgdColor = docController .getProperty( "backgroundColor" );
        if ( bkgdColor != null ) {
            dispatchEvent( "BACKGROUND_SET", bkgdColor );
        }
        // TODO: define a callback to support the ControllerWebSocket case?
//        consumer.start();
        RemoteClientRendering clientRendering = new RemoteClientRendering( this );
        docController .getModel() .getRenderedModel() .addListener( clientRendering );
        try {
            docController .actionPerformed( SHIM, "finish.load" );
            dispatchEvent( "MODEL_LOADED", "" );
        } catch ( Exception e ) {
            e.printStackTrace();
            dispatchEvent( "LOAD_FAILED", "Document load unknown FAILURE" );
        }
    }
    
    // %%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
    // %%%%%%  These are the entry points.  Always catch Throwable!
    // %%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%

    public static void main( String[] args )
    {
        try {
            SHIM = new RemoteClientShim();
            System .out .println( "vZome application initialized" );
//            openFile( "/Users/vorth/Downloads/greenTetra.vZome" );
        } catch (Throwable t) {
            t .printStackTrace();
        }
    }

    public static void openFile( String path )
    {
        Thread thread = Thread .currentThread();
        if ( thread == null ) {
            System.err.println( "currentThread is null!" );
            return;
        }
        try {
            SHIM .applicationController .doFileAction( "open", new File( path ) );
            SHIM .renderDocument( path );
        } catch (Throwable t) {
            t .printStackTrace();
        }
    }
}
