/* Generated from Java with JSweet 3.2.0-SNAPSHOT - http://www.jsweet.org */
namespace com.vzome.core.construction {
    /**
     * @author Scott Vorthmann
     * @param {com.vzome.core.algebra.Quaternion} leftQuaternion
     * @param {com.vzome.core.algebra.Quaternion} rightQuaternion
     * @param {com.vzome.core.construction.Segment} prototype
     * @class
     * @extends com.vzome.core.construction.Segment
     */
    export class SegmentRotated4D extends com.vzome.core.construction.Segment {
        /*private*/ mLeftQuaternion: com.vzome.core.algebra.Quaternion;

        /*private*/ mRightQuaternion: com.vzome.core.algebra.Quaternion;

        /*private*/ mPrototype: com.vzome.core.construction.Segment;

        static logger: java.util.logging.Logger; public static logger_$LI$(): java.util.logging.Logger { if (SegmentRotated4D.logger == null) { SegmentRotated4D.logger = java.util.logging.Logger.getLogger("com.vzome.core.4d"); }  return SegmentRotated4D.logger; }

        public constructor(leftQuaternion: com.vzome.core.algebra.Quaternion, rightQuaternion: com.vzome.core.algebra.Quaternion, prototype: com.vzome.core.construction.Segment) {
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
            let loc: com.vzome.core.algebra.AlgebraicVector = this.mPrototype.getStart();
            loc = loc.inflateTo4d$boolean(true);
            loc = this.mRightQuaternion.leftMultiply(loc);
            loc = this.mLeftQuaternion.rightMultiply(loc);
            let end: com.vzome.core.algebra.AlgebraicVector = this.mPrototype.getEnd();
            end = end.inflateTo4d$boolean(true);
            end = this.mRightQuaternion.leftMultiply(end);
            end = this.mLeftQuaternion.rightMultiply(end);
            if (SegmentRotated4D.logger_$LI$().isLoggable(java.util.logging.Level.FINER)){
                SegmentRotated4D.logger_$LI$().finer("------------------- SegmentRotated4D");
                SegmentRotated4D.logger_$LI$().finer("left:    " + this.mLeftQuaternion.toString());
                SegmentRotated4D.logger_$LI$().finer("right:   " + this.mRightQuaternion.toString());
                SegmentRotated4D.logger_$LI$().finer("start: " + this.mPrototype.getStart().getVectorExpression$int(com.vzome.core.algebra.AlgebraicField.EXPRESSION_FORMAT));
                SegmentRotated4D.logger_$LI$().finer("end:   " + this.mPrototype.getEnd().getVectorExpression$int(com.vzome.core.algebra.AlgebraicField.EXPRESSION_FORMAT));
                SegmentRotated4D.logger_$LI$().finer("new start: " + loc.getVectorExpression$int(com.vzome.core.algebra.AlgebraicField.EXPRESSION_FORMAT));
                SegmentRotated4D.logger_$LI$().finer("new end:   " + end.getVectorExpression$int(com.vzome.core.algebra.AlgebraicField.EXPRESSION_FORMAT));
            }
            return this.setStateVariables(loc, end.minus(loc), false);
        }
    }
    SegmentRotated4D["__class"] = "com.vzome.core.construction.SegmentRotated4D";

}

