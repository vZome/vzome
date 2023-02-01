package com.vzome.server;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLDecoder;
import java.util.Objects;
import java.util.Properties;
import java.util.StringTokenizer;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.DefaultServlet;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.eclipse.jetty.util.log.Log;
import org.eclipse.jetty.util.log.Logger;
import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.WebSocketListener;
import org.eclipse.jetty.websocket.servlet.WebSocketServlet;
import org.eclipse.jetty.websocket.servlet.WebSocketServletFactory;
import org.vorthmann.j3d.J3dComponentFactory;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.vzome.core.render.Scene;
import com.vzome.desktop.api.Controller;
import com.vzome.desktop.awt.ApplicationController;
import com.vzome.desktop.awt.DocumentController;
import com.vzome.desktop.awt.RenderingViewer;
import com.vzome.desktop.controller.JsonClientRendering;

public class ControllerWebSocket implements WebSocketListener, JsonClientRendering.EventDispatcher
{
    private static final Logger LOG = Log.getLogger( ControllerWebSocket.class );
    private Session outbound;
    private final ThrottledQueue queue = new ThrottledQueue( 200, 1000 ); // 40 gets/second
    private DocumentController docController;
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final ObjectWriter objectWriter = objectMapper .writer();

    @Override
    public void onWebSocketClose( int statusCode, String reason )
    {
        this.outbound = null;
        if ( this .docController != null )
            this .docController .setProperty( "visible", false );
        LOG.info( "WebSocket Close: {} - {}", statusCode, reason );
    }

