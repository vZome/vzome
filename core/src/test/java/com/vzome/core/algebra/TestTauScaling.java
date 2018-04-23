
//(c) Copyright 2007, Scott Vorthmann.  All rights reserved.

package com.vzome.core.algebra;

public class TestTauScaling
{

    /**
     * 
     * Algorithm:
     *  
     *   if a>0 and b>0, mult by (1,-1) until one goes negative... then last positive pair is it
     *   
     *   if a<0 or b<0, mult by (1,0) until both are positive... that pair is it
     *   
     *   if ever a<0 and b<0, you have a negative number, flip the signs and start over
     *   
     *   if resulting pair has a==0, you have a power of phi
     *   
     * @param args
     */
    public static void main( String[] args )
    {
        int a = Integer .parseInt( args[0] );
        int b = Integer .parseInt( args[1] );
        for ( int i = 0; i < 20; i++ )
        {
            System .out .println( a + "  " + b );
            int b1 = a;
            a = a + b;
            b = b1;
        }
    }

}
