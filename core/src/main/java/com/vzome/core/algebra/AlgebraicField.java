//(c) Copyright 2005, Scott Vorthmann.  All rights reserved.

package com.vzome.core.algebra;

import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

import com.vzome.core.math.RealVector;

public abstract class AlgebraicField
{
    public interface Registry
    {
        AlgebraicField getField( String name );
    }

    abstract BigRational[] multiply( BigRational[] v1, BigRational[] v2 );

    abstract double evaluateNumber( BigRational[] factors );

    abstract BigRational[] scaleBy( BigRational[] factors, int whichIrrational );
    
    /**
     * The integers should be the same indices used by getUnitTerm().
     * Subclasses must override to usefully participate in the generation
     * of AlgebraicSeries.
     * @param input
     * @return
     */
    List<Integer> recurrence( List<Integer> input )
    {
        return input;
    }

    void normalize( BigRational[] factors ) {}

    public abstract void defineMultiplier( StringBuffer instances, int w );

    public final int getOrder() { return order; }

    public final int getNumIrrationals()
    {
        return order - 1;
    }

    public abstract String getIrrational( int i, int format );

    public String getIrrational( int which )
    {
        return this .getIrrational( which, DEFAULT_FORMAT );
    }
    
    public abstract double[] getCoefficients();

    protected final String name;

    private final int order;
    
    private Integer hashCode; // initialized on first use

    protected final AlgebraicNumber one;

    protected final AlgebraicNumber zero;

    /**
     * Positive powers of the irrationals.
     */
    private final ArrayList<AlgebraicNumber>[] positivePowers;

    /**
     * Negative powers of the irrationals.
     */
    private final ArrayList<AlgebraicNumber>[] negativePowers;
    
    private static final double SMALL_SERIES_THRESHOLD = 30d;

    private AlgebraicSeries smallSeries;

    // Eclipse says that rawtypes is unnecessary here, but without it,
    // Netbeans and the gradle command line both generate the rawtypes warning 
    @SuppressWarnings({"unchecked", "rawtypes"})  
	public AlgebraicField( String name, int order )
    {
        this.name = name;
        this.order = order;
        // Since derived class constructors are not fully initialized,
        // it's possible that they may not be able to perform some operations including multiply.
        // Specifically, reciprocal() depends on scaleBy()
        // which, in the case of ParameterizedField classes,
        // depends on the constructor being fully executed beforehand.
        // Also, normalize() may not work for some AlgebraicNumbers with non-zero irrational factors
        // although createRational() can safely be called at this point.
        zero = this .createRational( BigRational.ZERO );
        one = this .createRational( BigRational.ONE );
        positivePowers = new ArrayList[ order-1 ];
        negativePowers = new ArrayList[ order-1 ];
    }

    // generateSeries() calls createPower() which can't be used in the ParameterizedField
    // until the c'tor is fully executed. That means smallSeries can't be initialized
    // in this base class c'tor, so this method generates it one time upon first use 
    // rather than in the c'tor which also means smallSeries can't be final.
    private void initSmallSeries() {
        if(smallSeries == null) {
            this .smallSeries = this .generateSeries( SMALL_SERIES_THRESHOLD );
        }
    }

    public AlgebraicNumber nearestAlgebraicNumber( double target )
    {
        initSmallSeries(); // initialze smallSeries on first use;
        return this .smallSeries .nearestAlgebraicNumber( target );
    }
    
    public AlgebraicVector nearestAlgebraicVector( RealVector target )
    {
        initSmallSeries(); // initialze smallSeries on first use;
        return new AlgebraicVector(
                this .smallSeries .nearestAlgebraicNumber( target.x ),
                this .smallSeries .nearestAlgebraicNumber( target.y ),
                this .smallSeries .nearestAlgebraicNumber( target.z ) );
    }

    public String getName()
    {
        return this.name;
    }

    @Override
    public String toString() {
        return getName();
    }

