/* Generated from Java with JSweet 3.2.0-SNAPSHOT - http://www.jsweet.org */
namespace com.vzome.core.math.symmetry {
    /**
     * @author Scott Vorthmann
     * @class
     */
    export abstract class AbstractSymmetry implements com.vzome.core.math.symmetry.Symmetry {
        mDirectionMap: java.util.Map<string, com.vzome.core.math.symmetry.Direction>;

        mDirectionList: java.util.List<com.vzome.core.math.symmetry.Direction>;

        orbitSet: com.vzome.core.math.symmetry.OrbitSet;

        mOrientations: com.vzome.core.math.symmetry.Permutation[];

        mMatrices: com.vzome.core.algebra.AlgebraicMatrix[];

        mField: com.vzome.core.algebra.AlgebraicField;

        /*private*/ principalReflection: com.vzome.core.algebra.AlgebraicMatrix;

        /*private*/ dotLocator: com.vzome.core.math.symmetry.OrbitDotLocator;

        constructor(order: number, field: com.vzome.core.algebra.AlgebraicField, frameColor: string, principalReflection: com.vzome.core.algebra.AlgebraicMatrix) {
            this.mDirectionMap = <any>(new java.util.HashMap<any, any>());
            this.mDirectionList = <any>(new java.util.ArrayList<any>());
            this.orbitSet = new com.vzome.core.math.symmetry.OrbitSet(this);
            if (this.mOrientations === undefined) { this.mOrientations = null; }
            if (this.mMatrices === undefined) { this.mMatrices = null; }
            if (this.mField === undefined) { this.mField = null; }
            this.principalReflection = null;
            if (this.dotLocator === undefined) { this.dotLocator = null; }
            this.mField = field;
            this.principalReflection = principalReflection;
            this.mOrientations = (s => { let a=[]; while(s-->0) a.push(null); return a; })(order);
            this.mMatrices = (s => { let a=[]; while(s-->0) a.push(null); return a; })(order);
            this.createInitialPermutations();
            let done: boolean = false;
            while((!done)) {{
                done = true;
                for(let i: number = 1; i < order; i++) {{
                    const p1: com.vzome.core.math.symmetry.Permutation = this.mOrientations[i];
                    if (p1 == null){
                        done = false;
                        continue;
                    }
                    done = true;
                    for(let j: number = 1; j < order; j++) {{
                        const p2: com.vzome.core.math.symmetry.Permutation = this.mOrientations[j];
                        if (p2 == null){
                            done = false;
                            continue;
                        }
                        const result: number = p1.mapIndex(p2.mapIndex(0));
                        if (this.mOrientations[result] != null)continue;
                        const map: number[] = (s => { let a=[]; while(s-->0) a.push(0); return a; })(order);
                        for(let k: number = 0; k < order; k++) {map[k] = p1.mapIndex(p2.mapIndex(k));}
                        this.mOrientations[result] = new com.vzome.core.math.symmetry.Permutation(this, map);
                    };}
                    if (done)break;
                };}
            }};
            this.createFrameOrbit(frameColor);
            this.createOtherOrbits();
        }

        abstract createFrameOrbit(frameColor: string);

        abstract createOtherOrbits();

        abstract createInitialPermutations();

        /**
         * 
         * @return {*}
         */
        public getField(): com.vzome.core.algebra.AlgebraicField {
            return this.mField;
        }

        /**
         * 
         * @return {com.vzome.core.math.symmetry.Axis}
         */
        public getPreferredAxis(): com.vzome.core.math.symmetry.Axis {
            return null;
        }

        public createZoneOrbit$java_lang_String$int$int$int_A_A(name: string, prototype: number, rotatedPrototype: number, norm: number[][]): com.vzome.core.math.symmetry.Direction {
            const aNorm: com.vzome.core.algebra.AlgebraicVector = this.mField.createVector(norm);
            return this.createZoneOrbit$java_lang_String$int$int$com_vzome_core_algebra_AlgebraicVector$boolean(name, prototype, rotatedPrototype, aNorm, false);
        }

