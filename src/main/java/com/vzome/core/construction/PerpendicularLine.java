

package com.vzome.core.construction;


/**
 * @author Scott Vorthmann
 */
public class PerpendicularLine extends Line
{
    private Line mLine1, mLine2;
    private Point mPoint;

    /**
     * @param step
     * @param start
     * @param end
     */
    public PerpendicularLine( Line l1, Line l2, Point p )
    {
        super( l1 .field );
        mLine1 = l1;
        mLine2 = l2;
        mPoint = p;
        mapParamsToState();
    }

    public void attach()
    {
        mLine1 .addDerivative( this );
        mLine2 .addDerivative( this );
        mPoint .addDerivative( this );
    }
    
    public void detach()
    {
        mLine1 .removeDerivative( this );
        mLine2 .removeDerivative( this );
        mPoint .removeDerivative( this );
    }
    
    /**
     * returns true if something changed.
     * @return
     */
    protected boolean mapParamsToState()
    {
        if ( mLine1 .isImpossible() || mLine2 .isImpossible() || mPoint .isImpossible() )
            return setStateVariables( null, null, true );
        int[] /*AlgebraicVector*/ norm1 = mLine1 .getDirection();
        int[] /*AlgebraicVector*/ norm2 = mLine2 .getDirection();
        int[] /*AlgebraicVector*/ cross = field .cross( norm1, norm2 );
        return setStateVariables( mPoint .getLocation(), cross, field .isOrigin( cross ) );
    }
    
    public void accept( Visitor v )
    {
        v .visitPerpendicularLine( this );
    }

}
