/*
 * Created on Aug 8, 2004
 */

package com.vzome.core.render;

import com.vzome.core.algebra.AlgebraicNumber;
import com.vzome.core.math.symmetry.Axis;
import com.vzome.core.math.symmetry.Direction;
import com.vzome.core.math.symmetry.Permutation;
import com.vzome.core.math.symmetry.Symmetry;

/**
 * @author vorth
 */
public abstract class AbstractZomicEventHandler implements ZomicEventHandler
{
    protected final Symmetry mSymmetry;
    
    protected Permutation     mOrientation;
    
    protected int mHandedNess = Symmetry .PLUS;

    protected AlgebraicNumber mScale; // one

    protected int         	   mAction		= BUILD;
    
    public AbstractZomicEventHandler( Symmetry symm )
    {
        mSymmetry = symm;
        mScale = symm .getField() .createPower( 0 );
        mOrientation = mSymmetry .getPermutation( 0 );
    }

    protected Permutation getPermutation()
    {
        return mOrientation;
    }
    
    public Direction getDirection( String name )
    {
        return mSymmetry .getDirection( name );
    }

	public void permute( Permutation permutation, int sense )
	{
	    mOrientation = permutation .compose( mOrientation );
	    mHandedNess = ( mHandedNess + sense ) % 2;
	}
	
    public void rotate( Axis axis, int steps )
    {
        axis = mOrientation .permute( axis, mHandedNess );
        if ( axis .getSense() == mHandedNess )
            steps *= - 1;
        permute( axis .getRotationPermutation() .power( steps ), Symmetry .PLUS );
	}

    public void reflect( Axis blueAxis )
    {
    	if ( blueAxis == null )
			permute( mSymmetry .getPermutation( 0 ), Symmetry .MINUS );
    	else {
    	    blueAxis = mOrientation .permute( blueAxis, mHandedNess );
    		permute( blueAxis .getRotationPermutation(), Symmetry .MINUS );
    	}
	}

    public void scale( AlgebraicNumber scale )
    {
        mScale = mScale .times( scale );
    }

    public void action( int action )
    {
        mAction = action;
    }
    
    protected abstract AbstractZomicEventHandler copyLocation();
    
    protected abstract void restoreLocation( AbstractZomicEventHandler changed );

    public ZomicEventHandler save( int variables )
    {
        AbstractZomicEventHandler newVM = copyLocation();
        newVM .mAction = mAction;
        newVM .mOrientation = mOrientation;
        newVM .mHandedNess = mHandedNess;
        newVM .mScale = mScale;
        return newVM;
    }

    public void restore( ZomicEventHandler changes, int variables )
    {
        AbstractZomicEventHandler changedVM = (AbstractZomicEventHandler) changes;
        if ( ( LOCATION & variables ) == 0 )
            restoreLocation( changedVM );
        if ( ( SCALE & variables ) == 0 )
            mScale = changedVM .mScale;
        if ( ( ORIENTATION & variables ) == 0 ) {
            mOrientation = changedVM .mOrientation;
            mHandedNess = changedVM .mHandedNess;
        }
        if ( ( ACTION & variables ) == 0 )
            mAction = changedVM .mAction;
    }
}
