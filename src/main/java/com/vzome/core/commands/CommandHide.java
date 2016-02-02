

package com.vzome.core.commands;


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
    public ConstructionList apply( final ConstructionList parameters, AttributeMap attributes, final ConstructionChanges effects ) throws Failure
    {
    	throw new Failure( "CommandHide apply attempted" );
    }
}
