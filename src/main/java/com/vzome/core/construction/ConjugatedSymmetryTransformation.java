package com.vzome.core.construction;

import com.vzome.core.algebra.AlgebraicMatrix;
import com.vzome.core.math.symmetry.Symmetry;

public class ConjugatedSymmetryTransformation extends SymmetryTransformation
{

	private AlgebraicMatrix post;
	private AlgebraicMatrix pre;

	public ConjugatedSymmetryTransformation( Symmetry symm, int orientation, Point center, AlgebraicMatrix pre, AlgebraicMatrix post )
	{
		super( symm, orientation, center );
		this .pre = pre;
		this .post = post;
        mapParamsToState();
    }
    
    @Override
    protected boolean mapParamsToState()
    {
        if ( ! super .mapParamsToState() )
            return setStateVariables( null, null, true );
        
        AlgebraicMatrix matrix = mSymmetry .getMatrix( mOrientation );
        matrix = this .post .times( matrix .times( this .pre ) );  // this is the whole reason for this subclass
        return setStateVariables( matrix, this .mOffset, false );
    }
}
