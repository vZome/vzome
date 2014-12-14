
//(c) Copyright 2005, Scott Vorthmann.  All rights reserved.

package com.vzome.core.algebra;


public class TriacontagonField extends AlgebraicField
{
    public static final TriacontagonField INSTANCE = new TriacontagonField();
    
    private TriacontagonField()
    {
        super( "triacontagon" );
    };
    
    private static final double COS = Math .cos( Math.PI / 30d ), SIN = Math .sin( Math.PI / 30d );

    public double evaluateNumber( int[] representation )
    {
        return 0d; // TODO
    }

    public void getNumberExpression( StringBuffer buf, int[] an )
    {
        // TODO
    }

    public void createRepresentation( int[] number, int i, int[][] rep, int j, int k )
    {
        int a = i+0;
        int b = i+1;
        int c = i+2;
        RationalNumbers .copy( number, a, rep[j+0], k+0 ); RationalNumbers .copy( number, b, rep[j+0], k+1 );          RationalNumbers .copy( number, c, rep[j+0], k+2 );
        RationalNumbers .copy( number, b, rep[j+1], k+0 ); RationalMatrices .add( number, a, number, c, rep[j+1], k+1 ); RationalMatrices .add( number, b, number, c, rep[j+1], k+2 );
        RationalNumbers .copy( number, c, rep[j+2], k+0 ); RationalNumbers .copy( rep[j+1], k+2, rep[j+2], k+1 );         RationalMatrices .add( number, a, rep[j+1], k+2, rep[j+2], k+2 );
    }

    public int getOrder()
    {
        return 8;
    }

    public int[] createAlgebraicNumber( int ones, int irrat, int denominator, int power )
    {
        // TODO Auto-generated method stub
        return null;
    }

    public int[] getDefaultStrutScaling()
    {
        // TODO Auto-generated method stub
        return null;
    }

    public String getIrrational( int which )
    {
        return "IRRATIONAL" + which;
    }
}
