import { java, javaemul } from "../../../../../../candies/j4ts-2.1.0-SNAPSHOT/bundle.js";
import { AlgebraicField } from "../../algebra/AlgebraicField.js";
import { AlgebraicMatrix } from "../../algebra/AlgebraicMatrix.js";
import { AlgebraicNumber } from "../../algebra/AlgebraicNumber.js";
import { AlgebraicVector } from "../../algebra/AlgebraicVector.js";
import { AlgebraicVectors } from "../../algebra/AlgebraicVectors.js";
import { RealVector } from "../RealVector.js";
import { Axis } from "./Axis.js";
import { Direction } from "./Direction.js";
import { OrbitDotLocator } from "./OrbitDotLocator.js";
import { OrbitSet } from "./OrbitSet.js";
import { Permutation } from "./Permutation.js";
import { SpecialOrbit } from "./SpecialOrbit.js";
import { Symmetry } from "./Symmetry.js";

/**
 * @author Scott Vorthmann
 * @class
 */
export abstract class AbstractSymmetry implements Symmetry {
    mDirectionMap: java.util.Map<string, Direction>;

    mDirectionList: java.util.List<Direction>;

    orbitSet: OrbitSet;

    mOrientations: Permutation[];

    mMatrices: AlgebraicMatrix[];

    mField: AlgebraicField;

    /*private*/ principalReflection: AlgebraicMatrix;

    /*private*/ dotLocator: OrbitDotLocator;

