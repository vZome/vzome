/* Generated from Java with JSweet 3.2.0-SNAPSHOT - http://www.jsweet.org */
namespace com.vzome.core.construction {
    /**
     * @author Scott Vorthmann
     * @extends com.vzome.core.construction.Construction
     * @class
     */
    export abstract class Line extends com.vzome.core.construction.Construction {
        /*private*/ mDirection: com.vzome.core.algebra.AlgebraicVector;

        /*private*/ mStart: com.vzome.core.algebra.AlgebraicVector;

        constructor(field: com.vzome.core.algebra.AlgebraicField) {
            super(field);
            if (this.mDirection === undefined) { this.mDirection = null; }
            if (this.mStart === undefined) { this.mStart = null; }
        }

        /**
         * 
         * @return {boolean}
         */
        public is3d(): boolean {
            return true;
        }

        /**
         * 
         * @param {com.vzome.core.algebra.AlgebraicVector} start
         * @param {com.vzome.core.algebra.AlgebraicVector} norm need not be normalized yet
         * @return
         * @param {boolean} impossible
         * @return {boolean}
         */
        setStateVariables(start: com.vzome.core.algebra.AlgebraicVector, norm: com.vzome.core.algebra.AlgebraicVector, impossible: boolean): boolean {
            if (impossible){
                if (this.isImpossible())return false;
                this.setImpossible(true);
                return true;
            }
            if (norm.equals(this.mDirection) && start.equals(this.mStart) && !this.isImpossible())return false;
            this.mDirection = norm;
            this.mStart = start;
            this.setImpossible(false);
            return true;
        }

        public getStart(): com.vzome.core.algebra.AlgebraicVector {
            return this.mStart;
        }

        /**
         * @return {com.vzome.core.algebra.AlgebraicVector} a "unit" vector... always normalized
         */
        public getDirection(): com.vzome.core.algebra.AlgebraicVector {
            return this.mDirection;
        }

        /**
         * 
         * @param {*} doc
         * @return {*}
         */
        public getXml(doc: org.w3c.dom.Document): org.w3c.dom.Element {
            const result: org.w3c.dom.Element = doc.createElement("line");
            return result;
        }

        public getHomogeneous(): com.vzome.core.algebra.Bivector3dHomogeneous {
            const v1: com.vzome.core.algebra.Vector3dHomogeneous = new com.vzome.core.algebra.Vector3dHomogeneous(this.mStart, this.getField());
            const v2: com.vzome.core.algebra.Vector3dHomogeneous = new com.vzome.core.algebra.Vector3dHomogeneous(this.mStart.plus(this.mDirection), this.getField());
            return v1.outer(v2);
        }
    }
    Line["__class"] = "com.vzome.core.construction.Line";

}

