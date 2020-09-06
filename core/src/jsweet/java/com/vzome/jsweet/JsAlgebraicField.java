package com.vzome.jsweet;

import static jsweet.util.Lang.any;

import com.vzome.core.algebra.AlgebraicField;
import com.vzome.core.algebra.AlgebraicMatrix;
import com.vzome.core.algebra.AlgebraicNumber;
import com.vzome.core.algebra.AlgebraicVector;
import com.vzome.core.math.RealVector;

import def.js.Function;
import def.js.Object;

public class JsAlgebraicField implements AlgebraicField
{
    private final Object delegate;

    public JsAlgebraicField( Object delegate )
    {
        this.delegate = delegate;
    }

    @Override
    public String getName()
    {
        return this.delegate .$get( "name" );
    }

    @Override
    public int getOrder()
    {
        return this.delegate .$get( "order" );
    }

    @Override
    public int getNumIrrationals()
    {
        return getOrder() - 1;
    }

    int[] add( int[] v1, int[] v2 )
    {
        Function f = (Function) this.delegate .$get( "plus" );
        return (int[]) f.$apply( any( v1 ), any( v2 ) );
    }

    int[] multiply( int[] v1, int[] v2 )
    {
        Function f = (Function) this.delegate .$get( "times" );
        return (int[]) f.$apply( any( v1 ), any( v2 ) );
    }

    float evaluateNumber( int[] factors )
    {
        Function f = (Function) this.delegate .$get( "embed" );
        return (float) f.$apply( any( factors ) );
    }

    @Override
    public AlgebraicNumber createAlgebraicNumberFromTD( int[] trailingDivisorForm )
    {
        return new JsAlgebraicNumber( this, trailingDivisorForm );
    }

    @Override
    public AlgebraicNumber createRational( long numerator, long denominator )
    {
        Function f = (Function) this.delegate .$get( "createRational" );
        int[] frac = f.$apply( any( numerator ), any( denominator ) );
        return new JsAlgebraicNumber( this, frac );
    }

    @Override
    public AlgebraicNumber zero()
    {
        return new JsAlgebraicNumber( this, this.delegate .$get( "zero" ) );
    }

    @Override
    public AlgebraicNumber one()
    {
        return new JsAlgebraicNumber( this, this.delegate .$get( "one" ) );
    }

    @Override
    public AlgebraicVector createVector( int[][] nums )
    {
        AlgebraicNumber x = this .createAlgebraicNumberFromTD( nums[0] );
        AlgebraicNumber y = this .createAlgebraicNumberFromTD( nums[1] );
        AlgebraicNumber z = this .createAlgebraicNumberFromTD( nums[2] );
        return new AlgebraicVector( x, y, z );
    }

    
    
    
    
    int[] scaleBy( int[] factors, int whichIrrational )
    {
        throw new RuntimeException( "unimplemented" );
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
