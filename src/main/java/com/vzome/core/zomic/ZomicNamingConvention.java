
package com.vzome.core.zomic;

import com.vzome.core.math.symmetry.Axis;
import com.vzome.core.math.symmetry.Direction;
import com.vzome.core.math.symmetry.DirectionNaming;
import com.vzome.core.math.symmetry.IcosahedralSymmetry;
import com.vzome.core.math.symmetry.NamingConvention;
import com.vzome.core.math.symmetry.Permutation;
import com.vzome.core.math.symmetry.Symmetry;

/**
 * @author Scott Vorthmann
 *
 */
public class ZomicNamingConvention extends NamingConvention
{
	public static final int SHORT = 3;

	public static final int MEDIUM = 4;

	public static final int LONG = 5;
	
	public ZomicNamingConvention( IcosahedralSymmetry symm )
	{
		Direction dir = symm .getDirection( "red" );
		DirectionNaming redNames = new ZomodDirectionNaming( dir, new int[]{ 0, 1, 2, 15, 17, 46 } );
		addDirectionNaming( redNames );
		dir = symm .getDirection( "yellow" );
		DirectionNaming yellowNames = new ZomodDirectionNaming( dir, new int[]{ 6, 9, 12, 0, 3, 1, 14, 5, 24, 17 } );
		addDirectionNaming( yellowNames );
		dir = symm .getDirection( "blue" );
		addDirectionNaming( new ZomodDirectionNaming( dir, new int[]{ 9, 12, 0, 3, 6, 1, 14, 18, 26, 52, 58, 4, 7, 2, 5 } ) );

        dir = symm .getDirection( "olive" );
        addDirectionNaming( new DirectionNaming( dir, dir .getName() ) );
        dir = symm .getDirection( "maroon" );
        addDirectionNaming( new DirectionNaming( dir, dir .getName() ) );
        dir = symm .getDirection( "lavender" );
        addDirectionNaming( new DirectionNaming( dir, dir .getName() ) );
        dir = symm .getDirection( "rose" );
        addDirectionNaming( new DirectionNaming( dir, dir .getName() ) );
        dir = symm .getDirection( "navy" );
        addDirectionNaming( new DirectionNaming( dir, dir .getName() ) );
        dir = symm .getDirection( "turquoise" );
        addDirectionNaming( new DirectionNaming( dir, dir .getName() ) );
        dir = symm .getDirection( "coral" );
        addDirectionNaming( new DirectionNaming( dir, dir .getName() ) );
        dir = symm .getDirection( "sulfur" );
        addDirectionNaming( new DirectionNaming( dir, dir .getName() ) );
        
        dir = symm .getDirection( "green" );
        addDirectionNaming( new GreenDirectionNaming( dir, redNames, yellowNames ) );
		dir = symm .getDirection( "orange" );
		addDirectionNaming( new GreenDirectionNaming( dir, redNames, yellowNames ) );
		dir = symm .getDirection( "purple" );
		addDirectionNaming( new GreenDirectionNaming( dir, redNames, yellowNames ) {

			public String getName( Axis axis )
			{
				int orn = axis .getOrientation();
			    Axis redNeighbor = mRedNames .getDirection() .getAxis( axis .getSense(), orn );
			    String redName = mRedNames .getName( redNeighbor );
			    // rotate twice since purple RY is opposite green RY
			    Permutation rot = redNeighbor .getRotationPermutation();
			    orn = rot .mapIndex( rot .mapIndex( orn ) );
			    if ( axis.getSense() == Symmetry .MINUS )
				    orn = rot .mapIndex( orn );
			    Axis yellowNeighbor = mYellowNames .getDirection() .getAxis( axis .getSense(), orn );
			    String yellowName = mYellowNames .getName( yellowNeighbor ) .substring( 1 ); // remove the sign
			    
//			    System .out .println( SIGN[ axis.getSense() ] + axis .getOrientation() + " is purple " + redName + yellowName );
			    
			    return redName + yellowName;
			}
		} );
		dir = symm .getDirection( "black" );
		addDirectionNaming( new BlackDirectionNaming( dir, redNames, yellowNames ) );
	}
}
