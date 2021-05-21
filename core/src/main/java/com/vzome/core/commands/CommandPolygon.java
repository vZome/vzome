
package com.vzome.core.commands;

import java.util.ArrayList;
import java.util.List;

import com.vzome.core.algebra.AlgebraicVector;
import com.vzome.core.algebra.AlgebraicVectors;
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
    public boolean ordersSelection()
    {
        return true;
    }

    @Override
    public ConstructionList apply( ConstructionList parameters, AttributeMap attrs, ConstructionChanges effects ) throws Failure
    {
        List<Point> points = new ArrayList<>();
        // collect the vertices
        for (Construction param : parameters .getConstructions()) {
            if (param instanceof Point) {
                points.add((Point) param);
            }
        }
        
        // validate the vertices
        String errorMsg = null;
        if ( points.size() < 3 ) {
            errorMsg = "A polygon requires at least three vertices.";
        } else if ( points.get( 0 ) .is3d() && points.get( 1 ) .is3d() && points.get( 1 ) .is3d() )
        {
            AlgebraicVector normal = AlgebraicVectors.getNormal(
                    points.get(0).getLocation(), 
                    points.get(1).getLocation(), 
                    points.get(2).getLocation() );
            if( normal. isOrigin() ) {
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
                // TODO: Remove the requirement for the first 3 to be non-collinear, 
                // but still require that at least one vertex is non-collinear
                errorMsg = "First 3 points cannot be collinear.";
            } else {
                AlgebraicVector base = null;
                for(Point point : points) {
                    if(base == null ) {
                        base = point.getLocation();
                    } else {
                        if ( ! point.getLocation().minus( base ) .dot( normal ) .isZero() ) {
                            errorMsg= "Points are not coplanar.";
                            break;
                        }
                    }
                }                
            }
        }
        
        // Handle an error differently depending if we're loading from a file or not
        // If we're not loading from a file, then fail now
        // otherwise, create the polygon, but flag it as failed
        if( errorMsg != null && attrs .get( Command .LOADING_FROM_FILE ) == null) {
            throw new Failure( errorMsg );
        }

        PolygonFromVertices poly = new PolygonFromVertices( points );
        
        if ( errorMsg != null ) {
            poly .setFailed(); // and don't add it to effects for rendering
        } else {
            effects .constructionAdded( poly );
        }

        ConstructionList result = new ConstructionList();
        result .addConstruction( poly );
        return result;
    }
}
