/* Generated from Java with JSweet 3.2.0-SNAPSHOT - http://www.jsweet.org */
namespace com.vzome.fields.heptagon {
    export class HeptagonalAntiprismSymmetry extends com.vzome.core.math.symmetry.AbstractSymmetry {
        /*private*/ sigmaX2: number;

        /*private*/ skewFactor: number;

        /*private*/ correctedOrbits: boolean;

        /*private*/ preferredAxis: com.vzome.core.math.symmetry.Axis;

        public constructor(field?: any, frameColor?: any, correctedOrbits?: any) {
            if (((field != null && (field.constructor != null && field.constructor["__interfaces"] != null && field.constructor["__interfaces"].indexOf("com.vzome.core.algebra.AlgebraicField") >= 0)) || field === null) && ((typeof frameColor === 'string') || frameColor === null) && ((typeof correctedOrbits === 'boolean') || correctedOrbits === null)) {
                let __args = arguments;
                super(14, field, frameColor, correctedOrbits ? new com.vzome.core.algebra.AlgebraicMatrix(field.basisVector(3, com.vzome.core.algebra.AlgebraicVector.X), field.basisVector(3, com.vzome.core.algebra.AlgebraicVector.Y), field.basisVector(3, com.vzome.core.algebra.AlgebraicVector.Z).negate()) : null);
                if (this.sigmaX2 === undefined) { this.sigmaX2 = 0; } 
                if (this.skewFactor === undefined) { this.skewFactor = 0; } 
                if (this.correctedOrbits === undefined) { this.correctedOrbits = false; } 
                if (this.preferredAxis === undefined) { this.preferredAxis = null; } 
                this.sigmaX2 = field.getUnitTerm(2).timesInt(2).evaluate();
                this.skewFactor = Math.sin((3.0 / 7.0) * Math.PI);
                this.correctedOrbits = correctedOrbits;
            } else if (((field != null && (field.constructor != null && field.constructor["__interfaces"] != null && field.constructor["__interfaces"].indexOf("com.vzome.core.algebra.AlgebraicField") >= 0)) || field === null) && ((typeof frameColor === 'string') || frameColor === null) && correctedOrbits === undefined) {
                let __args = arguments;
                {
                    let __args = arguments;
                    let correctedOrbits: any = false;
                    super(14, field, frameColor, correctedOrbits ? new com.vzome.core.algebra.AlgebraicMatrix(field.basisVector(3, com.vzome.core.algebra.AlgebraicVector.X), field.basisVector(3, com.vzome.core.algebra.AlgebraicVector.Y), field.basisVector(3, com.vzome.core.algebra.AlgebraicVector.Z).negate()) : null);
                    if (this.sigmaX2 === undefined) { this.sigmaX2 = 0; } 
                    if (this.skewFactor === undefined) { this.skewFactor = 0; } 
                    if (this.correctedOrbits === undefined) { this.correctedOrbits = false; } 
                    if (this.preferredAxis === undefined) { this.preferredAxis = null; } 
                    this.sigmaX2 = field.getUnitTerm(2).timesInt(2).evaluate();
                    this.skewFactor = Math.sin((3.0 / 7.0) * Math.PI);
                    this.correctedOrbits = correctedOrbits;
                }
            } else throw new Error('invalid overload');
        }

        /**
         * Called by the super constructor.
         */
        createInitialPermutations() {
            this.mOrientations[0] = new com.vzome.core.math.symmetry.Permutation(this, null);
            let map: number[] = [1, 2, 3, 4, 5, 6, 0, 8, 9, 10, 11, 12, 13, 7];
            this.mOrientations[1] = new com.vzome.core.math.symmetry.Permutation(this, map);
            map = [7, 13, 12, 11, 10, 9, 8, 0, 6, 5, 4, 3, 2, 1];
            this.mOrientations[7] = new com.vzome.core.math.symmetry.Permutation(this, map);
        }

