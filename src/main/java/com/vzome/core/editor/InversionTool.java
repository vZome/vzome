
//(c) Copyright 2008, Scott Vorthmann.  All rights reserved.

package com.vzome.core.editor;


import com.vzome.core.construction.Point;
import com.vzome.core.construction.PointReflection;
import com.vzome.core.construction.Transformation;
import com.vzome.core.model.Connector;
import com.vzome.core.model.Manifestation;
import com.vzome.core.model.RealizedModel;

public class InversionTool extends TransformationTool
{
    public InversionTool( String name, Selection selection, RealizedModel realized, Tool.Registry tools, Point originPoint )
    {
        super( name, selection, realized, tools, originPoint );
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
        return "point reflection";
    }

    @Override
    public String getDefaultName( String baseName )
    {
        return "point reflect through origin";
    }
}