    @Override
    public int hashCode() {
        if(hashCode == null) {
            final int prime = 43;
            hashCode = 7;
            double[] coefficients = getCoefficients();
            for(int i = 0; i < coefficients.length; i++) {
                Double coefficient = coefficients[i];
                hashCode = prime * hashCode + coefficient.hashCode();
            }
        }
        return hashCode;
    }

    /**
     * With the use of parameterized fields, it's possible for two fields 
     * of different classes to be equal 
     * or for two fields of the same class to not be equal.
     * For example RootTwoField equals SqrtField(2)
     * but SqrtField(2) does not equal SqrtField(3).
     * Similarly, PolygonField(4) does equal SqrtField(2)
     * but PolygonField(6) does not equal SqrtField(3) though they share 
     * the same irrational coefficient, but they are of different order.
     * 
     * @param obj
     * @return 
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if(!AlgebraicField.class.isAssignableFrom(obj.getClass())) {
            return false;
        }
        AlgebraicField that = (AlgebraicField) obj;
        if(this.getName().equals(that.getName())) {
            // assume that all parameterized fields include the parameter in their name
            return true;
        }
        if(getOrder() != that.getOrder()) {
            return false;
        }
        double[] thisCoefficients = this.getCoefficients();
        double[] thatCoefficients = that.getCoefficients();
        for(int i = 0; i < thisCoefficients.length; i++) {
            // subtract and compare the difference to zero 
            // instead of comparing floating point numbers directly
            if(thisCoefficients[i] - thatCoefficients[i] != 0d) {
                return false;
            }
        }
        return true;
    }

    /**
     * The AlgebraicNumber constructor already validates the number of terms.
     * It pads the array with ZEROs if it's too short,
     * throws an exception if it's too long,
     * and replaces all nulls with ZERO.
     * No need to duplicate any of that here.
     * This method is called by the AlgebraicNumber constructor. 
     * It is primarily intended to allow subclasses to intercept
     * a pair of terms from the golden field and remap them as needed for that field.
     * Otherwise, the terms are returned unchanged.
     * @param terms
     * @return
     */
    protected BigRational[] prepareAlgebraicNumberTerms(BigRational[] terms) {
        return terms;
    }
    
    /**
     * Generates an AlgebraicNumber with the specified {@code BigRational} factors.
     * @param factors
     * @return
     */
    public final AlgebraicNumber createAlgebraicNumber( BigRational[] factors )
    {
        return new AlgebraicNumber( this, factors );
    }

    /**
     * Generates an AlgebraicNumber with integer terms (having only unit denominators).
     * Use {@code createAlgebraicNumber( int[] numerators, int denominator )} 
     * or {@code createAlgebraicNumber( BigRational[] factors )} 
     * if denominators other than one are required.
     * @param terms
     * @return
     */
    public final AlgebraicNumber createAlgebraicNumber( int[] terms )
    {
        return createAlgebraicNumber( terms, 1 );
    }

    /**
     * Generates an AlgebraicNumber with the specified numerators,
     * all having a common denominator as specified.
     * @param numerators
     * @param denominator is a common denominator for all numerators
     * @return
     */
    public final AlgebraicNumber createAlgebraicNumber( int[] numerators, int denominator )
    {
        BigRational[] brs = new BigRational[ numerators .length ];
        for ( int j = 0; j < numerators.length; j++ ) {
            brs[ j ] = new BigRational( numerators[ j ], denominator );
        }
        return createAlgebraicNumber( brs );
    }

    public final AlgebraicNumber createAlgebraicNumber( int ones, int irrat, int denominator, int scalePower )
    {
        BigRational[] factors = new BigRational[ this .getOrder() ];
        factors[ 0 ] = new BigRational( ones, denominator );
        factors[ 1 ] = new BigRational( irrat, denominator );
        for ( int i = 2; i < factors.length; i++ ) {
            factors[ i ] = new BigRational( 0 );
        }
        if ( scalePower != 0 ) {
            AlgebraicNumber multiplier = this .createPower( scalePower );
            return createAlgebraicNumber(factors ) .times( multiplier );
        }
        else
            return createAlgebraicNumber( factors );
    }

