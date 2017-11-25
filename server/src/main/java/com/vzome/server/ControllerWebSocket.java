package com.vzome.server;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLDecoder;
import java.util.Objects;
import java.util.Properties;

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
import org.vorthmann.ui.Controller;
import org.vorthmann.zome.app.impl.ApplicationController;

import com.vzome.core.render.RenderingChanges;
import com.vzome.desktop.controller.Controller3d;
import com.vzome.desktop.controller.RenderingViewer;

public class ControllerWebSocket implements WebSocketListener
{
	private static final Logger LOG = Log.getLogger( ControllerWebSocket.class );
    private Session outbound;
    private Controller3d docController;

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
        
		String urlStr = session .getUpgradeRequest() .getQueryString();
		try {
			urlStr = URLDecoder.decode( urlStr, "UTF-8");
	        this.outbound .getRemote() .sendString( "Opening " + urlStr, null );
		} catch (UnsupportedEncodingException e1) {
			e1.printStackTrace();
	        this.outbound .getRemote() .sendString( e1 .getMessage(), null );
	        return;
		}

        docController = (Controller3d) APP .getSubController( urlStr );
        if ( docController != null ) {
	        this.outbound .getRemote() .sendString( "Document already in use: " + urlStr, null );
	        docController = null; // prevent action on the document
        } else {
            APP .doAction( "openURL-" + urlStr, null );
            docController = (Controller3d) APP .getSubController( urlStr );
            RemoteClientRendering clientRendering = new RemoteClientRendering( session );
            docController .attachViewer( clientRendering, clientRendering, null, "custom" );
	    		try {
	    			docController .doAction( "finish.load", null );
	    	        this.outbound .getRemote() .sendString( "Document load SUCCESS", null );
	    		} catch ( Exception e ) {
	    			e.printStackTrace();
	    	        this.outbound .getRemote() .sendString( "Document load FAILURE", null );
	    		}
        }
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
    
    private static ApplicationController APP;
    
    public static void main( String[] args )
    {    		
		Properties props = new Properties();
		props .setProperty( "entitlement.model.edit", "true" );

		APP = new ApplicationController( new ActionListener()
		{	
			@Override
			public void actionPerformed( ActionEvent e )
			{
				System .out .println( "UI event: " + e .toString() );
			}
		}, props, new RenderingViewer.Factory() {
			
			// These will never be called.  We need this just to avoid the default Java3dFactory construction.
			
			@Override
			public Component createJ3dComponent( String name )
			{
				return null;
			}
			
			@Override
			public RenderingViewer createRenderingViewer( RenderingChanges scene, Component canvas )
			{
				return null;
			}
			
			@Override
			public RenderingChanges createRenderingChanges( boolean isSticky, Controller controller )
			{
				return null;
			}
		} );
		APP .setErrorChannel( new Controller.ErrorChannel() {
			
			@Override
			public void reportError(String errorCode, Object[] arguments)
			{
				System .out .println( errorCode );
			}
			
			@Override
			public void clearError() {}
		});

        Server server = new Server( 8532 );

        ServletContextHandler context = new ServletContextHandler( ServletContextHandler.SESSIONS );
        context.setContextPath("/");
        server.setHandler(context);

        // Add websocket servlet
        ServletHolder wsHolder = new ServletHolder( "echo", new Servlet() );
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