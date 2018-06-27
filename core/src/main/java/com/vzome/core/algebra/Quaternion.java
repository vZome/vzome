
//(c) Copyright 2007, Scott Vorthmann.  All rights reserved.

package com.vzome.core.algebra;


public class Quaternion
{
    @Override
	public String toString() {
		return "Quaternion: " + vector .toString();
	}

	AlgebraicMatrix representation, transpose;
    
    private final AlgebraicField field;
    
    private final AlgebraicVector vector;
    
    public Quaternion( AlgebraicField field, AlgebraicVector vector )
    {
        this.field = field;
        this.vector = vector;
        int w_offset = 0;
        AlgebraicNumber factor = field .createRational( 0 );
        if ( vector .dimension() > 3 ) // a 4D vector
        {
            factor = vector .getComponent( 0 );
            w_offset = 1;
        }
        
        representation = field .identityMatrix( 4 ) .timesScalar( factor );

        factor = vector .getComponent( 0 + w_offset );
        representation .setElement( 1, 0, factor );
        representation .setElement( 3, 2, factor );
        factor = factor .negate();
        representation .setElement( 0, 1, factor );
        representation .setElement( 2, 3, factor );
        
        factor = vector .getComponent( 1 + w_offset );
        representation .setElement( 1, 3, factor );
        representation .setElement( 2, 0, factor );
        factor = factor .negate();
        representation .setElement( 3, 1, factor );
        representation .setElement( 0, 2, factor );

        factor = vector .getComponent( 2 + w_offset );
        representation .setElement( 3, 0, factor );
        representation .setElement( 2, 1, factor );
        factor = factor .negate();
        representation .setElement( 1, 2, factor );
        representation .setElement( 0, 3, factor );
                
        if ( w_offset == 1 ) // a 4D vector
            factor = vector .getComponent( 0 );
        else
            factor = field .createRational( 0 );

        transpose = field .identityMatrix( 4 ) .timesScalar( factor );
        
        factor = vector .getComponent( 0 + w_offset );
        transpose .setElement( 0, 1, factor );
        transpose .setElement( 2, 3, factor );
        factor = factor .negate();
        transpose .setElement( 1, 0, factor );
        transpose .setElement( 3, 2, factor );
        
        factor = vector .getComponent( 1 + w_offset );
        transpose .setElement( 3, 1, factor );
        transpose .setElement( 0, 2, factor );
        factor = factor .negate();
        transpose .setElement( 1, 3, factor );
        transpose .setElement( 2, 0, factor );
        
        factor = vector .getComponent( 2 + w_offset );
        transpose .setElement( 1, 2, factor );
        transpose .setElement( 0, 3, factor );
        factor = factor .negate();
        transpose .setElement( 3, 0, factor );
        transpose .setElement( 2, 1, factor );
    }
    
    public AlgebraicVector getVector() {
        return vector;
    }
    
    private AlgebraicVector conjugate( AlgebraicVector q )
    {
        AlgebraicVector result = this.field .origin( 4 );
        result .setComponent( 3, q .getComponent( 3 ) .negate() );
        result .setComponent( 1, q .getComponent( 1 ) .negate() );
        result .setComponent( 2, q .getComponent( 2 ) .negate() );
        result .setComponent( 0, q .getComponent( 0 ) );
        return result;
    }
    
    public AlgebraicVector reflect( AlgebraicVector v )
    {
        AlgebraicVector reflection = rightMultiply( conjugate( v ) );
        reflection = leftMultiply( reflection );
        return reflection .negate();
    }
    
    /**
     * Compute the product this * q.
     * @param q
     * @return 
     */
    public AlgebraicVector rightMultiply( AlgebraicVector q )
    {
        return representation .timesColumn( q );
    }

    /**
     * Compute the product q*this.
     * This is computed using the identities:
     * 
     *     conjugate( q*this ) == conjugate( this ) * conjugate( q )
     *     
     *     q * this == conjugate( conjugate( this ) * conjugate( q ) )
     *     
     * @param q
     * @return
     */
    public AlgebraicVector leftMultiply( AlgebraicVector q )
    {
        AlgebraicVector result = conjugate( q );
        result = transpose .timesColumn( result );  // transpose is the same as the conjugate of representation
        return conjugate( result );
    }
}
