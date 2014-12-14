
//(c) Copyright 2005, Scott Vorthmann.  All rights reserved.

package com.vzome.core.algebra;

import java.util.Arrays;



public class RationalMatrices extends RationalVectors
{
    public static final boolean equals( int[][] m1, int[][] m2 )
    {
        if ( m1.length != m2.length )
            return false;
        for ( int i = 0; i < m1.length; i++ )
            if ( ! Arrays.equals( m1[ i ], m2[ i ] ) )
                return false;
        return true;
    }
    
    public static final int[][] add( int[][] m1, int[][] m2 )
    {
        return addition( m1, m2, true );
    }
    
    public static final int[][] subtract( int[][] m1, int[][] m2 )
    {
        return addition( m1, m2, false );
    }
    
    private static final int[][] addition( int[][] m1, int m2[][], boolean adding )
    {
        if ( m1.length != m2.length )
            throw new IllegalArgumentException( ( adding? "adding ":"subtracting ") + m2.length + "-row matrix " +
                                                ( adding? "to ":"from " ) + m1.length + "-row matrix." );

        if ( m1[0].length != m2[0].length )
            throw new IllegalArgumentException( ( adding? "adding ":"subtracting ") + m2[0].length/2 + "-column matrix " +
                                                ( adding? "to ":"from " ) + m1[0].length/2 + "-column matrix." );
        
        int[][] result = new int[ m1.length ][ m1[0].length ];
        for ( int i = 0; i < m1.length; i++ )
            result[ i ] = addition( m1[i], m2[i], adding );
        return result;
    }
    
    /**
     * Multiply a matrix times a rational scalar.
     * @param m1 the matrix
     * @param m2 a vector of rationals
     * @param k the index of the scalar in m2
     * @return a matrix with the same dimensions as m1
     */
    public static final int[][] multiply( int[][] m1, int[] m2, int k )
    {
        int[][] result = new int[ m1.length ][ m1[0].length ];
        for ( int i = 0; i < m1.length; i++ )
            for ( int j = 0; j < m1[0].length/2; j++ )
                multiply( m1[i], j, m2, k, result[i], j );
        return result;
    }
    
    
    public static final int[][] multiply( int[][] m1, int[][] m2 )
    {
        if ( m1[0].length != m2.length )
            throw new IllegalArgumentException( "multiplying " + m1[0].length/2 + "-column matrix by " + m2.length + "-row matrix." );

        int[] SUM = new int[2], PRODUCT = new int[2];
        int[][] result = new int[ m1.length ][ m2[0].length ];
        for ( int i = 0; i < m1.length; i++ )
            for ( int j = 0; j < m2[0].length/2; j++ )
            {
                SUM[ 0 ] = 0;
                SUM[ 1 ] = 1;
                for ( int k = 0; k < m1[0].length/2; k++ )
                {
                    multiply( m1[i], k, m2[k], j, PRODUCT, 0 );
                    add( SUM, 0, PRODUCT, 0, SUM, 0 );
                }
                copy( SUM, 0, result[ i ], j );
            }
        return result;
    }
    
    
    public static final void gaussJordanReduction( int[][] matrix, int[][] adjoined )
    {
        int[] temp = new int[ 2 ];
        for ( int upleft = 0; upleft < matrix.length; upleft++ )
        {
            int pivot = -1;
            // find pivot: skip columns all zero, find highest non-zero row
            for ( int j = upleft; j < matrix[0].length/2; j++ ) {
                for ( int i = upleft; i < matrix.length; i++ ) {
                    if ( ! isZero( matrix[i], j ) ) {
                        pivot = i;
                        break;
                    }
                }
                if ( pivot >= 0 ) break;
            }
            if ( pivot < 0 )
                return; // all done, in ReREF
            
            // exchange pivot and top rows
            if ( pivot != upleft ) {
                for ( int j = upleft; j < matrix[0].length/2; j++ ) {
                    copy( matrix[ upleft ], j, temp, 0 );
                    copy( matrix[ pivot ], j, matrix[ upleft ], j );
                    copy( temp, 0, matrix[ pivot ], j );
                }
                if ( adjoined != null )
                    for ( int j = upleft; j < adjoined[0].length/2; j++ ) {
                        copy( adjoined[ upleft ], j, temp, 0 );
                        copy( adjoined[ pivot ], j, adjoined[ upleft ], j );
                        copy( temp, 0, adjoined[ pivot ], j );
                    }
            }
            
            // normalize the top row
            if ( ! isOne( matrix[ upleft ], upleft ) ) {
                int[] divisor = new int[2];
                invert( matrix[ upleft ], upleft, divisor, 0 );
                for ( int j = upleft; j < matrix[0].length/2; j++ )
                    multiply( matrix[ upleft ], j, divisor, 0, matrix[ upleft ], j );
                if ( adjoined != null )
                    for ( int j = 0; j < adjoined[0].length/2; j++ )
                        multiply( adjoined[ upleft ], j, divisor, 0, adjoined[ upleft ], j );
            }
            
            // zero out all other entries in the upleft column, subtracting m[i,upleft]*m[upleft,j] from m[i,j]
            for ( int i = 0; i < matrix.length; i++ )
                if ( i != upleft && ! isZero( matrix[i], upleft ) ) {
                    int[] factor = new int[2];
                    copy( matrix[i], upleft, factor, 0 );
                    copy( ZERO, 0, matrix[i], upleft );
                    for ( int j = upleft+1; j < matrix[0].length/2; j++ ) {
                        multiply( matrix[upleft], j, factor, 0, temp, 0 );
                        subtract( matrix[i], j, temp, 0, matrix[i], j );
                    }
                    if ( adjoined != null )
                        for ( int j = 0; j < adjoined[0].length/2; j++ ) {
                            multiply( adjoined[upleft], j, factor, 0, temp, 0 );
                            subtract( adjoined[i], j, temp, 0, adjoined[i], j );
                        }
                }
        }
    }

