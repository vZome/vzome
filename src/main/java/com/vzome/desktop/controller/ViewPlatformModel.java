/*
 * Created on Jun 30, 2003
 *
 * To change the template for this generated file go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
package com.vzome.desktop.controller;

import java.awt.event.ActionEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.vecmath.Matrix4d;
//import javax.media.j3d.Transform3D;
import javax.vecmath.Point3d;
import javax.vecmath.Quat4d;
import javax.vecmath.Vector3d;
import javax.vecmath.Vector3f;

import org.vorthmann.j3d.MouseTool;
import org.vorthmann.j3d.MouseToolDefault;
import org.vorthmann.j3d.Trackball;
import org.vorthmann.ui.DefaultController;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.vzome.core.math.DomUtils;
import com.vzome.core.viewing.ViewModel;

/**
 * In this view model, the frustum shape is generally held constant
 * as other parameters are varied.
 */
public class ViewPlatformModel extends DefaultController
{
	/**
	 * The original frustum.
	 */
	public static final double ORIG_WIDTH = 18f, ORIG_DISTANCE = 40f;
	    
    public static final double DEFAULT_STEREO_ANGLE = Math .PI * 5d / 360d;

	protected static final Vector3f ORIG_LOOK = new Vector3f(0,0,-1);
	
	protected static final Vector3f ORIG_UP = new Vector3f(0,1,0);
	
	private ViewModel mParameters = new ViewModel();

    private final Map bookmarks = new HashMap();
    
    private ViewModel copied = null;
    
	protected final List mViewers = new ArrayList();
	
	private final ViewModel initialView;

    public interface Snapper
    {
        void snapDirections( Vector3f lookDir, Vector3f upDir );
    }
    
	public static interface Viewer 
	{
	    int MONOCULAR = 0; int LEFT_EYE = 1; int RIGHT_EYE = 2;

	    void setEye( int eye );
	    
	    void setViewTransformation( Matrix4d trans, int eye );

	    void setPerspective( double fov, double aspectRatio, double near, double far );

	    void setOrthographic( double halfEdge, double near, double far );
	}

	public boolean isPerspective()
	{
		return mParameters .isPerspective();
	}
	
	/**
	 * The width of the frustum at the look-at point is held
	 * constant, as well as the other dimensions of the frustum.
	 * @param value
	 */
	public void setPerspective( boolean value )
	{
		mParameters .setPerspective( value );
		adjustView( mParameters );
		adjustProjection( mParameters );
	}
    
    public void setStereoAngle( double value )
    {
//        mStereoAngle = value;        
    }
	
	public float getFieldOfView()
	{
		return mParameters .getFieldOfView();
	}

	public float getViewDistance()
	{
		return mParameters .getViewDistance();
	}
	
	public void getViewOrientation( Vector3d lookDir, Vector3d upDir )
	{
		mParameters .getViewOrientation( lookDir, upDir );
	}
    
    
    public void addViewer( ViewPlatformModel.Viewer viewer )
    {
        mViewers .add( viewer );
    }
    
    public void removeViewer( ViewPlatformModel.Viewer viewer )
    {
        mViewers .remove( viewer );
    }
    
    
    public ViewPlatformModel()
    {
        resetViewDirection();
        initialView = new ViewModel( mParameters );
    }
    
    
    public void init()
    {
		adjustView( mParameters );
		adjustProjection( mParameters );
	}
    
    public void bookmarkView( String name )
    {
        bookmarks .put( name, new ViewModel( mParameters ) );
    }
    
    public ViewModel getView()
    {
        return new ViewModel( mParameters );
    }

    protected ViewModel restoreView( String name )
    {
        return restoreView( (ViewModel) bookmarks .get( name ) );
    }

    public ViewModel restoreView( ViewModel view )
    {
        if ( view == null )
            return mParameters;
        boolean wasPerspective = mParameters .isPerspective();
        boolean wasStereo = mParameters .isStereo();
        float oldMag = getMagnification();
        mParameters = new ViewModel( view );
        adjustView( mParameters );
        adjustProjection( mParameters );

        if ( wasPerspective != mParameters .isPerspective() )
            properties() .firePropertyChange( "perspective", wasPerspective, mParameters .isPerspective() );
        if ( wasStereo != mParameters .isStereo() )
            properties() .firePropertyChange( "stereo", wasStereo, mParameters .isStereo() );
        if ( oldMag != getMagnification() )
            properties() .firePropertyChange( "magnification", Float .toString( oldMag ), Float .toString(  getMagnification() ) );
        
        return mParameters;
    }

