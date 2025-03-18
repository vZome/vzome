
package com.vzome.core.tools;


import com.vzome.core.commands.Command;
import com.vzome.core.construction.PerspectiveProjection;
import com.vzome.core.construction.Plane;
import com.vzome.core.construction.PlaneExtensionOfPolygon;
import com.vzome.core.construction.Point;
import com.vzome.core.construction.Polygon;
import com.vzome.core.construction.Transformation;
import com.vzome.core.editor.ToolsModel;
import com.vzome.core.model.Connector;
import com.vzome.core.model.Manifestation;
import com.vzome.core.model.Panel;

public class PerspectiveProjectionTool extends TransformationTool
{
	static final String ID = "perspective";
	static final String LABEL = "Create a perspective projection tool";
	static final String TOOLTIP = "<p>" +
	    		"Created tools project selected objects to a 2D plane.<br>"
                + "<br>"
                + "To create a tool, define the projection<br>" 
                + " by selecting a single panel<br>"
                + " and a ball not in the plane of the panel."
                + "</p>";
	
	public PerspectiveProjectionTool( String id, ToolsModel tools )
    {
        super( id, tools );
        this .setInputBehaviors( false, true );
    }

    @Override
    public void perform() throws Command.Failure
    {
        Plane plane = null;
        Point point = null;
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
            if (man instanceof Connector) {
                if (point == null) {
                    Connector ball = (Connector) man;
                    point = (Point) ball .toConstruction();
                    continue;
                } else {
                    throw new Command.Failure("Projection tool allows only a single selected ball");
                }
            }
        }

        if (plane == null || point == null ) {
            throw new Command.Failure("Projection tool requires a selected panel and a selected ball.");
        }

        this .transforms = new Transformation[ 1 ];
        transforms[ 0 ] = new PerspectiveProjection( plane, point );
        
//        AlgebraicVector test = transforms[ 0 ] .transform( line .getDirection() );
//        if ( test == null )
//            throw new Command.Failure( "Selected strut and plane must not be parallel" );

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
        return "PerspectiveProjectionTool";
    }

    @Override
    public String getCategory()
    {
        return ID;
    }
}
