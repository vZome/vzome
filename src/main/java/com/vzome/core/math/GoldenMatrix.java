package com.vzome.core.math;



abstract class GoldenMatrix{

	public String toString()
    {
        StringBuffer buff = new StringBuffer();
        GoldenMatrix transpose = this .transpose();
        buff .append( transpose .x .toString() + "\n" );
        buff .append( transpose .y .toString() + "\n" );
        buff .append( transpose .z .toString() + "\n" );
        buff .append( transpose .w .toString() );
        return buff.toString();
    }

    public final  GoldenVector x, y, z, w;  // these are the columns, not the rows

	public 
	GoldenMatrix( GoldenVector x, GoldenVector y, GoldenVector z, GoldenVector w ) {
		super();

			this .x = x;
			this .y = y;
			this .z = z;
			this .w = w;
		}
    
    public interface Factory extends GoldenVector.Factory
    {
        GoldenMatrix createGoldenMatrix( GoldenVector x, GoldenVector y, GoldenVector z, GoldenVector w );

        GoldenMatrix identity();
    }
    
    public abstract Factory getFactory();

	public GoldenVector transform( GoldenVector in )
	{
	    GoldenMatrix t = transpose();
		return getFactory() .createGoldenVector( t .x .dot( in ), t .y .dot( in ), t .z .dot( in ), t .w .dot( in ), x .getX().one() );
	}

	public GoldenMatrix times( GoldenMatrix other )
	{
        // TODO this appears to have a bug, something to do with a transpose... does not seem to affect
        //  multiplication by the identity (or its negation)
        Factory f = getFactory();
        IntegralNumber one = f .one();
		return f .createGoldenMatrix(
                f .createGoldenVector( x .dot( other .x ), x .dot( other .y ), x .dot( other .z ), x .dot( other .w ), one ),
                f .createGoldenVector( y .dot( other .x ), y .dot( other .y ), y .dot( other .z ), y .dot( other .w ), one ),
                f .createGoldenVector( z .dot( other .x ), z .dot( other .y ), z .dot( other .z ), z .dot( other .w ), one ),
                f .createGoldenVector( w .dot( other .x ), w .dot( other .y ), w .dot( other .z ), w .dot( other .w ), one ) );
	}

	public  GoldenMatrix transpose()
	{
        Factory f = getFactory();
        IntegralNumber one = f .one();
		return f .createGoldenMatrix(
                f .createGoldenVector( x .getX(), y .getX(), z .getX(), w .getX(), one ),
                f .createGoldenVector( x .getY(), y .getY(), z .getY(), w .getY(), one ),
                f .createGoldenVector( x .getZ(), y .getZ(), z .getZ(), w .getZ(), one ),
                f .createGoldenVector( x .getW(), y .getW(), z .getW(), w .getW(), one ) );
	}

	public IntegralNumber determinant()
	{
        IntegralNumber cofactor = y.y .times( z.z .times( w.w ) );
        cofactor = cofactor .plus( z.y .times( w.z .times( y.w ) ) );
        cofactor = cofactor .plus( w.y .times( y.z .times( z.w ) ) );
        cofactor = cofactor .minus( w.y .times( z.z .times( y.w ) ) );
        cofactor = cofactor .minus( y.y .times( w.z .times( z.w ) ) );
        cofactor = cofactor .minus( z.y .times( y.z .times( w.w ) ) );
        IntegralNumber determinant = cofactor .times( x.x );
        cofactor = y.x .times( z.z .times( w.w ) );
        cofactor = cofactor .plus( z.x .times( w.z .times( y.w ) ) );
        cofactor = cofactor .plus( w.x .times( y.z .times( z.w ) ) );
        cofactor = cofactor .minus( w.x .times( z.z .times( y.w ) ) );
        cofactor = cofactor .minus( y.x .times( w.z .times( z.w ) ) );
        cofactor = cofactor .minus( z.x .times( y.z .times( w.w ) ) );
        determinant = determinant .plus( cofactor .times( x.y ) );
        cofactor = y.x .times( z.y .times( w.w ) );
        cofactor = cofactor .plus( z.x .times( w.y .times( y.w ) ) );
        cofactor = cofactor .plus( w.x .times( y.y .times( z.w ) ) );
        cofactor = cofactor .minus( w.x .times( z.y .times( y.w ) ) );
        cofactor = cofactor .minus( y.x .times( w.y .times( z.w ) ) );
        cofactor = cofactor .minus( z.x .times( y.y .times( w.w ) ) );
        determinant = determinant .plus( cofactor .times( x.z ) );
        cofactor = y.x .times( z.y .times( w.z ) );
        cofactor = cofactor .plus( z.x .times( w.y .times( y.z ) ) );
        cofactor = cofactor .plus( w.x .times( y.y .times( z.z ) ) );
        cofactor = cofactor .minus( w.x .times( z.y .times( y.z ) ) );
        cofactor = cofactor .minus( y.x .times( w.y .times( z.z ) ) );
        cofactor = cofactor .minus( z.x .times( y.y .times( w.z ) ) );
        determinant = determinant .plus( cofactor .times( x.w ) );
        return determinant;
	}
    
	/**
	 * Return a row-first enumeration of the matrix elements:
	 *    x    y    z
	 *   -------------
	 *    0    1    2
	 *    3    4    5
	 *    6    7    8
	 *    
	 * @return
	 */
    public float[] getValues()
    {
        float[] values = new float[9];
        RealVector r = x .location();
        values[ 0 ] = (float) r .x;
        values[ 3 ] = (float) r .y;
        values[ 6 ] = (float) r .z;
        r = y .location();
        values[ 1 ] = (float) r .x;
        values[ 4 ] = (float) r .y;
        values[ 7 ] = (float) r .z;
        r = z .location();
        values[ 2 ] = (float) r .x;
        values[ 5 ] = (float) r .y;
        values[ 8 ] = (float) r .z;
        return values;
    }
	
	public int hashcode()
	{
	    return x .hashCode() & y .hashCode() & z .hashCode() & w .hashCode();
	}
	
	public boolean equals( Object other )
	{
		if ( other == null )
			return false;
		if ( other == this )
			return true;
		try  {
		    GoldenMatrix gm = (GoldenMatrix) other;
			return x .equals( gm .x ) && y .equals( gm .y ) && z .equals( gm .z ) && w .equals( gm .w );
		} catch( ClassCastException cce ) {
			return false;
		}
	}

    public GoldenMatrix neg()
    {
        return getFactory() .createGoldenMatrix( x.neg(), y.neg(), z.neg(), w.neg() );
    }
}


