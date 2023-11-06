
package com.vzome.core.algebra;

import java.io.IOException;
import java.math.BigInteger;
import java.util.Arrays;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;

/**
 * 
 * Immutable representation of an Algebraic Number
 *
 */
@JsonSerialize( using = AlgebraicNumberImpl.Serializer.class )
public class AlgebraicNumberImpl implements AlgebraicNumber
{
    private final AbstractAlgebraicField field;
    private final BigRational[] factors;

    private final boolean isOne;
    private final boolean isZero;

    private Double doubleValue;	// initialized on first use
    private Integer signum;     // initialized on first use
    private final String[] toString = new String[AlgebraicField .VEF_FORMAT + 1]; // cache various String representations

    private Integer hashCode;	// initialized on first use

    public static AlgebraicNumberFactory FACTORY = new AlgebraicNumberFactory()
    {
        @Override
        public BigRational zero()
        {
            return BigRationalImpl.ZERO;
        }
        
        @Override
        public BigRational one()
        {
            return BigRationalImpl.ONE;
        }
                
        @Override
        public BigRational createBigRational( long numerator, long denominator )
        {
            return new BigRationalImpl( numerator, denominator );
        }

        @Override
        public AlgebraicNumber createAlgebraicNumber( AlgebraicField field, int[] numerators, int divisor )
        {
            BigRational[] brs = new BigRational[ numerators .length ];
            for ( int j = 0; j < numerators.length; j++ ) {
                brs[ j ] = new BigRationalImpl( numerators[ j ], divisor );
            }
            return new AlgebraicNumberImpl( field, brs );
        }

        @Override
        public AlgebraicNumber createAlgebraicNumberFromTD( AlgebraicField field, int[] trailingDivisorForm )
        {
            int n = trailingDivisorForm .length;
            int denominator = 1;
            if ( n == field .getOrder() + 1 ) {
                --n;
                denominator = trailingDivisorForm[ n ];
            }
            BigRational[] brs = new BigRational[ n ];
            for ( int j = 0; j < n; j++ ) {
                brs[ j ] = new BigRationalImpl( trailingDivisorForm[ j ], denominator );
            }
            return new AlgebraicNumberImpl( field, brs );
        }

        @Override
        public AlgebraicNumber createAlgebraicNumberFromPairs( AlgebraicField field, long[] pairs )
        {
            BigRational[] brs = new BigRational[ pairs .length / 2 ];
            for ( int j = 0; j < brs.length; j++ ) {
                brs[ j ] = new BigRationalImpl( pairs[ j*2 ], pairs[ j*2+1 ] );
            }
            return new AlgebraicNumberImpl( field, brs );
        }

        @Override
        public AlgebraicNumber createRational( AlgebraicField field, long numerator, long denominator )
        {
            return new AlgebraicNumberImpl( field, new BigRationalImpl( numerator, denominator ) );
        }

        @Override
        public boolean isPrime( int n )
        {
            final int certainty = 100; // same value as nextProbablePrime()
            return BigInteger.valueOf(n).isProbablePrime(certainty);
        }

        @Override
        public int nextPrime( int prime )
        {
            return BigInteger.valueOf( prime ).nextProbablePrime() .intValue();
        }
    };
    
    /**
     * This non-varargs constructor does not call normalize(), 
     * so it can safely be called from within the base AlgebraicField constructor
     * before initializeNormalizer is called by derived ParameterizedField constructors
     * @param field
     * @param units
     */
    AlgebraicNumberImpl( AlgebraicField field, BigRational units )
    {
        this.field = (AbstractAlgebraicField) field;
        factors = new BigRational[ field .getOrder() ];
        factors[ 0 ] = units;
        for ( int i = 1; i < factors.length; i++ ) {
            factors[ i ] = BigRationalImpl.ZERO;
        }
        isZero = isZero(this.factors);
        isOne = isOne(this.factors);
    }

