

package com.vzome.core.math.symmetry;


/**
 * @author Scott Vorthmann
 *
 */
public class DirectionNaming
{
    protected static final String[] SIGN = new String[]{ "+", "-" };
    
	private final String mName;
	
	private final Direction mDirection;
	
	public DirectionNaming( Direction dir, String name )
	{
		mName = name;
		mDirection = dir;
	}
	
	public String getName()
	{
		return mName;
	}
	
	/**
	 * Default behavior.
	 * @param axisName
	 * @return
	 */
	public Axis getAxis( String axisName )
	{
		return mDirection .getAxis( getSign( axisName ), getInteger( axisName ) );
	}
	
	protected int getSign( String axisName )
	{
	    if ( axisName .startsWith( "-" ) )
	        return Symmetry .MINUS;
	    return Symmetry .PLUS;
	}
	
	protected int getInteger( String axisName )
	{
	    if ( axisName .startsWith( "-" ) || axisName .startsWith( "+" ) )
	        return Integer .parseInt( axisName .substring( 1 ) );
	    return Integer .parseInt( axisName );
	}
	
	public String getName( Axis axis )
	{
		String sign = SIGN[ axis .getSense() ];
		return sign + axis .getOrientation();
	}
	
	public String getFullName( Axis axis )
	{
	    return mName + " " + getName( axis );
	}

	public Direction getDirection()
	{
		return mDirection;
	}

}