    constructor(order: number, field: AlgebraicField, frameColor: string, principalReflection: AlgebraicMatrix) {
        this.mDirectionMap = <any>(new java.util.HashMap<any, any>());
        this.mDirectionList = <any>(new java.util.ArrayList<any>());
        this.orbitSet = new OrbitSet(this);
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
                const p1: Permutation = this.mOrientations[i];
                if (p1 == null){
                    done = false;
                    continue;
                }
                done = true;
                for(let j: number = 1; j < order; j++) {{
                    const p2: Permutation = this.mOrientations[j];
                    if (p2 == null){
                        done = false;
                        continue;
                    }
                    const result: number = p1.mapIndex(p2.mapIndex(0));
                    if (this.mOrientations[result] != null)continue;
                    const map: number[] = (s => { let a=[]; while(s-->0) a.push(0); return a; })(order);
                    for(let k: number = 0; k < order; k++) {map[k] = p1.mapIndex(p2.mapIndex(k));}
                    this.mOrientations[result] = new Permutation(this, map);
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
    public getField(): AlgebraicField {
        return this.mField;
    }

    /**
     * 
     * @return {Axis}
     */
    public getPreferredAxis(): Axis {
        return null;
    }

    public createZoneOrbit$java_lang_String$int$int$int_A_A(name: string, prototype: number, rotatedPrototype: number, norm: number[][]): Direction {
        const aNorm: AlgebraicVector = this.mField.createVector(norm);
        return this.createZoneOrbit$java_lang_String$int$int$com_vzome_core_algebra_AlgebraicVector$boolean(name, prototype, rotatedPrototype, aNorm, false);
    }

    public createZoneOrbit$java_lang_String$int$int$com_vzome_core_algebra_AlgebraicVector(name: string, prototype: number, rotatedPrototype: number, norm: AlgebraicVector): Direction {
        return this.createZoneOrbit$java_lang_String$int$int$com_vzome_core_algebra_AlgebraicVector$boolean(name, prototype, rotatedPrototype, norm, false);
    }

    public createZoneOrbit$java_lang_String$int$int$int_A_A$boolean(name: string, prototype: number, rotatedPrototype: number, norm: number[][], standard: boolean): Direction {
        const aNorm: AlgebraicVector = this.mField.createVector(norm);
        return this.createZoneOrbit$java_lang_String$int$int$com_vzome_core_algebra_AlgebraicVector$boolean$boolean(name, prototype, rotatedPrototype, aNorm, standard, false);
    }

    public createZoneOrbit$java_lang_String$int$int$com_vzome_core_algebra_AlgebraicVector$boolean(name: string, prototype: number, rotatedPrototype: number, norm: AlgebraicVector, standard: boolean): Direction {
        return this.createZoneOrbit$java_lang_String$int$int$com_vzome_core_algebra_AlgebraicVector$boolean$boolean(name, prototype, rotatedPrototype, norm, standard, false);
    }

    createZoneOrbit$java_lang_String$int$int$int_A_A$boolean$boolean(name: string, prototype: number, rotatedPrototype: number, norm: number[][], standard: boolean, halfSizes: boolean): Direction {
        const aNorm: AlgebraicVector = this.mField.createVector(norm);
        return this.createZoneOrbit$java_lang_String$int$int$com_vzome_core_algebra_AlgebraicVector$boolean$boolean$com_vzome_core_algebra_AlgebraicNumber(name, prototype, rotatedPrototype, aNorm, standard, false, null);
    }

    createZoneOrbit$java_lang_String$int$int$com_vzome_core_algebra_AlgebraicVector$boolean$boolean(name: string, prototype: number, rotatedPrototype: number, norm: AlgebraicVector, standard: boolean, halfSizes: boolean): Direction {
        return this.createZoneOrbit$java_lang_String$int$int$com_vzome_core_algebra_AlgebraicVector$boolean$boolean$com_vzome_core_algebra_AlgebraicNumber(name, prototype, rotatedPrototype, norm, standard, false, this.mField.one());
    }

    public createZoneOrbit$java_lang_String$int$int$int_A_A$boolean$boolean$com_vzome_core_algebra_AlgebraicNumber(name: string, prototype: number, rotatedPrototype: number, norm: number[][], standard: boolean, halfSizes: boolean, unitLength: AlgebraicNumber): Direction {
        const aNorm: AlgebraicVector = this.mField.createVector(norm);
        return this.createZoneOrbit$java_lang_String$int$int$com_vzome_core_algebra_AlgebraicVector$boolean$boolean$com_vzome_core_algebra_AlgebraicNumber(name, prototype, rotatedPrototype, aNorm, standard, halfSizes, unitLength);
    }

    public createZoneOrbit(name?: any, prototype?: any, rotatedPrototype?: any, norm?: any, standard?: any, halfSizes?: any, unitLength?: any): Direction {
        if (((typeof name === 'string') || name === null) && ((typeof prototype === 'number') || prototype === null) && ((typeof rotatedPrototype === 'number') || rotatedPrototype === null) && ((norm != null && norm instanceof <any>Array && (norm.length == 0 || norm[0] == null ||norm[0] instanceof Array)) || norm === null) && ((typeof standard === 'boolean') || standard === null) && ((typeof halfSizes === 'boolean') || halfSizes === null) && ((unitLength != null && (unitLength.constructor != null && unitLength.constructor["__interfaces"] != null && unitLength.constructor["__interfaces"].indexOf("com.vzome.core.algebra.AlgebraicNumber") >= 0)) || unitLength === null)) {
            return <any>this.createZoneOrbit$java_lang_String$int$int$int_A_A$boolean$boolean$com_vzome_core_algebra_AlgebraicNumber(name, prototype, rotatedPrototype, norm, standard, halfSizes, unitLength);
        } else if (((typeof name === 'string') || name === null) && ((typeof prototype === 'number') || prototype === null) && ((typeof rotatedPrototype === 'number') || rotatedPrototype === null) && ((norm != null && norm instanceof <any>AlgebraicVector) || norm === null) && ((typeof standard === 'boolean') || standard === null) && ((typeof halfSizes === 'boolean') || halfSizes === null) && ((unitLength != null && (unitLength.constructor != null && unitLength.constructor["__interfaces"] != null && unitLength.constructor["__interfaces"].indexOf("com.vzome.core.algebra.AlgebraicNumber") >= 0)) || unitLength === null)) {
            return <any>this.createZoneOrbit$java_lang_String$int$int$com_vzome_core_algebra_AlgebraicVector$boolean$boolean$com_vzome_core_algebra_AlgebraicNumber(name, prototype, rotatedPrototype, norm, standard, halfSizes, unitLength);
        } else if (((typeof name === 'string') || name === null) && ((typeof prototype === 'number') || prototype === null) && ((typeof rotatedPrototype === 'number') || rotatedPrototype === null) && ((norm != null && norm instanceof <any>Array && (norm.length == 0 || norm[0] == null ||norm[0] instanceof Array)) || norm === null) && ((typeof standard === 'boolean') || standard === null) && ((typeof halfSizes === 'boolean') || halfSizes === null) && unitLength === undefined) {
            return <any>this.createZoneOrbit$java_lang_String$int$int$int_A_A$boolean$boolean(name, prototype, rotatedPrototype, norm, standard, halfSizes);
        } else if (((typeof name === 'string') || name === null) && ((typeof prototype === 'number') || prototype === null) && ((typeof rotatedPrototype === 'number') || rotatedPrototype === null) && ((norm != null && norm instanceof <any>AlgebraicVector) || norm === null) && ((typeof standard === 'boolean') || standard === null) && ((typeof halfSizes === 'boolean') || halfSizes === null) && unitLength === undefined) {
            return <any>this.createZoneOrbit$java_lang_String$int$int$com_vzome_core_algebra_AlgebraicVector$boolean$boolean(name, prototype, rotatedPrototype, norm, standard, halfSizes);
        } else if (((typeof name === 'string') || name === null) && ((typeof prototype === 'number') || prototype === null) && ((typeof rotatedPrototype === 'number') || rotatedPrototype === null) && ((norm != null && norm instanceof <any>Array && (norm.length == 0 || norm[0] == null ||norm[0] instanceof Array)) || norm === null) && ((typeof standard === 'boolean') || standard === null) && halfSizes === undefined && unitLength === undefined) {
            return <any>this.createZoneOrbit$java_lang_String$int$int$int_A_A$boolean(name, prototype, rotatedPrototype, norm, standard);
        } else if (((typeof name === 'string') || name === null) && ((typeof prototype === 'number') || prototype === null) && ((typeof rotatedPrototype === 'number') || rotatedPrototype === null) && ((norm != null && norm instanceof <any>AlgebraicVector) || norm === null) && ((typeof standard === 'boolean') || standard === null) && halfSizes === undefined && unitLength === undefined) {
            return <any>this.createZoneOrbit$java_lang_String$int$int$com_vzome_core_algebra_AlgebraicVector$boolean(name, prototype, rotatedPrototype, norm, standard);
        } else if (((typeof name === 'string') || name === null) && ((typeof prototype === 'number') || prototype === null) && ((typeof rotatedPrototype === 'number') || rotatedPrototype === null) && ((norm != null && norm instanceof <any>Array && (norm.length == 0 || norm[0] == null ||norm[0] instanceof Array)) || norm === null) && standard === undefined && halfSizes === undefined && unitLength === undefined) {
            return <any>this.createZoneOrbit$java_lang_String$int$int$int_A_A(name, prototype, rotatedPrototype, norm);
        } else if (((typeof name === 'string') || name === null) && ((typeof prototype === 'number') || prototype === null) && ((typeof rotatedPrototype === 'number') || rotatedPrototype === null) && ((norm != null && norm instanceof <any>AlgebraicVector) || norm === null) && standard === undefined && halfSizes === undefined && unitLength === undefined) {
            return <any>this.createZoneOrbit$java_lang_String$int$int$com_vzome_core_algebra_AlgebraicVector(name, prototype, rotatedPrototype, norm);
        } else throw new Error('invalid overload');
    }

    public createZoneOrbit$java_lang_String$int$int$com_vzome_core_algebra_AlgebraicVector$boolean$boolean$com_vzome_core_algebra_AlgebraicNumber(name: string, prototype: number, rotatedPrototype: number, norm: AlgebraicVector, standard: boolean, halfSizes: boolean, unitLength: AlgebraicNumber): Direction {
        const existingDir: Direction = this.mDirectionMap.get(name);
        if (existingDir != null){
            this.mDirectionMap.remove(name);
            this.orbitSet.remove(existingDir);
            this.mDirectionList.remove(existingDir);
        }
        const orbit: Direction = new Direction(name, this, prototype, rotatedPrototype, norm, standard);
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
     * @param {AlgebraicVector} norm
     * @return {Direction}
     */
    public createNewZoneOrbit(name: string, prototype: number, rotatedPrototype: number, norm: AlgebraicVector): Direction {
        const orbit: Direction = new Direction(name, this, prototype, rotatedPrototype, norm, false).withCorrection();
        if (this.dotLocator == null)this.dotLocator = new OrbitDotLocator(this, this.getOrbitTriangle());
        this.dotLocator.locateOrbitDot(orbit);
        return orbit;
    }

    /**
     * 
     * @return {OrbitSet}
     */
    public getOrbitSet(): OrbitSet {
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
        if (to === Symmetry.NO_ROTATION)return Symmetry.NO_ROTATION;
        for(let p: number = 0; p < this.mOrientations.length; p++) {if (this.mOrientations[p].mapIndex(from) === to)return p;;}
        return Symmetry.NO_ROTATION;
    }

    public mapAxis(from: Axis, to: Axis): Permutation {
        return this.mapAxes([from], [to]);
    }

    public mapAxes(from: Axis[], to: Axis[]): Permutation {
        if (from.length !== to.length)throw new AbstractSymmetry.MismatchedAxes("must map to equal number of axes");
        if (from.length > 3)throw new AbstractSymmetry.MismatchedAxes("must map three or fewer axes");
        for(let i: number = 0; i < from.length; i++) {if (from[i].getDirection().equals(to[i].getDirection()))throw new AbstractSymmetry.MismatchedAxes("must map between same color axes");;}
        const result: Permutation[] = [null];
        return result[0];
    }

    /**
     * 
     * @return {*}
     */
    public getDirections(): java.lang.Iterable<Direction> {
        return this.mDirectionList;
    }

    public getAxis$com_vzome_core_algebra_AlgebraicVector(vector: AlgebraicVector): Axis {
        return this.getAxis$com_vzome_core_algebra_AlgebraicVector$com_vzome_core_math_symmetry_OrbitSet(vector, this.orbitSet);
    }

    public getAxis$com_vzome_core_algebra_AlgebraicVector$com_vzome_core_math_symmetry_OrbitSet(vector: AlgebraicVector, orbits: OrbitSet): Axis {
        if (vector.isOrigin()){
            return null;
        }
        const canonicalOrbit: Direction = this.getSpecialOrbit(SpecialOrbit.BLACK);
        if (canonicalOrbit == null)for(let index=orbits.getDirections().iterator();index.hasNext();) {
            let dir = index.next();
            {
                const candidate: Axis = dir.getAxis$com_vzome_core_algebra_AlgebraicVector(vector);
                if (candidate != null){
                    return candidate;
                }
            }
        } else {
            const zone: Axis = canonicalOrbit.getAxis$com_vzome_core_math_RealVector(vector.toRealVector());
            const orientation: number = zone.getOrientation();
            const sense: number = zone.getSense();
            for(let index=orbits.getDirections().iterator();index.hasNext();) {
                let orbit = index.next();
                {
                    const candidate: Axis = orbit.getCanonicalAxis(sense, orientation);
                    if (AlgebraicVectors.areParallel(candidate.normal(), vector)){
                        return candidate;
                    }
                }
            }
        }
        return null;
    }

    /**
     * 
     * @param {AlgebraicVector} vector
     * @param {OrbitSet} orbits
     * @return {Axis}
     */
    public getAxis(vector?: any, orbits?: any): Axis {
        if (((vector != null && vector instanceof <any>AlgebraicVector) || vector === null) && ((orbits != null && orbits instanceof <any>OrbitSet) || orbits === null)) {
            return <any>this.getAxis$com_vzome_core_algebra_AlgebraicVector$com_vzome_core_math_symmetry_OrbitSet(vector, orbits);
        } else if (((vector != null && vector instanceof <any>RealVector) || vector === null) && ((orbits != null && (orbits.constructor != null && orbits.constructor["__interfaces"] != null && orbits.constructor["__interfaces"].indexOf("java.util.Collection") >= 0)) || orbits === null)) {
            return <any>this.getAxis$com_vzome_core_math_RealVector$java_util_Collection(vector, orbits);
        } else if (((vector != null && vector instanceof <any>AlgebraicVector) || vector === null) && orbits === undefined) {
            return <any>this.getAxis$com_vzome_core_algebra_AlgebraicVector(vector);
        } else throw new Error('invalid overload');
    }

    public getAxis$com_vzome_core_math_RealVector$java_util_Collection(vector: RealVector, dirMask: java.util.Collection<Direction>): Axis {
        if (RealVector.ORIGIN_$LI$().equals(vector)){
            return null;
        }
        let maxCosine: number = -1.0;
        let closest: Axis = null;
        let orientation: number = -1;
        let sense: number = -1;
        const chiralOrbit: Direction = this.getSpecialOrbit(SpecialOrbit.BLACK);
        if (chiralOrbit != null){
            const closestChiralAxis: Axis = chiralOrbit.getChiralAxis(vector);
            orientation = closestChiralAxis.getOrientation();
            sense = closestChiralAxis.getSense();
        }
        const dirs: java.lang.Iterable<Direction> = dirMask == null ? this.orbitSet.getDirections() : dirMask;
        for(let index=dirs.iterator();index.hasNext();) {
            let dir = index.next();
            {
                const axis: Axis = (orientation >= 0) ? dir.getCanonicalAxis(sense, orientation) : dir.getAxisBruteForce(vector);
                const axisV: RealVector = axis.normal().toRealVector();
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
     * @return {Permutation}
     */
    public getPermutation(i: number): Permutation {
        if ((i < 0) || (i > this.mOrientations.length))return null;
        return this.mOrientations[i];
    }

    public getPermutations(): Permutation[] {
        return this.mOrientations;
    }

    /**
     * 
     * @param {number} i
     * @return {AlgebraicMatrix}
     */
    public getMatrix(i: number): AlgebraicMatrix {
        return this.mMatrices[i];
    }

    public getMatrices(): AlgebraicMatrix[] {
        return this.mMatrices;
    }

    /**
     * 
     * @param {number} orientation
     * @return {number}
     */
    public inverse(orientation: number): number {
        if ((orientation < 0) || (orientation > this.mOrientations.length))return Symmetry.NO_ROTATION;
        return this.mOrientations[orientation].inverse().mapIndex(0);
    }

    /**
     * 
     * @param {string} color
     * @return {Direction}
     */
    public getDirection(color: string): Direction {
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
        const newPerms: java.util.List<Permutation> = <any>(new java.util.ArrayList<any>());
        const knownPerms: Permutation[] = (s => { let a=[]; while(s-->0) a.push(null); return a; })(this.mOrientations.length);
        let closureSize: number = 0;
        for(let i: number = 0; i < perms.length; i++) {{
            const perm: Permutation = this.mOrientations[perms[i]];
            knownPerms[perms[i]] = perm;
            newPerms.add(perm);
            ++closureSize;
        };}
        while((!newPerms.isEmpty())) {{
            const perm: Permutation = newPerms.remove(0);
            for(let index = 0; index < knownPerms.length; index++) {
                let knownPerm = knownPerms[index];
                {
                    if (knownPerm != null){
                        let composition: Permutation = perm.compose(knownPerm);
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
     * @param {AlgebraicVector} v
     * @return {RealVector}
     */
    public embedInR3(v: AlgebraicVector): RealVector {
        return v.toRealVector();
    }

    /**
     * 
     * @param {AlgebraicVector} v
     * @return {double[]}
     */
    public embedInR3Double(v: AlgebraicVector): number[] {
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
     * @return {AlgebraicMatrix}
     */
    public getPrincipalReflection(): AlgebraicMatrix {
        return this.principalReflection;
    }

    /**
     * 
     * @return {AlgebraicVector[]}
     */
    public getOrbitTriangle(): AlgebraicVector[] {
        const blueVertex: AlgebraicVector = this.getSpecialOrbit(SpecialOrbit.BLUE).getPrototype();
        const redVertex: AlgebraicVector = this.getSpecialOrbit(SpecialOrbit.RED).getPrototype();
        const yellowVertex: AlgebraicVector = this.getSpecialOrbit(SpecialOrbit.YELLOW).getPrototype();
        return [blueVertex, redVertex, yellowVertex];
    }

    /**
     * 
     * @return {string}
     */
    public computeOrbitDots(): string {
        if (this.dotLocator == null)this.dotLocator = new OrbitDotLocator(this, this.getOrbitTriangle());
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
