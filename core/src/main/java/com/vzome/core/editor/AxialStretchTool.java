
//(c) Copyright 2008, Scott Vorthmann.  All rights reserved.

package com.vzome.core.editor;


import java.util.EnumSet;

import org.w3c.dom.Element;

import com.vzome.api.Tool.InputBehaviors;
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
import com.vzome.core.model.Strut;

public class AxialStretchTool extends TransformationTool
{
	private static final String TOOLTIP_REDSQUASH1 = "<p>" +
    		"Each tool applies a \"squash\" transformation to the<br>" +
    		"selected objects, compressing along a red axis.  To create<br>" +
    		"a tool, select a ball as the center of the mapping, and a<br>" +
    		"red strut as the direction of the compression.  The ball and<br>" +
    		"strut need not be collinear.<br>" +
    		"<br>" +
    		"The mapping comes from the usual Zome projection of the<br>" +
    		"120-cell.  It is the mapping that transforms the central,<br>" +
    		"blue dodecahedron into the compressed form in the next<br>" +
    		"layer outward.<br>" +
    		"<br>" +
    		"By default, the input selection will be removed, and replaced<br>" +
    		"with the squashed equivalent.  If you want to keep the inputs,<br>" +
    		"you can right-click after creating the tool, to configure it.<br>" +
	    "</p>";
	private static final String TOOLTIP_REDSTRETCH1 = "<p>" +
    		"Each tool applies a \"stretch\" transformation to the<br>" +
    		"selected objects, stretching along a red axis.  To create<br>" +
    		"a tool, select a ball as the center of the mapping, and a<br>" +
    		"red strut as the direction of the stretch.  The ball and<br>" +
    		"strut need not be collinear.<br>" +
    		"<br>" +
    		"The mapping comes from the usual Zome projection of the<br>" +
    		"120-cell.  It is the inverse of the mapping that transforms<br>" +
    		"the central, blue dodecahedron into the compressed form in<br>" +
    		"the next layer outward.<br>" +
    		"<br>" +
    		"By default, the input selection will be removed, and replaced<br>" +
    		"with the stretched equivalent.  If you want to keep the inputs,<br>" +
    		"you can right-click after creating the tool, to configure it.<br>" +
	    "</p>";
    private static final String TOOLTIP_YELLOWSQUASH = "<p>" +
    		"Each tool applies a \"squash\" transformation to the<br>" +
    		"selected objects, compressing along a yellow axis.  To create<br>" +
    		"a tool, select a ball as the center of the mapping, and a<br>" +
    		"yellow strut as the direction of the compression.  The ball and<br>" +
    		"strut need not be collinear.<br>" +
    		"<br>" +
    		"The mapping comes from the usual Zome projection of the<br>" +
    		"120-cell.  It is the mapping that transforms the central,<br>" +
    		"blue dodecahedron into the compressed form along a yellow axis.<br>" +
    		"<br>" +
    		"By default, the input selection will be removed, and replaced<br>" +
    		"with the squashed equivalent.  If you want to keep the inputs,<br>" +
    		"you can right-click after creating the tool, to configure it.<br>" +
    	"</p>";
    private static final String TOOLTIP_YELLOWSTRETCH = "<p>" +
    		"Each tool applies a \"stretch\" transformation to the<br>" +
    		"selected objects, stretching along a yellow axis.  To create<br>" +
    		"a tool, select a ball as the center of the mapping, and a<br>" +
    		"yellow strut as the direction of the stretch.  The ball and<br>" +
    		"strut need not be collinear.<br>" +
    		"<br>" +
    		"The mapping comes from the usual Zome projection of the<br>" +
    		"120-cell.  It is the inverse of the mapping that transforms<br>" +
    		"the central, blue dodecahedron into the compressed form along<br>" +
    		"a yellow axis.<br>" +
    		"<br>" +
    		"By default, the input selection will be removed, and replaced<br>" +
    		"with the stretched equivalent.  If you want to keep the inputs,<br>" +
    		"you can right-click after creating the tool, to configure it.<br>" +
    	"</p>";
    private static final String TOOLTIP_REDSQUASH2 = "<p>" +
    		"Each tool applies a \"squash\" transformation to the<br>" +
    		"selected objects, compressing along a red axis.  To create<br>" +
    		"a tool, select a ball as the center of the mapping, and a<br>" +
    		"red strut as the direction of the compression.  The ball and<br>" +
    		"strut need not be collinear.<br>" +
    		"<br>" +
    		"The mapping comes from the usual Zome projection of the<br>" +
    		"120-cell.  It is the mapping that transforms the central,<br>" +
    		"blue dodecahedron into the compressed form in the second<br>" +
    		"layer outward along a red axis.<br>" +
    		"<br>" +
    		"By default, the input selection will be removed, and replaced<br>" +
    		"with the squashed equivalent.  If you want to keep the inputs,<br>" +
    		"you can right-click after creating the tool, to configure it.<br>" +
    	"</p>";
    private static final String TOOLTIP_REDSTRETCH2 = "<p>" +
    		"Each tool applies a \"stretch\" transformation to the<br>" +
    		"selected objects, stretching along a red axis.  To create<br>" +
    		"a tool, select a ball as the center of the mapping, and a<br>" +
    		"red strut as the direction of the stretch.  The ball and<br>" +
    		"strut need not be collinear.<br>" +
    		"<br>" +
    		"The mapping comes from the usual Zome projection of the<br>" +
    		"120-cell.  It is the inverse of the mapping that transforms<br>" +
    		"the central, blue dodecahedron into the compressed form in<br>" +
    		"the second layer outward along a red axis.<br>" +
    		"<br>" +
    		"By default, the input selection will be removed, and replaced<br>" +
    		"with the stretched equivalent.  If you want to keep the inputs,<br>" +
    		"you can right-click after creating the tool, to configure it.<br>" +
        "</p>";

