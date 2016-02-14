
//(c) Copyright 2005, Scott Vorthmann.  All rights reserved.

package com.vzome.core.commands;

import java.util.ArrayList;
import java.util.List;

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

    @Override
    public Object[][] getParameterSignature()
    {
        return PARAM_SIGNATURE;
    }

    @Override
    public Object[][] getAttributeSignature()
    {
        return ATTR_SIGNATURE;
    }
    
    @Override
    public ConstructionList apply( ConstructionList parameters, AttributeMap attrs, ConstructionChanges effects ) throws Failure
    {
        Boolean loadingFile = ((Boolean) attrs .get( Command .LOADING_FROM_FILE ) );
        boolean failed = false;
        ConstructionList result = new ConstructionList();
        final Construction[] params = parameters .getConstructions();
        List<Point> verticesList = new ArrayList<>();
        
        AlgebraicVector normal = null, base = null;
        int numPoints = 0;
        for (Construction param : params) {
            if (param instanceof Point) {
                Point point = (Point) param;
                ++numPoints;
                verticesList.add(point);
                if (numPoints == 1) {
                    base = point.getLocation();
                } else if (numPoints == 3) {
                    AlgebraicVector v1 = verticesList .get( 1 ) .getLocation() .minus( base );
                    AlgebraicVector v2 = point.getLocation().minus(base);
                    normal = v1 .cross( v2 );
                    if ( normal .isOrigin() ) {
                        if ( loadingFile != null ) {
                            failed = true;
                        } else {
                            // DJH: As far as I can tell, the only problem with any of vertices being collinear
                            // is that Panel.getNormal and Face.getNormal both use the first three vertices 
                            // to calculate their normals. At this point, even if they were changed 
                            // to use any three points that are non-collinear (rather than just the first three points),
                            // they would generate files that are not readable by older versions of vzome
                            // unless we also automatically "rotate" the points around the perimeter 
                            // until the first three are non-collinear. Until then, it's necessary to keep this constraint.
                            // However, a polygon with the same collinear points in a different order 
                            // is OK, just as long as the first three are not collinear.
                            // This message should imply the option of reordering the points.
                            // Also note that test cases, which construct Panels or Faces 
                            // outside of this command must be careful not to introduce this problem 
                            // since the Panel and Face classes don't check for the first three to be non-collinear. 
                            // They expect the caller to have done that checking.
                            throw new Failure( "First 3 points cannot be collinear." );
                        }
                    }
                } else if (numPoints > 3) {
                    AlgebraicVector loc = point.getLocation().minus(base);
                    AlgebraicNumber dotProd = loc .dot( normal );
                    if ( ! dotProd .isZero() ) {
                        if ( loadingFile != null )
                            failed = true;
                        else
                            throw new Failure( "Points are not coplanar." );
                    }
                }
            }
        }
        if ( numPoints < 3 ) {
            if ( loadingFile != null )
                failed = true;
            else
                throw new Failure( "A polygon requires at least three vertices." );
        }
        PolygonFromVertices poly = new PolygonFromVertices( verticesList .toArray( new Point[verticesList.size()] ) );
        if ( failed )
            poly .setFailed(); // and don't add it to effects for rendering
        else
            effects .constructionAdded( poly );
        result .addConstruction( poly );
        return result;
    }
}
