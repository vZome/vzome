import { java, javaemul } from "../../../../../candies/j4ts-2.1.0-SNAPSHOT/bundle.js";
import { AlgebraicField } from "./AlgebraicField.js";
import { AlgebraicNumber } from "./AlgebraicNumber.js";
import { AlgebraicVector } from "./AlgebraicVector.js";

/**
 * A collection of static helper methods for the AlgebraicVector class
 * @class
 */
export class AlgebraicVectors {
    constructor() {
    }

    public static getNormal$com_vzome_core_algebra_AlgebraicVector$com_vzome_core_algebra_AlgebraicVector(v1: AlgebraicVector, v2: AlgebraicVector): AlgebraicVector {
        return v1.cross(v2);
    }

    public static getNormal$com_vzome_core_algebra_AlgebraicVector$com_vzome_core_algebra_AlgebraicVector$com_vzome_core_algebra_AlgebraicVector(v0: AlgebraicVector, v1: AlgebraicVector, v2: AlgebraicVector): AlgebraicVector {
        return AlgebraicVectors.getNormal$com_vzome_core_algebra_AlgebraicVector$com_vzome_core_algebra_AlgebraicVector(v1.minus(v0), v2.minus(v0));
    }

    /**
     * 
     * @param {AlgebraicVector} v0
     * @param {AlgebraicVector} v1
     * @param {AlgebraicVector} v2
     * @return {AlgebraicVector} normal to vectors v1 and v2,
     * with both v1 and v2 positioned at v0
     * using the righthand rule.
     */
    public static getNormal(v0?: any, v1?: any, v2?: any): AlgebraicVector {
        if (((v0 != null && v0 instanceof <any>AlgebraicVector) || v0 === null) && ((v1 != null && v1 instanceof <any>AlgebraicVector) || v1 === null) && ((v2 != null && v2 instanceof <any>AlgebraicVector) || v2 === null)) {
            return <any>AlgebraicVectors.getNormal$com_vzome_core_algebra_AlgebraicVector$com_vzome_core_algebra_AlgebraicVector$com_vzome_core_algebra_AlgebraicVector(v0, v1, v2);
        } else if (((v0 != null && v0 instanceof <any>AlgebraicVector) || v0 === null) && ((v1 != null && v1 instanceof <any>AlgebraicVector) || v1 === null) && v2 === undefined) {
            return <any>AlgebraicVectors.getNormal$com_vzome_core_algebra_AlgebraicVector$com_vzome_core_algebra_AlgebraicVector(v0, v1);
        } else if (((v0 != null && (v0.constructor != null && v0.constructor["__interfaces"] != null && v0.constructor["__interfaces"].indexOf("java.util.Collection") >= 0)) || v0 === null) && v1 === undefined && v2 === undefined) {
            return <any>AlgebraicVectors.getNormal$java_util_Collection(v0);
        } else throw new Error('invalid overload');
    }

    /**
     * 
     * @param {AlgebraicVector} v1
     * @param {AlgebraicVector} v2
     * @return {boolean} true if vectors v1 and v2 are parallel, otherwise false.
     * Considered as position vectors, this is the same as testing if they are collinear with the origin.
     */
    public static areParallel(v1: AlgebraicVector, v2: AlgebraicVector): boolean {
        return AlgebraicVectors.getNormal$com_vzome_core_algebra_AlgebraicVector$com_vzome_core_algebra_AlgebraicVector(v1, v2).isOrigin();
    }

    public static getNormal$java_util_Collection(vectors: java.util.Collection<AlgebraicVector>): AlgebraicVector {
        if (vectors.size() < 3){
            throw new java.lang.IllegalArgumentException("Three vertices are required to calculate a normal. Found " + vectors.size());
        }
        let v0: AlgebraicVector = null;
        let v1: AlgebraicVector = null;
        let normal: AlgebraicVector = null;
        for(let index=vectors.iterator();index.hasNext();) {
            let vector = index.next();
            {
                if (v0 == null){
                    v0 = vector;
                } else if (v1 == null){
                    if (vector !== v0){
                        v1 = vector;
                    }
                } else {
                    normal = AlgebraicVectors.getNormal$com_vzome_core_algebra_AlgebraicVector$com_vzome_core_algebra_AlgebraicVector$com_vzome_core_algebra_AlgebraicVector(v0, v1, vector);
                    if (!normal.isOrigin()){
                        return normal;
                    }
                }
            }
        }
        return normal;
    }

