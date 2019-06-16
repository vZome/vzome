/*
 * Created on Jun 30, 2003
 */
package com.vzome.desktop.controller;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import javax.vecmath.Matrix4d;
import javax.vecmath.Point3d;
import javax.vecmath.Quat4d;
import javax.vecmath.Vector3d;
import javax.vecmath.Vector3f;

import org.vorthmann.j3d.MouseTool;
import org.vorthmann.j3d.MouseToolDefault;
import org.vorthmann.j3d.Trackball;
import org.vorthmann.ui.DefaultController;
import org.vorthmann.zome.app.impl.TrackballRenderingViewer;

import com.vzome.core.render.RenderedManifestation;
import com.vzome.core.render.RenderedModel;
import com.vzome.core.render.RenderingChanges;
import com.vzome.core.viewing.Camera;

/**
 * In this camera model, the view frustum shape is generally held constant
 * as other parameters are varied.
 */
public class CameraController extends DefaultController implements Controller3d
{
    /**
     * The original frustum.
     */
    public static final double ORIG_WIDTH = 18f, ORIG_DISTANCE = 40f;

    public static final double DEFAULT_STEREO_ANGLE = Math .PI * 5d / 360d;

    protected static final Vector3f ORIG_LOOK = new Vector3f(0,0,-1);

    protected static final Vector3f ORIG_UP = new Vector3f(0,1,0);

    private Camera model;

    private Camera copied = null;

    protected final List<CameraController.Viewer> mViewers = new ArrayList<>();

    private final Camera initialCamera;

    private RenderingChanges scene;
    private RenderedModel symmetryModel;

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

    /**
     * The width of the frustum at the look-at point is held
     * constant, as well as the other dimensions of the frustum.
     * @param value
     */
    public void setPerspective( boolean value )
    {
        model .setPerspective( value );
        updateViewersTransformation();
        updateViewersProjection();
    }

    public void getViewOrientation( Vector3d lookDir, Vector3d upDir )
    {
        model .getViewOrientation( lookDir, upDir );
    }


    public void addViewer( CameraController.Viewer viewer )
    {
        mViewers .add( viewer );
    }

    public void removeViewer( CameraController.Viewer viewer )
    {
        mViewers .remove( viewer );
    }

    public CameraController( Camera init )
    {
        model = init;
        initialCamera = new Camera( model );
    }

    // TODO get rid of this
    public Camera getView()
    {
        return new Camera( model );
    }

    public Camera restoreView( Camera view )
    {
        if ( view == null )
            return model;
        boolean wasPerspective = model .isPerspective();
        boolean wasStereo = model .isStereo();
        float oldMag = model .getMagnification();
        model = new Camera( view );
        updateViewersTransformation();
        updateViewersProjection();

        if ( wasPerspective != model .isPerspective() )
            firePropertyChange( "perspective", wasPerspective, model .isPerspective() );
        if ( wasStereo != model .isStereo() )
            firePropertyChange( "stereo", wasStereo, model .isStereo() );
        if ( oldMag != model .getMagnification() )
            firePropertyChange( "magnification", Float .toString( oldMag ), Float .toString( model .getMagnification() ) );

        return model;
    }

    private void updateViewersTransformation()
    {
        if ( mViewers .size() == 0 )
            return;
        Matrix4d trans = new Matrix4d();

        model .getViewTransform( trans, 0d );
        trans .invert();
        for ( int i = 0; i < mViewers .size(); i++ )
            mViewers .get( i ) .setViewTransformation( trans, Viewer .MONOCULAR );

        model .getStereoViewTransform( trans, Viewer .LEFT_EYE );
        trans .invert();
        for ( int i = 0; i < mViewers .size(); i++ )
            mViewers .get( i ) .setViewTransformation( trans, Viewer .LEFT_EYE );

        model .getStereoViewTransform( trans, Viewer .RIGHT_EYE );
        trans .invert();
        for ( int i = 0; i < mViewers .size(); i++ )
            mViewers .get( i ) .setViewTransformation( trans, Viewer .RIGHT_EYE );
    }

