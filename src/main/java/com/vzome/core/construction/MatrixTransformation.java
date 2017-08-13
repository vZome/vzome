package com.vzome.core.construction;

import com.vzome.core.algebra.AlgebraicMatrix;
import com.vzome.core.algebra.AlgebraicVector;

public class MatrixTransformation extends Transformation
{
	public MatrixTransformation( AlgebraicMatrix matrix, AlgebraicVector center )
	{
		super( center .getField() );
        setStateVariables( matrix, center, false );
    }

	@Override
	protected boolean mapParamsToState()
	{
        return true;
	}
}
