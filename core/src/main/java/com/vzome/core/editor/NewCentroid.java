
//(c) Copyright 2005, Scott Vorthmann.  All rights reserved.

package com.vzome.core.editor;

import java.util.ArrayList;
import java.util.List;

import com.vzome.core.commands.Command;
import com.vzome.core.commands.Command.Failure;
import com.vzome.core.construction.CentroidPoint;
import com.vzome.core.construction.Point;
import com.vzome.core.model.Connector;
import com.vzome.core.model.Manifestation;
import com.vzome.core.model.RealizedModel;

public class NewCentroid extends ChangeManifestations
{
    @Override
    public void perform() throws Command.Failure
    {
        List<Point> verticesList = new ArrayList<>();
        for (Manifestation man : mSelection) {
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
        CentroidPoint centroid = new CentroidPoint( verticesList .toArray( points ) );
        manifestConstruction( centroid );

        redo();
    }

    public NewCentroid( Selection selection, RealizedModel realized )
    {
        super( selection, realized );
    }
    
    @Override
    protected boolean groupingAware()
    {
        return true;
    }
        
    @Override
    protected String getXmlElementName()
    {
        return "NewCentroid";
    }
}
