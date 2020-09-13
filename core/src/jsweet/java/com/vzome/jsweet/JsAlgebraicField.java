package com.vzome.jsweet;

import static jsweet.util.Lang.any;

import java.util.ArrayList;

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

    /**
     * Positive powers of the irrationals.
     */
    private final ArrayList<AlgebraicNumber>[] positivePowers;

    /**
     * Negative powers of the irrationals.
     */
    private final ArrayList<AlgebraicNumber>[] negativePowers;

    public JsAlgebraicField( Object delegate )
    {
        this.delegate = delegate;
        int order = delegate .$get( "order" );
        positivePowers = new ArrayList[ order-1 ];
        negativePowers = new ArrayList[ order-1 ];
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

    int[] subtract( int[] v1, int[] v2 )
    {
        Function f = (Function) this.delegate .$get( "minus" );
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

    int[] reciprocal( int[] factors )
    {
        Function f = (Function) this.delegate .$get( "reciprocal" );
        return (int[]) f.$apply( any( factors ) );
    }

    int[] negate( int[] factors )
    {
        Function f = (Function) this.delegate .$get( "negate" );
        return (int[]) f.$apply( any( factors ) );
    }

    @Override
    public AlgebraicNumber createAlgebraicNumberFromTD( int[] trailingDivisorForm )
    {
        return new JsAlgebraicNumber( this, trailingDivisorForm );
    }

    public AlgebraicNumber createAlgebraicNumberFromPairs( int[] pairs )
    {
        Function f = (Function) this.delegate .$get( "createRationalFromPairs" );
        int[] frac = f.$apply( any( pairs ) );
        return new JsAlgebraicNumber( this, frac );
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
        AlgebraicNumber x = this .createAlgebraicNumberFromPairs( nums[0] );
        AlgebraicNumber y = this .createAlgebraicNumberFromPairs( nums[1] );
        AlgebraicNumber z = this .createAlgebraicNumberFromPairs( nums[2] );
        return new AlgebraicVector( x, y, z );
    }

    public AlgebraicVector createVectorFromTDs( int[][] nums )
    {
        AlgebraicNumber x = this .createAlgebraicNumberFromTD( nums[0] );
        AlgebraicNumber y = this .createAlgebraicNumberFromTD( nums[1] );
        AlgebraicNumber z = this .createAlgebraicNumberFromTD( nums[2] );
        return new AlgebraicVector( x, y, z );
    }

    @Override
    public AlgebraicVector origin( int dims )
    {
        AlgebraicNumber zero = this .zero();
        return new AlgebraicVector( zero, zero, zero );
    }

    @Override
    public boolean scale4dRoots()
    {
        return false;
    }

    @Override
    public boolean doubleFrameVectors()
    {
        return false;
    }

    @Override
    public AlgebraicVector basisVector( int dims, int axis )
    {
        AlgebraicVector result = origin( dims );
        return result .setComponent( axis, this .one() );
    }

    @Override
    public AlgebraicNumber createRational( long wholeNumber )
    {
        return this .createRational( wholeNumber, 1 );
    }

    public final AlgebraicNumber createPower( int power )
    {
        return this .createPower( power, 1 );
    }

    @Override
    public AlgebraicNumber createPower( int power, int irr )
    {
        AlgebraicNumber one = this .one();
        if ( power == 0 || irr == 0 )
            return one;
        irr -= 1;  // no powers recorded for 0
        if ( power > 0 )
        {
            if ( positivePowers[irr] == null )
                positivePowers[irr] = new ArrayList<>( 8 );
            // fill in any missing powers at the end of the list
            if(power >= positivePowers[irr] .size()) {
                if (positivePowers[irr].isEmpty()) {
                    positivePowers[irr].add(one);
                    positivePowers[irr].add( getUnitTerm( irr+1 ) );
                }
                int size = positivePowers[irr] .size();
                AlgebraicNumber irrat = this .positivePowers[irr] .get( 1 );
                AlgebraicNumber last = this .positivePowers[irr] .get( size - 1 );
                for (int i = size; i <= power; i++) {
                    AlgebraicNumber next = last .times( irrat );
                    this .positivePowers[irr] .add( next );
                    last = next;
                }
            }
            return positivePowers[irr] .get( power );
        }
        else
        {
            power = - power; // power is now a positive number and can be used as an offset into negativePowers
            // fill in any missing powers at the end of the list
            if ( negativePowers[irr] == null )
                negativePowers[irr] = new ArrayList<>( 8 );
            if(power >= negativePowers[irr] .size()) {
                if (negativePowers[irr].isEmpty()) {
                    negativePowers[irr].add(one);
                    negativePowers[irr].add( getUnitTerm( irr+1 ) .reciprocal() );
                }
                int size = negativePowers[irr] .size();
                AlgebraicNumber irrat = this .negativePowers[irr] .get( 1 );
                AlgebraicNumber last = this .negativePowers[irr] .get( size - 1 );
                for (int i = size; i <= power; i++) {
                    AlgebraicNumber next = last .times( irrat );
                    this .negativePowers[irr] .add( next );
                    last = next;
                }
            }
            return negativePowers[irr] .get( power );
        }
    }

    /**
     * @param n specifies the ordinal of the term in the AlgebraicNumber which will be set to one.
     * When {@code n == 0}, the result is the same as {@code createRational(1)}.
     * When {@code n == 1}, the result is the same as {@code createPower(1)}.
     * When {@code n < 0}, the result will be {@code zero()}.
     * When {@code n >= getOrder()}, an IndexOutOfBoundsException will be thrown.
     * @return an AlgebraicNumber with the factor specified by {@code n} set to one.
     */
    public final AlgebraicNumber getUnitTerm( int n )
    {
        if ( n < 0 ) {
            return zero();
        }
        int[] factors = this .zero() .toTrailingDivisor(); // makes a copy
        factors[ n ] = 1;
        return createAlgebraicNumber( factors );
    }

    /**
     * Generates an AlgebraicNumber with integer terms (having only unit denominators).
     * Use {@code createAlgebraicNumber( int[] numerators, int denominator )} 
     * or {@code createAlgebraicNumber( BigRational[] factors )} 
     * if denominators other than one are required.
     * @param terms
     * @return
     */
    @Override
    public AlgebraicNumber createAlgebraicNumber( int[] terms )
    {
        return this .createAlgebraicNumber( terms, 1 );
    }

    @Override
    public AlgebraicNumber createAlgebraicNumber( int[] numerators, int denominator )
    {
        int[] factors = new int[ numerators.length + 1 ];
        System .arraycopy( numerators, 0, factors, 0, numerators.length );
        factors[ numerators.length ] = denominator;
        return new JsAlgebraicNumber( this, factors );
    }

    @Override
    public AlgebraicNumber getGoldenRatio()
    {
        int[] value = this.delegate .$get( "goldenRatio" );
        if ( value == null )
            return null;
        return new JsAlgebraicNumber( this, value );
    }

    
    
    
    
    int[] scaleBy( int[] factors, int whichIrrational )
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
    public AlgebraicNumber createAlgebraicNumber(int ones, int irrat, int denominator, int scalePower)
    {
        throw new RuntimeException( "unimplemented" );
    }

    @Override
    public AlgebraicNumber getAffineScalar()
    {
        throw new RuntimeException( "unimplemented" );
    }

    @Override
    public AlgebraicVector projectTo3d(AlgebraicVector source, boolean wFirst)
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
