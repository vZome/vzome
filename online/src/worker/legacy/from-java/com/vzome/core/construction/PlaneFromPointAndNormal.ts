import { AlgebraicVector } from "../algebra/AlgebraicVector.js";
import { Plane } from "./Plane.js";

/**
 * @author Scott Vorthmann
 * @param {AlgebraicVector} point
 * @param {AlgebraicVector} normal
 * @class
 * @extends Plane
 */
export class PlaneFromPointAndNormal extends Plane {
    /*private*/ normal: AlgebraicVector;

    /*private*/ point: AlgebraicVector;

    public constructor(point: AlgebraicVector, normal: AlgebraicVector) {
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
