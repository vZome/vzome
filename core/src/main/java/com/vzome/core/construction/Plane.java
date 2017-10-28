

package com.vzome.core.construction;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.vzome.core.algebra.AlgebraicField;
import com.vzome.core.algebra.AlgebraicVector;
import com.vzome.core.algebra.Trivector3dHomogeneous;

/**
 * @author Scott Vorthmann
 */
public abstract class Plane extends Construction
{
    private AlgebraicVector mBase;
    private AlgebraicVector mNormal;
    
    protected Plane( AlgebraicField field )
    {
        super( field );
    }

    protected final boolean setStateVariables( AlgebraicVector base, AlgebraicVector normal, boolean impossible )
    {
        if ( impossible ) {
            // don't attempt to access other params
            if ( isImpossible() )
                return false;
            setImpossible( true );
            return true;
        }
        if ( normal .equals( mNormal )
        && ! isImpossible()
        &&  base .equals( mBase ) )
            return false;
        
        // symm axis is used for the normal, and might be a 4D vector
        normal = normal .projectTo3d( true );

        // try to find some cleaner vector to use for the line direction, to reduce the likelihood
        //   of ill-conditioned intersection computations
//        Symmetry[] symms = getField() .getSymmetryPerspectives();
//        for (Symmetry symm : symms) {
//            Axis axis = symm.getAxis(normal);
//            if ( axis != null )
//            {
//                if ( ! axis .getDirection() .isAutomatic() )
//                    normal = axis .normal();
//                break;
//            }
//        }

        mNormal = normal;
        // symm center is used for the base, and might be a 4D vector
        mBase = base .projectTo3d( true );
        setImpossible( false );
        return true;
    }
    
    public AlgebraicVector getBase()
    {
        return mBase;
    }
    
    public AlgebraicVector getNormal()
    {
        return mNormal;
    }


    @Override
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
