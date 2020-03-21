package com.vzome.core.construction;

import com.vzome.core.algebra.AlgebraicMatrix;
import com.vzome.core.algebra.AlgebraicVector;

public class MoveAndRotate extends Transformation
{
    private final MatrixTransformation rotation;
    private final Translation translation;

    public MoveAndRotate( AlgebraicMatrix rotation, AlgebraicVector start, AlgebraicVector end )
    {
        super( start .getField() );
        this .rotation = new MatrixTransformation( rotation, start );
        this .translation = new Translation( end .minus( start ) );
    }

    @Override
    protected boolean mapParamsToState()
    {
        return this .rotation .mapParamsToState() && this .translation .mapParamsToState();
    }

    @Override
    public AlgebraicVector transform( AlgebraicVector arg )
    {
        return this .translation .transform( this .rotation .transform( arg ) );
    }
}
