import { java, javaemul } from "../../../../../candies/j4ts-2.1.0-SNAPSHOT/bundle.js";
import { AlgebraicField } from "../algebra/AlgebraicField.js";
import { AlgebraicVector } from "../algebra/AlgebraicVector.js";
import { Quaternion } from "../algebra/Quaternion.js";
import { Segment } from "./Segment.js";

/**
 * @author Scott Vorthmann
 * @param {Quaternion} leftQuaternion
 * @param {Quaternion} rightQuaternion
 * @param {Segment} prototype
 * @class
 * @extends Segment
 */
export class SegmentRotated4D extends Segment {
    /*private*/ mLeftQuaternion: Quaternion;

    /*private*/ mRightQuaternion: Quaternion;

    /*private*/ mPrototype: Segment;

    static logger: java.util.logging.Logger; public static logger_$LI$(): java.util.logging.Logger { if (SegmentRotated4D.logger == null) { SegmentRotated4D.logger = java.util.logging.Logger.getLogger("com.vzome.core.4d"); }  return SegmentRotated4D.logger; }

    public constructor(leftQuaternion: Quaternion, rightQuaternion: Quaternion, prototype: Segment) {
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
        if (this.mPrototype.isImpossible())return this.setStateVariables(null, null, true);
        let loc: AlgebraicVector = this.mPrototype.getStart();
        loc = loc.inflateTo4d$boolean(true);
        loc = this.mRightQuaternion.leftMultiply(loc);
        loc = this.mLeftQuaternion.rightMultiply(loc);
        let end: AlgebraicVector = this.mPrototype.getEnd();
        end = end.inflateTo4d$boolean(true);
        end = this.mRightQuaternion.leftMultiply(end);
        end = this.mLeftQuaternion.rightMultiply(end);
        if (SegmentRotated4D.logger_$LI$().isLoggable(java.util.logging.Level.FINER)){
            SegmentRotated4D.logger_$LI$().finer("------------------- SegmentRotated4D");
            SegmentRotated4D.logger_$LI$().finer("left:    " + this.mLeftQuaternion.toString());
            SegmentRotated4D.logger_$LI$().finer("right:   " + this.mRightQuaternion.toString());
            SegmentRotated4D.logger_$LI$().finer("start: " + this.mPrototype.getStart().getVectorExpression$int(AlgebraicField.EXPRESSION_FORMAT));
            SegmentRotated4D.logger_$LI$().finer("end:   " + this.mPrototype.getEnd().getVectorExpression$int(AlgebraicField.EXPRESSION_FORMAT));
            SegmentRotated4D.logger_$LI$().finer("new start: " + loc.getVectorExpression$int(AlgebraicField.EXPRESSION_FORMAT));
            SegmentRotated4D.logger_$LI$().finer("new end:   " + end.getVectorExpression$int(AlgebraicField.EXPRESSION_FORMAT));
        }
        return this.setStateVariables(loc, end.minus(loc), false);
    }
}
SegmentRotated4D["__class"] = "com.vzome.core.construction.SegmentRotated4D";
