
//(c) Copyright 2008, Scott Vorthmann.  All rights reserved.

package com.vzome.core.editor;

import java.util.Iterator;

import com.vzome.core.commands.Command;
import com.vzome.core.construction.ChangeOfBasis;
import com.vzome.core.construction.Point;
import com.vzome.core.construction.Segment;
import com.vzome.core.construction.Transformation;
import com.vzome.core.model.Connector;
import com.vzome.core.model.Manifestation;
import com.vzome.core.model.RealizedModel;
import com.vzome.core.model.Strut;

public class LinearMapTool extends TransformationTool
{
    private final boolean originalScaling;

    public LinearMapTool( String name, Selection selection, RealizedModel realized, Tool.Registry tools, Point originPoint )
    {
        this( name, selection, realized, tools, originPoint, false );
    }

    public LinearMapTool(
        String name, Selection selection, RealizedModel realized, Tool.Registry tools, Point originPoint, boolean originalScaling )
    {
        super( name, selection, realized, tools, originPoint );
        this.originalScaling = originalScaling;
    }

    public void perform() throws Command.Failure
    {
        Segment s1 = null, s2 = null, s3 = null;
        Point center = null;
        boolean correct = true;
        for ( Iterator mans = mSelection .iterator(); mans .hasNext(); ) {
            Manifestation man = (Manifestation) mans .next();
            unselect( man );
            if ( man instanceof Connector )
            {
                if ( center != null )
                {
                    correct = false;
                    break;
                }
                center = (Point) ((Connector) man) .getConstructions() .next();
            }
            else if ( man instanceof Strut )
            {
                if ( s3 != null )
                {
                    correct = false;
                    break;
                }
                if ( s1 == null )
                    s1 = (Segment) ((Strut) man) .getConstructions() .next();
                else if ( s2 == null )
                    s2 = (Segment) ((Strut) man) .getConstructions() .next();
                else
                    s3 = (Segment) ((Strut) man) .getConstructions() .next();
            }
        }
        
        correct = correct && s3 != null;
        if ( !correct )
            throw new Command.Failure( "linear map tool requires three non-parallel struts and a single (optional) center ball" );

        if ( center == null )
            center = this.originPoint;
        
        this .transforms = new Transformation[ 1 ];
        transforms[ 0 ] = new ChangeOfBasis( s1, s2, s3, center, originalScaling );


        defineTool();
    }

    protected String getXmlElementName()
    {
        return "LinearTransformTool";
    }

    public String getCategory()
    {
        return "linear map";
    }

    public String getDefaultName( String baseName )
    {
        return "SHOULD NEVER HAPPEN";
    }
}
