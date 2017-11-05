package com.vzome.server;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Collection;
import java.util.Properties;
import java.util.StringTokenizer;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.vecmath.Matrix4d;
import javax.vecmath.Point3d;

import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.AbstractHandler;
import org.vorthmann.ui.Controller;
import org.vorthmann.zome.app.impl.ApplicationController;

import com.vzome.core.render.RenderedManifestation;
import com.vzome.core.render.RenderingChanges;
import com.vzome.core.viewing.Lights;
import com.vzome.desktop.controller.RenderingViewer;

public class ControllerServer extends AbstractHandler
{
    private final Controller docController;

	public ControllerServer( Controller docController )
    {
		this .docController = docController;
	}

	@Override
    public void handle( String target,
                        Request baseRequest,
                        HttpServletRequest request,
                        HttpServletResponse response ) throws IOException,
                                                      ServletException
    {
        // Declare response encoding and types
        response.setContentType("text/html; charset=utf-8");

        PrintWriter out = response.getWriter();
        
        Controller controller = this .docController;
        
        StringTokenizer tokens = new StringTokenizer( target, "/" );
        int num = tokens .countTokens();
        for ( int i = 0; i < num-1; i++)
        {
			String subName = tokens .nextToken();
			controller = controller .getSubController( subName );
		}
        
        String action = tokens .nextToken();
        if ( "favicon.ico" .equals( action ) ) {
    			response.setStatus( HttpServletResponse.SC_NOT_FOUND );
		}
        else {
	        	try {
	        		controller .actionPerformed( new ActionEvent( this, 0, action ) );
	        		out.println("<h1>" + action + "</h1>");
	        		response.setStatus( HttpServletResponse.SC_OK );
	        	} catch (Throwable e) {
	        		out.println("<h1>FAILURE</h1>");
	        		e.printStackTrace();
	        		response.setStatus( HttpServletResponse.SC_BAD_REQUEST );
	        	}
        }
        baseRequest.setHandled(true);
    }

    public static void main( String[] args ) throws Exception
    {
    		System.setProperty( "java.awt.headless", "true" );

    		String urlStr = "http://vzome.com/models/2007/07-Jul/affine120-bop/purpleBlueOrange-affine120cell.vZome";
    		if ( args.length > 0 )
    			urlStr = args[ 0 ];
    		
    		Properties props = new Properties();
    		props .setProperty( "entitlement.model.edit", "true" );
    		
    		ApplicationController app = new ApplicationController( new ActionListener()
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
						}};
				}
				
				@Override
				public RenderingChanges createRenderingChanges( Lights lights, boolean isSticky, Controller controller )
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
	    						System .out .println( "manifestationRemoved: " + manifestation .getManifestation() .toString() );
						}
						
						@Override
						public void manifestationAdded( RenderedManifestation manifestation )
						{
		    					System .out .println( "manifestationAdded: " + manifestation .getManifestation() .toString() );
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
			}
    		);
    		app .setErrorChannel( new Controller.ErrorChannel() {
    			
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

    		app .doAction( "openURL-" + urlStr, null );
    		Controller docController = app .getSubController( urlStr );
    		docController .doAction( "finish.load", null );
    	
    		Server server = new Server( 8532 );
        server.setHandler( new ControllerServer( docController ) );

        server.start();
//        server.join();
    }
}
