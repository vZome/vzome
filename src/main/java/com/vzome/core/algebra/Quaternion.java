
//(c) Copyright 2007, Scott Vorthmann.  All rights reserved.

package com.vzome.core.algebra;


public class Quaternion
{
    int[][] /*AlgebraicMatrix*/ representation, transpose;
    
    private final AlgebraicField field;
    
    public Quaternion( AlgebraicField field, int[] /*AlgebraicVector*/ vector )
    {
        this.field = field;
        int order = field .getOrder();
        int w_offset = 0;
        int[] /*AlgebraicNumber*/ factor = field .createRational( new int[]{ 0,1 } );
        if ( vector .length > 6 * order ) // a 4D vector
        {
            factor = field .getVectorComponent( vector, 0 );
            w_offset = 1;
        }
        
        representation = field .createScalingMatrix( factor, 4 );
        
        factor = field .getVectorComponent( vector, 0 + w_offset );
        field .createRepresentation( factor, 0, representation, 1*order, 0*order );
        field .createRepresentation( factor, 0, representation, 3*order, 2*order );
        factor = field .negate( factor );
        field .createRepresentation( factor, 0, representation, 0*order, 1*order );
        field .createRepresentation( factor, 0, representation, 2*order, 3*order );
        
        factor = field .getVectorComponent( vector, 1 + w_offset );
        field .createRepresentation( factor, 0, representation, 1*order, 3*order );
        field .createRepresentation( factor, 0, representation, 2*order, 0*order );
        factor = field .negate( factor );
        field .createRepresentation( factor, 0, representation, 3*order, 1*order );
        field .createRepresentation( factor, 0, representation, 0*order, 2*order );

        factor = field .getVectorComponent( vector, 2 + w_offset );
        field .createRepresentation( factor, 0, representation, 3*order, 0*order );
        field .createRepresentation( factor, 0, representation, 2*order, 1*order );
        factor = field .negate( factor );
        field .createRepresentation( factor, 0, representation, 1*order, 2*order );
        field .createRepresentation( factor, 0, representation, 0*order, 3*order );
        
        // NOTE that transpose is a transpose by field elements (order X order submatrices),
        //   NOT by rationals
        
        if ( w_offset == 1 ) // a 4D vector
            factor = field .getVectorComponent( vector, 0 );
        else
            factor = field .createRational( new int[]{ 0,1 } );

        transpose = field .createScalingMatrix( factor, 4 );
        
        factor = field .getVectorComponent( vector, 0 + w_offset );
        field .createRepresentation( factor, 0, transpose, 0*order, 1*order );
        field .createRepresentation( factor, 0, transpose, 2*order, 3*order );
        factor = field .negate( factor );
        field .createRepresentation( factor, 0, transpose, 1*order, 0*order );
        field .createRepresentation( factor, 0, transpose, 3*order, 2*order );
        
        factor = field .getVectorComponent( vector, 1 + w_offset );
        field .createRepresentation( factor, 0, transpose, 3*order, 1*order );
        field .createRepresentation( factor, 0, transpose, 0*order, 2*order );
        factor = field .negate( factor );
        field .createRepresentation( factor, 0, transpose, 1*order, 3*order );
        field .createRepresentation( factor, 0, transpose, 2*order, 0*order );

        factor = field .getVectorComponent( vector, 2 + w_offset );
        field .createRepresentation( factor, 0, transpose, 1*order, 2*order );
        field .createRepresentation( factor, 0, transpose, 0*order, 3*order );
        factor = field .negate( factor );
        field .createRepresentation( factor, 0, transpose, 3*order, 0*order );
        field .createRepresentation( factor, 0, transpose, 2*order, 1*order );
    }
    
    private int[] /*AlgebraicVector*/ conjugate( int[] /*AlgebraicVector*/ q )
    {
        int[] result = this.field .origin( 4 );
        field .setVectorComponent( result, 3, field .negate( field .getVectorComponent( q, 3 ) ) );
        field .setVectorComponent( result, 1, field .negate( field .getVectorComponent( q, 1 ) ) );
        field .setVectorComponent( result, 2, field .negate( field .getVectorComponent( q, 2 ) ) );
        field .setVectorComponent( result, 0, field .getVectorComponent( q, 0 ) );
        return result;
    }
    
    public int[] /*AlgebraicVector*/ reflect( int[] /*AlgebraicVector*/ v )
    {
        int [] reflection = rightMultiply( conjugate( v ) );
        reflection = leftMultiply( reflection );
        return field .negate( reflection );
    }
    
    /**
     * Compute the product this*q.
     */
    public int[] /*AlgebraicVector*/ rightMultiply( int[] /*AlgebraicVector*/ q )
    {
        return RationalMatrices .transform( representation, q );
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
    public int[] /*AlgebraicVector*/ leftMultiply( int[] /*AlgebraicVector*/ q )
    {
        int[] result = conjugate( q );
        result = RationalMatrices .transform( transpose, result );  // transpose is the same as the conjugate of representation
        return conjugate( result );
    }
}
