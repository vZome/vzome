package com.vzome.core.math.symmetry;


import java.util.Arrays;
import java.util.logging.Logger;

import org.w3c.dom.Element;

import com.vzome.core.algebra.AlgebraicField;
import com.vzome.core.algebra.RationalMatrices;
import com.vzome.core.math.DomUtils;


public class Axis
{
    private final Direction mDirection;
    
    private int orientation;  // how the prototype for this direction maps to this
    
    private int mSense;

	private final int mRotation;
    
    private Permutation mRotationPerm;

	private final int[] normal;   // not a unit vector
    
    private final int[][] dotNormal; // ready-to-use matrix to do v dot normal for any v
    
    private final int[][][] timesNormal = new int[3][][]; // multipliers for doing cross-product with normal
    
    private final int[][][] divNormal = new int[3][][]; // divisor for finding the length of a parallel vector
    
    static Logger logger = Logger.getLogger( "com.vzome.core.math" );

	Axis( Direction dir, int index, int sense, int rotation, Permutation rotPerm, int[] normal )
	{
		this.mDirection = dir;
		this .mRotation = rotation ;
        mRotationPerm = rotPerm;
		this.orientation = index;
		this.normal = normal;
		mSense = sense;
        
        AlgebraicField field = dir .getSymmetry() .getField();
        int order = field .getOrder();
        dotNormal = new int[ order ][ 3 * 2 * order ];
        for ( int i = 0; i < 3; i++ ) {
            field .createRepresentation( normal, i * order, dotNormal, 0, i * order );
            timesNormal[ i ] = new int[ order ][ 2 * order ];
            field .createRepresentation( normal, i * order, timesNormal[ i ], 0, 0 );
            try {
                divNormal[ i ] = RationalMatrices .invert( timesNormal[ i ] );
			} catch ( IllegalStateException e ) {
			    logger .warning( "Problem with direction = " + dir .toString() + ", normal = " + Arrays.toString( normal ) +
			                        ".  Unable to invert the matrix.  This Axis won't support getLength()." );
			}
        }
	}

    /**
     * Return the normal vector for this axis.
     * Note that this vector may not have length=1.0, but it will have length
     * equal to one "unit" for this axis.
     * @return GoldenVector
     */
    public int[] normal() { return normal; }
    
    public final int[] /*AlgebraicNumber*/ dotNormal( int[] /*AlgebraicVector*/ vector )
    {
        return mDirection .getSymmetry() .getField() .transform( dotNormal, vector );
    }
    
    public final boolean isParallel( int[] /*AlgebraicVector*/ vector )
    {
        AlgebraicField field = mDirection .getSymmetry() .getField();
        return field .isParallel( vector, normal, timesNormal );
    }
    
    public final int[] /*AlgebraicNumber*/ getLength( int[] /*AlgebraicVector*/ vector )
    {
        AlgebraicField field = mDirection .getSymmetry() .getField();
        return field .getLength( vector, normal, divNormal );
    }
    

    public int[] /*AlgebraicVector*/ scaleNormal( int[] length )
    {
        AlgebraicField field = mDirection .getSymmetry() .getField();
        return field .scaleNormal( length, normal, timesNormal );
    }


	public boolean equals( Object other )
	{
		if (other == null) {
			return false;
		}
		if (other == this) {
			return true;
		}
		try {
			Axis a = (Axis) other;
			return mDirection == a.mDirection && Arrays .equals( normal, a.normal );
		}
		catch (ClassCastException cce) {
			return false;
		}
	}

	public int hashCode()
	{
		return mDirection .hashCode();
	}
	
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
