/* Generated from Java with JSweet 3.2.0-SNAPSHOT - http://www.jsweet.org */
namespace com.vzome.core.zomic.program {
    export class Repeat extends com.vzome.core.zomic.program.Nested {
        /*private*/ repetitions: number;

        public constructor(repetitions: number) {
            super();
            if (this.repetitions === undefined) { this.repetitions = 0; }
            this.repetitions = repetitions;
        }

        /**
         * 
         * @param {*} visitor
         */
        public accept(visitor: com.vzome.core.zomic.program.Visitor) {
            visitor.visitRepeat(this, this.repetitions);
        }
    }
    Repeat["__class"] = "com.vzome.core.zomic.program.Repeat";

}

