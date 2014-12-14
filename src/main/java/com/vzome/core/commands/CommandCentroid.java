

package com.vzome.core.commands;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.vzome.core.construction.CentroidPoint;
import com.vzome.core.construction.Construction;
import com.vzome.core.construction.ConstructionChanges;
import com.vzome.core.construction.ConstructionList;
import com.vzome.core.construction.Point;

/**
 * @author Scott Vorthmann
 */
public class CommandCentroid extends AbstractCommand
{
    private static final Object[][] PARAM_SIGNATURE =
        new Object[][]{ { GENERIC_PARAM_NAME, Point.class } };

    private static final Object[][] ATTR_SIGNATURE = new Object[][]{};

    public Object[][] getParameterSignature()
    {
        return PARAM_SIGNATURE;
    }

    public Object[][] getAttributeSignature()
    {
        return ATTR_SIGNATURE;
    }
    
    public ConstructionList apply( ConstructionList parameters, Map attrs, ConstructionChanges effects ) throws Failure
    {
        ConstructionList result = new ConstructionList();
        if ( parameters == null || parameters .size() == 0 )
            throw new Failure( "Select at least two balls to compute the centroid." );
        final Construction[] params = parameters .getConstructions();
        
        List verticesList = new ArrayList();
        for ( int j = 0; j < params .length; j++ )
            if ( params[j] instanceof Point )
                verticesList .add( params[j] );
        // this test causes old files to fail to load
//        if ( verticesList .size() < 2 )
//            throw new Failure( "Select at least two balls to compute the centroid." );
        Point[] points = new Point[0];
        CentroidPoint centroid = new CentroidPoint( (Point[]) verticesList .toArray( points ) );
        effects .constructionAdded( centroid );
        result .addConstruction( centroid );
        return result;
    }
}
