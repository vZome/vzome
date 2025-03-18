/* Generated from Java with JSweet 3.2.0-SNAPSHOT - http://www.jsweet.org */
namespace com.vzome.core.construction {
    /**
     * @author Scott Vorthmann
     * @param {com.vzome.core.construction.Polygon} polygon
     * @param {number} index
     * @class
     * @extends com.vzome.core.construction.Point
     */
    export class PolygonVertex extends com.vzome.core.construction.Point {
        /*private*/ polygon: com.vzome.core.construction.Polygon;

        /*private*/ index: number;

        public constructor(polygon: com.vzome.core.construction.Polygon, index: number) {
            super(polygon.field);
            if (this.polygon === undefined) { this.polygon = null; }
            if (this.index === undefined) { this.index = 0; }
            this.polygon = polygon;
            this.index = index;
            this.mapParamsToState();
        }

        /**
         * 
         * @return {boolean}
         */
        mapParamsToState(): boolean {
            if (this.polygon.isImpossible()){
                return this.setStateVariable(null, true);
            }
            const loc: com.vzome.core.algebra.AlgebraicVector = this.polygon.getVertex(this.index);
            return this.setStateVariable(loc, false);
        }
    }
    PolygonVertex["__class"] = "com.vzome.core.construction.PolygonVertex";

}

