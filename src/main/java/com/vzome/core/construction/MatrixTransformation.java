package com.vzome.core.construction;

import com.vzome.core.algebra.AlgebraicMatrix;
import com.vzome.core.algebra.AlgebraicVector;

public class MatrixTransformation extends Transformation
{
	private final AlgebraicMatrix matrix;
	private final AlgebraicVector center;

	public MatrixTransformation( AlgebraicMatrix matrix, AlgebraicVector center )
	{
		super( center .getField() );
		this .matrix = matrix;
		this .center = center;
		
    }

	@Override
	protected boolean mapParamsToState()
	{
        setStateVariables( matrix, this .center, false );
        return true;
	}
}
