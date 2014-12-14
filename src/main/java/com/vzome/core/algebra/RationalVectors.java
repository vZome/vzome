package com.vzome.core.algebra;


public class RationalVectors extends RationalNumbers
{
    static final int[] negate( int[] v )
    {
        int[] result = new int[ v.length ];
        for ( int j = 0; j < v.length/2; j++ )
            negate( v, j, result, j );
        return result;
    }

    /**
     * vector addition
     * @param v1
     * @param v2
     * @param adding
     * @return
     */
    static final int[] addition( int[] v1, int[] v2, boolean adding )
    {
        int[] result = new int[ v1.length ];
        for ( int j = 0; j < v1.length/2; j++ )
            if ( adding )
                add( v1, j, v2, j, result, j );
            else
                subtract( v1, j, v2, j, result, j );
        return result;
    }

    public static final int[] subtract( int[] v1, int[] v2 )
    {
        return addition( v1, v2, false );
    }

    public static boolean isOrigin( int[] v )
    {
        for( int i = 0; i < v.length/2; i++ )
            if ( ! isZero( v, i ) )
                return false;
        return true;
    }

    public static final int X = 0, Y = 1, Z = 2;

    public static final int W4 = 0, X4 = 1, Y4 = 2, Z4 = 3;
    
    public static int hashCode( int[] vector )
    {
        int result = 0;
        for ( int i = 0, j = 0; i < vector .length; i++, j++ )
            result ^= vector[ i ] << j;
        return result;
    }
}