    /**
     * 
     * @param {AlgebraicVector} vector
     * @return {number} index of the vector component having the greatest absolute value.
     * If more than one component has the same absolute value,
     * the greatest index will be returned.
     */
    public static getMaxComponentIndex(vector: AlgebraicVector): number {
        let maxIndex: number = 0;
        let maxSq: AlgebraicNumber = vector.getField().zero();
        for(let i: number = 0; i < vector.dimension(); i++) {{
            const n: AlgebraicNumber = vector.getComponent(i);
            const sq: AlgebraicNumber = n['times$com_vzome_core_algebra_AlgebraicNumber'](n);
            if (!sq.lessThan(maxSq)){
                maxIndex = i;
                maxSq = sq;
            }
        };}
        return maxIndex;
    }

    public static areCoplanar(vectors: java.util.Collection<AlgebraicVector>): boolean {
        if (vectors.size() < 4){
            return true;
        }
        const normal: AlgebraicVector = AlgebraicVectors.getNormal$java_util_Collection(vectors);
        if (normal.isOrigin() || normal.dimension() < 3){
            return true;
        }
        return AlgebraicVectors.areOrthogonalTo(normal, vectors);
    }

    public static areOrthogonalTo(normal: AlgebraicVector, vectors: java.util.Collection<AlgebraicVector>): boolean {
        if (normal.isOrigin()){
            throw new java.lang.IllegalArgumentException("Normal vector cannot be the origin");
        }
        let v0: AlgebraicVector = null;
        for(let index=vectors.iterator();index.hasNext();) {
            let vector = index.next();
            {
                if (v0 == null){
                    v0 = vector;
                } else if (!vector.minus(v0).dot(normal).isZero()){
                    return false;
                }
            }
        }
        return true;
    }

    public static areCollinear$java_util_Collection(vectors: java.util.Collection<AlgebraicVector>): boolean {
        return (vectors.size() < 3) ? true : AlgebraicVectors.getNormal$java_util_Collection(vectors).isOrigin();
    }

    public static areCollinear$com_vzome_core_algebra_AlgebraicVector$com_vzome_core_algebra_AlgebraicVector$com_vzome_core_algebra_AlgebraicVector(v0: AlgebraicVector, v1: AlgebraicVector, v2: AlgebraicVector): boolean {
        return AlgebraicVectors.getNormal$com_vzome_core_algebra_AlgebraicVector$com_vzome_core_algebra_AlgebraicVector$com_vzome_core_algebra_AlgebraicVector(v0, v1, v2).isOrigin();
    }

    /**
     * 
     * @param {AlgebraicVector} v0
     * @param {AlgebraicVector} v1
     * @param {AlgebraicVector} v2
     * @return {boolean} true if position vectors v0, v1 and v2 are collinear, otherwise false.
     */
    public static areCollinear(v0?: any, v1?: any, v2?: any): boolean {
        if (((v0 != null && v0 instanceof <any>AlgebraicVector) || v0 === null) && ((v1 != null && v1 instanceof <any>AlgebraicVector) || v1 === null) && ((v2 != null && v2 instanceof <any>AlgebraicVector) || v2 === null)) {
            return <any>AlgebraicVectors.areCollinear$com_vzome_core_algebra_AlgebraicVector$com_vzome_core_algebra_AlgebraicVector$com_vzome_core_algebra_AlgebraicVector(v0, v1, v2);
        } else if (((v0 != null && (v0.constructor != null && v0.constructor["__interfaces"] != null && v0.constructor["__interfaces"].indexOf("java.util.Collection") >= 0)) || v0 === null) && v1 === undefined && v2 === undefined) {
            return <any>AlgebraicVectors.areCollinear$java_util_Collection(v0);
        } else throw new Error('invalid overload');
    }

