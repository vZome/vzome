

package com.vzome.core.construction;

import java.util.Arrays;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.vzome.core.algebra.AlgebraicField;
import com.vzome.core.algebra.Trivector3dHomogeneous;
import com.vzome.core.math.symmetry.Axis;
import com.vzome.core.math.symmetry.Symmetry;

/**
 * @author Scott Vorthmann
 */
public abstract class Plane extends Construction
{
    private int[] /*AlgebraicVector*/ mBase;
    private int[] /*AlgebraicVector*/ mNormal;
    
    protected Plane( AlgebraicField field )
    {
        super( field );
    }

    public void accept( Visitor v )
    {
        v .visitPlane( this );
    }


    protected final boolean setStateVariables( int[] /*AlgebraicVector*/ base, int[] /*AlgebraicVector*/ normal, boolean impossible )
    {
        if ( impossible ) {
            // don't attempt to access other params
            if ( isImpossible() )
                return false;
            setImpossible( true );
            return true;
        }
        if ( Arrays .equals( normal, mNormal )
        && ! isImpossible()
        &&  Arrays .equals( base, mBase ) )
            return false;
        
        // symm axis is used for the normal, and might be a 4D vector
        normal = getField() .projectTo3d( normal, true );

        // try to find some cleaner vector to use for the line direction, to reduce the likelihood
        //   of ill-conditioned intersection computations
        Symmetry[] symms = getField() .getSymmetries();
        for ( int i = 0; i < symms.length; i++ ) {
            Axis axis = symms[ i ] .getAxis( normal );  // TODO: this is BAD!  creating auto directions!
            if ( axis != null )
            {
                if ( ! axis .getDirection() .isAutomatic() )
                    normal = axis .normal();
                break;
            }
        }

        mNormal = normal;
        mBase = base;
        setImpossible( false );
        return true;
    }
    
    public int[] /*AlgebraicVector*/ getBase()
    {
        return mBase;
    }
    
    public int[] /*AlgebraicVector*/ getNormal()
    {
        return mNormal;
    }


    public Element getXml( Document doc )
    {
        Element result = doc .createElement( "plane" );
        return result;
    }

	public Trivector3dHomogeneous getHomogeneous()
	{
		return null;
	}
}
