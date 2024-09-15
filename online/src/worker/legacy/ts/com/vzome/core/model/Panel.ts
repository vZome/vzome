/* Generated from Java with JSweet 3.2.0-SNAPSHOT - http://www.jsweet.org */
namespace com.vzome.core.model {
    export interface Panel extends com.vzome.core.model.Manifestation, java.lang.Iterable<com.vzome.core.algebra.AlgebraicVector> {
        getZoneVector(): com.vzome.core.algebra.AlgebraicVector;

        setZoneVector(vector: com.vzome.core.algebra.AlgebraicVector);

        getFirstVertex(): com.vzome.core.algebra.AlgebraicVector;

        /**
         * 
         * @return {*}
         */
        iterator(): java.util.Iterator<com.vzome.core.algebra.AlgebraicVector>;

        getVertexCount(): number;

        getNormal(embedding?: any): any;

        getQuadrea(): com.vzome.core.algebra.AlgebraicNumber;
    }
}