        public createZoneOrbit$java_lang_String$int$int$com_vzome_core_algebra_AlgebraicVector(name: string, prototype: number, rotatedPrototype: number, norm: com.vzome.core.algebra.AlgebraicVector): com.vzome.core.math.symmetry.Direction {
            return this.createZoneOrbit$java_lang_String$int$int$com_vzome_core_algebra_AlgebraicVector$boolean(name, prototype, rotatedPrototype, norm, false);
        }

        public createZoneOrbit$java_lang_String$int$int$int_A_A$boolean(name: string, prototype: number, rotatedPrototype: number, norm: number[][], standard: boolean): com.vzome.core.math.symmetry.Direction {
            const aNorm: com.vzome.core.algebra.AlgebraicVector = this.mField.createVector(norm);
            return this.createZoneOrbit$java_lang_String$int$int$com_vzome_core_algebra_AlgebraicVector$boolean$boolean(name, prototype, rotatedPrototype, aNorm, standard, false);
        }

        public createZoneOrbit$java_lang_String$int$int$com_vzome_core_algebra_AlgebraicVector$boolean(name: string, prototype: number, rotatedPrototype: number, norm: com.vzome.core.algebra.AlgebraicVector, standard: boolean): com.vzome.core.math.symmetry.Direction {
            return this.createZoneOrbit$java_lang_String$int$int$com_vzome_core_algebra_AlgebraicVector$boolean$boolean(name, prototype, rotatedPrototype, norm, standard, false);
        }

        createZoneOrbit$java_lang_String$int$int$int_A_A$boolean$boolean(name: string, prototype: number, rotatedPrototype: number, norm: number[][], standard: boolean, halfSizes: boolean): com.vzome.core.math.symmetry.Direction {
            const aNorm: com.vzome.core.algebra.AlgebraicVector = this.mField.createVector(norm);
            return this.createZoneOrbit$java_lang_String$int$int$com_vzome_core_algebra_AlgebraicVector$boolean$boolean$com_vzome_core_algebra_AlgebraicNumber(name, prototype, rotatedPrototype, aNorm, standard, false, null);
        }

        createZoneOrbit$java_lang_String$int$int$com_vzome_core_algebra_AlgebraicVector$boolean$boolean(name: string, prototype: number, rotatedPrototype: number, norm: com.vzome.core.algebra.AlgebraicVector, standard: boolean, halfSizes: boolean): com.vzome.core.math.symmetry.Direction {
            return this.createZoneOrbit$java_lang_String$int$int$com_vzome_core_algebra_AlgebraicVector$boolean$boolean$com_vzome_core_algebra_AlgebraicNumber(name, prototype, rotatedPrototype, norm, standard, false, this.mField.one());
        }

        public createZoneOrbit$java_lang_String$int$int$int_A_A$boolean$boolean$com_vzome_core_algebra_AlgebraicNumber(name: string, prototype: number, rotatedPrototype: number, norm: number[][], standard: boolean, halfSizes: boolean, unitLength: com.vzome.core.algebra.AlgebraicNumber): com.vzome.core.math.symmetry.Direction {
            const aNorm: com.vzome.core.algebra.AlgebraicVector = this.mField.createVector(norm);
            return this.createZoneOrbit$java_lang_String$int$int$com_vzome_core_algebra_AlgebraicVector$boolean$boolean$com_vzome_core_algebra_AlgebraicNumber(name, prototype, rotatedPrototype, aNorm, standard, halfSizes, unitLength);
        }

