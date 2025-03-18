/* Generated from Java with JSweet 3.2.0-SNAPSHOT - http://www.jsweet.org */
namespace com.vzome.core.construction {
    /**
     * @author Scott Vorthmann
     * @param {com.vzome.core.construction.Point[]} points
     * @class
     * @extends com.vzome.core.construction.Point
     */
    export class CentroidPoint extends com.vzome.core.construction.Point {
        /*private*/ mPoints: com.vzome.core.construction.Point[];

        public constructor(points: com.vzome.core.construction.Point[]) {
            super(points[0].field);
            if (this.mPoints === undefined) { this.mPoints = null; }
            this.mPoints = points;
            this.mapParamsToState();
        }

        /**
         * 
         * @return {boolean}
         */
        mapParamsToState(): boolean {
            let centroid: com.vzome.core.algebra.AlgebraicVector = this.mPoints[0].getLocation();
            let num: number = 1;
            for(let i: number = 1; i < this.mPoints.length; i++) {{
                centroid = centroid.plus(this.mPoints[i].getLocation());
                num++;
            };}
            centroid = centroid.scale(this.field['createRational$long$long'](1, num));
            return this.setStateVariable(centroid, false);
        }
    }
    CentroidPoint["__class"] = "com.vzome.core.construction.CentroidPoint";

}

