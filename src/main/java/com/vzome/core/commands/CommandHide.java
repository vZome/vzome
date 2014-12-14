

package com.vzome.core.commands;

import java.util.Map;

import com.vzome.core.construction.Construction;
import com.vzome.core.construction.ConstructionChanges;
import com.vzome.core.construction.ConstructionList;

/**
 * @author Scott Vorthmann
 */
public class CommandHide extends AbstractCommand
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
        
        for ( int j = 0; j < params .length; j++ ){
            params[ j ] .setVisible( false );
            effects .constructionHidden( params[ j ] );
        }
        return new ConstructionList();
    }
}
