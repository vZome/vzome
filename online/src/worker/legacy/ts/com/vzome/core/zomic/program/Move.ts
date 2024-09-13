/* Generated from Java with JSweet 3.2.0-SNAPSHOT - http://www.jsweet.org */
namespace com.vzome.core.zomic.program {
    export class Move extends com.vzome.core.zomic.program.ZomicStatement {
        axis: com.vzome.core.math.symmetry.Axis;

        length: com.vzome.core.algebra.AlgebraicNumber;

        public constructor(axis: com.vzome.core.math.symmetry.Axis, len: com.vzome.core.algebra.AlgebraicNumber) {
            super();
            if (this.axis === undefined) { this.axis = null; }
            if (this.length === undefined) { this.length = null; }
            this.axis = axis;
            this.length = len;
        }

        /**
         * 
         * @param {*} visitor
         */
        public accept(visitor: com.vzome.core.zomic.program.Visitor) {
            visitor.visitMove(this.axis, this.length);
        }

        /**
         * @return
         * @return {*}
         */
        public getLength(): com.vzome.core.algebra.AlgebraicNumber {
            return this.length;
        }

        public getAxis(): com.vzome.core.math.symmetry.Axis {
            return this.axis;
        }

        /**
         * Needed only for Zomic XMLS2AST.  TODO: remove this by
         * rearranging the XML?
         * @param axis2
         * @param {*} len
         * @param {com.vzome.core.math.symmetry.Axis} axis
         */
        public reset(axis: com.vzome.core.math.symmetry.Axis, len: com.vzome.core.algebra.AlgebraicNumber) {
            this.axis = axis;
            this.length = len;
        }
    }
    Move["__class"] = "com.vzome.core.zomic.program.Move";

}

