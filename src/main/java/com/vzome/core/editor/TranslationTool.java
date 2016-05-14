
//(c) Copyright 2008, Scott Vorthmann.  All rights reserved.

package com.vzome.core.editor;


import com.vzome.core.commands.Command;
import com.vzome.core.construction.Point;
import com.vzome.core.construction.PointToPointTranslation;
import com.vzome.core.construction.Transformation;
import com.vzome.core.model.Connector;
import com.vzome.core.model.Manifestation;
import com.vzome.core.model.RealizedModel;

public class TranslationTool extends TransformationTool
{
    public TranslationTool( String name, Selection selection, RealizedModel realized, Tool.Registry tools, Point originPoint )
    {
        super( name, selection, realized, tools, originPoint );
    }
    
    @Override
    public String getDefaultName( String baseName )
    {
        return "translation along X axis";
    }

	@Override
	public boolean isValidForSelection()
	{
		return null == checkSelection( false );
	}

    @Override
    public void perform() throws Command.Failure
    {
    	String error = checkSelection( true );
    	if ( error != null )
    		throw new Command.Failure( error );
    	else
    		defineTool();
	}

    @Override
    protected String checkSelection( boolean prepareTool )
    {
        Point p1 = null, p2 = null;
        boolean correct = true;
        for (Manifestation man : mSelection) {
        	if ( prepareTool )
        		unselect( man );
        	if ( man instanceof Connector )
        	{
        		if ( p2 != null )
        		{
        			correct = false;
        			break;
        		}
        		if ( p1 == null )
        			p1 = (Point) ((Connector) man) .getConstructions() .next();
        		else
        			p2 = (Point) ((Connector) man) .getConstructions() .next();
        	}
        }
        
        if ( p1 == null )
        {
        	correct = false;
        }
        else if ( p2 == null )
        {
            p2 = p1;
            p1 = this.originPoint;
        }

        if ( ! correct )
            return "translation tool requires start and end points, or just an end point";

        if ( prepareTool ) {
            this .transforms = new Transformation[ 1 ];
            transforms[ 0 ] = new PointToPointTranslation( p1, p2 );
        }
        return null;
    }

    @Override
    protected String getXmlElementName()
    {
        return "TranslationTool";
    }

    @Override
    public String getCategory()
    {
        return "translation";
    }
}
