

package com.vzome.core.commands;


import com.vzome.core.construction.ConstructionChanges;
import com.vzome.core.construction.ConstructionList;
import com.vzome.core.construction.Point;
import com.vzome.core.construction.Segment;
import com.vzome.core.construction.SegmentJoiningPoints;
import com.vzome.core.construction.SegmentTauDivision;

/**
 * @author Scott Vorthmann
 */
public class CommandTauDivision extends AbstractCommand
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
            throw new Failure( "Tau division applies to two balls." );
        try {
            Point start = (Point) parameters .get( 0 );
            Point end = (Point) parameters .get( 1 );
            Segment join = new SegmentJoiningPoints( start, end );
            Point midpoint = new SegmentTauDivision( join );
            result .addConstruction( midpoint );
            effects .constructionAdded( midpoint );
        } catch ( ClassCastException e ) {
            throw new Failure( "Tau division applies to two balls." );
        }
        return result;
    }
}
