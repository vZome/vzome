
package com.vzome.core.tools;


import com.vzome.core.algebra.AlgebraicVector;
import com.vzome.core.commands.Command;
import com.vzome.core.construction.Line;
import com.vzome.core.construction.LineExtensionOfSegment;
import com.vzome.core.construction.Plane;
import com.vzome.core.construction.PlaneExtensionOfPolygon;
import com.vzome.core.construction.PlaneFromPointAndNormal;
import com.vzome.core.construction.PlaneProjection;
import com.vzome.core.construction.Polygon;
import com.vzome.core.construction.Segment;
import com.vzome.core.construction.Transformation;
import com.vzome.core.editor.AbstractToolFactory;
import com.vzome.core.editor.Tool;
import com.vzome.core.editor.ToolsModel;
import com.vzome.core.editor.api.Selection;
import com.vzome.core.model.Connector;
import com.vzome.core.model.Manifestation;
import com.vzome.core.model.Panel;
import com.vzome.core.model.Strut;

public class ProjectionTool extends TransformationTool
{
	private static final String ID = "projection";
	private static final String LABEL = "Create a plane projection tool";
	private static final String TOOLTIP = "<p>" +
	    		"Created tools project selected objects to a 2D plane.<br>"
                + "<br>"
                + "To create a tool, define the projection plane<br>" 
                + " by selecting either a single panel<br>"
                + " or strut that is normal to the projection plane<br>"
                + " and a ball on the plane.<br>"
	    		+ "When the projection plane is defined by selecting a panel,<br>"
                +"  an optional strut may be selected to define the line of projection.<br>"
                + "The default line of projection is orthogonal to the projection plane.<br>"
                + "</p>";
	
	public static class Factory extends AbstractToolFactory
	{
		public Factory( ToolsModel tools )
		{
			super( tools, null, ID, LABEL, TOOLTIP );
		}

		@Override
		protected boolean countsAreValid( int total, int balls, int struts, int panels )
		{
			return ( total == 2 && panels == 1 && struts == 1 )
	                || ( total == 1 && panels == 1 )
	                || ( total == 2 && balls == 1 && struts == 1 );
		}

		@Override
		public Tool createToolInternal( String id )
		{
			return new ProjectionTool( id, getToolsModel() );
		}

        @Override
        public Tool createTool()
        {
            Tool result = super.createTool();
            result .setCopyColors( false ); // Overriding true default, only for newly created tools
            return result;
        }

		@Override
		protected boolean bindParameters( Selection selection )
		{
			return true;
		}
	}

	public ProjectionTool( String id, ToolsModel tools )
    {
        super( id, tools );
        this .setInputBehaviors( false, true );
    }

    @Override
    public void perform() throws Command.Failure
    {
        Plane plane = null;
        Line line = null;
        AlgebraicVector point = null;
        for (Manifestation man : mSelection) {
            unselect(man);
            if (man instanceof Panel) {
                if (plane == null) {
                    Panel panel = (Panel) man;
                    Polygon polygon = (Polygon) panel.toConstruction();
                    plane = new PlaneExtensionOfPolygon(polygon);
                } else {
                    throw new Command.Failure("Projection tool allows only a single selected panel");
                }
            }
            if (man instanceof Strut) {
                if (line == null) {
                    Strut strut = (Strut) man;
                    Segment segment = (Segment) strut.toConstruction();
                    line = new LineExtensionOfSegment(segment);
                } else {
                    throw new Command.Failure("Projection tool allows only a single selected strut");
                }
            }
            if (man instanceof Connector) {
                if (point == null) {
                    point = man.getLocation();
                    continue;
                } else {
                    throw new Command.Failure("Projection tool allows only a single selected ball");
                }
            }
        }

        if (point != null && line != null) {
            plane = new PlaneFromPointAndNormal(point, line.getDirection());
        }

        if (plane == null) {
            throw new Command.Failure("Projection tool requires a selected panel or else a selected ball and strut.");
        }

        this .transforms = new Transformation[ 1 ];
        transforms[ 0 ] = new PlaneProjection( plane, line );
        
        if ( line != null ) {
            AlgebraicVector test = transforms[ 0 ] .transform( line .getDirection() );
            if ( test == null )
                throw new Command.Failure( "Selected strut and plane must not be parallel" );
        }

        super .perform();
    }

    @Override
    protected String checkSelection( boolean prepareTool )
    {
        return null;
    }

    @Override
    protected String getXmlElementName()
    {
        return "ProjectionTool";
    }

    @Override
    public String getCategory()
    {
        return ID;
    }
}
