
//(c) Copyright 2005, Scott Vorthmann.  All rights reserved.

package com.vzome.core.commands;


import com.vzome.core.construction.ConstructionChanges;
import com.vzome.core.construction.ConstructionList;
import com.vzome.core.construction.Segment;

public class CommandObliquePentagon extends AbstractCommand
{
    private static final Object[][] PARAM_SIGNATURE =
        new Object[][]{ { "segment1", Segment.class }, { "segment2", Segment.class } };

    private static final Object[][] ATTR_SIGNATURE = new Object[][]{};

    public Object[][] getParameterSignature()
    {
        return PARAM_SIGNATURE;
    }

    public Object[][] getAttributeSignature()
    {
        return ATTR_SIGNATURE;
    }
    

    public ConstructionList apply( ConstructionList parameters, AttributeMap attributes,
            ConstructionChanges effects ) throws Failure
    {
        throw new Failure( "Oblique pentagon should never be called." );
    }

}
