

package com.vzome.core.commands;


import com.vzome.core.construction.ConstructionChanges;
import com.vzome.core.construction.ConstructionList;
import com.vzome.core.construction.Point;
import com.vzome.core.construction.Segment;
import com.vzome.core.construction.SegmentJoiningPoints;

/**
 * @author Scott Vorthmann
 */
public class CommandJoinPoints extends AbstractCommand
{
    private static final Object[][] PARAM_SIGNATURE =
        new Object[][]{ { "start", Point.class }, { "end", Point.class } };

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
        ConstructionList result = new ConstructionList();
        if ( parameters == null || parameters .size() != 2 )
            throw new Failure( "parameters must be two points" );
        try {
            Point pt1 = (Point) parameters .get( 0 );
            Point pt2 = (Point) parameters .get( 1 );
            Segment segment = new SegmentJoiningPoints( pt1, pt2 );
//            result .addConstruction( pt1 );
//            result .addConstruction( pt2 );
            result .addConstruction( segment );
            effects .constructionAdded( segment );
        } catch ( ClassCastException e ) {
            throw new Failure( "parameters must be two points" );
        }
        return result;
    }
}