    public static getLinePlaneIntersection(lineStart: AlgebraicVector, lineDirection: AlgebraicVector, planeCenter: AlgebraicVector, planeNormal: AlgebraicVector): AlgebraicVector {
        const denom: AlgebraicNumber = planeNormal.dot(lineDirection);
        if (denom.isZero())return null;
        const p1p3: AlgebraicVector = planeCenter.minus(lineStart);
        const numerator: AlgebraicNumber = planeNormal.dot(p1p3);
        const u: AlgebraicNumber = numerator.dividedBy(denom);
        return lineStart.plus(lineDirection.scale(u));
    }

    public static calculateCentroid(vectors: java.util.Collection<AlgebraicVector>): AlgebraicVector {
        return AlgebraicVectors.getCentroid(vectors.toArray<any>((s => { let a=[]; while(s-->0) a.push(null); return a; })(vectors.size())));
    }

    public static getCentroid(vectors: AlgebraicVector[]): AlgebraicVector {
        const field: AlgebraicField = vectors[0].getField();
        let sum: AlgebraicVector = new AlgebraicVector(field, vectors[0].dimension());
        for(let index = 0; index < vectors.length; index++) {
            let vector = vectors[index];
            {
                sum = sum.plus(vector);
            }
        }
        return sum.scale(field['createRational$long$long'](1, vectors.length));
    }

    public static getMagnitudeSquared(v: AlgebraicVector): AlgebraicNumber {
        return v.dot(v);
    }

    /**
     * @param {AlgebraicVector} vector
     * @return {AlgebraicVector} the greater of {@code vector} and its inverse.
     * The comparison is based on a canonical (not mathematical) comparison as implemented in {@code AlgebraicVector.compareTo()}.
     * There is no reasonable mathematical sense of ordering vectors,
     * but this provides a way to map a vector and its inverse to a common vector for such purposes as sorting and color mapping.
     */
    public static getCanonicalOrientation(vector: AlgebraicVector): AlgebraicVector {
        const negate: AlgebraicVector = vector.negate();
        return vector.compareTo(negate) > 0 ? vector : negate;
    }

    /**
     * getMostDistantFromOrigin() is is used by a few ColorMapper classes, but I think it can eventually be useful elsewhere as well, for example, a zoom-to-fit command or in deriving a convex hull. I've made it a static method of the AlgebraicVector class to encourage such reuse.
     * 
     * @param {*} vectors A collection of vectors to be evaluated.
     * @return {java.util.TreeSet} A canonically sorted subset (maybe all) of the {@code vectors} collection. All of the returned vectors will be the same distance from the origin. That distance will be the maximum distance from the origin of any of the vectors in the original collection. If the original collection contains only the origin then so will the result.
     */
    public static getMostDistantFromOrigin(vectors: java.util.Collection<AlgebraicVector>): java.util.TreeSet<AlgebraicVector> {
        const mostDistant: java.util.TreeSet<AlgebraicVector> = <any>(new java.util.TreeSet<any>());
        let maxDistanceSquared: number = 0.0;
        for(let index=vectors.iterator();index.hasNext();) {
            let vector = index.next();
            {
                const magnitudeSquared: number = AlgebraicVectors.getMagnitudeSquared(vector).evaluate();
                if (magnitudeSquared >= maxDistanceSquared){
                    if (magnitudeSquared > maxDistanceSquared){
                        mostDistant.clear();
                    }
                    maxDistanceSquared = magnitudeSquared;
                    mostDistant.add(vector);
                }
            }
        }
        return mostDistant;
    }
}
AlgebraicVectors["__class"] = "com.vzome.core.algebra.AlgebraicVectors";
