

package com.vzome.core.math;

import java.util.ArrayList;

import org.w3c.dom.Element;

import com.vzome.core.algebra.RationalNumbers;
import com.vzome.core.math.symmetry.Symmetry;

public class GoldenField implements IntegralNumberField
{
    public static final IntegralNumberField INSTANCE = new GoldenField();
    
    private final ArrayList mSymmetries = new ArrayList();
    
    protected GoldenField() {};
    
    static {
        IntegralNumberField .FIELDS .put( "golden", INSTANCE );
        GoldenNumber .FACTORY = INSTANCE;
        GoldenNumberVector .FACTORY = INSTANCE;
        GoldenNumberMatrix .FACTORY = INSTANCE;
    }

    public IntegralNumber createIntegralNumber( int taus, int ones, int divisor, int power )
    {
        if ( taus==0 && ones==0 )
            return GoldenNumber.ZERO;
        if ( taus==0 && ones==1 && divisor==1 && power==0 )
            return GoldenNumber.ONE;
        
        // don't want to turn a power==0 into anything else, screws up plus() recursion
//        if ( taus==1 && ones==0 && divisor==1 && power==0 )
//            return GoldenNumber.TAU;
        if ( taus==0 && ones==1 && divisor==1 && power==1 )
            return GoldenNumber.TAU;
        
        // TODO generalized interning for all TAU_POWERS... but make sure that
        // the power==0 line is an attractor so plus works?
        
        return new GoldenNumber( taus, ones, divisor, power );
    }

    public IntegralNumber parseString( String string )
    {
        return GoldenNumber .parseString( string );
    }

    public GoldenVector parseXml( Element elem )
    {
        String val = elem .getAttribute( "x" );
        IntegralNumber x = (val==null || val .isEmpty() )? zero() : parseString( val );
        val = elem .getAttribute( "y" );
        IntegralNumber y = (val==null || val .isEmpty() )? zero() : parseString( val );
        val = elem .getAttribute( "z" );
        IntegralNumber z = (val==null || val .isEmpty() )? zero() : parseString( val );
        val = elem .getAttribute( "w" );
        IntegralNumber w = (val==null || val .isEmpty() )? zero() : parseString( val );
        return createGoldenVector( x, y, z, w, GoldenNumber.ONE );
    }
    
    public int[] parseRationalVector( Element elem )
    {
        GoldenVector gv = parseXml( elem );
        int[] w = gv .getW() .getAlgebraicNumber();
        int[] result = new int[ 4 * w.length ];
        RationalNumbers .copy( w, 0, result, 0 );
        RationalNumbers .copy( w, 1, result, 1 );
        int[] x = gv .getX() .getAlgebraicNumber();
        RationalNumbers .copy( x, 0, result, 2 );
        RationalNumbers .copy( x, 1, result, 3 );
        int[] y = gv .getY() .getAlgebraicNumber();
        RationalNumbers .copy( y, 0, result, 4 );
        RationalNumbers .copy( y, 1, result, 5 );
        int[] z = gv .getZ() .getAlgebraicNumber();
        RationalNumbers .copy( z, 0, result, 6 );
        RationalNumbers .copy( z, 1, result, 7 );
        return result;
    }
    
    public String getName()
    {
        return "golden";
    }

    public GoldenVector createGoldenVector( IntegralNumber x, IntegralNumber y, IntegralNumber z, IntegralNumber w, IntegralNumber factor )
    {
        return new GoldenNumberVector( x, y, z, w, factor );
    }

    public GoldenVector origin()
    {
        return GoldenNumberVector .ORIGIN;
    }

    public GoldenVector getAxis( int axis )
    {
        if ( axis == GoldenVector .X_AXIS )
            return GoldenNumberVector .X;
        if ( axis == GoldenVector .Y_AXIS )
            return GoldenNumberVector .Y;
        if ( axis == GoldenVector .Z_AXIS )
            return GoldenNumberVector .Z;
        if ( axis == GoldenVector .W_AXIS )
            return GoldenNumberVector .W;
        return null;
    }

    public GoldenMatrix createGoldenMatrix( GoldenVector x, GoldenVector y, GoldenVector z, GoldenVector w )
    {
        return new GoldenNumberMatrix( x, y, z, w );
    }

    public GoldenMatrix identity()
    {
        return GoldenNumberMatrix.IDENTITY_MATRIX;
    }

    public IntegralNumber zero()
    {
        return GoldenNumber.ZERO;
    }

    public IntegralNumber one()
    {
        return GoldenNumber.ONE;
    }
    
    public void addSymmetry( Symmetry symm )
    {
        mSymmetries .add( symm );
    }

    public Symmetry[] getSymmetries()
    {
        return (Symmetry[]) mSymmetries .toArray( new Symmetry[]{} );
    }

    public GoldenVector inferGoldenVector( float x, float y, float z )
    {
        return new GoldenNumberVector( inferGoldenNumber( x ), inferGoldenNumber( y ), inferGoldenNumber( z ), GoldenNumber.ZERO, GoldenNumber.ONE );
    }
    
    
    private static final float EPSILON = 0.001f;
    
    public IntegralNumber inferGoldenNumber( float value )
    {
        if ( value - 0f < EPSILON )
            return GoldenNumber.ZERO;
        boolean negate = value < 0f;
        value = Math .abs( value );
        if ( Math .abs( value - 1f ) < EPSILON )
            return negate? GoldenNumber.ONE.neg() : GoldenNumber.ONE;
        if ( Math .abs( value - 0.5f ) < EPSILON )
            return negate? GoldenNumber.HALF.neg() : GoldenNumber.HALF;
            
        int sign = Float .compare( value, 1f );
        for ( int i = 1; i < 13; i++ )
        {
            GoldenNumber candidate = GoldenNumber .tau( sign * i );
            if ( Math .abs( value - candidate .value() ) < EPSILON )
                return negate? candidate .neg() : candidate;
            if ( sign > 0 && candidate.value() > value )
                break;
            if ( sign < 0 && candidate.value() < value )
                break;
        }
        sign = Float .compare( value, 0.5f );
        for ( int i = 1; i < 13; i++ )
        {
            IntegralNumber candidate = GoldenNumber .tau( sign * i ) .div( 2 );
            if ( Math .abs( value - candidate .value() ) < EPSILON )
                return negate? candidate .neg() : candidate;
            if ( sign > 0 && candidate.value() > value )
                break;
            if ( sign < 0 && candidate.value() < value )
                break;
        }
        sign = Float .compare( value, 0.25f );
        for ( int i = 1; i < 13; i++ )
        {
            IntegralNumber candidate = GoldenNumber .tau( sign * i ) .div( 4 );
            if ( Math .abs( value - candidate .value() ) < EPSILON )
                return negate? candidate .neg() : candidate;
            if ( sign > 0 && candidate.value() > value )
                break;
            if ( sign < 0 && candidate.value() < value )
                break;
        }
        
        return null;
    }
}