
package com.vzome.core.tools;


import com.vzome.core.construction.Point;
import com.vzome.core.construction.PointReflection;
import com.vzome.core.construction.Transformation;
import com.vzome.core.editor.ToolsModel;
import com.vzome.core.model.Connector;
import com.vzome.core.model.Manifestation;

public class InversionTool extends TransformationTool
{	
	static final String ID = "point reflection";
	static final String LABEL = "Create a point reflection tool";
	static final String TOOLTIP = "<p>" +
			    "Each tool duplicates the selection by reflecting<br>" +
			    "each point through the defined center.  To create a<br>" +
			    "tool, select a single ball that defines that center.<br>" +
			"</p>";
	
	public InversionTool( String toolName, ToolsModel tools )
	{
        super( toolName, tools );
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
        			center = (Point) ((Connector) man) .getFirstConstruction();
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
