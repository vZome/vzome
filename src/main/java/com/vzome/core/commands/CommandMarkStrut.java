

package com.vzome.core.commands;

import java.util.Map;

import com.vzome.core.construction.ConstructionChanges;
import com.vzome.core.construction.ConstructionList;
import com.vzome.core.construction.Marker;
import com.vzome.core.construction.Segment;

public class CommandMarkStrut extends AbstractCommand
{
    private static final Object[][] PARAM_SIGNATURE = new Object[][]{ { "segment", Segment.class } };

    private static final Object[][] ATTR_SIGNATURE = new Object[][]{ { CommandTransform.SYMMETRY_AXIS_ATTR_NAME, Segment.class } };

    public Object[][] getParameterSignature()
    {
        return PARAM_SIGNATURE;
    }

    public Object[][] getAttributeSignature()
    {
        return ATTR_SIGNATURE;
    }

    public ConstructionList apply( ConstructionList parameters, Map attributes,
            ConstructionChanges effects ) throws Failure
    {
        if ( parameters == null || parameters .size() != 1 )
            throw new Failure( "Mark can only apply to a single strut." );
        try {
            Segment segment = (Segment) parameters .get( 0 );
            
            Marker marker = new Marker( segment );
            marker .attach();
        } catch ( ClassCastException e ) {
            throw new Failure( "Mark Strut can only apply to a strut." );
        }
        return parameters;
    }

}
