
//(c) Copyright 2008, Scott Vorthmann.  All rights reserved.

package com.vzome.core.editor;


import com.vzome.core.construction.Plane;
import com.vzome.core.construction.PlaneExtensionOfPolygon;
import com.vzome.core.construction.PlaneFromNormalSegment;
import com.vzome.core.construction.PlaneReflection;
import com.vzome.core.construction.Point;
import com.vzome.core.construction.Polygon;
import com.vzome.core.construction.Segment;
import com.vzome.core.construction.Transformation;
import com.vzome.core.model.Connector;
import com.vzome.core.model.Manifestation;
import com.vzome.core.model.Panel;
import com.vzome.core.model.RealizedModel;
import com.vzome.core.model.Strut;

public class MirrorTool extends TransformationTool
{
	public MirrorTool( String name, Selection selection, RealizedModel realized, Tool.Registry tools, Point originPoint )
    {
        super( name, selection, realized, tools, originPoint );
    }

    @Override
    protected String checkSelection( boolean prepareTool )
    {
        Point center = null;
        Segment axis = null;
        Polygon mirrorPanel = null;
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
        		center = (Point) ((Connector) man) .getConstructions() .next();
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
        		axis = (Segment) ((Strut) man) .getConstructions() .next();
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
        		mirrorPanel = (Polygon) ((Panel) man) .getConstructions() .next();
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
        else
            return "mirror tool requires a single panel,\n"
            + "or a single strut and a single center ball";
    
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
        return "mirror";
    }

    @Override
    public String getDefaultName( String baseName )
    {
        return "reflect through X axis";
    }
}
