package com.vzome.core.math.symmetry;

import com.vzome.core.algebra.AlgebraicVector;
import com.vzome.core.math.RealVector;

public interface Embedding
{
	RealVector embedInR3( AlgebraicVector v );
}
