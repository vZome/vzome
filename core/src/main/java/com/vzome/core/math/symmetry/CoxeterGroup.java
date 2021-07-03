package com.vzome.core.math.symmetry;

import com.vzome.core.algebra.AlgebraicField;
import com.vzome.core.algebra.AlgebraicVector;

public interface CoxeterGroup
{
    int getOrder();
    
    AlgebraicField getField();

    AlgebraicVector groupAction( AlgebraicVector model, int element );

    AlgebraicVector getOrigin();

    AlgebraicVector getWeight( int i );
    
    AlgebraicVector getSimpleRoot( int i );

    AlgebraicVector chiralSubgroupAction( AlgebraicVector model, int i );
}
