

package com.vzome.core.construction;


import com.vzome.core.algebra.AlgebraicMatrix;
import com.vzome.core.algebra.AlgebraicVector;
import com.vzome.core.math.symmetry.Symmetry;

/**
 * @author Scott Vorthmann
 */
public class SymmetryTransformation extends Transformation
{
    // parameters
    private Point mCenter;
    
    // attributes
    private Symmetry mSymmetry;
    private int mOrientation;

    /**
     * @param prototype
     */
    public SymmetryTransformation( Symmetry symm, int orientation, Point center )
    {
        super( center .field );
        mSymmetry = symm;
        mOrientation = orientation;
        mCenter = center;
        mapParamsToState();
    }

    public void attach()
    {
        mCenter .addDerivative( this );
    }
    
    public void detach()
    {
        mCenter .removeDerivative( this );
    }
    
    protected boolean mapParamsToState()
    {
        if ( mCenter .isImpossible() )
            return setStateVariables( null, null, true );
        
        AlgebraicVector loc = mCenter .getLocation() .projectTo3d( true );
        AlgebraicMatrix matrix = mSymmetry .getMatrix( mOrientation );
        return setStateVariables( matrix, loc, false );
    }
    

    public void accept( Visitor v )
    {
        v .visitIcosahedralRotation( this );
    }

    public void setOrientation( int orientation )
    {
        mOrientation = orientation;
        paramOrAttrChanged();
    }

}
