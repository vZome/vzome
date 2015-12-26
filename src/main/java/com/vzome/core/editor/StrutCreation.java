
//(c) Copyright 2005, Scott Vorthmann.  All rights reserved.

package com.vzome.core.editor;

import java.util.Map;

import org.w3c.dom.Element;

import com.vzome.core.algebra.AlgebraicNumber;
import com.vzome.core.commands.XmlSaveFormat;
import com.vzome.core.construction.AnchoredSegment;
import com.vzome.core.construction.Point;
import com.vzome.core.construction.Segment;
import com.vzome.core.construction.SegmentEndPoint;
import com.vzome.core.math.symmetry.Axis;
import com.vzome.core.model.RealizedModel;

public class StrutCreation extends ChangeConstructions
{
    private Point mAnchor;
    private Axis mAxis;
    private AlgebraicNumber mLength;
    
    public StrutCreation( Point anchor, Axis axis, AlgebraicNumber len, RealizedModel realized )
    {
        super( null, realized, false );
        
        mAnchor = anchor;
        mAxis = axis;
        mLength = len;
    }
    
    public void perform()
    {
        Segment segment = new AnchoredSegment( mAxis, mLength, mAnchor );
        manifestConstruction( segment );
        Point point = new SegmentEndPoint( segment );
        manifestConstruction( point );

        redo();
    }

    protected void getXmlAttributes( Element xml )
    {
        XmlSaveFormat .serializePoint( xml, "anchor", mAnchor );
        XmlSaveFormat .serializeAxis( xml, "symm", "dir", "index", "sense", mAxis );
        XmlSaveFormat .serializeNumber( xml, "len", mLength );
    }

    public void setXmlAttributes( Element xml, XmlSaveFormat format )
    {
        if ( format .rationalVectors() )
        {
            mAnchor = format .parsePoint( xml, "anchor" );
            mAxis = format .parseAxis( xml, "symm", "dir", "index", "sense" );
            mLength = format .parseNumber( xml, "len" );
        }
        else
        {
            Map attrs = format .loadCommandAttributes( xml, /*projectTo3d*/ true );
            mAnchor = (Point) attrs .get( "anchor" );
            mAxis = (Axis) attrs .get( "axis" );
            mLength = (AlgebraicNumber) attrs .get( "len" );
        }
    }

    protected String getXmlElementName()
    {
        return "StrutCreation";
    }
}
