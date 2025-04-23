/* Generated from Java with JSweet 3.2.0-SNAPSHOT - http://www.jsweet.org */
namespace com.vzome.core.math.symmetry {
    /**
     * @author Scott Vorthmann
     * @param {*} field
     * @class
     * @extends com.vzome.core.math.symmetry.AbstractSymmetry
     */
    export class IcosahedralSymmetry extends com.vzome.core.math.symmetry.AbstractSymmetry {
        /*private*/ INCIDENCES: number[][];

        public IDENTITY: com.vzome.core.math.symmetry.Permutation;

        /*private*/ preferredAxis: com.vzome.core.math.symmetry.Axis;

        public constructor(field: com.vzome.core.algebra.AlgebraicField) {
            super(60, field, "blue", null);
            this.INCIDENCES = <any> (function(dims) { let allocate = function(dims) { if (dims.length === 0) { return 0; } else { let array = []; for(let i = 0; i < dims[0]; i++) { array.push(allocate(dims.slice(1))); } return array; }}; return allocate(dims);})([60, 3]);
            this.IDENTITY = new com.vzome.core.math.symmetry.Permutation(this, null);
            if (this.preferredAxis === undefined) { this.preferredAxis = null; }
            this.tetrahedralSubgroup = [null, null, null, null, null];
            this.blueTetrahedral = (s => { let a=[]; while(s-->0) a.push(0); return a; })(60);
            this.greenTetrahedral = (s => { let a=[]; while(s-->0) a.push(0); return a; })(60);
            this.yellowTetrahedral = (s => { let a=[]; while(s-->0) a.push(0); return a; })(60);
            for(let i: number = 0; i < this.INCIDENCES.length; i++) {{
                this.INCIDENCES[i][0] = this.getPermutation(i).mapIndex(30);
                this.INCIDENCES[i][1] = this.getPermutation(i).mapIndex(45);
                this.INCIDENCES[i][2] = this.getPermutation(i).mapIndex(42);
            };}
            this.tetrahedralSubgroup[0] = this.closure([1, 15]);
            this.tetrahedralSubgroup[1] = this.closure([11, 20]);
            this.tetrahedralSubgroup[2] = this.closure([27, 58]);
            this.tetrahedralSubgroup[3] = this.closure([17, 4]);
            this.tetrahedralSubgroup[4] = this.closure([55, 14]);
            const blueOrbit: com.vzome.core.math.symmetry.Direction = this.getDirection("blue");
            const yellowOrbit: com.vzome.core.math.symmetry.Direction = this.getDirection("yellow");
            for(let i: number = 0; i < 60; i++) {{
                const blueZone: com.vzome.core.math.symmetry.Axis = blueOrbit.getAxis$int$int(com.vzome.core.math.symmetry.Symmetry.PLUS, i);
                const yellowZone: com.vzome.core.math.symmetry.Axis = yellowOrbit.getAxis$int$int(com.vzome.core.math.symmetry.Symmetry.PLUS, i);
                for(let j: number = 0; j < this.tetrahedralSubgroup.length; j++) {{
                    for(let k: number = 0; k < this.tetrahedralSubgroup[j].length; k++) {{
                        if (this.tetrahedralSubgroup[j][k] === blueZone.getRotation())this.blueTetrahedral[i] = j;
                        if (this.tetrahedralSubgroup[j][k] === yellowZone.getRotation())this.yellowTetrahedral[i] = j;
                    };}
                };}
            };}
            const greenSeeds: number[] = [6, 9, 12, 0, 3];
            for(let j: number = 0; j < this.tetrahedralSubgroup.length; j++) {{
                const seedAxis: number = greenSeeds[j];
                for(let k: number = 0; k < this.tetrahedralSubgroup[j].length; k++) {{
                    const mappedAxis: number = this.mOrientations[this.tetrahedralSubgroup[j][k]].mapIndex(seedAxis);
                    this.greenTetrahedral[mappedAxis] = j;
                };}
            };}
        }

