

package com.vzome.core.construction;

/**
 * @author Scott Vorthmann
 */
public class DerivativeVisitor implements Visitor
{

    public void visitConstruction( Construction c )
    {
        Construction[] results = c .getDerivatives();
        for ( int i = 0; i < results .length; i++ )
            results[ i ] .accept( this );
    }
    
    public void visitModelRoot( ModelRoot root )
    {
        visitConstruction( root );
    }
    
    public void visitPoint( Point p )
    {
        visitConstruction( p );
    }
    
    public void visitFreePoint( FreePoint p )
    {
        visitPoint( p );
    }
    
    public void visitSegmentEndPoint( SegmentEndPoint p )
    {
        visitPoint( p );
    }

    public void visitTransformedPoint( TransformedPoint p )
    {
        visitPoint( p );
    }
    
    public void visitLine( Line l )
    {
        visitConstruction( l );
    }
    
    public void visitPlane( Plane p )
    {
        visitConstruction( p );
    }
    
    public void visitSegment( Segment s )
    {
        visitConstruction( s );
    }

    public void visitAnchoredSegment( AnchoredSegment s )
    {
        visitSegment( s );
    }
    
    public void visitPerpendicularLine( PerpendicularLine s )
    {
        visitLine( s );
    }
    
    public void visitTransformation( Transformation t )
    {
        visitConstruction( t );
    }

    public void visitPlaneReflection( PlaneReflection symm )
    {
        visitTransformation( symm );
    }

    public void visitChangeOfBasis( ChangeOfBasis symm )
    {
        visitTransformation( symm );
    }

    public void visitPlaneFromNormalSegment( PlaneFromNormalSegment plane )
    {
        visitPlane( plane );
    }

    public void visitIcosahedralRotation( SymmetryTransformation rotation )
    {
        visitTransformation( rotation );
    }

    public void visitSegmentMidpoint( SegmentMidpoint midpoint )
    {
        visitPoint( midpoint );
    }

    public void visitPointReflection(PointReflection reflection)
    {
        visitTransformation( reflection );
    }

    public void visitTranslation(Translation translation)
    {
        visitTransformation( translation );
    }

    public void visitPolygon( Polygon polygon )
    {
        visitConstruction( polygon );
    }

}
