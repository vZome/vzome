import { AlgebraicField } from "../../algebra/AlgebraicField.js";
import { AlgebraicNumber } from "../../algebra/AlgebraicNumber.js";
import { AlgebraicVector } from "../../algebra/AlgebraicVector.js";
import { CoxeterGroup } from "./CoxeterGroup.js";

export class A4Group implements CoxeterGroup {
    /*private*/ field: AlgebraicField;

    /*private*/ ROOTS: AlgebraicVector[];

    /*private*/ WEIGHTS: AlgebraicVector[];

    /*private*/ ROOTS_R4: AlgebraicVector[];

    static S5_PERMS: number[][]; public static S5_PERMS_$LI$(): number[][] { if (A4Group.S5_PERMS == null) { A4Group.S5_PERMS = [[0, 1, 2, 3, 4], [1, 2, 3, 4, 0], [2, 3, 4, 0, 1], [3, 4, 0, 1, 2], [4, 0, 1, 2, 3], [1, 2, 0, 3, 4], [2, 0, 3, 4, 1], [0, 3, 4, 1, 2], [3, 4, 1, 2, 0], [4, 1, 2, 0, 3], [0, 2, 3, 1, 4], [2, 3, 1, 4, 0], [3, 1, 4, 0, 2], [1, 4, 0, 2, 3], [4, 0, 2, 3, 1], [0, 1, 3, 4, 2], [1, 3, 4, 2, 0], [3, 4, 2, 0, 1], [4, 2, 0, 1, 3], [2, 0, 1, 3, 4], [3, 1, 2, 4, 0], [1, 2, 4, 0, 3], [2, 4, 0, 3, 1], [4, 0, 3, 1, 2], [0, 3, 1, 2, 4], [1, 4, 2, 3, 0], [4, 2, 3, 0, 1], [2, 3, 0, 1, 4], [3, 0, 1, 4, 2], [0, 1, 4, 2, 3], [1, 3, 2, 0, 4], [3, 2, 0, 4, 1], [2, 0, 4, 1, 3], [0, 4, 1, 3, 2], [4, 1, 3, 2, 0], [0, 2, 4, 3, 1], [2, 4, 3, 1, 0], [4, 3, 1, 0, 2], [3, 1, 0, 2, 4], [1, 0, 2, 4, 3], [2, 1, 3, 0, 4], [1, 3, 0, 4, 2], [3, 0, 4, 2, 1], [0, 4, 2, 1, 3], [4, 2, 1, 3, 0], [0, 3, 2, 4, 1], [3, 2, 4, 1, 0], [2, 4, 1, 0, 3], [4, 1, 0, 3, 2], [1, 0, 3, 2, 4], [2, 1, 4, 3, 0], [1, 4, 3, 0, 2], [4, 3, 0, 2, 1], [3, 0, 2, 1, 4], [0, 2, 1, 4, 3], [4, 3, 2, 1, 0], [3, 2, 1, 0, 4], [2, 1, 0, 4, 3], [1, 0, 4, 3, 2], [0, 4, 3, 2, 1], [0, 1, 3, 2, 4], [0, 2, 1, 3, 4], [0, 3, 2, 1, 4], [1, 0, 2, 3, 4], [1, 2, 3, 0, 4], [1, 3, 0, 2, 4], [2, 1, 0, 3, 4], [2, 0, 3, 1, 4], [2, 3, 1, 0, 4], [3, 1, 2, 0, 4], [3, 2, 0, 1, 4], [3, 0, 1, 2, 4], [0, 1, 2, 4, 3], [0, 2, 3, 4, 1], [0, 3, 1, 4, 2], [1, 0, 3, 4, 2], [1, 2, 0, 4, 3], [1, 3, 2, 4, 0], [2, 1, 3, 4, 0], [2, 0, 1, 4, 3], [2, 3, 0, 4, 1], [3, 1, 0, 4, 2], [3, 2, 1, 4, 0], [3, 0, 2, 4, 1], [0, 2, 4, 1, 3], [0, 3, 4, 2, 1], [1, 0, 4, 2, 3], [1, 2, 4, 3, 0], [1, 3, 4, 0, 2], [2, 1, 4, 0, 3], [2, 0, 4, 3, 1], [2, 3, 4, 1, 0], [3, 1, 4, 2, 0], [3, 2, 4, 0, 1], [3, 0, 4, 1, 2], [0, 4, 1, 2, 3], [0, 4, 2, 3, 1], [0, 4, 3, 1, 2], [1, 4, 0, 3, 2], [1, 4, 2, 0, 3], [1, 4, 3, 2, 0], [2, 4, 1, 3, 0], [2, 4, 0, 1, 3], [2, 4, 3, 0, 1], [3, 4, 1, 0, 2], [3, 4, 2, 1, 0], [3, 4, 0, 2, 1], [4, 0, 1, 3, 2], [4, 0, 2, 1, 3], [4, 0, 3, 2, 1], [4, 1, 0, 2, 3], [4, 1, 2, 3, 0], [4, 1, 3, 0, 2], [4, 2, 1, 0, 3], [4, 2, 0, 3, 1], [4, 2, 3, 1, 0], [4, 3, 1, 2, 0], [4, 3, 2, 0, 1], [0, 1, 4, 3, 2], [4, 3, 0, 1, 2]]; }  return A4Group.S5_PERMS; }

