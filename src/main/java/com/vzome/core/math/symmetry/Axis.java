package com.vzome.core.math.symmetry;


import org.w3c.dom.Element;

import com.vzome.core.algebra.AlgebraicNumber;
import com.vzome.core.algebra.AlgebraicVector;
import com.vzome.core.math.DomUtils;


/**
 * Should be called Zone, an infinite family of parallel lines, one member of an orbit (Direction)
 *   of a Symmetry group.
 */
public class Axis
{
	private final Direction mDirection;  // the containing orbit
    
    private int orientation;  // The permutation that maps the orbit (Direction) prototype zone to this zone
    
    public static final int PLUS = 0, MINUS = 1;  // Every reflection group has two rotation subgroups,
    											  //  corresponding to "white" / "right-handed" right triangles or
    											  //  "black" / "left-handed" right triangles.  The convention may be
    											  //  different for different Symmetry groups, in terms of color, shape, and sense.

    private int mSense; // Either PLUS or MINUS, depending on which rotation subgroup this orbit is part of.
    
    private Permutation mRotationPerm; // the permutation that is a rotation around this axis, or null
	private final int   mRotation;     // the index of mRotationPerm, or NO_ROTATION

	private final AlgebraicVector normal;   // The vector to which all lines in this zone are parallel; not a unit vector,
											//   but has the canonical unit length for this orbit (Direction)
        
	Axis( Direction dir, int index, int sense, int rotation, Permutation rotPerm, AlgebraicVector normal )
	{
		this.mDirection = dir;
		this .mRotation = rotation;
        mRotationPerm = rotPerm;
		this.orientation = index;
		this.normal = normal;
		mSense = sense;
	}

    /**
     * Return the normal vector for this axis.
     * Note that this vector may not have length=1.0, but it will have length
     * equal to one "unit" for this axis.
     * @return AlgebraicVector
     */
    public AlgebraicVector normal() { return normal; }
            
    public final AlgebraicNumber getLength( AlgebraicVector vector )
    {
        return vector .getLength( normal );
    }

    @Override
	public int hashCode()
    {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((mDirection == null) ? 0 : mDirection.hashCode());
		result = prime * result + mSense;
		result = prime * result + ((normal == null) ? 0 : normal.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj)
	{
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Axis other = (Axis) obj;
		if (mDirection == null) {
			if (other.mDirection != null)
				return false;
		} else if (!mDirection.equals(other.mDirection))
			return false;
		if (mSense != other.mSense)
			return false;
		if (normal == null) {
			if (other.normal != null)
				return false;
		} else if (!normal.equals(other.normal))
			return false;
		return true;
	}

    @Override
	public String toString()
	{
		return mDirection .toString() + " " + orientation;
	}
	
	public Direction getOrbit()
	{
	    return mDirection;
	}

    public Direction getDirection()
    {
        return mDirection;
    }
    
    public int getOrientation()
    {
        return orientation;
    }
    
    public int getRotation()
    {
        return mRotation;
    }

    public int getCorrectRotation()
    {
        return (mRotationPerm == null)? Symmetry.NO_ROTATION : mRotationPerm .mapIndex( 0 );
    }

    public Permutation getRotationPermutation()
    {
        return mRotationPerm;
    }

    public int getSense()
    {
    	return mSense;
    }

    /**
     * @param plus
     * @param orientation2
     */
    public void rename( int sense, int orientation2 )
    {
        mSense = sense;
        orientation = orientation2;
    }


    public void getXML( Element elem )
    {
        DomUtils .addAttribute( elem, "symm", mDirection .getSymmetry() .getName() );
        DomUtils .addAttribute( elem, "dir", mDirection .getName() );
        DomUtils .addAttribute( elem, "index", Integer .toString( orientation ) );
        if ( mSense != Symmetry.PLUS )
        	DomUtils .addAttribute( elem, "sense", "minus" );
    }
}
