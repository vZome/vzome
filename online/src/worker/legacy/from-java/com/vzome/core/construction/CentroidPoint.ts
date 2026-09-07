import { AlgebraicVector } from "../algebra/AlgebraicVector.js";
import { Point } from "./Point.js";

/**
 * @author Scott Vorthmann
 * @param {Point[]} points
 * @class
 * @extends Point
 */
export class CentroidPoint extends Point {
    /*private*/ mPoints: Point[];

    public constructor(points: Point[]) {
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
        let centroid: AlgebraicVector = this.mPoints[0].getLocation();
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
