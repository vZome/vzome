//(c) Copyright 2005, Scott Vorthmann.  All rights reserved.

package com.vzome.core.math.symmetry;

import com.vzome.core.algebra.AlgebraicField;

public interface CoxeterGroup
{
    int getOrder();
    
    AlgebraicField getField();

    int[] /*AlgebraicVector*/ groupAction( int[] /*AlgebraicVector*/ model, int element );

    int[] /*AlgebraicVector*/ getOrigin();

    int[] /*AlgebraicVector*/ getWeight( int i );
    
    int[] /*AlgebraicVector*/ getSimpleRoot( int i );

    int[] chiralSubgroupAction( int[] model, int i );
}