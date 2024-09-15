/* Generated from Java with JSweet 3.2.0-SNAPSHOT - http://www.jsweet.org */
namespace com.vzome.core.construction {
    /**
     * @param step
     * @param start
     * @param end
     * @param {com.vzome.core.construction.Line} l1
     * @param {com.vzome.core.construction.Line} l2
     * @param {com.vzome.core.construction.Point} p
     * @class
     * @extends com.vzome.core.construction.Line
     * @author Scott Vorthmann
     */
    export class PerpendicularLine extends com.vzome.core.construction.Line {
        /*private*/ mLine1: com.vzome.core.construction.Line;

        /*private*/ mLine2: com.vzome.core.construction.Line;

        /*private*/ mPoint: com.vzome.core.construction.Point;

        public constructor(l1: com.vzome.core.construction.Line, l2: com.vzome.core.construction.Line, p: com.vzome.core.construction.Point) {
            super(l1.field);
            if (this.mLine1 === undefined) { this.mLine1 = null; }
            if (this.mLine2 === undefined) { this.mLine2 = null; }
            if (this.mPoint === undefined) { this.mPoint = null; }
            this.mLine1 = l1;
            this.mLine2 = l2;
            this.mPoint = p;
            this.mapParamsToState();
        }

        /**
         * returns true if something changed.
         * @return
         * @return {boolean}
         */
        mapParamsToState(): boolean {
            if (this.mLine1.isImpossible() || this.mLine2.isImpossible() || this.mPoint.isImpossible()){
                return this.setStateVariables(null, null, true);
            }
            const normal: com.vzome.core.algebra.AlgebraicVector = com.vzome.core.algebra.AlgebraicVectors.getNormal$com_vzome_core_algebra_AlgebraicVector$com_vzome_core_algebra_AlgebraicVector(this.mLine1.getDirection(), this.mLine2.getDirection());
            return this.setStateVariables(this.mPoint.getLocation(), normal, normal.isOrigin());
        }
    }
    PerpendicularLine["__class"] = "com.vzome.core.construction.PerpendicularLine";

}

