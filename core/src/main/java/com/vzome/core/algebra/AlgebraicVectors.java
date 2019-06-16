package com.vzome.core.algebra;

import java.util.Collection;
import java.util.TreeSet;

/**
 * A collection of static helper methods for the AlgebraicVector class
 */
public class AlgebraicVectors {
    private AlgebraicVectors() {}

    /**
     * 
     * @param v1
     * @param v2
     * @return normal to position vectors v1 and v2, 
     * with both v1 and v2 starting at the origin
     * using the righthand rule.
     */
    public static AlgebraicVector getNormal(final AlgebraicVector v1, final AlgebraicVector v2) {
        return v1.cross(v2);
    }

    /**
     * 
     * @param v0
     * @param v1
     * @param v2
     * @return normal to vectors v1 and v2, 
     * with both v1 and v2 positioned at v0
     * using the righthand rule.
     */
    public static AlgebraicVector getNormal(final AlgebraicVector v0, final AlgebraicVector v1, final AlgebraicVector v2) {
        return getNormal(v1.minus(v0), v2.minus(v0));
    }

    /**
     * 
     * @param v1
     * @param v2
     * @return true if vectors v1 and v2 are parallel, otherwise false.
     * Considered as position vectors, this is the same as testing if they are collinear with the origin.
     */
    public static boolean areParallel(final AlgebraicVector v1, final AlgebraicVector v2) {
        return getNormal(v1, v2).isOrigin();
    }
    
    /** 
     * @return a vector that is orthogonal to a plane defined by three of the vertices.
     * If vertices are all collinear, then the origin is returned.
     * @throws IllegalArgumentException if fewer than three vectors are in the collection.
     */ 
    public static AlgebraicVector getNormal(final Collection<AlgebraicVector> vectors) 
    { 
        if(vectors.size() < 3) { 
            throw new IllegalArgumentException("Three vertices are required to calculate a normal. Found " + vectors.size()); 
        } 
        AlgebraicVector v0 = null;
        AlgebraicVector v1 = null;
        AlgebraicVector normal = null;
        for(AlgebraicVector vector : vectors)
        {
            if(v0 == null) {
                v0 = vector;
            }
            else if(v1 == null) {
                if(vector != v0) { // in case vectors are not unique
                    v1 = vector;
                }
            }
            else {
                normal = getNormal(v0, v1, vector);
                if(!normal.isOrigin()) {
                    return normal;
                }
            }
        }
        return normal;
    }
    
    /**
     * 
     * @param vector
     * @return index of the vector component having the greatest absolute value.
     *      If more than one component has the same absolute value, 
     *      the greatest index will be returned.
     */
    public static int getMaxComponentIndex(AlgebraicVector vector) {
        int maxIndex = 0;
        AlgebraicNumber maxSq = vector.getField().zero();
        for(int i = 0; i < vector.dimension(); i++) {
            AlgebraicNumber n = vector.getComponent(i);
            AlgebraicNumber sq = n.times(n);
            if(! sq.lessThan(maxSq) ) {
                // ensure that we have the max index of any equal components
                // by testing if not lessThan, not just if greaterThan
                maxIndex = i;
                maxSq = sq;
            }
        }
        return maxIndex;
    }

    public static boolean areCoplanar(Collection<AlgebraicVector> vectors) {
        if(vectors.size() < 4) {
            return true;
        }
        AlgebraicVector normal = getNormal(vectors);
        if(normal.isOrigin() || normal.dimension() < 3) {
            return true;
        }
        return areOrthogonalTo(normal, vectors);
    }
    
    public static boolean areOrthogonalTo(AlgebraicVector normal, Collection<AlgebraicVector> vectors) {
        if(normal.isOrigin()) {
            throw new IllegalArgumentException("Normal vector cannot be the origin"); 
        }
        AlgebraicVector v0 = null;
        for(AlgebraicVector vector : vectors) {
            if(v0 == null) {
                v0 = vector;
            } else if(!vector.minus(v0).dot(normal).isZero()) {
                return false;
            }
        }
        return true;
    }
    
