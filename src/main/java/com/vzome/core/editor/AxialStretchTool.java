
//(c) Copyright 2008, Scott Vorthmann.  All rights reserved.

package com.vzome.core.editor;


import com.vzome.core.algebra.AlgebraicMatrix;
import com.vzome.core.algebra.AlgebraicNumber;
import com.vzome.core.algebra.AlgebraicVector;
import com.vzome.core.construction.MatrixTransformation;
import com.vzome.core.construction.Point;
import com.vzome.core.construction.Segment;
import com.vzome.core.construction.Transformation;
import com.vzome.core.math.symmetry.Axis;
import com.vzome.core.math.symmetry.Direction;
import com.vzome.core.math.symmetry.IcosahedralSymmetry;
import com.vzome.core.math.symmetry.Symmetry;
import com.vzome.core.model.Connector;
import com.vzome.core.model.Manifestation;
import com.vzome.core.model.Panel;
import com.vzome.core.model.RealizedModel;
import com.vzome.core.model.Strut;

public class AxialStretchTool extends TransformationTool
{
    private final IcosahedralSymmetry symmetry;
	private final boolean stretch;

	public AxialStretchTool( String name, IcosahedralSymmetry symmetry, Selection selection, RealizedModel realized, Tool.Registry tools, boolean stretch )
    {
        super( name, selection, realized, tools, null );
		this.symmetry = symmetry;
		this .stretch = stretch;
    }

    protected String checkSelection( boolean prepareTool )
    {
    	Point center = null;
    	Segment axis = null;
        for ( Manifestation man : mSelection ) {
        	if ( prepareTool )
        		unselect( man );
            if ( man instanceof Connector )
            {
                if ( center != null )
                	return "Only one ball may be selected";
                center = (Point) ((Connector) man) .getConstructions() .next();
            }
            else if ( man instanceof Strut )
            {
                if ( axis != null )
                	return "Only one strut may be selected";
                axis = (Segment) ((Strut) man) .getConstructions() .next();
            } else if ( man instanceof Panel ) {
            	return "Panels are not supported.";
            }
        }
        if ( center == null )
        	return "Exactly one ball must be selected.";
        if ( axis == null )
        	return "Exactly one strut must be selected.";
        
		Axis zone = this .symmetry .getAxis( axis .getOffset() );
		if ( zone == null )
			return "Selected alignment strut is not an appropriate axis.";

		AlgebraicVector o0, o1, o2, n0, n1, n2;
    	switch ( zone .getDirection() .getName() ) {

		case "yellow":
	    	Direction blueOrbit = symmetry .getDirection( "blue" );
	    	AlgebraicNumber blueScale = blueOrbit .getUnitLength();
        	o0 = blueOrbit .getAxis( Symmetry.PLUS, 2 ) .normal() .scale( blueScale );
        	o1 = blueOrbit .getAxis( Symmetry.PLUS, 54 ) .normal() .scale( blueScale );
        	o2 = blueOrbit .getAxis( Symmetry.PLUS, 36 ) .normal() .scale( blueScale );
        	Direction redOrbit = symmetry .getDirection( "red" );
	    	AlgebraicNumber redScale = redOrbit .getUnitLength();
        	n0 = redOrbit .getAxis( Symmetry.PLUS, 2 ) .normal() .scale( redScale );
        	n1 = redOrbit .getAxis( Symmetry.PLUS, 46 ) .normal() .scale( redScale );
        	n2 = redOrbit .getAxis( Symmetry.PLUS, 16 ) .normal() .scale( redScale );
			break;

		case "red":
	    	blueOrbit = symmetry .getDirection( "blue" );
	    	blueScale = blueOrbit .getUnitLength();
        	o0 = blueOrbit .getAxis( Symmetry.PLUS, 56 ) .normal() .scale( blueScale );
        	o1 = blueOrbit .getAxis( Symmetry.PLUS, 38 ) .normal() .scale( blueScale );
        	o2 = blueOrbit .getAxis( Symmetry.PLUS, 40 ) .normal() .scale( blueScale );
        	redOrbit = symmetry .getDirection( "red" );
	    	redScale = redOrbit .getUnitLength();
        	n0 = redOrbit .getAxis( Symmetry.PLUS, 46 ) .normal() .scale( redScale );
        	n1 = redOrbit .getAxis( Symmetry.PLUS, 1 ) .normal() .scale( redScale );
        	n2 = redOrbit .getAxis( Symmetry.PLUS, 2 ) .normal() .scale( redScale );
			break;

		default:
			return "Selected alignment strut is not an appropriate axis.";
		}

        if ( prepareTool )
        {
        	AlgebraicMatrix orientation = symmetry .getMatrix( zone .getOrientation() );
        	AlgebraicMatrix inverse = orientation .inverse();
        	AlgebraicMatrix oldBasis = new AlgebraicMatrix( o0, o1, o2 );
        	AlgebraicMatrix newBasis = new AlgebraicMatrix( n0, n1, n2 );
        	if ( this .stretch ) {
        		AlgebraicMatrix temp = oldBasis;
        		oldBasis = newBasis;
        		newBasis = temp;
        	}
	    	AlgebraicMatrix matrix = orientation .times( newBasis .times( oldBasis .inverse() ) .times( inverse ) );
	    	this .transforms = new Transformation[ 1 ];
	    	transforms[ 0 ] = new MatrixTransformation( matrix, center .getLocation() );
		}
        return null;
    }

    @Override
    protected String getXmlElementName()
    {
        return "AxialStretchTool";
    }

    @Override
    public String getCategory()
    {
        return "axial stretch";
    }

    @Override
    public String getDefaultName( String baseName )
    {
        return "SHOULD NEVER HAPPEN";
    }
}
