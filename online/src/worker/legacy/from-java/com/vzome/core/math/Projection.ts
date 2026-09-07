import { AlgebraicField } from "../algebra/AlgebraicField.js";
import { AlgebraicVector } from "../algebra/AlgebraicVector.js";
import { Element } from "../../../../org/w3c/dom/Element.js";

export interface Projection {
    projectImage(source: AlgebraicVector, wFirst: boolean): AlgebraicVector;

    getXmlAttributes(element: Element);

    setXmlAttributes(xml: Element);

    getProjectionName(): string;
}

export namespace Projection {

    export class Default implements Projection {
        field: AlgebraicField;

        public constructor(field: AlgebraicField) {
            if (this.field === undefined) { this.field = null; }
            this.field = field;
        }

        /**
         * 
         * @param {AlgebraicVector} source
         * @param {boolean} wFirst
         * @return {AlgebraicVector}
         */
        public projectImage(source: AlgebraicVector, wFirst: boolean): AlgebraicVector {
            return this.field.projectTo3d(source, wFirst);
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
            return "";
        }
    }
    Default["__class"] = "com.vzome.core.math.Projection.Default";
    Default["__interfaces"] = ["com.vzome.core.math.Projection"];


}
