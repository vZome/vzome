package com.vzome.core.math.symmetry;

import com.vzome.core.algebra.AlgebraicVector;
import com.vzome.core.math.RealVector;

public interface Embedding
{
	RealVector embedInR3( AlgebraicVector v );
	
	double[] embedInR3Double( AlgebraicVector v );
	
	boolean isTrivial();
	
	public class Trivial implements Embedding
	{
        @Override
        public RealVector embedInR3( AlgebraicVector v )
        {
            return v .toRealVector();
        }

        // An array of 3 doubles is used when high precision (double) vector values are needed
        // without the need to manipulate them, thus no operators as in the RealVector class 
        @Override
        public double[] embedInR3Double( AlgebraicVector v )
        {
            return v .to3dDoubleVector();
        }

        @Override
        public boolean isTrivial()
        {
            return true;
        }
	}
}
