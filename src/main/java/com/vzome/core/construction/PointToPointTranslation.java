

package com.vzome.core.construction;

import com.vzome.core.algebra.AlgebraicVector;

public class PointToPointTranslation extends Transformation
{
    public PointToPointTranslation( Point p1, Point p2 )
    {
        super( p1 .field );
        mOffset = field .projectTo3d( p2 .getLocation() .minus( p1 .getLocation() ), true );
    }

    public AlgebraicVector transform( AlgebraicVector arg )
    {
        return arg .plus( mOffset );
    }

    protected boolean mapParamsToState()
    {
//        AlgebraicField factory = (AlgebraicField) mOffset .getFactory();
        return setStateVariables( null, null, /*factory .identity(), factory .origin(),*/ false );
    }
}
