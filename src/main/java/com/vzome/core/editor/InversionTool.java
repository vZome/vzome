
//(c) Copyright 2008, Scott Vorthmann.  All rights reserved.

package com.vzome.core.editor;

import java.util.Iterator;

import com.vzome.core.commands.Command;
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

    public void perform() throws Command.Failure
    {
        Point center = null;
        if ( ! isAutomatic() )
            for ( Iterator<Manifestation> mans = mSelection .iterator(); mans .hasNext(); ) {
                Manifestation man = mans .next();
                unselect( man );
                if ( man instanceof Connector )
                {
                    if ( center != null )
                        throw new Command.Failure( "more than one center selected" );
                    center = (Point) ((Connector) man) .getConstructions() .next();
                }
            }
        
        if ( center == null )
            center = originPoint;
    
        this .transforms = new Transformation[ 1 ];
        transforms[ 0 ] = new PointReflection( center );

        defineTool();
    }

    protected String getXmlElementName()
    {
        return "InversionTool";
    }

    public String getCategory()
    {
        return "point reflection";
    }

    public String getDefaultName( String baseName )
    {
        return "point reflect through origin";
    }
}
