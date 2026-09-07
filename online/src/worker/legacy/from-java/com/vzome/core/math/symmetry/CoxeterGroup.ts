import { AlgebraicField } from "../../algebra/AlgebraicField.js";
import { AlgebraicVector } from "../../algebra/AlgebraicVector.js";

export interface CoxeterGroup {
    getOrder(): number;

    getField(): AlgebraicField;

    groupAction(model: AlgebraicVector, element: number): AlgebraicVector;

    getOrigin(): AlgebraicVector;

    getWeight(i: number): AlgebraicVector;

    getSimpleRoot(i: number): AlgebraicVector;

    chiralSubgroupAction(model: AlgebraicVector, i: number): AlgebraicVector;
}
