/* Generated from Java with JSweet 3.2.0-SNAPSHOT - http://www.jsweet.org */
namespace com.vzome.core.math.symmetry {
    export interface CoxeterGroup {
        getOrder(): number;

        getField(): com.vzome.core.algebra.AlgebraicField;

        groupAction(model: com.vzome.core.algebra.AlgebraicVector, element: number): com.vzome.core.algebra.AlgebraicVector;

        getOrigin(): com.vzome.core.algebra.AlgebraicVector;

        getWeight(i: number): com.vzome.core.algebra.AlgebraicVector;

        getSimpleRoot(i: number): com.vzome.core.algebra.AlgebraicVector;

        chiralSubgroupAction(model: com.vzome.core.algebra.AlgebraicVector, i: number): com.vzome.core.algebra.AlgebraicVector;
    }
}

