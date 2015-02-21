
package com.vzome.core.math;


import java.nio.IntBuffer;

import com.vzome.core.algebra.AlgebraicNumber;
import com.vzome.core.algebra.PentagonField;

/**
 * A number of the form A*Tau+B, where Tau is the golden ratio,
 * and A and B are integers.
 * As Walter Venable showed, any location reachable with Zometool struts
 * can be expressed as a vector whose coordinates are golden numbers,
 * with a scale factor of 1/2, taking the short blue strut length as 1.
 * <p/>
 * I can further show that this works for negative powers of Tau as well, using
 * the bi-directional Fibonacci sequence implemented below.  This means that
 * Zome struts can scale arbitrarily large *and small*.
 * <p/>
 */
class GoldenNumber implements IntegralNumber
{
    public static final double GOLDEN_RATIO = ( 1.0 + Math.sqrt( 5.0 ) ) / 2.0;

	public static final GoldenNumber ZERO = new GoldenNumber( 0, 0, 1, 0 );
	
	public static final GoldenNumber ONE = new GoldenNumber( 0, 1, 1, 0 );
	
	public static final GoldenNumber VARIABLE = new GoldenNumber( 0, 1, 1, -99 );

    public static final GoldenNumber HALF = new GoldenNumber( 0, 1, 2, 0 );

    public static final GoldenNumber TWO = new GoldenNumber( 0, 2, 1, 0 );

    public static final GoldenNumber TAU;

    public static final GoldenNumber TAU_INV;

    public static final GoldenNumber TAU_2, TAU_3;

	public static final int INT_SIZE = 4;
    
    static IntegralNumber.Factory FACTORY; // set by the factory, to avoid circular dependency

    protected final int phis, ones, divisor, power;
	
	protected transient GoldenNumber normalized = null;

	protected transient double mValue;
	
	private final PentagonField field;

	public void write( IntBuffer buf )
	{}
    
	public GoldenNumber( int phis, int ones, int divisor, int power )
	{
	    this .phis = phis;
	    this .ones = ones;
	    this .divisor = divisor;
	    this .power = power;
	    if ( divisor == 0 )
	        throw new IllegalStateException();
//	    if ( divisor > 200 )
//	        throw new IllegalStateException();
	    this.field = new PentagonField();
	}
    
    public AlgebraicNumber getAlgebraicNumber()
    {
        return field .createAlgebraicNumber( ones, phis, divisor, power );
    }
	
	public int getPhis()
	{
	    return ((GoldenNumber) normalize()) .phis;
	}
	
	public int getOnes()
	{
	    return ((GoldenNumber) normalize()) .ones;
	}
	
	public int getDivisor()
	{
	    return ((GoldenNumber) normalize()) .divisor;
	}
	
    private GoldenNumber add( GoldenNumber addend )
    {
        if ( power == addend.power )
            if ( divisor == addend .divisor )
                return new GoldenNumber( phis + addend .phis, ones + addend .ones, divisor, power );
            else {
                return (GoldenNumber) new GoldenNumber( addend.divisor*phis + divisor*addend.phis, addend.divisor*ones + divisor*addend.ones,
                        divisor*addend.divisor, power ) .normalize();
            }
        else
            return null;
    }

	public  IntegralNumber plus( IntegralNumber arg )
	{
        GoldenNumber addend = (GoldenNumber) ((GoldenNumber) arg) .normalize();
        if ( addend .isZero() )
            return this;
        if ( this .isZero() )
            return addend;
		GoldenNumber result = ((GoldenNumber) normalize() ) .add( addend );
		if ( result != null )
            return result;
		boolean addendBigger = addend .power > power;
		GoldenNumber bigger = addendBigger? addend : this;
		GoldenNumber smaller = addendBigger? this : addend;
		int diff = bigger .power - smaller .power;
		GoldenNumber factor = phi( diff );
		bigger = (GoldenNumber) bigger .scale( -diff );
		return smaller .add( (GoldenNumber) factor .multiply( bigger ) );
	}