    /**
     * Basic matrix multiplication of a column vector: m*v.
     * @param m a rational matrix
     * @param v a rational column vector
     * @return
     */
    public final static int[] transform( int[][] m, int[] v )
    {
        if ( m[0].length != v.length )
            throw new IllegalArgumentException( "multiplying " + m[0].length/2 + "-column matrix by " + v.length + "-row vector." );

        int[] SUM = new int[2], PRODUCT = new int[2];
        int[] result = new int[ m.length*2 ];
        for ( int i = 0; i < m.length; i++ )
        {
            SUM[ 0 ] = 0;
            SUM[ 1 ] = 1;
            for ( int k = 0; k < m[0].length/2; k++ )
            {
                multiply( m[i], k, v, k, PRODUCT, 0 );
                add( SUM, 0, PRODUCT, 0, SUM, 0 );
            }
            copy( SUM, 0, result, i );
        }
        return result;
    }


    /**
     * Basic matrix multiplication of a row vector: v*m.
     * @param m a rational matrix
     * @param v a rational row vector
     * @return
     */
    public final static int[] transform( int[] v, int[][] m )
    {
        if ( m.length != v.length/2 )
            throw new IllegalArgumentException( "multiplying " + m.length + "-row matrix by " + v.length/2 + "-column vector." );

        int[] SUM = new int[2], PRODUCT = new int[2];
        int[] result = new int[ v.length ];
        for ( int j = 0; j < m[0].length/2; j++ )
        {
            SUM[ 0 ] = 0;
            SUM[ 1 ] = 1;
            for ( int k = 0; k < m.length; k++ )
            {
                multiply( v, k, m[k], j, PRODUCT, 0 );
                add( SUM, 0, PRODUCT, 0, SUM, 0 );
            }
            copy( SUM, 0, result, j );
        }
        return result;
    }

    public static final int[][] negate( int[][] v )
    {
        int[][] result = new int[ v.length ][];
        for ( int j = 0; j < v.length; j++ )
            result[j] = negate( v[j] );
        return result;
    }

    private static final int[][][] IDENTITIES = new int[ 60 ][][];
    
    /**
     * Return a static identity matrix with the given number of rows.
     * @param size number of rows, doubled for number of columns
     * @return
     */
    public final static int[][] identity( int size )
    {
        int[][] identity = IDENTITIES[ size ];
        if ( identity == null ) {
            identity = new int[ size ][ size*2 ];
            IDENTITIES[ size ] = identity;
            for ( int i = 0; i < size; i++ )
                for ( int j = 0; j < size; j++ )
                    copy( i==j? ONE : ZERO, 0, identity[ i ], j );
        }
        return identity;
    }

    public final static int[] /*AlgebraicVector*/ scaleVector( AlgebraicField field, int[] /*AlgebraicVector*/ vector, int[] /*AlgebraicNumber*/ scale )
    {
        int order = scale .length / 2;
        int dim = vector .length / 2;
        return transform( field .createScalingMatrix( scale, dim/order ), vector );
    }
    
    public static int[][] copy( int[][] source )
    {
        int[][] target = new int[ source.length ][ source[0] .length ];
        for ( int i = 0; i < source.length; i++ )
            for ( int j = 0; j < source[0].length/2; j++ )
                copy( source[ i ], j, target[ i ], j );
        return target;
    }

    public static int[][] invert( int[][] matrix )
    {
        boolean isZero = true;
        for ( int i = 0; i < matrix.length; i++ )
            if ( ! isOrigin( matrix[ i ] ) )
            {
                isZero = false;
                break;
            }
        if ( isZero )
            return null;
        // TODO check non-invertible in general, not just for zero
        int[][] result = copy( identity( matrix.length ) );
        int[][] source = copy( matrix );
        gaussJordanReduction( source, result );  // TODO exception when non-invertible, or return null
        return result;
    }
}
