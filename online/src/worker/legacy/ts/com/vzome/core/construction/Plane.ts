/* Generated from Java with JSweet 3.2.0-SNAPSHOT - http://www.jsweet.org */
namespace com.vzome.core.construction {
    /**
     * @author Scott Vorthmann
     * @extends com.vzome.core.construction.Construction
     * @class
     */
    export abstract class Plane extends com.vzome.core.construction.Construction {
        /*private*/ mBase: com.vzome.core.algebra.AlgebraicVector;

        /*private*/ mNormal: com.vzome.core.algebra.AlgebraicVector;

        constructor(field: com.vzome.core.algebra.AlgebraicField) {
            super(field);
            if (this.mBase === undefined) { this.mBase = null; }
            if (this.mNormal === undefined) { this.mNormal = null; }
        }

        /**
         * 
         * @return {boolean}
         */
        public is3d(): boolean {
            return true;
        }

        setStateVariables(base: com.vzome.core.algebra.AlgebraicVector, normal: com.vzome.core.algebra.AlgebraicVector, impossible: boolean): boolean {
            if (impossible){
                if (this.isImpossible())return false;
                this.setImpossible(true);
                return true;
            }
            if (normal.equals(this.mNormal) && !this.isImpossible() && base.equals(this.mBase))return false;
            normal = normal.projectTo3d(true);
            this.mNormal = normal;
            this.mBase = base.projectTo3d(true);
            this.setImpossible(false);
            return true;
        }

        public getBase(): com.vzome.core.algebra.AlgebraicVector {
            return this.mBase;
        }

        public getNormal(): com.vzome.core.algebra.AlgebraicVector {
            return this.mNormal;
        }

        /**
         * 
         * @param {*} doc
         * @return {*}
         */
        public getXml(doc: org.w3c.dom.Document): org.w3c.dom.Element {
            const result: org.w3c.dom.Element = doc.createElement("plane");
            return result;
        }

        public getHomogeneous(): com.vzome.core.algebra.Trivector3dHomogeneous {
            return null;
        }
    }
    Plane["__class"] = "com.vzome.core.construction.Plane";

}