        public createZoneOrbit(name?: any, prototype?: any, rotatedPrototype?: any, norm?: any, standard?: any, halfSizes?: any, unitLength?: any): com.vzome.core.math.symmetry.Direction {
            if (((typeof name === 'string') || name === null) && ((typeof prototype === 'number') || prototype === null) && ((typeof rotatedPrototype === 'number') || rotatedPrototype === null) && ((norm != null && norm instanceof <any>Array && (norm.length == 0 || norm[0] == null ||norm[0] instanceof Array)) || norm === null) && ((typeof standard === 'boolean') || standard === null) && ((typeof halfSizes === 'boolean') || halfSizes === null) && ((unitLength != null && (unitLength.constructor != null && unitLength.constructor["__interfaces"] != null && unitLength.constructor["__interfaces"].indexOf("com.vzome.core.algebra.AlgebraicNumber") >= 0)) || unitLength === null)) {
                return <any>this.createZoneOrbit$java_lang_String$int$int$int_A_A$boolean$boolean$com_vzome_core_algebra_AlgebraicNumber(name, prototype, rotatedPrototype, norm, standard, halfSizes, unitLength);
            } else if (((typeof name === 'string') || name === null) && ((typeof prototype === 'number') || prototype === null) && ((typeof rotatedPrototype === 'number') || rotatedPrototype === null) && ((norm != null && norm instanceof <any>com.vzome.core.algebra.AlgebraicVector) || norm === null) && ((typeof standard === 'boolean') || standard === null) && ((typeof halfSizes === 'boolean') || halfSizes === null) && ((unitLength != null && (unitLength.constructor != null && unitLength.constructor["__interfaces"] != null && unitLength.constructor["__interfaces"].indexOf("com.vzome.core.algebra.AlgebraicNumber") >= 0)) || unitLength === null)) {
                return <any>this.createZoneOrbit$java_lang_String$int$int$com_vzome_core_algebra_AlgebraicVector$boolean$boolean$com_vzome_core_algebra_AlgebraicNumber(name, prototype, rotatedPrototype, norm, standard, halfSizes, unitLength);
            } else if (((typeof name === 'string') || name === null) && ((typeof prototype === 'number') || prototype === null) && ((typeof rotatedPrototype === 'number') || rotatedPrototype === null) && ((norm != null && norm instanceof <any>Array && (norm.length == 0 || norm[0] == null ||norm[0] instanceof Array)) || norm === null) && ((typeof standard === 'boolean') || standard === null) && ((typeof halfSizes === 'boolean') || halfSizes === null) && unitLength === undefined) {
                return <any>this.createZoneOrbit$java_lang_String$int$int$int_A_A$boolean$boolean(name, prototype, rotatedPrototype, norm, standard, halfSizes);
            } else if (((typeof name === 'string') || name === null) && ((typeof prototype === 'number') || prototype === null) && ((typeof rotatedPrototype === 'number') || rotatedPrototype === null) && ((norm != null && norm instanceof <any>com.vzome.core.algebra.AlgebraicVector) || norm === null) && ((typeof standard === 'boolean') || standard === null) && ((typeof halfSizes === 'boolean') || halfSizes === null) && unitLength === undefined) {
                return <any>this.createZoneOrbit$java_lang_String$int$int$com_vzome_core_algebra_AlgebraicVector$boolean$boolean(name, prototype, rotatedPrototype, norm, standard, halfSizes);
            } else if (((typeof name === 'string') || name === null) && ((typeof prototype === 'number') || prototype === null) && ((typeof rotatedPrototype === 'number') || rotatedPrototype === null) && ((norm != null && norm instanceof <any>Array && (norm.length == 0 || norm[0] == null ||norm[0] instanceof Array)) || norm === null) && ((typeof standard === 'boolean') || standard === null) && halfSizes === undefined && unitLength === undefined) {
                return <any>this.createZoneOrbit$java_lang_String$int$int$int_A_A$boolean(name, prototype, rotatedPrototype, norm, standard);
            } else if (((typeof name === 'string') || name === null) && ((typeof prototype === 'number') || prototype === null) && ((typeof rotatedPrototype === 'number') || rotatedPrototype === null) && ((norm != null && norm instanceof <any>com.vzome.core.algebra.AlgebraicVector) || norm === null) && ((typeof standard === 'boolean') || standard === null) && halfSizes === undefined && unitLength === undefined) {
                return <any>this.createZoneOrbit$java_lang_String$int$int$com_vzome_core_algebra_AlgebraicVector$boolean(name, prototype, rotatedPrototype, norm, standard);
            } else if (((typeof name === 'string') || name === null) && ((typeof prototype === 'number') || prototype === null) && ((typeof rotatedPrototype === 'number') || rotatedPrototype === null) && ((norm != null && norm instanceof <any>Array && (norm.length == 0 || norm[0] == null ||norm[0] instanceof Array)) || norm === null) && standard === undefined && halfSizes === undefined && unitLength === undefined) {
                return <any>this.createZoneOrbit$java_lang_String$int$int$int_A_A(name, prototype, rotatedPrototype, norm);
            } else if (((typeof name === 'string') || name === null) && ((typeof prototype === 'number') || prototype === null) && ((typeof rotatedPrototype === 'number') || rotatedPrototype === null) && ((norm != null && norm instanceof <any>com.vzome.core.algebra.AlgebraicVector) || norm === null) && standard === undefined && halfSizes === undefined && unitLength === undefined) {
                return <any>this.createZoneOrbit$java_lang_String$int$int$com_vzome_core_algebra_AlgebraicVector(name, prototype, rotatedPrototype, norm);
            } else throw new Error('invalid overload');
        }

