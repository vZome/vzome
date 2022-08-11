
package com.vzome.core.tools;


import org.w3c.dom.Element;

import com.vzome.core.algebra.AlgebraicField;
import com.vzome.core.algebra.AlgebraicMatrix;
import com.vzome.core.algebra.AlgebraicNumber;
import com.vzome.core.algebra.AlgebraicVector;
import com.vzome.core.commands.Command.Failure;
import com.vzome.core.commands.XmlSaveFormat;
import com.vzome.core.construction.FreePoint;
import com.vzome.core.construction.MatrixTransformation;
import com.vzome.core.construction.Point;
import com.vzome.core.construction.Scaling;
import com.vzome.core.construction.Segment;
import com.vzome.core.construction.SegmentJoiningPoints;
import com.vzome.core.construction.Transformation;
import com.vzome.core.editor.AbstractToolFactory;
import com.vzome.core.editor.Tool;
import com.vzome.core.editor.ToolsModel;
import com.vzome.core.editor.api.Selection;
import com.vzome.core.editor.api.SideEffects;
import com.vzome.core.math.symmetry.Axis;
import com.vzome.core.math.symmetry.Direction;
import com.vzome.core.math.symmetry.Symmetry;
import com.vzome.core.model.Connector;
import com.vzome.core.model.Manifestation;
import com.vzome.core.model.Panel;
import com.vzome.core.model.Strut;

public class ScalingTool extends SymmetryTool
{	
    private static final String ID = "scaling";
    private static final String LABEL = "Create a scaling tool";
    private static final String TOOLTIP = "<p>" +
            "Each tool enlarges or shrinks the selected objects,<br>" +
            "relative to a central point.  To create a tool,<br>" +
            "select a ball representing the central point, and<br>" +
            "two struts from the same orbit (color) with different<br>" +
            "sizes.<br>" +
            "<br>" +
            "The selection order matters.  First select a strut<br>" +
            "that you want to enlarge or shrink, then select a<br>" +
            "strut that has the desired target size.<br>" +
            "</p>";

    public static class Factory extends AbstractToolFactory
    {
        public Factory( ToolsModel tools, Symmetry symmetry )
        {
            super( tools, symmetry, ID, LABEL, TOOLTIP );
        }

        @Override
        protected boolean countsAreValid( int total, int balls, int struts, int panels )
        {
            return ( total == 3 && balls == 1 && struts == 2 );
        }

        @Override
        public Tool createToolInternal( String id )
        {
            ScalingTool tool = new ScalingTool( id, getSymmetry(), this .getToolsModel() );

            int scalePower = 0;;
            switch ( id ) {

            case "scaling.builtin/scale up":
                scalePower = 1;
                break;

            case "scaling.builtin/scale down":
                scalePower = -1;
                break;

            default:
                return tool;  // non-predefined, no scale factor set
            }
            AlgebraicField field = this .getToolsModel() .getEditorModel() .getRealizedModel() .getField();
            tool .setScaleFactor( field .createPower( scalePower ) );
            return tool;
        }

        // This is never called for the predefined tool, so symmetry can be null.
        @Override
        protected boolean bindParameters( Selection selection )
        {
            Symmetry symmetry = getSymmetry();
            AlgebraicVector offset1 = null;
            AlgebraicVector offset2 = null;
            for ( Manifestation man : selection )
                if ( man instanceof Strut ) {
                    Strut strut = (Strut) man;
                    if ( offset1 == null )
                        offset1 = strut .getOffset();
                    else
                        offset2 = strut .getOffset();
                }
            Axis zone1 = symmetry .getAxis( offset1 );
            Axis zone2 = symmetry .getAxis( offset2 );
            if ( zone1 == null || zone2 == null )
                return false;
            Direction orbit1 = zone1 .getDirection();
            Direction orbit2 = zone2 .getDirection();
            if ( orbit1 != orbit2 )
                return false;
            if ( orbit1 .isAutomatic() )
                return false;
            AlgebraicNumber l1 = zone1 .getLength( offset1 );
            AlgebraicNumber l2 = zone2 .getLength( offset2 );
            if ( l1 .equals( l2 ) )
                return false;
            return true;
        }
    }

    private AlgebraicNumber scaleFactor;

    @Override
    public String getCategory()
    {
        return ID;
    }

    public ScalingTool( String id, Symmetry symmetry, ToolsModel tools )
    {
        super( id, symmetry, tools );
        this .scaleFactor = null;
        this .setInputBehaviors( false, true );
    }

    void setScaleFactor( AlgebraicNumber scaleFactor )
    {
        this.scaleFactor = scaleFactor;
    }

    @Override
    protected String checkSelection( boolean prepareTool )
    {
        if ( this .scaleFactor != null ) {
            // a predefined tool; does not use this .symmetry
            AlgebraicField field = this .scaleFactor .getField();
            this .transforms = new Transformation[ 1 ];
            AlgebraicVector column1 = field .basisVector( 3, AlgebraicVector.X ) .scale( this .scaleFactor );
            AlgebraicVector column2 = field .basisVector( 3, AlgebraicVector.Y ) .scale( this .scaleFactor );
            AlgebraicVector column3 = field .basisVector( 3, AlgebraicVector.Z ) .scale( this .scaleFactor );
            Point p1 = new FreePoint( field .basisVector( 3, AlgebraicVector.X ) .scale( field .createPower( 4 ) ) );
            Point p2 = new FreePoint( column2 .scale( field .createPower( 4 ) ) );
            this .addParameter( this .originPoint );
            this .addParameter( new SegmentJoiningPoints( this .originPoint, p1 ) );
            this .addParameter( new SegmentJoiningPoints( this .originPoint, p2 ) );
            AlgebraicMatrix transform = new AlgebraicMatrix( column1, column2, column3 );
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
                center = (Point) ((Connector) man) .getFirstConstruction();
            }
            else if ( man instanceof Strut )
            {
                if ( s2 != null )
                {
                    correct = false;
                    break;
                }
                if ( s1 == null )
                    s1 = (Segment) ((Strut) man) .getFirstConstruction();
                else
                    s2 = (Segment) ((Strut) man) .getFirstConstruction();
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
            SideEffects.logBugAccommodation( "scaling tool serialized with no symmetry; assuming icosahedral" );
        }
        super .setXmlAttributes( element, format );
    }
}
