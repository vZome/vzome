/* Generated from Java with JSweet 3.2.0-SNAPSHOT - http://www.jsweet.org */
namespace com.vzome.core.construction {
    /**
     * @author Scott Vorthmann
     * @param {com.vzome.core.algebra.AlgebraicVector} point
     * @param {com.vzome.core.algebra.AlgebraicVector} direction
     * @class
     * @extends com.vzome.core.construction.Line
     */
    export class LineFromPointAndVector extends com.vzome.core.construction.Line {
        /*private*/ point: com.vzome.core.algebra.AlgebraicVector;

        /*private*/ direction: com.vzome.core.algebra.AlgebraicVector;

        public constructor(point: com.vzome.core.algebra.AlgebraicVector, direction: com.vzome.core.algebra.AlgebraicVector) {
            super(point.getField());
            if (this.point === undefined) { this.point = null; }
            if (this.direction === undefined) { this.direction = null; }
            this.point = point;
            this.direction = direction;
            this.mapParamsToState();
        }

        /**
         * 
         * @return {boolean}
         */
        mapParamsToState(): boolean {
            if (this.direction.isOrigin())return this.setStateVariables(null, null, true);
            return this.setStateVariables(this.point, this.direction, false);
        }
    }
    LineFromPointAndVector["__class"] = "com.vzome.core.construction.LineFromPointAndVector";

}

