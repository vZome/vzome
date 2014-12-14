

package com.vzome.core.construction;

public class PointToPointTranslation extends Transformation
{
    private Point p1, p2;

    public PointToPointTranslation( Point p1, Point p2 )
    {
        super( p1 .field );
        mOffset = field .projectTo3d( field .subtract( p2 .getLocation(), p1 .getLocation() ), true );
        this.p1 = p1;
        this.p2 = p2;        
    }

    public int[] /*AlgebraicVector*/ transform( int[] /*AlgebraicVector*/ arg )
    {
        arg = field .add( arg, mOffset );
        return arg;
    }

    public void attach()
    {
        p1 .addDerivative( this );
        p2 .addDerivative( this );
    }
    
    public void detach()
    {
        p1 .removeDerivative( this );
        p2 .removeDerivative( this );
    }

    protected boolean mapParamsToState()
    {
//        AlgebraicField factory = (AlgebraicField) mOffset .getFactory();
        return setStateVariables( null, null, /*factory .identity(), factory .origin(),*/ false );
    }

    public void accept( Visitor v )
    {
//        v .visitTranslation( this );
    }
}
