

package com.vzome.core.commands;


import com.vzome.core.construction.Construction;
import com.vzome.core.construction.ConstructionChanges;
import com.vzome.core.construction.ConstructionList;
import com.vzome.core.construction.Point;
import com.vzome.core.construction.PointReflection;
import com.vzome.core.construction.Transformation;

/**
 * @author Scott Vorthmann
 */
public class CommandCentralSymmetry extends CommandTransform
{   
    @Override
    public Object[][] getAttributeSignature()
    {
        return ATTR_SIGNATURE;
    }
    
    @Override
    public ConstructionList apply( final ConstructionList parameters, AttributeMap attributes, final ConstructionChanges effects ) throws Failure
    {
        ConstructionList output = new ConstructionList();
        final Point center = (Point) attributes .get( SYMMETRY_CENTER_ATTR_NAME );
        final Construction[] params = parameters .getConstructions();
        for (Construction param : params) {
            output.addConstruction(param);
        }
        
        Transformation transform = new PointReflection( center );
        effects .constructionAdded( transform );
        return transform( params, transform, effects ); 
    }
}
