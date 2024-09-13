/* Generated from Java with JSweet 3.2.0-SNAPSHOT - http://www.jsweet.org */
namespace com.vzome.core.math {
    /**
     * @param {*} field
     * @param {com.vzome.core.algebra.AlgebraicVector} leftQuat
     * @param {com.vzome.core.algebra.AlgebraicVector} rightQuat
     * @class
     */
    export class QuaternionProjection implements com.vzome.core.math.Projection {
        /*private*/ quaternions: com.vzome.core.algebra.Quaternion[][];

        /*private*/ field: com.vzome.core.algebra.AlgebraicField;

        public constructor(field: com.vzome.core.algebra.AlgebraicField, leftQuat: com.vzome.core.algebra.AlgebraicVector, rightQuat: com.vzome.core.algebra.AlgebraicVector) {
            this.quaternions = <any> (function(dims) { let allocate = function(dims) { if (dims.length === 0) { return null; } else { let array = []; for(let i = 0; i < dims[0]; i++) { array.push(allocate(dims.slice(1))); } return array; }}; return allocate(dims);})([2, 2]);
            if (this.field === undefined) { this.field = null; }
            this.field = field;
            this.setQuaternion(leftQuat, QuaternionProjection.LEFT);
            this.setQuaternion(rightQuat, QuaternionProjection.RIGHT);
        }

        /**
         * 
         * @param {com.vzome.core.algebra.AlgebraicVector} source
         * @param {boolean} wFirst
         * @return {com.vzome.core.algebra.AlgebraicVector}
         */
        public projectImage(source: com.vzome.core.algebra.AlgebraicVector, wFirst: boolean): com.vzome.core.algebra.AlgebraicVector {
            let result: com.vzome.core.algebra.AlgebraicVector = source;
            const leftQuat: com.vzome.core.algebra.Quaternion = this.getQuaternion(QuaternionProjection.LEFT, wFirst);
            const rightQuat: com.vzome.core.algebra.Quaternion = this.getQuaternion(QuaternionProjection.RIGHT, wFirst);
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

        /*private*/ setQuaternion(quatVector: com.vzome.core.algebra.AlgebraicVector, hand: number) {
            this.quaternions[hand][QuaternionProjection.WFIRST] = quatVector == null ? null : new com.vzome.core.algebra.Quaternion(this.field, quatVector.inflateTo4d$boolean(true));
            this.quaternions[hand][QuaternionProjection.WLAST] = quatVector == null ? null : new com.vzome.core.algebra.Quaternion(this.field, quatVector.inflateTo4d$boolean(false));
        }

        /*private*/ getQuaternion(hand: number, wFirst: boolean): com.vzome.core.algebra.Quaternion {
            return this.quaternions[hand][wFirst ? QuaternionProjection.WFIRST : QuaternionProjection.WLAST];
        }

        static RIGHT_QUATERNION_ATTRIBUTENAME: string = "quaternion";

        static LEFT_QUATERNION_ATTRIBUTENAME: string = "leftQuaternion";

        /**
         * 
         * @param {*} element
         */
        public getXmlAttributes(element: org.w3c.dom.Element) {
            const leftQuat: com.vzome.core.algebra.Quaternion = this.getQuaternion(QuaternionProjection.LEFT, true);
            const rightQuat: com.vzome.core.algebra.Quaternion = this.getQuaternion(QuaternionProjection.RIGHT, true);
            if (leftQuat != null){
                com.vzome.xml.DomUtils.addAttribute(element, QuaternionProjection.LEFT_QUATERNION_ATTRIBUTENAME, leftQuat.getVector().toParsableString());
            }
            if (rightQuat != null){
                com.vzome.xml.DomUtils.addAttribute(element, QuaternionProjection.RIGHT_QUATERNION_ATTRIBUTENAME, rightQuat.getVector().toParsableString());
            }
        }

        /**
         * 
         * @param {*} xml
         */
        public setXmlAttributes(xml: org.w3c.dom.Element) {
            this.setQuaternion(this.parseRationalVector(xml, QuaternionProjection.LEFT_QUATERNION_ATTRIBUTENAME), QuaternionProjection.LEFT);
            this.setQuaternion(this.parseRationalVector(xml, QuaternionProjection.RIGHT_QUATERNION_ATTRIBUTENAME), QuaternionProjection.RIGHT);
        }

        /*private*/ parseRationalVector(xml: org.w3c.dom.Element, attrName: string): com.vzome.core.algebra.AlgebraicVector {
            const nums: string = xml.getAttribute(attrName);
            if (nums == null || /* isEmpty */(nums.length === 0))return null;
            const loc: com.vzome.core.algebra.AlgebraicVector = this.field.parseVector(nums);
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


}

