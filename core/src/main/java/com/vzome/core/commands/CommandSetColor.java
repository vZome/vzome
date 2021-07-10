

package com.vzome.core.commands;


import com.vzome.core.construction.ConstructionChanges;
import com.vzome.core.construction.ConstructionList;

public class CommandSetColor extends AbstractCommand
{
    public static final String MANIFESTATION_ATTR = "manifestation.context";
    
    public static final String COLOR_ATTR = "color";
    
    private static final Object[][] PARAM_SIGNATURE = new Object[][]{};

//    private static final Object[][] ATTR_SIGNATURE = new Object[][]{ { MANIFESTATION_ATTR, Manifestation.class },
//        { COLOR_ATTR, Color.class } };

    @Override
    public Object[][] getParameterSignature()
    {
        return PARAM_SIGNATURE;
    }

    @Override
    public Object[][] getAttributeSignature()
    {
        return null;
    }

    @Override
    public ConstructionList apply( ConstructionList parameters, AttributeMap attributes,
            ConstructionChanges effects ) throws Failure
    {
//        Manifestation man = (Manifestation) attributes .get( MANIFESTATION_ATTR );
//        Color color = (Color) attributes .get( COLOR_ATTR );
//        man .setColor( color );
        return parameters;
    }

}
