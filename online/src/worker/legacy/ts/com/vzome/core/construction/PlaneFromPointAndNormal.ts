/* Generated from Java with JSweet 3.2.0-SNAPSHOT - http://www.jsweet.org */
namespace com.vzome.core.construction {
    /**
     * @author Scott Vorthmann
     * @param {com.vzome.core.algebra.AlgebraicVector} point
     * @param {com.vzome.core.algebra.AlgebraicVector} normal
     * @class
     * @extends com.vzome.core.construction.Plane
     */
    export class PlaneFromPointAndNormal extends com.vzome.core.construction.Plane {
        /*private*/ normal: com.vzome.core.algebra.AlgebraicVector;

        /*private*/ point: com.vzome.core.algebra.AlgebraicVector;

        public constructor(point: com.vzome.core.algebra.AlgebraicVector, normal: com.vzome.core.algebra.AlgebraicVector) {
            super(point.getField());
            if (this.normal === undefined) { this.normal = null; }
            if (this.point === undefined) { this.point = null; }
            this.point = point;
            this.normal = normal;
            this.mapParamsToState();
        }

        /**
         * 
         * @return {boolean}
         */
        mapParamsToState(): boolean {
            if (this.normal.isOrigin())return this.setStateVariables(null, null, true);
            return this.setStateVariables(this.point, this.normal, false);
        }
    }
    PlaneFromPointAndNormal["__class"] = "com.vzome.core.construction.PlaneFromPointAndNormal";

}

