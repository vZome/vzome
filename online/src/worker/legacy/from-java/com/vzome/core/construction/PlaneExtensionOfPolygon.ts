import { Trivector3dHomogeneous } from "../algebra/Trivector3dHomogeneous.js";
import { Vector3dHomogeneous } from "../algebra/Vector3dHomogeneous.js";
import { Plane } from "./Plane.js";
import { Polygon } from "./Polygon.js";

/**
 * @author Scott Vorthmann
 * @param {Polygon} polygon
 * @class
 * @extends Plane
 */
export class PlaneExtensionOfPolygon extends Plane {
    /*private*/ mPolygon: Polygon;

    public constructor(polygon: Polygon) {
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
     * @return {Trivector3dHomogeneous}
     */
    public getHomogeneous(): Trivector3dHomogeneous {
        const v1: Vector3dHomogeneous = new Vector3dHomogeneous(this.mPolygon.getVertex(0), this.getField());
        const v2: Vector3dHomogeneous = new Vector3dHomogeneous(this.mPolygon.getVertex(1), this.getField());
        const v3: Vector3dHomogeneous = new Vector3dHomogeneous(this.mPolygon.getVertex(2), this.getField());
        return v1.outer(v2).outer(v3);
    }
}
PlaneExtensionOfPolygon["__class"] = "com.vzome.core.construction.PlaneExtensionOfPolygon";
