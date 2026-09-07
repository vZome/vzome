import { AlgebraicMatrix } from "../../algebra/AlgebraicMatrix.js";
import { AlgebraicNumber } from "../../algebra/AlgebraicNumber.js";
import { AlgebraicVector } from "../../algebra/AlgebraicVector.js";
import { AlgebraicVectors } from "../../algebra/AlgebraicVectors.js";
import { PolygonField } from "../../algebra/PolygonField.js";
import { RealVector } from "../RealVector.js";
import { AbstractSymmetry } from "./AbstractSymmetry.js";
import { Axis } from "./Axis.js";
import { Direction } from "./Direction.js";
import { Permutation } from "./Permutation.js";
import { SpecialOrbit } from "./SpecialOrbit.js";
import { Symmetry } from "./Symmetry.js";

/**
 * @author David Hall
 * This class is a generalized implementation initially based on the HeptagonalAntiprismSymmetry by Scott Vorthmann
 * @param {PolygonField} field
 * @class
 * @extends AbstractSymmetry
 */
export class AntiprismSymmetry extends AbstractSymmetry {
    /*private*/ preferredAxis: Axis;

    /*private*/ useShear: boolean;

    /*private*/ shearTransform: RealVector[];

    public constructor(field: PolygonField) {
        super(field.polygonSides() * 2, field, "blue", field.isEven() ? null : new AlgebraicMatrix(field.basisVector(3, AlgebraicVector.X), field.basisVector(3, AlgebraicVector.Y), field.basisVector(3, AlgebraicVector.Z).negate()));
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
        this.shearTransform = [new RealVector(1, m10, 0), new RealVector(0, m11, 0), new RealVector(0, 0, 1)];
    }

    /**
     * 
     * @return {PolygonField}
     */
    public getField(): PolygonField {
        return <PolygonField><any>super.getField();
    }

    /**
     * Called by the super constructor.
     */
    createInitialPermutations() {
        const nSides: number = this.getField().polygonSides();
        this.mOrientations[0] = new Permutation(this, null);
        const map1: number[] = (s => { let a=[]; while(s-->0) a.push(0); return a; })(nSides * 2);
        for(let i: number = 0; i < nSides; i++) {{
            map1[i] = (i + 1) % nSides;
            map1[i + nSides] = map1[i] + nSides;
        };}
        this.mOrientations[1] = new Permutation(this, map1);
        const map2: number[] = (s => { let a=[]; while(s-->0) a.push(0); return a; })(map1.length);
        let n: number = nSides * 2;
        for(let i: number = 0; i < map2.length; i++) {{
            n--;
            map2[i] = map1[n];
        };}
        this.mOrientations[nSides] = new Permutation(this, map2);
    }

    /**
     * 
     * @param {string} frameColor
     */
    createFrameOrbit(frameColor: string) {
        const field: PolygonField = this.getField();
        const nSides: number = field.polygonSides();
        const nDiags: number = field.diagonalCount();
        const rotationMatrix: AlgebraicMatrix = this.getRotationMatrix();
        let vX: AlgebraicVector = field.basisVector(3, AlgebraicVector.X);
        let vY: AlgebraicVector = field.basisVector(3, AlgebraicVector.Y);
        let vZ: AlgebraicVector = field.basisVector(3, AlgebraicVector.Z);
        for(let i: number = 0; i < nSides * 2; i++) {{
            if (i === nSides){
                vY = vY.negate();
                if (field.isOdd()){
                    vY = vY.setComponent(AlgebraicVector.X, field.getUnitDiagonal(nDiags - 1).reciprocal());
                }
                vZ = vZ.negate();
            }
            this.mMatrices[i] = new AlgebraicMatrix(vX, vY, vZ);
            vX = rotationMatrix.timesColumn(vX);
            vY = rotationMatrix.timesColumn(vY);
        };}
    }

    /*private*/ rotationMatrix: AlgebraicMatrix;

    public getRotationMatrix(): AlgebraicMatrix {
        if (this.rotationMatrix == null){
            const field: PolygonField = this.getField();
            const diagCount: number = field.diagonalCount();
            const p_x: AlgebraicNumber = field.getUnitDiagonal(diagCount - 3);
            const q_y: AlgebraicNumber = field.getUnitDiagonal(diagCount - (field.isEven() ? 3 : 2));
            const den: AlgebraicNumber = field.getUnitDiagonal(diagCount - 1);
            const num: AlgebraicNumber = field.getUnitDiagonal(1);
            const p: AlgebraicVector = field.origin(3).setComponent(AlgebraicVector.X, p_x.dividedBy(den)).setComponent(AlgebraicVector.Y, num.dividedBy(den));
            const q: AlgebraicVector = field.origin(3).setComponent(AlgebraicVector.X, num.dividedBy(den).negate()).setComponent(AlgebraicVector.Y, q_y.dividedBy(den));
            const zAxis: AlgebraicVector = field.basisVector(3, AlgebraicVector.Z);
            this.rotationMatrix = new AlgebraicMatrix(p, q, zAxis);
        }
        return this.rotationMatrix;
    }