        public createZoneOrbit$java_lang_String$int$int$com_vzome_core_algebra_AlgebraicVector$boolean$boolean$com_vzome_core_algebra_AlgebraicNumber(name: string, prototype: number, rotatedPrototype: number, norm: com.vzome.core.algebra.AlgebraicVector, standard: boolean, halfSizes: boolean, unitLength: com.vzome.core.algebra.AlgebraicNumber): com.vzome.core.math.symmetry.Direction {
            const existingDir: com.vzome.core.math.symmetry.Direction = this.mDirectionMap.get(name);
            if (existingDir != null){
                this.mDirectionMap.remove(name);
                this.orbitSet.remove(existingDir);
                this.mDirectionList.remove(existingDir);
            }
            const orbit: com.vzome.core.math.symmetry.Direction = new com.vzome.core.math.symmetry.Direction(name, this, prototype, rotatedPrototype, norm, standard);
            if (halfSizes)orbit.setHalfSizes(true);
            orbit.setUnitLength(unitLength);
            this.mDirectionMap.put(orbit.getName(), orbit);
            this.mDirectionList.add(orbit);
            this.orbitSet.add(orbit);
            if (this.dotLocator != null)this.dotLocator.locateOrbitDot(orbit);
            return orbit;
        }

        /**
         * 
         * @param {string} name
         * @param {number} prototype
         * @param {number} rotatedPrototype
         * @param {com.vzome.core.algebra.AlgebraicVector} norm
         * @return {com.vzome.core.math.symmetry.Direction}
         */
        public createNewZoneOrbit(name: string, prototype: number, rotatedPrototype: number, norm: com.vzome.core.algebra.AlgebraicVector): com.vzome.core.math.symmetry.Direction {
            const orbit: com.vzome.core.math.symmetry.Direction = new com.vzome.core.math.symmetry.Direction(name, this, prototype, rotatedPrototype, norm, false).withCorrection();
            if (this.dotLocator == null)this.dotLocator = new com.vzome.core.math.symmetry.OrbitDotLocator(this, this.getOrbitTriangle());
            this.dotLocator.locateOrbitDot(orbit);
            return orbit;
        }

        /**
         * 
         * @return {com.vzome.core.math.symmetry.OrbitSet}
         */
        public getOrbitSet(): com.vzome.core.math.symmetry.OrbitSet {
            return this.orbitSet;
        }

        /**
         * @param unit
         * @param rot
         * @return
         * @param {number} from
         * @param {number} to
         * @return {number}
         */
        public getMapping(from: number, to: number): number {
            if (to === com.vzome.core.math.symmetry.Symmetry.NO_ROTATION)return com.vzome.core.math.symmetry.Symmetry.NO_ROTATION;
            for(let p: number = 0; p < this.mOrientations.length; p++) {if (this.mOrientations[p].mapIndex(from) === to)return p;;}
            return com.vzome.core.math.symmetry.Symmetry.NO_ROTATION;
        }

        public mapAxis(from: com.vzome.core.math.symmetry.Axis, to: com.vzome.core.math.symmetry.Axis): com.vzome.core.math.symmetry.Permutation {
            return this.mapAxes([from], [to]);
        }

        public mapAxes(from: com.vzome.core.math.symmetry.Axis[], to: com.vzome.core.math.symmetry.Axis[]): com.vzome.core.math.symmetry.Permutation {
            if (from.length !== to.length)throw new AbstractSymmetry.MismatchedAxes("must map to equal number of axes");
            if (from.length > 3)throw new AbstractSymmetry.MismatchedAxes("must map three or fewer axes");
            for(let i: number = 0; i < from.length; i++) {if (from[i].getDirection().equals(to[i].getDirection()))throw new AbstractSymmetry.MismatchedAxes("must map between same color axes");;}
            const result: com.vzome.core.math.symmetry.Permutation[] = [null];
            return result[0];
        }

