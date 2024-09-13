/* Generated from Java with JSweet 3.2.0-SNAPSHOT - http://www.jsweet.org */
namespace com.vzome.core.math {
    export interface Projection {
        projectImage(source: com.vzome.core.algebra.AlgebraicVector, wFirst: boolean): com.vzome.core.algebra.AlgebraicVector;

        getXmlAttributes(element: org.w3c.dom.Element);

        setXmlAttributes(xml: org.w3c.dom.Element);

        getProjectionName(): string;
    }

    export namespace Projection {

        export class Default implements com.vzome.core.math.Projection {
            field: com.vzome.core.algebra.AlgebraicField;

            public constructor(field: com.vzome.core.algebra.AlgebraicField) {
                if (this.field === undefined) { this.field = null; }
                this.field = field;
            }

            /**
             * 
             * @param {com.vzome.core.algebra.AlgebraicVector} source
             * @param {boolean} wFirst
             * @return {com.vzome.core.algebra.AlgebraicVector}
             */
            public projectImage(source: com.vzome.core.algebra.AlgebraicVector, wFirst: boolean): com.vzome.core.algebra.AlgebraicVector {
                return this.field.projectTo3d(source, wFirst);
            }

            /**
             * 
             * @param {*} element
             */
            public getXmlAttributes(element: org.w3c.dom.Element) {
            }

            /**
             * 
             * @param {*} xml
             */
            public setXmlAttributes(xml: org.w3c.dom.Element) {
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

}

