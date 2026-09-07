import { java, javaemul } from "../../../../../candies/j4ts-2.1.0-SNAPSHOT/bundle.js";
import { AlgebraicField } from "../algebra/AlgebraicField.js";
import { AlgebraicNumber } from "../algebra/AlgebraicNumber.js";
import { AlgebraicVector } from "../algebra/AlgebraicVector.js";
import { AlgebraicVectors } from "../algebra/AlgebraicVectors.js";
import { Construction } from "../construction/Construction.js";
import { FreePoint } from "../construction/FreePoint.js";
import { Point } from "../construction/Point.js";
import { PolygonFromVertices } from "../construction/PolygonFromVertices.js";
import { RealVector } from "../math/RealVector.js";
import { Embedding } from "../math/symmetry/Embedding.js";
import { ManifestationImpl } from "./ManifestationImpl.js";
import { Panel } from "./Panel.js";

/**
 * Create a panel from a list of AlgebraicVectors
 * 
 * @param {*} vertices
 * @class
 * @extends ManifestationImpl
 */
export class PanelImpl extends ManifestationImpl implements Panel {
    /*private*/ mVertices: java.util.List<AlgebraicVector>;

    /*private*/ zoneVector: AlgebraicVector;

    /*private*/ label: string;

    public constructor(vertices: java.util.List<AlgebraicVector>) {
        super();
        if (this.mVertices === undefined) { this.mVertices = null; }
        if (this.zoneVector === undefined) { this.zoneVector = null; }
        if (this.label === undefined) { this.label = null; }
        this.mVertices = <any>(new java.util.ArrayList<any>(vertices));
    }

    /**
     * 
     * @return {AlgebraicVector}
     */
    public getZoneVector(): AlgebraicVector {
        if (this.zoneVector != null)return this.zoneVector; else return this.getNormal$();
    }

    /**
     * 
     * @param {AlgebraicVector} vector
     */
    public setZoneVector(vector: AlgebraicVector) {
        this.zoneVector = vector;
    }

    /**
     * 
     * @return {AlgebraicVector}
     */
    public getLocation(): AlgebraicVector {
        return null;
    }

    /**
     * 
     * @return {AlgebraicVector}
     */
    public getFirstVertex(): AlgebraicVector {
        return this.mVertices.get(0);
    }

    /**
     * 
     * @return {AlgebraicVector}
     */
    public getCentroid(): AlgebraicVector {
        return AlgebraicVectors.calculateCentroid(this.mVertices);
    }

    /**
     * 
     * @return {Construction}
     */
    public toConstruction(): Construction {
        const first: Construction = this.getFirstConstruction();
        if (first != null && first.is3d())return first;
        const field: AlgebraicField = this.mVertices.get(0).getField();
        const projected: java.util.List<Point> = <any>(this.mVertices.stream().map<any>(((field) => {
            return (pt) => new FreePoint(field.projectTo3d(pt, true))
        })(field)).collect<any, any>(java.util.stream.Collectors.toList<any>()));
        return new PolygonFromVertices(projected);
    }

    /**
     * 
     * @return {*}
     */
    public iterator(): java.util.Iterator<AlgebraicVector> {
        return this.mVertices.iterator();
    }

    /**
     * 
     * @return {number}
     */
    public getVertexCount(): number {
        return this.mVertices.size();
    }

    /**
     * 
     * @return {number}
     */
    public hashCode(): number {
        const len: number = this.mVertices.size();
        if (len === 0)return 0;
        let val: number = /* hashCode */(<any>((o: any) => { if (o.hashCode) { return o.hashCode(); } else { return o.toString().split('').reduce((prevHash, currVal) => (((prevHash << 5) - prevHash) + currVal.charCodeAt(0))|0, 0); }})((this.mVertices.get(0))));
        for(let i: number = 1; i < len; i++) {val ^= /* hashCode */(<any>((o: any) => { if (o.hashCode) { return o.hashCode(); } else { return o.toString().split('').reduce((prevHash, currVal) => (((prevHash << 5) - prevHash) + currVal.charCodeAt(0))|0, 0); }})((this.mVertices.get(i))));}
        return val;
    }

