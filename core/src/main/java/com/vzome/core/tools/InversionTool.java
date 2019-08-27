
//(c) Copyright 2008, Scott Vorthmann.  All rights reserved.

package com.vzome.core.tools;


import com.vzome.core.construction.Point;
import com.vzome.core.construction.PointReflection;
import com.vzome.core.construction.Transformation;
import com.vzome.core.editor.AbstractToolFactory;
import com.vzome.core.editor.Selection;
import com.vzome.core.editor.Tool;
import com.vzome.core.editor.ToolsModel;
import com.vzome.core.model.Connector;
import com.vzome.core.model.Manifestation;

public class InversionTool extends TransformationTool
{	
	private static final String ID = "point reflection";
	private static final String LABEL = "Create a point reflection tool";
	private static final String TOOLTIP = "<p>" +
			    "Each tool duplicates the selection by reflecting<br>" +
			    "each point through the defined center.  To create a<br>" +
			    "tool, select a single ball that defines that center.<br>" +
			"</p>";
	
	public InversionTool( String toolName, ToolsModel tools )
	{
        super( toolName, tools );
	}

	public static class Factory extends AbstractToolFactory
	{
		private transient Connector center;  // TODO use this in createToolInternal()
		
		public Factory( ToolsModel tools )
		{
			super( tools, null, ID, LABEL, TOOLTIP );
		}

		@Override
		protected boolean countsAreValid( int total, int balls, int struts, int panels )
		{
			return ( total == 1 && balls == 1 );
		}

		@Override
		public Tool createToolInternal( String id )
		{
			return new InversionTool( id, getToolsModel() );
		}

		@Override
		protected boolean bindParameters( Selection selection )
		{
			assert selection .size() == 1;
        	for ( Manifestation man : selection )
        		center = (Connector) man;
			return true;
		}
	}

    @Override
    protected String checkSelection( boolean prepareTool )
    {
        Point center = null;
        if ( ! isAutomatic() )
        	for (Manifestation man : mSelection) {
        		if ( prepareTool )
        			unselect( man );
        		if ( man instanceof Connector )
        		{
        			if ( center != null )
        				return "more than one center selected";
        			center = (Point) ((Connector) man) .getConstructions() .next();
        		}
        		else if ( ! prepareTool )
        			return "panel or strut selected";
        	}
        
        if ( center == null ) {
        	if ( prepareTool ) {
        		center = originPoint;
        		this .addParameter( center );
        	}
        	else
        		return "No symmetry center selected";
        }
    
    	if ( prepareTool ) {
    		this .transforms = new Transformation[ 1 ];
    		transforms[ 0 ] = new PointReflection( center );
    	}
        return null;
    }

    @Override
    protected String getXmlElementName()
    {
        return "InversionTool";
    }

    @Override
    public String getCategory()
    {
        return ID;
    }
}
