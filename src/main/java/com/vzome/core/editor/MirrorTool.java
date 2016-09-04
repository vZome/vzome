
//(c) Copyright 2008, Scott Vorthmann.  All rights reserved.

package com.vzome.core.editor;


import com.vzome.core.algebra.AlgebraicField;
import com.vzome.core.algebra.AlgebraicVector;
import com.vzome.core.construction.FreePoint;
import com.vzome.core.construction.Plane;
import com.vzome.core.construction.PlaneExtensionOfPolygon;
import com.vzome.core.construction.PlaneFromNormalSegment;
import com.vzome.core.construction.PlaneReflection;
import com.vzome.core.construction.Point;
import com.vzome.core.construction.Polygon;
import com.vzome.core.construction.Segment;
import com.vzome.core.construction.SegmentJoiningPoints;
import com.vzome.core.construction.Transformation;
import com.vzome.core.math.symmetry.Direction;
import com.vzome.core.model.Connector;
import com.vzome.core.model.Manifestation;
import com.vzome.core.model.Panel;
import com.vzome.core.model.RealizedModel;
import com.vzome.core.model.Strut;

public class MirrorTool extends TransformationTool
{
	public static class Factory extends AbstractToolFactory implements ToolFactory
	{
		public Factory( EditorModel model, UndoableEdit.Context context )
		{
			super( model, context );
		}

		@Override
		protected boolean countsAreValid( int total, int balls, int struts, int panels )
		{
			return ( total == 2 && balls == 1 && struts == 1 )
				|| ( total == 1 && panels == 1 );
		}

		@Override
		public Tool createToolInternal( int index )
		{
			return new MirrorTool( "mirror." + index, getSelection(), getModel(), null );
		}

		@Override
		protected boolean bindParameters( Selection selection, SymmetrySystem symmetry )
		{
			// TODO Auto-generated method stub
			return true;
		}
	}

	public MirrorTool( String name, Selection selection, RealizedModel realized, Point originPoint )
    {
        super( name, selection, realized, null, originPoint );
    }
	
    @Override
    protected String checkSelection( boolean prepareTool )
    {
        Point center = null;
        Segment axis = null;
        Polygon mirrorPanel = null;
        if ( this .getName() .equals( "mirror.builtin/reflection through XY plane" ) )
        {
            center = originPoint;
    		this .addParameter( center );
            AlgebraicField field = originPoint .getField();
            AlgebraicVector zAxis = field .basisVector( 3, AlgebraicVector .Z ) .scale( field .createPower( Direction.USER_SCALE ) );
            Point p2 = new FreePoint( zAxis );
            axis = new SegmentJoiningPoints( center, p2 );
    		this .addParameter( axis );
        }
        else if ( isAutomatic() )
        {
            center = originPoint;
            AlgebraicField field = originPoint .getField();
            AlgebraicVector xAxis = field .basisVector( 3, AlgebraicVector .X );
            Point p2 = new FreePoint( xAxis );
            axis = new SegmentJoiningPoints( center, p2 );
        }
        else
        	for (Manifestation man : mSelection) {
        		if ( prepareTool )
        			unselect( man );
        		// The legacy validation / binding is associated with prepareTool,
        		//  so that old files will open.  Apparently, the first of many
        		//  balls, panels, or struts was used for the parameter, though I
        		//  doubt that anyone ever used it this way.
        		if ( man instanceof Connector )
        		{
        			if ( center != null )
        			{
        				if ( prepareTool )
        					break;
        				else
        					return "Only one center ball may be selected";
        			}
        			center = (Point) ((Connector) man) .getConstructions() .next();
        		}
        		else if ( man instanceof Strut )
        		{
        			if ( axis != null )
        			{
        				if ( prepareTool )
        					break;
        				else
        					return "Only one mirror axis strut may be selected";
        			}
        			axis = (Segment) ((Strut) man) .getConstructions() .next();
        		}
        		else if ( man instanceof Panel )
        		{
        			if ( mirrorPanel != null )
        			{
        				if ( prepareTool )
        					break;
        				else
        					return "Only one mirror panel may be selected";
        			}
        			mirrorPanel = (Polygon) ((Panel) man) .getConstructions() .next();
        		}
        	}
        if ( center == null ) {
        	if ( prepareTool ) // after validation, or when loading from a file
        		center = originPoint;
        	else if ( mirrorPanel == null ) // just validating the selection, not really creating a tool
        		return "No symmetry center selected";
        }
        
        Plane mirrorPlane = null;
        if ( axis != null && center != null && mirrorPanel == null ) {
        	if ( prepareTool )
        		mirrorPlane = new PlaneFromNormalSegment( center, axis );
        }
        else if ( axis == null && mirrorPanel != null ) {
        	if ( prepareTool )
        		mirrorPlane = new PlaneExtensionOfPolygon( mirrorPanel );
        	else if ( center != null )
                return "mirror tool requires a single panel,\n"
                + "or a single strut and a single center ball";
        }
        else
            return "mirror tool requires a single panel,\n"
            + "or a single strut and a single center ball";
    
    	if ( prepareTool ) {
    		this .transforms = new Transformation[ 1 ];
    		transforms[ 0 ] = new PlaneReflection( mirrorPlane );
    	}
    
        return null;
    }

    @Override
    protected String getXmlElementName()
    {
        return "MirrorTool";
    }

    @Override
    public String getCategory()
    {
        return "mirror";
    }

    @Override
    public String getDefaultName( String baseName )
    {
        return "reflect through X axis";
    }
}