    public AlgebraicNumberImpl( AlgebraicField field, BigRational[] givenFactors )
    {
        if ( givenFactors.length > field .getOrder() )
            throw new IllegalStateException( givenFactors.length + " is too many factors for field \"" + field.getName() + "\"" );
        this .field = (AbstractAlgebraicField) field;
        this .factors = new BigRational[ field .getOrder() ];
        for ( int i = 0; i < givenFactors.length; i++ ) {
            this .factors[ i ] = givenFactors[ i ] == null 
                    ? BigRationalImpl.ZERO
                            : givenFactors[ i ];
        }
        for ( int i = givenFactors.length; i < this.factors.length; i++ ) {
            this .factors[ i ] = BigRationalImpl.ZERO;
        }
        this .field .normalize(this.factors);
        isZero = isZero(this.factors);
        isOne = isOne(this.factors);
    }

    /**
     * Extract the least common multiple of the divisors.
     * @return
     */
    public final BigInteger getDivisor()
    {
        BigInteger lcm = BigInteger.ONE;
        for (BigRational factor : this.factors) {
            if(! ((BigRationalImpl) factor).isWhole() ) {
                BigInteger aDivisor = ((BigRationalImpl) factor).getDenominator();
                lcm = lcm .multiply( aDivisor ) .abs() .divide( lcm .gcd( aDivisor ) );
            }
        }
        return lcm;
    }

    public BigRational[] getFactors()
    {
        return this .factors.clone(); // return a copy to ensure that this instance remains immutable
    }

    @Override
    public int hashCode()
    {
        if(hashCode == null) {
            hashCode = 31 // prime
                    + Arrays.hashCode( factors );
        }
        return hashCode;
    }

    @Override
    public boolean equals( Object obj )
    {
        if ( this == obj )
            return true;
        if ( obj == null )
            return false;
        if ( getClass() != obj.getClass() )
            return false;
        AlgebraicNumberImpl other = (AlgebraicNumberImpl) obj;
        if(!field.equals( other.field )) {
            String reason  = "Invalid comparison of " 
                    + getClass().getSimpleName() + "s"
                    + "with different fields: "
                    + field.getName()
                    + " and "
                    + other.field.getName();
            throw new IllegalStateException(reason);
        }
        return Arrays.equals( factors, other.factors );
    }
    
    @Override
    public boolean greaterThan(AlgebraicNumber other) {
        return compareTo(other) > 0;
    }

    @Override
    public boolean lessThan(AlgebraicNumber other) {
        return compareTo(other) < 0;
    }

    @Override
    public boolean greaterThanOrEqualTo(AlgebraicNumber other) {
        return compareTo(other) >= 0;
    }

    @Override
    public boolean lessThanOrEqualTo(AlgebraicNumber other) {
        return compareTo(other) <= 0;
    }

    @Override
    public int compareTo(AlgebraicNumber other) {
        if (this == other) {
            return 0;
        }
        if (other.equals(this)) { 
            // intentionally throws a NullPointerException if other is null
            // or an IllegalStateException if fields are different
            return 0;
        }
        // Since we know both fields are the same, 
        // both AlgebraicNumbers must have the same number of factors
        // so there's no point in comparing factors.length
        // assert(this.factors.length == other.factors.length);
        Double d1 = this.evaluate();
        Double d2 = other.evaluate();
        // Because of the rounding errors when converting to a double,
        // It's possible that the two double values are equal 
        // yet the two AlgebraicaNumbers are not.
        // TODO: Develop a test case to show this scenario
        // TODO: Consider if it's worth the overhead of using BigDecimal.compareTo() in this case
        return d1.compareTo(d2);
    }
    
    public static AlgebraicNumber max(AlgebraicNumber a, AlgebraicNumber b) {
        return a.greaterThanOrEqualTo(b) ? a : b;
    }

    public static AlgebraicNumber min(AlgebraicNumber a, AlgebraicNumber b) {
        return a.lessThanOrEqualTo(b) ? a : b;
    }

    @Override
    public AlgebraicField getField()
    {
        return this .field;
    }

    /**
     * 
     * @param n is the value to be added
     * @return this + n
     */
    @Override
    public AlgebraicNumber plusInt( int n )
    {
        return n == 0 ? this : this.plus(field.createRational(n));
    }

    /**
     * 
     * @param num is the numerator of the rational value to be added
     * @param den is the denominator of the rational value to be added
     * @return this + (num / den)
     */
    @Override
    public AlgebraicNumber plusRational( int num, int den )
    {
        return this.plus(field.createRational(num, den));
    }

