package com.vzome.jsweet;

import static jsweet.util.Lang.any;

import java.util.ArrayList;
import java.util.Stack;
import java.util.StringTokenizer;

import com.vzome.core.algebra.AlgebraicField;
import com.vzome.core.algebra.AlgebraicMatrix;
import com.vzome.core.algebra.AlgebraicNumber;
import com.vzome.core.algebra.AlgebraicVector;
import com.vzome.core.construction.ConstructionChanges;
import com.vzome.core.construction.Point;
import com.vzome.core.math.RealVector;
import com.vzome.core.math.symmetry.Symmetry;

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

    @Override
    public int getNumMultipliers()
    {
        return getNumIrrationals();
    }

    int parseInt( String s )
    {
        Function f = (Function) this.delegate .$get( "parseInt" );
        return (int) f.$apply( any( s ) );
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
    
    String toString( int[] factors, int format )
    {
        Function f = (Function) this.delegate .$get( "toString" );
        return (String) f.$apply( any( factors ), any( format ) );
    }

    public String getMathML( int[] v1 )
    {
        Function f = (Function) this.delegate .$get( "toString" );
        return (String) f.$apply( any( v1 ), any( 4 ) );
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

    @Override
    public AlgebraicVector createVectorFromTDs( int[][] nums )
    {
        int dims = nums.length;
        AlgebraicNumber[] coords = new AlgebraicNumber[ dims ];
        for(int c = 0; c < coords.length; c++) {
          coords[c] = this.createAlgebraicNumberFromTD( nums[c] );
        }
        return new AlgebraicVector( coords );
    }

    @Override
    public AlgebraicVector origin( int dims )
    {
        AlgebraicNumber zero = this .zero();
        switch ( dims ) {

        case 1:
            return new AlgebraicVector( zero );

        case 2:
            return new AlgebraicVector( zero, zero );

        case 3:
            return new AlgebraicVector( zero, zero, zero );

        case 4:
            return new AlgebraicVector( zero, zero, zero, zero );

        case 5:
            return new AlgebraicVector( zero, zero, zero, zero, zero );

        default:
            // TODO fix this
            return null;
        }
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
        factors[ n ] = factors[ factors.length - 1 ]; // copies the 1n denominator
        return new JsAlgebraicNumber( this, factors );
    }

    @Override
    public AlgebraicNumber createRational( long wholeNumber )
    {
        return this .createRational( wholeNumber, 1 );
    }

    @Override
    public AlgebraicNumber createAlgebraicNumberFromTD( int[] trailingDivisorForm )
    {
        Function f = (Function) this.delegate .$get( "createNumber" );
        int[] simplified = f.$apply( any( trailingDivisorForm ) );
        return new JsAlgebraicNumber( this, simplified );
    }

    public AlgebraicNumber createAlgebraicNumberFromPairs( int[] pairs )
    {
        Function f = (Function) this.delegate .$get( "createNumberFromPairs" );
        int[] simplified = f.$apply( any( pairs ) );
        return new JsAlgebraicNumber( this, simplified );
    }

    @Override
    public AlgebraicNumber createRational( long numerator, long denominator )
    {
        Function f = (Function) this.delegate .$get( "createNumberFromPairs" );
        int[] simplified = f.$apply( any( new long[] { numerator, denominator } ) );
        return new JsAlgebraicNumber( this, simplified );
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
        int[] factors = this .zero() .toTrailingDivisor(); // makes a copy
        System .arraycopy( numerators, 0, factors, 0, numerators.length );
        factors[ numerators.length ] = denominator;
        return this .createAlgebraicNumberFromTD( factors );
    }

    @Override
    public AlgebraicNumber getGoldenRatio()
    {
        int[] value = this.delegate .$get( "goldenRatio" );
        if ( value == null )
            return null;
        return new JsAlgebraicNumber( this, value );
    }

    @Override
    public AlgebraicMatrix identityMatrix( int dims )
    {
        AlgebraicVector[] columns = new AlgebraicVector[ dims ];
        for ( int i = 0; i < columns.length; i++ ) {
            columns[ i ] = this .basisVector( dims, i );
        }
        return new AlgebraicMatrix( columns );
    }

    /**
     * Modeled after AbstractAlgebraicField, with a switch from BigRationals to int[]s.
     */
    @Override
    public AlgebraicNumber parseVefNumber( String string, boolean isRational )
    {
        int[] pairs = new int[ this .getOrder() + 1 ];
        // if the field is declared as rational, then we won't allow the irrational syntax using parenthesis
        // if the field is NOT declared as rational, then we will still allow the rational format as shorthand with no parenthesis
        // or we will allow any order N string representation where N <= field.getOrder().
        if( (!isRational) &&  string.startsWith("(") && string.endsWith(")") ) {
            // strip "(" and ")", tokenize on ","
            StringTokenizer tokens = new StringTokenizer( string.substring( 1, string.length() - 1 ), "," );
            // The tokens get pushed into the factors array in reverse order 
            // from the string representation so the last token becomes the 0th factor.
            // For example, with an order 2 field, the factors for "(3,-2)" are parsed to a 2 element array as {-2, 3}
            // With an order 6 field such as the snubDodec, the factors for "(0,0,0,0,3,-2)" are parsed to a 6 element array: {-2, 3, 0, 0, 0, 0}
            // When a field needs more factors than are supplied, the factors that are provided must still be parsed into the begining of the array:
            // With an order 6 field, if only 2 factors are provided, "(3,-2)" must still be parsed into a 6 element array as {-2, 3, 0, 0, 0, 0}
            // Since VEF version 7 no longer requires that all factors be provided, we need to push the factors onto a stack
            // and pop them off to reverse the order as they are inserted into the begining of the factors array.
            Stack<Integer> numStack = new Stack<>();
            Stack<Integer> denomStack = new Stack<>();
            while( tokens .hasMoreTokens() ) {
                if( numStack.size() >= this .getOrder() ) {
                    throw new RuntimeException( "VEF format error: \"" + string + "\" has too many factors for " + this.getName() + " field" );
                }
                String[] parts = tokens .nextToken() .split( "/" );
                numStack   .push( this .parseInt( parts[ 0 ] ) );
                denomStack .push( (parts.length > 1)? this .parseInt( parts[ 1 ] ) : 1 );
            }
            int i = 0;
            while( ! numStack.empty() ) {
                pairs[ i++ ] = numStack   .pop();
                pairs[ i++ ] = denomStack .pop();
            }
        }
        else {
            // format >= 7 supports the rational numeric format which expects no irrational factors,
            // so there are no parentheses or commas, but still allows the optional "/" if a denominator is specified.
            String[] parts = string .split( "/" );
            pairs[ 0 ] = this .parseInt( parts[ 0 ] );
            pairs[ 1 ] = (parts.length > 1)? this .parseInt( parts[ 1 ] ) : 1;
        }
        return this .createAlgebraicNumberFromPairs( pairs );
    }

    /**
     * Drop one coordinate from the 4D vector. If wFirst (the usual), then drop
     * the first coordinate, taking the "imaginary part" of the vector. If
     * !wFirst (for old VEF import, etc.), drop the last coordinate.
     *
     * @param source
     * @param wFirst
     * @return
     */
    public final AlgebraicVector projectTo3d( AlgebraicVector source, boolean wFirst )
    {
        if ( source .dimension() == 3 )
            return source;
        else {
            AlgebraicVector result = this .origin( 3 );
            for ( int i = 0; i < 3; i++ )
                result .setComponent( i, source .getComponent( wFirst? i+1 : i ) );
            return result;
        }
    }

    @Override
    public AlgebraicNumber createAlgebraicNumber( int ones, int irrat, int denominator, int scalePower )
    {
        AlgebraicNumber result = this .createAlgebraicNumberFromTD( new int[]{ ones, irrat, denominator } );
        if ( scalePower != 0 ) {
            AlgebraicNumber multiplier = this .createPower( scalePower );
            return result .times( multiplier );
        }
        else
            return result;
    }

    @Override
    public AlgebraicVector parseVector(String nums)
    {
        String noLF = nums .replace( '\n', ' ' );
        String noCRLF = noLF .replace( '\r', ' ' );
        StringTokenizer tokens = new StringTokenizer( noCRLF, " " );
        int numToks = tokens .countTokens();
        if ( numToks % getOrder() != 0 )
            throw new IllegalStateException( "Field order (" + getOrder() + ") does not divide token count: " + numToks + ", for '" + noCRLF + "'" );

        int dims = numToks / getOrder();
        AlgebraicNumber[] coords = new AlgebraicNumber[ dims ];
        for ( int i = 0; i < dims; i++ ) {
            coords[ i ] = this .parseNumber( tokens );
        }
        return new AlgebraicVector( coords );
    }

    @Override
    public AlgebraicNumber parseNumber( String nums )
    {
        StringTokenizer tokens = new StringTokenizer( nums, " " );
        return this .parseNumber( tokens );
    }

    private AlgebraicNumber parseNumber( StringTokenizer tokens )
    {
        int order = this .getOrder();
        int[] pairs = new int[ order * 2 ];
        for ( int i = 0; i < order; i++ ) {
            String digit = tokens.nextToken();
            String[] parts = digit.split( "/" );
            pairs[ i * 2 ] = this .parseInt( parts[ 0 ] );
            if ( parts.length > 1 )
                pairs[ i * 2 + 1 ] = this .parseInt( parts[ 1 ] );
            else
                pairs[ i * 2 + 1 ] = 1;
        }
        return this .createAlgebraicNumberFromPairs( pairs );
    }

    @Override
    public AlgebraicNumber parseLegacyNumber( String string )
    {
        int div = 1;
        if ( string .startsWith( "(" ) ) {
            int closeParen = string .indexOf( ')' );
            div = this .parseInt( string .substring( closeParen+2 ) );
            string = string .substring( 1, closeParen );
        }

        int phis = 0;
        int bump = 3;
        int phiIndex = string .indexOf( "phi" );
        if ( phiIndex < 0 ) {
          phiIndex = string .indexOf( "sqrt2" );
          bump = 5;
        }
        if ( phiIndex >= 0 ) {
            String part = string .substring( 0, phiIndex );
            if ( part .length() == 0 )
                phis = 1;
            else if ( part .equals( "-" ) )
                phis = -1;
            else
                phis = this .parseInt( part );
            string = string .substring( phiIndex+bump);
        }

        int ones;
        if ( string .length() == 0 )
            ones = 0;
        else {
            if ( string .startsWith( "+" ) )
                string = string .substring( 1 );
            ones = this .parseInt( string );
        }
        return createAlgebraicNumber( ones, phis, div, 0 );
    }

    @Override
    public String getIrrational(int i, int format)
    {
        Function f = (Function) this.delegate .$get( "getIrrational" );
        return f.$apply( any( i ) );
    }

    @Override
    public String getIrrational(int which)
    {
        return this.getIrrational( which, 0 );
    }

    private Object zomicModule;
    private Object vzomePkg;

    public void setInterpreterModule( Object module, Object vzomePkg )
    {
      this.zomicModule = module;
      this.vzomePkg = vzomePkg;
    }

    public void interpretScript( String script, String language, Point offset, Symmetry symmetry, ConstructionChanges effects ) throws Exception
    {
        if ( this.zomicModule == null )
          throw new Exception( "The Zomic module was not loaded." );

        Function f = (Function) this.zomicModule .$get( "interpretScript" );
        f.$apply( any( script ), any( language ), any( offset ), any( symmetry ), any( effects ), any( vzomePkg ) );
    }
    
    
    
    
    
    
    
    public AlgebraicNumber getNumberByName( String name )
    {
        throw new RuntimeException( "unimplemented JsAlgebraicField.getNumberByName" );
    }

    int[] scaleBy( int[] factors, int whichIrrational )
    {
        throw new RuntimeException( "unimplemented JsAlgebraicField.scaleBy" );
    }

    @Override
    public AlgebraicVector nearestAlgebraicVector(RealVector target)
    {
        throw new RuntimeException( "unimplemented JsAlgebraicField.nearestAlgebraicVector" );
    }

    @Override
    public AlgebraicVector createIntegerVector(int[][] nums)
    {
        throw new RuntimeException( "unimplemented JsAlgebraicField.createIntegerVector" );
    }

    @Override
    public AlgebraicMatrix createMatrix(int[][][] data)
    {
        throw new RuntimeException( "unimplemented JsAlgebraicField.createMatrix" );
    }
}