    /**
     * 
     * @param {*} other
     * @return {boolean}
     */
    public equals(other: any): boolean {
        if (other == null)return false;
        if (other === this)return true;
        if (!(other != null && other instanceof <any>PanelImpl))return false;
        const panel: PanelImpl = <PanelImpl>other;
        const size: number = this.mVertices.size();
        if (size !== panel.mVertices.size())return false;
        const found: boolean[] = (s => { let a=[]; while(s-->0) a.push(false); return a; })(size);
        for(let i: number = 0; i < size; i++) {{
            let found_i: boolean = false;
            for(let j: number = 0; j < size; j++) {{
                if (found[j])continue;
                if (this.mVertices.get(j).equals(panel.mVertices.get(i))){
                    found[j] = true;
                    found_i = true;
                    break;
                }
            };}
            if (!found_i)return false;
        };}
        for(let j: number = 0; j < size; j++) {if (!found[j])return false;;}
        return true;
    }

    public getNormal$(): AlgebraicVector {
        const v0: AlgebraicVector = this.mVertices.get(0);
        const v1: AlgebraicVector = this.mVertices.get(1);
        const v2: AlgebraicVector = this.mVertices.get(2);
        return AlgebraicVectors.getNormal$com_vzome_core_algebra_AlgebraicVector$com_vzome_core_algebra_AlgebraicVector$com_vzome_core_algebra_AlgebraicVector(v0, v1, v2);
    }

    public getNormal$com_vzome_core_math_symmetry_Embedding(embedding: Embedding): RealVector {
        const v0: AlgebraicVector = this.mVertices.get(0);
        const v1: AlgebraicVector = this.mVertices.get(1);
        const v2: AlgebraicVector = this.mVertices.get(2);
        const rv1: RealVector = embedding.embedInR3(v1.minus(v0));
        const rv2: RealVector = embedding.embedInR3(v2.minus(v0));
        return rv1.cross(rv2);
    }

    /**
     * 
     * @param {*} embedding
     * @return {RealVector}
     */
    public getNormal(embedding?: any): any {
        if (((embedding != null && (embedding.constructor != null && embedding.constructor["__interfaces"] != null && embedding.constructor["__interfaces"].indexOf("com.vzome.core.math.symmetry.Embedding") >= 0)) || embedding === null)) {
            return <any>this.getNormal$com_vzome_core_math_symmetry_Embedding(embedding);
        } else if (embedding === undefined) {
            return <any>this.getNormal$();
        } else throw new Error('invalid overload');
    }

    /**
     * 
     * @return {string}
     */
    public toString(): string {
        const buf: java.lang.StringBuilder = new java.lang.StringBuilder("panel: ");
        let delim: string = "";
        for(let index=this.mVertices.iterator();index.hasNext();) {
            let vertex = index.next();
            {
                buf.append(delim).append(vertex.toString());
                delim = ", ";
            }
        }
        return buf.toString();
    }

    /**
     * 
     * @param {string} label
     */
    public setLabel(label: string) {
        this.label = label;
    }

    /**
     * 
     * @return {string}
     */
    public getLabel(): string {
        return this.label;
    }

    /**
     * 
     * @return {*}
     */
    public getQuadrea(): AlgebraicNumber {
        const field: AlgebraicField = this.mVertices.get(0).getField();
        return field.one();
    }
}
PanelImpl["__class"] = "com.vzome.core.model.PanelImpl";
PanelImpl["__interfaces"] = ["com.vzome.core.model.HasRenderedObject","com.vzome.core.model.GroupElement","com.vzome.core.model.Panel","com.vzome.core.model.Manifestation","java.lang.Iterable"];
