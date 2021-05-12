package com.vzome.jsweet;

import static jsweet.util.Lang.any;
import static jsweet.util.Lang.array;

import java.util.Arrays;

import com.vzome.core.algebra.AlgebraicField;
import com.vzome.core.algebra.AlgebraicNumber;

public class JsAlgebraicNumber implements AlgebraicNumber
{
    private final JsAlgebraicField field;
    private final int[] factors;

    public JsAlgebraicNumber( JsAlgebraicField field, int[] factors )
    {
        this .field = field;
        this .factors = any(array(factors).slice());
    }

    @Override
    public AlgebraicField getField()
    {
        return this .field;
    }

    @Override
    public double evaluate()
    {
        return this .field .evaluateNumber( this .factors );
    }

    @Override
    public int[] toTrailingDivisor()
    {
        return any(array(factors).slice());
    }

    /**
     * 
     * @param n is the value to be added
     * @return this + n
     */
    public AlgebraicNumber plus( int n )
    {
        return n == 0 ? this : this.plus( field.createRational(n) );
    }

    /**
     * 
     * @param num is the numerator of the rational value to be added
     * @param den is the denominator of the rational value to be added
     * @return this + (num / den)
     */
    public AlgebraicNumber plus( int num, int den )
    {
        return this.plus( field.createRational( num, den ) );
    }

    @Override
    public AlgebraicNumber plus( AlgebraicNumber that )
    {
        int[] factors = this.field .add( this.factors, ((JsAlgebraicNumber) that) .factors );
        return new JsAlgebraicNumber( this .field, factors );
    }

    @Override
    public AlgebraicNumber minus( int n )
    {
        return n == 0 ? this : this.minus( field.createRational(n) );
    }

    @Override
    public AlgebraicNumber minus( int num, int den )
    {
        return this.minus( field.createRational( num, den ) );
    }

    @Override
    public AlgebraicNumber minus(AlgebraicNumber that)
    {
        int[] factors = this.field .subtract( this.factors, ((JsAlgebraicNumber) that) .factors );
        return new JsAlgebraicNumber( this .field, factors );
    }

    @Override
    public AlgebraicNumber times( AlgebraicNumber that )
    {
        int[] factors = this.field .multiply( this.factors, ((JsAlgebraicNumber) that) .factors );
        return new JsAlgebraicNumber( this .field, factors );
    }

    @Override
    public AlgebraicNumber dividedBy( int divisor )
    {
        return divisor == 1 ? this : this.times( field.createRational( 1, divisor ) );
    }

    @Override
    public AlgebraicNumber dividedBy( int num, int den )
    {
        return this.times( field.createRational( den, num ) );
    }

    @Override
    public AlgebraicNumber dividedBy( AlgebraicNumber that )
    {
        int[] recip = this.field .reciprocal( ((JsAlgebraicNumber) that) .factors );
        int[] factors = this.field .multiply( this.factors, recip );
        return new JsAlgebraicNumber( this .field, factors );
    }

    public boolean equals( AlgebraicNumber that )
    {
        return Arrays.equals( this.factors, ((JsAlgebraicNumber) that ) .factors );
    }

    @Override
    public AlgebraicNumber negate()
    {
        int[] factors = this.field .negate( this.factors );
        return new JsAlgebraicNumber( this .field, factors );
    }

    @Override
    public boolean isZero()
    {
        return this .equals( this.field .zero() );
    }

    @Override
    public boolean isOne()
    {
        return this .equals( this.field .one() );
    }

    @Override
    public AlgebraicNumber reciprocal()
    {
        return new JsAlgebraicNumber( field, ((JsAlgebraicField) field) .reciprocal( factors ) );
    }

    /**
     * @param buf
     * @param format must be one of the following values.
     * The result is formatted as follows:
     * <br>
     * {@code DEFAULT_FORMAT    // 4 + 3φ}<br>
     * {@code EXPRESSION_FORMAT // 4 +3*phi}<br>
     * {@code ZOMIC_FORMAT      // 4 3}<br>
     * {@code VEF_FORMAT        // (3,4)}<br>
     */
    public void getNumberExpression( StringBuffer buf, int format )
    {
        buf .append( this .toString( format ) );
    }

    /**
     * @param format must be one of the following values.
     * The result is formatted as follows:
     * <br>
     * {@code DEFAULT_FORMAT    // 4 + 3φ}<br>
     * {@code EXPRESSION_FORMAT // 4 +3*phi}<br>
     * {@code ZOMIC_FORMAT      // 4 3}<br>
     * {@code VEF_FORMAT        // (3,4)}
     * @return 
     */
    public String toString( int format )
    {
        return Arrays .toString( this .factors );
    }

    @Override
    public int compareTo( AlgebraicNumber other )
    {
        if ( this == other ) {
            return 0;
        }
        if ( other.equals(this) ) { 
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

    @Override
    public boolean greaterThan( AlgebraicNumber other )
    {
        return compareTo(other) > 0;
    }

    @Override
    public boolean lessThan( AlgebraicNumber other )
    {
        return compareTo(other) < 0;
    }

    @Override
    public boolean greaterThanOrEqualTo( AlgebraicNumber other )
    {
        return compareTo(other) >= 0;
    }

    @Override
    public boolean lessThanOrEqualTo( AlgebraicNumber other )
    {
        return compareTo(other) <= 0;
    }

    @Override
    public int signum()
    {
        return Double.valueOf( Math.signum( evaluate()) ).intValue();
    }

    
    
    
    
    
    

    @Override
    public AlgebraicNumber times(int n)
    {
        throw new RuntimeException( "unimplemented" );
    }

    @Override
    public AlgebraicNumber times(int num, int den)
    {
        throw new RuntimeException( "unimplemented" );
    }

    @Override
    public boolean isRational()
    {
        throw new RuntimeException( "unimplemented" );
    }
}
