
//(c) Copyright 2005, Scott Vorthmann.  All rights reserved.

package com.vzome.core.algebra;

import java.util.StringTokenizer;



/**
 * All public methods operate in indexed arrays, and all assume the index is for a pair.
 * That is, if an array has value { 2, 1, 4, 5, 0, 1 }, representing three rational numbers ( 2, 4/5, 0 ),
 * then the index passed in to access 0 will be 2, rather than 4.
 * 
 */
public class RationalNumbers
{
    public static final int[] ZERO = { 0, 1 };
    public static final int[] ONE = { 1, 1 };
    
    public final static long gcd( long u, long v )
    {
        // TODO implement faster binary GCD, ala Knuth 4.5.2
        u = Math.abs( u );
        v = Math.abs( v );
        while ( v != 0 )
        {
            long r = u % v;
            u = v;
            v = r;
        }
        return u;
    }
    
    private final static int fitLong( long i )
    {
        if ( i >= Integer.MAX_VALUE )
            throw new IllegalStateException( "integer overflow" );
        return (int) i;
    }

    /**
     * 
     * @param u
     * @param u_
     * @param v
     * @param v_
     * @param result
     * @param i index of the target pair of ints in result... will be doubled internally
     */
    public final static void add( int[] q1, int i, int[] q2, int j, int[] result, int k )
    {
        i <<= 1;
        j <<= 1;
        k <<= 1;
        int u = q1[ i ];
        int u_ = q1[ i+1 ];
        int v = q2[ j ];
        int v_ = q2[ j+1 ];
        // per Knuth 4.5.1
        long d1 = gcd( u_, v_ );
        if ( d1 == 1 ) {
            result[ k ] = u * v_ + v * u_;
            result[ k+1 ] = u_ * v_;
        }
        else {
            long t = u * (v_/d1) + v * (u_/d1);
            long d2 = gcd( t, d1 );
            result[ k ] = fitLong( t / d2 );
            result[ k+1 ] = fitLong( (u_/d1) * (v_/d2) );
        }
    }

    public final static void subtract( int[] q1, int i, int[] q2, int j, int[] result, int k )
    {
        i <<= 1;
        j <<= 1;
        k <<= 1;
        int u = q1[ i ];
        int u_ = q1[ i+1 ];
        int v = q2[ j ];
        int v_ = q2[ j+1 ];
        // per Knuth 4.5.1
        long d1 = gcd( u_, v_ );
        if ( d1 == 1 ) {
            result[ k ] = u * v_ - v * u_;
            result[ k+1 ] = u_ * v_;
        }
        else {
            long t = u * (v_/d1) - v * (u_/d1);
            long d2 = gcd( t, d1 );
            result[ k ] = fitLong( t / d2 );
            result[ k+1 ] = fitLong( (u_/d1) * (v_/d2) );
        }
    }
    
    public final static void multiply( int[] q1, int i, int[] q2, int j, int[] result, int k )
    {
        i <<= 1;
        j <<= 1;
        k <<= 1;
        int u = q1[ i ];
        int u_ = q1[ i+1 ];
        int v = q2[ j ];
        int v_ = q2[ j+1 ];
        // per Knuth 4.5.1
        if ( u == 0 || v == 0 ) {
            result[ k ] = 0;
            result[ k+1 ] = 1;
            return;
        }
        long d1 = gcd( u, v_ );
        long d2 = gcd( u_, v );
        result[ k ] = fitLong( (u/d1) * (v/d2) );
        result[ k+1 ] = fitLong( (u_/d2) * (v_/d1) );
    }

    public final static void divide( int[] q1, int i, int[] q2, int j, int[] result, int k )
    {
        i <<= 1;
        j <<= 1;
        k <<= 1;
        int u = q1[ i ];
        int u_ = q1[ i+1 ];
        int v = q2[ j ];
        int v_ = q2[ j+1 ];
        // per Knuth 4.5.1
        if ( u == 0 ) {
            result[ k ] = 0;
            result[ k+1 ] = 1;
            return;
        }
        if ( v == 0 )
            v = 1 / v; // force an ArithmeticException for divide by zero
        long d1 = gcd( u, v );
        long d2 = gcd( u_, v_ );
        result[ k ] = fitLong( (u/d1) * (v_/d2) * (v>0?1:-1) );
        result[ k+1 ] = fitLong( Math .abs( (u_/d2) * (v/d1) ) );
    }
    
    public final static boolean isZero( int[] q, int i )
    {
        return q[ i<<1 ] == 0;
    }
    
    public final static boolean isOne( int[] q, int i )
    {
        i <<= 1;
        return ( q[ i ] == 1 ) && ( q[ i+1 ] == 1 );
    }
    
    public final static boolean isNegative( int[] q, int i )
    {
        return q[ i<<1 ] < 0;
    }
    
    public static final boolean equals( int[] m1, int i1, int[] m2, int i2 )
    {
        i1 <<= 1;
        i2 <<= 1;
        return m1[ i1 ] == m2[ i2 ] && m1[ i1+1 ] == m2[ i2+1 ];
    }
    
    public final static void copy( int[] q, int i, int[] result, int j )
    {
        i <<= 1;
        j <<= 1;
        result[ j ] = q[ i ];
        result[ j+1 ] = q[ i+1 ];
    }
    
    public final static void invert( int[] q, int i, int[] result, int j )
    {
        i <<= 1;
        j <<= 1;
        boolean negate = q[ i ] < 0;
        result[ j ] = negate? -q[ i+1 ] : q[ i+1 ];
        result[ j+1 ] = negate? -q[ i ] : q[ i ];
    }
    
    public final static void negate( int[] q, int i, int[] result, int j )
    {
        i <<= 1;
        j <<= 1;
        result[ j ] = -q[ i ];
        result[ j+1 ] = q[ i+1 ];
    }

    public final static double getReal( int[] q, int i )
    {
        i <<= 1;
        if ( q[ i+1 ] == 1 )
            return (double) q[ i ];
        else
            return ((double) q[ i ]) / ((double) q[ i+1 ]);
    }

    public final static String toString( int[] q, int i )
    {
        i <<= 1;
        if ( q[ i+1 ] == 1 )
            return Integer .toString( q[ i ] );
        else
            return Integer .toString( q[ i ] ) + "/" + Integer .toString( q[ i+1 ] );
    }

    public final static String toString( int[] q )
    {
        StringBuffer buffer = new StringBuffer();
        for ( int i = 0; i < q .length / 2; i++ )
        {
            if ( i > 0 )
                buffer .append( ' ' );
            buffer .append( toString( q, i ) );
        }
        return buffer .toString();
    }

    public static final int[] parseRationalVector( String nums )
    {
        StringTokenizer tokens = new StringTokenizer( nums, " " );
        int[] result = new int[ 2 * tokens .countTokens() ];
        for ( int i = 0; tokens .hasMoreTokens(); i++ )
            parseRational( tokens .nextToken(), result, i );
        return result;
    }

    public static final void parseRational( String string, int[] result, int i )
    {
        int slash = string .indexOf( '/' );
        if ( slash < 0 )
        {
            result[ i*2 ] = Integer .parseInt( string );
            result[ i*2 + 1 ] = 1;
        }
        else {
            result[ i*2 ] = Integer .parseInt( string .substring( 0, slash ) );
            result[ i*2 + 1 ] = Integer .parseInt( string .substring( slash+1 ) );
        }
    }
}
