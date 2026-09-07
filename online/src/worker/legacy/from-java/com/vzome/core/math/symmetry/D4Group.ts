import { AlgebraicField } from "../../algebra/AlgebraicField.js";
import { AlgebraicNumber } from "../../algebra/AlgebraicNumber.js";
import { AlgebraicVector } from "../../algebra/AlgebraicVector.js";
import { CoxeterGroup } from "./CoxeterGroup.js";

export class D4Group implements CoxeterGroup {
    /*private*/ field: AlgebraicField;

    ROOTS: AlgebraicVector[];

    WEIGHTS: AlgebraicVector[];

    static D4_PERMS: number[][]; public static D4_PERMS_$LI$(): number[][] { if (D4Group.D4_PERMS == null) { D4Group.D4_PERMS = [[0, 1, 2, 3], [2, 3, 0, 1], [1, 0, 3, 2], [3, 2, 1, 0], [2, 1, 3, 0], [3, 0, 2, 1], [1, 2, 0, 3], [0, 3, 1, 2], [0, 2, 3, 1], [3, 1, 0, 2], [2, 0, 1, 3], [1, 3, 2, 0], [1, 0, 2, 3], [2, 3, 1, 0], [0, 1, 3, 2], [3, 2, 0, 1], [0, 2, 1, 3], [1, 3, 0, 2], [2, 0, 3, 1], [3, 1, 2, 0], [2, 1, 0, 3], [0, 3, 2, 1], [1, 2, 3, 0], [3, 0, 1, 2]]; }  return D4Group.D4_PERMS; }

    public constructor(field: AlgebraicField) {
        if (this.field === undefined) { this.field = null; }
        this.ROOTS = [null, null, null, null];
        this.WEIGHTS = [null, null, null, null];
        this.field = field;
        const neg_one: AlgebraicNumber = field['createRational$long'](-1);
        this.ROOTS[0] = field.basisVector(4, AlgebraicVector.X4);
        this.ROOTS[0].setComponent(AlgebraicVector.Y4, neg_one);
        this.ROOTS[1] = field.basisVector(4, AlgebraicVector.Y4);
        this.ROOTS[1].setComponent(AlgebraicVector.Z4, neg_one);
        this.ROOTS[2] = field.basisVector(4, AlgebraicVector.Z4);
        this.ROOTS[2].setComponent(AlgebraicVector.W4, neg_one);
        this.ROOTS[3] = field.basisVector(4, AlgebraicVector.Z4);
        this.ROOTS[3].setComponent(AlgebraicVector.W4, field.one());
        const y: AlgebraicVector = field.basisVector(4, AlgebraicVector.Y4);
        const half: AlgebraicNumber = field['createRational$long$long'](1, 2);
        const neg_half: AlgebraicNumber = field['createRational$long$long'](-1, 2);
        this.WEIGHTS[0] = field.basisVector(4, AlgebraicVector.X4);
        this.WEIGHTS[1] = this.WEIGHTS[0].plus(y);
        this.WEIGHTS[2] = field.basisVector(4, AlgebraicVector.X4);
        this.WEIGHTS[2].setComponent(AlgebraicVector.X4, half);
        this.WEIGHTS[2].setComponent(AlgebraicVector.Y4, half);
        this.WEIGHTS[2].setComponent(AlgebraicVector.Z4, half);
        this.WEIGHTS[2].setComponent(AlgebraicVector.W4, neg_half);
        this.WEIGHTS[3] = field.basisVector(4, AlgebraicVector.X4);
        this.WEIGHTS[3].setComponent(AlgebraicVector.X4, half);
        this.WEIGHTS[3].setComponent(AlgebraicVector.Y4, half);
        this.WEIGHTS[3].setComponent(AlgebraicVector.Z4, half);
        this.WEIGHTS[3].setComponent(AlgebraicVector.W4, half);
    }

    /**
     * 
     * @return {number}
     */
    public getOrder(): number {
        return 24 * 8;
    }

    /**
     * 
     * @param {AlgebraicVector} model
     * @param {number} element
     * @return {AlgebraicVector}
     */
    public groupAction(model: AlgebraicVector, element: number): AlgebraicVector {
        const result: AlgebraicVector = this.field.basisVector(4, AlgebraicVector.X4);
        const perm: number = (element / 8|0);
        let signs: number = element % 8;
        let even: boolean = true;
        for(let c: number = 0; c < 4; c++) {{
            let source: AlgebraicNumber = model.getComponent((D4Group.D4_PERMS_$LI$()[perm][c] + 1) % 4);
            if (c === 3 && !even){
                source = source.negate();
            } else if (signs % 2 !== 0){
                even = !even;
                source = source.negate();
            }
            result.setComponent((c + 1) % 4, source);
            signs = signs >> 1;
        };}
        return result;
    }

    /**
     * 
     * @return {AlgebraicVector}
     */
    public getOrigin(): AlgebraicVector {
        return this.field.origin(4);
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
        const result: AlgebraicVector = this.field.basisVector(4, AlgebraicVector.X4);
        const perm: number = (element / 8|0);
        if (perm >= 12)return null;
        let signs: number = element % 8;
        let even: boolean = true;
        for(let c: number = 0; c < 4; c++) {{
            let source: AlgebraicNumber = model.getComponent((D4Group.D4_PERMS_$LI$()[perm][c] + 1) % 4);
            if (c === 3 && !even){
                source = source.negate();
            } else if (signs % 2 !== 0){
                even = !even;
                source = source.negate();
            }
            result.setComponent((c + 1) % 4, source);
            signs = signs >> 1;
        };}
        return result;
    }
}
D4Group["__class"] = "com.vzome.core.math.symmetry.D4Group";
D4Group["__interfaces"] = ["com.vzome.core.math.symmetry.CoxeterGroup"];
