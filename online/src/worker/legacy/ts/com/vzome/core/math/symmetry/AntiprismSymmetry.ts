/* Generated from Java with JSweet 3.2.0-SNAPSHOT - http://www.jsweet.org */
namespace com.vzome.core.math.symmetry {
    /**
     * @author David Hall
     * This class is a generalized implementation initially based on the HeptagonalAntiprismSymmetry by Scott Vorthmann
     * @param {com.vzome.core.algebra.PolygonField} field
     * @class
     * @extends com.vzome.core.math.symmetry.AbstractSymmetry
     */
    export class AntiprismSymmetry extends com.vzome.core.math.symmetry.AbstractSymmetry {
        /*private*/ preferredAxis: com.vzome.core.math.symmetry.Axis;

        /*private*/ useShear: boolean;

        /*private*/ shearTransform: com.vzome.core.math.RealVector[];

        public constructor(field: com.vzome.core.algebra.PolygonField) {
            super(field.polygonSides() * 2, field, "blue", field.isEven() ? null : new com.vzome.core.algebra.AlgebraicMatrix(field.basisVector(3, com.vzome.core.algebra.AlgebraicVector.X), field.basisVector(3, com.vzome.core.algebra.AlgebraicVector.Y), field.basisVector(3, com.vzome.core.algebra.AlgebraicVector.Z).negate()));
            if (this.preferredAxis === undefined) { this.preferredAxis = null; }
            if (this.useShear === undefined) { this.useShear = false; }
            if (this.shearTransform === undefined) { this.shearTransform = null; }
            this.rotationMatrix = null;
            const nSides: number = field.polygonSides();
            let m10: number = 0;
            let m11: number = 1;
            this.useShear = field.isOdd();
            if (this.useShear){
                m10 = field.getUnitDiagonal(field.diagonalCount() - 1).reciprocal().evaluate() / 2.0;
                m11 = Math.cos(Math.PI / (2.0 * nSides));
            }
            this.shearTransform = [new com.vzome.core.math.RealVector(1, m10, 0), new com.vzome.core.math.RealVector(0, m11, 0), new com.vzome.core.math.RealVector(0, 0, 1)];
        }

        /**
         * 
         * @return {com.vzome.core.algebra.PolygonField}
         */
        public getField(): com.vzome.core.algebra.PolygonField {
            return <com.vzome.core.algebra.PolygonField><any>super.getField();
        }

        /**
         * Called by the super constructor.
         */
        createInitialPermutations() {
            const nSides: number = this.getField().polygonSides();
            this.mOrientations[0] = new com.vzome.core.math.symmetry.Permutation(this, null);
            const map1: number[] = (s => { let a=[]; while(s-->0) a.push(0); return a; })(nSides * 2);
            for(let i: number = 0; i < nSides; i++) {{
                map1[i] = (i + 1) % nSides;
                map1[i + nSides] = map1[i] + nSides;
            };}
            this.mOrientations[1] = new com.vzome.core.math.symmetry.Permutation(this, map1);
            const map2: number[] = (s => { let a=[]; while(s-->0) a.push(0); return a; })(map1.length);
            let n: number = nSides * 2;
            for(let i: number = 0; i < map2.length; i++) {{
                n--;
                map2[i] = map1[n];
            };}
            this.mOrientations[nSides] = new com.vzome.core.math.symmetry.Permutation(this, map2);
        }

        /**
         * 
         * @param {string} frameColor
         */
        createFrameOrbit(frameColor: string) {
            const field: com.vzome.core.algebra.PolygonField = this.getField();
            const nSides: number = field.polygonSides();
            const nDiags: number = field.diagonalCount();
            const rotationMatrix: com.vzome.core.algebra.AlgebraicMatrix = this.getRotationMatrix();
            let vX: com.vzome.core.algebra.AlgebraicVector = field.basisVector(3, com.vzome.core.algebra.AlgebraicVector.X);
            let vY: com.vzome.core.algebra.AlgebraicVector = field.basisVector(3, com.vzome.core.algebra.AlgebraicVector.Y);
            let vZ: com.vzome.core.algebra.AlgebraicVector = field.basisVector(3, com.vzome.core.algebra.AlgebraicVector.Z);
            for(let i: number = 0; i < nSides * 2; i++) {{
                if (i === nSides){
                    vY = vY.negate();
                    if (field.isOdd()){
                        vY = vY.setComponent(com.vzome.core.algebra.AlgebraicVector.X, field.getUnitDiagonal(nDiags - 1).reciprocal());
                    }
                    vZ = vZ.negate();
                }
                this.mMatrices[i] = new com.vzome.core.algebra.AlgebraicMatrix(vX, vY, vZ);
                vX = rotationMatrix.timesColumn(vX);
                vY = rotationMatrix.timesColumn(vY);
            };}
        }

