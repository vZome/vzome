import { AlgebraicVector } from "../algebra/AlgebraicVector.js";
import { Point } from "./Point.js";
import { Polygon } from "./Polygon.js";

/**
 * @author Scott Vorthmann
 * @param {Polygon} polygon
 * @param {number} index
 * @class
 * @extends Point
 */
export class PolygonVertex extends Point {
    /*private*/ polygon: Polygon;

    /*private*/ index: number;

    public constructor(polygon: Polygon, index: number) {
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
        const loc: AlgebraicVector = this.polygon.getVertex(this.index);
        return this.setStateVariable(loc, false);
    }
}
PolygonVertex["__class"] = "com.vzome.core.construction.PolygonVertex";
