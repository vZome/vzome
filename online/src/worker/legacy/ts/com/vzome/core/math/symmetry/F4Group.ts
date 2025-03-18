/* Generated from Java with JSweet 3.2.0-SNAPSHOT - http://www.jsweet.org */
namespace com.vzome.core.math.symmetry {
    export class F4Group extends com.vzome.core.math.symmetry.B4Group {
        ROOTS: com.vzome.core.algebra.AlgebraicVector[];

        /*private*/ WEIGHTS: com.vzome.core.algebra.AlgebraicVector[];

        public A: com.vzome.core.algebra.AlgebraicMatrix;

        public constructor(field: com.vzome.core.algebra.AlgebraicField) {
            super(field);
            this.ROOTS = [null, null, null, null];
            this.WEIGHTS = [null, null, null, null];
            if (this.A === undefined) { this.A = null; }
            const one: com.vzome.core.algebra.AlgebraicNumber = field['createRational$long'](1);
            const two: com.vzome.core.algebra.AlgebraicNumber = field['createRational$long'](2);
            const three: com.vzome.core.algebra.AlgebraicNumber = field['createRational$long'](3);
            const four: com.vzome.core.algebra.AlgebraicNumber = field['createRational$long'](4);
            const neg_one: com.vzome.core.algebra.AlgebraicNumber = field['createRational$long'](-1);
            const neg_two: com.vzome.core.algebra.AlgebraicNumber = field['createRational$long'](-2);
            this.ROOTS[0] = field.basisVector(4, com.vzome.core.algebra.AlgebraicVector.X4);
            this.ROOTS[0].setComponent(com.vzome.core.algebra.AlgebraicVector.X4, two);
            this.ROOTS[0].setComponent(com.vzome.core.algebra.AlgebraicVector.Y4, neg_two);
            this.ROOTS[1] = field.basisVector(4, com.vzome.core.algebra.AlgebraicVector.Y4);
            this.ROOTS[1].setComponent(com.vzome.core.algebra.AlgebraicVector.Y4, two);
            this.ROOTS[1].setComponent(com.vzome.core.algebra.AlgebraicVector.Z4, neg_two);
            this.ROOTS[2] = field.basisVector(4, com.vzome.core.algebra.AlgebraicVector.Z4);
            this.ROOTS[2].setComponent(com.vzome.core.algebra.AlgebraicVector.Z4, two);
            this.ROOTS[3] = field.basisVector(4, com.vzome.core.algebra.AlgebraicVector.W4);
            this.ROOTS[3].setComponent(com.vzome.core.algebra.AlgebraicVector.X4, neg_one);
            this.ROOTS[3].setComponent(com.vzome.core.algebra.AlgebraicVector.Y4, neg_one);
            this.ROOTS[3].setComponent(com.vzome.core.algebra.AlgebraicVector.Z4, neg_one);
            this.WEIGHTS[0] = field.basisVector(4, com.vzome.core.algebra.AlgebraicVector.X4);
            this.WEIGHTS[0].setComponent(com.vzome.core.algebra.AlgebraicVector.X4, two);
            this.WEIGHTS[0].setComponent(com.vzome.core.algebra.AlgebraicVector.W4, two);
            this.WEIGHTS[1] = field.basisVector(4, com.vzome.core.algebra.AlgebraicVector.Y4);
            this.WEIGHTS[1].setComponent(com.vzome.core.algebra.AlgebraicVector.X4, two);
            this.WEIGHTS[1].setComponent(com.vzome.core.algebra.AlgebraicVector.Y4, two);
            this.WEIGHTS[1].setComponent(com.vzome.core.algebra.AlgebraicVector.W4, four);
            this.WEIGHTS[2] = field.basisVector(4, com.vzome.core.algebra.AlgebraicVector.X4);
            this.WEIGHTS[2].setComponent(com.vzome.core.algebra.AlgebraicVector.Y4, one);
            this.WEIGHTS[2].setComponent(com.vzome.core.algebra.AlgebraicVector.Z4, one);
            this.WEIGHTS[2].setComponent(com.vzome.core.algebra.AlgebraicVector.W4, three);
            this.WEIGHTS[3] = field.basisVector(4, com.vzome.core.algebra.AlgebraicVector.W4);
            this.WEIGHTS[3].setComponent(com.vzome.core.algebra.AlgebraicVector.W4, two);
            if (field.scale4dRoots()){
                const scale: com.vzome.core.algebra.AlgebraicNumber = field['createPower$int'](1);
                this.ROOTS[2] = this.ROOTS[2].scale(scale);
                this.WEIGHTS[2] = this.WEIGHTS[2].scale(scale);
            }
            const half: com.vzome.core.algebra.AlgebraicNumber = field['createRational$long$long'](1, 2);
            const neg_half: com.vzome.core.algebra.AlgebraicNumber = field['createRational$long$long'](-1, 2);
            const col1: com.vzome.core.algebra.AlgebraicVector = field.basisVector(4, com.vzome.core.algebra.AlgebraicVector.X4);
            col1.setComponent(com.vzome.core.algebra.AlgebraicVector.X4, half);
            col1.setComponent(com.vzome.core.algebra.AlgebraicVector.Y4, half);
            col1.setComponent(com.vzome.core.algebra.AlgebraicVector.Z4, half);
            col1.setComponent(com.vzome.core.algebra.AlgebraicVector.W4, neg_half);
            const col2: com.vzome.core.algebra.AlgebraicVector = field.basisVector(4, com.vzome.core.algebra.AlgebraicVector.X4);
            col2.setComponent(com.vzome.core.algebra.AlgebraicVector.X4, half);
            col2.setComponent(com.vzome.core.algebra.AlgebraicVector.Y4, half);
            col2.setComponent(com.vzome.core.algebra.AlgebraicVector.Z4, neg_half);
            col2.setComponent(com.vzome.core.algebra.AlgebraicVector.W4, half);
            const col3: com.vzome.core.algebra.AlgebraicVector = field.basisVector(4, com.vzome.core.algebra.AlgebraicVector.X4);
            col3.setComponent(com.vzome.core.algebra.AlgebraicVector.X4, half);
            col3.setComponent(com.vzome.core.algebra.AlgebraicVector.Y4, neg_half);
            col3.setComponent(com.vzome.core.algebra.AlgebraicVector.Z4, half);
            col3.setComponent(com.vzome.core.algebra.AlgebraicVector.W4, half);
            const col4: com.vzome.core.algebra.AlgebraicVector = field.basisVector(4, com.vzome.core.algebra.AlgebraicVector.X4);
            col4.setComponent(com.vzome.core.algebra.AlgebraicVector.X4, half);
            col4.setComponent(com.vzome.core.algebra.AlgebraicVector.Y4, neg_half);
            col4.setComponent(com.vzome.core.algebra.AlgebraicVector.Z4, neg_half);
            col4.setComponent(com.vzome.core.algebra.AlgebraicVector.W4, neg_half);
            this.A = new com.vzome.core.algebra.AlgebraicMatrix(col1, col2, col3, col4);
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
         * @param {com.vzome.core.algebra.AlgebraicVector} model
         * @param {number} element
         * @return {com.vzome.core.algebra.AlgebraicVector}
         */
        public groupAction(model: com.vzome.core.algebra.AlgebraicVector, element: number): com.vzome.core.algebra.AlgebraicVector {
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
         * @return {com.vzome.core.algebra.AlgebraicVector}
         */
        public getWeight(i: number): com.vzome.core.algebra.AlgebraicVector {
            return this.WEIGHTS[i];
        }

        /**
         * 
         * @param {number} i
         * @return {com.vzome.core.algebra.AlgebraicVector}
         */
        public getSimpleRoot(i: number): com.vzome.core.algebra.AlgebraicVector {
            return this.ROOTS[i];
        }
    }
    F4Group["__class"] = "com.vzome.core.math.symmetry.F4Group";
    F4Group["__interfaces"] = ["com.vzome.core.math.symmetry.CoxeterGroup"];


}

