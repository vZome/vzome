
//(c) Copyright 2005, Scott Vorthmann.  All rights reserved.

package com.vzome.core.math.symmetry;

import java.util.Iterator;
import java.util.Set;

import com.vzome.core.algebra.AlgebraicField;
import com.vzome.core.algebra.AlgebraicMatrix;
import com.vzome.core.algebra.AlgebraicVector;
import com.vzome.core.math.RealVector;

/**
 * @author Scott Vorthmann
 *
 */
public interface Symmetry
{
	public enum SpecialOrbit {
	    BLUE,
	    RED,
	    YELLOW,
	    BLACK
    }

    int PLUS = 0, MINUS = 1, NO_SENSE = 2;
	
	int NO_ROTATION = -1;
    
    String TETRAHEDRAL = "tetrahedral", PYRITOHEDRAL = "pyritohedral";
    
    int getChiralOrder();
    
    String getName();

    Axis getAxis( AlgebraicVector vector );
    
    Axis getAxis( AlgebraicVector vector, OrbitSet orbits );

    Axis getAxis( RealVector vector, Set<Direction> filter );
    
    int getMapping( int from, int to );

    Permutation getPermutation( int i );

    AlgebraicMatrix getMatrix( int i );

	int inverse(int orientation);
	
    String[] getDirectionNames();

    Iterator<Direction> getDirections();
    
	Direction getDirection( String name );
    
    AlgebraicField getField();

    OrbitSet getOrbitSet();

    /**
     * Generate a subgroup, by taking the closure of some collection of Permutations
     * @param perms an array of Permutations indices
     * @return an array of Permutations indices
     */
    int[] closure( int[] perms );
    
    int[] subgroup( String name );
    
    Direction createNewZoneOrbit( String name, int prototype, int rotatedPrototype, AlgebraicVector vector );

    String getDefaultStyle();

    public abstract int[] getIncidentOrientations( int orientation );

    public abstract Direction getSpecialOrbit( SpecialOrbit which );
}