    private void publish( JsonNode node )
    {
        try {
            this .queue .add( this .objectWriter .writeValueAsString( node ) );
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void dispatchEvent( String type, JsonNode payload )
    {
        ObjectNode event = this .objectMapper .createObjectNode();
        event .put( "type", type );
        event .set( "payload", payload );
        publish( event );
    }

    private void dispatchEvent( String type, String payload )
    {
        ObjectNode event = this .objectMapper .createObjectNode();
        event .put( "type", type );
        event .put( "payload", payload );
        publish( event );
    }

    @Override
    public void onWebSocketConnect( Session session )
    {
        this.outbound = session;

        Thread consumer = new Thread( new Runnable() {
            @Override
            public void run() {
                while (true)
                    if ( ! queue .isEmpty() ) {
                        String msg = (String) queue .get();
                        session .getRemote() .sendString( msg, null );
                        LOG.info( "WebSocket Send: ", msg );
                    }
            }
        } );
        LOG.info( "WebSocket Connect: {}", session );
        dispatchEvent( "info", "You are now connected to " + this.getClass().getName() );

        String urlStr = session .getUpgradeRequest() .getQueryString();
        try {
            urlStr = URLDecoder.decode( urlStr, "UTF-8");
            dispatchEvent( "info", "Opening " + urlStr );
        } catch (UnsupportedEncodingException e1) {
            e1.printStackTrace();
            dispatchEvent( "LOAD_FAILED", e1 .getMessage() );
            return;
        }
        
        // NOTE: THIS CODE IS NOW UNTESTED.  I have attempted alignment with the
        //  new approach in the client app, at least for event dispatch, but the client
        //  app is no longer using websockets to talk to a server.

        docController = (DocumentController) APP .getSubController( urlStr );
        if ( docController != null ) {
            dispatchEvent( "LOAD_FAILED", "Document already in use: " + urlStr );
            this .docController = null; // prevent action on the document
        } else {
            APP .doAction( "openURL-" + urlStr );
            this .docController = (DocumentController) APP .getSubController( urlStr );
            if ( this .docController == null ) {
                dispatchEvent( "LOAD_FAILED", "Document load FAILURE: " + urlStr );
                return;
            }
            String bkgdColor = docController .getProperty( "backgroundColor" );
            if ( bkgdColor != null ) {
                dispatchEvent( "BACKGROUND_SET", bkgdColor );
            }
            consumer.start();
            JsonClientRendering clientRendering = new JsonClientRendering( this, false );
            this .docController .getModel() .getRenderedModel() .addListener( clientRendering );
            try {
                this .docController .actionPerformed( this, "finish.load" );
                dispatchEvent( "MODEL_LOADED", "" );
            } catch ( Exception e ) {
                e.printStackTrace();
                dispatchEvent( "LOAD_FAILED", "Document load unknown FAILURE}" );
            }
        }
    }

    @Override
    public void onWebSocketError( Throwable cause )
    {
        LOG.warn( "WebSocket Error", cause );
        cause .printStackTrace();
    }

    @Override
    public void onWebSocketText( String message )
    {
        if ((outbound != null) && (outbound.isOpen()))
        {
            LOG.info( "Action from client: [{}]", message );
            try {
                JsonNode json = this .objectMapper .readValue( message, JsonNode.class);
                String action = json .get( "action" ) .asText();

                Controller controller = this .docController;

                StringTokenizer tokens = new StringTokenizer( action, "/" );
                int num = tokens .countTokens();
                for ( int i = 0; i < num-1; i++)
                {
                    String subName = tokens .nextToken();
                    controller = controller .getSubController( subName );
                }
                action = tokens .nextToken();

                controller .actionPerformed( this, action );
            } catch (Throwable e) {
                LOG.warn( "doAction error: [{}]", e .getMessage() );
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onWebSocketBinary( byte[] arg0, int arg1, int arg2 )
    {
        /* ignore */
    }


    @SuppressWarnings("serial")
    public static class Servlet extends WebSocketServlet
    {
        @Override
        public void configure(WebSocketServletFactory factory)
        {
            factory.register( ControllerWebSocket.class );
        }
    }

    private static ApplicationController APP;

    public static void main( String[] args )
    {    		
        Properties props = new Properties();
        props .setProperty( "entitlement.model.edit", "true" );
        props .setProperty( "keep.alive", "true" );

        APP = new ApplicationController( new ApplicationController.UI()
        {	
            @Override
            public void doAction( String action )
            {
                System .out .println( "UI event: " + action );
            }

            @Override
            public void runScript( String script, File file )
            {}

            @Override
            public void openApplication( File file )
            {}
        }, props, new J3dComponentFactory()
        {
            @Override
            public RenderingViewer createRenderingViewer( Scene scene, boolean lightweight )
            {
                // Should never be called
                return null;
            }
        });
        APP .setErrorChannel( new Controller.ErrorChannel() {

            @Override
            public void reportError(String errorCode, Object[] arguments)
            {
                System .out .println( errorCode );
            }

            @Override
            public void clearError() {}
        });

        String portStr = System.getenv( "PORT" );
        int port = ( portStr == null )? 8532 : Integer .parseInt( portStr );
        Server server = new Server( port );

        ServletContextHandler context = new ServletContextHandler( ServletContextHandler.SESSIONS );
        context.setContextPath("/");
        server.setHandler(context);

        // Add websocket servlet
        ServletHolder wsHolder = new ServletHolder( "vZome", new Servlet() );
        context.addServlet(wsHolder,"/vZome");

        String pwdPath = System.getProperty("user.dir");
        System.out.println( "Working directory is " + pwdPath );

        // Add default servlet (to serve the html/css/js)

        String resourceBase = System.getenv( "CLIENT_BUILD" );
        if ( resourceBase == null || resourceBase .isEmpty() ) {
            System.out.println( "No value provided for CLIENT_BUILD" );
            // Figure out where the static files are stored.
            URL urlStatics = Thread.currentThread().getContextClassLoader().getResource( "index.html" );
            Objects.requireNonNull( urlStatics, "Unable to find index.html in classpath" );
            resourceBase = urlStatics .toExternalForm( ).replaceFirst( "/[^/]*$","/" );
        }

        ServletHolder defHolder = new ServletHolder("default",new DefaultServlet());
        defHolder.setInitParameter( "resourceBase",resourceBase );
        defHolder.setInitParameter("dirAllowed","true");
        context.addServlet(defHolder,"/");

        // add exporter servlet
        
        context.addServlet( new ServletHolder( new ExporterServlet() ), "/exporter/*" );

        try
        {
            server.start();
            server.join();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

}