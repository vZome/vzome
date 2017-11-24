package com.vzome.server;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLDecoder;
import java.util.Collection;
import java.util.Objects;
import java.util.Properties;

import javax.vecmath.Matrix4d;
import javax.vecmath.Point3d;

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

import com.vzome.core.render.RenderedManifestation;
import com.vzome.core.render.RenderingChanges;
import com.vzome.desktop.controller.Controller3d;
import com.vzome.desktop.controller.RenderingViewer;

public class ControllerWebSocket implements WebSocketListener
{
	private static class Viewer implements RenderingChanges, RenderingViewer, PropertyChangeListener
	{
		private final Session session;

		public Viewer( Session session )
		{
			this .session = session;
		}

		@Override
		public void setEye( int eye ) {}

		@Override
		public void setViewTransformation( Matrix4d trans, int eye ) {}

		@Override
		public void setPerspective( double fov, double aspectRatio, double near, double far ) {}

		@Override
		public void setOrthographic( double halfEdge, double near, double far ) {}

		@Override
		public RenderedManifestation pickManifestation( MouseEvent e )
		{
			return null;
		}

		@Override
		public Collection<RenderedManifestation> pickCube()
		{
			return null;
		}

		@Override
		public void pickPoint(MouseEvent e, Point3d imagePt, Point3d eyePt) {}

		@Override
		public RenderingChanges getRenderingChanges()
		{
			return this;
		}

		@Override
		public void captureImage(int maxSize, ImageCapture capture) {}

		@Override
		public void reset() {}

		@Override
		public void manifestationAdded( RenderedManifestation manifestation )
		{
			this .session .getRemote() .sendString( "add manifestation: " + manifestation .toString(), null );
		}

		@Override
		public void manifestationRemoved(RenderedManifestation manifestation) {}

		@Override
		public void manifestationSwitched(RenderedManifestation from, RenderedManifestation to) {}

		@Override
		public void glowChanged(RenderedManifestation manifestation) {}

		@Override
		public void colorChanged(RenderedManifestation manifestation) {}

		@Override
		public void locationChanged(RenderedManifestation manifestation) {}

		@Override
		public void orientationChanged(RenderedManifestation manifestation) {}

		@Override
		public void shapeChanged(RenderedManifestation manifestation) {}

		@Override
		public void propertyChange( PropertyChangeEvent evt )
		{
			this .session .getRemote() .sendString( "property " + evt .getPropertyName() + " now: " + evt .getNewValue(), null );
		}
	}
	
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
            Viewer viewerScene = new Viewer( session );
            docController .attachViewer( viewerScene, viewerScene, null, "custom" );
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
			
			@Override
			public Component createJ3dComponent( String name )
			{
				// TODO Auto-generated method stub
				return null;
			}
			
			@Override
			public RenderingViewer createRenderingViewer( RenderingChanges scene, Component canvas )
			{
				return new RenderingViewer() {

					@Override
					public void setEye(int eye)
					{
						// TODO Auto-generated method stub
					}

					@Override
					public void setViewTransformation( Matrix4d trans, int eye )
					{
						// TODO Auto-generated method stub
					}

					@Override
					public void setPerspective( double fov, double aspectRatio, double near, double far )
					{
						// TODO Auto-generated method stub
					}

					@Override
					public void setOrthographic( double halfEdge, double near, double far )
					{
						// TODO Auto-generated method stub
					}

					@Override
					public RenderedManifestation pickManifestation( MouseEvent e )
					{
						// TODO Auto-generated method stub
						return null;
					}

					@Override
					public Collection<RenderedManifestation> pickCube()
					{
						// TODO Auto-generated method stub
						return null;
					}

					@Override
					public void pickPoint( MouseEvent e, Point3d imagePt, Point3d eyePt )
					{
						// TODO Auto-generated method stub
					}

					@Override
					public RenderingChanges getRenderingChanges()
					{
						// TODO Auto-generated method stub
						return null;
					}

					@Override
					public void captureImage( int maxSize, ImageCapture capture )
					{
						// TODO Auto-generated method stub
					}
				};
			}
			
			@Override
			public RenderingChanges createRenderingChanges( boolean isSticky, Controller controller )
			{
				return new RenderingChanges() {
					
					@Override
					public void shapeChanged( RenderedManifestation manifestation )
					{
						// TODO Auto-generated method stub
					}
					
					@Override
					public void reset()
					{
						// TODO Auto-generated method stub
					}
					
					@Override
					public void orientationChanged( RenderedManifestation manifestation )
					{
						// TODO Auto-generated method stub
					}
					
					@Override
					public void manifestationSwitched( RenderedManifestation from, RenderedManifestation to )
					{
						// TODO Auto-generated method stub
					}
					
					@Override
					public void manifestationRemoved( RenderedManifestation manifestation )
					{
    						// System .out .println( "manifestationRemoved: " + manifestation .getManifestation() .toString() );
					}
					
					@Override
					public void manifestationAdded( RenderedManifestation manifestation )
					{
	    					// System .out .println( "manifestationAdded: " + manifestation .getManifestation() .toString() );
					}
					
					@Override
					public void locationChanged( RenderedManifestation manifestation )
					{
						// TODO Auto-generated method stub
					}
					
					@Override
					public void glowChanged( RenderedManifestation manifestation )
					{
						// TODO Auto-generated method stub
					}
					
					@Override
					public void colorChanged( RenderedManifestation manifestation )
					{
						// TODO Auto-generated method stub
					}
				};
			}
		} );
		APP .setErrorChannel( new Controller.ErrorChannel() {
			
			@Override
			public void reportError(String errorCode, Object[] arguments)
			{
				System .out .println( errorCode );
			}
			
			@Override
			public void clearError()
			{
				// TODO Auto-generated method stub
			}
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