        /*private*/ rotationMatrix: com.vzome.core.algebra.AlgebraicMatrix;

        public getRotationMatrix(): com.vzome.core.algebra.AlgebraicMatrix {
            if (this.rotationMatrix == null){
                const field: com.vzome.core.algebra.PolygonField = this.getField();
                const diagCount: number = field.diagonalCount();
                const p_x: com.vzome.core.algebra.AlgebraicNumber = field.getUnitDiagonal(diagCount - 3);
                const q_y: com.vzome.core.algebra.AlgebraicNumber = field.getUnitDiagonal(diagCount - (field.isEven() ? 3 : 2));
                const den: com.vzome.core.algebra.AlgebraicNumber = field.getUnitDiagonal(diagCount - 1);
                const num: com.vzome.core.algebra.AlgebraicNumber = field.getUnitDiagonal(1);
                const p: com.vzome.core.algebra.AlgebraicVector = field.origin(3).setComponent(com.vzome.core.algebra.AlgebraicVector.X, p_x.dividedBy(den)).setComponent(com.vzome.core.algebra.AlgebraicVector.Y, num.dividedBy(den));
                const q: com.vzome.core.algebra.AlgebraicVector = field.origin(3).setComponent(com.vzome.core.algebra.AlgebraicVector.X, num.dividedBy(den).negate()).setComponent(com.vzome.core.algebra.AlgebraicVector.Y, q_y.dividedBy(den));
                const zAxis: com.vzome.core.algebra.AlgebraicVector = field.basisVector(3, com.vzome.core.algebra.AlgebraicVector.Z);
                this.rotationMatrix = new com.vzome.core.algebra.AlgebraicMatrix(p, q, zAxis);
            }
            return this.rotationMatrix;
        }

        /**
         * 
         */
        createOtherOrbits() {
        }

        public createStandardOrbits(frameColor: string): AntiprismSymmetry {
            const redOrbit: com.vzome.core.math.symmetry.Direction = this.createZoneOrbit$java_lang_String$int$int$com_vzome_core_algebra_AlgebraicVector$boolean("red", 0, 1, this.mField.basisVector(3, com.vzome.core.algebra.AlgebraicVector.Z), true);
            this.preferredAxis = redOrbit.getAxis$int$int(com.vzome.core.math.symmetry.Symmetry.PLUS, 0);
            const blueFrameVector: com.vzome.core.algebra.AlgebraicVector = this.mField.basisVector(3, com.vzome.core.algebra.AlgebraicVector.X);
            const nSides: number = this.getField().polygonSides();
            const blueOrbit: com.vzome.core.math.symmetry.Direction = this.createZoneOrbit$java_lang_String$int$int$com_vzome_core_algebra_AlgebraicVector$boolean(frameColor, 0, nSides, blueFrameVector, true);
            let greenVector: com.vzome.core.algebra.AlgebraicVector;
            if (this.getField().isOdd()){
                const blueRotatedVector: com.vzome.core.algebra.AlgebraicVector = blueOrbit.getAxis$int$int(com.vzome.core.math.symmetry.Symmetry.PLUS, ((nSides + 1) / 2|0)).normal();
                greenVector = blueFrameVector.minus(blueRotatedVector);
            } else {
                const blueRotatedVector: com.vzome.core.algebra.AlgebraicVector = blueOrbit.getAxis$int$int(com.vzome.core.math.symmetry.Symmetry.PLUS, 1).normal();
                greenVector = blueFrameVector.plus(blueRotatedVector);
            }
            this.createZoneOrbit$java_lang_String$int$int$com_vzome_core_algebra_AlgebraicVector$boolean("green", 0, nSides, greenVector, false);
            const yellowVector: com.vzome.core.algebra.AlgebraicVector = greenVector.plus(redOrbit.getAxis$int$int(com.vzome.core.math.symmetry.Symmetry.PLUS, 1).normal());
            this.createZoneOrbit$java_lang_String$int$int$com_vzome_core_algebra_AlgebraicVector$boolean("yellow", 0, nSides, yellowVector, false);
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
            if (this.useShear){
                const sums: number[] = [0, 0, 0];
                for(let i: number = 0; i < this.shearTransform.length; i++) {{
                    sums[i] += (<any>Math).fround(this.shearTransform[i].x * rv.x);
                    sums[i] += (<any>Math).fround(this.shearTransform[i].y * rv.y);
                    sums[i] += (<any>Math).fround(this.shearTransform[i].z * rv.z);
                };}
                return new com.vzome.core.math.RealVector(sums[0], sums[1], sums[2]);
            }
            return rv;
        }

