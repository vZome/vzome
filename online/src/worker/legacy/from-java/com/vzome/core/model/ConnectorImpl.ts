import { AlgebraicField } from "../algebra/AlgebraicField.js";
import { AlgebraicVector } from "../algebra/AlgebraicVector.js";
import { Construction } from "../construction/Construction.js";
import { FreePoint } from "../construction/FreePoint.js";
import { Connector } from "./Connector.js";
import { ManifestationImpl } from "./ManifestationImpl.js";

/**
 * @author Scott Vorthmann
 * @param {AlgebraicVector} loc
 * @class
 * @extends ManifestationImpl
 */
export class ConnectorImpl extends ManifestationImpl implements Connector {
    public constructor(loc: AlgebraicVector) {
        super();
        if (this.m_center === undefined) { this.m_center = null; }
        if (this.label === undefined) { this.label = null; }
        this.m_center = loc;
    }

    /*private*/ m_center: AlgebraicVector;

    /*private*/ label: string;

    /**
     * 
     * @return {AlgebraicVector}
     */
    public getLocation(): AlgebraicVector {
        return this.m_center;
    }

    /**
     * 
     * @return {AlgebraicVector}
     */
    public getCentroid(): AlgebraicVector {
        return this.m_center;
    }

    /**
     * 
     * @return {Construction}
     */
    public toConstruction(): Construction {
        const first: Construction = this.getFirstConstruction();
        if (first != null && first.is3d())return first;
        const field: AlgebraicField = this.m_center.getField();
        return new FreePoint(field.projectTo3d(this.m_center, true));
    }

    /**
     * 
     * @return {number}
     */
    public hashCode(): number {
        return /* hashCode */(<any>((o: any) => { if (o.hashCode) { return o.hashCode(); } else { return o.toString().split('').reduce((prevHash, currVal) => (((prevHash << 5) - prevHash) + currVal.charCodeAt(0))|0, 0); }})(this.m_center));
    }

    /**
     * 
     * @param {*} other
     * @return {boolean}
     */
    public equals(other: any): boolean {
        if (other == null)return false;
        if (other === this)return true;
        if (!(other != null && other instanceof <any>ConnectorImpl))return false;
        const conn: ConnectorImpl = <ConnectorImpl>other;
        return this.getLocation().equals(conn.getLocation());
    }

    /**
     * 
     * @param {*} other
     * @return {number}
     */
    public compareTo(other: Connector): number {
        if (this === other){
            return 0;
        }
        if (/* equals */(<any>((o1: any, o2: any) => { if (o1 && o1.equals) { return o1.equals(o2); } else { return o1 === o2; } })(other,this))){
            return 0;
        }
        return this.getLocation().compareTo(other.getLocation());
    }

    /**
     * 
     * @return {string}
     */
    public toString(): string {
        return "connector at " + this.m_center.toString();
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
}
ConnectorImpl["__class"] = "com.vzome.core.model.ConnectorImpl";
ConnectorImpl["__interfaces"] = ["com.vzome.core.model.HasRenderedObject","com.vzome.core.model.GroupElement","com.vzome.core.model.Connector","java.lang.Comparable","com.vzome.core.model.Manifestation"];
