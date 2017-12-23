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

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.vzome.core.render.RenderingChanges;
import com.vzome.desktop.controller.Controller3d;
import com.vzome.desktop.controller.RenderingViewer;

public class ControllerWebSocket implements WebSocketListener
{
	private static final Logger LOG = Log.getLogger( ControllerWebSocket.class );
    private Session outbound;
    private Controller3d docController;
	private final ObjectMapper objectMapper = new ObjectMapper();

    public void onWebSocketClose( int statusCode, String reason )
    {
        this.outbound = null;
        this .docController .setProperty( "visible", false );
        LOG.info( "WebSocket Close: {} - {}", statusCode, reason );
    }

    public void onWebSocketConnect( Session session )
    {
        this.outbound = session;
        LOG.info( "WebSocket Connect: {}", session );
        this.outbound .getRemote() .sendString( "{ \"info\": \"You are now connected to " + this.getClass().getName() + "\" }", null );
        
		String urlStr = session .getUpgradeRequest() .getQueryString();
		try {
			urlStr = URLDecoder.decode( urlStr, "UTF-8");
	        this.outbound .getRemote() .sendString( "{ \"info\": \"Opening " + urlStr + "\" }", null );
		} catch (UnsupportedEncodingException e1) {
			e1.printStackTrace();
	        this.outbound .getRemote() .sendString( e1 .getMessage(), null );
	        return;
		}

        docController = (Controller3d) APP .getSubController( urlStr );
        if ( docController != null ) {
	        this .outbound .getRemote() .sendString( "{ \"error\": \"Document already in use: " + urlStr + "\" }", null );
	        this .docController = null; // prevent action on the document
        } else {
            APP .doAction( "openURL-" + urlStr, null );
            this .docController = (Controller3d) APP .getSubController( urlStr );
            RemoteClientRendering clientRendering = new RemoteClientRendering( session );
            this .docController .attachViewer( clientRendering, clientRendering, null, "custom" );
	    		try {
	    			this .docController .doAction( "finish.load", null );
	    			this .outbound .getRemote() .sendString( "{ \"render\": \"flush\" }", null );
	    	        this .outbound .getRemote() .sendString( "{ \"info\": \"Document load SUCCESS\" }", null );
	    		} catch ( Exception e ) {
	    			e.printStackTrace();
	    	        this.outbound .getRemote() .sendString( "{ \"error\": \"Document load FAILURE\" }", null );
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
				this .docController .doAction( action, null );
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
        ServletHolder wsHolder = new ServletHolder( "vZome", new Servlet() );
        context.addServlet(wsHolder,"/vZome");

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