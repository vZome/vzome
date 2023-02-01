package com.vzome.core.math.symmetry;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonValue;

public class Permutation {

	/**
	    * Since the MINUS side always mirrors the PLUS side,
	    *  the map only needs to indicate one.
	    */
	private int[] m_map;
	
	private int m_order = 1;
	
	private final Symmetry mSymmetryGroup;
	
	public Permutation( Symmetry group, int[] map )
	{
		mSymmetryGroup = group;
		if ( map == null ) {
			int numUnits = group .getChiralOrder();
			map = new int[numUnits];
			for (int unit = 0; unit < numUnits; unit++)
			    map[unit] = unit;
		}
		m_map = map;
		int unit = 0;
		for ( int i = 0; i < m_map.length; i++ ) {
		    unit = m_map[ unit ];
		    if ( unit == 0 ) {
		        m_order = i+1;
		        break;
		    }
		}
	}

	@JsonValue
    public int[] getJsonValue()
    {
    	return this .m_map;
    }

    @Override
	public String toString()
	{
	    return "permutation #"  + mapIndex( 0 );
	}

    @JsonIgnore
    public int getOrder()
	{
		return m_order;
	}


	/** Composition, where p1.compose( p2 ) .permute(axis)  == p1.permute( p2.permute( axis ) ) */
	public Permutation compose( Permutation other )
	{
	    return mSymmetryGroup .getPermutation( m_map[ other .m_map[ 0 ] ] );
	}

	public Permutation inverse()
	{
    	for ( int i = 0; i < m_map .length; i++ )
    		if ( mapIndex( i ) == 0 )
    			return mSymmetryGroup .getPermutation( i );
        return null;
    }

	public Permutation power(int power)
	{
		if (power == 0)
			return mSymmetryGroup .getPermutation( 0 );
		
		Permutation base = this;
		if (power < 0) {
			base = this.inverse();
			power *= -1;
		}
		if (power == 1)
			return base;
		
		return base.compose(base.power(power - 1));
	}


    public int mapIndex( int i )
    {
    	    if ( ( i < 0 ) || ( i >= m_map.length ) )
    		    return Symmetry .NO_ROTATION;
        return m_map[ i ];
    }


    public Axis permute( Axis axis, int sense )
    {
        int orn = axis .getOrientation();
        orn = mapIndex( orn );
        return axis .getDirection() .getAxis( ( sense + axis .getSense() ) % 2, orn );
    }


}