        /**
         * 
         * @param {number} orientation
         * @return {int[]}
         */
        public getIncidentOrientations(orientation: number): number[] {
            return this.INCIDENCES[orientation];
        }

        /**
         * Create a collection of blue-axis normals from a prototype,
         * by applying known rotations.
         * @param {com.vzome.core.math.symmetry.Direction} dir
         * @param {number} prototype
         * @param {number} rotated
         * @param {com.vzome.core.algebra.AlgebraicVector} xyz
         * @private
         */
        /*private*/ createBlueAxes(dir: com.vzome.core.math.symmetry.Direction, prototype: number, rotated: number, xyz: com.vzome.core.algebra.AlgebraicVector) {
            let orientation: number = 0;
            const reflect: boolean[] = [false, false, false];
            for(let i: number = 0; i < 3; i++) {{
                for(let k: number = 0; k < 2; k++) {{
                    for(let l: number = 0; l < 2; l++) {{
                        const unit: number = this.mOrientations[orientation].mapIndex(prototype);
                        if (!dir.zoneInitialized(com.vzome.core.math.symmetry.Symmetry.PLUS, unit)){
                            const rot: number = this.mOrientations[orientation].mapIndex(rotated);
                            const rotation: number = this.getMapping(unit, rot);
                            const norm: com.vzome.core.algebra.AlgebraicVector = this.mField.origin(3);
                            for(let m: number = 0; m < 3; m++) {{
                                const offset: number = ((m + 3 - i) % 3);
                                if (reflect[m]){
                                    norm.setComponent(m, xyz.getComponent(offset).negate());
                                } else {
                                    norm.setComponent(m, xyz.getComponent(offset));
                                }
                            };}
                            dir.createAxis$int$int$com_vzome_core_algebra_AlgebraicVector(unit, rotation, norm);
                            dir.createAxis$int$int$com_vzome_core_algebra_AlgebraicVector(rot, rotation, norm);
                        }
                        orientation = this.mOrientations[45].mapIndex(orientation);
                        reflect[0] = !reflect[0];
                        reflect[2] = !reflect[2];
                    };}
                    orientation = this.mOrientations[15].mapIndex(orientation);
                    reflect[1] = !reflect[1];
                    reflect[2] = !reflect[2];
                };}
                orientation = this.mOrientations[1].mapIndex(orientation);
            };}
        }

        /**
         * 
         * @return {string}
         */
        public getName(): string {
            return "icosahedral";
        }

        /**
         * 
         * @return {com.vzome.core.algebra.AlgebraicVector[]}
         */
        public getOrbitTriangle(): com.vzome.core.algebra.AlgebraicVector[] {
            const twice: com.vzome.core.algebra.AlgebraicNumber = this.mField['createRational$long'](2);
            const blueVertex: com.vzome.core.algebra.AlgebraicVector = this.getDirection("blue").getPrototype().scale(twice);
            const redVertex: com.vzome.core.algebra.AlgebraicVector = this.getDirection("red").getPrototype();
            const phiInv: com.vzome.core.algebra.AlgebraicNumber = this.mField.getGoldenRatio().reciprocal();
            const yellowVertex: com.vzome.core.algebra.AlgebraicVector = this.getDirection("yellow").getPrototype().scale(phiInv);
            return [blueVertex, redVertex, yellowVertex];
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
                return this.getDirection("yellow");
            default:
                return this.getDirection("black");
            }
        }