    private void updateViewersProjection()
    {
        if ( mViewers .size() == 0 )
            return;
        double near = model .getNearClipDistance();
        double far = model .getFarClipDistance();
        if ( ! model .isPerspective() ) {
            double edge = model .getWidth() / 2;
            for ( int i = 0; i < mViewers .size(); i++ )
                mViewers .get( i ) .setOrthographic( edge, near, far );
        }
        else {
            double field = model .getFieldOfView();
            for ( int i = 0; i < mViewers .size(); i++ )
                mViewers .get( i ) .setPerspective( field, 1.0d, near, far );
        }

        // TODO - make aspect ratio track the screen window shape
    }


    public void getWorldRotation( Quat4d q )
    {
        Vector3d axis = new Vector3d( q.x, q.y, q.z );

        Matrix4d viewTrans = new Matrix4d();
        model .getViewTransform( viewTrans, 0d );
        viewTrans .invert();

        // now map the axis back to world coordinates
        viewTrans .transform( axis );
        q.x = axis.x; q.y = axis.y; q.z = axis.z;
    }


    public void mapViewToWorld( Vector3f vector )
    {
        Matrix4d viewTrans = new Matrix4d();
        model .getViewTransform( viewTrans, 0d );
        viewTrans .invert();
        viewTrans .transform( vector );
    }

    public void setViewDirection( Vector3f lookDir )
    {
        model .setViewDirection( lookDir );
        updateViewersTransformation();
    }

    public void setViewDirection( Vector3f lookDir, Vector3f upDir )
    {
        model .setViewDirection( lookDir, upDir );
        updateViewersTransformation();
    }

    public void setLookAtPoint( Point3d lookAt )
    {
        model .setLookAtPoint( lookAt );
        updateViewersTransformation();
    }

    public void addViewpointRotation( Quat4d rotation )
    {
        model .addViewpointRotation( rotation );
        updateViewersTransformation();
    }

    /**
     * All view parameters will scale with distance, to keep the frustum
     * shape fixed.
     * @param distance
     */
    public void setMagnification( float exp )
    {
        model .setMagnification( exp );

        // have to adjust the projection, since the clipping distances
        //   adjust with distance
        updateViewersTransformation();
        updateViewersProjection();
    }


    private final LinkedList<Camera> recentViews = new LinkedList<>();

    private Camera baselineView = model;  // invariant: baselineView .equals( mParameters) whenever the view
    // is "at rest" (not rolling or zooming), AND baselineView equals the latest recentView
    private final static int MAX_RECENT = 20;

    private int currentRecentView = 0;

    private boolean saveBaselineView()
    {
        if ( model .equals( baselineView ) )
            return false;
        baselineView = new Camera( model );
        recentViews .add( baselineView );
        if ( recentViews .size() > MAX_RECENT )
            recentViews .removeFirst();
        currentRecentView = recentViews .size();
        return true;
    }

    private static final Vector3f Z = new Vector3f( 0f, 0f, -1f ), Y = new Vector3f( 0f, 1f, 0f );

    private Snapper snapper = null;

    private boolean snapping = false;

    public boolean isSnapping()
    {
        return snapping;
    }

    public void snapView()
    {
        Z .set( 0f, 0f, -1f );
        mapViewToWorld( Z );
        Y .set( 0f, 1f, 0f );
        mapViewToWorld( Y );

        snapper .snapDirections( Z, Y );

        setViewDirection( Z, Y );
    }

    @Override
    public void doAction( String action, ActionEvent e ) throws Exception
    {
        if ( action .equals( "toggleSnap" ) )
        {
            snapping = !snapping;
            if ( snapping )
            {
                saveBaselineView(); // might have been zooming
                snapView();
                saveBaselineView();
            }
        }
        else if ( action .equals( "toggleStereo" ) )
        {
            boolean wasStereo = model .isStereo();
            if ( ! wasStereo )
                model .setStereoAngle( CameraController.DEFAULT_STEREO_ANGLE );
            else
                model .setStereoAngle( 0d );
            updateViewersTransformation();
            updateViewersProjection();
            firePropertyChange( "stereo", wasStereo, !wasStereo );
        }
        else if ( action .equals( "togglePerspective" ) )
        {
            saveBaselineView(); // might have been zooming
            model .setPerspective( ! model .isPerspective() );
            updateViewersTransformation();
            updateViewersProjection();
            saveBaselineView();
        }
        else if ( action .equals( "goForward" ) )
        {
            if ( currentRecentView >= recentViews .size() )
                return;
            restoreView( recentViews .get( ++currentRecentView ) );
        }
        else if ( action .equals( "goBack" ) )
        {
            if ( currentRecentView == 0 )
                return;
            boolean wasZooming = saveBaselineView(); // might have been zooming
            if ( ( currentRecentView == recentViews .size() ) && wasZooming ) // we're not browsing recent views
                --currentRecentView; //    skip over the view we just saved
            restoreView( recentViews .get( --currentRecentView ) );
        }
        else if ( action .equals( "initialView" ) )
        {
            saveBaselineView(); // might have been zooming
            restoreView( this .initialCamera );
            // bookmarked views are not "special"... they are stored in recent, too
            saveBaselineView();
        }
    }

