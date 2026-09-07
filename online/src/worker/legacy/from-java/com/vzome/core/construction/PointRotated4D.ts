import { AlgebraicField } from "../algebra/AlgebraicField.js";
import { AlgebraicVector } from "../algebra/AlgebraicVector.js";
import { Quaternion } from "../algebra/Quaternion.js";
import { Point } from "./Point.js";

/**
 * @author Scott Vorthmann
 * @param {Quaternion} leftQuaternion
 * @param {Quaternion} rightQuaternion
 * @param {Point} prototype
 * @class
 * @extends Point
 */
export class PointRotated4D extends Point {
    /*private*/ mLeftQuaternion: Quaternion;

    /*private*/ mRightQuaternion: Quaternion;

    /*private*/ mPrototype: Point;

    public constructor(leftQuaternion: Quaternion, rightQuaternion: Quaternion, prototype: Point) {
        super(prototype.field);
        if (this.mLeftQuaternion === undefined) { this.mLeftQuaternion = null; }
        if (this.mRightQuaternion === undefined) { this.mRightQuaternion = null; }
        if (this.mPrototype === undefined) { this.mPrototype = null; }
        this.mLeftQuaternion = leftQuaternion;
        this.mRightQuaternion = rightQuaternion;
        this.mPrototype = prototype;
        this.mapParamsToState();
    }

    /**
     * 
     * @return {boolean}
     */
    mapParamsToState(): boolean {
        if (this.mPrototype.isImpossible())return this.setStateVariable(null, true);
        const field: AlgebraicField = this.mPrototype.getField();
        let loc: AlgebraicVector = field.origin(4);
        const loc3d: AlgebraicVector = this.mPrototype.getLocation();
        loc = loc3d.inflateTo4d$boolean(true);
        loc = this.mRightQuaternion.leftMultiply(loc);
        loc = this.mLeftQuaternion.rightMultiply(loc);
        return this.setStateVariable(loc, false);
    }
}
PointRotated4D["__class"] = "com.vzome.core.construction.PointRotated4D";