        /**
         * 
         * @param {string} frameColor
         */
        createFrameOrbit(frameColor: string) {
            const xAxis: com.vzome.core.algebra.AlgebraicVector = this.mField.basisVector(3, com.vzome.core.algebra.AlgebraicVector.X);
            const dir: com.vzome.core.math.symmetry.Direction = this.createZoneOrbit$java_lang_String$int$int$com_vzome_core_algebra_AlgebraicVector$boolean$boolean$com_vzome_core_algebra_AlgebraicNumber(frameColor, 0, 15, xAxis, true, true, this.mField['createRational$long'](2));
            dir.setScaleNames(["b0", "b1", "b2", "b3"]);
            this.createBlueAxes(dir, 0, 15, xAxis);
            this.createBlueAxes(dir, 9, 13, this.mField.createVector([[1, 2, 0, 1], [0, 1, 1, 2], [-1, 2, 1, 2]]));
            this.createBlueAxes(dir, 6, 49, this.mField.createVector([[1, 2, 0, 1], [0, 1, 1, 2], [1, 2, -1, 2]]));
            for(let p: number = 0; p < this.mOrientations.length; p++) {{
                const x: number = this.mOrientations[p].mapIndex(0);
                const y: number = this.mOrientations[p].mapIndex(1);
                const z: number = this.mOrientations[p].mapIndex(2);
                this.mMatrices[p] = new com.vzome.core.algebra.AlgebraicMatrix(dir.getAxis$int$int(com.vzome.core.math.symmetry.Symmetry.PLUS, x).normal(), dir.getAxis$int$int(com.vzome.core.math.symmetry.Symmetry.PLUS, y).normal(), dir.getAxis$int$int(com.vzome.core.math.symmetry.Symmetry.PLUS, z).normal());
                const axis: com.vzome.core.math.symmetry.Axis = dir.getAxis$int$int(com.vzome.core.math.symmetry.Symmetry.PLUS, p);
                const norm: com.vzome.core.algebra.AlgebraicVector = this.mMatrices[p].timesColumn(xAxis);
                if (!norm.equals(axis.normal()))throw new java.lang.IllegalStateException("matrix wrong: " + p);
            };}
        }

        /**
         * 
         * @return {com.vzome.core.math.symmetry.Axis}
         */
        public getPreferredAxis(): com.vzome.core.math.symmetry.Axis {
            return this.preferredAxis;
        }

