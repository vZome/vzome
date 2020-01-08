
package org.vorthmann.zome.app.impl;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;

import javax.vecmath.Point3d;
import javax.vecmath.Quat4d;

import org.vorthmann.j3d.CanvasTool;
import org.vorthmann.j3d.MouseTool;
import org.vorthmann.j3d.MouseToolDefault;
import org.vorthmann.j3d.MouseToolFilter;
import org.vorthmann.j3d.Trackball;
import org.vorthmann.ui.DefaultController;
import org.vorthmann.ui.LeftMouseDragAdapter;

import com.vzome.core.algebra.AlgebraicField;
import com.vzome.core.algebra.AlgebraicVector;
import com.vzome.core.construction.Point;
import com.vzome.core.model.Connector;
import com.vzome.core.model.Manifestation;
import com.vzome.core.render.RenderingChanges;
import com.vzome.desktop.controller.CameraController;
import com.vzome.desktop.controller.RenderingViewer;

public class StrutBuilderController extends DefaultController implements CanvasTool
{    
    private boolean useGraphicalViews = false;

    private boolean showStrutScales = false;

    private boolean useWorkingPlane = false;

    private AlgebraicVector workingPlaneAxis = null;

    private PreviewStrut previewStrut;

    private MouseTool previewStrutStart, previewStrutRoll, previewStrutPlanarDrag, previewStrutLength;

    private DocumentController docController;

    private CameraController cameraController;

    public StrutBuilderController( DocumentController docController, CameraController cameraController )
    {
        super();
        this .docController = docController;
        this .cameraController = cameraController;
    }

    public StrutBuilderController withGraphicalViews( boolean value )
    {
        this .useGraphicalViews = value;
        return this;
    }

    public StrutBuilderController withShowStrutScales( boolean value )
    {
        this .showStrutScales = value;
        return this;
    }

    @Override
    public String getProperty( String propName )
    {
        switch ( propName ) {

        case "useGraphicalViews":
            return Boolean.toString( this.useGraphicalViews );

        case "useWorkingPlane":
            return Boolean .toString( useWorkingPlane );

        case "workingPlaneDefined":
            return Boolean .toString( workingPlaneAxis != null );

        case "showStrutScales":
            return Boolean.toString( this.showStrutScales );

        default:
            return super .getProperty( propName );
        }
    }
    @Override
    public void setModelProperty( String name, Object value )
    {
        switch ( name ) {

        case "useGraphicalViews": {
            boolean old = useGraphicalViews;
            this.useGraphicalViews = "true".equals( value );
            firePropertyChange( name, old, this.useGraphicalViews );
            break;
        }
            
        case "showStrutScales": {
            boolean old = showStrutScales;
            this.showStrutScales = "true" .equals( value );
            firePropertyChange( name, old, this.showStrutScales );
            break;
        }

        default:
            super .setModelProperty( name, value );
        }
    }
    
    @Override
    public void doAction( String action, ActionEvent e ) throws Exception
    {
        switch ( action ) {

        case "toggleWorkingPlane":
            useWorkingPlane = ! useWorkingPlane;
            break;

        case "toggleOrbitViews": {
            boolean old = useGraphicalViews;
            useGraphicalViews = ! old;
            firePropertyChange( "useGraphicalViews", old, this.useGraphicalViews );
            break;
        }

        case "toggleStrutScales": {
            boolean old = showStrutScales;
            showStrutScales = ! old;
            firePropertyChange( "showStrutScales", old, this.showStrutScales );
            break;
        }

        default:
            super .doAction( action, e );
        }
    }
    
    public void attach( RenderingViewer viewer, RenderingChanges scene )
    {
        // The preview strut rendering is the main reason we distinguish the mainScene as a listener
        AlgebraicField field = this .docController .getModel() .getField();
        this .previewStrut = new PreviewStrut( field, scene, cameraController );

        this .previewStrutLength = new MouseToolFilter( cameraController .getZoomScroller() )
        {
            @Override
            public void mouseWheelMoved( MouseWheelEvent e )
            {
                LengthController length = previewStrut .getLengthModel();
                if ( length != null )
                {
                    // scroll to scale the preview strut (when it is rendered)
                    length .getMouseTool() .mouseWheelMoved( e );
                    // don't adjustPreviewStrut() here, let the prop change trigger it,
                    // so we don't flicker for every tick of the mousewheel
                }
                else
                {
                    // no strut build in progress, so zoom the view
                    super .mouseWheelMoved( e );
                }
            }
        };

        // drag events to render or realize the preview strut;
        //   only works when drag starts over a ball
        this .previewStrutStart = new LeftMouseDragAdapter( new ManifestationPicker( viewer )
        {
            @Override
            public void mouseClicked( MouseEvent e ) {} // avoid the duplicate pick!

            @Override
            protected void dragStarted( Manifestation target, boolean b )
            {
                if ( target instanceof Connector )
                {
                    mErrors .clearError();
                    Point point = (Point) target .getConstructions() .next();
                    AlgebraicVector workingPlaneNormal = null;
                    if ( useWorkingPlane && (workingPlaneAxis != null ) )
                        workingPlaneNormal = workingPlaneAxis;
                    previewStrut .startRendering( docController .getSymmetryController(), point, workingPlaneNormal );
                }
            }

            @Override
            protected void dragFinished( Manifestation target, boolean b )
            {
                previewStrut .finishPreview( docController .getModel() );
            }
        } );

        // trackball to adjust the preview strut (when it is rendered)
        this .previewStrutRoll = new LeftMouseDragAdapter( new Trackball()
        {
            @Override
            protected void trackballRolled( Quat4d roll )
            {
                previewStrut .trackballRolled( roll );
            }
        } );
        
        // working plane drag events to adjust the preview strut (when it is rendered)
        this .previewStrutPlanarDrag = new LeftMouseDragAdapter( new MouseToolDefault()
        {
            @Override
            public void mouseDragged( MouseEvent e )
            {
                Point3d imagePt = new Point3d();
                Point3d eyePt = new Point3d();
                viewer .pickPoint( e, imagePt, eyePt );
                previewStrut .workingPlaneDrag( imagePt, eyePt );
            }
        } );
    }
    
    public void setSymmetryController( SymmetryController symmetryController )
    {
        if ( previewStrut != null )
            previewStrut .setSymmetryController( symmetryController );
    }

    @Override
    public void attach( Component canvas )
    {
        previewStrutStart .attach( canvas );
        previewStrutRoll .attach( canvas );
        previewStrutPlanarDrag .attach( canvas );
        previewStrutLength .attach( canvas );
    }

    @Override
    public void detach( Component canvas )
    {
        previewStrutStart .detach( canvas );
        previewStrutRoll .detach( canvas );
        previewStrutPlanarDrag .detach( canvas );
        previewStrutLength .detach( canvas );
    }

    public void setWorkingPlaneAxis( AlgebraicVector axis )
    {
        this .workingPlaneAxis = axis;
        this .firePropertyChange( "workingPlaneDefined", false, true );
    }
}
