
//(c) Copyright 2008, Scott Vorthmann.  All rights reserved.

package com.vzome.core.editor;


import com.vzome.core.algebra.AlgebraicVector;
import com.vzome.core.construction.Point;
import com.vzome.core.construction.Segment;
import com.vzome.core.construction.SegmentEndPoint;
import com.vzome.core.construction.SymmetryTransformation;
import com.vzome.core.construction.Transformation;
import com.vzome.core.math.symmetry.Axis;
import com.vzome.core.math.symmetry.Symmetry;
import com.vzome.core.model.Connector;
import com.vzome.core.model.Manifestation;
import com.vzome.core.model.RealizedModel;
import com.vzome.core.model.Strut;

public class RotationTool extends SymmetryTool
{
    public String getDefaultName()
    {
        return "rotation around Z axis";
    }

    @Override
    public String getCategory()
    {
        return "rotation";
    }

    public RotationTool( String name, Symmetry symmetry, Selection selection, RealizedModel realized, Tool.Registry tools, Point originPoint )
    {
        super( name, symmetry, selection, realized, tools, originPoint );
    }

    @Override
    protected String checkSelection( boolean prepareTool )
    {
        Point center = null;
        Segment axisStrut = null;
        boolean correct = true;
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
        		if ( axisStrut != null )
        		{
        			correct = false;
        			break;
        		}
        		axisStrut = (Segment) ((Strut) man) .getConstructions() .next();
        	}
        }
        
        if ( axisStrut == null )
        {
        	correct = false;
        }
        else if ( center == null )
            center = new SegmentEndPoint( axisStrut );
        
        if ( ! correct )
            return "rotation tool requires a single axis strut,\n" +
                                        "and optionally a separate center point";

        AlgebraicVector vector = axisStrut .getOffset();
        vector = axisStrut .getField() .projectTo3d( vector, true );
        Axis axis = symmetry .getAxis( vector );
        if ( axis == null )
        	return "selected strut is not an axis of rotation";
        int rotation = axis .getRotation();
        if ( rotation == Symmetry .NO_ROTATION )
        	return "selected strut is not an axis of rotation";
    	if ( prepareTool ) {
    		this .transforms = new Transformation[ 1 ];
    		transforms[ 0 ] = new SymmetryTransformation( symmetry, rotation, center );
    	}
    	return null;
    }

    @Override
    protected String getXmlElementName()
    {
        return "RotationTool";
    }
}
