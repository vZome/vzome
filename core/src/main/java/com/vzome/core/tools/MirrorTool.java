
package com.vzome.core.tools;


import com.vzome.core.algebra.AlgebraicField;
import com.vzome.core.algebra.AlgebraicVector;
import com.vzome.core.construction.FreePoint;
import com.vzome.core.construction.Plane;
import com.vzome.core.construction.PlaneExtensionOfPolygon;
import com.vzome.core.construction.PlaneFromNormalSegment;
import com.vzome.core.construction.PlaneReflection;
import com.vzome.core.construction.Point;
import com.vzome.core.construction.Polygon;
import com.vzome.core.construction.Segment;
import com.vzome.core.construction.SegmentJoiningPoints;
import com.vzome.core.construction.Transformation;
import com.vzome.core.editor.AbstractToolFactory;
import com.vzome.core.editor.Tool;
import com.vzome.core.editor.ToolsModel;
import com.vzome.core.editor.api.OrbitSource;
import com.vzome.core.editor.api.Selection;
import com.vzome.core.editor.api.SymmetryAware;
import com.vzome.core.math.symmetry.Direction;
import com.vzome.core.math.symmetry.SpecialOrbit;
import com.vzome.core.math.symmetry.Symmetry;
import com.vzome.core.model.Connector;
import com.vzome.core.model.Manifestation;
import com.vzome.core.model.Panel;
import com.vzome.core.model.Strut;

public class MirrorTool extends TransformationTool
{
    private static final String ID = "mirror";
    private static final String LABEL = "Create a mirror reflection tool";
    private static final String TOOLTIP = "<p>" +
            "Each tool duplicates the selection by reflecting<br>" +
            "each object in a mirror plane.  To create a<br>" +
            "tool, define the mirror plane by selecting a single<br>" +
            "panel, or by selecting a strut orthogonal to the<br>" +
            "plane and a ball lying in the plane.<br>" +
            "</p>";

    public static class Factory extends AbstractToolFactory
    {
        public Factory( ToolsModel tools )
        {
            super( tools, null, ID, LABEL, TOOLTIP );
        }

        @Override
        protected boolean countsAreValid( int total, int balls, int struts, int panels )
        {
            return ( total == 2 && balls == 1 && struts == 1 )
                    || ( total == 1 && panels == 1 );
        }

        @Override
        public Tool createToolInternal( String id )
        {
            return new MirrorTool( id, getToolsModel() );
        }

        @Override
        protected boolean bindParameters( Selection selection )
        {
            return true;
        }
    }

    protected final OrbitSource symmSys;
    
    public MirrorTool( String id, ToolsModel tools )
    {
        super( id, tools );
        // symmSys may be null for some test cases, but shouldn't be otherwise
        symmSys = ((SymmetryAware) tools.getEditorModel()).getSymmetrySystem();
    }

    @Override
    protected String checkSelection( boolean prepareTool )
    {
        Point center = null;
        Segment axis = null;
        Polygon mirrorPanel = null;
        if ( this .getId() .equals( "mirror.builtin/reflection through XY plane" ) )
        {
            center = originPoint;
            this .addParameter( center );
            AlgebraicField field = originPoint .getField();
            AlgebraicVector zAxis = field .basisVector( 3, AlgebraicVector .Z ) .scale( field .createPower( Direction.USER_SCALE ) );
            Point p2 = new FreePoint( zAxis );
            axis = new SegmentJoiningPoints( center, p2 );
            this .addParameter( axis );
        }
        else if ( this .getId() .equals( "mirror.builtin/reflection through red plane" ) )
        {
            center = originPoint;
            this .addParameter( center );
            // this is intended to be used by antiprism symmetries which only have one red axis
            // and it's not the XY plane. Initially, that's only the SqrtPhiField.
            AlgebraicVector redAxis = symmSys.getSymmetry().getSpecialOrbit(SpecialOrbit.RED).getAxis(Symmetry.PLUS, 0).normal();
            Point p2 = new FreePoint( redAxis );
            axis = new SegmentJoiningPoints( center, p2 );
            this .addParameter( axis );
        }
        else if ( isAutomatic() )
        {
            center = originPoint;
            AlgebraicField field = originPoint .getField();
            AlgebraicVector xAxis = field .basisVector( 3, AlgebraicVector .X );
            Point p2 = new FreePoint( xAxis );
            axis = new SegmentJoiningPoints( center, p2 );
        }
        else
            for (Manifestation man : mSelection) {
                if ( prepareTool )
                    unselect( man );
                // The legacy validation / binding is associated with prepareTool,
                //  so that old files will open.  Apparently, the first of many
                //  balls, panels, or struts was used for the parameter, though I
                //  doubt that anyone ever used it this way.
                if ( man instanceof Connector )
                {
                    if ( center != null )
                    {
                        if ( prepareTool )
                            break;
                        else
                            return "Only one center ball may be selected";
                    }
                    center = (Point) ((Connector) man) .getFirstConstruction();
                }
                else if ( man instanceof Strut )
                {
                    if ( axis != null )
                    {
                        if ( prepareTool )
                            break;
                        else
                            return "Only one mirror axis strut may be selected";
                    }
                    axis = (Segment) ((Strut) man) .getFirstConstruction();
                }
                else if ( man instanceof Panel )
                {
                    if ( mirrorPanel != null )
                    {
                        if ( prepareTool )
                            break;
                        else
                            return "Only one mirror panel may be selected";
                    }
                    mirrorPanel = (Polygon) ((Panel) man) .getFirstConstruction();
                }
            }
        if ( center == null ) {
            if ( prepareTool ) // after validation, or when loading from a file
                center = originPoint;
            else if ( mirrorPanel == null ) // just validating the selection, not really creating a tool
                return "No symmetry center selected";
        }

        Plane mirrorPlane = null;
        if ( axis != null && center != null && mirrorPanel == null ) {
            if ( prepareTool )
                mirrorPlane = new PlaneFromNormalSegment( center, axis );
        }
        else if ( axis == null && mirrorPanel != null ) {
            if ( prepareTool )
                mirrorPlane = new PlaneExtensionOfPolygon( mirrorPanel );
            else if ( center != null )
                return "mirror tool requires a single panel,\n"
                + "or a single strut and a single center ball";
        }
        else {
            String msg = "mirror tool requires a single panel,\n"
            + "or a single strut and a single center ball";
            if ( prepareTool ) {
                // tool will fail when it's used,
                // so let's fail now to avoid finding the bug later.
                throw new IllegalStateException("Failed to prepare tool: " + msg);
            } else {
                return msg;
            }
        }

        if ( prepareTool ) {
            this .transforms = new Transformation[ 1 ];
            transforms[ 0 ] = new PlaneReflection( mirrorPlane );
        }

        return null;
    }

    @Override
    protected String getXmlElementName()
    {
        return "MirrorTool";
    }

    @Override
    public String getCategory()
    {
        return ID;
    }
}
