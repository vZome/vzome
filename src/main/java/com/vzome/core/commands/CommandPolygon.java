
//(c) Copyright 2005, Scott Vorthmann.  All rights reserved.

package com.vzome.core.commands;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.vzome.core.algebra.AlgebraicField;
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
    
    public ConstructionList apply( ConstructionList parameters, Map attrs, ConstructionChanges effects ) throws Failure
    {
        AlgebraicField field = (AlgebraicField) attrs .get( FIELD_ATTR_NAME );
        Boolean loadingFile = ((Boolean) attrs .get( Command .LOADING_FROM_FILE ) );
        boolean failed = false;
        ConstructionList result = new ConstructionList();
        final Construction[] params = parameters .getConstructions();
        List verticesList = new ArrayList();
        
        int[] /*AlgebraicVector*/ normal = null, base = null;
        int numPoints = 0;
        for ( int j = 0; j < params .length; j++ ){
            if ( params[j] instanceof Point ) {
                ++numPoints;
                verticesList .add( params[j] );
                if ( numPoints == 1 ) {
                    base = ((Point) verticesList .get( 0 ) ) .getLocation();
                } else if ( numPoints == 3 ) {
                    
                    int[] /*AlgebraicVector*/ v1 = field .subtract( ((Point) verticesList .get( 1 ) ) .getLocation(), base );
                    int[] /*AlgebraicVector*/ v2 = field .subtract( ((Point) params[j] ) .getLocation(), base );
                    normal = field .cross( v1, v2 );
                    if ( field .isOrigin( normal ) )
                        if ( loadingFile != null )
                            failed = true;
                        else
                            throw new Failure( "points are colinear" );
                } else if ( numPoints > 3 ) {
                    int[] /*AlgebraicVector*/ loc = field .subtract ( ((Point) params[j] ) .getLocation(),base );
                    int[] /*AlgebraicNumber*/ dotProd = field .dot( loc, normal );
                    if ( ! field .isZero( dotProd ) )
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
        PolygonFromVertices poly = new PolygonFromVertices( (Point[]) verticesList .toArray( points ) );
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
