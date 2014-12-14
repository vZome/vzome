

package com.vzome.core.commands;

import java.util.Map;

import com.vzome.core.algebra.AlgebraicField;
import com.vzome.core.construction.Construction;
import com.vzome.core.construction.ConstructionChanges;
import com.vzome.core.construction.ConstructionList;
import com.vzome.core.construction.FreePoint;
import com.vzome.core.construction.ModelRoot;
import com.vzome.core.construction.Point;
import com.vzome.core.construction.Segment;
import com.vzome.core.construction.SegmentJoiningPoints;

/**
 * @author Scott Vorthmann
 */
public class CommandConjugate extends AbstractCommand
{
    private static final Object[][] PARAM_SIGNATURE = new Object[][]{ { GENERIC_PARAM_NAME, Construction.class } };

    private static final Object[][] ATTR_SIGNATURE = new Object[][]{};

    public Object[][] getParameterSignature()
    {
        return PARAM_SIGNATURE;
    }

    public Object[][] getAttributeSignature()
    {
        return ATTR_SIGNATURE;
    }
    
    public ConstructionList apply( final ConstructionList parameters, Map attributes, final ConstructionChanges effects ) throws Failure
    {
        final Construction[] params = parameters .getConstructions();
        ModelRoot root = (ModelRoot) attributes .get( MODEL_ROOT_ATTR_NAME );
        ConstructionList results = new ConstructionList();
        AlgebraicField field = root .getField();
        int[] up = field .createPower( 5 );
        int[] down = field .createPower( -5 );
        
        for ( int j = 0; j < params .length; j++ ){
            Construction conjugate = null;
            if ( params[ j ] instanceof Point ) {
                int[] loc = ((Point) params[ j ]) .getLocation();
                loc = field .conjugate( loc );
                conjugate = new FreePoint( field .scaleVector( loc, up ), root );
            }
            else if ( params[ j ] instanceof Segment ) {
                int[] loc = ((Segment) params[ j ]) .getStart();
                loc = field .conjugate( loc );
                Point p1 = new FreePoint( field .scaleVector( loc, up ), root );
                loc = ((Segment) params[ j ]) .getEnd();
                loc = field .conjugate( loc );
                Point p2 = new FreePoint( field .scaleVector( loc, up ), root );
                conjugate = new SegmentJoiningPoints( p1, p2 );
            }
            if ( conjugate != null ) {
                effects .constructionAdded( conjugate );
                results .addConstruction( conjugate );
            }
        }
        return new ConstructionList();
    }
}