	public IntegralNumber minus( IntegralNumber subtrahend )
	{
		if ( subtrahend .isZero() )
			return this;
		return plus( subtrahend .neg() );
	}
	
	
	/**
	 * multiply by the given power of phi
	 * @param power
	 * @return
	 */
	public IntegralNumber scale( int power )
	{
	    if ( power == 0 )
	        return this;
	    return getFactory() .createIntegralNumber( phis, ones, divisor, this.power + power );
	}
	
	
	protected IntegralNumber multiply( IntegralNumber multiplicand )
	{
	    // MUST NOT denormalize or normalize here!
        GoldenNumber mcand = (GoldenNumber) multiplicand;
		int a1 = mcand .phis;
		int b1 = mcand .ones;
        //        return getFactory() .createIntegralNumber( ones * a1 + phis * b1, 2 * phis * a1 + ones * b1, divisor * mcand .divisor, power + mcand .power );
        return new GoldenNumber( (phis + ones) * a1 + phis * b1, phis * a1 + ones * b1, divisor * mcand .divisor, power + mcand .power );
	}

	/**
	 * @param multiplicand
	 * @return
	 */
	public IntegralNumber times( IntegralNumber multiplicand )
	{
	    // TODO denormalize here (special case for tau^n), but add a non-denormalizing private method
		if ( this .isZero() || multiplicand .isZero() )
			return zero();
		if ( this .isOne() )
			return multiplicand;
		if ( multiplicand .isOne() )
			return this;
		return multiply( multiplicand );
	}

	public IntegralNumber times( int factor )
	{
		if ( this .isZero() || factor == 0 )
			return zero();
		if ( factor == 1 )
			return this;
		if ( factor == -1 )
		    return div( -1 );
		return getFactory() .createIntegralNumber( phis * factor, ones * factor, divisor, power );
	}
	
	
	public IntegralNumber div( int divisor )
	{
	    if ( divisor == 1 )
	        return this;
	    return getFactory() .createIntegralNumber( phis, ones, this.divisor * divisor, power );
	}
	

	public IntegralNumber div( IntegralNumber divisor )
	{
		// optimizations
		if ( divisor .isZero() )
			throw new IllegalArgumentException();
		if ( divisor .isOne() )
			return this;
		if ( this .equals( divisor ) )
			return one();
		
		return this .multiply( divisor .inverse() );
	}


    public IntegralNumber neg()
    {
    	if ( this .isZero() )
    		return this;
        return getFactory() .createIntegralNumber( - phis, - ones, divisor, power );
    }
    
    
    protected IntegralNumber normalize()
    {
        if ( normalized == null ) {
            if ( power == 0 && divisor == 1 )
                normalized = this;
            normalized = new GoldenNumber( phis, ones, divisor, 0 );
            if ( power != 0 ) {
                // normalize is called by equals(), which is called by most public methods,
                //   so we cannot call times() or tau() here.
                normalized = (GoldenNumber) normalized .multiply( phi( power ) );
            }
            if ( normalized.divisor == 1 || normalized.divisor == -1 )
                return normalized;
            int gcd = reduce( normalized.phis, normalized.divisor );
            gcd = reduce( gcd, normalized.ones );
            if ( gcd != 1 )
                normalized = new GoldenNumber( normalized.phis/gcd, normalized.ones/gcd, normalized.divisor/gcd, 0 );
        }
        return normalized;
    }
    

    private int reduce( int num, int denom )
    {
        int remainder = num;
        if ( Math.abs(denom) < Math.abs(num) ) {
            remainder = denom;
            denom = num;
        }
        while ( Math.abs(remainder) > 0 ) {
            int next = denom % remainder;
            if ( next == 0 )
                return remainder;
            denom = remainder;
            remainder = next;
        }
        return denom;
    }
    
    /**
     * Return the value of this GoldenNumber as an unstructured real number.
     * That is, a*Tau+b is computed and returned.
     * @return double
     */
	public double value()
	{
	    if ( this .isZero() )
	        return 0d;
		if ( mValue == 0d ) {
	        mValue = phis * GOLDEN_RATIO + ones;
	        if ( divisor != 1 )
	            mValue /= divisor;
		    if ( power != 0 )
		        mValue *= phi( power ) .value();
		}
		return mValue;
	}
	
	
	public IntegralNumber inverse()
	{
	    return getFactory() .createIntegralNumber( -divisor*phis, divisor*(phis+ones), determinant(), -power );
	}
	
	
	public int determinant()
	{
	    return ones*ones + phis*ones - phis*phis;
	}

	
	private final static String PHI = "phi";
	