    public static boolean areCollinear(Collection<AlgebraicVector> vectors) {
        return (vectors.size() < 3) 
                ? true 
                : getNormal(vectors).isOrigin();
    }
     /**
     * 
     * @param v0
     * @param v1
     * @param v2
     * @return true if position vectors v0, v1 and v2 are collinear, otherwise false.
     */
    public static boolean areCollinear(final AlgebraicVector v0, final AlgebraicVector v1, final AlgebraicVector v2) {
        return getNormal(v0, v1, v2).isOrigin();
    }
    
    public static AlgebraicVector getLinePlaneIntersection( AlgebraicVector lineStart, AlgebraicVector lineDirection,
                                                            AlgebraicVector planeCenter, AlgebraicVector planeNormal )
    {
        AlgebraicNumber denom = planeNormal .dot( lineDirection );
        if ( denom .isZero() )
            return null;

        AlgebraicVector p1p3 = planeCenter .minus( lineStart );
        AlgebraicNumber numerator = planeNormal .dot( p1p3 );
        AlgebraicNumber u = numerator .dividedBy( denom );
        return lineStart .plus( lineDirection .scale( u ) );
    }

    public static AlgebraicVector calculateCentroid(Collection<AlgebraicVector> vectors) {
        return getCentroid(vectors.toArray(new AlgebraicVector[vectors.size()]));
    }

    public static AlgebraicVector getCentroid(AlgebraicVector[] vectors) {
        // Assert that vectors is neither null nor empty.
        AlgebraicField field = vectors[0].getField();
        // Start with 0 as when calculating the average of any set of numbers...
        AlgebraicVector sum = new AlgebraicVector(field, vectors[0].dimension()); // preinitialized to 0
        for (AlgebraicVector vector : vectors) {
            // add them all together
            sum = sum.plus(vector);
        }
        // then divide by the number of items we added (to divide is to scale by the reciprocal)
        return sum.scale(field.createRational(1, vectors.length));
    }

    public static AlgebraicNumber getMagnitudeSquared(AlgebraicVector v) {
        return v.dot(v);
    }

    /**
     * @param vector
     * @return the greater of {@code vector} and its inverse. 
     * The comparison is based on a canonical (not mathematical) comparison as implemented in {@code AlgebraicVector.compareTo()}. 
     * There is no reasonable mathematical sense of ordering vectors, 
     * but this provides a way to map a vector and its inverse to a common vector for such purposes as sorting and color mapping.    
     */
    public static AlgebraicVector getCanonicalOrientation( AlgebraicVector vector ) {
        AlgebraicVector negate = vector.negate();
        return vector.compareTo(negate) > 0 ? vector : negate;
    }

    /**
     * getMostDistantFromOrigin() is is used by a few ColorMapper classes, but I think it can eventually be useful elsewhere as well, for example, a zoom-to-fit command or in deriving a convex hull. I've made it a static method of the AlgebraicVector class to encourage such reuse.
     *
     * @param vectors A collection of vectors to be evaluated.
     * @return A canonically sorted subset (maybe all) of the {@code vectors} collection. All of the returned vectors will be the same distance from the origin. That distance will be the maximum distance from the origin of any of the vectors in the original collection. If the original collection contains only the origin then so will the result.
     */
    public static TreeSet<AlgebraicVector> getMostDistantFromOrigin(Collection<AlgebraicVector> vectors) {
        TreeSet<AlgebraicVector> mostDistant = new TreeSet<>();
        double maxDistanceSquared = 0D;
        for (AlgebraicVector vector : vectors) {
            double magnitudeSquared = AlgebraicVectors.getMagnitudeSquared(vector).evaluate();
            if (magnitudeSquared >= maxDistanceSquared) {
                if (magnitudeSquared > maxDistanceSquared) {
                    mostDistant.clear();
                }
                maxDistanceSquared = magnitudeSquared;
                mostDistant.add(vector);
            }
        }
        return mostDistant;
    }

}