        /**
         * @see com.vzome.core.math.symmetry.AbstractSymmetry#createOtherOrbits()
         * 
         * @see com.vzome.core.algebra.AlgebraicField#createVector()
         * 
         * @see com.vzome.core.math.symmetry.AbstractSymmetry#createZoneOrbit(String, int, int, AlgebraicVector, boolean, boolean, AlgebraicNumber)
         */
        createOtherOrbits() {
            const yellowOrbit: com.vzome.core.math.symmetry.Direction = this.createZoneOrbit$java_lang_String$int$int$com_vzome_core_algebra_AlgebraicVector$boolean$boolean$com_vzome_core_algebra_AlgebraicNumber("yellow", 0, 27, this.mField.createVector([[1, 1, 1, 1], [0, 1, 0, 1], [-1, 1, 0, 1]]), true, false, this.mField['createPower$int'](-1));
            yellowOrbit.setScaleNames(["y0", "y1", "y2", "y3"]);
            yellowOrbit.iterator();
            const redOrbit: com.vzome.core.math.symmetry.Direction = this.createZoneOrbit$java_lang_String$int$int$com_vzome_core_algebra_AlgebraicVector$boolean("red", 0, 3, this.mField.createVector([[0, 1, 1, 1], [1, 1, 0, 1], [0, 1, 0, 1]]), true);
            redOrbit.setScaleNames(["r0", "r1", "r2", "r3"]);
            this.preferredAxis = redOrbit.getAxis$int$int(com.vzome.core.math.symmetry.Symmetry.PLUS, 1);
            const dir: com.vzome.core.math.symmetry.Direction = this.createZoneOrbit$java_lang_String$int$int$com_vzome_core_algebra_AlgebraicVector$boolean$boolean$com_vzome_core_algebra_AlgebraicNumber("green", 6, com.vzome.core.math.symmetry.Symmetry.NO_ROTATION, this.mField.createVector([[1, 1, 0, 1], [1, 1, 0, 1], [0, 1, 0, 1]]), true, true, this.mField['createRational$long'](2));
            dir.setScaleNames(["g0", "g1", "g2", "g3"]);
            this.createZoneOrbit$java_lang_String$int$int$com_vzome_core_algebra_AlgebraicVector("orange", 6, com.vzome.core.math.symmetry.Symmetry.NO_ROTATION, this.mField.createVector([[1, 1, 0, 1], [0, 1, 1, 1], [0, 1, 0, 1]]));
            this.createZoneOrbit$java_lang_String$int$int$com_vzome_core_algebra_AlgebraicVector$boolean$boolean$com_vzome_core_algebra_AlgebraicNumber("purple", 0, com.vzome.core.math.symmetry.Symmetry.NO_ROTATION, this.mField.createVector([[1, 1, 1, 1], [1, 1, 0, 1], [0, 1, 0, 1]]), false, false, this.mField['createPower$int'](-1));
            this.createZoneOrbit$java_lang_String$int$int$com_vzome_core_algebra_AlgebraicVector("black", 3, com.vzome.core.math.symmetry.Symmetry.NO_ROTATION, this.mField.createVector([[0, 1, 1, 1], [1, 1, 0, 1], [1, 1, -1, 1]]));
            this.createZoneOrbit$java_lang_String$int$int$com_vzome_core_algebra_AlgebraicVector("lavender", 0, com.vzome.core.math.symmetry.Symmetry.NO_ROTATION, this.mField.createVector([[2, 1, -1, 1], [0, 1, 1, 1], [2, 1, -1, 1]])).withCorrection();
            this.createZoneOrbit$java_lang_String$int$int$com_vzome_core_algebra_AlgebraicVector("olive", 0, com.vzome.core.math.symmetry.Symmetry.NO_ROTATION, this.mField.createVector([[0, 1, 1, 1], [0, 1, 1, 1], [2, 1, -1, 1]])).withCorrection();
            this.createZoneOrbit$java_lang_String$int$int$com_vzome_core_algebra_AlgebraicVector("maroon", 0, com.vzome.core.math.symmetry.Symmetry.NO_ROTATION, this.mField.createVector([[-1, 1, 1, 1], [3, 1, -1, 1], [1, 1, -1, 1]])).withCorrection();
            this.createZoneOrbit$java_lang_String$int$int$com_vzome_core_algebra_AlgebraicVector("rose", 0, com.vzome.core.math.symmetry.Symmetry.NO_ROTATION, this.mField.createVector([[2, 1, -1, 1], [-1, 1, 2, 1], [0, 1, 0, 1]])).withCorrection();
            this.createZoneOrbit$java_lang_String$int$int$com_vzome_core_algebra_AlgebraicVector$boolean$boolean$com_vzome_core_algebra_AlgebraicNumber("navy", 0, com.vzome.core.math.symmetry.Symmetry.NO_ROTATION, this.mField.createVector([[-1, 1, 2, 1], [1, 1, 1, 1], [0, 1, 0, 1]]), false, false, this.mField['createPower$int'](-1)).withCorrection();
            this.createZoneOrbit$java_lang_String$int$int$com_vzome_core_algebra_AlgebraicVector("turquoise", 0, com.vzome.core.math.symmetry.Symmetry.NO_ROTATION, this.mField.createVector([[2, 1, 0, 1], [2, 1, -1, 1], [-3, 1, 2, 1]])).withCorrection();
            this.createZoneOrbit$java_lang_String$int$int$com_vzome_core_algebra_AlgebraicVector("coral", 0, com.vzome.core.math.symmetry.Symmetry.NO_ROTATION, this.mField.createVector([[-3, 1, 3, 1], [0, 1, 0, 1], [1, 1, 0, 1]])).withCorrection();
            this.createZoneOrbit$java_lang_String$int$int$com_vzome_core_algebra_AlgebraicVector("sulfur", 0, com.vzome.core.math.symmetry.Symmetry.NO_ROTATION, this.mField.createVector([[-3, 1, 3, 1], [2, 1, -1, 1], [0, 1, 0, 1]])).withCorrection();
            this.createZoneOrbit$java_lang_String$int$int$com_vzome_core_algebra_AlgebraicVector("sand", 0, com.vzome.core.math.symmetry.Symmetry.NO_ROTATION, this.mField.createVector([[-2, 1, 2, 1], [-2, 1, 2, 1], [2, 1, 0, 1]])).withCorrection();
            this.createZoneOrbit$java_lang_String$int$int$com_vzome_core_algebra_AlgebraicVector("apple", 0, com.vzome.core.math.symmetry.Symmetry.NO_ROTATION, this.mField.createVector([[5, 1, -3, 1], [1, 1, 0, 1], [0, 1, 1, 1]])).withCorrection();
            this.createZoneOrbit$java_lang_String$int$int$com_vzome_core_algebra_AlgebraicVector("cinnamon", 0, com.vzome.core.math.symmetry.Symmetry.NO_ROTATION, this.mField.createVector([[5, 1, -3, 1], [2, 1, -1, 1], [2, 1, 0, 1]])).withCorrection();
            this.createZoneOrbit$java_lang_String$int$int$com_vzome_core_algebra_AlgebraicVector("spruce", 0, com.vzome.core.math.symmetry.Symmetry.NO_ROTATION, this.mField.createVector([[-3, 1, 2, 1], [-3, 1, 2, 1], [5, 1, -2, 1]])).withCorrection();
            this.createZoneOrbit$java_lang_String$int$int$com_vzome_core_algebra_AlgebraicVector("brown", 0, com.vzome.core.math.symmetry.Symmetry.NO_ROTATION, this.mField.createVector([[-1, 1, 1, 1], [-1, 1, 1, 1], [-2, 1, 2, 1]])).withCorrection();
        }

