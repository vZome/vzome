
package com.vzome.core.model;

import java.util.Iterator;

import com.vzome.core.algebra.AlgebraicNumber;
import com.vzome.core.algebra.AlgebraicVector;
import com.vzome.core.math.RealVector;
import com.vzome.core.math.symmetry.Embedding;

public interface Panel extends Manifestation, Iterable<AlgebraicVector>
{
    public AlgebraicVector getZoneVector();

    public void setZoneVector( AlgebraicVector vector );

    public AlgebraicVector getFirstVertex();

    @Override
    public Iterator<AlgebraicVector> iterator();

    public int getVertexCount();

    public AlgebraicVector getNormal( );

    public RealVector getNormal( Embedding embedding );

    public AlgebraicNumber getQuadrea();
}