        /**
         * 
         * @return {*}
         */
        public getDirections(): java.lang.Iterable<com.vzome.core.math.symmetry.Direction> {
            return this.mDirectionList;
        }

        public getAxis$com_vzome_core_algebra_AlgebraicVector(vector: com.vzome.core.algebra.AlgebraicVector): com.vzome.core.math.symmetry.Axis {
            return this.getAxis$com_vzome_core_algebra_AlgebraicVector$com_vzome_core_math_symmetry_OrbitSet(vector, this.orbitSet);
        }

        public getAxis$com_vzome_core_algebra_AlgebraicVector$com_vzome_core_math_symmetry_OrbitSet(vector: com.vzome.core.algebra.AlgebraicVector, orbits: com.vzome.core.math.symmetry.OrbitSet): com.vzome.core.math.symmetry.Axis {
            if (vector.isOrigin()){
                return null;
            }
            const canonicalOrbit: com.vzome.core.math.symmetry.Direction = this.getSpecialOrbit(com.vzome.core.math.symmetry.SpecialOrbit.BLACK);
            if (canonicalOrbit == null)for(let index=orbits.getDirections().iterator();index.hasNext();) {
                let dir = index.next();
                {
                    const candidate: com.vzome.core.math.symmetry.Axis = dir.getAxis$com_vzome_core_algebra_AlgebraicVector(vector);
                    if (candidate != null){
                        return candidate;
                    }
                }
            } else {
                const zone: com.vzome.core.math.symmetry.Axis = canonicalOrbit.getAxis$com_vzome_core_math_RealVector(vector.toRealVector());
                const orientation: number = zone.getOrientation();
                const sense: number = zone.getSense();
                for(let index=orbits.getDirections().iterator();index.hasNext();) {
                    let orbit = index.next();
                    {
                        const candidate: com.vzome.core.math.symmetry.Axis = orbit.getCanonicalAxis(sense, orientation);
                        if (com.vzome.core.algebra.AlgebraicVectors.areParallel(candidate.normal(), vector)){
                            return candidate;
                        }
                    }
                }
            }
            return null;
        }

        /**
         * 
         * @param {com.vzome.core.algebra.AlgebraicVector} vector
         * @param {com.vzome.core.math.symmetry.OrbitSet} orbits
         * @return {com.vzome.core.math.symmetry.Axis}
         */
        public getAxis(vector?: any, orbits?: any): com.vzome.core.math.symmetry.Axis {
            if (((vector != null && vector instanceof <any>com.vzome.core.algebra.AlgebraicVector) || vector === null) && ((orbits != null && orbits instanceof <any>com.vzome.core.math.symmetry.OrbitSet) || orbits === null)) {
                return <any>this.getAxis$com_vzome_core_algebra_AlgebraicVector$com_vzome_core_math_symmetry_OrbitSet(vector, orbits);
            } else if (((vector != null && vector instanceof <any>com.vzome.core.math.RealVector) || vector === null) && ((orbits != null && (orbits.constructor != null && orbits.constructor["__interfaces"] != null && orbits.constructor["__interfaces"].indexOf("java.util.Collection") >= 0)) || orbits === null)) {
                return <any>this.getAxis$com_vzome_core_math_RealVector$java_util_Collection(vector, orbits);
            } else if (((vector != null && vector instanceof <any>com.vzome.core.algebra.AlgebraicVector) || vector === null) && orbits === undefined) {
                return <any>this.getAxis$com_vzome_core_algebra_AlgebraicVector(vector);
            } else throw new Error('invalid overload');
        }