	public static class Factory extends AbstractToolFactory
	{
		private final boolean red, stretch, first;

		private static String getCategory( boolean red, boolean stretch, boolean first )
		{
			if ( red )
				if ( first )
					return stretch? "redstretch1" : "redsquash1";
				else
					return stretch? "redstretch2" : "redsquash2";
			else
				return stretch? "yellowstretch" : "yellowsquash";
		}
		        
		private static String getLabel( boolean red, boolean stretch, boolean first )
		{
			String label;
			if ( red )
				if ( first )
					label = stretch? "weak red stretch" : "weak red squash";
				else
					label = stretch? "strong red stretch" : "strong red squash";
			else
				label = stretch? "yellow stretch" : "yellow squash";
			return "Create a " + label + " tool";
		}

		private static String getToolTip( boolean red, boolean stretch, boolean first )
		{
			if ( red )
				if ( first )
					return stretch? TOOLTIP_REDSTRETCH1 : TOOLTIP_REDSQUASH1;
				else
					return stretch? TOOLTIP_REDSTRETCH2 : TOOLTIP_REDSQUASH2;
			else
				return stretch? TOOLTIP_YELLOWSTRETCH : TOOLTIP_YELLOWSQUASH;
		}

		public Factory( ToolsModel tools, IcosahedralSymmetry symmetry, boolean red, boolean stretch, boolean first )
		{
			super( tools, symmetry, getCategory( red, stretch, first ), getLabel( red, stretch, first ), getToolTip( red, stretch, first ) );
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
		public Tool createToolInternal( String id )
		{
			String category = id .substring( 0, id .indexOf( "." ) );
			return new AxialStretchTool( id, (IcosahedralSymmetry) getSymmetry(), getToolsModel(), stretch, red, first, category );
		}

		@Override
		protected boolean bindParameters( Selection selection )
		{
			IcosahedralSymmetry symmetry = (IcosahedralSymmetry) getSymmetry();
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

	private IcosahedralSymmetry symmetry;
	private boolean stretch;
	private boolean red;
	private boolean first;
	private final String category;

	public AxialStretchTool( String id, IcosahedralSymmetry symmetry, ToolsModel tools, boolean stretch, boolean red, boolean first, String category )
    {
        super( id, tools );
		this .symmetry = symmetry;
		this .stretch = stretch;
		this .red = red;
		this .first = first;
		this .category = category;
    }

	@Override
	public EnumSet<InputBehaviors> defaultInputBehaviors()
	{
		return EnumSet.of( InputBehaviors.DELETE );
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

        String symmName = element .getAttribute( "symmetry" );
        this .symmetry = (IcosahedralSymmetry) format .parseSymmetry( "icosahedral" );
        super .setXmlAttributes( element, format );
    }

    @Override
    public String getCategory()
    {
        return this .category;
    }
}
