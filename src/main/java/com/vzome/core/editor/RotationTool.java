
//(c) Copyright 2008, Scott Vorthmann.  All rights reserved.

package com.vzome.core.editor;


import org.w3c.dom.Element;

import com.vzome.core.algebra.AlgebraicField;
import com.vzome.core.algebra.AlgebraicNumber;
import com.vzome.core.algebra.AlgebraicVector;
import com.vzome.core.commands.XmlSaveFormat;
import com.vzome.core.commands.Command.Failure;
import com.vzome.core.construction.AnchoredSegment;
import com.vzome.core.construction.Point;
import com.vzome.core.construction.Segment;
import com.vzome.core.construction.SegmentEndPoint;
import com.vzome.core.construction.SymmetryTransformation;
import com.vzome.core.construction.Transformation;
import com.vzome.core.math.symmetry.Axis;
import com.vzome.core.math.symmetry.Direction;
import com.vzome.core.math.symmetry.Permutation;
import com.vzome.core.math.symmetry.Symmetry;
import com.vzome.core.model.Connector;
import com.vzome.core.model.Manifestation;
import com.vzome.core.model.RealizedModel;
import com.vzome.core.model.Strut;

public class RotationTool extends SymmetryTool
{	
	public static class Factory extends AbstractToolFactory implements ToolFactory
	{
		private transient Symmetry symmetry;
		
		public Factory( EditorModel model, UndoableEdit.Context context )
		{
			super( model, context );
		}

		@Override
		protected boolean countsAreValid( int total, int balls, int struts, int panels )
		{
			return ( total == 1 && struts == 1 ) || ( total == 2 && balls == 1 && struts == 1 );
		}

		@Override
		public Tool createToolInternal( int index )
		{
			return new RotationTool( "rotation." + index, this.symmetry, getSelection(), getModel(), null, false );
		}
		
		protected Symmetry getSymmetry()
		{
			return this .symmetry;
		}

		@Override
		protected boolean bindParameters( Selection selection, SymmetrySystem symmetry )
		{
			this .symmetry = symmetry .getSymmetry();
        	for ( Manifestation man : selection )
        		if ( man instanceof Strut )
        		{
        			Strut axisStrut = (Strut) man;
        	        AlgebraicVector vector = axisStrut .getOffset();
        	        vector = this .symmetry .getField() .projectTo3d( vector, true ); // TODO: still necessary?
        	        Axis axis = symmetry .getAxis( vector );
        	        if ( axis == null )
        	        	return false;
        			Permutation perm = axis .getRotationPermutation();
        	        if ( perm == null )
        	        	return false;
        		}
			return true;
		}
	}

    private boolean fullRotation, corrected;

	public String getDefaultName()
    {
        return "rotation around Z axis";
    }

    @Override
    public String getCategory()
    {
        return "rotation";
    }

    /**
     * Create a RotationTool while loading a saved model.
     * @param name
     * @param symmetry
     * @param selection
     * @param realized
     * @param tools
     * @param originPoint
     */
    public RotationTool( String name, Symmetry symmetry, Selection selection, RealizedModel realized, Point originPoint )
    {
    	this( name, symmetry, selection, realized, originPoint, false );
    	this .corrected = false; // for backward compatibility... may get overwritten in setXmlAttributes
    }

    /**
     * Create a new RotationTool.
     * @param name
     * @param symmetry
     * @param selection
     * @param realized
     * @param tools
     * @param originPoint
     * @param full
     */
    public RotationTool( String name, Symmetry symmetry, Selection selection, RealizedModel realized, Point originPoint, boolean full )
    {
        super( name, symmetry, selection, realized, null, originPoint );
        this .fullRotation = full;
        this .corrected = true;
    }

    @Override
    protected String checkSelection( boolean prepareTool )
    {
        Point center = null;
        Segment axisStrut = null;
        boolean correct = true;
        if ( ! isAutomatic() )
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
            if ( this .getName() .contains( ".builtin/" ) ) { // TODO: fix this lame mechanism; use an API
                center = originPoint;
        		this .addParameter( center );
            	Direction redOrbit = symmetry .getDirection( "red" );
                AlgebraicField field = symmetry .getField();
    	    	AlgebraicNumber redScale = redOrbit .getUnitLength() .times( field .createPower( Direction.USER_SCALE ) );
                axisStrut = new AnchoredSegment( redOrbit .getAxis( Symmetry.PLUS, 1 ), redScale, center );
        		this .addParameter( axisStrut );
            }
            else if ( isAutomatic() )
            {
                center = originPoint;
                AlgebraicField field = symmetry .getField();
                AlgebraicVector zAxis = field .basisVector( 3, AlgebraicVector .Z );
                AlgebraicNumber len = field .createPower( 2 );  // does not matter
                axisStrut = new AnchoredSegment( symmetry .getAxis( zAxis ), len, center );
            }
            else
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
		Permutation perm = axis .getRotationPermutation();
        if ( perm == null )
        	return "selected strut is not an axis of rotation";
		// This can correct for the very old bug in Direction.createAxis() for negative axes.
        int rotation = this.corrected? perm .mapIndex( 0 ) : axis .getRotation();
    	if ( prepareTool ) {
    		if ( this .fullRotation ) {
    			int order = perm .getOrder();
        		this .transforms = new Transformation[ order - 1 ];
    			for (int i = 0; i < transforms.length; i++) {
					transforms[ i ] = new SymmetryTransformation( symmetry, rotation, center );
					rotation = perm .mapIndex( rotation );
				}
    		} else {
        		this .transforms = new Transformation[ 1 ];
        		transforms[ 0 ] = new SymmetryTransformation( symmetry, rotation, center );
    		}
    	}
    	return null;
    }

    @Override
    protected String getXmlElementName()
    {
        return "RotationTool";
    }

    @Override
    protected void getXmlAttributes( Element element )
    {
    	if ( this .fullRotation )
    		element .setAttribute( "full", "true" );
    	if ( this .corrected )
    		element .setAttribute( "corrected", "true" );
        super .getXmlAttributes( element );
    }

    @Override
    protected void setXmlAttributes( Element element, XmlSaveFormat format ) throws Failure
    {
        String value = element .getAttribute( "full" );
        this .fullRotation = ( value != null ) && "true" .equals( value );
        value = element .getAttribute( "corrected" );
        this .corrected = ( value != null ) && "true" .equals( value );
        super .setXmlAttributes( element, format );
    }
}
