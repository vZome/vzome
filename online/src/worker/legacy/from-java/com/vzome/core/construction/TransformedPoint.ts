import { AlgebraicVector } from "../algebra/AlgebraicVector.js";
import { Point } from "./Point.js";
import { Transformation } from "./Transformation.js";

/**
 * @author Scott Vorthmann
 * @param {Transformation} transform
 * @param {Point} prototype
 * @class
 * @extends Point
 */
export class TransformedPoint extends Point {
    /*private*/ mTransform: Transformation;

    /*private*/ mPrototype: Point;

    public constructor(transform: Transformation, prototype: Point) {
        super(prototype.field);
        if (this.mTransform === undefined) { this.mTransform = null; }
        if (this.mPrototype === undefined) { this.mPrototype = null; }
        this.mTransform = transform;
        this.mPrototype = prototype;
        this.mapParamsToState();
    }

    /**
     * 
     * @return {boolean}
     */
    mapParamsToState(): boolean {
        if (this.mTransform.isImpossible() || this.mPrototype.isImpossible())return this.setStateVariable(null, true);
        const loc: AlgebraicVector = this.mTransform.transform$com_vzome_core_algebra_AlgebraicVector(this.mPrototype.getLocation());
        return this.setStateVariable(loc, loc == null);
    }
}
TransformedPoint["__class"] = "com.vzome.core.construction.TransformedPoint";
