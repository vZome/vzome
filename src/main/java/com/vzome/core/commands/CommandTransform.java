

package com.vzome.core.commands;

import java.util.Map;

import com.vzome.core.construction.Construction;
import com.vzome.core.construction.ConstructionChanges;
import com.vzome.core.construction.ConstructionList;
import com.vzome.core.construction.Point;
import com.vzome.core.construction.Polygon;
import com.vzome.core.construction.Segment;
import com.vzome.core.construction.Transformation;
import com.vzome.core.construction.TransformedPoint;
import com.vzome.core.construction.TransformedPolygon;
import com.vzome.core.construction.TransformedSegment;
import com.vzome.core.math.symmetry.Symmetry;

/**
 * @author Scott Vorthmann
 */
public abstract class CommandTransform extends AbstractCommand
{
    public void setFixedAttributes( Map attributes, XmlSaveFormat format )
    {
        if ( format .getScale() != 0 )
            attributes .put( CommandTransform .SCALE_ATTR_NAME, new Integer( format .getScale() ) );

        super.setFixedAttributes( attributes, format );
    }

    public ConstructionList apply( ConstructionList parameters, Map attributes, ConstructionChanges effects ) throws Failure
    {
        // TODO Auto-generated method stub
        return null;
    }

    public static final String SYMMETRY_GROUP_ATTR_NAME = "symmetry.group";
    public static final String SYMMETRY_CENTER_ATTR_NAME = "symmetry.center";
    public static final String SYMMETRY_AXIS_ATTR_NAME = "symmetry.axis.segment";
    public static final String SCALE_ATTR_NAME = "scale.factor";

    private static final Object[][] PARAM_SIGNATURE = new Object[][]{ { GENERIC_PARAM_NAME, Construction.class } };

    protected static final Object[][] ATTR_SIGNATURE = new Object[][]{ { SYMMETRY_CENTER_ATTR_NAME, Point.class } };

    protected static final Object[][] AXIS_ATTR_SIGNATURE = new Object[][]{ { SYMMETRY_CENTER_ATTR_NAME, Point.class }, { SYMMETRY_AXIS_ATTR_NAME, Segment.class } };

    protected static final Object[][] GROUP_ATTR_SIGNATURE = new Object[][]{ { SYMMETRY_CENTER_ATTR_NAME, Point.class }, { SYMMETRY_GROUP_ATTR_NAME, Symmetry.class } };

    public Object[][] getParameterSignature()
    {
        return PARAM_SIGNATURE;
    }

    public Object[][] getAttributeSignature()
    {
        return AXIS_ATTR_SIGNATURE;
    }
    
    protected ConstructionList transform( Construction[] params, Transformation transform, final ConstructionChanges effects ) throws Failure
    {
        final ConstructionList output = new ConstructionList();
        effects .constructionAdded( transform );
        for ( int j = 0; j < params .length; j++ ){
            Construction result = null;
            if ( params[j] instanceof Point ) {
                result = new TransformedPoint( transform, (Point) params[j] );
            } else if ( params[j] instanceof Segment ) {
                result = new TransformedSegment( transform, (Segment) params[j] );
            } else if ( params[j] instanceof Polygon ) {
                result = new TransformedPolygon( transform, (Polygon) params[j] );
            } else {
                // TODO handle other constructions 
            }
            if ( result == null )
                continue;
            effects .constructionAdded( result );
            output .addConstruction( result );
        }
        return output;
    }
}
