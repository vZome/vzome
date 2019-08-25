
//(c) Copyright 2008, Scott Vorthmann.  All rights reserved.

package com.vzome.core.tools;


import java.util.EnumSet;

import org.w3c.dom.Element;

import com.vzome.core.algebra.AlgebraicField;
import com.vzome.core.algebra.AlgebraicNumber;
import com.vzome.core.algebra.AlgebraicVector;
import com.vzome.core.commands.Command.Failure;
import com.vzome.core.commands.XmlSaveFormat;
import com.vzome.core.construction.AnchoredSegment;
import com.vzome.core.construction.Point;
import com.vzome.core.construction.Segment;
import com.vzome.core.construction.SegmentEndPoint;
import com.vzome.core.construction.SymmetryTransformation;
import com.vzome.core.construction.Transformation;
import com.vzome.core.editor.AbstractToolFactory;
import com.vzome.core.editor.Selection;
import com.vzome.core.editor.Tool;
import com.vzome.core.editor.ToolsModel;
import com.vzome.core.math.symmetry.Axis;
import com.vzome.core.math.symmetry.Direction;
import com.vzome.core.math.symmetry.Permutation;
import com.vzome.core.math.symmetry.Symmetry;
import com.vzome.core.math.symmetry.Symmetry.SpecialOrbit;
import com.vzome.core.model.Connector;
import com.vzome.core.model.Manifestation;
import com.vzome.core.model.Strut;

public class RotationTool extends SymmetryTool
{	
    private static final String ID = "rotation";
    private static final String LABEL = "Create a rotation tool";
    private static final String TOOLTIP = "<p>" +
            "Each tool rotates the selected objects around an axis<br>" +
            "of symmetry.  To create a tool, select a strut that<br>" +
            "defines that axis.  You can also define the direction<br>" +
            "and center independently, by selecting a ball for the<br>" +
            "center and a strut for the axis.  Note: not all struts<br>" +
            "correspond to rotational symmetries!<br>" +
            "<br>" +
            "The direction of rotation depends on the strut<br>" +
            "orientation, which is hard to discover, but easy to<br>" +
            "control, by dragging out a new strut.<br>" +
            "<br>" +
            "By default, the input selection will be moved to the new,<br>" +
            "rotated orientation.  After creating a tool, you can<br>" +
            "right-click to configure the tool to create a copy, instead.<br>" +
            "</p>";

    public static class Factory extends AbstractToolFactory
    {
        private transient boolean noStrut;

        public Factory( ToolsModel tools, Symmetry symmetry )
        {
            this( tools, symmetry, ID, LABEL, TOOLTIP );
        }

        public Factory( ToolsModel tools, Symmetry symmetry, String id, String label, String tooltip )
        {
            super( tools, symmetry, id, label, tooltip );
        }

        @Override
        protected boolean countsAreValid( int total, int balls, int struts, int panels )
        {
            if ( total == 1 && balls == 1 ) 
            {
                this .noStrut = true;
                return true;
            }
            else if ( ( total == 1 && struts == 1 ) || ( total == 2 && balls == 1 && struts == 1 ) )
            {
                this .noStrut = false;
                return true;
            }
            return false;
        }

        @Override
        public Tool createToolInternal( String id )
        {
            return new RotationTool( id, getSymmetry(), getToolsModel(), false );
        }

        @Override
        protected boolean bindParameters( Selection selection )
        {
            Symmetry symmetry = getSymmetry();
            for ( Manifestation man : selection )
                if ( man instanceof Strut )
                {
                    Strut axisStrut = (Strut) man;
                    AlgebraicVector vector = axisStrut .getOffset();
                    vector = symmetry .getField() .projectTo3d( vector, true ); // TODO: still necessary?
                    Axis axis = symmetry .getAxis( vector );
                    if ( axis == null )
                        return false;
                    Permutation perm = axis .getRotationPermutation();
                    if ( perm == null )
                        return false;
                }
                else if ( this .noStrut )
                {
                    Axis axis = symmetry .getPreferredAxis();
                    if ( axis == null )
                        return false;
                }
            return true;
        }
    }

    private boolean fullRotation, corrected;

    @Override
    public String getCategory()
    {
        if ( this .fullRotation )
            return "axial symmetry";
        else
            return ID;
    }

    public RotationTool( String id, Symmetry symmetry, ToolsModel editor )
    {
        this( id, symmetry, editor, false );
        this .corrected = false; // for backward compatibility... may get overwritten in setXmlAttributes
    }

    public RotationTool( String id, Symmetry symmetry, ToolsModel tools, boolean full )
    {
        super( id, symmetry, tools );
        this .fullRotation = full;
        this .corrected = true;
        if ( full )
            this .setInputBehaviors( EnumSet.of( InputBehaviors.SELECT ) );
        else
            this .setInputBehaviors( EnumSet.of( InputBehaviors.DELETE ) );
        ;
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
            Axis preferredAxis = this .symmetry .getPreferredAxis();
            if ( preferredAxis != null )
            {
                AlgebraicField field = symmetry .getField();
                center = originPoint;
                this .addParameter( center );
                axisStrut = new AnchoredSegment( preferredAxis, field .one(), center );
                this .addParameter( axisStrut );
            }
            else if ( this .isPredefined() )
            {
                center = originPoint;
                this .addParameter( center );
                Direction redOrbit = symmetry .getSpecialOrbit( SpecialOrbit.RED );
                AlgebraicField field = symmetry .getField();
                AlgebraicNumber redScale = redOrbit .getUnitLength() .times( field .createPower( Direction.USER_SCALE ) );
                axisStrut = new AnchoredSegment( redOrbit .getAxis( Symmetry.PLUS, 1 ), redScale, center );
                this .addParameter( axisStrut );
            }
            else if ( isAutomatic() )
            {
                center = originPoint;
                this .addParameter( center );
                AlgebraicField field = symmetry .getField();
                AlgebraicVector zAxis = field .basisVector( 3, AlgebraicVector .Z );
                AlgebraicNumber len = field .createPower( 2 );  // does not matter
                axisStrut = new AnchoredSegment( symmetry .getAxis( zAxis ), len, center );
                this .addParameter( axisStrut );
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