    /**
     * 
     * @param that is the value to be added
     * @return this + n
     */
    @Override
    public AlgebraicNumber plus( AlgebraicNumber that )
    {
        if ( this .isZero() )
            return that;
        if ( that .isZero() )
            return this;
        int order = this .factors .length;
        BigRational[] sum = new BigRational[ order ];
        for ( int i = 0; i < order; i++ ) {
            sum[ i ] = this .factors[ i ] .plus( ((AlgebraicNumberImpl) that) .factors[ i ] );
        }
        return new AlgebraicNumberImpl( this .field, sum );
    }

    /**
     * 
     * @param n is the value to be multiplied
     * @return this * n
     */
    @Override
    public AlgebraicNumber timesInt( int n )
    {
        switch(n) {
        case 0:
            return field.zero();
        case 1:
            return this;
        case -1:
            return this.negate();
        default:
            return this.times(field.createRational(n));
        }
    }

    /**
     * 
     * @param num is the numerator of the rational value to be multiplied
     * @param den is the denominator of the rational value to be multiplied
     * @return this * (num / den)
     */
    @Override
    public AlgebraicNumber timesRational( int num, int den )
    {
        return this.times(field.createRational(num, den));
    }

    @Override
    public AlgebraicNumber times( AlgebraicNumber that )
    {
        if ( this.isZero() || that .isZero() )
            return this .field .zero();
        if ( this .isOne() )
            return that;
        if ( that .isOne() )
            return this;
        return new AlgebraicNumberImpl( this .field, this .field .multiply( this .factors, ((AlgebraicNumberImpl) that) .factors ) );
    }

    /**
     * 
     * @param n is the value to be subtracted
     * @return this - n
     */
    @Override
    public AlgebraicNumber minusInt( int n )
    {
        return n == 0 ? this : this.minus(field.createRational(n));
    }

    /**
     * 
     * @param num is the numerator of the rational value to be subtracted
     * @param den is the denominator of the rational value to be subtracted
     * @return this - (num / den)
     */
    @Override
    public AlgebraicNumber minusRational( int num, int den )
    {
        return this.minus(field.createRational(num, den));
    }

    /**
     * 
     * @param that is the value to be subtracted
     * @return this - n
     */
    @Override
    public AlgebraicNumber minus( AlgebraicNumber that )
    {
        // Subtraction is not commutative so don't be tempted to optimize for the case when this.isZero()
        if ( that .isZero() )
            return this;
        return this .plus( that .negate() );
    }

    /**
     * 
     * @param divisor
     * @return this / divisor
     */
    @Override
    public AlgebraicNumber dividedByInt( int divisor )
    {
        return divisor == 1 ? this : this.dividedBy(field.createRational(divisor));
    }

    /**
     * 
     * @param num is the numerator of the divisor
     * @param den is the denominator of the divisor
     * @return this / (num / den)
     */
    @Override
    public AlgebraicNumber dividedByRational( int num, int den )
    {
        return this.dividedBy(field.createRational(num, den));
    }

    @Override
    public AlgebraicNumber dividedBy( AlgebraicNumber that )
    {
        // Division is not commutative so don't be tempted to optimize for the case when this.isOne()
        if ( that .isOne() )
            return this;
        return this .times( that .reciprocal() );
    }

    @Override
    public double evaluate()
    {
        if(doubleValue == null) {
            doubleValue = field .evaluateNumber( factors );
        }
        return doubleValue;
    }

    private static boolean isZero(BigRational[] factors)
    {
        for ( BigRational factor : factors ) {
            if ( ! factor .isZero() )
                return false;
        }
        return true;
    }

    private static boolean isOne(BigRational[] factors)
    {
        if( ! factors[ 0 ] .isOne() ) {
            return false;
        }
        for( int i = 1; i < factors.length; i++ ) {
            if ( ! factors[ i ] .isZero() )
                return false;
        }
        return true;
    }

    // isRational() is not currently used enough 
    // to warrant caching it in a private field like isZero and isOne
    // so just calculate it
    @Override
    public boolean isRational()
    {
        for( int i = 1; i < factors.length; i++ ) {
            if ( ! factors[ i ] .isZero() )
                return false;
        }
        return true;
    }

    @Override
    public boolean isZero()     { return isZero; }
    @Override
    public boolean isOne()      { return isOne; }
    
