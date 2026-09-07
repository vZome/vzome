import { AlgebraicField } from "../algebra/AlgebraicField.js";
import { AlgebraicNumber } from "../algebra/AlgebraicNumber.js";
import { AlgebraicVector } from "../algebra/AlgebraicVector.js";
import { Projection } from "./Projection.js";
import { Element } from "../../../../org/w3c/dom/Element.js";

/**
 * @author Scott Vorthmann
 * @param {*} field
 * @class
 */
export class SixCubeProjection implements Projection {
    /*private*/ field: AlgebraicField;

    /*private*/ basis: AlgebraicVector[];

    public constructor(field: AlgebraicField) {
        if (this.field === undefined) { this.field = null; }
        if (this.basis === undefined) { this.basis = null; }
        this.field = field;
        const zero: AlgebraicNumber = field.zero();
        const one: AlgebraicNumber = field.one();
        const nOne: AlgebraicNumber = one.negate();
        const phi: AlgebraicNumber = field['createPower$int'](1);
        this.basis = [null, null, null, null, null, null];
        this.basis[0] = new AlgebraicVector(phi, one, zero);
        this.basis[1] = new AlgebraicVector(phi, nOne, zero);
        this.basis[2] = new AlgebraicVector(zero, phi, one);
        this.basis[3] = new AlgebraicVector(zero, phi, nOne);
        this.basis[4] = new AlgebraicVector(one, zero, phi);
        this.basis[5] = new AlgebraicVector(nOne, zero, phi);
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
        return "SixCube";
    }
}
SixCubeProjection["__class"] = "com.vzome.core.math.SixCubeProjection";
SixCubeProjection["__interfaces"] = ["com.vzome.core.math.Projection"];
