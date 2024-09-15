/* Generated from Java with JSweet 3.2.0-SNAPSHOT - http://www.jsweet.org */
namespace com.vzome.core.math.symmetry {
    /**
     * @author Scott Vorthmann
     * @param {*} field
     * @class
     * @extends com.vzome.core.math.symmetry.AbstractSymmetry
     */
    export class DodecagonalSymmetry extends com.vzome.core.math.symmetry.AbstractSymmetry {
        static ORDER: number = 12;

        public IDENTITY: com.vzome.core.math.symmetry.Permutation;

        public constructor(field: com.vzome.core.algebra.AlgebraicField) {
            super(DodecagonalSymmetry.ORDER, field, "blue", null);
            this.IDENTITY = new com.vzome.core.math.symmetry.Permutation(this, null);
        }

        /**
         * 
         */
        createInitialPermutations() {
            this.mOrientations[0] = this.IDENTITY;
            const map: number[] = (s => { let a=[]; while(s-->0) a.push(0); return a; })(DodecagonalSymmetry.ORDER);
            for(let i: number = 0; i < DodecagonalSymmetry.ORDER; i++) {map[i] = (i + 1) % DodecagonalSymmetry.ORDER;}
            this.mOrientations[1] = new com.vzome.core.math.symmetry.Permutation(this, map);
        }

        /**
         * 
         * @param {string} frameColor
         */
        createFrameOrbit(frameColor: string) {
            const xAxis: com.vzome.core.algebra.AlgebraicVector = this.mField.createVector([[1, 1, 0, 1], [0, 1, 0, 1], [0, 1, 0, 1]]);
            const dir: com.vzome.core.math.symmetry.Direction = this.createZoneOrbit$java_lang_String$int$int$com_vzome_core_algebra_AlgebraicVector$boolean(frameColor, 0, 15, xAxis, true);
            dir.createAxis$int$int$com_vzome_core_algebra_AlgebraicVector(0, com.vzome.core.math.symmetry.Symmetry.NO_ROTATION, xAxis);
            dir.createAxis$int$int$int_A_A(1, com.vzome.core.math.symmetry.Symmetry.NO_ROTATION, [[0, 1, 1, 2], [1, 2, 0, 1], [0, 1, 0, 1]]);
            dir.createAxis$int$int$int_A_A(2, com.vzome.core.math.symmetry.Symmetry.NO_ROTATION, [[1, 2, 0, 1], [0, 1, 1, 2], [0, 1, 0, 1]]);
            dir.createAxis$int$int$int_A_A(3, com.vzome.core.math.symmetry.Symmetry.NO_ROTATION, [[0, 1, 0, 1], [1, 1, 0, 1], [0, 1, 0, 1]]);
            dir.createAxis$int$int$int_A_A(4, com.vzome.core.math.symmetry.Symmetry.NO_ROTATION, [[-1, 2, 0, 1], [0, 1, 1, 2], [0, 1, 0, 1]]);
            dir.createAxis$int$int$int_A_A(5, com.vzome.core.math.symmetry.Symmetry.NO_ROTATION, [[0, 1, -1, 2], [1, 2, 0, 1], [0, 1, 0, 1]]);
            dir.createAxis$int$int$int_A_A(6, com.vzome.core.math.symmetry.Symmetry.NO_ROTATION, [[-1, 1, 0, 1], [0, 1, 0, 1], [0, 1, 0, 1]]);
            dir.createAxis$int$int$int_A_A(7, com.vzome.core.math.symmetry.Symmetry.NO_ROTATION, [[0, 1, -1, 2], [-1, 2, 0, 1], [0, 1, 0, 1]]);
            dir.createAxis$int$int$int_A_A(8, com.vzome.core.math.symmetry.Symmetry.NO_ROTATION, [[-1, 2, 0, 1], [0, 1, -1, 2], [0, 1, 0, 1]]);
            dir.createAxis$int$int$int_A_A(9, com.vzome.core.math.symmetry.Symmetry.NO_ROTATION, [[0, 1, 0, 1], [-1, 1, 0, 1], [0, 1, 0, 1]]);
            dir.createAxis$int$int$int_A_A(10, com.vzome.core.math.symmetry.Symmetry.NO_ROTATION, [[1, 2, 0, 1], [0, 1, -1, 2], [0, 1, 0, 1]]);
            dir.createAxis$int$int$int_A_A(11, com.vzome.core.math.symmetry.Symmetry.NO_ROTATION, [[0, 1, 1, 2], [-1, 2, 0, 1], [0, 1, 0, 1]]);
            const zAxis: com.vzome.core.algebra.AlgebraicVector = this.mField.createVector([[0, 1, 0, 1], [0, 1, 0, 1], [1, 1, 0, 1]]);
            for(let p: number = 0; p < DodecagonalSymmetry.ORDER; p++) {{
                const x: number = this.mOrientations[p].mapIndex(0);
                const y: number = this.mOrientations[p].mapIndex(3);
                this.mMatrices[p] = new com.vzome.core.algebra.AlgebraicMatrix(dir.getAxis$int$int(com.vzome.core.math.symmetry.Symmetry.PLUS, x).normal(), dir.getAxis$int$int(com.vzome.core.math.symmetry.Symmetry.PLUS, y).normal(), zAxis);
                const axis: com.vzome.core.math.symmetry.Axis = dir.getAxis$int$int(com.vzome.core.math.symmetry.Symmetry.PLUS, p);
                const norm: com.vzome.core.algebra.AlgebraicVector = this.mMatrices[p].timesColumn(xAxis);
                if (!norm.equals(axis.normal()))throw new java.lang.IllegalStateException("matrix wrong: " + p);
            };}
        }