        /**
         * 
         */
        createInitialPermutations() {
            const ORDER: number = 60;
            this.mOrientations[0] = this.IDENTITY;
            let map: number[] = (s => { let a=[]; while(s-->0) a.push(0); return a; })(ORDER);
            for(let i: number = 0; i < 15; i++) {{
                map[i] = i + 15;
                map[i + 15] = i;
                map[i + 30] = i + 45;
                map[i + 45] = i + 30;
            };}
            this.mOrientations[15] = new com.vzome.core.math.symmetry.Permutation(this, map);
            map = (s => { let a=[]; while(s-->0) a.push(0); return a; })(ORDER);
            const starts: number[][] = [[0, 1, 2], [15, 46, 32], [16, 47, 30], [17, 45, 31]];
            for(let index = 0; index < starts.length; index++) {
                let start = starts[index];
                {
                    for(let j: number = 0; j < start.length; j++) {{
                        for(let k: number = 0; k < 5; k++) {{
                            map[start[j] + k * 3] = start[(j + 1) % 3] + k * 3;
                        };}
                    };}
                }
            }
            this.mOrientations[1] = new com.vzome.core.math.symmetry.Permutation(this, map);
            map = (s => { let a=[]; while(s-->0) a.push(0); return a; })(ORDER);
            const cycles: number[][] = [[0, 3, 6, 9, 12], [30, 42, 39, 36, 33], [2, 21, 29, 55, 4], [5, 24, 17, 58, 7], [8, 27, 20, 46, 10], [11, 15, 23, 49, 13], [1, 14, 18, 26, 52], [16, 50, 57, 38, 40], [19, 53, 45, 41, 43], [22, 56, 48, 44, 31], [25, 59, 51, 32, 34], [28, 47, 54, 35, 37]];
            for(let index = 0; index < cycles.length; index++) {
                let cycle = cycles[index];
                {
                    for(let j: number = 0; j < cycle.length; j++) {{
                        map[cycle[j]] = cycle[(j + 1) % 5];
                    };}
                }
            }
            this.mOrientations[3] = new com.vzome.core.math.symmetry.Permutation(this, map);
        }

