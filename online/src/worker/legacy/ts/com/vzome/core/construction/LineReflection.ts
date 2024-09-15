/* Generated from Java with JSweet 3.2.0-SNAPSHOT - http://www.jsweet.org */
namespace com.vzome.core.construction {
    export class LineReflection extends com.vzome.core.construction.Transformation {
        /*private*/ two: com.vzome.core.algebra.AlgebraicNumber;

        /*private*/ mMirrorLine: com.vzome.core.construction.Line;

        /*private*/ mStart: com.vzome.core.algebra.AlgebraicVector;

        /*private*/ mEnd: com.vzome.core.algebra.AlgebraicVector;

        public constructor(axis: com.vzome.core.construction.Segment) {
            super(axis.field);
            if (this.two === undefined) { this.two = null; }
            if (this.mMirrorLine === undefined) { this.mMirrorLine = null; }
            if (this.mStart === undefined) { this.mStart = null; }
            if (this.mEnd === undefined) { this.mEnd = null; }
            this.two = this.field['createRational$long'](2);
            this.mMirrorLine = new com.vzome.core.construction.LineExtensionOfSegment(axis);
            this.mStart = axis.getStart();
            this.mEnd = axis.getEnd();
        }

        /**
         * 
         * @return {boolean}
         */
        mapParamsToState(): boolean {
            return true;
        }

        public transform$com_vzome_core_algebra_AlgebraicVector(arg: com.vzome.core.algebra.AlgebraicVector): com.vzome.core.algebra.AlgebraicVector {
            const norm1: com.vzome.core.algebra.AlgebraicVector = com.vzome.core.algebra.AlgebraicVectors.getNormal$com_vzome_core_algebra_AlgebraicVector$com_vzome_core_algebra_AlgebraicVector$com_vzome_core_algebra_AlgebraicVector(this.mStart, this.mEnd, arg);
            if (norm1.isOrigin()){
                return arg;
            }
            const norm2: com.vzome.core.algebra.AlgebraicVector = com.vzome.core.algebra.AlgebraicVectors.getNormal$com_vzome_core_algebra_AlgebraicVector$com_vzome_core_algebra_AlgebraicVector$com_vzome_core_algebra_AlgebraicVector(this.mStart, this.mEnd, this.mEnd.plus(norm1));
            const line2: com.vzome.core.construction.Line = new com.vzome.core.construction.LineFromPointAndVector(arg, norm2);
            const point: com.vzome.core.construction.Point = new com.vzome.core.construction.LineLineIntersectionPoint(this.mMirrorLine, line2);
            const intersection: com.vzome.core.algebra.AlgebraicVector = point.getLocation();
            const translation: com.vzome.core.algebra.AlgebraicVector = intersection.minus(arg).scale(this.two);
            return arg.plus(translation);
        }

        /**
         * 
         * @param {com.vzome.core.algebra.AlgebraicVector} arg
         * @return {com.vzome.core.algebra.AlgebraicVector}
         */
        public transform(arg?: any): any {
            if (((arg != null && arg instanceof <any>com.vzome.core.algebra.AlgebraicVector) || arg === null)) {
                return <any>this.transform$com_vzome_core_algebra_AlgebraicVector(arg);
            } else if (((arg != null && arg instanceof <any>com.vzome.core.construction.Construction) || arg === null)) {
                return <any>this.transform$com_vzome_core_construction_Construction(arg);
            } else throw new Error('invalid overload');
        }
    }
    LineReflection["__class"] = "com.vzome.core.construction.LineReflection";

}

