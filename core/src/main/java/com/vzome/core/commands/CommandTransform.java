

package com.vzome.core.commands;


import com.vzome.core.construction.Construction;
import com.vzome.core.construction.ConstructionChanges;
import com.vzome.core.construction.ConstructionList;
import com.vzome.core.construction.Point;
import com.vzome.core.construction.Segment;
import com.vzome.core.construction.Transformation;
import com.vzome.core.math.symmetry.Symmetry;

/**
 * @author Scott Vorthmann
 */
public abstract class CommandTransform extends AbstractCommand
{
    @Override
    public void setFixedAttributes( AttributeMap attributes, XmlSaveFormat format )
    {
        if ( format .getScale() != 0 )
            attributes .put(CommandTransform .SCALE_ATTR_NAME, format .getScale());

        super.setFixedAttributes( attributes, format );
    }

    @Override
    public ConstructionList apply( ConstructionList parameters, AttributeMap attributes, ConstructionChanges effects ) throws Failure
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

    @Override
    public Object[][] getParameterSignature()
    {
        return PARAM_SIGNATURE;
    }

    @Override
    public Object[][] getAttributeSignature()
    {
        return AXIS_ATTR_SIGNATURE;
    }
    
    protected ConstructionList transform( Construction[] params, Transformation transform, final ConstructionChanges effects ) throws Failure
    {
        final ConstructionList output = new ConstructionList();
        effects .constructionAdded( transform );
        for (Construction param : params) {
            Construction result = transform .transform( param );
            if ( result == null )
                continue;
            effects .constructionAdded( result );
            output .addConstruction( result );
        }
        return output;
    }
}
