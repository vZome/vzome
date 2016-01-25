

package com.vzome.core.commands;

import java.util.Map;

import com.vzome.core.construction.Construction;
import com.vzome.core.construction.ConstructionChanges;
import com.vzome.core.construction.ConstructionList;
import com.vzome.core.construction.Plane;
import com.vzome.core.construction.PlaneFromNormalSegment;
import com.vzome.core.construction.PlaneReflection;
import com.vzome.core.construction.Point;
import com.vzome.core.construction.Segment;
import com.vzome.core.construction.Transformation;

/**
 * @author Scott Vorthmann
 */
public class CommandMirrorSymmetry extends CommandTransform
{
    public ConstructionList apply( final ConstructionList parameters, AttributeMap attributes, final ConstructionChanges effects ) throws Failure
    {
        final Point center = (Point) attributes .get( SYMMETRY_CENTER_ATTR_NAME );
        final Segment norm = (Segment) attributes .get( SYMMETRY_AXIS_ATTR_NAME );
        if ( norm == null ) {
            throw new Command.Failure( "no symmetry axis provided" );
        }
        
        final Construction[] params = parameters .getConstructions();
        
        Plane mirror = new PlaneFromNormalSegment( center, norm );
        effects .constructionAdded( mirror );
        
        Transformation transform = new PlaneReflection( mirror );
        return transform( params, transform, effects );
    }
}
