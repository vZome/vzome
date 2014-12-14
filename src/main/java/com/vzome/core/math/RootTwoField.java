

package com.vzome.core.math;

import java.util.ArrayList;

import org.w3c.dom.Element;

import com.vzome.core.algebra.RationalNumbers;
import com.vzome.core.math.symmetry.Symmetry;

public class RootTwoField implements IntegralNumberField
{
    public static final IntegralNumberField INSTANCE = new RootTwoField();
    
    private final ArrayList mSymmetries = new ArrayList();
    
    private RootTwoField()
    {
        IntegralNumberField .FIELDS .put( "rootTwo", this );
        RootTwoNumber .R2FACTORY = this;
        RootTwoVector .FACTORY = this;
        RootTwoMatrix .FACTORY = this;
    };

    public IntegralNumber createIntegralNumber( int rootTwos, int ones, int divisor, int power )
    {
        if ( rootTwos==0 && ones==0 )
            return RootTwoNumber.ZERO;
        if ( rootTwos==0 && ones==1 && divisor==1 && power==0 )
            return RootTwoNumber.ONE;
        if ( rootTwos==1 && ones==0 && divisor==1 && power==0 )
            return RootTwoNumber.ROOT_TWO;
        if ( rootTwos==0 && ones==1 && divisor==1 && power==1 )
            return RootTwoNumber.ROOT_TWO;
        
        return new RootTwoNumber( rootTwos, ones, divisor, power );
    }

    public IntegralNumber parseString( String string )
    {
        int div = 1;
        boolean hasDiv = string .startsWith( "(" );
        if ( hasDiv )
            string = string .substring( 1 );
        int phiIndex = string .indexOf( "sqrt2" );
        int phis = 0;
        if ( phiIndex >= 0 ) {
            String part = string .substring( 0, phiIndex );
            if ( part .length() == 0 )
                part = "1";
            else if ( part .equals( "-" ) )
                part = "-1";
            phis = Integer .parseInt( part );
            string = string .substring( phiIndex+5 );
        }
        if ( hasDiv ) {
            int closeParen = string .indexOf( ")" );
            String part = string .substring( closeParen+2 );
            string = string .substring( 0, closeParen );
            div = Integer .parseInt( part );
        }
        if ( string .length() == 0 )
            string = "0";
        else if ( string .startsWith( "+" ) )
            string = string .substring( 1 );
        int ones = Integer .parseInt( string );
        return new RootTwoNumber( phis, ones, div, 0 );
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
        return createGoldenVector( x, y, z, w, RootTwoNumber.ONE );
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
        return "rootTwo";
    }

    public GoldenVector createGoldenVector( IntegralNumber x, IntegralNumber y, IntegralNumber z, IntegralNumber w, IntegralNumber factor )
    {
        return new RootTwoVector( x, y, z, w, factor );
    }

    public GoldenVector origin()
    {
        return RootTwoVector .ORIGIN;
    }

    public GoldenVector getAxis( int axis )
    {
        if ( axis == GoldenVector .X_AXIS )
            return RootTwoVector .X;
        if ( axis == GoldenVector .Y_AXIS )
            return RootTwoVector .Y;
        if ( axis == GoldenVector .Z_AXIS )
            return RootTwoVector .Z;
        if ( axis == GoldenVector .W_AXIS )
            return RootTwoVector .W;
        return null;
    }

    public GoldenMatrix createGoldenMatrix( GoldenVector x, GoldenVector y, GoldenVector z, GoldenVector w )
    {
        return new RootTwoMatrix( x, y, z, w );
    }

    public GoldenMatrix identity()
    {
        return RootTwoMatrix.IDENTITY_MATRIX;
    }

    public IntegralNumber zero()
    {
        return RootTwoNumber.ZERO;
    }

    public IntegralNumber one()
    {
        return RootTwoNumber.ONE;
    }
    
    public void addSymmetry( Symmetry symm )
    {
        mSymmetries .add( symm );
    }

    public Symmetry[] getSymmetries()
    {
        return (Symmetry[]) mSymmetries .toArray( new Symmetry[]{} );
    }
}