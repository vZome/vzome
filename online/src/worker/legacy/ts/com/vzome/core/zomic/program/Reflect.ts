/* Generated from Java with JSweet 3.2.0-SNAPSHOT - http://www.jsweet.org */
namespace com.vzome.core.zomic.program {
    export class Reflect extends com.vzome.core.zomic.program.Permute {
        /**
         * 
         * @param {*} visitor
         */
        public accept(visitor: com.vzome.core.zomic.program.Visitor) {
            visitor.visitReflect(this.getAxis());
        }

        public constructor() {
            super(null);
        }

        /**
         * 
         * @param {com.vzome.core.math.symmetry.Axis} axis
         */
        public setAxis(axis: com.vzome.core.math.symmetry.Axis) {
            super.setAxis(axis);
        }
    }
    Reflect["__class"] = "com.vzome.core.zomic.program.Reflect";

}

