package com.vzome.core.math.convexhull;

import com.vzome.core.algebra.AlgebraicVector;

/**
 * Represents vertices of the hull, as well as the points from which it is
 * formed.
 *
 * @author John E. Lloyd, Fall 2004
 */
class Vertex {
    /**
     * Spatial point associated with this vertex.
     */
    final AlgebraicVector pnt;

    /**
     * Back index into an array.
     */
    int index;

    /**
     * List forward link.
     */
    Vertex prev;

    /**
     * List backward link.
     */
    Vertex next;

    /**
     * Current face that this vertex is outside of.
     */
    Face face;

    /**
     * Constructs a vertex with the specified coordinates and index.
     */
    public Vertex(AlgebraicVector v, int idx) {
        pnt = v;
        index = idx;
    }

}
