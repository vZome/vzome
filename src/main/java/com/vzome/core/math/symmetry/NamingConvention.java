

package com.vzome.core.math.symmetry;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;


/**
 * @author Scott Vorthmann
 *
 */
public class NamingConvention
{
	private final Map mNamings = new HashMap();
	
	public void addDirectionNaming( DirectionNaming naming )
	{
		mNamings .put( naming .getName(), naming );
	}
	
	public Axis getAxis( String color, String name )
	{
		DirectionNaming naming = (DirectionNaming) mNamings .get( color );
        if ( naming == null )
            return null;
		return naming .getAxis( name );
	}
	
	public String getName( Axis axis )
	{
		for ( Iterator namings = mNamings .values() .iterator(); namings .hasNext(); ) {
			DirectionNaming naming = (DirectionNaming) namings .next();
			if ( naming .getDirection() .equals( axis .getDirection() ) )
				return naming .getName( axis );
		}
		return "UNKNOWN AXIS";
	}

}
