package com.vzome.core.model;

import com.vzome.core.algebra.AlgebraicVector;

public interface Strut extends Manifestation, Comparable<Strut>
{
    public AlgebraicVector getZoneVector();
    
    public void setZoneVector( AlgebraicVector vector );
	
	@Override
	public int compareTo( Strut other );
	
	public AlgebraicVector getCanonicalLesserEnd();

	public AlgebraicVector getCanonicalGreaterEnd();

    public AlgebraicVector getEnd();
    
    public AlgebraicVector getOffset();
}
