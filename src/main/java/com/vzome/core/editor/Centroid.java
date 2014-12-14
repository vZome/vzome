
//(c) Copyright 2005, Scott Vorthmann.  All rights reserved.

package com.vzome.core.editor;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.vzome.core.commands.Command;
import com.vzome.core.commands.Command.Failure;
import com.vzome.core.construction.CentroidPoint;
import com.vzome.core.construction.Point;
import com.vzome.core.model.Connector;
import com.vzome.core.model.Manifestation;
import com.vzome.core.model.RealizedModel;

public class Centroid extends ChangeConstructions
{
    public void perform() throws Command.Failure
    {
        List verticesList = new ArrayList();
        for ( Iterator mans = mSelection .iterator(); mans .hasNext(); ) {
            Manifestation man = (Manifestation) mans .next();
            unselect( man );
            if ( man instanceof Connector )
            {
                Point nextPoint = (Point) ((Connector) man) .getConstructions() .next();
                verticesList .add( nextPoint );
            }
        }
        if ( verticesList .size() < 2 )
            throw new Failure( "Select at least two balls to compute the centroid." );

        Point[] points = new Point[0];
        CentroidPoint centroid = new CentroidPoint( (Point[]) verticesList .toArray( points ) );
        addConstruction( centroid );
        manifestConstruction( centroid );

        redo();
    }

    public Centroid( Selection selection, RealizedModel realized, boolean groupInSelection )
    {
        super( selection, realized, groupInSelection );
    }
        
    protected String getXmlElementName()
    {
        return "NewCentroid";
    }
}
