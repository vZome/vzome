package org.vorthmann.zome.app.impl;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;

import javax.vecmath.Point3d;
import javax.vecmath.Quat4d;

import org.vorthmann.j3d.MouseTool;
import org.vorthmann.j3d.MouseToolDefault;
import org.vorthmann.j3d.MouseToolFilter;
import org.vorthmann.j3d.Trackball;
import org.vorthmann.ui.Controller;
import org.vorthmann.ui.DefaultController;
import org.vorthmann.ui.LeftMouseDragAdapter;

import com.vzome.core.algebra.AlgebraicField;
import com.vzome.core.algebra.AlgebraicVector;
import com.vzome.core.commands.Command.Failure;
import com.vzome.core.construction.Point;
import com.vzome.core.construction.Segment;
import com.vzome.core.editor.DocumentModel;
import com.vzome.core.model.Connector;
import com.vzome.core.model.Manifestation;
import com.vzome.core.render.RenderingChanges;
import com.vzome.desktop.controller.CameraController;
import com.vzome.desktop.controller.RenderingViewer;

public class StrutBuilderController extends DefaultController
{
	// state variables
	private boolean useGraphicalViews;
	private boolean showStrutScales;
	private boolean useWorkingPlane = false;
	private Segment workingPlaneAxis = null;
	
	// resources
    private SymmetryController symmetryController;
	private final PreviewStrut previewStrut;
	private final MouseTool previewStrutStart, previewStrutRoll, previewStrutPlanarDrag, previewStrutLength;
	private final DocumentModel documentModel;

	public StrutBuilderController( Controller parent, final CameraController cameraController, DocumentModel documentModel,
									final RenderingChanges mainScene, final RenderingViewer imageCaptureViewer )
	{
		super();
		this.documentModel = documentModel;
		setNextController( parent );
		
		this.useGraphicalViews = parent .propertyIsTrue( "useGraphicalViews" );
		this.showStrutScales = parent .propertyIsTrue( "showStrutScales" );

        AlgebraicField field = this .documentModel .getField();
        previewStrut = new PreviewStrut( field, mainScene, cameraController );

        // drag events to render or realize the preview strut;
        //   only works when drag starts over a ball
		previewStrutStart = new LeftMouseDragAdapter( new ManifestationPicker( imageCaptureViewer )
        {                
			@Override
			public void attach( Component canvas )
			{
				// This MouseTool serves as the proxy for all four previewStrut tools
				super .attach( canvas );
				previewStrutRoll .attach( canvas );
				previewStrutPlanarDrag .attach( canvas );
				previewStrutLength .attach( canvas );
			}

			@Override
			public void detach( Component canvas )
			{
				previewStrutLength .detach( canvas );
				previewStrutPlanarDrag .detach( canvas );
				previewStrutRoll .detach( canvas );
				super .detach( canvas );
			}

			@Override
            protected void dragStarted( Manifestation target, boolean b )
            {
                if ( target instanceof Connector )
                {
                    mErrors .clearError();
                    Point point = (Point) target .getConstructions() .next();
                    AlgebraicVector workingPlaneNormal = null;
                    if ( useWorkingPlane && (workingPlaneAxis != null ) )
                        workingPlaneNormal = workingPlaneAxis .getOffset();
                    previewStrut .startRendering( symmetryController, point, workingPlaneNormal );
                }
            }

            @Override
            protected void dragFinished( Manifestation target, boolean b )
            {
                previewStrut .finishPreview( documentModel );
            }
        } );

        // trackball to adjust the preview strut (when it is rendered)
		previewStrutRoll = new LeftMouseDragAdapter( new Trackball()
        {
            @Override
            protected void trackballRolled( Quat4d roll )
            {
                previewStrut .trackballRolled( roll );
            }
        } );
        
        // working plane drag events to adjust the preview strut (when it is rendered)
		previewStrutPlanarDrag = new LeftMouseDragAdapter( new MouseToolDefault()
        {
            @Override
            public void mouseDragged( MouseEvent e )
            {
                Point3d imagePt = new Point3d();
                Point3d eyePt = new Point3d();
                imageCaptureViewer .pickPoint( e, imagePt, eyePt );
                previewStrut .workingPlaneDrag( imagePt, eyePt );
            }
        } );
		
        previewStrutLength = new MouseToolFilter( cameraController .getZoomScroller() )
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
	}
	
    @Override
	public MouseTool getMouseTool()
    {
		return this .previewStrutStart;
	}

	@Override
    public String getProperty( String name )
    {
    	switch ( name ) {
    	
    	case "showStrutScales":
            return Boolean.toString( this.showStrutScales );

		case "useWorkingPlane":
			return Boolean .toString( this.useWorkingPlane );
            
		case "workingPlaneDefined":
			return Boolean .toString( this.workingPlaneAxis != null );

		case "useGraphicalViews":
			return Boolean.toString( this.useGraphicalViews );

		default:
			return super .getProperty( name );
		}
    }
    
    @Override
    public void setProperty( String name, Object value )
    {
    	switch ( name ) {

    	case "useGraphicalViews":
            this.useGraphicalViews = "true".equals( value );
            properties().firePropertyChange( name, false, this.useGraphicalViews );
			break;
    	
    	case "showStrutScales":
    		boolean old = showStrutScales;
    		this.showStrutScales = "true" .equals( value );
    		properties() .firePropertyChange( name, old, this.showStrutScales );
    		break;

		default:
	        super .setProperty( name, value );
			break;
		}
    }

    @Override
    public void doAction( String action, ActionEvent e ) throws Failure
    {
        switch ( action ) {

        case "toggleStrutScales":
            boolean old = showStrutScales;
            showStrutScales = ! old;
            properties() .firePropertyChange( "showStrutScales", old, this.showStrutScales );
        	break;

        case "toggleOrbitViews":
            old = useGraphicalViews;
            useGraphicalViews = ! old;
            properties() .firePropertyChange( "useGraphicalViews", old, this.useGraphicalViews );
        	break;

        case "toggleWorkingPlane":
            useWorkingPlane = ! useWorkingPlane;
			break;
            
//        case "setBuildOrbitAndLength": {
//            AlgebraicVector offset = ((Strut) pickedManifestation) .getOffset();
//            Axis zone = symmetryController .getZone( offset );
//            Direction orbit = zone .getOrbit();
//            AlgebraicNumber length = zone .getLength( offset );
//            symmetryController .availableController .doAction( "enableDirection." + orbit .getName(), null );
//            symmetryController .buildController .doAction( "setSingleDirection." + orbit .getName(), null );
//            LengthController lmodel = (LengthController) symmetryController .buildController .getSubController( "currentLength" );
//            lmodel .setActualLength( length );
//            }
//            break;
//
		default:
			break;
		}
    }

	public void setSymmetryController( SymmetryController symmetryController )
	{
		this.symmetryController = symmetryController;
	}
}