        public getAxis$com_vzome_core_math_RealVector$java_util_Collection(vector: com.vzome.core.math.RealVector, dirMask: java.util.Collection<com.vzome.core.math.symmetry.Direction>): com.vzome.core.math.symmetry.Axis {
            if (com.vzome.core.math.RealVector.ORIGIN_$LI$().equals(vector)){
                return null;
            }
            let maxCosine: number = -1.0;
            let closest: com.vzome.core.math.symmetry.Axis = null;
            let orientation: number = -1;
            let sense: number = -1;
            const chiralOrbit: com.vzome.core.math.symmetry.Direction = this.getSpecialOrbit(com.vzome.core.math.symmetry.SpecialOrbit.BLACK);
            if (chiralOrbit != null){
                const closestChiralAxis: com.vzome.core.math.symmetry.Axis = chiralOrbit.getChiralAxis(vector);
                orientation = closestChiralAxis.getOrientation();
                sense = closestChiralAxis.getSense();
            }
            const dirs: java.lang.Iterable<com.vzome.core.math.symmetry.Direction> = dirMask == null ? this.orbitSet.getDirections() : dirMask;
            for(let index=dirs.iterator();index.hasNext();) {
                let dir = index.next();
                {
                    const axis: com.vzome.core.math.symmetry.Axis = (orientation >= 0) ? dir.getCanonicalAxis(sense, orientation) : dir.getAxisBruteForce(vector);
                    const axisV: com.vzome.core.math.RealVector = axis.normal().toRealVector();
                    const cosine: number = vector.dot(axisV) / (vector.length() * axisV.length());
                    if (cosine > maxCosine){
                        maxCosine = cosine;
                        closest = axis;
                    }
                }
            }
            return closest;
        }

        /**
         * 
         * @return {number}
         */
        public getChiralOrder(): number {
            return this.mOrientations.length;
        }

        /**
         * 
         * @param {number} i
         * @return {com.vzome.core.math.symmetry.Permutation}
         */
        public getPermutation(i: number): com.vzome.core.math.symmetry.Permutation {
            if ((i < 0) || (i > this.mOrientations.length))return null;
            return this.mOrientations[i];
        }

        public getPermutations(): com.vzome.core.math.symmetry.Permutation[] {
            return this.mOrientations;
        }

        /**
         * 
         * @param {number} i
         * @return {com.vzome.core.algebra.AlgebraicMatrix}
         */
        public getMatrix(i: number): com.vzome.core.algebra.AlgebraicMatrix {
            return this.mMatrices[i];
        }

        public getMatrices(): com.vzome.core.algebra.AlgebraicMatrix[] {
            return this.mMatrices;
        }

        /**
         * 
         * @param {number} orientation
         * @return {number}
         */
        public inverse(orientation: number): number {
            if ((orientation < 0) || (orientation > this.mOrientations.length))return com.vzome.core.math.symmetry.Symmetry.NO_ROTATION;
            return this.mOrientations[orientation].inverse().mapIndex(0);
        }

        /**
         * 
         * @param {string} color
         * @return {com.vzome.core.math.symmetry.Direction}
         */
        public getDirection(color: string): com.vzome.core.math.symmetry.Direction {
            return this.mDirectionMap.get(color);
        }

        /**
         * 
         * @return {java.lang.String[]}
         */
        public getDirectionNames(): string[] {
            const list: java.util.ArrayList<string> = <any>(new java.util.ArrayList<any>());
            for(let index=this.mDirectionList.iterator();index.hasNext();) {
                let dir = index.next();
                {
                    if (!dir.isAutomatic())list.add(dir.getName());
                }
            }
            return list.toArray<any>([]);
        }

        /**
         * 
         * @param {int[]} perms
         * @return {int[]}
         */
        public closure(perms: number[]): number[] {
            const newPerms: java.util.List<com.vzome.core.math.symmetry.Permutation> = <any>(new java.util.ArrayList<any>());
            const knownPerms: com.vzome.core.math.symmetry.Permutation[] = (s => { let a=[]; while(s-->0) a.push(null); return a; })(this.mOrientations.length);
            let closureSize: number = 0;
            for(let i: number = 0; i < perms.length; i++) {{
                const perm: com.vzome.core.math.symmetry.Permutation = this.mOrientations[perms[i]];
                knownPerms[perms[i]] = perm;
                newPerms.add(perm);
                ++closureSize;
            };}
            while((!newPerms.isEmpty())) {{
                const perm: com.vzome.core.math.symmetry.Permutation = newPerms.remove(0);
                for(let index = 0; index < knownPerms.length; index++) {
                    let knownPerm = knownPerms[index];
                    {
                        if (knownPerm != null){
                            let composition: com.vzome.core.math.symmetry.Permutation = perm.compose(knownPerm);
                            let j: number = composition.mapIndex(0);
                            if (knownPerms[j] == null){
                                newPerms.add(composition);
                                knownPerms[j] = composition;
                                ++closureSize;
                            }
                            composition = knownPerm.compose(perm);
                            j = composition.mapIndex(0);
                            if (knownPerms[j] == null){
                                newPerms.add(composition);
                                knownPerms[j] = composition;
                                ++closureSize;
                            }
                        }
                    }
                }
            }};
            const result: number[] = (s => { let a=[]; while(s-->0) a.push(0); return a; })(closureSize);
            let j: number = 0;
            for(let i: number = 0; i < knownPerms.length; i++) {{
                if (knownPerms[i] != null){
                    result[j++] = i;
                }
            };}
            return result;
        }