    public String toString()
    {
        return normalize() .toString( DEFAULT_FORMAT );
    }

    
    /**
     * Normalizes for all but DEFAULT_FORMAT
     */
    public String toString( int format )
    {
        GoldenNumber num = (GoldenNumber) normalize();
        int A = num .getPhis();
        int B = num .getOnes();
        int divisor = num .getDivisor();
        String result = "";
        switch ( format ) {

        case ZOMIC_FORMAT:
            return B + " " + A;

        case VEF_FORMAT:
            if ( divisor == 1 )
                return "(" + A + "," + B + ")";
            else if ( divisor == -1 )
                return "(" + -A + "," + -B + ")";
            else
                return "(" + A + "/" + divisor + "," + B + "/" + divisor + ")";

        case EXPRESSION_FORMAT:
            result = "*tau";
            if ( A == 0 ) {
                if ( B == 0 )
                    return "0";
                result = Integer .toString( B );
            }
            else {
                result = A + result;
                if ( B != 0 )
                    if ( B < 0 )
                        result += B;
                    else
                        result += "+" + B;
            }
            if ( divisor != 1 )
                result = "(" + result + ")/" + divisor;
            return result;
            
        case DEFAULT_FORMAT:
        default:
            result = PHI;
            if ( this.phis == 0 ) {
                if ( this.ones == 0 )
                    return "0";
                result = Integer .toString( this.ones );
            }
            else {
                if ( phis == - 1 ) {
                    result = "-" + result;
                }
                else {
                    if ( phis != 1 ) {
                        result = phis + result;
                    }
                }
                if ( ones != 0 ) {
                    if ( ones < 0 ) {
                        result += ones;
                    }
                    else {
                        result += "+" + ones;
                    }
                }
            }
            if ( this.divisor != 1 || power != 0 ) {
                result = "(" + result + ")";
                if ( this.divisor != 1 )
                    result += "/" + this.divisor;
                if ( power != 0 )
                    result = PHI + "^" + power + "*" + result;
            }
            return result;
        }
    }
    
	
    public static GoldenNumber parseString( String string )
    {
        int div = 1;
        if ( string .startsWith( "(" ) ) {
            int closeParen = string .indexOf( ")" );
            div = Integer .parseInt( string .substring( closeParen+2 ) );
            string = string .substring( 1, closeParen );
        }
        
        int phis = 0;
        int phiIndex = string .indexOf( PHI );
        if ( phiIndex >= 0 ) {
            String part = string .substring( 0, phiIndex );
            if ( part .length() == 0 )
                phis = 1;
            else if ( part .equals( "-" ) )
                phis = -1;
            else
                phis = Integer .parseInt( part );
            string = string .substring( phiIndex+3 );
        }
        
        int ones;
        if ( string .length() == 0 )
            ones = 0;
        else {
            if ( string .startsWith( "+" ) )
                string = string .substring( 1 );
            ones = Integer .parseInt( string );
        }
        
        return new GoldenNumber( phis, ones, div, 0 );
    }
    

	public boolean equals( Object other )
	{
		if ( other == null ) {
			return false;
		}
		if ( other == this ) {
			return true;
		}
		if ( ! ( other instanceof GoldenNumber ) )
		    return false;
		
		GoldenNumber gn = (GoldenNumber) other;
		int div1 = getDivisor();
		int div2 = gn .getDivisor();

		if ( getPhis() * div2 != gn.getPhis() * div1 )
		    return false;
		if ( getOnes() * div2 != gn.getOnes() * div1 )
		    return false;
		return true;
	}

	public int hashCode()
	{
	    return Math .round( ((float) value()) * 100000f );
	}
	

	private static final int MAX_COMMON = 12;

	private static final int[] COMMON_FIB = new int[ MAX_COMMON ];

