
//(c) Copyright 2008, Scott Vorthmann.  All rights reserved.

package com.vzome.core.tools;


import java.util.EnumSet;

import com.vzome.core.algebra.AlgebraicVector;
import com.vzome.core.commands.Command;
import com.vzome.core.construction.Line;
import com.vzome.core.construction.LineExtensionOfSegment;
import com.vzome.core.construction.Plane;
import com.vzome.core.construction.PlaneExtensionOfPolygon;
import com.vzome.core.construction.PlaneProjection;
import com.vzome.core.construction.Polygon;
import com.vzome.core.construction.Segment;
import com.vzome.core.construction.Transformation;
import com.vzome.core.editor.AbstractToolFactory;
import com.vzome.core.editor.Tool;
import com.vzome.core.editor.ToolsModel;
import com.vzome.core.editor.api.Selection;
import com.vzome.core.model.Manifestation;
import com.vzome.core.model.Panel;
import com.vzome.core.model.Strut;

public class ProjectionTool extends TransformationTool
{
	private static final String ID = "projection";
	private static final String LABEL = "Create a plane projection tool";
	private static final String TOOLTIP = "<p>" +
	    		"Created tools project selected objects to a 2D plane.  To create a<br>" +
	    		"tool, define the projection plane by selecting a single plane.<br>"
	    		+ "Optionally, select a strut to define a line of projection;<br>"
	    		+ "the default line is orthogonal to the projection plane.<br>" +
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
			return ( total == 2 && panels == 1 && struts == 1 )
				|| ( total == 1 && panels == 1 );
		}

		@Override
		public Tool createToolInternal( String id )
		{
			return new ProjectionTool( id, getToolsModel() );
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
        this .setInputBehaviors( EnumSet.of( InputBehaviors.DELETE ) );
    }

    @Override
    public void perform() throws Command.Failure
    {
        Plane plane = null;
        Line line = null;
        for (Manifestation man : mSelection) {
            unselect( man );
            if ( man instanceof Panel )
            {
                if ( plane == null )
                {
                    Panel panel = (Panel) man; 
                    Polygon polygon = (Polygon) panel .toConstruction();
                    plane = new PlaneExtensionOfPolygon( polygon ) ;
                    continue;
                }
                else
                {
                    throw new Command.Failure( "Projection tool requires a single selected panel" );
                }
            }
            if ( man instanceof Strut )
            {
                if ( line == null )
                {
                    Strut strut = (Strut) man;
                    Segment segment = (Segment) strut .toConstruction();
                    line = new LineExtensionOfSegment( segment ) ;
                    continue;
                }
                else
                {
                    throw new Command.Failure( "Projection tool allows only a single selected strut" );
                }
            }
        }

        if ( plane == null )
            throw new Command.Failure( "Projection tool requires a selected panel" );

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
