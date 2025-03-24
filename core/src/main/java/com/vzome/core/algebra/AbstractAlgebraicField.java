
package com.vzome.core.algebra;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;
import java.util.StringTokenizer;

import com.vzome.core.construction.ConstructionChanges;
import com.vzome.core.construction.Point;
import com.vzome.core.math.RealVector;
import com.vzome.core.math.symmetry.Symmetry;

public abstract class AbstractAlgebraicField implements AlgebraicField
{
    abstract BigRational[] multiply( BigRational[] v1, BigRational[] v2 );

    abstract double evaluateNumber( BigRational[] factors );

    abstract BigRational[] scaleBy( BigRational[] factors, int whichIrrational );
    
    public abstract double[] getCoefficients();

    /**
     * The integers should be the same indices used by getUnitTerm().
     * Subclasses must override to usefully participate in the generation
     * of AlgebraicSeries.
     * @param input
     * @return
     */
    public List<Integer> recurrence( List<Integer> input )
    {
        return input;
    }

    void normalize( BigRational[] factors ) {}

    @Override
    public final int getOrder() { return order; }

    @Override
    public final int getNumIrrationals()
    {
        return order - 1;
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
    public AlgebraicNumber getNumberByName(String name)
    {
        switch(name) {
        case "zero":
            return zero();
            
        case "one":
            return one();
            
        case "phi": case "\u03C6":
            return getGoldenRatio();
            
        case "\u221A5": case "root5": case "sqrt5":
        {
            // this allows sqrt5 to behave consistently in any field that supports phi
            AlgebraicNumber n = getGoldenRatio();
            return n == null ? null : n.plus(n).minus(one());
        }
        case "\u221A8": case "root8": case "sqrt8":
        {
            // this allows sqrt8 to behave consistently in any field that supports sqrt2
            AlgebraicNumber n = getNumberByName("sqrt2");
            return n == null ? null : n.times(createRational(2));
        }
        default:
            // try to match irrational names
            for(int format = DEFAULT_FORMAT; format <= EXPRESSION_FORMAT; format++) {
                // start from 1 to skip the rational term
                for(int i=1; i < getOrder(); i++) {
                    if(getIrrational(i, format).equals(name)) {
                        return getUnitTerm(i);
                    }
                }
            }
            // DJH: I changed my mind. This method probably shouldn't be parsing anything
            // and for the initial use case, this is all unnecesary overhead that never returns any result.
            // I'll just comment it all out for now until we get a use-case where it makes sense.
//            try {
//                // try to parse as a rational number
//                String[] parts = name.split( "/" );
//                long numerator = Long.parseLong( parts[0] );
//                switch(parts.length) {
//                case 1:
//                    return createRational(numerator);
//                case 2:
//                    long denominator = Long.parseLong( parts[1] );
//                    return createRational(numerator, denominator);
//                }
//            }
//            catch(NumberFormatException ex) {
//                // This is OK. Just means name isn't a rational number
//            }
        }
        return null;
    }

    @Override
    public String getIrrational( int i )
    {
        return this .getIrrational( i, DEFAULT_FORMAT );
    }

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

    protected final AlgebraicNumberFactory numberFactory;

    // Eclipse says that rawtypes is unnecessary here, but without it,
    // Netbeans and the gradle command line both generate the rawtypes warning 
    @SuppressWarnings({"unchecked", "rawtypes"})  
	public AbstractAlgebraicField( String name, int order, AlgebraicNumberFactory factory )
    {
        this.name = name;
        this.order = order;
        this.numberFactory = factory;
        zero = this .numberFactory .createRational( this, 0, 1 );
        one = this .numberFactory .createRational( this, 1, 1 );
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
    
    @Override
    public AlgebraicVector nearestAlgebraicVector( RealVector target )
    {
        initSmallSeries(); // initialze smallSeries on first use;
        return new AlgebraicVector(
                this .smallSeries .nearestAlgebraicNumber( target.x ),
                this .smallSeries .nearestAlgebraicNumber( target.y ),
                this .smallSeries .nearestAlgebraicNumber( target.z ) );
    }

    @Override
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
     * Similarly, PolygonField(4) equals SqrtField(2)
     * and PolygonField(6) equals SqrtField(3).
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
        if( ! ( obj instanceof AbstractAlgebraicField ) ) {
            return false;
        }
        AbstractAlgebraicField that = (AbstractAlgebraicField) obj;
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
     * This method is intended to allow subclasses to intercept a 4 element int array 
     * representing the numerators and denominators of a pair of terms (units and phis) 
     * from the golden field and remap them as needed for that field.
     * Otherwise, the terms are returned unchanged.
     * @param terms
     * @return
     */
    protected long[] convertGoldenNumberPairs( long[] pairs )
    {
        if ( pairs.length == 2 * this.order )
            return pairs;
        else {
            // Assume that phi is the first irrational, but we still need to expand to the right length
            long[] newPairs = new long[ 2 * this.order ];
            for (int i = 0; i < this.order; i++) {
                newPairs[ 2*i + 0 ] = ( i >= 2 )? 0 : pairs[ 2*i + 0 ];
                newPairs[ 2*i + 1 ] = ( i >= 2 )? 1 : pairs[ 2*i + 1 ];
            }
            return newPairs;
        }
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
    public final AlgebraicNumber createAlgebraicNumber( int[] terms )
    {
        return this.numberFactory .createAlgebraicNumber( this, terms, 1 );
    }

    /**
     * Generates an AlgebraicNumber from a "trailing divisor" int array representation.
     * @param trailingDivisorForm numerators trailed by a common denominator for all numerators
     * @return
     */
    @Override
    public final AlgebraicNumber createAlgebraicNumberFromTD( int[] trailingDivisorForm )
    {
        int terms = trailingDivisorForm.length - 1;
        if ( terms == 2 && this.getOrder() > 2 ) {
            
            // Momentarily switch to rational pairs (not reduced), in order to call convertGoldenNumberPairs
            //  [ a1, a2, d ] => [ a1, d, a2, d, ... aN, d ]
            long[] pairs = new long[ 2*terms ];
            int divisor = trailingDivisorForm[ terms ];
            for (int i = 0; i < terms; i++) {
                pairs[ 2*i + 0 ] = trailingDivisorForm[ i ];
                pairs[ 2*i + 1 ] = divisor;
            }
            
            pairs = this .convertGoldenNumberPairs( pairs );
            
            // Now switch back.  Since only zero-valued terms were introduced, we don't need to reduce the fractions.
            terms = pairs.length/2;
            trailingDivisorForm = new int[ terms + 1 ];
            trailingDivisorForm[ terms ] = (int) pairs[ 1 ];
            for ( int i = 0; i < pairs.length/2; i++ ) {
                trailingDivisorForm[ i ] = (int) pairs[ 2*i ];
            }
        }
        return this.numberFactory .createAlgebraicNumberFromTD( this, trailingDivisorForm );
    }

    /**
     * Generates an AlgebraicNumber with the specified numerators,
     * all having a common denominator as specified.
     * @param numerators
     * @param denominator is a common denominator for all numerators
     * @return
     */
    @Override
    public final AlgebraicNumber createAlgebraicNumber( int[] numerators, int denominator )
    {
        return this.numberFactory .createAlgebraicNumber( this, numerators, denominator );
    }

    @Override
    public final AlgebraicNumber createAlgebraicNumber( int ones, int irrat, int denominator, int scalePower )
    {
        int[] factors = new int[ this .order + 1 ];
        factors[ 0 ] = ones;
        factors[ 1 ] = irrat;
        for ( int i = 2; i < this .order; i++ ) {
            factors[ i ] = 0;
        }
        factors[ this .order ] = denominator;
        AlgebraicNumber result = this.numberFactory .createAlgebraicNumberFromTD( this, factors );
        if ( scalePower != 0 ) {
            AlgebraicNumber multiplier = this .createPower( scalePower );
            return result .times( multiplier );
        }
        else
            return result;
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
    @Override
    public AlgebraicNumber getGoldenRatio()
    {
        return null;
    }

    @Override
    public final AlgebraicNumber createPower( int power )
    {
        return this .createPower( power, 1 );
    }

    @Override
    public final AlgebraicNumber createPower( int power, int irr )
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
     * @param wholeNumber becomes the numerator with 1 as the denominator
     * @return AlgebraicNumber
     */
    @Override
    public final AlgebraicNumber createRational( long wholeNumber )
    {
        return this .numberFactory .createRational( this, wholeNumber, 1 );
    }

    /**
     * @param numerator
     * @param denominator
     * @return AlgebraicNumber
     */
    @Override
    public final AlgebraicNumber createRational( long numerator, long denominator )
    {
        return this .numberFactory .createRational( this, numerator, denominator );
    }
        
    /**
     * @param n specifies the ordinal of the term in the AlgebraicNumber which will be set to one.
     * When {@code n == 0}, the result is the same as {@code createRational(1)}.
     * When {@code n == 1}, the result is the same as {@code createPower(1)}.
     * When {@code n < 0}, the result will be {@code zero()}.
     * When {@code n >= getOrder()}, an IndexOutOfBoundsException will be thrown.
     * @return an AlgebraicNumber with the factor specified by {@code n} set to one.
     */
    @Override
    public final AlgebraicNumber getUnitTerm( int n )
    {
        if ( n < 0 ) {
            return zero();
        }
        int[] factors = this .zero() .toTrailingDivisor(); // makes a copy
        factors[ n ] = factors[ factors.length - 1 ]; // copies the 1n denominator
        return this .numberFactory .createAlgebraicNumberFromTD( this, factors );
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
    @Override
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
    public final AlgebraicVector origin( int dims )
    {
        return new AlgebraicVector( this, dims );
    }

    @Override
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
                reciprocal[ j ][ i ] = ( i == j ) ? this.numberFactory.one() : this.numberFactory.zero();
            }
        }
        int rank = Fields .gaussJordanReduction( representation, reciprocal );
        BigRational[] reciprocalFactors = new BigRational[ length ];
        System.arraycopy(reciprocal[ 0 ], 0, reciprocalFactors, 0, length);
        return (rank == length) 
                ? reciprocalFactors 
                : onReciprocalRankDeficient(rank, reciprocal, reciprocalFactors);
    }
    
    /**
     * Subclasses can overloading this method to handle special cases. (e.g. SqrtField of a perfect square)
     * @param rank
     * @param reciprocal
     * @param reciprocalFactors
     * @throws IllegalStateException
     */
    protected BigRational[] onReciprocalRankDeficient(int rank, BigRational[][] reciprocal, BigRational[] reciprocalFactors) {
        String msg = this.getName() + " expects reciprocal matrix to be full rank (" + reciprocal.length + "), but it is " + rank + ".";
        System.err.println(msg);
        throw new IllegalStateException(msg);
        // A subclass could return reciprocalFactors or perform some normalization or reduction instead of throwing an exception
    }

    public final static int DEFAULT_FORMAT = 0; // 4 + 3φ

    public final static int EXPRESSION_FORMAT = 1; // 4 +3*phi

    public final static int ZOMIC_FORMAT = 2; // 4 3

    public final static int VEF_FORMAT = 3; // (3,4)

    @Override
    public AlgebraicNumber zero()
    {
        return this .zero;
    }

    @Override
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
        field.createVector( new int[][]{ {0,1,2,3}, {4,5,6,7}, {8,9,0,1} } );   // should be replaced by this.
     }
     * The older code shown in the first example requires an order 2 field.
     * The second example will work with any field of order 2 or greater.
     * This new overload has the advantage that the internal arrays representing the individual dimensions are more clearly delineated and controlled.
     * Inner arrays require an even number of elements since they represent a sequence of numerator/denominator pairs.
     * 
     * createVector is currently limited to int valued vectors, not long, and definitely not BigInteger
     * In most cases, this is adequate, but in the case where it's called by XmlSaveFormat.parseAlgebraicObject(), 
     * it seems possible that a value larger than Integer.MAX_VALUE could be saved to the XML which could not subsequently be parsed.
     * TODO: Consider refactoring createVector to use long[][] instead of int[][] if this becomes an issue. 
     * 
     * @return an AlgebraicVector
     */
    @Override
    public AlgebraicVector createVector( int[][] nums )
    {
        int dims = nums.length;
        AlgebraicNumber[] coords = new AlgebraicNumber[ dims ];
        for(int c = 0; c < coords.length; c++) {
            int coordLength = nums[c].length;
            if ( coordLength % 2 != 0 ) {
                throw new IllegalStateException( "Vector dimension " + c + " has " + coordLength + " components. An even number is required." );
            }
            int nTerms = coordLength / 2;
            if ( nTerms > getOrder() ) {
                throw new IllegalStateException( "Vector dimension " + c + " has " + (coordLength /2) + " terms." 
                        + " Each dimension of the " + this.getName() + " field is limited to " + getOrder() + " terms."
                        + " Each term consists of a numerator and a denominator." );
            }
            long[] pairs = new long[ nums[ c ] .length ];
            for (int i = 0; i < pairs.length; i++) {
                pairs[ i ] = nums[ c ][ i ];
            }
            if ( pairs.length == 4 && getOrder() > 2 ) {
                pairs = this .convertGoldenNumberPairs( pairs );
            }
            coords[c] = this.numberFactory .createAlgebraicNumberFromPairs( this, pairs );
        }
        return new AlgebraicVector( coords );
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

    /**
     * Generates an AlgebraicVector with all AlgebraicNumber terms being integers (having unit denominators).
     * Contrast this with {@code createVector(int[][] nums)} which requires all denominators to be specified.
     * @param nums is a 2 dimensional integer array. The length of nums becomes the number of dimensions in the resulting AlgebraicVector.
     * For example, {@code (new PentagonField()).createIntegerVector( new int[][]{ {0,-1}, {2,3}, {4,5} } ); } 
     * generates the 3 dimensional vector (-φ, 2 +3φ, 4 +5φ) having all integer terms. 
     * @return an AlgebraicVector
     */
    @Override
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
    @Override
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

    @Override
    public AlgebraicNumber parseLegacyNumber( String val )
    {
        throw new IllegalStateException( "This field does not support vZome 2.x files." );
    }
    
    @Override
    public AlgebraicNumber parseVefNumber( String string, boolean isRational )
    {
        long[] pairs = new long[ this .getOrder() * 2 ];
        // The pairs array is pre-initialized with zeros since it's a native type (not Integer)
        // so we can simply set all of the denominators to 1.
        for ( int i = 1; i < pairs.length; i+=2 ) {
            pairs[ i ] = 1;
        }
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
                numStack   .push( Integer .parseInt( parts[ 0 ] ) );
                denomStack .push( (parts.length > 1)? Integer .parseInt( parts[ 1 ] ) : 1 );
            }
            int i = 0;
            while( ! numStack.empty() ) {
                pairs[ i++ ] = numStack   .pop();
                pairs[ i++ ] = denomStack .pop();
            }
            if ( i == 4 && getOrder() > 2 ) {
                pairs = this .convertGoldenNumberPairs( new long[] { pairs[0], pairs[1], pairs[2], pairs[3] } );
            }
        }
        else {
            // format >= 7 supports the rational numeric format which expects no irrational factors,
            // so there are no parentheses or commas, but still allows the optional "/" if a denominator is specified.
            String[] parts = string .split( "/" );
            pairs[ 0 ] = Integer .parseInt( parts[ 0 ] );
            pairs[ 1 ] = (parts.length > 1)? Integer .parseInt( parts[ 1 ] ) : 1;
        }
        return this.numberFactory .createAlgebraicNumberFromPairs( this, pairs );
    }

