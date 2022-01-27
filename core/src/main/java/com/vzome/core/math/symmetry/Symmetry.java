
package com.vzome.core.math.symmetry;

import java.util.Collection;

import com.vzome.core.algebra.AlgebraicField;
import com.vzome.core.algebra.AlgebraicMatrix;
import com.vzome.core.algebra.AlgebraicVector;
import com.vzome.core.math.RealVector;

// Should be unnecessary, but JSweet fails under GitHub Actions (but not locally,
//   using the exact same Eclipse Temurin OpenJDK 8!)
import com.vzome.core.math.symmetry.Axis;

/**
 * @author Scott Vorthmann
 *
 */
public interface Symmetry extends Iterable<Direction>, Embedding
{
    int NO_ROTATION = -1;
    
    String TETRAHEDRAL = "tetrahedral", PYRITOHEDRAL = "pyritohedral";
    
    int getChiralOrder();
    
    String getName();

    Axis getAxis( AlgebraicVector vector );
    
    Axis getAxis( AlgebraicVector vector, OrbitSet orbits );

    Axis getAxis( RealVector vector, Collection<Direction> filter );
    
    int PLUS = Axis.PLUS, MINUS = Axis.MINUS;
	
    int getMapping( int from, int to );

    Permutation getPermutation( int i );

    AlgebraicMatrix getMatrix( int i );

	int inverse(int orientation);
	
    String[] getDirectionNames();
    
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

    public abstract int[] getIncidentOrientations( int orientation );

    public abstract Direction getSpecialOrbit( SpecialOrbit which );

	Axis getPreferredAxis();

	/**
	 * Get the transformation matrix that maps zone 7 to zone -7 (for example).
	 * If null, the matrix is implicitly a central inversion, negating vectors.
	 * @return {@link AlgebraicMatrix}
	 */
	public abstract AlgebraicMatrix getPrincipalReflection();
	
	public AlgebraicVector[] getOrbitTriangle();
	
	/**
	 * Compute the orbit triangle dots for all existing orbits,
	 * and leave behind an OrbitDotLocator for new ones.
	 * The result is just a VEF string, for debugging.
	 * @return
	 */
	public String computeOrbitDots();

    public boolean reverseOrbitTriangle();
}
