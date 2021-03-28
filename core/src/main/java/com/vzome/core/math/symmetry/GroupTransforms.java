package com.vzome.core.math.symmetry;

import java.lang.NullPointerException;
import java.lang.IllegalArgumentException;

import com.vzome.core.algebra.AlgebraicField;
import com.vzome.core.algebra.AlgebraicNumber;
import com.vzome.core.algebra.AlgebraicVector;
import com.vzome.core.algebra.AlgebraicMatrix;

// Stores transformation matrices for every element of a Coxeter group

public class GroupTransforms
{

    public AlgebraicField field;
    public int dim; // Number of dimensions in which the group operates e.g. E8 -> 8
    public int order; // Order of the group e.g. H4 -> 14400
    public AlgebraicMatrix[] transforms;
    public boolean[] parities; // parities[i] stores whether transforms[i] is a reflection
    public AlgebraicMatrix[] generators; // Matrices which generate the Coxeter group
    
    public GroupTransforms ( AlgebraicMatrix simplexVerts, int order )
	throws NullPointerException, IllegalArgumentException
    {

	if (simplexVerts == null)
	    throw new NullPointerException("simplexVerts must be non-null");
	if (!simplexVerts.isSquare())
	    throw new IllegalArgumentException("simplexVerts must be square");
	if (simplexVerts.determinant().isZero())
	    throw new IllegalArgumentException("simplexVerts must be invertible");

	this .field = simplexVerts .getMatrix()[ 0 ][ 0 ] .getField();
	this .dim = simplexVerts .getMatrix() .length;
	this .order = order;
	this .transforms = new AlgebraicMatrix[order];
	this .parities = new boolean[order];
	this .generators = new AlgebraicMatrix[dim];

	// Find the generator matrices: reflections about the facets of the simplex
	// whose coordinates (besides the origin) are given in simplexVerts
	for ( int i = 0; i < this. dim; i++ ) {

	    // Find the vector perpendicular to all columns of simplexVerts except column i,
	    // i.e. perpendicular to the facet of the simplex opposite vertex i
	    // In 3D this is the cross product,
	    // and in ND it is the dual of the wedge product
	    AlgebraicVector normal = new AlgebraicVector(this .field, this .dim);

	    for ( int j = 0; j < this. dim; j++ ) {

		// Create a minor excluding column i and row j
		// (but with the sign change built in since we use modulo)
		AlgebraicMatrix minor = new AlgebraicMatrix(this .field, this .dim - 1);
		for ( int k = 0; k < this. dim - 1; k++ ) {
		    for ( int l = 0; l < this. dim - 1; l++ ) {
			AlgebraicNumber entry = simplexVerts.getElement((j+1+k)%dim, (i+1+l)%dim);
			minor.setElement(k, l, entry);
		    }
		}

		// The determinant of the minor is a component of the normal,
		// since dotting the final vector with any one of the columns used to create it
		// (i.e. any of them except column i)
		// can be made into the cofactor expansion of a matrix with two identical columns,
		// whose determinant is trivially 0
		normal.setComponent(j, minor.determinant());

	    }

	    // Find the matrix which reflects about the normal, whose formula is
	    // I - 2 a a^T (a dot a)^-1
	    // where I = identity matrix, a = normal, a^T = a transpose

	    // Calculate (a dot a)^-1 once
	    AlgebraicNumber normalMagInvSq = normal.dot(normal).reciprocal();

	    for ( int m = 0; m < this .dim; m++ ) {

		// Used with a_n to calculate elements of a a^T
		AlgebraicNumber a_m = normal.getComponent(m);

		for ( int n = 0; n < this .dim; n++ ) {

		    AlgebraicNumber a_n = normal.getComponent(n);

		    // Appropriate element of I
		    AlgebraicNumber I_mn = (m == n) ? field .one() : field .zero();

		    // Appropriate element of a a^T (a dot a)^-1,
		    // representing the perpendicular component of the vector being reflected
		    // (subtracted twice to obtain the reflection)
		    AlgebraicNumber perp = a_m.times(a_n).times(normalMagInvSq);
		    
		    generators[i].setElement(m, n, I_mn.minus(perp.plus(perp))); 
		}

	    }
	    
	}
	
	int numTransforms = 0; // Current "length" of transforms, should equal order when done
	int thisLayer = 0; // Start of the current layer being reflected to create the next one
	int lastLayer = 0; // Start of the previous layer being compared to the next one
	while ( numTransforms < this. order ) {
	    // TODO: Find all other matrices
	}
	
    }
    
}
