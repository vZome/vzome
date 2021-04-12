package com.vzome.core.math.symmetry;

import java.lang.NullPointerException;
import java.lang.IllegalArgumentException;

import com.vzome.core.algebra.AlgebraicField;
import com.vzome.core.algebra.AlgebraicNumber;
import com.vzome.core.algebra.AlgebraicVector;
import com.vzome.core.algebra.AlgebraicMatrix;

// Finds and stores transformation matrices for every element of a Coxeter group,
// as well as their parities (rotation/reflection) and adjacencies (related by a generator)

/**
 * @author Aresh Pourkavoos
 */

public class GroupTransforms
{

    public AlgebraicField field;
    public int dim; // Number of dimensions in which the group operates e.g. E8 -> 8
    public int order; // Order of the group e.g. H4 -> 14400
    public AlgebraicMatrix simplexVerts;
    public AlgebraicMatrix[] transforms;
    public boolean[] parities; // parities[i] stores whether transforms[i] is a reflection
    public int[][] adjacencies; // adjacencies[i][j] stores the index of the j-th reflection of transforms[i]
    public AlgebraicMatrix[] generators; // Matrices which generate the Coxeter group
    public boolean[][] commutes; // commutes[i][j] stores whether generators i and j commute
    
    public GroupTransforms ( AlgebraicMatrix simplexVerts, int order )
	throws NullPointerException, IllegalArgumentException
    {

	if ( simplexVerts == null )
	    throw new NullPointerException("simplexVerts must be non-null");
	if ( !simplexVerts .isSquare() )
	    throw new IllegalArgumentException("simplexVerts must be square");
	if ( simplexVerts .determinant() .isZero() )
	    throw new IllegalArgumentException("simplexVerts must be invertible");

	this .field = simplexVerts .getMatrix()[ 0 ][ 0 ] .getField();
	this .dim = simplexVerts .getMatrix() .length;
	this .order = order;

	if (this .dim != 3 && this .dim != 4)
	    throw new IllegalArgumentException("At the moment, only 3D and 4D are supported.");

	this .simplexVerts = simplexVerts;
	this .transforms = new AlgebraicMatrix[ order ];
	this .parities = new boolean[ order ];
	this .generators = new AlgebraicMatrix[ dim ];

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
			minor .setElement(k, l, entry);
		    }
		}

		// The determinant of the minor is a component of the normal,
		// since dotting the final vector with any one of the columns used to create it
		// (i.e. any of them except column i)
		// can be made into the cofactor expansion of a matrix with two identical columns,
		// whose determinant is trivially 0
		normal .setComponent(j, minor.determinant());

	    }

	    // Find the matrix which reflects about the normal, whose formula is
	    // I - 2 a a^T (a dot a)^-1
	    // where I = identity matrix, a = normal, a^T = a transpose

	    // Calculate (a dot a)^-1 once
	    AlgebraicNumber normalMagInvSq = normal .dot(normal) .reciprocal();

	    for ( int m = 0; m < this .dim; m++ ) {

		// Used with a_n to calculate elements of a a^T
		AlgebraicNumber a_m = normal .getComponent(m);

		for ( int n = 0; n < this .dim; n++ ) {
		    
		    AlgebraicNumber a_n = normal .getComponent(n);

		    // Appropriate element of I
		    AlgebraicNumber I_mn = (m == n) ? field .one() : field .zero();

		    // Appropriate element of a a^T (a dot a)^-1,
		    // representing the perpendicular component of the vector being reflected
		    // (subtracted twice to obtain the reflection)
		    AlgebraicNumber perp = a_m .times(a_n) .times(normalMagInvSq);
		    
		    this .generators[ i ] .setElement(m, n, I_mn .minus(perp) .minus(perp));
		    
		}
		
	    }
	    
	}

	// Generate all transformation matrices from the reflections using breadth-first search
	// The first transformation is the identity, with even parity
	this .transforms[ 0 ] = new AlgebraicMatrix(this .field, dim);
	this .parities[ 0 ] = false;
	
	int lastLayer = 0; // Start of the previous layer being compared against the next one
	int thisLayer = 0; // Start of the current layer being reflected
	int nextLayer = 1; // Start of the next layer being created
	int numTransforms = 1; // Current "length" of transforms, should equal order when done
	boolean nextParity = true; // Parity of the next layer being created
	
	while ( true ) {

	    // If this remains true after a layer is done, all transforms have been found
	    boolean nextLayerEmpty = true; 

	    for ( int thisTransform = thisLayer; thisTransform < nextLayer; thisTransform++ ) {

		for ( int i = 0; i < this .dim ; i++ ) {

		    AlgebraicMatrix newTransform =
			this .transforms[ thisTransform ]
			.times(this .generators[ i ]);

		    // Check if the "new" transform is actually new:
		    boolean newTransformNew = true;

		    // compare it against the previous layer with the same parity...
		    for ( int j = lastLayer; j < thisLayer; j++ ) {
			if ( newTransform .equals(this .transforms[ j ]) ) {
			    newTransformNew = false;
			    break;
			}
		    }

		    // ...and against the others already generated in its own layer
		    for ( int j = nextLayer; j < numTransforms; j++ ) {
			if ( newTransform .equals(this .transforms[ j ]) ) {
			    this .adjacencies[ thisTransform ][ i ] = j;
			    this .adjacencies[ j ][ i ] = thisTransform;
			    newTransformNew = false;
			    break;
			}
		    }

		    // Add the new transform if it's not a duplicate
		    if (newTransformNew){
			
			if ( numTransforms >= this .order )
			    throw new IllegalArgumentException("The transformations generated exceed the given group order.");

			this .transforms[ numTransforms ] = newTransform;
			this .adjacencies[ thisTransform ][ i ] = numTransforms;
			this .adjacencies[ numTransforms ][ i ] = thisTransform;
			this .parities[ numTransforms ] = !parities[thisTransform];
			numTransforms++;
			nextLayerEmpty = false;
			
		    }
		    
		}

	    }

	    if (nextLayerEmpty)
		break;

	    // Move everything one layer up
	    lastLayer = thisLayer;
	    thisLayer = nextLayer;
	    nextLayer = numTransforms;
	    
	}

	if (numTransforms < this .order)
	    throw new IllegalArgumentException("Not enough transformations were generated to match the given group order.");

	// Find commuting matrices
	for (int i = 0; i < this .dim; i++){
	    AlgebraicMatrix g_i = this .generators[ i ];
	    for (int j = 0; j < this .dim; j++){
		AlgebraicMatrix g_j = this .generators[ j ];
		commutes[i][j] = g_i .times(g_j) .equals(g_j .times(g_i));
	    }
	}

    }
   
    public void buildNonSnub( AlgebraicVector edgeScales, boolean[] renderEdges)
	throws NullPointerException, IllegalArgumentException
    {

	if (edgeScales == null)
	    throw new NullPointerException("edgeScales must be non-null");

	if (renderEdges == null)
	    throw new NullPointerException("renderEdges must be non-null");

	if (edgeScales.dimension() != this .dim)
	    throw new IllegalArgumentException("edgeScales is the wrong length");

	if (renderEdges.length != this .dim)
	    throw new IllegalArgumentException("renderEdges is the wrong length");
	
	// TODO: ensure edgeScales is in the same field
      
	AlgebraicVector initVertex = this .simplexVerts .timesColumn(edgeScales);
	AlgebraicVector[] vertices = new AlgebraicVector[this .order];

	for ( int i = 0; i < this .order; i++ ) {
	    vertices[ i ] = this .transforms[ i ] .timesColumn(initVertex);
	    // TODO: Add vertex at position vertices[ i ]
	}

	for ( int j = 0; j < this .dim; j++ ) {
	    if ( renderEdges[ j ] ) {
		for ( int i1 = 0; i1 < this .order; i1++ ) {
		    
		    AlgebraicVector p1 = vertices[ i1 ];
		    int i2 = this .adjacencies[ i1 ][ j ];
		    AlgebraicVector p2 = vertices[ i2 ];
		    
		    if ( i1 < i2 ) {
			// TODO: Add edge between p1 and p2
		    }
		    
		}
	    }
	}
	
    }

    public void buildSnub( AlgebraicVector edgeScales )
	throws NullPointerException, IllegalArgumentException
    {

	if (edgeScales == null)
	    throw new NullPointerException("edgeScales must be non-null");

	if (edgeScales.dimension() != this .dim)
	    throw new IllegalArgumentException("edgeScales is the wrong length");

	for (int i = 0; i < this.dim; i++){
	    if (edgeScales .getComponent(i) .isZero()){
		boolean[] renderEdges = new boolean[ this .dim ];
		for (int j = 0; j < this.dim; j++)
		    renderEdges[ j ] = true;
		this .buildNonSnub(edgeScales, renderEdges);
		return;
	    }
	}
	
	// TODO: ensure edgeScales is in the same field
	
	AlgebraicVector initVertex = this .simplexVerts .timesColumn(edgeScales);
	AlgebraicVector[] vertices = new AlgebraicVector[this .order];

	for ( int i = 0; i < this .order; i++ ) {
	    if ( this .parities[ i ] ) {
		vertices[ i ] = this .transforms[ i ] .timesColumn(initVertex);
		// TODO: Add vertex at position vertices[ i ]
	    }
	}

	for ( int j1 = 0; j1 < this .dim; j1++ ) {
	    for ( int j2 = j1+1; j2 < this .dim; j2++ ) {
		for ( int i1 = 0; i1 < this .order; i1++ ) {
			
		    AlgebraicVector p1 = vertices[ i1 ];
		    int i2 = this .adjacencies[ this .adjacencies[ i1 ][ j1 ] ][ j2 ];
		    AlgebraicVector p2 = vertices[ i2 ];
		    // TODO: Add edge between p1 and p2

		    if (!commutes[ j1 ][ j2 ]) {
			int i3 = this .adjacencies[ this .adjacencies[ i1 ][ j2 ] ][ j1 ];
			AlgebraicVector p3 = vertices[ i3 ];
			// TODO: Add edge between p1 and p3
		    }
			
		}
	    }
	}
	
    }
    
}