    @Override
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
    private AlgebraicNumber parseNumber( StringTokenizer tokens )
    {
        int order = this .getOrder();
        long[] pairs = new long[ order * 2 ];
        for ( int i = 0; i < order; i++ ) {
            String digit = tokens .nextToken();
            String[] parts = digit.split( "/" );
            pairs[ i * 2 ] = Long.parseLong( parts[ 0 ] );
            if ( parts.length > 1 )
                pairs[ i * 2 + 1 ] = Long.parseLong( parts[ 1 ] );
            else
                pairs[ i * 2 + 1 ] = 1;
        }
        return this.numberFactory .createAlgebraicNumberFromPairs( this, pairs );
    }

    @Override
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
     * @return the number of independent multipliers in this field.
     * These are the primitive elements of the field.
     * The value should be less than or equal to getNumIrrationals.
     * It will be less whenever the irrationals are dependent.
     * For example, in the field for sqrt(phi), there is only one
     * multiplier, since the other irrational is just the square of that one.
     */
    @Override
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

    public String getMathML( BigRational[] factors )
    {
        StringBuffer buf = new StringBuffer();
        int first = 0;
        for ( int i = 0; i < factors.length; i++ )
        {
            BigRational factor = factors[ i ];
            if ( factor .isZero() ) {
                ++ first;
                continue;
            }
            
            if ( factor .isNegative() )
            {
                factor = factor .negate();
                buf .append( "<mo>-</mo>" );
            }
            else if ( i > first )
            {
                buf .append( "<mo>+</mo>" );
            }
            
            if ( i == 0 )
                buf .append( factor .getMathML() );
            else
            {
                if ( ! factor .isOne() )
                {
                    buf .append( factor .getMathML() );
                }
                String multiplier = this .getIrrational( i, DEFAULT_FORMAT );
                buf .append( "<mi>" );
                buf .append( multiplier );
                buf .append( "</mi>" );
            }
        }
        if ( first == factors.length )
            // all factors were zero
            return "<mn>0</mn>";
        else if ( factors.length - first > 1 )
            return "<mrow>" + buf .toString() + "</mrow>";
        else
            return buf .toString();
    }

    @Override
    public void interpretScript( String script, String language, Point offset, Symmetry symmetry, ConstructionChanges effects ) throws Exception
    {
        throw new Exception( "Scripts are only supported in the golden field." );
    }
}