    /**
     * 
     */
    createOtherOrbits() {
    }

    public createStandardOrbits(frameColor: string): AntiprismSymmetry {
        const redOrbit: Direction = this.createZoneOrbit$java_lang_String$int$int$com_vzome_core_algebra_AlgebraicVector$boolean("red", 0, 1, this.mField.basisVector(3, AlgebraicVector.Z), true);
        this.preferredAxis = redOrbit.getAxis$int$int(Symmetry.PLUS, 0);
        const blueFrameVector: AlgebraicVector = this.mField.basisVector(3, AlgebraicVector.X);
        const nSides: number = this.getField().polygonSides();
        const blueOrbit: Direction = this.createZoneOrbit$java_lang_String$int$int$com_vzome_core_algebra_AlgebraicVector$boolean(frameColor, 0, nSides, blueFrameVector, true);
        let greenVector: AlgebraicVector;
        if (this.getField().isOdd()){
            const blueRotatedVector: AlgebraicVector = blueOrbit.getAxis$int$int(Symmetry.PLUS, ((nSides + 1) / 2|0)).normal();
            greenVector = blueFrameVector.minus(blueRotatedVector);
        } else {
            const blueRotatedVector: AlgebraicVector = blueOrbit.getAxis$int$int(Symmetry.PLUS, 1).normal();
            greenVector = blueFrameVector.plus(blueRotatedVector);
        }
        this.createZoneOrbit$java_lang_String$int$int$com_vzome_core_algebra_AlgebraicVector$boolean("green", 0, nSides, greenVector, false);
        const yellowVector: AlgebraicVector = greenVector.plus(redOrbit.getAxis$int$int(Symmetry.PLUS, 1).normal());
        this.createZoneOrbit$java_lang_String$int$int$com_vzome_core_algebra_AlgebraicVector$boolean("yellow", 0, nSides, yellowVector, false);
        return this;
    }

    /**
     * 
     * @return {Axis}
     */
    public getPreferredAxis(): Axis {
        return this.preferredAxis;
    }

    /**
     * 
     * @param {AlgebraicVector} v
     * @return {RealVector}
     */
    public embedInR3(v: AlgebraicVector): RealVector {
        const rv: RealVector = super.embedInR3(v);
        if (this.useShear){
            const sums: number[] = [0, 0, 0];
            for(let i: number = 0; i < this.shearTransform.length; i++) {{
                sums[i] += (<any>Math).fround(this.shearTransform[i].x * rv.x);
                sums[i] += (<any>Math).fround(this.shearTransform[i].y * rv.y);
                sums[i] += (<any>Math).fround(this.shearTransform[i].z * rv.z);
            };}
            return new RealVector(sums[0], sums[1], sums[2]);
        }
        return rv;
    }

    /**
     * 
     * @param {AlgebraicVector} v
     * @return {double[]}
     */
    public embedInR3Double(v: AlgebraicVector): number[] {
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
     * @return {AlgebraicVector[]}
     */
    public getOrbitTriangle(): AlgebraicVector[] {
        const field: PolygonField = this.getField();
        const diagCount: number = field.diagonalCount();
        const sideVertex: AlgebraicVector = field.basisVector(3, AlgebraicVector.Z);
        const blueOrbit: Direction = this.getSpecialOrbit(SpecialOrbit.BLUE);
        const topVertex: AlgebraicVector = blueOrbit.getAxis$int$int(Symmetry.PLUS, diagCount).normal();
        const bottomVert: AlgebraicVector = blueOrbit.getAxis$int$int(Symmetry.PLUS, diagCount + 1).normal();
        const orthoVertex: AlgebraicVector = AlgebraicVectors.getCentroid([topVertex, bottomVert]);
        return [orthoVertex, sideVertex, topVertex];
    }

    /**
     * 
     * @param {SpecialOrbit} which
     * @return {Direction}
     */
    public getSpecialOrbit(which: SpecialOrbit): Direction {
        switch((which)) {
        case SpecialOrbit.BLUE:
            return this.getDirection("blue");
        case SpecialOrbit.RED:
            return this.getDirection("red");
        case SpecialOrbit.YELLOW:
            return this.getDirection(this.getField().isEven() ? "green" : "blue");
        default:
            return null;
        }
    }
}
AntiprismSymmetry["__class"] = "com.vzome.core.math.symmetry.AntiprismSymmetry";
AntiprismSymmetry["__interfaces"] = ["com.vzome.core.math.symmetry.Symmetry","com.vzome.core.math.symmetry.Embedding"];
