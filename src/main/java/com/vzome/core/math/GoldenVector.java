
package com.vzome.core.math;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;

import org.w3c.dom.Element;

import com.vzome.core.algebra.AlgebraicVector;

abstract class GoldenVector
{
    public interface Factory extends IntegralNumber.Factory
    {
        GoldenVector createGoldenVector( IntegralNumber x, IntegralNumber y, IntegralNumber z, IntegralNumber w, IntegralNumber factor );
        
        GoldenVector origin();

        GoldenVector getAxis( int w2 );
        
        GoldenVector parseXml( Element elem );
        
        AlgebraicVector parseRationalVector( Element elem );
    }
    
    public static final int W_AXIS = 0, X_AXIS = 1, Y_AXIS = 2, Z_AXIS = 3;
    
	public final IntegralNumber x, y, z, w, factor;
	
	private transient GoldenVector normalized = null;

	private transient RealVector mLocation;

    
	public void write( IntBuffer buf )
	{
	    x .write( buf );
	    y .write( buf );
	    z .write( buf );
	    w .write( buf );
	    factor .write( buf );
	}

	public void write( FloatBuffer buf )
	{
	    float fval = (float) factor .value();
	    buf .put( (float) x .value() * fval );
	    buf .put( (float) y .value() * fval );
	    buf .put( (float) z .value() * fval );
	    buf .put( (float) w .value() * fval );
//	    System .out .println( x .value() * fval + "   " + y .value() * fval + "   " + z .value() * fval );
	}

	protected 
	GoldenVector( IntegralNumber x, IntegralNumber y, IntegralNumber z, IntegralNumber w, IntegralNumber factor ) {
		super();

			this .x = x;
			this .y = y;
			this .z = z;
			this .w = w;
			this .factor = factor;
		}

	public  GoldenVector plus( GoldenVector addend )
	{
	    if ( factor .equals( addend .factor ) )
	        return getFactory() .createGoldenVector( x .plus( addend .x ), y .plus( addend .y ), z .plus( addend .z ), w .plus( addend .w ), factor );
	    else
	        return normalize() .plus( addend .normalize() );
	}

	public  GoldenVector minus( GoldenVector subtrahend )
	{
	    if ( factor .equals( subtrahend .factor ) )
	        return getFactory() .createGoldenVector( x .minus( subtrahend .x ), y .minus( subtrahend .y ), z .minus( subtrahend .z ), w .minus( subtrahend .w ), factor );
	    else
	        return normalize() .minus( subtrahend .normalize() );
	}

	public  GoldenVector times( IntegralNumber scale )
	{
	    if ( scale .isOne() )
	        return this;
		return getFactory() .createGoldenVector( x, y, z, w, factor .times( scale ) );
	}

	public  GoldenVector times( int scale )
	{
	    if ( scale == 1 )
	        return this;
		return getFactory() .createGoldenVector( x, y, z, w, factor .times( scale ) );
	}
	
	public  GoldenVector div( IntegralNumber scale )
	{
	    if ( scale .isOne() )
	        return this;
		return getFactory() .createGoldenVector( x, y, z, w, factor .div( scale ) );
	}

	public  GoldenVector div( int scale )
	{
	    if ( scale == 1 )
	        return this;
		return getFactory() .createGoldenVector( x, y, z, w, factor .div( scale ) );
	}
	
	public GoldenVector scale( int power )
	{
	    if ( power == 0 )
	        return this;
	    return getFactory() .createGoldenVector( x, y, z, w, factor .scale( power ) );
	}

	public  GoldenVector neg()
	{
		return getFactory() .createGoldenVector( x, y, z, w, factor .neg() );
	}

	public  IntegralNumber dot( GoldenVector other )
	{
	    GoldenVector v1 = normalize();
	    GoldenVector v2 = other .normalize();
        IntegralNumber result = v1.x .times( v2 .x );
		result = result .plus( v1.y .times( v2 .y ) );
		result = result .plus( v1.z .times( v2 .z ) );
		result = result .plus( v1.w .times( v2 .w ) );
		return result;
	}

	public  GoldenVector cross( GoldenVector other )
	{
	    GoldenVector v1 = normalize();
	    GoldenVector v2 = other .normalize();
		return getFactory() .createGoldenVector(
		        v1.y .times( v2 .z ) .minus( v1.z .times( v2 .y ) ),
		        v1.z .times( v2 .x ) .minus( v1.x .times( v2 .z ) ),
		        v1.x .times( v2 .y ) .minus( v1.y .times( v2 .x ) ),
		        z.zero(), z.one() );
	}

	public RealVector location()
	{
		if ( mLocation == null ) {
		    if ( factor .isOne() )
		        mLocation = new RealVector( x .value(), y .value(), z .value() );
		    else
		        mLocation = new RealVector( x .value() * factor.value(), y .value() * factor.value(), z .value() * factor.value() );
		}
		return mLocation;
	}
    
