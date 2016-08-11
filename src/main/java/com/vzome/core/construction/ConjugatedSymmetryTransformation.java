package com.vzome.core.construction;

import com.vzome.core.algebra.AlgebraicMatrix;
import com.vzome.core.math.symmetry.Symmetry;

public class ConjugatedSymmetryTransformation extends SymmetryTransformation
{
	public ConjugatedSymmetryTransformation( Symmetry symm, int orientation, Point center, AlgebraicMatrix pre, AlgebraicMatrix post )
	{
		super( symm, orientation, center );
		
        AlgebraicMatrix matrix = mSymmetry .getMatrix( mOrientation );
        matrix = post .times( matrix .times( pre ) );  // this is the whole reason for this subclass
        setStateVariables( matrix, this .mOffset, false );
    }
}
