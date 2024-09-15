/* Generated from Java with JSweet 3.2.0-SNAPSHOT - http://www.jsweet.org */
namespace com.vzome.core.zomic.program {
    export class Rotate extends com.vzome.core.zomic.program.Permute {
        /*private*/ steps: number;

        /**
         * 
         * @param {*} visitor
         */
        public accept(visitor: com.vzome.core.zomic.program.Visitor) {
            visitor.visitRotate(this.getAxis(), this.steps);
        }

        public constructor(axis: com.vzome.core.math.symmetry.Axis, steps: number) {
            super(axis);
            if (this.steps === undefined) { this.steps = 0; }
            this.steps = steps;
        }

        public setSteps(steps: number) {
            this.steps = steps;
        }
    }
    Rotate["__class"] = "com.vzome.core.zomic.program.Rotate";

}

