

package com.vzome.core.commands;

import java.util.Map;

import com.vzome.core.construction.ConstructionChanges;
import com.vzome.core.construction.ConstructionList;
import com.vzome.core.model.Manifestation;
import com.vzome.core.render.Color;

public class CommandSetColor extends AbstractCommand
{
    public static final String MANIFESTATION_ATTR = "manifestation.context";
    
    public static final String COLOR_ATTR = "color";
    
    private static final Object[][] PARAM_SIGNATURE = new Object[][]{};

    private static final Object[][] ATTR_SIGNATURE = new Object[][]{ { MANIFESTATION_ATTR, Manifestation.class },
        { COLOR_ATTR, Color.class } };

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
//        Manifestation man = (Manifestation) attributes .get( MANIFESTATION_ATTR );
//        Color color = (Color) attributes .get( COLOR_ATTR );
//        man .setColor( color );
        return parameters;
    }

}
