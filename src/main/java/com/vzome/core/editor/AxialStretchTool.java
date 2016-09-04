
//(c) Copyright 2008, Scott Vorthmann.  All rights reserved.

package com.vzome.core.editor;


import org.w3c.dom.Element;

import com.vzome.core.algebra.AlgebraicMatrix;
import com.vzome.core.algebra.AlgebraicNumber;
import com.vzome.core.algebra.AlgebraicVector;
import com.vzome.core.commands.Command.Failure;
import com.vzome.core.commands.XmlSaveFormat;
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
	public static class Factory extends AbstractToolFactory implements ToolFactory
	{
		private transient IcosahedralSymmetry symmetry;
		private final boolean red, stretch, first;
		
		public Factory( EditorModel model, UndoableEdit.Context context, boolean red, boolean stretch, boolean first )
		{
			super( model, context );
			this.red = red;
			this.stretch = stretch;
			this.first = first;
		}

		@Override
		protected boolean countsAreValid( int total, int balls, int struts, int panels )
		{
			return ( total == 2 && balls == 1 && struts == 1 );
		}

		@Override
		public Tool createToolInternal( int index )
		{
			String category;
			if ( red )
				if ( first )
					category = stretch? "redstretch1" : "redsquash1";
				else
					category = stretch? "redstretch2" : "redsquash2";
			else
				category = stretch? "yellowstretch" : "yellowsquash";
			return new AxialStretchTool( category + "." + index, this.symmetry, getSelection(), getModel(), stretch, red, first );
		}

		@Override
		protected boolean bindParameters( Selection selection, SymmetrySystem symmSystem )
		{
			Symmetry symmetry = symmSystem .getSymmetry();
			if ( ! ( symmetry instanceof IcosahedralSymmetry ) )
				return false;
			this .symmetry = (IcosahedralSymmetry) symmetry;
        	for ( Manifestation man : selection )
        		if ( man instanceof Strut )
        		{
        			Strut axisStrut = (Strut) man;
        	        AlgebraicVector vector = axisStrut .getOffset();
        	        vector = symmetry .getField() .projectTo3d( vector, true ); // TODO: still necessary?
        	        Axis axis = symmetry .getAxis( vector );
        	        if ( axis == null )
        	        	return false;
        	        String orbitName = axis .getDirection() .getName();
        	        if ( red )
        	        	return orbitName .equals( "red" );
        	        else
        	        	return orbitName .equals( "yellow" );
        		}
			return true;
		}
	}

	private final IcosahedralSymmetry symmetry;
	private boolean stretch;
	private boolean red;
	private boolean first;

	public AxialStretchTool( String name, IcosahedralSymmetry symmetry, Selection selection, RealizedModel realized, boolean stretch, boolean red, boolean first )
    {
        super( name, selection, realized, null, null );
		this .symmetry = symmetry;
		this .stretch = stretch;
		this .red = red;
		this .first = first;
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
			if ( this .red )
				return "A red axis strut must be selected.";
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
			if ( ! this .red )
				return "A yellow axis strut must be selected.";
	    	blueOrbit = symmetry .getDirection( "blue" );
	    	blueScale = blueOrbit .getUnitLength();
        	redOrbit = symmetry .getDirection( "red" );
	    	redScale = redOrbit .getUnitLength();
	    	if ( this .first ) {
	        	o0 = blueOrbit .getAxis( Symmetry.PLUS, 56 ) .normal() .scale( blueScale );
	        	o1 = blueOrbit .getAxis( Symmetry.PLUS, 38 ) .normal() .scale( blueScale );
	        	o2 = blueOrbit .getAxis( Symmetry.PLUS, 40 ) .normal() .scale( blueScale );
	        	n0 = redOrbit .getAxis( Symmetry.PLUS, 46 ) .normal() .scale( redScale );
	        	n1 = redOrbit .getAxis( Symmetry.PLUS, 1 ) .normal() .scale( redScale );
	        	n2 = redOrbit .getAxis( Symmetry.PLUS, 2 ) .normal() .scale( redScale );
	    	} else {
	        	o0 = blueOrbit .getAxis( Symmetry.PLUS, 37 ) .normal() .scale( blueScale );
	        	o1 = blueOrbit .getAxis( Symmetry.PLUS, 25 ) .normal() .scale( blueScale );
	        	o2 = blueOrbit .getAxis( Symmetry.PLUS, 45 ) .normal() .scale( blueScale );
	        	n0 = blueOrbit .getAxis( Symmetry.PLUS, 37 ) .normal() .scale( blueScale );
	        	n1 = blueOrbit .getAxis( Symmetry.PLUS, 25 ) .normal() .scale( blueScale );
	        	redScale = redScale .times( symmetry .getField() .createPower( -1 ) );
	        	n2 = redOrbit .getAxis( Symmetry.PLUS, 45 ) .normal() .scale( redScale );
	    	}
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
    protected void getXmlAttributes( Element element )
    {
    	super .getXmlAttributes( element );
        if ( this .stretch )
            element .setAttribute( "stretch", "true" );
        element .setAttribute( "orbit", this .red? "red" : "yellow" );
        if ( ! this .first )
            element .setAttribute( "first", "false" );
    }

    @Override
    protected void setXmlAttributes( Element element, XmlSaveFormat format ) throws Failure
    {
        String value = element .getAttribute( "stretch" );
        this .stretch = value != null && value .equals( "true" );
        value = element .getAttribute( "orbit" );
        this .red = value .equals( "red" );
        value = element .getAttribute( "first" );
        this .first = value == null || ! value .equals( "false" );;
    	super .setXmlAttributes( element, format );
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