	private void adjustView( ViewModel model )
	{
        if ( mViewers .size() == 0 )
            return;
		Matrix4d trans = new Matrix4d();
		
        model .getViewTransform( trans, 0d );
        trans .invert();
        for ( int i = 0; i < mViewers .size(); i++ )
            ((ViewPlatformModel.Viewer) mViewers .get( i )) .setViewTransformation( trans, Viewer .MONOCULAR );
		
		model .getStereoViewTransform( trans, Viewer .LEFT_EYE );
        trans .invert();
        for ( int i = 0; i < mViewers .size(); i++ )
            ((ViewPlatformModel.Viewer) mViewers .get( i )) .setViewTransformation( trans, Viewer .LEFT_EYE );
        
        model .getStereoViewTransform( trans, Viewer .RIGHT_EYE );
        trans .invert();
        for ( int i = 0; i < mViewers .size(); i++ )
            ((ViewPlatformModel.Viewer) mViewers .get( i )) .setViewTransformation( trans, Viewer .RIGHT_EYE );
	}
	
	private void adjustProjection( ViewModel model )
	{
        if ( mViewers .size() == 0 )
            return;
        double near = model .getNearClipDistance();
        double far = model .getFarClipDistance();
		if ( ! model .isPerspective() ) {
			double edge = model .getWidth() / 2;
            for ( int i = 0; i < mViewers .size(); i++ )
                ((ViewPlatformModel.Viewer) mViewers .get( i )) .setOrthographic( edge, near, far );
		}
		else {
			double field = model .getFieldOfView();
            for ( int i = 0; i < mViewers .size(); i++ )
                ((ViewPlatformModel.Viewer) mViewers .get( i )) .setPerspective( field, 1.0d, near, far );
		}

        // TODO - make aspect ratio track the screen window shape
	}

	
	public void getWorldRotation( Quat4d q )
	{
		Vector3d axis = new Vector3d( q.x, q.y, q.z );

		Matrix4d viewTrans = new Matrix4d();
		mParameters .getViewTransform( viewTrans, 0d );
        viewTrans .invert();

		// now map the axis back to world coordinates
		viewTrans .transform( axis );
		q.x = axis.x; q.y = axis.y; q.z = axis.z;
	}
	
	
	public void mapViewToWorld( Vector3f vector )
	{
		Matrix4d viewTrans = new Matrix4d();
		mParameters .getViewTransform( viewTrans, 0d );
        viewTrans .invert();
		viewTrans .transform( vector );
	}
	

	public Point3d getLookAtPoint()
	{
		return mParameters .getLookAtPoint();
	}


    public void setViewDirection( Vector3f lookDir )
    {
    	mParameters .setViewDirection( lookDir );
        adjustView( mParameters );
    }


    public void setViewDirection( Vector3f lookDir, Vector3f upDir )
    {
        mParameters .setViewDirection( lookDir, upDir );
        adjustView( mParameters );
    }
	
	
	public void setLookAtPoint( Point3d lookAt )
	{
		mParameters .setLookAtPoint( lookAt );
        adjustView( mParameters );
	}


    public void resetViewDirection()
    {
        mParameters .setViewDirection( ORIG_LOOK, ORIG_UP );
    	adjustView( mParameters );
    }
    

	public void addViewpointRotation( Quat4d rotation )
	{
		mParameters .addViewpointRotation( rotation );
    	adjustView( mParameters );
	}

	
	/**
	 * All view parameters will scale with distance, to keep the frustum
	 * shape fixed.
	 * @param distance
	 */
	public void setViewpointDistance( float distance )
	{
		mParameters .setViewpointDistance( distance );
	
		// have to adjust the projection, since the clipping distances
		//   adjust with distance
		adjustView( mParameters );
		adjustProjection( mParameters );
	}

