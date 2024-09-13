/* Generated from Java with JSweet 3.2.0-SNAPSHOT - http://www.jsweet.org */
namespace com.vzome.core.model {
    export interface Strut extends com.vzome.core.model.Manifestation, java.lang.Comparable<Strut> {
        getZoneVector(): com.vzome.core.algebra.AlgebraicVector;

        setZoneVector(vector: com.vzome.core.algebra.AlgebraicVector);

        /**
         * 
         * @param {*} other
         * @return {number}
         */
        compareTo(other: Strut): number;

        getCanonicalLesserEnd(): com.vzome.core.algebra.AlgebraicVector;

        getCanonicalGreaterEnd(): com.vzome.core.algebra.AlgebraicVector;

        getEnd(): com.vzome.core.algebra.AlgebraicVector;

        getOffset(): com.vzome.core.algebra.AlgebraicVector;
    }
}

