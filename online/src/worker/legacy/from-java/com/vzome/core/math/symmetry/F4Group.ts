import { AlgebraicField } from "../../algebra/AlgebraicField.js";
import { AlgebraicMatrix } from "../../algebra/AlgebraicMatrix.js";
import { AlgebraicNumber } from "../../algebra/AlgebraicNumber.js";
import { AlgebraicVector } from "../../algebra/AlgebraicVector.js";
import { B4Group } from "./B4Group.js";

export class F4Group extends B4Group {
    ROOTS: AlgebraicVector[];

    /*private*/ WEIGHTS: AlgebraicVector[];

    public A: AlgebraicMatrix;

    public constructor(field: AlgebraicField) {
        super(field);
        this.ROOTS = [null, null, null, null];
        this.WEIGHTS = [null, null, null, null];
        if (this.A === undefined) { this.A = null; }
        const one: AlgebraicNumber = field['createRational$long'](1);
        const two: AlgebraicNumber = field['createRational$long'](2);
        const three: AlgebraicNumber = field['createRational$long'](3);
        const four: AlgebraicNumber = field['createRational$long'](4);
        const neg_one: AlgebraicNumber = field['createRational$long'](-1);
        const neg_two: AlgebraicNumber = field['createRational$long'](-2);
        this.ROOTS[0] = field.basisVector(4, AlgebraicVector.X4);
        this.ROOTS[0].setComponent(AlgebraicVector.X4, two);
        this.ROOTS[0].setComponent(AlgebraicVector.Y4, neg_two);
        this.ROOTS[1] = field.basisVector(4, AlgebraicVector.Y4);
        this.ROOTS[1].setComponent(AlgebraicVector.Y4, two);
        this.ROOTS[1].setComponent(AlgebraicVector.Z4, neg_two);
        this.ROOTS[2] = field.basisVector(4, AlgebraicVector.Z4);
        this.ROOTS[2].setComponent(AlgebraicVector.Z4, two);
        this.ROOTS[3] = field.basisVector(4, AlgebraicVector.W4);
        this.ROOTS[3].setComponent(AlgebraicVector.X4, neg_one);
        this.ROOTS[3].setComponent(AlgebraicVector.Y4, neg_one);
        this.ROOTS[3].setComponent(AlgebraicVector.Z4, neg_one);
        this.WEIGHTS[0] = field.basisVector(4, AlgebraicVector.X4);
        this.WEIGHTS[0].setComponent(AlgebraicVector.X4, two);
        this.WEIGHTS[0].setComponent(AlgebraicVector.W4, two);
        this.WEIGHTS[1] = field.basisVector(4, AlgebraicVector.Y4);
        this.WEIGHTS[1].setComponent(AlgebraicVector.X4, two);
        this.WEIGHTS[1].setComponent(AlgebraicVector.Y4, two);
        this.WEIGHTS[1].setComponent(AlgebraicVector.W4, four);
        this.WEIGHTS[2] = field.basisVector(4, AlgebraicVector.X4);
        this.WEIGHTS[2].setComponent(AlgebraicVector.Y4, one);
        this.WEIGHTS[2].setComponent(AlgebraicVector.Z4, one);
        this.WEIGHTS[2].setComponent(AlgebraicVector.W4, three);
        this.WEIGHTS[3] = field.basisVector(4, AlgebraicVector.W4);
        this.WEIGHTS[3].setComponent(AlgebraicVector.W4, two);
        if (field.scale4dRoots()){
            const scale: AlgebraicNumber = field['createPower$int'](1);
            this.ROOTS[2] = this.ROOTS[2].scale(scale);
            this.WEIGHTS[2] = this.WEIGHTS[2].scale(scale);
        }
        const half: AlgebraicNumber = field['createRational$long$long'](1, 2);
        const neg_half: AlgebraicNumber = field['createRational$long$long'](-1, 2);
        const col1: AlgebraicVector = field.basisVector(4, AlgebraicVector.X4);
        col1.setComponent(AlgebraicVector.X4, half);
        col1.setComponent(AlgebraicVector.Y4, half);
        col1.setComponent(AlgebraicVector.Z4, half);
        col1.setComponent(AlgebraicVector.W4, neg_half);
        const col2: AlgebraicVector = field.basisVector(4, AlgebraicVector.X4);
        col2.setComponent(AlgebraicVector.X4, half);
        col2.setComponent(AlgebraicVector.Y4, half);
        col2.setComponent(AlgebraicVector.Z4, neg_half);
        col2.setComponent(AlgebraicVector.W4, half);
        const col3: AlgebraicVector = field.basisVector(4, AlgebraicVector.X4);
        col3.setComponent(AlgebraicVector.X4, half);
        col3.setComponent(AlgebraicVector.Y4, neg_half);
        col3.setComponent(AlgebraicVector.Z4, half);
        col3.setComponent(AlgebraicVector.W4, half);
        const col4: AlgebraicVector = field.basisVector(4, AlgebraicVector.X4);
        col4.setComponent(AlgebraicVector.X4, half);
        col4.setComponent(AlgebraicVector.Y4, neg_half);
        col4.setComponent(AlgebraicVector.Z4, neg_half);
        col4.setComponent(AlgebraicVector.W4, neg_half);
        this.A = new AlgebraicMatrix(col1, col2, col3, col4);
    }

    /**
     * 
     * @return {number}
     */
    public getOrder(): number {
        return 3 * super.getOrder();
    }

    /**
     * 
     * @param {AlgebraicVector} model
     * @param {number} element
     * @return {AlgebraicVector}
     */
    public groupAction(model: AlgebraicVector, element: number): AlgebraicVector {
        const b4Order: number = super.getOrder();
        const aPower: number = (element / b4Order|0);
        const b4Element: number = element % b4Order;
        switch((aPower)) {
        case 0:
            return super.groupAction(model, b4Element);
        case 1:
            return super.groupAction(this.A.timesColumn(model), b4Element);
        case 2:
            return super.groupAction(this.A.timesColumn(this.A.timesColumn(model)), b4Element);
        default:
            break;
        }
        return null;
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
}
F4Group["__class"] = "com.vzome.core.math.symmetry.F4Group";
F4Group["__interfaces"] = ["com.vzome.core.math.symmetry.CoxeterGroup"];