    /*private*/ ONE_FIFTH: AlgebraicNumber;

    /*private*/ TWO_FIFTHS: AlgebraicNumber;

    /*private*/ THREE_FIFTHS: AlgebraicNumber;

    /*private*/ FOUR_FIFTHS: AlgebraicNumber;

    public constructor(field: AlgebraicField) {
        if (this.field === undefined) { this.field = null; }
        this.ROOTS = [null, null, null, null];
        this.WEIGHTS = [null, null, null, null];
        this.ROOTS_R4 = [null, null, null, null];
        if (this.ONE_FIFTH === undefined) { this.ONE_FIFTH = null; }
        if (this.TWO_FIFTHS === undefined) { this.TWO_FIFTHS = null; }
        if (this.THREE_FIFTHS === undefined) { this.THREE_FIFTHS = null; }
        if (this.FOUR_FIFTHS === undefined) { this.FOUR_FIFTHS = null; }
        this.field = field;
        this.ONE_FIFTH = field['createRational$long$long'](1, 5);
        this.TWO_FIFTHS = field['createRational$long$long'](2, 5);
        this.THREE_FIFTHS = field['createRational$long$long'](3, 5);
        this.FOUR_FIFTHS = field['createRational$long$long'](4, 5);
        const neg_one: AlgebraicNumber = field['createRational$long'](-1);
        this.ROOTS[0] = field.basisVector(5, 0);
        this.ROOTS[0].setComponent(1, neg_one);
        this.ROOTS[1] = field.basisVector(5, 1);
        this.ROOTS[1].setComponent(2, neg_one);
        this.ROOTS[2] = field.basisVector(5, 2);
        this.ROOTS[2].setComponent(3, neg_one);
        this.ROOTS[3] = field.basisVector(5, 3);
        this.ROOTS[3].setComponent(4, neg_one);
        this.WEIGHTS[0] = field.basisVector(5, 0);
        this.WEIGHTS[0].setComponent(0, this.FOUR_FIFTHS);
        this.WEIGHTS[0].setComponent(1, this.ONE_FIFTH.negate());
        this.WEIGHTS[0].setComponent(2, this.ONE_FIFTH.negate());
        this.WEIGHTS[0].setComponent(3, this.ONE_FIFTH.negate());
        this.WEIGHTS[0].setComponent(4, this.ONE_FIFTH.negate());
        this.WEIGHTS[1] = field.basisVector(5, 0);
        this.WEIGHTS[1].setComponent(0, this.THREE_FIFTHS);
        this.WEIGHTS[1].setComponent(1, this.THREE_FIFTHS);
        this.WEIGHTS[1].setComponent(2, this.TWO_FIFTHS.negate());
        this.WEIGHTS[1].setComponent(3, this.TWO_FIFTHS.negate());
        this.WEIGHTS[1].setComponent(4, this.TWO_FIFTHS.negate());
        this.WEIGHTS[2] = field.basisVector(5, 0);
        this.WEIGHTS[2].setComponent(0, this.TWO_FIFTHS);
        this.WEIGHTS[2].setComponent(1, this.TWO_FIFTHS);
        this.WEIGHTS[2].setComponent(2, this.TWO_FIFTHS);
        this.WEIGHTS[2].setComponent(3, this.THREE_FIFTHS.negate());
        this.WEIGHTS[2].setComponent(4, this.THREE_FIFTHS.negate());
        this.WEIGHTS[3] = field.basisVector(5, 0);
        this.WEIGHTS[3].setComponent(0, this.ONE_FIFTH);
        this.WEIGHTS[3].setComponent(1, this.ONE_FIFTH);
        this.WEIGHTS[3].setComponent(2, this.ONE_FIFTH);
        this.WEIGHTS[3].setComponent(3, this.ONE_FIFTH);
        this.WEIGHTS[3].setComponent(4, this.FOUR_FIFTHS.negate());
        const two: AlgebraicNumber = field['createRational$long'](2);
        const two_neg: AlgebraicNumber = field['createRational$long'](-2);
        this.ROOTS_R4[0] = field.basisVector(4, 1);
        this.ROOTS_R4[0].setComponent(1, two);
        this.ROOTS_R4[0].setComponent(2, two_neg);
        this.ROOTS_R4[1] = field.basisVector(4, 1);
        this.ROOTS_R4[1].setComponent(3, two_neg);
        this.ROOTS_R4[1].setComponent(1, two_neg);
        this.ROOTS_R4[2] = field.basisVector(4, 1);
        this.ROOTS_R4[2].setComponent(1, two);
        this.ROOTS_R4[2].setComponent(2, two);
        this.ROOTS_R4[3] = field.basisVector(4, 3);
        const root5: AlgebraicNumber = field['createAlgebraicNumber$int$int$int$int'](-1, 2, 1, 0);
        this.ROOTS_R4[3].setComponent(1, neg_one);
        this.ROOTS_R4[3].setComponent(2, neg_one);
        this.ROOTS_R4[3].setComponent(0, root5);
    }

