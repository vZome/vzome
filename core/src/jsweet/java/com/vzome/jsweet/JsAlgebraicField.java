package com.vzome.jsweet;

import com.vzome.core.algebra.AlgebraicField;
import com.vzome.core.algebra.AlgebraicMatrix;
import com.vzome.core.algebra.AlgebraicNumber;
import com.vzome.core.algebra.AlgebraicVector;
import com.vzome.core.math.RealVector;

public class JsAlgebraicField implements AlgebraicField
{
    public JsAlgebraicField()
    {
        // TODO Take a JS object as a parameter, to make this class wrap JS AFs
    }

    @Override
    public String getName()
    {
        return "golden";
    }

    @Override
    public int getOrder()
    {
        return DIVISOR;
    }

    public static final float PHI_VALUE = (float) (( 1.0 + Math.sqrt( 5.0 ) ) / 2.0);

    private static final int ONES_PLACE = 0, PHIS_PLACE = 1, DIVISOR = 2;

    @Override
    public int getNumIrrationals()
    {
        return 1;
    }

    int[] multiply( int[] v1, int[] v2 )
    {
        // TODO support rationals
        int phis = v1[PHIS_PLACE] * v2[ONES_PLACE] + v1[ONES_PLACE] * v2[PHIS_PLACE] + v1[PHIS_PLACE] * v2[PHIS_PLACE];
        int ones = v1[ONES_PLACE] * v2[ONES_PLACE] + v1[PHIS_PLACE] * v2[PHIS_PLACE];

        return new int[]{ ones, phis, 1 };
    }

    float evaluateNumber( int[] factors )
    {
        return ( factors[ ONES_PLACE ] + PHI_VALUE * factors[ PHIS_PLACE ] ) / factors[ DIVISOR ] ;
    }

    @Override
    public AlgebraicNumber createAlgebraicNumberFromTD( int[] trailingDivisorForm )
    {
        return new JsAlgebraicNumber( this, trailingDivisorForm );
    }

    int[] scaleBy( int[] factors, int whichIrrational )
    {
        return null;
    }

    private final static int[] ZERO = { 0, 0, 1 };

    private final static int[] ONE = { 1, 0, 1 };

    @Override
    public AlgebraicNumber zero()
    {
        return new JsAlgebraicNumber( this, ZERO );
    }

    @Override
    public AlgebraicNumber one()
    {
        return new JsAlgebraicNumber( this, ONE );
    }

    @Override
    public AlgebraicVector origin(int dims)
    {
        throw new RuntimeException( "unimplemented" );
    }

    @Override
    public void defineMultiplier(StringBuffer instances, int w)
    {
        throw new RuntimeException( "unimplemented" );
    }

    @Override
    public String getIrrational(int i, int format)
    {
        throw new RuntimeException( "unimplemented" );
    }

    @Override
    public String getIrrational(int which)
    {
        throw new RuntimeException( "unimplemented" );
    }

    @Override
    public AlgebraicVector nearestAlgebraicVector(RealVector target)
    {
        throw new RuntimeException( "unimplemented" );
    }

    @Override
    public AlgebraicNumber createAlgebraicNumber(int[] terms)
    {
        throw new RuntimeException( "unimplemented" );
    }

    @Override
    public AlgebraicNumber createAlgebraicNumber(int[] numerators, int denominator)
    {
        throw new RuntimeException( "unimplemented" );
    }

    @Override
    public AlgebraicNumber createAlgebraicNumber(int ones, int irrat, int denominator, int scalePower)
    {
        throw new RuntimeException( "unimplemented" );
    }

    @Override
    public AlgebraicNumber getGoldenRatio()
    {
        throw new RuntimeException( "unimplemented" );
    }

    @Override
    public AlgebraicNumber createPower(int power)
    {
        throw new RuntimeException( "unimplemented" );
    }

    @Override
    public AlgebraicNumber createPower(int power, int irr)
    {
        throw new RuntimeException( "unimplemented" );
    }

    @Override
    public AlgebraicNumber createRational(long wholeNumber)
    {
        throw new RuntimeException( "unimplemented" );
    }

    @Override
    public AlgebraicNumber createRational( long numerator, long denominator )
    {
        return new JsAlgebraicNumber( this, new int[] { (int) numerator, 0, (int) denominator } );
    }

    @Override
    public AlgebraicNumber getAffineScalar()
    {
        throw new RuntimeException( "unimplemented" );
    }

    @Override
    public AlgebraicNumber getUnitTerm(int n)
    {
        throw new RuntimeException( "unimplemented" );
    }

    @Override
    public AlgebraicVector projectTo3d(AlgebraicVector source, boolean wFirst)
    {
        throw new RuntimeException( "unimplemented" );
    }

    @Override
    public AlgebraicVector basisVector(int dims, int axis)
    {
        throw new RuntimeException( "unimplemented" );
    }

    @Override
    public AlgebraicVector createVector(int[][] nums)
    {
        throw new RuntimeException( "unimplemented" );
    }

    @Override
    public AlgebraicVector createIntegerVector(int[][] nums)
    {
        throw new RuntimeException( "unimplemented" );
    }

    @Override
    public AlgebraicVector createIntegerVectorFromTDs(int[][] nums)
    {
        throw new RuntimeException( "unimplemented" );
    }

    @Override
    public AlgebraicMatrix createMatrix(int[][][] data)
    {
        throw new RuntimeException( "unimplemented" );
    }

    @Override
    public AlgebraicNumber parseLegacyNumber(String val)
    {
        throw new RuntimeException( "unimplemented" );
    }

    @Override
    public AlgebraicNumber parseNumber(String nums)
    {
        throw new RuntimeException( "unimplemented" );
    }

    @Override
    public AlgebraicVector parseVector(String nums)
    {
        throw new RuntimeException( "unimplemented" );
    }

    @Override
    public AlgebraicMatrix identityMatrix(int dims)
    {
        throw new RuntimeException( "unimplemented" );
    }

    @Override
    public int getNumMultipliers()
    {
        throw new RuntimeException( "unimplemented" );
    }

    @Override
    public AlgebraicNumber parseVefNumber(String string, boolean isRational)
    {
        throw new RuntimeException( "unimplemented" );
    }
}