        /*private*/ tetrahedralSubgroup: number[][];

        /*private*/ blueTetrahedral: number[];

        /*private*/ greenTetrahedral: number[];

        /*private*/ yellowTetrahedral: number[];

        public subgroup$java_lang_String(name: string): number[] {
            if (com.vzome.core.math.symmetry.Symmetry.TETRAHEDRAL === name)return this.tetrahedralSubgroup[0];
            return null;
        }

        /**
         * 
         * @param {string} color
         * @return {com.vzome.core.math.symmetry.Direction}
         */
        public getDirection(color: string): com.vzome.core.math.symmetry.Direction {
            if ("spring" === color)color = "apple";
            if ("tan" === color)color = "sand";
            return super.getDirection(color);
        }

        public subgroup$java_lang_String$com_vzome_core_math_symmetry_Axis(name: string, zone: com.vzome.core.math.symmetry.Axis): number[] {
            return this.subgroup$java_lang_String$com_vzome_core_math_symmetry_Axis$boolean(name, zone, true);
        }

        public subgroup$java_lang_String$com_vzome_core_math_symmetry_Axis$boolean(name: string, zone: com.vzome.core.math.symmetry.Axis, allowYellow: boolean): number[] {
            const orientation: number = zone.getOrientation();
            const orbit: com.vzome.core.math.symmetry.Direction = zone.getDirection();
            const orbitName: string = orbit.getName();
            if (orbitName === ("blue")){
                const subgroup: number = this.blueTetrahedral[orientation];
                return this.tetrahedralSubgroup[subgroup];
            } else if (orbitName === ("green")){
                const subgroup: number = this.greenTetrahedral[orientation];
                return this.tetrahedralSubgroup[subgroup];
            } else if (allowYellow && (orbitName === ("yellow"))){
                const subgroup: number = this.yellowTetrahedral[orientation];
                return this.tetrahedralSubgroup[subgroup];
            }
            return null;
        }

        public subgroup(name?: any, zone?: any, allowYellow?: any): number[] {
            if (((typeof name === 'string') || name === null) && ((zone != null && zone instanceof <any>com.vzome.core.math.symmetry.Axis) || zone === null) && ((typeof allowYellow === 'boolean') || allowYellow === null)) {
                return <any>this.subgroup$java_lang_String$com_vzome_core_math_symmetry_Axis$boolean(name, zone, allowYellow);
            } else if (((typeof name === 'string') || name === null) && ((zone != null && zone instanceof <any>com.vzome.core.math.symmetry.Axis) || zone === null) && allowYellow === undefined) {
                return <any>this.subgroup$java_lang_String$com_vzome_core_math_symmetry_Axis(name, zone);
            } else if (((typeof name === 'string') || name === null) && zone === undefined && allowYellow === undefined) {
                return <any>this.subgroup$java_lang_String(name);
            } else throw new Error('invalid overload');
        }

        public blueTetrahedralFromGreen(greenIndex: number): number {
            const subgroup: number = this.greenTetrahedral[greenIndex];
            for(let i: number = 0; i < this.blueTetrahedral.length; i++) {{
                if (this.blueTetrahedral[i] === subgroup)return i;
            };}
            return 0;
        }

        public interpretScript(script: string, language: string, offset: com.vzome.core.construction.Point, symmetry: com.vzome.core.math.symmetry.Symmetry, effects: com.vzome.core.construction.ConstructionChanges) {
            this.mField.interpretScript(script, language, offset, symmetry, effects);
        }
    }
    IcosahedralSymmetry["__class"] = "com.vzome.core.math.symmetry.IcosahedralSymmetry";
    IcosahedralSymmetry["__interfaces"] = ["com.vzome.core.math.symmetry.Symmetry","com.vzome.core.math.symmetry.Embedding"];


}

