package com.vzome.server;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
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
import org.vorthmann.ui.Controller;
import org.vorthmann.zome.app.impl.ApplicationController;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.vzome.core.render.Color;
import com.vzome.desktop.controller.Controller3d;

public class ControllerWebSocket implements WebSocketListener
{
    private static final Logger LOG = Log.getLogger( ControllerWebSocket.class );
    private Session outbound;
    private final ThrottledQueue queue = new ThrottledQueue( 30, 1000 ); // 40 gets/second
    private Controller3d docController;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public void onWebSocketClose( int statusCode, String reason )
    {
        this.outbound = null;
        this .docController .setProperty( "visible", false );
        LOG.info( "WebSocket Close: {} - {}", statusCode, reason );
    }

    private void publish( String message )
    {
        this .queue .add( message );
    }

    public void onWebSocketConnect( Session session )
    {
        this.outbound = session;

        Thread consumer = new Thread( new Runnable() {
            @Override
            public void run() {
                while (true)
                    if ( ! queue .isEmpty()) {
                        session .getRemote() .sendString( (String) queue .get(), null );
                    }
            }
        } );
        LOG.info( "WebSocket Connect: {}", session );
        publish( "{ \"info\": \"You are now connected to " + this.getClass().getName() + "\" }" );

        String urlStr = session .getUpgradeRequest() .getQueryString();
        try {
            urlStr = URLDecoder.decode( urlStr, "UTF-8");
            publish( "{ \"info\": \"Opening " + urlStr + "\" }" );
        } catch (UnsupportedEncodingException e1) {
            e1.printStackTrace();
            publish( e1 .getMessage() );
            return;
        }

        docController = (Controller3d) APP .getSubController( urlStr );
        if ( docController != null ) {
            publish( "{ \"error\": \"Document already in use: " + urlStr + "\" }" );
            this .docController = null; // prevent action on the document
        } else {
            APP .doAction( "openURL-" + urlStr, null );
            this .docController = (Controller3d) APP .getSubController( urlStr );
            String bkgdColor = docController .getProperty( "backgroundColor" );
            if ( bkgdColor != null ) {
                int rgb =  Integer .parseInt( bkgdColor, 16 );
                String color = new Color( rgb ) .toWebString();
                publish( "{ \"render\": \"background\", \"color\": \"" + color + "\" }" );
            }
            consumer.start();
            RemoteClientRendering clientRendering = new RemoteClientRendering( queue );
            this .docController .attachViewer( clientRendering, clientRendering, null );
            try {
                this .docController .doAction( "finish.load", null );
                publish( "{ \"render\": \"flush\" }" );
                publish( "{ \"info\": \"Document load SUCCESS\" }" );
            } catch ( Exception e ) {
                e.printStackTrace();
                publish( "{ \"error\": \"Document load FAILURE\" }" );
            }
        }
    }

    public void onWebSocketError( Throwable cause )
    {
        LOG.warn( "WebSocket Error", cause );
        cause .printStackTrace();
    }

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

                controller .doAction( action, null );
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

        APP = new ApplicationController( new ActionListener()
        {	
            @Override
            public void actionPerformed( ActionEvent e )
            {
                System .out .println( "UI event: " + e .toString() );
            }
        }, props, new J3dComponentFactory()
        {
            @Override
            public Component createRenderingComponent( boolean isSticky,
                    boolean isOffScreen, Controller3d controller )
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