
package com.vzome.desktop.controller;

import com.vzome.core.algebra.AlgebraicField;
import com.vzome.core.algebra.AlgebraicVector;
import com.vzome.core.construction.Point;
import com.vzome.core.editor.api.Context;
import com.vzome.core.math.RealVector;
import com.vzome.core.render.RenderingChanges;

public class StrutBuilderController extends DefaultController
{    
    private boolean useGraphicalViews = false;

    private boolean showStrutScales = false;

    private boolean useWorkingPlane = false;

    private AlgebraicVector workingPlaneAxis = null;

    private PreviewStrut previewStrut;

    private final AlgebraicField field;

    private final Context context;

    public StrutBuilderController( Context context, AlgebraicField field )
    {
        super();
        this.context = context;
        this.field = field;
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
    public void doAction( String action ) throws Exception
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
            super .doAction( action );
        }
    }

    public void setWorkingPlaneAxis( AlgebraicVector axis )
    {
        this .workingPlaneAxis = axis;
        this .firePropertyChange( "workingPlaneDefined", false, true );
    }

    public void setMainScene( RenderingChanges mainScene )
    {
        // The preview strut rendering is the main reason we distinguish the mainScene as a listener
        this .previewStrut = new PreviewStrut( this.field, mainScene, this.context );
    }

    public PreviewStrut getPreviewStrut()
    {
        return this .previewStrut;
    }

    public void startRendering( Point point, RealVector worldEye )
    {
        this .previewStrut .startRendering( point, this .workingPlaneAxis, worldEye );
    }

    public void setSymmetryController( SymmetryController symmetryController )
    {
        if ( this .previewStrut != null )
            this.previewStrut .setSymmetryController( symmetryController );
    }
}
