
//(c) Copyright 2008, Scott Vorthmann.  All rights reserved.

package com.vzome.core.editor;


import org.w3c.dom.Element;

import com.vzome.core.algebra.AlgebraicField;
import com.vzome.core.algebra.AlgebraicMatrix;
import com.vzome.core.algebra.AlgebraicNumber;
import com.vzome.core.algebra.AlgebraicVector;
import com.vzome.core.commands.Command.Failure;
import com.vzome.core.commands.XmlSaveFormat;
import com.vzome.core.construction.MatrixTransformation;
import com.vzome.core.construction.Point;
import com.vzome.core.construction.Scaling;
import com.vzome.core.construction.Segment;
import com.vzome.core.construction.Transformation;
import com.vzome.core.math.symmetry.Axis;
import com.vzome.core.math.symmetry.Direction;
import com.vzome.core.math.symmetry.Symmetry;
import com.vzome.core.model.Connector;
import com.vzome.core.model.Manifestation;
import com.vzome.core.model.Panel;
import com.vzome.core.model.RealizedModel;
import com.vzome.core.model.Strut;

public class ScalingTool extends SymmetryTool
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
			return ( total == 3 && balls == 1 && struts == 2 );
		}

		@Override
		public Tool createToolInternal( int index )
		{
			return new ScalingTool( "scaling." + index, null, getSelection(), getModel(), null );
		}

		@Override
		protected boolean bindParameters(Selection selection, SymmetrySystem symmetry) {
			// TODO Auto-generated method stub
			return false;
		}
	}

	private final AlgebraicNumber scaleFactor;
	
    @Override
    public String getCategory()
    {
        return "scaling";
    }
    
    public ScalingTool( String name, AlgebraicNumber scaleFactor, Point originPoint )
    {
    	super( name, null, null, null, originPoint );
    	this .scaleFactor = scaleFactor;
    }

    public ScalingTool( String name, Symmetry symmetry, Selection selection, RealizedModel realized, Point originPoint )
    {
        super( name, symmetry, selection, realized, originPoint );
        this .scaleFactor = null;
    }

    @Override
    protected String checkSelection( boolean prepareTool )
    {
    	if ( this .scaleFactor != null ) {
    		// a predefined tool
    		AlgebraicField field = this .scaleFactor .getField();
    		this .transforms = new Transformation[ 1 ];
            AlgebraicMatrix transform = new AlgebraicMatrix(
                    field .basisVector( 3, AlgebraicVector.X ) .scale( this .scaleFactor ),
                    field .basisVector( 3, AlgebraicVector.Y ) .scale( this .scaleFactor ),
                    field .basisVector( 3, AlgebraicVector.Z ) .scale( this .scaleFactor ) );
    		transforms[ 0 ] = new MatrixTransformation( transform, this .originPoint .getLocation() );
    		return null;
    	}
    	
        Segment s1 = null, s2 = null;
        Point center = null;
        boolean correct = true;
        boolean hasPanels = false;
        for (Manifestation man : mSelection) {
        	if ( prepareTool )
        		unselect( man );
            if ( man instanceof Connector )
            {
                if ( center != null )
                {
                    correct = false;
                    break;
                }
                center = (Point) ((Connector) man) .getConstructions() .next();
            }
            else if ( man instanceof Strut )
            {
                if ( s2 != null )
                {
                    correct = false;
                    break;
                }
                if ( s1 == null )
                    s1 = (Segment) ((Strut) man) .getConstructions() .next();
                else
                    s2 = (Segment) ((Strut) man) .getConstructions() .next();
            }
            else if ( man instanceof Panel )
            	hasPanels = true;
        }
        
        if ( center == null ) {
        	if ( prepareTool ) // after validation, or when loading from a file
        		center = originPoint;
        	else // just validating the selection, not really creating a tool
        		return "No symmetry center selected";
        }
        
        correct = correct && s2 != null;
        if ( !prepareTool && hasPanels )
        	correct = false;  // panels must be allowed when opening legacy files (prepareTool)
        if ( !correct )
            return "scaling tool requires before and after struts, and a single center";

        Axis zone1 = symmetry .getAxis( s1 .getOffset() );
        Axis zone2 = symmetry .getAxis( s2 .getOffset() );
        if(zone1 == null || zone2 == null)
            return "struts cannot be automatic";
        Direction orbit = zone1 .getDirection();
        if ( orbit != zone2 .getDirection() )
            return "before and after struts must be from the same orbit";

    	if ( prepareTool ) {
    		this .transforms = new Transformation[ 1 ];
    		transforms[ 0 ] = new Scaling( s1, s2, center, symmetry );
    	}
        return null;
    }

    @Override
    protected String getXmlElementName()
    {
        return "ScalingTool";
    }

    @Override
    protected void setXmlAttributes( Element element, XmlSaveFormat format ) throws Failure
    {
        String symmName = element .getAttribute( "symmetry" );

        if ( symmName == null || symmName .isEmpty() )
        {
            element .setAttribute( "symmetry", "icosahedral" );
            logBugAccommodation( "scaling tool serialized with no symmetry; assuming icosahedral" );
        }
        super .setXmlAttributes( element, format );
    }
}
