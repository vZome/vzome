package com.vzome.server;

import java.net.URL;
import java.util.Objects;

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

public class ControllerWebSocket implements WebSocketListener
{
    private static final Logger LOG = Log.getLogger( ControllerWebSocket.class );
    private Session outbound;

    public void onWebSocketClose( int statusCode, String reason )
    {
        this.outbound = null;
        LOG.info( "WebSocket Close: {} - {}", statusCode, reason );
    }

    public void onWebSocketConnect( Session session )
    {
        this.outbound = session;
        LOG.info( "WebSocket Connect: {}", session );
        this.outbound .getRemote() .sendString( "You are now connected to " + this.getClass().getName(), null );
    }

    public void onWebSocketError( Throwable cause )
    {
        LOG.warn( "WebSocket Error", cause );
    }

    public void onWebSocketText( String message )
    {
        if ((outbound != null) && (outbound.isOpen()))
        {
            LOG.info( "Echoing back text message [{}]", message );
            outbound .getRemote() .sendString( message, null );
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
    
    public static void main(String[] args)
    {
        Server server = new Server( 8532 );

        ServletContextHandler context = new ServletContextHandler( ServletContextHandler.SESSIONS );
        context.setContextPath("/");
        server.setHandler(context);

        // Add websocket servlet
        ServletHolder wsHolder = new ServletHolder( "echo",new Servlet() );
        context.addServlet(wsHolder,"/echo");

        // Add default servlet (to serve the html/css/js)
        // Figure out where the static files are stored.
        URL urlStatics = Thread.currentThread().getContextClassLoader().getResource( "index.html" );
        Objects.requireNonNull( urlStatics, "Unable to find index.html in classpath" );
        String urlBase = urlStatics .toExternalForm( ).replaceFirst( "/[^/]*$","/" );
        ServletHolder defHolder = new ServletHolder("default",new DefaultServlet());
        defHolder.setInitParameter("resourceBase",urlBase);
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