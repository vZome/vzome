
//(c) Copyright 2011, Scott Vorthmann.

package com.vzome.core.algebra;

import java.math.BigInteger;
import java.util.Arrays;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonValue;

/**
 * 
 * Immutable representation of an Algebraic Number
 *
 */
public class AlgebraicNumber implements Fields.Element<AlgebraicNumber>, Comparable<AlgebraicNumber>
{
    private final AlgebraicField field;
    private final BigRational[] factors;

    private final boolean isOne;
    private final boolean isZero;
    
    private Double doubleValue;	// initialized on first use
    private final String[] toString = new String[AlgebraicField .VEF_FORMAT + 1]; // cache various String representations
    
    private Integer hashCode;	// initialized on first use
    
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
        isZero = isZero(this);
        isOne = isOne(this);
    }

    /**
     * Extract the least common multiple of the divisors.
     * @param value
     * @return
     */
    @JsonIgnore
    public final BigInteger getDivisor()
    {
        BigInteger lcm = BigInteger.ONE;
        for (BigRational factor : this.factors) {
        	if(! factor.isWhole() ) {
        		BigInteger aDivisor = factor.getDenominator();
        		lcm = lcm .multiply( aDivisor ) .abs() .divide( lcm .gcd( aDivisor ) );
        	}
        }
        return lcm;
    }

    @JsonValue
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
    
    @JsonIgnore
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

    @Override
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
    	if(doubleValue == null) {
        	doubleValue = field .evaluateNumber( factors );
    	}
        return doubleValue;
    }

    private static boolean isZero(AlgebraicNumber that)
    {
        for ( BigRational factor : that .factors ) {
            if ( ! factor .isZero() )
                return false;
        }
        return true;
    }

    private static boolean isOne(AlgebraicNumber that)
    {
        if ( ! that .factors[ 0 ] .isOne() )
            return false;
        for ( int i = 1; i < that .factors.length; i++ ) {
            if ( ! that .factors[ i ] .isZero() )
                return false;
        }
        return true;
    }

    @Override
    public boolean isZero()     { return isZero; }
    @Override
    public boolean isOne()      { return isOne; }

    @Override
    public AlgebraicNumber negate()
    {
        BigRational[] result = new BigRational[ factors .length ];
        for ( int i = 0; i < result.length; i++ ) {
            result[ i ] = factors[ i ] .negate();
        }
        return new AlgebraicNumber( field, result );
    }

    @Override
    public AlgebraicNumber reciprocal()
    {
        return new AlgebraicNumber( field, field .reciprocal( factors ) );
    }

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

}