    @Override
    public int signum() {
        if(signum == null) {
            signum = Double.valueOf( Math.signum(evaluate()) ).intValue();
        }
        return signum;
    }
    
    @Override
    public AlgebraicNumber negate()
    {
        BigRational[] result = new BigRational[ factors .length ];
        for ( int i = 0; i < result.length; i++ ) {
            result[ i ] = factors[ i ] .negate();
        }
        return new AlgebraicNumberImpl( field, result );
    }

    @Override
    public AlgebraicNumber reciprocal()
    {
        return new AlgebraicNumberImpl( field, field .reciprocal( factors ) );
    }

    /**
     * 
     * @param buf
     * @param format must be one of the following values.
     * The result is formatted as follows:
     * <br>
     * {@code DEFAULT_FORMAT    // 4 + 3φ}<br>
     * {@code EXPRESSION_FORMAT // 4 +3*phi}<br>
     * {@code ZOMIC_FORMAT      // 4 3}<br>
     * {@code VEF_FORMAT        // (3,4)}<br>
     */
    @Override
    public void getNumberExpression( StringBuffer buf, int format )
    {
        if(toString[format] == null) {
            int originalLength = buf.length(); // may not be empty
            field .getNumberExpression( buf, factors, format ); // calculate it
            toString[format] = buf.toString().substring(originalLength); // cache it 
        } else {
            buf.append(toString[format]);
        }
    }

    /**
     * 
     * @param format must be one of the following values.
     * The result is formatted as follows:
     * <br>
     * {@code DEFAULT_FORMAT    // 4 + 3φ}<br>
     * {@code EXPRESSION_FORMAT // 4 +3*phi}<br>
     * {@code ZOMIC_FORMAT      // 4 3}<br>
     * {@code VEF_FORMAT        // (3,4)}
     * @return 
     */
    @Override
    public String toString( int format )
    {
        if(toString[format] == null) {
            StringBuffer buf = new StringBuffer();
            getNumberExpression( buf, format );
            //	        toString[format] = buf .toString(); // getNumberExpression() will have cached it so no need to do it again here 
        }
        return toString[format];
    }

    @Override
    public String toString()
    {
        return this .toString( AlgebraicField .DEFAULT_FORMAT );
    }
    
    @Override
    public int[] toTrailingDivisor()
    {
        BigInteger divisor = this .getDivisor();
        int order = this .factors .length;
        int[] result = new int[ order + 1 ];
        result[ order ] = divisor .intValue();
        for ( int i = 0; i < order; i++ ) {
            result[ i ] = ((BigRationalImpl) this .factors[ i ] .times( new BigRationalImpl( divisor, BigInteger.ONE ) )) .getNumerator() .intValue();
        }
        return result;
    }

    // JSON serialization:
    //
    //  A custom serializer works here without compromising the view mechanism,
    //  because we don't need to recursively serialize objects that use views
    //  themselves.  It is difficult or impossible to pass along the SerializerProvider,
    //  which carries the view class.
    //
    //  AlgebraicNumber itself cannot use views, since they don't control @JsonValue,
    //  which is really what we are simulating here.  You can only have one @JsonValue
    //  annotation per class.
    
    @SuppressWarnings("serial")
    public static class Serializer extends StdSerializer<AlgebraicNumberImpl> {
        
        public Serializer()
        {
            this(null);
        }
       
        public Serializer( Class<AlgebraicNumberImpl> t )
        {
            super(t);
        }
     
        @Override
        public void serialize( AlgebraicNumberImpl value, JsonGenerator jgen, SerializerProvider provider ) 
            throws IOException, JsonProcessingException
        {
            @SuppressWarnings("rawtypes")
            Class view = provider .getActiveView();
            if ( ( view != null ) && Views.Real.class .isAssignableFrom( view ) )
            {
                jgen .writeNumber( value .evaluate() );
            }
            else if ( ( view != null ) && Views.TrailingDivisor.class .isAssignableFrom( view ) )
            {
                jgen .writeObject( value .toTrailingDivisor() );
            }
            else
            {
                jgen .writeObject( value .factors );
            }
        }
    }

    @Override
    public String getMathML()
    {
        return field .getMathML( factors );
    }
}
