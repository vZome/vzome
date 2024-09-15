/* Generated from Java with JSweet 3.2.0-SNAPSHOT - http://www.jsweet.org */
namespace com.vzome.core.construction {
    /**
     * @author Scott Vorthmann
     * @param {com.vzome.core.construction.Polygon} polygon
     * @class
     * @extends com.vzome.core.construction.Plane
     */
    export class PlaneExtensionOfPolygon extends com.vzome.core.construction.Plane {
        /*private*/ mPolygon: com.vzome.core.construction.Polygon;

        public constructor(polygon: com.vzome.core.construction.Polygon) {
            super(polygon.field);
            if (this.mPolygon === undefined) { this.mPolygon = null; }
            this.mPolygon = polygon;
            this.mapParamsToState();
        }

        /**
         * 
         * @return {boolean}
         */
        mapParamsToState(): boolean {
            if (this.mPolygon.isImpossible()){
                return this.setStateVariables(null, null, true);
            }
            return this.setStateVariables(this.mPolygon.getVertex(0), this.mPolygon.getNormal(), false);
        }

        /**
         * 
         * @return {com.vzome.core.algebra.Trivector3dHomogeneous}
         */
        public getHomogeneous(): com.vzome.core.algebra.Trivector3dHomogeneous {
            const v1: com.vzome.core.algebra.Vector3dHomogeneous = new com.vzome.core.algebra.Vector3dHomogeneous(this.mPolygon.getVertex(0), this.getField());
            const v2: com.vzome.core.algebra.Vector3dHomogeneous = new com.vzome.core.algebra.Vector3dHomogeneous(this.mPolygon.getVertex(1), this.getField());
            const v3: com.vzome.core.algebra.Vector3dHomogeneous = new com.vzome.core.algebra.Vector3dHomogeneous(this.mPolygon.getVertex(2), this.getField());
            return v1.outer(v2).outer(v3);
        }
    }
    PlaneExtensionOfPolygon["__class"] = "com.vzome.core.construction.PlaneExtensionOfPolygon";

}