	public float getMagnification()
	{
		return (float) Math .log( getViewDistance() / ORIG_DISTANCE );
	}
	
	public void setMagnification( float exp )
	{
		setViewpointDistance( (float) ( ORIG_DISTANCE * Math .pow( Math.E, exp ) ) );
	}

    
    private final LinkedList recentViews = new LinkedList();
    
    private ViewModel baselineView = mParameters;  // invariant: baselineView .equals( mParameters) whenever the view
                                                    // is "at rest" (not rolling or zooming), AND baselineView equals the latest recentView
    private final static int MAX_RECENT = 20;
    
    private int currentRecentView = 0;
    
    private boolean saveBaselineView()
    {
        if ( mParameters .equals( baselineView ) )
            return false;
        baselineView = new ViewModel( mParameters );
        recentViews .add( baselineView );
        if ( recentViews .size() > MAX_RECENT )
            recentViews .removeFirst();
        currentRecentView = recentViews .size();
        return true;
    }
    
    private static final Vector3f Z = new Vector3f( 0f, 0f, -1f ), Y = new Vector3f( 0f, 1f, 0f );
    
    private Snapper mSnapper = null;
    
    private boolean mSnapping = false;

    public void setSnapper( Snapper snapper )
    {
    	mSnapper = snapper;
        if ( mSnapping ) {
            saveBaselineView(); // might have been zooming
            snapView();
            saveBaselineView();
        }
    }
    
    public boolean isSnapping()
    {
    	return mSnapping;
    }
    
	public void snapView()
	{
		Z .set( 0f, 0f, -1f );
		mapViewToWorld( Z );
		Y .set( 0f, 1f, 0f );
		mapViewToWorld( Y );

		mSnapper .snapDirections( Z, Y );

		setViewDirection( Z, Y );
	}

    public void doAction( String action, ActionEvent e ) throws Exception
    {
        if ( action .equals( "toggleSnap" ) )
        {
            mSnapping = !mSnapping;
            if ( mSnapping )
            {
                saveBaselineView(); // might have been zooming
                snapView();
                saveBaselineView();
            }
        }
        else if ( action .equals( "toggleStereo" ) )
        {
            boolean wasStereo = mParameters .isStereo();
            if ( ! wasStereo )
                mParameters .setStereoAngle( ViewPlatformModel.DEFAULT_STEREO_ANGLE );
            else
                mParameters .setStereoAngle( 0d );
            adjustView( mParameters );
            adjustProjection( mParameters );
            properties() .firePropertyChange( "stereo", wasStereo, !wasStereo );
        }
        else if ( action .equals( "togglePerspective" ) )
        {
            saveBaselineView(); // might have been zooming
            mParameters .setPerspective( ! mParameters .isPerspective() );
            adjustView( mParameters );
            adjustProjection( mParameters );
            saveBaselineView();
        }
        else if ( action .equals( "goForward" ) )
        {
            if ( currentRecentView >= recentViews .size() )
                return;
            restoreView( (ViewModel) recentViews .get( ++currentRecentView ) );
        }
        else if ( action .equals( "goBack" ) )
        {
            if ( currentRecentView == 0 )
                return;
            boolean wasZooming = saveBaselineView(); // might have been zooming
            if ( ( currentRecentView == recentViews .size() ) && wasZooming ) // we're not browsing recent views
                --currentRecentView; //    skip over the view we just saved
            restoreView( (ViewModel) recentViews .get( --currentRecentView ) );
        }
        else if ( action .startsWith( "saveView." ) )
        {
            bookmarkView( action .substring( "saveView." .length() ) );
        }
        else if ( action .startsWith( "restoreView." ) )
        {
            saveBaselineView(); // might have been zooming
            String name = action .substring( "restoreView." .length() );
            restoreView( name );
            // bookmarked views are not "special"... they are stored in recent, too
            saveBaselineView();
        }
        else if ( action .equals( "initialView" ) )
        {
            saveBaselineView(); // might have been zooming
            restoreView( this .initialView );
            // bookmarked views are not "special"... they are stored in recent, too
            saveBaselineView();
        }
    }
    