        /**
         * 
         */
        createOtherOrbits() {
            this.createZoneOrbit$java_lang_String$int$int$int_A_A$boolean("green", 0, com.vzome.core.math.symmetry.Symmetry.NO_ROTATION, [[1, 1, 1, 2], [1, 2, 0, 1], [0, 1, 0, 1]], true);
            this.createZoneOrbit$java_lang_String$int$int$int_A_A$boolean("red", 0, 1, [[0, 1, 0, 1], [0, 1, 0, 1], [1, 1, 0, 1]], true);
        }

        /**
         * 
         * @param {com.vzome.core.math.symmetry.SpecialOrbit} which
         * @return {com.vzome.core.math.symmetry.Direction}
         */
        public getSpecialOrbit(which: com.vzome.core.math.symmetry.SpecialOrbit): com.vzome.core.math.symmetry.Direction {
            switch((which)) {
            case com.vzome.core.math.symmetry.SpecialOrbit.BLUE:
                return this.getDirection("blue");
            case com.vzome.core.math.symmetry.SpecialOrbit.RED:
                return this.getDirection("red");
            case com.vzome.core.math.symmetry.SpecialOrbit.YELLOW:
                return this.getDirection("green");
            default:
                return null;
            }
        }

        /**
         * 
         * @return {com.vzome.core.math.symmetry.Axis}
         */
        public getPreferredAxis(): com.vzome.core.math.symmetry.Axis {
            return this.getDirection("red").getAxis$int$int(0, 0);
        }

        /**
         * 
         * @return {string}
         */
        public getName(): string {
            return "dodecagonal";
        }

        /**
         * 
         * @param {string} name
         * @return {int[]}
         */
        public subgroup(name: string): number[] {
            return null;
        }
    }
    DodecagonalSymmetry["__class"] = "com.vzome.core.math.symmetry.DodecagonalSymmetry";
    DodecagonalSymmetry["__interfaces"] = ["com.vzome.core.math.symmetry.Symmetry","com.vzome.core.math.symmetry.Embedding"];


}

