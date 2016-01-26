

package com.vzome.core.commands;


import com.vzome.core.construction.ConstructionChanges;
import com.vzome.core.construction.ConstructionList;
import com.vzome.core.construction.Point;
import com.vzome.core.construction.Segment;
import com.vzome.core.construction.SegmentMidpoint;

/**
 * @author Scott Vorthmann
 */
public class CommandMidpoint extends AbstractCommand
{
    private static final Object[][] PARAM_SIGNATURE =
        new Object[][]{ { "segment", Segment.class } };

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
        if ( parameters == null || parameters .size() != 1 )
            throw new Failure( "Midpoint can only apply to a single strut." );
        try {
            Segment segment = (Segment) parameters .get( 0 );
            Point midpoint = new SegmentMidpoint( segment );
            result .addConstruction( midpoint );
            effects .constructionAdded( midpoint );
        } catch ( ClassCastException e ) {
            throw new Failure( "Midpoint can only apply to a strut." );
        }
        return result;
    }
}
