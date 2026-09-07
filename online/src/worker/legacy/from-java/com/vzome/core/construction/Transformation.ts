import { AlgebraicField } from "../algebra/AlgebraicField.js";
import { AlgebraicMatrix } from "../algebra/AlgebraicMatrix.js";
import { AlgebraicVector } from "../algebra/AlgebraicVector.js";
import { Construction } from "./Construction.js";
import { Point } from "./Point.js";
import { Polygon } from "./Polygon.js";
import { Segment } from "./Segment.js";
import { TransformedPoint } from "./TransformedPoint.js";
import { TransformedPolygon } from "./TransformedPolygon.js";
import { TransformedSegment } from "./TransformedSegment.js";
import { Document } from "../../../../org/w3c/dom/Document.js";
import { Element } from "../../../../org/w3c/dom/Element.js";

/**
 * @author Scott Vorthmann
 * @extends Construction
 * @class
 */
export abstract class Transformation extends Construction {
    /**
     * 
     * @return {boolean}
     */
    public is3d(): boolean {
        return true;
    }

    /*private*/ mTransform: AlgebraicMatrix;

    mOffset: AlgebraicVector;

    constructor(field: AlgebraicField) {
        super(field);
        if (this.mTransform === undefined) { this.mTransform = null; }
        if (this.mOffset === undefined) { this.mOffset = null; }
    }

    /**
     * 
     * @param {*} that
     * @return {boolean}
     */
    public equals(that: any): boolean {
        if (this === that){
            return true;
        }
        if (that == null){
            return false;
        }
        if (!(that != null && that instanceof <any>Transformation)){
            return false;
        }
        const other: Transformation = <Transformation>that;
        if (this.mOffset == null){
            if (other.mOffset != null){
                return false;
            }
        } else if (!this.mOffset.equals(other.mOffset)){
            return false;
        }
        if (this.mTransform == null){
            if (other.mTransform != null){
                return false;
            }
        } else if (!this.mTransform.equals(other.mTransform)){
            return false;
        }
        return true;
    }

    setStateVariables(transform: AlgebraicMatrix, offset: AlgebraicVector, impossible: boolean): boolean {
        if (impossible){
            if (this.isImpossible())return false;
            this.setImpossible(true);
            return true;
        }
        if (transform != null && transform.equals(this.mTransform) && offset.equals(this.mOffset) && !this.isImpossible())return false;
        this.mTransform = transform;
        this.mOffset = offset;
        this.setImpossible(false);
        return true;
    }

    public transform$com_vzome_core_algebra_AlgebraicVector(arg: AlgebraicVector): AlgebraicVector {
        arg = arg.minus(this.mOffset);
        arg = this.mTransform.timesColumn(arg);
        arg = arg.plus(this.mOffset);
        return arg;
    }

    public transform(arg?: any): any {
        if (((arg != null && arg instanceof <any>AlgebraicVector) || arg === null)) {
            return <any>this.transform$com_vzome_core_algebra_AlgebraicVector(arg);
        } else if (((arg != null && arg instanceof <any>Construction) || arg === null)) {
            return <any>this.transform$com_vzome_core_construction_Construction(arg);
        } else throw new Error('invalid overload');
    }

    public transform$com_vzome_core_construction_Construction(c: Construction): Construction {
        if (c != null && c instanceof <any>Point){
            return new TransformedPoint(this, <Point>c);
        } else if (c != null && c instanceof <any>Segment){
            return new TransformedSegment(this, <Segment>c);
        } else if (c != null && c instanceof <any>Polygon){
            return new TransformedPolygon(this, <Polygon>c);
        } else {
            return null;
        }
    }

    /**
     * 
     * @param {*} doc
     * @return {*}
     */
    public getXml(doc: Document): Element {
        const result: Element = doc.createElement("transformation");
        return result;
    }
}
Transformation["__class"] = "com.vzome.core.construction.Transformation";


export namespace Transformation {

    export class Identity extends Transformation {
        public transform$int_A(arg: number[]): number[] {
            return arg;
        }

        public transform(arg?: any): any {
            if (((arg != null && arg instanceof <any>Array && (arg.length == 0 || arg[0] == null ||(typeof arg[0] === 'number'))) || arg === null)) {
                return <any>this.transform$int_A(arg);
            } else if (((arg != null && arg instanceof <any>AlgebraicVector) || arg === null)) {
                return super.transform(arg);
            } else if (((arg != null && arg instanceof <any>Construction) || arg === null)) {
                return <any>this.transform$com_vzome_core_construction_Construction(arg);
            } else throw new Error('invalid overload');
        }

        public constructor(field: AlgebraicField) {
            super(field);
        }

        public attach() {
        }

        public detach() {
        }

        /**
         * 
         * @return {boolean}
         */
        mapParamsToState(): boolean {
            return true;
        }
    }
    Identity["__class"] = "com.vzome.core.construction.Transformation.Identity";

}