    public MouseTool getTrackball()
    {
        return new Trackball()
        {
            public void mousePressed( MouseEvent e )
            {
                saveBaselineView(); // might have been zooming
                super .mousePressed( e );
            }
            
            public void trackballRolled( Quat4d roll )
            {
                Quat4d copy = new Quat4d( roll );
                
                getWorldRotation( copy );
                
                addViewpointRotation( copy );
                
                // TODO give will-snap feedback when drag paused
            }
            
            public void mouseReleased( MouseEvent e )
            {
                if ( mSnapping )
                    snapView();
                saveBaselineView();
            }
        };
    }
    
    // ticks <-> mag mapping is duplicated in ViewPlatformControlPanel... should live here only
    
    private static final float MAG_PER_TICKS = -50f; // MAX_MAG = 4f, MIN_MAG = -2f;
    
    private static int magToTicks( float magnification )
    {
        return Math.round( MAG_PER_TICKS * ( magnification - 1f ) );
    }
    
    private static float ticksToMag( int ticks )
    {
        return ( ticks / MAG_PER_TICKS ) + 1f;
    }

    public MouseTool getZoomScroller()
    {
        return new MouseToolDefault()
        {
            public void mouseWheelMoved( MouseWheelEvent e )
            {
                int amt = e .getWheelRotation();
                float oldMag = getMagnification();
                int ticks = magToTicks( oldMag );
                ticks -= amt;
                float newMag = ticksToMag( ticks );
                setMagnification( newMag );
                properties() .firePropertyChange( "magnification", Float .toString( oldMag ), Float .toString( newMag ) );
            }
        };
    }

    public String getProperty( String propName )
    {
		if ( "magnification" .equals( propName ) )
			return Float .toString(  getMagnification() );
		if ( "perspective" .equals( propName ) )
			return Boolean .toString( isPerspective() );
		if ( "snap" .equals( propName ) )
			return Boolean .toString( isSnapping() );
		if ( "stereo" .equals( propName ) )
			return Boolean .toString( mParameters .isStereo() );
        return super .getProperty( propName );
    }
    
    private final static long ZOOM_PAUSE = 3000; // three seconds
    
    private long lastZoom = 0;
    
    
    public void setProperty( String propName, Object value )
    {
    	if ( "magnification" .equals( propName ) )
        {
            long now = System .currentTimeMillis();
            if ( now - lastZoom > ZOOM_PAUSE ) {
                // it has been a while... save the last view
                saveBaselineView();
            }
			setMagnification( Float .parseFloat( (String) value ) );
            lastZoom = now;
        }
    }

    public String[] getCommandList( String listName )
    {
        if ( listName .equals( "views" ) )
        {
            String[] result = new String[ bookmarks .size() ];
            int i = 0;
            for ( Iterator it = bookmarks .keySet() .iterator(); it .hasNext(); i++ )
                result[ i ] = (String) it .next();
            return result;
        }
        return new String[0];
    }

    public Element getXml( Document doc )
    {
        Element result = doc .createElement( "Viewing" );
        Element modelXml = mParameters .getXML( doc );
        result .appendChild( modelXml );
        for ( Iterator it = bookmarks .keySet() .iterator(); it .hasNext(); )
        {
            String name = (String) it .next();
            ViewModel model = (ViewModel) bookmarks .get( name );
            modelXml = model .getXML( doc );
            DomUtils .addAttribute( modelXml, "name", name );
            result .appendChild( modelXml );
        }
        return result;
    }

    public void setXml( Element views )
    {
        NodeList nodes = views .getChildNodes();
        for ( int i = 0; i < nodes .getLength(); i++ ) {
            Node node = nodes .item( i );
            if ( node instanceof Element ) {
                Element viewElem = (Element) node;
                String name = viewElem .getAttribute( "name" );
                ViewModel model = new ViewModel( viewElem );
                if ( name == null || name .isEmpty() )
                    restoreView( model );
//                else
//                    bookmarks .put( name, model );
            }
        }
    }

	public void copyView( ViewModel newView )
	{
		this .copied = newView;
	}

	public void useCopiedView()
	{
		this .restoreView( this .copied );
	}

	public boolean hasCopiedView()
	{
		return this .copied != null;
	}
}
