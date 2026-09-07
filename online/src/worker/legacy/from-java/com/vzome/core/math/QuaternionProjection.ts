import { AlgebraicField } from "../algebra/AlgebraicField.js";
import { AlgebraicVector } from "../algebra/AlgebraicVector.js";
import { Quaternion } from "../algebra/Quaternion.js";
import { Projection } from "./Projection.js";
import { DomUtils } from "../../xml/DomUtils.js";
import { Element } from "../../../../org/w3c/dom/Element.js";

/**
 * @param {*} field
 * @param {AlgebraicVector} leftQuat
 * @param {AlgebraicVector} rightQuat
 * @class
 */
export class QuaternionProjection implements Projection {
    /*private*/ quaternions: Quaternion[][];

    /*private*/ field: AlgebraicField;

    public constructor(field: AlgebraicField, leftQuat: AlgebraicVector, rightQuat: AlgebraicVector) {
        this.quaternions = <any> (function(dims) { let allocate = function(dims) { if (dims.length === 0) { return null; } else { let array = []; for(let i = 0; i < dims[0]; i++) { array.push(allocate(dims.slice(1))); } return array; }}; return allocate(dims);})([2, 2]);
        if (this.field === undefined) { this.field = null; }
        this.field = field;
        this.setQuaternion(leftQuat, QuaternionProjection.LEFT);
        this.setQuaternion(rightQuat, QuaternionProjection.RIGHT);
    }

    /**
     * 
     * @param {AlgebraicVector} source
     * @param {boolean} wFirst
     * @return {AlgebraicVector}
     */
    public projectImage(source: AlgebraicVector, wFirst: boolean): AlgebraicVector {
        let result: AlgebraicVector = source;
        const leftQuat: Quaternion = this.getQuaternion(QuaternionProjection.LEFT, wFirst);
        const rightQuat: Quaternion = this.getQuaternion(QuaternionProjection.RIGHT, wFirst);
        if (rightQuat != null){
            if (leftQuat != null){
                result = leftQuat.rightMultiply(result);
                console.info("left mult: " + result.toString());
            }
            result = rightQuat.leftMultiply(result);
        } else {
            result = leftQuat.rightMultiply(result);
        }
        return this.field.projectTo3d(result, wFirst);
    }

    static LEFT: number = 0;

    static RIGHT: number = 1;

    static WFIRST: number = 0;

    static WLAST: number = 1;

    /*private*/ setQuaternion(quatVector: AlgebraicVector, hand: number) {
        this.quaternions[hand][QuaternionProjection.WFIRST] = quatVector == null ? null : new Quaternion(this.field, quatVector.inflateTo4d$boolean(true));
        this.quaternions[hand][QuaternionProjection.WLAST] = quatVector == null ? null : new Quaternion(this.field, quatVector.inflateTo4d$boolean(false));
    }

    /*private*/ getQuaternion(hand: number, wFirst: boolean): Quaternion {
        return this.quaternions[hand][wFirst ? QuaternionProjection.WFIRST : QuaternionProjection.WLAST];
    }

    static RIGHT_QUATERNION_ATTRIBUTENAME: string = "quaternion";

    static LEFT_QUATERNION_ATTRIBUTENAME: string = "leftQuaternion";

    /**
     * 
     * @param {*} element
     */
    public getXmlAttributes(element: Element) {
        const leftQuat: Quaternion = this.getQuaternion(QuaternionProjection.LEFT, true);
        const rightQuat: Quaternion = this.getQuaternion(QuaternionProjection.RIGHT, true);
        if (leftQuat != null){
            DomUtils.addAttribute(element, QuaternionProjection.LEFT_QUATERNION_ATTRIBUTENAME, leftQuat.getVector().toParsableString());
        }
        if (rightQuat != null){
            DomUtils.addAttribute(element, QuaternionProjection.RIGHT_QUATERNION_ATTRIBUTENAME, rightQuat.getVector().toParsableString());
        }
    }

    /**
     * 
     * @param {*} xml
     */
    public setXmlAttributes(xml: Element) {
        this.setQuaternion(this.parseRationalVector(xml, QuaternionProjection.LEFT_QUATERNION_ATTRIBUTENAME), QuaternionProjection.LEFT);
        this.setQuaternion(this.parseRationalVector(xml, QuaternionProjection.RIGHT_QUATERNION_ATTRIBUTENAME), QuaternionProjection.RIGHT);
    }

    /*private*/ parseRationalVector(xml: Element, attrName: string): AlgebraicVector {
        const nums: string = xml.getAttribute(attrName);
        if (nums == null || /* isEmpty */(nums.length === 0))return null;
        const loc: AlgebraicVector = this.field.parseVector(nums);
        return loc;
    }

    /**
     * 
     * @return {string}
     */
    public getProjectionName(): string {
        return "Quaternion";
    }
}
QuaternionProjection["__class"] = "com.vzome.core.math.QuaternionProjection";
QuaternionProjection["__interfaces"] = ["com.vzome.core.math.Projection"];
