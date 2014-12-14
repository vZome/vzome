

package com.vzome.core.construction;

/**
 * @author Scott Vorthmann
 */
public interface Visitor
{
    void visitConstruction( Construction c );
    
    void visitModelRoot( ModelRoot root );
    
    void visitPoint( Point p );
    
    void visitFreePoint( FreePoint p );
    
    void visitSegmentEndPoint( SegmentEndPoint p );
    
    void visitTransformedPoint( TransformedPoint p );
    
    void visitLine( Line l );
    
    void visitPlane( Plane p );
    
    void visitSegment( Segment s );
    
    void visitAnchoredSegment( AnchoredSegment s );
    
    void visitPerpendicularLine( PerpendicularLine s );
    
    void visitTransformation( Transformation t );
    
    void visitPlaneReflection( PlaneReflection symm );
    
    void visitChangeOfBasis( ChangeOfBasis t );

    /**
     * @param segment
     */
    void visitPlaneFromNormalSegment( PlaneFromNormalSegment plane );
    
    void visitIcosahedralRotation( SymmetryTransformation rotation );

    /**
     * @param midpoint
     */
    void visitSegmentMidpoint( SegmentMidpoint midpoint );

    /**
     * @param reflection
     */
    void visitPointReflection( PointReflection reflection );

    void visitTranslation(Translation translation);

    void visitPolygon( Polygon polygon );
}
