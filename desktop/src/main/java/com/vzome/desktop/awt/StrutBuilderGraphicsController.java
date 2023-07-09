package com.vzome.desktop.awt;

import java.awt.Component;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;

import javax.vecmath.Matrix3f;
import javax.vecmath.Quat4f;
import javax.vecmath.Vector3f;

import org.vorthmann.j3d.CanvasTool;
import org.vorthmann.j3d.MouseTool;
import org.vorthmann.j3d.MouseToolDefault;
import org.vorthmann.j3d.MouseToolFilter;
import org.vorthmann.j3d.Trackball;
import org.vorthmann.ui.LeftMouseDragAdapter;

import com.vzome.core.construction.Point;
import com.vzome.core.math.Line;
import com.vzome.core.math.RealVector;
import com.vzome.core.model.Connector;
import com.vzome.core.model.Manifestation;
import com.vzome.core.render.RenderingChanges;
import com.vzome.desktop.controller.CameraController;
import com.vzome.desktop.controller.PreviewStrut;
import com.vzome.desktop.controller.StrutBuilderController;

public class StrutBuilderGraphicsController extends DefaultGraphicsController implements CanvasTool
{
    private MouseTool previewStrutStart, previewStrutRoll, previewStrutPlanarDrag, previewStrutLength;

    private final CameraController cameraController;

    private final StrutBuilderController parent;

    private transient LengthCanvasTool mouseLengthTool; // only populated when preview strut drag starts

    private PreviewStrut previewStrut;
    
    public StrutBuilderGraphicsController( StrutBuilderController parent, CameraController cameraController )
    {
        super();
        this.parent = parent;
        this.cameraController = cameraController;
        
        this .parent .addSubController( "graphics", this );
    }

    public void attach( RenderingViewer viewer, RenderingChanges scene )
    {
        this .previewStrut = this.parent .getPreviewStrut();
        this .previewStrutLength = new MouseToolFilter( new CameraZoomWheel( cameraController ) ) // this filter seems spurious
        {
            @Override
            public void mouseWheelMoved( MouseWheelEvent e )
            {
                if ( mouseLengthTool != null )
                {
                    // scroll to scale the preview strut (when it is rendered)
                    mouseLengthTool .mouseWheelMoved( e );
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
                    Point point = (Point) target .getFirstConstruction();
                    Vector3f Z = new Vector3f( 0f, 0f, 1f );
                    cameraController .mapViewToWorld( Z );
                    mouseLengthTool = new LengthCanvasTool( previewStrut );
                    parent .startRendering( point, new RealVector( Z.x, Z.y, Z.z ) );
                }
            }

            @Override
            protected void dragFinished( Manifestation target, boolean b )
            {
                previewStrut .finishPreview();
                mouseLengthTool = null;
            }
        } );

        // trackball to adjust the preview strut (when it is rendered)
        this .previewStrutRoll = new LeftMouseDragAdapter( new Trackball()
        {
            @Override
            protected void trackballRolled( Quat4f roll )
            {
                cameraController .getWorldRotation( roll );
                Matrix3f R = new Matrix3f();
                R.set( roll );
                RealVector[] rowMajor = new RealVector[]{
                    new RealVector( R.m00, R.m01, R.m02 ),
                    new RealVector( R.m10, R.m11, R.m12 ),
                    new RealVector( R.m20, R.m21, R.m22 ) };
                previewStrut .trackballRolled( rowMajor );
            }
        } );
        
        // working plane drag events to adjust the preview strut (when it is rendered)
        this .previewStrutPlanarDrag = new LeftMouseDragAdapter( new MouseToolDefault()
        {
            @Override
            public void mouseDragged( MouseEvent e )
            {
                Line ray = viewer .pickRay( e );
                previewStrut .workingPlaneDrag( ray );
            }
        } );
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

}
