/* Generated from Java with JSweet 3.2.0-SNAPSHOT - http://www.jsweet.org */
namespace com.vzome.core.math.symmetry {
    export class D4Group implements com.vzome.core.math.symmetry.CoxeterGroup {
        /*private*/ field: com.vzome.core.algebra.AlgebraicField;

        ROOTS: com.vzome.core.algebra.AlgebraicVector[];

        WEIGHTS: com.vzome.core.algebra.AlgebraicVector[];

        static D4_PERMS: number[][]; public static D4_PERMS_$LI$(): number[][] { if (D4Group.D4_PERMS == null) { D4Group.D4_PERMS = [[0, 1, 2, 3], [2, 3, 0, 1], [1, 0, 3, 2], [3, 2, 1, 0], [2, 1, 3, 0], [3, 0, 2, 1], [1, 2, 0, 3], [0, 3, 1, 2], [0, 2, 3, 1], [3, 1, 0, 2], [2, 0, 1, 3], [1, 3, 2, 0], [1, 0, 2, 3], [2, 3, 1, 0], [0, 1, 3, 2], [3, 2, 0, 1], [0, 2, 1, 3], [1, 3, 0, 2], [2, 0, 3, 1], [3, 1, 2, 0], [2, 1, 0, 3], [0, 3, 2, 1], [1, 2, 3, 0], [3, 0, 1, 2]]; }  return D4Group.D4_PERMS; }

        public constructor(field: com.vzome.core.algebra.AlgebraicField) {
            if (this.field === undefined) { this.field = null; }
            this.ROOTS = [null, null, null, null];
            this.WEIGHTS = [null, null, null, null];
            this.field = field;
            const neg_one: com.vzome.core.algebra.AlgebraicNumber = field['createRational$long'](-1);
            this.ROOTS[0] = field.basisVector(4, com.vzome.core.algebra.AlgebraicVector.X4);
            this.ROOTS[0].setComponent(com.vzome.core.algebra.AlgebraicVector.Y4, neg_one);
            this.ROOTS[1] = field.basisVector(4, com.vzome.core.algebra.AlgebraicVector.Y4);
            this.ROOTS[1].setComponent(com.vzome.core.algebra.AlgebraicVector.Z4, neg_one);
            this.ROOTS[2] = field.basisVector(4, com.vzome.core.algebra.AlgebraicVector.Z4);
            this.ROOTS[2].setComponent(com.vzome.core.algebra.AlgebraicVector.W4, neg_one);
            this.ROOTS[3] = field.basisVector(4, com.vzome.core.algebra.AlgebraicVector.Z4);
            this.ROOTS[3].setComponent(com.vzome.core.algebra.AlgebraicVector.W4, field.one());
            const y: com.vzome.core.algebra.AlgebraicVector = field.basisVector(4, com.vzome.core.algebra.AlgebraicVector.Y4);
            const half: com.vzome.core.algebra.AlgebraicNumber = field['createRational$long$long'](1, 2);
            const neg_half: com.vzome.core.algebra.AlgebraicNumber = field['createRational$long$long'](-1, 2);
            this.WEIGHTS[0] = field.basisVector(4, com.vzome.core.algebra.AlgebraicVector.X4);
            this.WEIGHTS[1] = this.WEIGHTS[0].plus(y);
            this.WEIGHTS[2] = field.basisVector(4, com.vzome.core.algebra.AlgebraicVector.X4);
            this.WEIGHTS[2].setComponent(com.vzome.core.algebra.AlgebraicVector.X4, half);
            this.WEIGHTS[2].setComponent(com.vzome.core.algebra.AlgebraicVector.Y4, half);
            this.WEIGHTS[2].setComponent(com.vzome.core.algebra.AlgebraicVector.Z4, half);
            this.WEIGHTS[2].setComponent(com.vzome.core.algebra.AlgebraicVector.W4, neg_half);
            this.WEIGHTS[3] = field.basisVector(4, com.vzome.core.algebra.AlgebraicVector.X4);
            this.WEIGHTS[3].setComponent(com.vzome.core.algebra.AlgebraicVector.X4, half);
            this.WEIGHTS[3].setComponent(com.vzome.core.algebra.AlgebraicVector.Y4, half);
            this.WEIGHTS[3].setComponent(com.vzome.core.algebra.AlgebraicVector.Z4, half);
            this.WEIGHTS[3].setComponent(com.vzome.core.algebra.AlgebraicVector.W4, half);
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
         * @param {com.vzome.core.algebra.AlgebraicVector} model
         * @param {number} element
         * @return {com.vzome.core.algebra.AlgebraicVector}
         */
        public groupAction(model: com.vzome.core.algebra.AlgebraicVector, element: number): com.vzome.core.algebra.AlgebraicVector {
            const result: com.vzome.core.algebra.AlgebraicVector = this.field.basisVector(4, com.vzome.core.algebra.AlgebraicVector.X4);
            const perm: number = (element / 8|0);
            let signs: number = element % 8;
            let even: boolean = true;
            for(let c: number = 0; c < 4; c++) {{
                let source: com.vzome.core.algebra.AlgebraicNumber = model.getComponent((D4Group.D4_PERMS_$LI$()[perm][c] + 1) % 4);
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
         * @return {com.vzome.core.algebra.AlgebraicVector}
         */
        public getOrigin(): com.vzome.core.algebra.AlgebraicVector {
            return this.field.origin(4);
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

        /**
         * 
         * @return {*}
         */
        public getField(): com.vzome.core.algebra.AlgebraicField {
            return this.field;
        }

        /**
         * 
         * @param {com.vzome.core.algebra.AlgebraicVector} model
         * @param {number} element
         * @return {com.vzome.core.algebra.AlgebraicVector}
         */
        public chiralSubgroupAction(model: com.vzome.core.algebra.AlgebraicVector, element: number): com.vzome.core.algebra.AlgebraicVector {
            const result: com.vzome.core.algebra.AlgebraicVector = this.field.basisVector(4, com.vzome.core.algebra.AlgebraicVector.X4);
            const perm: number = (element / 8|0);
            if (perm >= 12)return null;
            let signs: number = element % 8;
            let even: boolean = true;
            for(let c: number = 0; c < 4; c++) {{
                let source: com.vzome.core.algebra.AlgebraicNumber = model.getComponent((D4Group.D4_PERMS_$LI$()[perm][c] + 1) % 4);
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


}

