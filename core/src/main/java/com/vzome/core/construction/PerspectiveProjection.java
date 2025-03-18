

package com.vzome.core.construction;

import com.vzome.core.algebra.AlgebraicVector;


/**
 * @author Scott Vorthmann
 */
public class PerspectiveProjection extends Transformation
{
    // parameters
    private final Plane projectionPlane;
    private final Point perspectivePoint;

    /**
     * @param prototype
     */
    public PerspectiveProjection( Plane projectionPlane, Point perspectivePoint )
    {
        super( projectionPlane .field );
        this.projectionPlane = projectionPlane;
        this.perspectivePoint = perspectivePoint;
        
        mapParamsToState();
    }
    
    @Override
    protected final boolean mapParamsToState()
    {
        if ( projectionPlane .isImpossible() )
            setStateVariables( null, null, true );
        
        AlgebraicVector loc = getField() .origin( 3 );
        
        return setStateVariables( null /*or field identity*/, loc, false );
    }

    @Override
    public AlgebraicVector transform( AlgebraicVector arg )
    {
        Segment segment = new SegmentJoiningPoints( this.perspectivePoint, new FreePoint(arg) );
        if ( segment .getOffset() .isOrigin() )
            return null;
        Line line = new LineExtensionOfSegment( segment );
        Point point = new LinePlaneIntersectionPoint( this .projectionPlane, line );
        return point .getLocation();
    }

    @Override
    public Construction transform( Construction c )
    {
        if ( c instanceof Point ) {
            Point result = new TransformedPoint( this, (Point) c );
            if ( result .isImpossible() )
                return null;
            return result;
        } else if ( c instanceof Segment ) {
            Segment result = new TransformedSegment( this, (Segment) c );
            if ( result .isImpossible() || result .getOffset() .isOrigin() ) {
                return new FreePoint( ((Segment) c) .getStart() );
            }
            return result;
        } else if ( c instanceof Polygon ) {
            Polygon p = new TransformedPolygon( this, (Polygon) c );
            if ( p .getNormal() .isOrigin() ) {
                // The polygon is projecting to a degenerate polygon with all vertices being collinear.
                // Rather than projecting each individual edge as a Segment,
                // some of which could then project down to a point,
                // we'll return a single Segment 
                // with its end points at the min and max points 
                // of the degenerate projected Polygon.
                AlgebraicVector min = p.getVertex(0);
                AlgebraicVector max = min;
                for(int i = 1; i < p.getVertexCount(); i++) {
                    AlgebraicVector v = p.getVertex(i);
                    if(v.compareTo(min) == -1) {
                        min = v;
                    }
                    if(v.compareTo(max) == 1) {
                        max = v;
                    }
                }
                Point p1 = new FreePoint(min);
                Point p2 = new FreePoint(max);
                return new SegmentJoiningPoints(p1, p2);
            }
            return p;
        }
        return super .transform( c ); // will return null
    }
}