	private static final GoldenNumber[][] TAU_POWERS = new GoldenNumber[ 2 ][ MAX_COMMON ];
	private static final GoldenNumber[][] TAU_POWERS_NORMALIZED = new GoldenNumber[ 2 ][ MAX_COMMON ];
	
	private static final int PLUS = 0, MINUS = 1;

	static {
        
		COMMON_FIB[ 0 ] = 0;
		COMMON_FIB[ 1 ] = 1;
		int i;
		for ( i = 2; i < MAX_COMMON; i++ )
			COMMON_FIB[ i ] = COMMON_FIB[ i-1 ] + COMMON_FIB[ i-2 ];
		
		TAU_POWERS[ PLUS ][ 0 ] = ONE;
		TAU_POWERS[ MINUS ][ 0 ] = ONE;
		TAU_POWERS[ PLUS ][ 1 ] = new GoldenNumber( 0, 1, 1, 1 );
		TAU_POWERS_NORMALIZED[ PLUS ][ 0 ] = ONE;
		TAU_POWERS_NORMALIZED[ MINUS ][ 0 ] = ONE;
		TAU_POWERS_NORMALIZED[ PLUS ][ 1 ] = new GoldenNumber( 1, 0, 1, 0 );
		for ( i = 2; i < MAX_COMMON; i++ ) {
			TAU_POWERS[ PLUS ][ i ] = new GoldenNumber( 0, 1, 1, i );
			TAU_POWERS[ MINUS ][ i-1 ] = new GoldenNumber( 0, 1, 1, 1-i );
			TAU_POWERS_NORMALIZED[ PLUS ][ i ] = new GoldenNumber( COMMON_FIB[ i ], COMMON_FIB[ i-1 ], 1, 0 );
			TAU_POWERS_NORMALIZED[ MINUS ][ i-1 ] = ( i % 2 != 0 )?
				new GoldenNumber( -COMMON_FIB[ i-1 ], COMMON_FIB[ i ], 1, 0 ):
				new GoldenNumber( COMMON_FIB[ i-1 ], -COMMON_FIB[ i ], 1, 0 );
		}
		TAU_POWERS[ MINUS ][ i-1 ] = new GoldenNumber( 0, 1, 1, 1-i );
		int maxFib = COMMON_FIB[ i-1 ] + COMMON_FIB[ i-2 ];
		TAU_POWERS_NORMALIZED[ MINUS ][ i-1 ] = ( i % 2 != 0 )?
			new GoldenNumber( - COMMON_FIB[ i-1 ], maxFib, 1, 0 ):
			new GoldenNumber( COMMON_FIB[ i-1 ], -maxFib, 1, 0 );
		
			TAU = tau( 1 );
			TAU_INV = tau( -1 );
            TAU_2 = tau( 2 );
            TAU_3 = tau( 3 );
	}


    /**
     * A non-traditional, bi-directional definition of the Fibonacci sequence,
     * which fits naturally into the definition of "tau", below.
     * The sequence is symmetrical around 0, except that alternating members
     * on the negative side are negated: ...,-8,5,-3,2,-1,1,0,1,1,2,3,5,8,...
     * Note that the sequence-defining relationship still holds, it is just not
     * useful for calculating negative members.
     */
	public static int fib( int n )
	{

		boolean neg = n < 0, evenNeg = false;
		if ( neg ) {
			n = -n;
			evenNeg = n % 2 == 0;
		}
		int fib = 0;
		if ( n < MAX_COMMON )
			fib = COMMON_FIB[ n ];
		else
		{
			int f2 = COMMON_FIB[ MAX_COMMON-2 ], f1 = COMMON_FIB[ MAX_COMMON-1 ];
			for ( int i = MAX_COMMON-1; i < n; i++ ){
				fib = f1 + f2;
				f2 = f1;
				f1 = fib;
			}
		}
		return evenNeg? -fib : fib;
		

//			if ( n < 0 ) {
//				return fib( n + 2 ) - fib( n + 1 );
//			}
//			else {
//                // the definition of Fibonacci's sequence
//				return fib( n - 1 ) + fib( n - 2 );
//			}
	}


