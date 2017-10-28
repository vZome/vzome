

package com.vzome.core.commands;


import org.w3c.dom.Element;

import com.vzome.core.algebra.AlgebraicNumber;
import com.vzome.core.construction.AnchoredSegment;
import com.vzome.core.construction.ConstructionChanges;
import com.vzome.core.construction.ConstructionList;
import com.vzome.core.construction.Point;
import com.vzome.core.construction.Segment;
import com.vzome.core.construction.SegmentEndPoint;
import com.vzome.core.math.symmetry.Axis;

/**
 * @author Scott Vorthmann
 */
public class CommandBuildAnchoredSegment extends AbstractCommand
{
    @Override
    public void getXml( Element xml, AttributeMap attributes )
    {
        XmlSaveFormat .serializeAxis( xml, "symm", "dir", "index", "sense", (Axis) attributes .get( "axis" ) );
        XmlSaveFormat .serializeNumber( xml, "len", (AlgebraicNumber) attributes .get( "length" ) );
    }

    @Override
    public AttributeMap setXml( Element xml, XmlSaveFormat format )
    {
        AttributeMap attrs = super .setXml( xml, format );
        
        if ( format .commandEditsCompacted() )
        {
            // attrs will be empty but for fixed attributes
            attrs .put( "axis", format .parseAxis( xml, "symm", "dir", "index", "sense" ) );
            attrs .put( "length", format .parseNumber( xml, "len" ) );
        }
        return attrs;
    }

    private static final String AXIS_ATTR = "axis";
    
    private static final String LENGTH_ATTR = "length";

    private static final Object[][] PARAM_SIGNATURE = new Object[][]{ { "start", Point.class } };

    private static final Object[][] ATTR_SIGNATURE = new Object[][]{ { AXIS_ATTR, Axis.class },
            														{ LENGTH_ATTR, int[].class } };

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
    public ConstructionList apply( ConstructionList parameters, AttributeMap attrs, ConstructionChanges effects ) throws Failure
    {
        ConstructionList result = new ConstructionList();
        if ( parameters == null || parameters .size() != 1 )
            throw new Failure( "start parameter must be a single point" );
        Object c = parameters .get( 0 );
        if ( ! ( c instanceof Point ) )
            throw new Failure( "start parameter must be a single point" );
        Point pt1 = (Point) c;
        Axis axis = (Axis) attrs .get( AXIS_ATTR );
        AlgebraicNumber len = (AlgebraicNumber) attrs .get( LENGTH_ATTR );
        
        Segment segment = new AnchoredSegment( axis, len, pt1 );
        effects .constructionAdded( segment );
        result .addConstruction( segment );
        
        Point pt2 = new SegmentEndPoint( segment );
        effects .constructionAdded( pt2 );
        result .addConstruction( pt2 );
    
        return result;
    }
}
