

package com.vzome.core.math.symmetry;

import java.util.HashMap;
import java.util.Map;


/**
 * @author Scott Vorthmann
 *
 */
public class NamingConvention
{
	public final static String UNKNOWN_AXIS = "UNKNOWN AXIS";
	
	private final Map<String, DirectionNaming> mNamings = new HashMap<>();
	
	public void addDirectionNaming( DirectionNaming naming )
	{
		mNamings .put( naming .getName(), naming );
	}
	
	public Axis getAxis( String color, String name )
	{
		DirectionNaming naming = mNamings .get( color );
        if ( naming == null )
            return null;
		return naming .getAxis( name );
	}
	
	public String getName( Axis axis )
	{
        for (DirectionNaming naming : mNamings .values()) {
            if ( naming .getDirection() .equals( axis .getDirection() ) )
                return naming .getName( axis );
        }
		return UNKNOWN_AXIS;
	}

}