        /**
         * 
         * @param {number} orientation
         * @return {int[]}
         */
        public getIncidentOrientations(orientation: number): number[] {
            return null;
        }

        /**
         * 
         * @param {com.vzome.core.algebra.AlgebraicVector} v
         * @return {com.vzome.core.math.RealVector}
         */
        public embedInR3(v: com.vzome.core.algebra.AlgebraicVector): com.vzome.core.math.RealVector {
            return v.toRealVector();
        }

        /**
         * 
         * @param {com.vzome.core.algebra.AlgebraicVector} v
         * @return {double[]}
         */
        public embedInR3Double(v: com.vzome.core.algebra.AlgebraicVector): number[] {
            return v.to3dDoubleVector();
        }

        /**
         * 
         * @return {boolean}
         */
        public isTrivial(): boolean {
            return true;
        }

        /**
         * 
         * @return {com.vzome.core.algebra.AlgebraicMatrix}
         */
        public getPrincipalReflection(): com.vzome.core.algebra.AlgebraicMatrix {
            return this.principalReflection;
        }

        /**
         * 
         * @return {com.vzome.core.algebra.AlgebraicVector[]}
         */
        public getOrbitTriangle(): com.vzome.core.algebra.AlgebraicVector[] {
            const blueVertex: com.vzome.core.algebra.AlgebraicVector = this.getSpecialOrbit(com.vzome.core.math.symmetry.SpecialOrbit.BLUE).getPrototype();
            const redVertex: com.vzome.core.algebra.AlgebraicVector = this.getSpecialOrbit(com.vzome.core.math.symmetry.SpecialOrbit.RED).getPrototype();
            const yellowVertex: com.vzome.core.algebra.AlgebraicVector = this.getSpecialOrbit(com.vzome.core.math.symmetry.SpecialOrbit.YELLOW).getPrototype();
            return [blueVertex, redVertex, yellowVertex];
        }

        /**
         * 
         * @return {string}
         */
        public computeOrbitDots(): string {
            if (this.dotLocator == null)this.dotLocator = new com.vzome.core.math.symmetry.OrbitDotLocator(this, this.getOrbitTriangle());
            for(let index=this.mDirectionList.iterator();index.hasNext();) {
                let orbit = index.next();
                {
                    this.dotLocator.locateOrbitDot(orbit);
                }
            }
            return null;
        }

        /**
         * 
         * @return {boolean}
         */
        public reverseOrbitTriangle(): boolean {
            return false;
        }

        public abstract getName(): any;
        public abstract getSpecialOrbit(which?: any): any;
        public abstract subgroup(name?: any): any;    }
    AbstractSymmetry["__class"] = "com.vzome.core.math.symmetry.AbstractSymmetry";
    AbstractSymmetry["__interfaces"] = ["com.vzome.core.math.symmetry.Symmetry","com.vzome.core.math.symmetry.Embedding"];



    export namespace AbstractSymmetry {

        export class MismatchedAxes extends java.lang.RuntimeException {
            static serialVersionUID: number = 2610579323321804987;

            public constructor(message: string) {
                super(message);
                (<any>Object).setPrototypeOf(this, MismatchedAxes.prototype);
            }
        }
        MismatchedAxes["__class"] = "com.vzome.core.math.symmetry.AbstractSymmetry.MismatchedAxes";
        MismatchedAxes["__interfaces"] = ["java.io.Serializable"];


    }

}

