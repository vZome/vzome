package com.vzome.core.tools;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;

import com.vzome.core.algebra.AlgebraicVector;
import com.vzome.core.algebra.AlgebraicVectors;
import com.vzome.core.commands.Command;
import com.vzome.core.construction.PerspectiveProjection;
import com.vzome.core.construction.Point;
import com.vzome.core.construction.Polygon;
import com.vzome.core.construction.Transformation;
import com.vzome.core.editor.AbstractToolFactory;
import com.vzome.core.editor.Selection;
import com.vzome.core.editor.Tool;
import com.vzome.core.editor.ToolsModel;
import com.vzome.core.model.Connector;
import com.vzome.core.model.Manifestation;
import com.vzome.core.model.Panel;

public class PerspectiveProjectionTool extends TransformationTool {
    private static final String ID = "perspective projection"; // this is the name of the png used for the tool bars.
    private static final String LABEL = "Create a perspective projection tool";
    private static final String TOOLTIP = "<p>" +
                "Created tools apply perspective projection<br>"
                + "to selected objects, projecting them onto a 2D plane<br>"
                + "from a focal point not on that plane.<br>"
                + "If any point on a part is at the focal point<br>"
                + "or would be projected to infinity, the entire part is ignored.<br>"
                + "To create a tool, select a panel as the projection plane and <br>"
                + "and a non-coplanar ball to be the focal point.<br>" +
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
            return ( total == 2 && panels == 1 && balls == 1 );
        }

        @Override
        public Tool createToolInternal( String id )
        {
            return new PerspectiveProjectionTool( id, getToolsModel() );
        }

        @Override
        protected boolean bindParameters( Selection selection )
        {
            return true;
        }
    }

    public PerspectiveProjectionTool(String id, ToolsModel tools) {
        super(id, tools);
        this .setInputBehaviors( EnumSet.of( InputBehaviors.DELETE ) );
    }

    @Override
    public void perform() throws Command.Failure
    {
        AlgebraicVector focalPoint = null;
        List<AlgebraicVector> points = new ArrayList<>();
        Panel panel = null;
        Connector ball = null;
        // since countsAreValid() we know we have exactly one ball and one panel selected
        for (Manifestation man : mSelection) {
            unselect( man );
            if ( man instanceof Panel ) {
                panel = (Panel) man;
                for(AlgebraicVector v : panel) {
                    points.add(v);
                }
            } else {
                // must be the ball
                ball = (Connector) man;
                focalPoint = man.getLocation();
            }
        }

        String errMsg = "The ball must not be coplanar with the panel.";
        if(points.contains(focalPoint) ) {
            throw new Command.Failure( errMsg );
        }
        points.add(focalPoint);
        if(AlgebraicVectors.areCoplanar(points)) {
            throw new Command.Failure( errMsg );
        }
        
        this .transforms = new Transformation[ ] { 
            new PerspectiveProjection( 
                (Polygon) panel.getFirstConstruction(), 
                (Point) ball.getFirstConstruction() ) 
        };
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
