import { AlgebraicVector } from "../algebra/AlgebraicVector.js";
import { Line } from "./Line.js";

/**
 * @author Scott Vorthmann
 * @param {AlgebraicVector} point
 * @param {AlgebraicVector} direction
 * @class
 * @extends Line
 */
export class LineFromPointAndVector extends Line {
    /*private*/ point: AlgebraicVector;

    /*private*/ direction: AlgebraicVector;

    public constructor(point: AlgebraicVector, direction: AlgebraicVector) {
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
