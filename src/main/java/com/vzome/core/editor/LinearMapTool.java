
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

        Segment[] oldBasis = new Segment[3];
        Segment[] newBasis = new Segment[3];
        int index = 0;
        boolean correct = true;
        Point center = null;
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
                if ( index >= 6 ) {
                    correct = false;
                    break;
                }
                if ( index / 3 == 0 )
                {
                    oldBasis[ index % 3 ] = (Segment) man .getConstructions() .next();
                }
                else
                {
                    newBasis[ index % 3 ] = (Segment) man .getConstructions() .next();
                }
                ++index;
            }
        }
        
        correct = correct && ( ( index == 3) || ( index == 6 ) );
        if ( !correct )
            throw new Command.Failure( "linear map tool requires three adjacent, non-parallel struts (or two sets of three) and a single (optional) center ball" );

        if ( center == null )
            center = this.originPoint;
        
        this .transforms = new Transformation[ 1 ];
        if ( index == 6 )
        	transforms[ 0 ] = new ChangeOfBasis( oldBasis, newBasis, center );
        else
        	transforms[ 0 ] = new ChangeOfBasis( oldBasis[ 0 ], oldBasis[ 1 ], oldBasis[ 2 ], center, originalScaling );

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
