
package com.vzome.core.tools;

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
import com.vzome.core.editor.ToolsModel;
import com.vzome.core.math.symmetry.Axis;
import com.vzome.core.math.symmetry.Direction;
import com.vzome.core.math.symmetry.Permutation;
import com.vzome.core.math.symmetry.SpecialOrbit;
import com.vzome.core.math.symmetry.Symmetry;
import com.vzome.core.model.Connector;
import com.vzome.core.model.Manifestation;
import com.vzome.core.model.Strut;

public class RotationTool extends SymmetryTool
{	
    private boolean fullRotation, corrected;

    @Override
    public String getCategory()
    {
        if ( this .fullRotation )
            return "axial symmetry";
        else
            return RotationToolFactory.ID;
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
            this .setInputBehaviors( true, false );
        else
            this .setInputBehaviors( false, true );
        ;
    }

    @Override
    protected String checkSelection( boolean prepareTool )
    {
        Point center = null;
        Segment axisStrut = null;
        boolean correct = true;
        Axis rotationZone = null;
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
                center = (Point) ((Connector) man) .getFirstConstruction();
            }
            else if ( man instanceof Strut )
            {
                if ( axisStrut != null )
                {
                    correct = false;
                    break;
                }
                axisStrut = (Segment) ((Strut) man) .getFirstConstruction();
                AlgebraicVector vector = axisStrut .getOffset();
                vector = axisStrut .getField() .projectTo3d( vector, true );
                rotationZone = symmetry .getAxis( vector );
            }
        }

        if ( axisStrut == null )
        {
            rotationZone = this .symmetry .getPreferredAxis();
            if ( rotationZone != null )
            {
                AlgebraicField field = symmetry .getField();
                center = originPoint;
                this .addParameter( center );
                axisStrut = new AnchoredSegment( rotationZone, field .one(), center );
                this .addParameter( axisStrut );
            }
            else if ( this .isPredefined() )
            {
                center = originPoint;
                this .addParameter( center );
                Direction redOrbit = symmetry .getSpecialOrbit( SpecialOrbit.RED );
                rotationZone = redOrbit .getAxis( Symmetry.PLUS, 1 );
                AlgebraicField field = symmetry .getField();
                AlgebraicNumber redScale = redOrbit .getUnitLength() .times( field .createPower( Direction.USER_SCALE ) );
                axisStrut = new AnchoredSegment( rotationZone, redScale, center );
                this .addParameter( axisStrut );
            }
            else if ( isAutomatic() )
            {
                center = originPoint;
                this .addParameter( center );
                AlgebraicField field = symmetry .getField();
                AlgebraicVector zAxis = field .basisVector( 3, AlgebraicVector .Z );
                AlgebraicNumber len = field .createPower( 2 );  // does not matter
                rotationZone = symmetry .getAxis( zAxis );
                axisStrut = new AnchoredSegment( rotationZone, len, center );
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

        if ( rotationZone == null )
            return "selected strut is not an axis of rotation";
        Permutation perm = rotationZone .getRotationPermutation();
        if ( perm == null )
            return "selected strut is not an axis of rotation";
        // This can correct for the very old bug in Direction.createAxis() for negative axes.
        int rotation = this.corrected? perm .mapIndex( 0 ) : rotationZone .getRotation();
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