        /**
         * 
         * @param {string} frameColor
         */
        createFrameOrbit(frameColor: string) {
            const hf: com.vzome.core.algebra.AlgebraicField = this.mField;
            const one: com.vzome.core.algebra.AlgebraicNumber = hf.one();
            const s: com.vzome.core.algebra.AlgebraicNumber = hf.getUnitTerm(2).reciprocal();
            const R: com.vzome.core.algebra.AlgebraicNumber = hf['createPower$int'](1)['times$com_vzome_core_algebra_AlgebraicNumber'](s);
            const zAxis: com.vzome.core.algebra.AlgebraicVector = hf.basisVector(3, com.vzome.core.algebra.AlgebraicVector.Z);
            const zAxisNeg: com.vzome.core.algebra.AlgebraicVector = zAxis.negate();
            const axis0: com.vzome.core.algebra.AlgebraicVector = hf.basisVector(3, com.vzome.core.algebra.AlgebraicVector.X);
            const axis1: com.vzome.core.algebra.AlgebraicVector = hf.origin(3).setComponent(com.vzome.core.algebra.AlgebraicVector.X, s).setComponent(com.vzome.core.algebra.AlgebraicVector.Y, R);
            const axis2: com.vzome.core.algebra.AlgebraicVector = hf.origin(3).setComponent(com.vzome.core.algebra.AlgebraicVector.X, s.negate()).setComponent(com.vzome.core.algebra.AlgebraicVector.Y, one);
            const axis3: com.vzome.core.algebra.AlgebraicVector = hf.origin(3).setComponent(com.vzome.core.algebra.AlgebraicVector.X, one.negate()).setComponent(com.vzome.core.algebra.AlgebraicVector.Y, s);
            const axis4: com.vzome.core.algebra.AlgebraicVector = hf.origin(3).setComponent(com.vzome.core.algebra.AlgebraicVector.X, R.negate()).setComponent(com.vzome.core.algebra.AlgebraicVector.Y, s.negate());
            const axis5: com.vzome.core.algebra.AlgebraicVector = hf.origin(3).setComponent(com.vzome.core.algebra.AlgebraicVector.Y, one.negate());
            const axis6: com.vzome.core.algebra.AlgebraicVector = hf.origin(3).setComponent(com.vzome.core.algebra.AlgebraicVector.X, R).setComponent(com.vzome.core.algebra.AlgebraicVector.Y, R.negate());
            this.mMatrices[0] = hf.identityMatrix(3);
            this.mMatrices[1] = new com.vzome.core.algebra.AlgebraicMatrix(axis1, axis6.negate(), zAxis);
            this.mMatrices[2] = new com.vzome.core.algebra.AlgebraicMatrix(axis2, axis0.negate(), zAxis);
            this.mMatrices[3] = new com.vzome.core.algebra.AlgebraicMatrix(axis3, axis1.negate(), zAxis);
            this.mMatrices[4] = new com.vzome.core.algebra.AlgebraicMatrix(axis4, axis2.negate(), zAxis);
            this.mMatrices[5] = new com.vzome.core.algebra.AlgebraicMatrix(axis5, axis3.negate(), zAxis);
            this.mMatrices[6] = new com.vzome.core.algebra.AlgebraicMatrix(axis6, axis4.negate(), zAxis);
            this.mMatrices[7] = new com.vzome.core.algebra.AlgebraicMatrix(axis0, axis2.negate(), zAxisNeg);
            this.mMatrices[8] = this.mMatrices[1].times(this.mMatrices[7]);
            this.mMatrices[9] = this.mMatrices[2].times(this.mMatrices[7]);
            this.mMatrices[10] = this.mMatrices[3].times(this.mMatrices[7]);
            this.mMatrices[11] = this.mMatrices[4].times(this.mMatrices[7]);
            this.mMatrices[12] = this.mMatrices[5].times(this.mMatrices[7]);
            this.mMatrices[13] = this.mMatrices[6].times(this.mMatrices[7]);
        }

        /**
         * 
         */
        createOtherOrbits() {
        }

