
package com.vzome.core.math;

import java.nio.FloatBuffer;
import java.text.NumberFormat;
import java.util.Locale;



/**
 * A vector (point) in R3 (3 dimensional space with real number
 * coordinate axes).
 */
public class RealVector {

	public static final  RealVector ORIGIN = new RealVector( 0d, 0d, 0d );

	private static final NumberFormat FORMAT = NumberFormat .getNumberInstance( Locale .US );
	
	static {
        FORMAT .setMaximumFractionDigits( 5 );
        FORMAT .setMinimumFractionDigits( 1 );
	}
	
	public final  double x, y, z;

	/**
		 * Construct a new vector from its coordinate values.
		 */
	public RealVector( double x, double y, double z ) {
		super();

			this .x = x;
			this .y = y;
			this .z = z;
		}

	/**
     * Return a string representing this vector as XML attribute values.
     */
	public String toXmlAttributes()
	{
		return "x=\"" + FORMAT .format(x) +
			"\" y=\"" + FORMAT .format(y) +
			"\" z=\"" + FORMAT .format(z) + "\"";
		}

	/**
		 * Return a string representing this vector in the form "x,y,z".
		 */
	public String toString( NumberFormat format )
	{
		return format .format(x) + "," + format .format(y) + "," + format .format(z);
		}

	/**
		 * Return a string representing this vector in the form "x,y,z".
		 */
	public String toString()
	{
		return FORMAT .format(x) + "," + FORMAT .format(y) + "," + FORMAT .format(z);
	}

	/**
     * Return a string representing this vector in the form "x y z".
     */
	public String spacedString()
	{
		String result = FORMAT .format(x) + " " + FORMAT .format(y) + " " + FORMAT .format(z);
		return result;
	}

	/**
		 * Return the sum of this vector plus the vector "other",
		 * as a new Vector3D.
		 */
	public  RealVector plus( RealVector other ) {
		return new RealVector( x + other .x, y + other .y, z + other .z );
	}

	/**
		 * Return the difference of this vector minus the vector "other",
		 * as a new Vector3D.
		 */
	public  RealVector minus( RealVector other ) {
		return new RealVector( x - other .x, y - other .y, z - other .z );
	}

	/**
		 * Return a new vector equal to this vector scaled by the given factor.
		 */
	public  RealVector scale( double factor ) {
		return new RealVector( x * factor, y * factor, z * factor );
	}

	/**
	 * Return the scalar (dot) product with the other vector
	 */
	public  double dot( RealVector other ) {
		return x * other .x + y * other .y + z * other .z;
	}

    public  RealVector cross( RealVector that )
    {
        return new RealVector(
                this.y * that .z - this.z * that .y,
                this.z * that .x - this.x * that .z,
                this.x * that .y - this.y * that .x );
    }

	/**
		 * Return the length of this vector.
		 */
	public  double length() {
		return Math .sqrt( dot( this ) );
	}
	
	public RealVector normalize()
	{
	    return scale( 1d / length() );
	}

	public void write( FloatBuffer buf, int offset )
	{
	    buf .put( offset, (float) x );
	    buf .put( offset+1, (float) y );
	    buf .put( offset+2, (float) z );
//	    System .out .println( x + "   " + y + "   " + z );
	}

	public  boolean equals( Object other ) {
		if ( other == null ) {
			return false;
		}
		if ( other == this ) {
			return true;
		}
		try  {
			RealVector v = (RealVector) other;
			return x == v .x && y == v .y && z == v .z;
		} catch( ClassCastException cce ) {
			return false;
		}
	}

}


