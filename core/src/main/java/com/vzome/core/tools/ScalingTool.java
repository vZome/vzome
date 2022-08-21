
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
import com.vzome.core.editor.ToolsModel;
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
    static final String ID = "scaling";
    static final String LABEL = "Create a scaling tool";
    static final String TOOLTIP = "<p>" +
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
