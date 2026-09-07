import { AlgebraicField } from "../algebra/AlgebraicField.js";
import { AlgebraicNumber } from "../algebra/AlgebraicNumber.js";
import { AlgebraicVector } from "../algebra/AlgebraicVector.js";
import { Projection } from "./Projection.js";
import { DomUtils } from "../../xml/DomUtils.js";
import { Element } from "../../../../org/w3c/dom/Element.js";

export class PerspectiveProjection implements Projection {
    /*private*/ field: AlgebraicField;

    /*private*/ cameraDist: AlgebraicNumber;

    public constructor(field: AlgebraicField, cameraDist: AlgebraicNumber) {
        if (this.field === undefined) { this.field = null; }
        if (this.cameraDist === undefined) { this.cameraDist = null; }
        if (this.minDenom === undefined) { this.minDenom = null; }
        if (this.minDenomValue === undefined) { this.minDenomValue = 0; }
        this.field = field;
        this.cameraDist = cameraDist;
    }

    /*private*/ minDenom: AlgebraicNumber;

    /*private*/ minDenomValue: number;

    /**
     * 
     * @param {AlgebraicVector} source
     * @param {boolean} wFirst
     * @return {AlgebraicVector}
     */
    public projectImage(source: AlgebraicVector, wFirst: boolean): AlgebraicVector {
        const result: AlgebraicVector = this.field.origin(4);
        const w: AlgebraicNumber = source.getComponent(0);
        let denom: AlgebraicNumber = this.cameraDist['minus$com_vzome_core_algebra_AlgebraicNumber'](w);
        if (this.minDenom == null){
            this.minDenom = this.field['createPower$int'](-5);
            this.minDenomValue = this.minDenom.evaluate();
        }
        const denomValue: number = denom.evaluate();
        if (denomValue < this.minDenomValue){
            denom = this.minDenom;
        }
        const numerator: AlgebraicNumber = denom.reciprocal();
        result.setComponent(0, this.field.one());
        result.setComponent(1, source.getComponent(1)['times$com_vzome_core_algebra_AlgebraicNumber'](numerator));
        result.setComponent(2, source.getComponent(2)['times$com_vzome_core_algebra_AlgebraicNumber'](numerator));
        result.setComponent(3, source.getComponent(3)['times$com_vzome_core_algebra_AlgebraicNumber'](numerator));
        return result;
    }

    /**
     * 
     * @param {*} element
     */
    public getXmlAttributes(element: Element) {
        if (this.cameraDist != null){
            DomUtils.addAttribute(element, "cameraDist", this.cameraDist.toString(AlgebraicField.ZOMIC_FORMAT));
        }
    }

    /**
     * 
     * @param {*} xml
     */
    public setXmlAttributes(xml: Element) {
        const nums: string = xml.getAttribute("cameraDist");
        if (nums == null || /* isEmpty */(nums.length === 0))return;
        this.cameraDist = this.field.parseNumber(nums);
    }

    /**
     * 
     * @return {string}
     */
    public getProjectionName(): string {
        return "Perspective";
    }
}
PerspectiveProjection["__class"] = "com.vzome.core.math.PerspectiveProjection";
PerspectiveProjection["__interfaces"] = ["com.vzome.core.math.Projection"];
