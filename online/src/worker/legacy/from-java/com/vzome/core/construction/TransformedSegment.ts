import { AlgebraicVector } from "../algebra/AlgebraicVector.js";
import { Segment } from "./Segment.js";
import { Transformation } from "./Transformation.js";

/**
 * @author Scott Vorthmann
 * @param {Transformation} transform
 * @param {Segment} prototype
 * @class
 * @extends Segment
 */
export class TransformedSegment extends Segment {
    /*private*/ mTransform: Transformation;

    /*private*/ mPrototype: Segment;

    public constructor(transform: Transformation, prototype: Segment) {
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
        if (this.mTransform.isImpossible() || this.mPrototype.isImpossible())return this.setStateVariables(null, null, true);
        const loc: AlgebraicVector = this.mTransform.transform$com_vzome_core_algebra_AlgebraicVector(this.mPrototype.getStart().projectTo3d(true));
        const end: AlgebraicVector = this.mTransform.transform$com_vzome_core_algebra_AlgebraicVector(this.mPrototype.getEnd().projectTo3d(true));
        if (end == null || loc == null)return this.setStateVariables(null, null, true);
        return this.setStateVariables(loc, end.minus(loc), false);
    }
}
TransformedSegment["__class"] = "com.vzome.core.construction.TransformedSegment";
