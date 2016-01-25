
//(c) Copyright 2005, Scott Vorthmann.  All rights reserved.

package com.vzome.core.commands;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.vzome.core.algebra.AlgebraicNumber;
import com.vzome.core.algebra.AlgebraicVector;
import com.vzome.core.construction.Construction;
import com.vzome.core.construction.ConstructionChanges;
import com.vzome.core.construction.ConstructionList;
import com.vzome.core.construction.Point;
import com.vzome.core.construction.PolygonFromVertices;

public class CommandPolygon extends AbstractCommand
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
    
    public ConstructionList apply( ConstructionList parameters, AttributeMap attrs, ConstructionChanges effects ) throws Failure
    {
        Boolean loadingFile = ((Boolean) attrs .get( Command .LOADING_FROM_FILE ) );
        boolean failed = false;
        ConstructionList result = new ConstructionList();
        final Construction[] params = parameters .getConstructions();
        List<Construction> verticesList = new ArrayList<>();
        
        AlgebraicVector normal = null, base = null;
        int numPoints = 0;
        for ( int j = 0; j < params .length; j++ ){
            if ( params[j] instanceof Point ) {
                ++numPoints;
                verticesList .add( params[j] );
                if ( numPoints == 1 ) {
                    base = ((Point) verticesList .get( 0 ) ) .getLocation();
                } else if ( numPoints == 3 ) {
                    
                    AlgebraicVector v1 = ((Point) verticesList .get( 1 ) ) .getLocation() .minus( base );
                    AlgebraicVector v2 = ((Point) params[j] ) .getLocation() .minus( base );
                    normal = v1 .cross( v2 );
                    if ( normal .isOrigin() )
                        if ( loadingFile != null )
                            failed = true;
                        else
                            throw new Failure( "points are colinear" );
                } else if ( numPoints > 3 ) {
                    AlgebraicVector loc = ((Point) params[j] ) .getLocation() .minus( base );
                    AlgebraicNumber dotProd = loc .dot( normal );
                    if ( ! dotProd .isZero() )
                        if ( loadingFile != null )
                            failed = true;
                        else
                            throw new Failure( "points not coplanar" );
                }
            }
        }
        if ( numPoints < 3 )
        {
            if ( loadingFile != null )
                failed = true;
            else
                throw new Failure( "A polygon requires at least three vertices." );
        }
        Point[] points = new Point[0];
        PolygonFromVertices poly = new PolygonFromVertices( verticesList .toArray( points ) );
        {
            
        }
        if ( failed )
            poly .setFailed(); // and don't add it to effects for rendering
        else
            effects .constructionAdded( poly );
        result .addConstruction( poly );
        return result;
    }
}