    /**
     * 
     * @return {number}
     */
    public getOrder(): number {
        return A4Group.S5_PERMS_$LI$().length;
    }

    /**
     * 
     * @param {AlgebraicVector} model
     * @param {number} element
     * @return {AlgebraicVector}
     */
    public groupAction(model: AlgebraicVector, element: number): AlgebraicVector {
        let result: AlgebraicVector = this.field.origin(4);
        let sum: AlgebraicNumber = this.field['createRational$long'](0);
        for(let c: number = 0; c < 4; c++) {{
            const source: AlgebraicNumber = model.getComponent(A4Group.S5_PERMS_$LI$()[element][c]);
            sum = sum['plus$com_vzome_core_algebra_AlgebraicNumber'](source);
            const scaled: AlgebraicVector = this.ROOTS_R4[c].scale(sum);
            result = result.plus(scaled);
        };}
        return result.scale(this.field['createPower$int'](-1));
    }

    /**
     * 
     * @return {AlgebraicVector}
     */
    public getOrigin(): AlgebraicVector {
        return this.field.origin(5);
    }

    /**
     * 
     * @param {number} i
     * @return {AlgebraicVector}
     */
    public getWeight(i: number): AlgebraicVector {
        return this.WEIGHTS[i];
    }

    /**
     * 
     * @param {number} i
     * @return {AlgebraicVector}
     */
    public getSimpleRoot(i: number): AlgebraicVector {
        return this.ROOTS[i];
    }

    /**
     * 
     * @return {*}
     */
    public getField(): AlgebraicField {
        return this.field;
    }

    /**
     * 
     * @param {AlgebraicVector} model
     * @param {number} element
     * @return {AlgebraicVector}
     */
    public chiralSubgroupAction(model: AlgebraicVector, element: number): AlgebraicVector {
        if (element >= 60)return null;
        let result: AlgebraicVector = this.field.origin(4);
        let sum: AlgebraicNumber = this.field['createRational$long'](0);
        for(let c: number = 0; c < 4; c++) {{
            const source: AlgebraicNumber = model.getComponent(A4Group.S5_PERMS_$LI$()[element][c]);
            sum = sum['plus$com_vzome_core_algebra_AlgebraicNumber'](source);
            const scaled: AlgebraicVector = this.ROOTS_R4[c].scale(sum);
            result = result.plus(scaled);
        };}
        return result.scale(this.field['createPower$int'](-1));
    }
}
A4Group["__class"] = "com.vzome.core.math.symmetry.A4Group";
A4Group["__interfaces"] = ["com.vzome.core.math.symmetry.CoxeterGroup"];
