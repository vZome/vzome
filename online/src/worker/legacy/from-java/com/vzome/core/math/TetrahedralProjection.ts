import { AlgebraicField } from "../algebra/AlgebraicField.js";
import { AlgebraicNumber } from "../algebra/AlgebraicNumber.js";
import { AlgebraicVector } from "../algebra/AlgebraicVector.js";
import { Projection } from "./Projection.js";
import { Element } from "../../../../org/w3c/dom/Element.js";

/**
 * @author David Hall
 * @param {*} field
 * @class
 */
export class TetrahedralProjection implements Projection {
    /*private*/ field: AlgebraicField;

    /*private*/ basis: AlgebraicVector[];

    public constructor(field: AlgebraicField) {
        if (this.field === undefined) { this.field = null; }
        if (this.basis === undefined) { this.basis = null; }
        this.field = field;
        const pos: AlgebraicNumber = field.one();
        const neg: AlgebraicNumber = pos.negate();
        this.basis = [null, null, null, null];
        this.basis[0] = new AlgebraicVector(pos, pos, pos);
        this.basis[1] = new AlgebraicVector(pos, neg, neg);
        this.basis[2] = new AlgebraicVector(neg, pos, neg);
        this.basis[3] = new AlgebraicVector(neg, neg, pos);
    }

    /**
     * 
     * @param {AlgebraicVector} source
     * @param {boolean} wFirst
     * @return {AlgebraicVector}
     */
    public projectImage(source: AlgebraicVector, wFirst: boolean): AlgebraicVector {
        let result: AlgebraicVector = this.field.origin(this.basis[0].dimension());
        let pos: number = wFirst ? 0 : this.basis.length - 1;
        for(let index = 0; index < this.basis.length; index++) {
            let unitVector = this.basis[index];
            {
                const scalar: AlgebraicNumber = source.getComponent(pos);
                result = result.plus(unitVector.scale(scalar));
                pos = (pos + 1) % this.basis.length;
            }
        }
        return result;
    }

    /**
     * 
     * @param {*} element
     */
    public getXmlAttributes(element: Element) {
    }

    /**
     * 
     * @param {*} xml
     */
    public setXmlAttributes(xml: Element) {
    }

    /**
     * 
     * @return {string}
     */
    public getProjectionName(): string {
        return "Tetrahedral";
    }
}
TetrahedralProjection["__class"] = "com.vzome.core.math.TetrahedralProjection";
TetrahedralProjection["__interfaces"] = ["com.vzome.core.math.Projection"];
