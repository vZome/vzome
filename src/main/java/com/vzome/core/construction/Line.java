

package com.vzome.core.construction;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.vzome.core.algebra.AlgebraicField;
import com.vzome.core.algebra.AlgebraicVector;
import com.vzome.core.algebra.Bivector3dHomogeneous;
import com.vzome.core.algebra.Vector3dHomogeneous;
import com.vzome.core.math.symmetry.Axis;
import com.vzome.core.math.symmetry.Symmetry;

/**
 * @author Scott Vorthmann
 */
public abstract class Line extends Construction
{
    // state variables
	private AlgebraicVector mDirection; 
    private AlgebraicVector mStart;
    
    // optimization
//    private boolean mAxisComputed = false;
//    private Axis mComputedAxis;
    
    protected Line( AlgebraicField field )
    {
        super( field );
    }
    
    /**
     * 
     * @param start
     * @param norm need not be normalized yet
     * @return
     */
    protected boolean setStateVariables( AlgebraicVector start, AlgebraicVector norm, boolean impossible )
    {
        // TODO here we normalize

        if ( impossible ) {
            // don't attempt to access other params
            if ( isImpossible() )
                return false;
            setImpossible( true );
            return true;
        }
        if ( norm .equals( mDirection )
        &&   start .equals( mStart )
        && ! isImpossible() )
            return false;
        
        // try to find some cleaner vector to use for the line direction, to reduce the likelihood
        //   of ill-conditioned intersection computations
        Symmetry[] symms = getField() .getSymmetries();
        for (Symmetry symm : symms) {
            Axis axis = symm.getAxis(norm);
            if ( axis != null )
            {
                if ( ! axis .getDirection() .isAutomatic() )
                    norm = axis .normal();
                break;
            }
        }

        mDirection = norm;
        mStart = start;
//        mAxisComputed = false;
//        mComputedAxis = null;
        setImpossible( false );
        return true;
    }
    
    public AlgebraicVector getStart()
    {
        return mStart;
    }
    
    /**
     * @return a "unit" vector... always normalized
     */
    public AlgebraicVector getDirection()
    {
        return mDirection;
    }
    
//    /**
//     * Compute the axis parallel to this line, if any.
//     * Subclasses should override this when they don't know it already.
//     * 
//     * @return a PLUS Axis, always, or null
//     */
//    public Axis getAxis()
//    {
//        if ( ! mAxisComputed ){
//            mComputedAxis = IcosahedralSymmetry .INSTANCE .getAxis( mUnitVector );
//            mAxisComputed = true;
//        }
//        return mComputedAxis;
//    }

    @Override
    public Element getXml( Document doc )
    {
        Element result = doc .createElement( "line" );
        return result;
    }

	public Bivector3dHomogeneous getHomogeneous()
	{
		Vector3dHomogeneous v1 = new Vector3dHomogeneous( mStart, this .getField() );
		Vector3dHomogeneous v2 = new Vector3dHomogeneous( mStart .plus( mDirection ), this .getField() );
		
		return v1 .outer( v2 );
	}
}
