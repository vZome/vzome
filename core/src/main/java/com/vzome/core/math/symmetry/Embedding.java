package com.vzome.core.math.symmetry;

import com.vzome.core.algebra.AlgebraicVector;
import com.vzome.core.math.RealVector;

public interface Embedding
{
	RealVector embedInR3( AlgebraicVector v );
	
	boolean isTrivial();
	
	public class Trivial implements Embedding
	{
        @Override
        public RealVector embedInR3( AlgebraicVector v )
        {
            return v .toRealVector();
        }

        @Override
        public boolean isTrivial()
        {
            return true;
        }
	}
}