    public MouseTool getTrackball()
    {
        return new Trackball()
        {
            @Override
            public void mousePressed( MouseEvent e )
            {
                saveBaselineView(); // might have been zooming
                super .mousePressed( e );
            }

            @Override
            public void trackballRolled( Quat4d roll )
            {
                Quat4d copy = new Quat4d( roll );

                getWorldRotation( copy );

                addViewpointRotation( copy );

                // TODO give will-snap feedback when drag paused
            }

            @Override
            public void mouseReleased( MouseEvent e )
            {
                if ( snapping )
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
            @Override
            public void mouseWheelMoved( MouseWheelEvent e )
            {
                int amt = e .getWheelRotation();
                float oldMag = model .getMagnification();
                int ticks = magToTicks( oldMag );
                ticks -= amt;
                float newMag = ticksToMag( ticks );
                setMagnification( newMag );
                firePropertyChange( "magnification", Float .toString( oldMag ), Float .toString( newMag ) );
            }
        };
    }

    @Override
    public String getProperty( String propName )
    {
        switch( propName) {

        case "magnification":
            return Float .toString(  model .getMagnification() );

        case "perspective":
            return Boolean .toString( model .isPerspective() );

        case "snap":
            return Boolean .toString( isSnapping() );

        case "stereo":
            return Boolean .toString( model .isStereo() );

        case "viewDistance":
            return Float .toString(  model .getViewDistance() );

        case "lookAtPoint":
            Point3d lookAt = model .getLookAtPoint();
            return lookAt.toString();

        case "lookDir":
        {
            Vector3d lookDir = new Vector3d();
            Vector3d upDir = new Vector3d();
            model .getViewOrientation(lookDir, upDir);
            return lookDir.toString();
        }

        case "upDir":
        {
            Vector3d lookDir = new Vector3d();
            Vector3d upDir = new Vector3d();
            model .getViewOrientation(lookDir, upDir);
            return upDir.toString();
        }

        case "drawNormals": // for the trackball rendering
            return "false";

        case "drawOutlines": // for the trackball rendering
            return "false";

        case "showIcosahedralLabels":
            // TODO refactor to fix this
            if ( super .propertyIsTrue( "isIcosahedralSymmetry" ) )
                return super.getProperty( "trackball.showIcosahedralLabels" );
            else
                return "false";
            
        default:
            return super .getProperty( propName );
        }
    }

    private final static long ZOOM_PAUSE = 3000; // three seconds

    private long lastZoom = 0;


    @Override
    public void setModelProperty( String propName, Object value )
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
        else
            super .setModelProperty( propName, value );
    }

    public void copyView( Camera newView )
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

    @Override
    public void attachViewer( RenderingViewer viewer, RenderingChanges scene, Component canvas )
    {
        MouseTool trackball = this .getTrackball();
        
        // cannot use MouseTool .attach(), because it attaches a useless wheel listener,
        //  and CameraControlPanel will attach a better one to the parent component 
        canvas .addMouseListener( trackball );
        canvas .addMouseMotionListener( trackball );

        this .addViewer( new TrackballRenderingViewer( viewer ) );

        this .scene = scene;
        for ( RenderedManifestation rm : this .symmetryModel )
            this .scene .manifestationAdded( rm );

        updateViewersTransformation();
        updateViewersProjection();
    }
    
    public void setSymmetry( RenderedModel model, Snapper snapper )
    {
        this .symmetryModel = model;
        if ( scene != null ) {
            scene .reset();
            for ( RenderedManifestation rm : symmetryModel )
                scene .manifestationAdded( rm );
        }
        this .snapper = snapper;
        if ( snapping ) {
            saveBaselineView(); // might have been zooming
            snapView();
            saveBaselineView();
        }
    }
}
