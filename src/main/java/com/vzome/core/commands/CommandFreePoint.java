

package com.vzome.core.commands;

import java.util.Map;

import com.vzome.core.construction.ConstructionChanges;
import com.vzome.core.construction.ConstructionList;
import com.vzome.core.construction.FreePoint;
import com.vzome.core.construction.ModelRoot;
import com.vzome.core.construction.Point;

/**
 * @author Scott Vorthmann
 */
public class CommandFreePoint extends AbstractCommand
{
    private static Object[][] PARAMS = new Object[][]{};
    
    private static Object[][] ATTRS = new Object[][]{ { "where", int[].class } };
    
    public Object[][] getParameterSignature()
    {
        return PARAMS;
    }

    public Object[][] getAttributeSignature()
    {
        return ATTRS;
    }

    public ConstructionList apply( ConstructionList parameters, Map attributes,
            ConstructionChanges effects ) throws Failure
    {
        ConstructionList result = new ConstructionList();
        int[] /*AlgebraicVector*/ loc = (int[] /*AlgebraicVector*/) attributes .get( "where" );
        ModelRoot root = (ModelRoot) attributes .get( MODEL_ROOT_ATTR_NAME );
        
        Point pt2 = new FreePoint( loc, root );
        effects .constructionAdded( pt2 );
        result .addConstruction( pt2 );
    
        return result;
    }

}