    public GoldenVector projectTo3D()
    {
        GoldenVector result = normalize();
        
        // looks like I was experimenting with a perspective projection, here.
//        IntegralNumber w = result .getW();
//        if ( w .isZero() )
          return getFactory() .createGoldenVector( result .getX(), result .getY(), result .getZ(), this.x.zero(), this.x.one() );
//        else {
//            w = w .scale( -6 );
//            return getFactory() .createGoldenVector( result .getX() .div( w ), result .getY() .div( w ), result .getZ() .div( w ), this.x.zero(), this.x.one() );
//        }
    }
	
	private GoldenVector normalize()
	{
	    if ( normalized == null ) {
		    if ( factor .isOne() )
		        normalized = this;
		    else
		        normalized = getFactory() .createGoldenVector( factor .times( x ), factor .times( y ), factor .times( z ), factor .times( w ), factor.one() );
		}
	    return normalized;
	}

	public IntegralNumber getX()
	{
		return normalize() .x;
	}
	
	public IntegralNumber getY()
	{
		return normalize() .y;
	}
	
	public IntegralNumber getZ()
	{
		return normalize() .z;
	}
	
	public IntegralNumber getW()
	{
		return normalize() .w;
	}
	
	/**
	 * A GoldenVector is "Zome-constructible" iff all its coordinates
	 * have divisor = 1 or = 2.
	 * @return
	 */
	public boolean isZomeConstructible()
	{
	    if ( ! factor .isOne() )
	        return normalize() .isZomeConstructible();
	    if ( Math .abs( getX() .getDivisor() ) > 2 )
	        return false;
	    if ( Math .abs( y .getDivisor() ) > 2 )
	        return false;
	    if ( Math .abs( z .getDivisor() ) > 2 )
	        return false;
	    return true;
	}

    public String toString( boolean fourDim )
    {
        return normalize() .asString( fourDim );
    }

    public String toString()
    {
        return normalize() .asString( true );
	}

	private String asString( boolean fourDim )
	{
		StringBuffer out = new StringBuffer( "[  " );
        if ( fourDim )  {
            out .append( w .toString( GoldenNumber.EXPRESSION_FORMAT ) );
            out .append( "  " );
        }
		out .append( x .toString( GoldenNumber.EXPRESSION_FORMAT ) );
		out .append( "  " );
		out .append( y .toString( GoldenNumber.EXPRESSION_FORMAT ) );
		out .append( "  " );
		out .append( z .toString( GoldenNumber.EXPRESSION_FORMAT ) );
		out .append( "  ]" );
	    if ( ! factor .isOne() )
	        out .append( " / " + factor .toString() );
		return out .toString();
	}
	

	public  boolean equals( Object other )
	{
		if ( other == null )
			return false;
		if ( other == this )
			return true;
		try  {
			GoldenVector gv = (GoldenVector) other;
			return getX() .equals( gv .getX() )
			&& getY() .equals( gv .getY() )
			&& getZ() .equals( gv .getZ() )
			&& getW() .equals( gv .getW() );
		} catch( ClassCastException cce ) {
			return false;
		}
	}

	public  int hashCode()
	{
	    if ( ! factor .isOne() )
	        return normalize() .hashCode();
		return x .hashCode() * 0x10000 + y .hashCode() * 0x100
		+ z .hashCode() + w .hashCode();
	}

	/**
	 * returns ZERO if vectors are not colinear.
	 * @param vector
	 * @return
	 */
	public IntegralNumber div( GoldenVector vector )
	{
		if ( ! vector .cross( this ) .isOrigin() )
			return this.x.zero();
		if ( this.x .equals( vector.x ) && y .equals( vector.y )
				&& z .equals( vector.z )
			&& w .equals( vector.w ) )
			return factor .div( vector .factor );
		IntegralNumber a = x, b = vector.x;
		if ( a .isZero() ){
			a = y; b = vector.y;
		}
		if ( a .isZero() ){
			a = z; b = vector.z;
		}
		if ( a .isZero() ){
			a = w; b = vector.w;
		}
		return a .times( factor ) .div( b .times( vector .factor ) );
	}


    public void getXML( Element elem )
    {
    	IntegralNumber coord = getX();
    	if ( ! coord .isZero() )
    		DomUtils .addAttribute( elem, "x", coord .toString() );
    	coord = getY();
    	if ( ! coord .isZero() )
    		DomUtils .addAttribute( elem, "y", coord .toString() );
    	coord = getZ();
    	if ( ! coord .isZero() )
    		DomUtils .addAttribute( elem, "z", coord .toString() );
    	coord = getW();
    	if ( ! coord .isZero() )
    		DomUtils .addAttribute( elem, "w", coord .toString() );
    }

    public boolean isOrigin()
    {
        return x.isZero() && y.isZero() && z.isZero() && w.isZero();
    }
    
    public abstract Factory getFactory();

    public GoldenVector conjugate()
    {
        return getFactory() .createGoldenVector( getX() .conjugate(), getY() .conjugate(), getZ() .conjugate(), getW() .conjugate(), getFactory() .one() );
    }

}


