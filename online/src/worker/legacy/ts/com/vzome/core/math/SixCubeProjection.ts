/* Generated from Java with JSweet 3.2.0-SNAPSHOT - http://www.jsweet.org */
namespace com.vzome.core.math {
    /**
     * @author Scott Vorthmann
     * @param {*} field
     * @class
     */
    export class SixCubeProjection implements com.vzome.core.math.Projection {
        /*private*/ field: com.vzome.core.algebra.AlgebraicField;

        /*private*/ basis: com.vzome.core.algebra.AlgebraicVector[];

        public constructor(field: com.vzome.core.algebra.AlgebraicField) {
            if (this.field === undefined) { this.field = null; }
            if (this.basis === undefined) { this.basis = null; }
            this.field = field;
            const zero: com.vzome.core.algebra.AlgebraicNumber = field.zero();
            const one: com.vzome.core.algebra.AlgebraicNumber = field.one();
            const nOne: com.vzome.core.algebra.AlgebraicNumber = one.negate();
            const phi: com.vzome.core.algebra.AlgebraicNumber = field['createPower$int'](1);
            this.basis = [null, null, null, null, null, null];
            this.basis[0] = new com.vzome.core.algebra.AlgebraicVector(phi, one, zero);
            this.basis[1] = new com.vzome.core.algebra.AlgebraicVector(phi, nOne, zero);
            this.basis[2] = new com.vzome.core.algebra.AlgebraicVector(zero, phi, one);
            this.basis[3] = new com.vzome.core.algebra.AlgebraicVector(zero, phi, nOne);
            this.basis[4] = new com.vzome.core.algebra.AlgebraicVector(one, zero, phi);
            this.basis[5] = new com.vzome.core.algebra.AlgebraicVector(nOne, zero, phi);
        }

        /**
         * 
         * @param {com.vzome.core.algebra.AlgebraicVector} source
         * @param {boolean} wFirst
         * @return {com.vzome.core.algebra.AlgebraicVector}
         */
        public projectImage(source: com.vzome.core.algebra.AlgebraicVector, wFirst: boolean): com.vzome.core.algebra.AlgebraicVector {
            let result: com.vzome.core.algebra.AlgebraicVector = this.field.origin(this.basis[0].dimension());
            let pos: number = wFirst ? 0 : this.basis.length - 1;
            for(let index = 0; index < this.basis.length; index++) {
                let unitVector = this.basis[index];
                {
                    const scalar: com.vzome.core.algebra.AlgebraicNumber = source.getComponent(pos);
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
            return "SixCube";
        }
    }
    SixCubeProjection["__class"] = "com.vzome.core.math.SixCubeProjection";
    SixCubeProjection["__interfaces"] = ["com.vzome.core.math.Projection"];


}