    /**
     * The golden ratio (and thus icosahedral symmetry and related tools) 
     * can be generated by some fields even though it's not one of their irrational coefficients. 
     * For example, SqrtField(5) and PolygonField(10) can both generate the golden ratio 
     * so they can support icosa symmetry and related tools.
     * In some such cases, the resulting AlgebraicNumber 
     * may have multiple terms and/or factors other than one. 
     * 
     * @return An AlgebraicNumber which evaluates to the golden ratio, or null if not possible in this field.
     */
    public AlgebraicNumber getGoldenRatio()
    {
        return null;
    }

    public final AlgebraicNumber createPower( int power )
    {
        return this .createPower( power, 1 );
    }

    public final AlgebraicNumber createPower( int power, int irr )
    {
        if ( power == 0 || irr == 0 )
            return this .one;
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
     * @param wholeNumber becomes the numerator with 1 as the denominator
     * @return AlgebraicNumber
     */
    public final AlgebraicNumber createRational( long wholeNumber )
    {
        return createRational( wholeNumber, 1 );
    }

    /**
     * @param numerator
     * @param denominator
     * @return AlgebraicNumber
     */
    public final AlgebraicNumber createRational( long numerator, long denominator )
    {
        return createRational( new BigRational( numerator, denominator) );
    }
    
    /**
     * @param rationalNumber is the value in the units term 
     * @return AlgebraicNumber
     */
    public final AlgebraicNumber createRational( BigRational rationalNumber )
    {
        return new AlgebraicNumber( this, rationalNumber );
    }
    
    /**
     * @return The AlgebraicNumber to be use for the Chord Ratio construction in the given field.
     * This method can be used to generalize an AffinePolygon tool and a PolygonalAntiprismSymmetry.
     * This base class returns one, which is the scalar for an affine square and works in any field. 
     * Derived classes should override this method if they can be used to generate any other affine polygon.
     */
    public AlgebraicNumber getAffineScalar() {
        return one;
    }

    /**
     * @param n specifies the ordinal of the term in the AlgebraicNumber which will be set to one.
     * When {@code n == 0}, the result is the same as {@code createRational(1)}.
     * When {@code n == 1}, the result is the same as {@code createPower(1)}.
     * When {@code n < 0}, the result will be {@code zero()}.
     * When {@code n >= getOrder()}, an IndexOutOfBoundsException will be thrown.
     * @return an AlgebraicNumber with the factor specified by {@code n} set to one.
     */
    public final AlgebraicNumber getUnitTerm(int n) {
        if(n < 0) {
            return zero();
        }
        // This is safe since AlgebraicNumber is immutable 
        // and getFactors() returns a copy of its factors rather than the actual array
        BigRational[] factors = zero.getFactors();
        factors[n] = BigRational.ONE;
        return createAlgebraicNumber(factors);
    }

    public BigRational[] negate( BigRational[] array )
    {
        BigRational[] result = new BigRational[ array.length ];
        for (int i = 0; i < array.length; i++) {
            result[ i ] = array[ i ] .negate();
        }
        return result;
    }

    public boolean isZero( BigRational[] array )
    {
        for (BigRational element : array) {
            if (!element.isZero()) {
                return false;
            }
        }
        return true;
    }

    public BigRational[] add( BigRational[] v1, BigRational[] v2 )
    {
        if ( v1.length != v2.length )
            throw new IllegalArgumentException( "arguments don't match" );
        BigRational[] result = new BigRational[ v1.length ];
        for (int i = 0; i < result.length; i++) {
            result[ i ] = v1[ i ] .plus( v2[ i ] );
        }
        return result;
    }

    public BigRational[] subtract( BigRational[] v1, BigRational[] v2 )
    {
        if ( v1.length != v2.length )
            throw new IllegalArgumentException( "arguments don't match" );
        BigRational[] result = new BigRational[ v1.length ];
        for (int i = 0; i < result.length; i++) {
            result[ i ] = v1[ i ] .minus( v2[ i ] );
        }
        return result;
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

    public final AlgebraicVector origin( int dims )
    {
        return new AlgebraicVector( this, dims );
    }

    public final AlgebraicVector basisVector( int dims, int axis )
    {
        AlgebraicVector result = origin( dims );
        return result .setComponent( axis, this .one() );
    }

    // ======================================================================================
    // number operations
    // ======================================================================================

    protected BigRational[] reciprocal( BigRational[] fieldElement )
    {
        int length = fieldElement .length;
        BigRational[][] representation = new BigRational[ length ][ length ];
        boolean isZero = true;
        for ( int i = 0; i < length; i++ ) {
        		isZero = isZero && fieldElement[ i ] .isZero();
            representation[ 0 ][ i ] = fieldElement[ i ];
        }
        if ( isZero )
        		throw new RuntimeException( "Denominator is zero" );
        for ( int j = 1; j < length; j++ ) {
            BigRational[] column = this .scaleBy( fieldElement, j );
            System.arraycopy(column, 0, representation[ j ], 0, length);
        }
        BigRational[][] reciprocal = new BigRational[ length ][ length ];
        // create an identity matrix
        for ( int j = 0; j < length; j++ ) {
            for ( int i = 0; i < length; i++ ) {
                reciprocal[ j ][ i ] = ( i == j ) ? BigRational.ONE : BigRational.ZERO;
            }
        }
        int rank = Fields .gaussJordanReduction( representation, reciprocal );
        if(rank != length) {
            // TODO: What should we do here?
            System.err.println((new Throwable()).getStackTrace()[0].getMethodName() 
                    + " expects matrix rank to be " + length + ", but it is " + rank + "."); 
        }
        BigRational[] reciprocalFactors = new BigRational[ length ];
        System.arraycopy(reciprocal[ 0 ], 0, reciprocalFactors, 0, length);
        return reciprocalFactors;
    }

    public final static int DEFAULT_FORMAT = 0; // 4 + 3φ

    public final static int EXPRESSION_FORMAT = 1; // 4 +3*phi

    public final static int ZOMIC_FORMAT = 2; // 4 3

    public final static int VEF_FORMAT = 3; // (3,4)

    public AlgebraicNumber zero()
    {
        return this .zero;
    }

    public AlgebraicNumber one()
    {
        return this .one;
    }

    /**
     *
     * @param nums is an array of integer arrays: One array of coordinate terms per dimension.
     * Initially, this is designed to simplify migration of order 2 golden directions
     * to new fields of higher order having golden subfields as their first two factors.
     {@code
        field.createVector( new int[]  {  0,1,2,3,   4,5,6,7,   8,9,0,1  } );   // older code like this...
        field.createVector( new int[][]{ {0,1,2,3}, {4,5,6,7}, {8,9,0,1} } );   // should be replaced by this...
        field.createVector( new int[][]{ {0,1,2,3}, {4,5,6,7}, {8,9    } } );   // ... or even this.
     }
     * The older code shown in the first example requires an order 2 field.
     * The second example will work with any field of order 2 or greater.
     * This new overload has the advantage that the internal arrays representing the individual dimensions are more clearly delineated and controlled.
     * As shown in the third example, the internal arrays need not be all the same length. Trailing zero terms can be omitted as shown.
     * Inner arrays require an even number of elements since they represent a sequence of numerator/denominator pairs.
     * 
     * createVector is currently limited to int valued vectors, not long, and definitely not BigInteger
     * In most cases, this is adequate, but in the case where it's called by XmlSaveFormat.parseAlgebraicObject(), 
     * it seems possible that a value larger than Integer.MAX_VALUE could be saved to the XML which could not subsequently be parsed.
     * TODO: Consider refactoring createVector to use long[][] instead of int[][] if this becomes an issue. 
     * 
     * @return an AlgebraicVector
     */
    public AlgebraicVector createVector( int[][] nums )
    {
        int dims = nums.length;
        AlgebraicNumber[] coords = new AlgebraicNumber[ dims ];
        for(int c = 0; c < coords.length; c++) {
            int coordLength = nums[c].length;
            if ( coordLength % 2 != 0 ) {
                throw new IllegalStateException( "Vector dimension " + c + " has " + coordLength + " components. An even number is required." );
            }
            int nFactors = coordLength / 2;
            if ( nFactors > getOrder() ) {
                throw new IllegalStateException( "Vector dimension " + c + " has " + (coordLength /2) + " terms." 
                        + " Each dimension of the " + this.getName() + " field is limited to " + getOrder() + " terms."
                        + " Each term consists of a numerator and a denominator." );
            }
            BigRational[] factors = new BigRational[nFactors];
            for (int f = 0; f < nFactors; f++) {
                int numerator   = nums[c][(f*2)  ];
                int denominator = nums[c][(f*2)+1];
                factors[f] = new BigRational(numerator, denominator);
            }
            coords[c] = createAlgebraicNumber(factors);
        }
        return new AlgebraicVector( coords );
    }
    
    /**
     * Generates an AlgebraicVector with all AlgebraicNumber terms being integers (having unit denominators).
     * Contrast this with {@code createVector(int[][] nums)} which requires all denominators to be specified.
     * @param nums is a 2 dimensional integer array. The length of nums becomes the number of dimensions in the resulting AlgebraicVector.
     * For example, {@code (new PentagonField()).createIntegerVector( new int[][]{ {0,-1}, {2,3}, {4,5} } ); } 
     * generates the 3 dimensional vector (-φ, 2 +3φ, 4 +5φ) having all integer terms. 
     * @return an AlgebraicVector
     */
    public AlgebraicVector createIntegerVector( int[][] nums )
    {
        final int dims = nums.length;
        AlgebraicVector result = origin( dims );
        for (int dim = 0; dim < dims; dim++) {
            result .setComponent( dim, createAlgebraicNumber( nums[dim] ) );
        }
        return result;
    }

    /**
     * Create a 3x3 square matrix from integer data.
     * TODO: Generalize this method to create a matrix with dimensions matching the dimensions of the data array
     * Sample input data for an order-4 field:
     *   {{{7,5,0,1,-4,5,0,1},{-2,5,0,1,4,5,0,1},{0,1,-8,5,0,1,6,5}},
     *    {{-2,5,0,1,4,5,0,1},{7,5,0,1,-4,5,0,1},{0,1,8,5,0,1,-6,5}},
     *    {{0,1,-8,5,0,1,6,5},{0,1,8,5,0,1,-6,5},{-9,5,0,1,8,5,0,1}}}
     * @param field
     * @param data integer coordinates, in row-major order, complete with denominators.
     * @return
     */
    public AlgebraicMatrix createMatrix( int[][][] data )
    {
		AlgebraicVector col1 = createVector( new int[][]{ data[0][0], data[1][0], data[2][0] } );
		AlgebraicVector col2 = createVector( new int[][]{ data[0][1], data[1][1], data[2][1] } );
		AlgebraicVector col3 = createVector( new int[][]{ data[0][2], data[1][2], data[2][2] } );
    		return new AlgebraicMatrix( col1, col2, col3 );
    }

    /**
     * 
     * @param buf
     * @param factors
     * @param format must be one of the following values.
     * The result is formatted as follows:
     * <br>
     * {@code DEFAULT_FORMAT    // 4 + 3φ}<br>
	 * {@code EXPRESSION_FORMAT // 4 +3*phi}<br>
	 * {@code ZOMIC_FORMAT      // 4 3}<br>
	 * {@code VEF_FORMAT        // (3,4)}
     */
    void getNumberExpression( StringBuffer buf, BigRational[] factors, int format )
    {
        switch ( format )
        {
        case ZOMIC_FORMAT:
            for ( int i = 0; i < factors.length; i++ )
            {
                if ( i > 0 )
                    buf.append( " " );
                buf .append( factors[ i ] .toString() );
            }
            break;

        case VEF_FORMAT:
            buf.append( "(" );
            for ( int i = factors.length; i > 0; i-- ) { // note that we go backwards!
                buf .append( factors[ i - 1 ] .toString() );
                if ( i > 1 )
                    buf.append( "," );
            }
            buf.append( ")" );
            break;

        default:
            int first = 0;
            for ( int i = 0; i < factors.length; i++ )
            {
                BigRational factor = factors[ i ];
                if ( factor .isZero() ) {
                    ++ first;
                    continue;
                }
                if ( i > first )
                {
                    buf .append( " " );
                }
                if ( factor .isNegative() )
                {
                    factor = factor .negate();
                    buf .append( "-" );
                }
                else if ( i > first )
                {
                    buf .append( "+" );
                }
                if ( i == 0 )
                    buf .append( factor .toString() );
                else
                {
                    if ( ! factor .isOne() )
                    {
                        buf .append( factor .toString() );
                        if ( format == EXPRESSION_FORMAT )
                            buf .append( "*" );
                    }
                    String multiplier = this .getIrrational( i, format );
                    buf .append(  multiplier );
                }
            }
            if ( first == factors.length )
                // all factors were zero
                buf .append( "0" );
            break;
        }
    }

    public AlgebraicNumber parseLegacyNumber( String val )
    {
        throw new IllegalStateException( "This field does not support vZome 2.x files." );
    }

    public AlgebraicNumber parseNumber( String nums )
    {
        StringTokenizer tokens = new StringTokenizer( nums, " " );
        return this .parseNumber( tokens );
    }

    /**
     * Consumes this.getOrder() tokens from the tokenizer
     * @param tokenizer
     * @return
     */
    private AlgebraicNumber parseNumber( StringTokenizer tokenizer )
    {
        BigRational[] rats = new BigRational[ this .getOrder() ];
        for ( int i = 0; i < rats.length; i++ ) {
            rats[ i ] = new BigRational( tokenizer .nextToken() );
        }
        return createAlgebraicNumber( rats );
    }

    public AlgebraicVector parseVector( String nums )
    {
        StringTokenizer tokens = new StringTokenizer( nums, " " );
        int numToks = tokens .countTokens();
        if ( numToks % getOrder() != 0 )
            throw new IllegalStateException( "Field order (" + getOrder() + ") does not divide token count: " + numToks + ", for '" + nums + "'" );

        int dims = numToks / getOrder();
        AlgebraicNumber[] coords = new AlgebraicNumber[ dims ];
        for ( int i = 0; i < dims; i++ ) {
            coords[ i ] = this .parseNumber( tokens );
        }
        return new AlgebraicVector( coords );
    }

    public AlgebraicMatrix identityMatrix( int dims )
    {
        AlgebraicVector[] columns = new AlgebraicVector[ dims ];
        for ( int i = 0; i < columns.length; i++ ) {
            columns[ i ] = this .basisVector( dims, i );
        }
        return new AlgebraicMatrix( columns );
    }

    /**
     * @return the number of independent multipliers in this field.
     * These are the primitive elements of the field.
     * The value should be less than or equal to getNumIrrationals.
     * It will be less whenever the irrationals are dependent.
     * For example, in the field for sqrt(phi), there is only one
     * multiplier, since the other irrational is just the square of that one.
     */
    public int getNumMultipliers()
    {
        return this .getNumIrrationals(); // the right value for any order-2 field, and for some higher order fields, also
    }

    public AlgebraicSeries generateSeries( double threshold )
    {
        AlgebraicNumber multiplier = this .createPower( 1, this .getNumIrrationals() );
        AlgebraicNumber cover = this .one();
        int power = 0;
        while ( cover .evaluate() < threshold ) {
            cover = cover .times( multiplier );
            ++ power;
        }
        return new AlgebraicSeries( this, power );
    }
}
