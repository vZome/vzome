package org.vorthmann.zome.app.impl;

import java.awt.event.ActionEvent;

import org.vorthmann.j3d.MouseTool;
import org.vorthmann.ui.Controller;
import org.vorthmann.ui.DefaultController;

import com.vzome.core.algebra.AlgebraicNumber;
import com.vzome.core.algebra.AlgebraicVector;
import com.vzome.core.commands.Command.Failure;
import com.vzome.core.construction.Segment;
import com.vzome.core.math.symmetry.Axis;
import com.vzome.core.math.symmetry.Direction;
import com.vzome.core.model.Strut;

public class StrutBuilderController extends DefaultController
{
	private boolean useGraphicalViews;
	private boolean showStrutScales;
	private boolean useWorkingPlane = false;
	private Segment workingPlaneAxis = null;
	
	private MouseTool modelModeMainTrackball;

	private PreviewStrut previewStrut;
	private MouseTool previewStrutStart;
	private MouseTool previewStrutRoll;
	private MouseTool previewStrutPlanarDrag;

	public StrutBuilderController( Controller parent )
	{
		super();
		setNextController( parent );
		
		this.useGraphicalViews = parent .propertyIsTrue( "useGraphicalViews" );
		this.showStrutScales = parent .propertyIsTrue( "showStrutScales" );
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
}