    /**
     * Compute Tau to the given power. For example,
     * tau(0) == 1, tau(1) == Tau, etc.
     * @param n
     * @return GoldenNumber
     */
	public static GoldenNumber tau( int n )
	{
		boolean neg = n < 0;
		if ( neg ) n = -n;
		if ( n < MAX_COMMON )
			return TAU_POWERS[ neg? MINUS : PLUS ][ n ];
		if ( neg ) n = -n;
		return new GoldenNumber( 0, 1, 1, n );
	}
	
	
	private static GoldenNumber phi( int n )
	{
		boolean neg = n < 0;
		if ( neg ) n = -n;
		if ( n < MAX_COMMON )
			return TAU_POWERS_NORMALIZED[ neg? MINUS : PLUS ][ n ];
		if ( neg ) n = -n;
        return new GoldenNumber( fib( n ), fib( n - 1 ), 1, 0 );
	}


	public static void main( String[] args )
	{
        System .out .println( new GoldenNumber( 0, 7850, 625, 0 ) .normalize().toString());
        System .out .println( new GoldenNumber( 7850, 0, 625, 0 ) .normalize().toString());
        System .out .println( new GoldenNumber( 625, 7850, 25, 0 ) .normalize().toString());
        System .out .println( new GoldenNumber( 625, 255, 25, 0 ) .normalize().toString());
        System .out .println( new GoldenNumber( 0, 0, 1, 0 ) .normalize().toString());
        System .out .println( new GoldenNumber( 0, 1, -1, 0 ) .normalize().toString());
        System .out .println( new GoldenNumber( 17, 1, 1, 0 ) .normalize().toString());
        System .out .println( new GoldenNumber( 1, 17, 1, 0 ) .normalize().toString());
        
        IntegralNumber lll = new GoldenNumber( 0, -1, 1, 0 );
        parseString( lll .toString() );
        
		for ( int i = -13; i < 14; i++ ) {
		    GoldenNumber num = tau( i );
		    System .out .println( i + "    " + fib(i) + "   " + tau(i) + "   " + phi(i) + "   " + num
		            				+ "   " + num.normalize() + "   " + num .value() );
		}
		
		int a = 235, b = 893, a1 = 838, b1 = 123;
		
		IntegralNumber A = new GoldenNumber( a, b, 1, 3 );
		IntegralNumber B = new GoldenNumber( a1, b1, 1, 3 );
		System .out .println( A );
		
		double Aval = A .value();
		double Bval = B .value();
		
		long start = System .currentTimeMillis();
		
		for ( long i = 0; i < 100000000; i++ ){
			if ( (a + b) * a1 + a * b1 > 0 );
			if ( a * a1 + b * b1 > 0 );
		}

		long stop = System .currentTimeMillis();
		System.out.println( stop - start );
		start = System .currentTimeMillis();;
		
		for ( long i = 0; i < 100000000; i++ )
			if ( Aval * Bval > 0.0 );
			
		stop = System .currentTimeMillis();
		System.out.println( stop - start );
		
		

        GoldenNumber g1 = new GoldenNumber( 0, -4, -2, 3 );
        System.out.println( g1 .normalize() );
        GoldenNumber g2 = new GoldenNumber( 0, 2, 1, 3);
        if ( ! g1 .equals( g2 ) )
            System .out .println( "g1 != g2" );
        else if ( ! g2 .equals( g1 ) )
            System .out .println( "g2 != g1" );
        else
            System .out .println( "g1 == g2" );
        if ( g1 .hashCode() != g2 .hashCode() )
            System .out .println( "hashes differ" );
	}


    public int euclideanNorm()
    {
        return getPhis() + getOnes();
    }

    public boolean isZero()
    {
        return getOnes() == 0 && getPhis() == 0;
    }

    public boolean isOne()
    {
        return getDivisor() == 1 && getOnes() == 1 && getPhis() == 0;
    }

    public IntegralNumber zero()
    {
        return getFactory() .zero();
    }

    public IntegralNumber one()
    {
        return getFactory() .one();
    }

    public Factory getFactory()
    {
        return FACTORY;
    }

    public IntegralNumber conjugate()
    {
        return getFactory() .createIntegralNumber( -getPhis(), getOnes()+getPhis(), getDivisor(), 0 );
    }
}


