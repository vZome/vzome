
//(c) Copyright 2011, Scott Vorthmann.

package com.vzome.core.algebra;

import java.util.Arrays;

public class Fields
{
    public interface BigRationalElement<R, T> extends RationalElement<R, T>
    {
        boolean isBig();
        boolean notBig();
    }
    
    public interface RationalElement<R, T> extends Element<T>
    {
        R getNumerator();

        R getDenominator();

        T dividedBy( T that );

        double evaluate();
    }

    public interface Element<T>
    {
        T times( T that );

        T plus( T that );

        T minus( T that );

        T reciprocal();

        T negate();

        boolean isZero();

        boolean isOne();
    }

    
    public static final int rows( Element[][] matrix )
    {
    	return matrix.length;
    }
    
    public static final int columns( Element[][] matrix )
    {
    	return matrix[ 0 ] .length;
    }
    
    public static <T extends Element<T>> void matrixMultiplication( T[][] left, T[][] right, T[][] product )
    {
    	if ( rows( right ) != columns( left ) )
    		throw new IllegalArgumentException( "matrices cannot be multiplied" );
    	if ( rows( product ) != rows( left ) )
    		throw new IllegalArgumentException( "product matrix has wrong number of rows" );
    	if ( columns( right ) != columns( product ) )
    		throw new IllegalArgumentException( "product matrix has wrong number of columns" );
    	
    	for (int i = 0; i < rows( product ); i++) {
			for (int j = 0; j < columns( product ); j++) {
				T sum = null;
				for (int j2 = 0; j2 < columns( left ); j2++) {
					T prod = left[ i ][ j2 ] .times( right[ j2 ][ j ] );
					if ( sum == null )
						sum = prod;
					else
						sum = sum .plus( prod );
				}
				product[ i ][ j ] = sum;
			}
		}
    }
    
    @SuppressWarnings("unchecked")
    public static <T extends Element<T>> void gaussJordanReduction( T[][] m, T[][] adjoined )
    {
        final Object[][] matrix = new Object[ m.length ][];
        for (int i = 0; i < m.length; i++) {
        	matrix[i] = Arrays.copyOf( m[i], m[i].length );
        }
        for ( int upleft = 0; upleft < matrix.length; upleft++ )
        {
            int pivot = -1;
            // find pivot: skip columns all zero, find highest non-zero row
            for ( int j = upleft; j < matrix[0].length; j++ ) {
                for ( int i = upleft; i < matrix.length; i++ ) {
                    T tmp = (T) matrix[ i ][ j ];
                    if ( ! tmp .isZero() ) {
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
                for ( int j = upleft; j < matrix[0].length; j++ ) {
                    T temp = (T) matrix[ upleft ][ j ];
                    matrix[ upleft ][ j ] = matrix[ pivot ][ j ];
                    matrix[ pivot ][ j ] = temp;
                }
                for ( int j = 0; j < adjoined[0].length; j++ ) {
                	T temp = adjoined[ upleft ][ j ];
                	adjoined[ upleft ][ j ] = adjoined[ pivot ][ j ];
                	adjoined[ pivot ][ j ] = temp;
                }
            }

            // normalize the top row
            T tmp = (T) matrix[ upleft ][ upleft ];
            if ( ! tmp .isOne() ) {
                T divisor = ( (T) matrix[ upleft ][ upleft ] ) .reciprocal();
                for ( int j = upleft; j < matrix[0].length; j++ ) {
                    T tmp2 = (T) matrix[ upleft ][ j ];
                    matrix[ upleft ][ j ] = tmp2.times( divisor );
                }
                for ( int j = 0; j < adjoined[0].length; j++ ) {
                	adjoined[ upleft ][ j ] = adjoined[ upleft ][ j ] .times( divisor );
                }
            }

            // zero out all other entries in the upleft column, subtracting m[i,upleft]*m[upleft,j] from m[i,j]
            for ( int i = 0; i < matrix.length; i++ ) {
                T tmp2 = (T) matrix[ i ][ upleft ];
                if ( i != upleft && ! tmp2 .isZero() ) {
                    T factor = (T) matrix[ i ][ upleft ];
                    matrix[ i ][ upleft ] = factor .plus( factor .negate() ); // zero it out
                    for ( int j = upleft+1; j < matrix[0].length; j++ ) {
                        T tmp3 = (T) matrix[ upleft ][ j ];
                        T temp = tmp3 .times( factor );
                        T tmp4 = (T) matrix[ i ][ j ];
                        matrix[ i ][ j ] = tmp4 .plus( temp .negate() );
                    }
                    for ( int j = 0; j < adjoined[0].length; j++ ) {
                    	T temp = adjoined[ upleft ][ j ] .times( factor );
                    	adjoined[ i ][ j ] = adjoined[ i ][ j ] .plus( temp .negate() );
                    }
                }
            }
        }
    }
}
