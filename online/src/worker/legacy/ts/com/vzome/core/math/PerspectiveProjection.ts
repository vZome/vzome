/* Generated from Java with JSweet 3.2.0-SNAPSHOT - http://www.jsweet.org */
namespace com.vzome.core.math {
    export class PerspectiveProjection implements com.vzome.core.math.Projection {
        /*private*/ field: com.vzome.core.algebra.AlgebraicField;

        /*private*/ cameraDist: com.vzome.core.algebra.AlgebraicNumber;

        public constructor(field: com.vzome.core.algebra.AlgebraicField, cameraDist: com.vzome.core.algebra.AlgebraicNumber) {
            if (this.field === undefined) { this.field = null; }
            if (this.cameraDist === undefined) { this.cameraDist = null; }
            if (this.minDenom === undefined) { this.minDenom = null; }
            if (this.minDenomValue === undefined) { this.minDenomValue = 0; }
            this.field = field;
            this.cameraDist = cameraDist;
        }

        /*private*/ minDenom: com.vzome.core.algebra.AlgebraicNumber;

        /*private*/ minDenomValue: number;

        /**
         * 
         * @param {com.vzome.core.algebra.AlgebraicVector} source
         * @param {boolean} wFirst
         * @return {com.vzome.core.algebra.AlgebraicVector}
         */
        public projectImage(source: com.vzome.core.algebra.AlgebraicVector, wFirst: boolean): com.vzome.core.algebra.AlgebraicVector {
            const result: com.vzome.core.algebra.AlgebraicVector = this.field.origin(4);
            const w: com.vzome.core.algebra.AlgebraicNumber = source.getComponent(0);
            let denom: com.vzome.core.algebra.AlgebraicNumber = this.cameraDist['minus$com_vzome_core_algebra_AlgebraicNumber'](w);
            if (this.minDenom == null){
                this.minDenom = this.field['createPower$int'](-5);
                this.minDenomValue = this.minDenom.evaluate();
            }
            const denomValue: number = denom.evaluate();
            if (denomValue < this.minDenomValue){
                denom = this.minDenom;
            }
            const numerator: com.vzome.core.algebra.AlgebraicNumber = denom.reciprocal();
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
        public getXmlAttributes(element: org.w3c.dom.Element) {
            if (this.cameraDist != null){
                com.vzome.xml.DomUtils.addAttribute(element, "cameraDist", this.cameraDist.toString(com.vzome.core.algebra.AlgebraicField.ZOMIC_FORMAT));
            }
        }

        /**
         * 
         * @param {*} xml
         */
        public setXmlAttributes(xml: org.w3c.dom.Element) {
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


}