        public createStandardOrbits(frameColor: string): HeptagonalAntiprismSymmetry {
            const redOrbit: com.vzome.core.math.symmetry.Direction = this.createZoneOrbit$java_lang_String$int$int$com_vzome_core_algebra_AlgebraicVector$boolean("red", 0, 1, this.mField.basisVector(3, com.vzome.core.algebra.AlgebraicVector.Z), true);
            this.preferredAxis = redOrbit.getAxis$int$int(com.vzome.core.math.symmetry.Symmetry.PLUS, 0);
            const blueFrameVector: com.vzome.core.algebra.AlgebraicVector = this.mField.basisVector(3, com.vzome.core.algebra.AlgebraicVector.X);
            const blueOrbit: com.vzome.core.math.symmetry.Direction = this.createZoneOrbit$java_lang_String$int$int$com_vzome_core_algebra_AlgebraicVector$boolean(frameColor, 0, 7, blueFrameVector, true);
            const blueRotatedVector: com.vzome.core.algebra.AlgebraicVector = blueOrbit.getAxis$int$int(com.vzome.core.math.symmetry.Symmetry.PLUS, ((7 + 1) / 2|0)).normal();
            const greenVector: com.vzome.core.algebra.AlgebraicVector = blueFrameVector.minus(blueRotatedVector);
            this.createZoneOrbit$java_lang_String$int$int$com_vzome_core_algebra_AlgebraicVector("green", 0, 7, greenVector);
            return this;
        }

        /**
         * 
         * @return {com.vzome.core.math.symmetry.Axis}
         */
        public getPreferredAxis(): com.vzome.core.math.symmetry.Axis {
            return this.preferredAxis;
        }

        /**
         * 
         * @param {com.vzome.core.algebra.AlgebraicVector} v
         * @return {com.vzome.core.math.RealVector}
         */
        public embedInR3(v: com.vzome.core.algebra.AlgebraicVector): com.vzome.core.math.RealVector {
            const rv: com.vzome.core.math.RealVector = super.embedInR3(v);
            const x: number = rv.x + (rv.y / this.sigmaX2);
            const y: number = rv.y * this.skewFactor;
            return new com.vzome.core.math.RealVector(x, y, rv.z);
        }

        /**
         * 
         * @param {com.vzome.core.algebra.AlgebraicVector} v
         * @return {double[]}
         */
        public embedInR3Double(v: com.vzome.core.algebra.AlgebraicVector): number[] {
            const dv: number[] = super.embedInR3Double(v);
            const x: number = dv[0] + (dv[1] / this.sigmaX2);
            const y: number = dv[1] * this.skewFactor;
            return [x, y, dv[2]];
        }

        /**
         * 
         * @return {boolean}
         */
        public isTrivial(): boolean {
            return false;
        }

        /**
         * 
         * @return {string}
         */
        public getName(): string {
            if (this.correctedOrbits)return "heptagonal antiprism corrected"; else return "heptagonal antiprism";
        }

        /**
         * 
         * @param {string} name
         * @return {int[]}
         */
        public subgroup(name: string): number[] {
            return null;
        }

        /**
         * 
         * @return {com.vzome.core.algebra.AlgebraicVector[]}
         */
        public getOrbitTriangle(): com.vzome.core.algebra.AlgebraicVector[] {
            const field: com.vzome.core.algebra.AlgebraicField = this.getField();
            const zero: com.vzome.core.algebra.AlgebraicNumber = field.zero();
            let x: com.vzome.core.algebra.AlgebraicNumber = field['createAlgebraicNumber$int_A']([0, -1, -1]).dividedBy(field['createAlgebraicNumber$int_A']([0, 0, 2]));
            const orthoVertex: com.vzome.core.algebra.AlgebraicVector = new com.vzome.core.algebra.AlgebraicVector(x, zero, zero);
            const sideVertex: com.vzome.core.algebra.AlgebraicVector = field.basisVector(3, com.vzome.core.algebra.AlgebraicVector.Z);
            x = field['createRational$long'](-1);
            const y: com.vzome.core.algebra.AlgebraicNumber = field['createAlgebraicNumber$int_A']([0, -1, 1]);
            const topVertex: com.vzome.core.algebra.AlgebraicVector = new com.vzome.core.algebra.AlgebraicVector(x, y, zero);
            return [orthoVertex, sideVertex, topVertex];
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
                return this.getDirection("blue");
            default:
                return null;
            }
        }
    }
    HeptagonalAntiprismSymmetry["__class"] = "com.vzome.fields.heptagon.HeptagonalAntiprismSymmetry";
    HeptagonalAntiprismSymmetry["__interfaces"] = ["com.vzome.core.math.symmetry.Symmetry","com.vzome.core.math.symmetry.Embedding"];


}

