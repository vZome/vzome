
//(c) Copyright 2011, Scott Vorthmann.

package com.vzome.core.algebra;

import java.math.BigInteger;
import java.util.Arrays;

public class AlgebraicNumber implements Fields.Element<AlgebraicNumber>, Comparable<AlgebraicNumber>
{
    private final AlgebraicField field;
    private final BigRational[] factors;

    AlgebraicNumber( AlgebraicField field, BigRational... factors )
    {
        if ( factors.length > field .getOrder() )
            throw new IllegalStateException( factors.length + " is too many coordinates for field \"" + field.getName() + "\"" );
        this .field = field;
        this .factors = new BigRational[ field .getOrder() ];
        for ( int i = 0; i < factors.length; i++ ) {
            this .factors[ i ] = factors[ i ] == null 
                    ? BigRational.ZERO
                    : factors[ i ];
        }
        for ( int i = factors.length; i < this.factors.length; i++ ) {
            this .factors[ i ] = BigRational.ZERO;
        }
    }

    /**
     * Extract the least common multiple of the divisors.
     * @param value
     * @return
     */
    public final BigInteger getDivisor()
    {
        BigInteger lcm = BigInteger.ONE;
        for (BigRational factor : this.factors) {
            BigInteger aDivisor = factor.getDenominator();
            lcm = lcm .multiply( aDivisor ) .abs() .divide( lcm .gcd( aDivisor ) );
        }
        return lcm;
    }

    public BigRational[] getFactors()
    {
        return this .factors;
    }

    @Override
    public int hashCode()
    {
        final int prime = 31;
        int result = 1;
        result = prime * result 
                + Arrays.hashCode( factors );
        return result;
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
        AlgebraicNumber other = (AlgebraicNumber) obj;
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
    public int compareTo(AlgebraicNumber other) {
        if (this == other) {
            return 0;
        }
        if (other.equals(this)) { 
            // intentionally throws a NullPointerException if other is null
            // or an IllegalStateException if fields are different
            return 0;
        }
        int comparison = Integer.compare(factors.length, other.factors.length);
        if (comparison != 0) {
            return comparison;
        }
        Double d1 = this.evaluate();
        Double d2 = other.evaluate();
        return d1.compareTo(d2);
    }
    
    public AlgebraicField getField()
    {
        return this .field;
    }

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
            sum[ i ] = this .factors[ i ] .plus( that .factors[ i ] );
        }
        return new AlgebraicNumber( this .field, sum );
    }

    public AlgebraicNumber times( AlgebraicNumber that )
    {
        if ( this.isZero() || that .isZero() )
            return this .field .zero();
        if ( this .isOne() )
            return that;
        if ( that .isOne() )
            return this;
        return new AlgebraicNumber( this .field, this .field .multiply( this .factors, that .factors ) );
    }

    @Override
    public AlgebraicNumber minus( AlgebraicNumber that )
    {
        // Subtraction is not commutative so don't be tempted to optimize for the case when this.isZero()
        if ( that .isZero() )
            return this;
        return this .plus( that .negate() );
    }

    public AlgebraicNumber dividedBy( AlgebraicNumber that )
    {
        // Division is not commutative so don't be tempted to optimize for the case when this.isOne()
        if ( that .isOne() )
            return this;
        return this .times( that .reciprocal() );
    }

    public double evaluate()
    {
        return this .field .evaluateNumber( factors );
    }

    @Override
    public boolean isZero()
    {
        for ( BigRational factor : this .factors ) {
            if ( ! factor .isZero() )
                return false;
        }
        return true;
    }

    @Override
    public boolean isOne()
    {
        if ( ! this .factors[ 0 ] .isOne() )
            return false;
        for ( int i = 1; i < this .factors.length; i++ ) {
            if ( ! this .factors[ i ] .isZero() )
                return false;
        }
        return true;
    }

    @Override
    public AlgebraicNumber negate()
    {
        BigRational[] result = new BigRational[ this .factors .length ];
        for ( int i = 0; i < result.length; i++ ) {
            result[ i ] = this .factors[ i ] .negate();
        }
        return new AlgebraicNumber( this .field, result );
    }

    @Override
    public AlgebraicNumber reciprocal()
    {
        return new AlgebraicNumber( this .field, this .field .reciprocal( this .factors ) );
    }

    public void getNumberExpression( StringBuffer buf, int format )
    {
        this .field .getNumberExpression( buf, this .factors, format );
    }

    public String toString( int format )
    {
        StringBuffer buf = new StringBuffer();
        this .getNumberExpression( buf, format );
        return buf .toString();
    }

    @Override
    public String toString()
    {
        return this .toString( AlgebraicField .DEFAULT_FORMAT );
    }

}
