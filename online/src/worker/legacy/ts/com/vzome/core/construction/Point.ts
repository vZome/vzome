/* Generated from Java with JSweet 3.2.0-SNAPSHOT - http://www.jsweet.org */
namespace com.vzome.core.construction {
    /**
     * @author Scott Vorthmann
     * @extends com.vzome.core.construction.Construction
     * @class
     */
    export abstract class Point extends com.vzome.core.construction.Construction {
        /*private*/ mLocation: com.vzome.core.algebra.AlgebraicVector;

        constructor(field: com.vzome.core.algebra.AlgebraicField) {
            super(field);
            if (this.mLocation === undefined) { this.mLocation = null; }
        }

        public getSignature(): string {
            return this.mLocation.projectTo3d(true).toString();
        }

        /**
         * 
         * @return {boolean}
         */
        public is3d(): boolean {
            return this.mLocation.dimension() === 3;
        }

        setStateVariable(loc: com.vzome.core.algebra.AlgebraicVector, impossible: boolean): boolean {
            if (impossible){
                if (this.isImpossible())return false;
                this.setImpossible(true);
                return true;
            }
            if (loc.equals(this.mLocation) && !this.isImpossible())return false;
            this.mLocation = loc;
            this.setImpossible(false);
            return true;
        }

        public getLocation(): com.vzome.core.algebra.AlgebraicVector {
            return this.mLocation;
        }

        /**
         * 
         * @param {*} doc
         * @return {*}
         */
        public getXml(doc: org.w3c.dom.Document): org.w3c.dom.Element {
            const result: org.w3c.dom.Element = doc.createElement("point");
            result.setAttribute("at", this.getLocation().getVectorExpression$int(com.vzome.core.algebra.AlgebraicField.ZOMIC_FORMAT));
            return result;
        }

        /**
         * 
         * @return {string}
         */
        public toString(): string {
            return "point at " + this.mLocation;
        }
    }
    Point["__class"] = "com.vzome.core.construction.Point";

}