        /**
         * 
         * @param {com.vzome.core.algebra.AlgebraicVector} v
         * @return {double[]}
         */
        public embedInR3Double(v: com.vzome.core.algebra.AlgebraicVector): number[] {
            const dv: number[] = super.embedInR3Double(v);
            if (this.useShear){
                const sums: number[] = [0, 0, 0];
                for(let i: number = 0; i < this.shearTransform.length; i++) {{
                    sums[i] += this.shearTransform[i].x * dv[0];
                    sums[i] += this.shearTransform[i].y * dv[1];
                    sums[i] += this.shearTransform[i].z * dv[2];
                };}
                return sums;
            }
            return dv;
        }

        /**
         * 
         * @return {boolean}
         */
        public isTrivial(): boolean {
            return !this.useShear;
        }

        /**
         * 
         * @return {string}
         */
        public getName(): string {
            return ("antiprism" + this.getField().polygonSides());
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
         * These three vertices represent the corners of the canonical orbit triangle.
         * They must correspond to the three "special" orbits returned by getSpecialOrbit().
         * All other canonical direction prototype vectors
         * must intersect this plane at a unique point within the triangle.
         * 
         * OrbitDotLocator will use the three vectors to locate the dots in this order:
         * AlgebraicVector[] triangle = getOrbitTriangle();
         * triangle[0] .. // SpecialOrbit.BLUE   = orthoVertex
         * triangle[1] .. // SpecialOrbit.RED    = sideVertex
         * triangle[2] .. // SpecialOrbit.YELLOW = topVertex
         * 
         * These variable names and their position in the array
         * correspond to the positions where they will be shown in the orbit triangle
         * rather than any specific colors.
         * The SpecialOrbit names originally matched the color position in the icosa orbit triangle
         * but other symmetries don't necessarily have any such corellation.
         * 
         * top
         * @
         * | `\
         * |    `\
         * @-------`@
         * ortho     side
         * 
         * AntiprismTrackball also uses these 3 vertices to locate the trackball orbit triangle hints.
         * @return {com.vzome.core.algebra.AlgebraicVector[]}
         */
        public getOrbitTriangle(): com.vzome.core.algebra.AlgebraicVector[] {
            const field: com.vzome.core.algebra.PolygonField = this.getField();
            const diagCount: number = field.diagonalCount();
            const sideVertex: com.vzome.core.algebra.AlgebraicVector = field.basisVector(3, com.vzome.core.algebra.AlgebraicVector.Z);
            const blueOrbit: com.vzome.core.math.symmetry.Direction = this.getSpecialOrbit(com.vzome.core.math.symmetry.SpecialOrbit.BLUE);
            const topVertex: com.vzome.core.algebra.AlgebraicVector = blueOrbit.getAxis$int$int(com.vzome.core.math.symmetry.Symmetry.PLUS, diagCount).normal();
            const bottomVert: com.vzome.core.algebra.AlgebraicVector = blueOrbit.getAxis$int$int(com.vzome.core.math.symmetry.Symmetry.PLUS, diagCount + 1).normal();
            const orthoVertex: com.vzome.core.algebra.AlgebraicVector = com.vzome.core.algebra.AlgebraicVectors.getCentroid([topVertex, bottomVert]);
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
                return this.getDirection(this.getField().isEven() ? "green" : "blue");
            default:
                return null;
            }
        }
    }
    AntiprismSymmetry["__class"] = "com.vzome.core.math.symmetry.AntiprismSymmetry";
    AntiprismSymmetry["__interfaces"] = ["com.vzome.core.math.symmetry.Symmetry","com.vzome.core.math.symmetry.Embedding"];


}

