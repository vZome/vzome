package com.vzome.core.math.symmetry;

import com.vzome.core.algebra.AlgebraicNumber;

public interface Symmetries4D
{
    void constructPolytope( String groupName, int index, int edgesToRender, AlgebraicNumber[] edgeScales,
            WythoffConstruction.Listener listener );

    QuaternionicSymmetry getQuaternionSymmetry( String name );
}