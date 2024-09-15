/* Generated from Java with JSweet 3.2.0-SNAPSHOT - http://www.jsweet.org */
namespace com.vzome.core.zomic.program {
    export class Symmetry extends com.vzome.core.zomic.program.Nested {
        /*private*/ permute: com.vzome.core.zomic.program.Permute;

        /**
         * 
         * @param {*} visitor
         */
        public accept(visitor: com.vzome.core.zomic.program.Visitor) {
            visitor.visitSymmetry(this, this.permute);
        }

        public setPermute(permute: com.vzome.core.zomic.program.Permute) {
            this.permute = permute;
        }

        public getPermute(): com.vzome.core.zomic.program.Permute {
            return this.permute;
        }

        constructor() {
            super();
            if (this.permute === undefined) { this.permute = null; }
        }
    }
    Symmetry["__class"] = "com.vzome.core.zomic.program.Symmetry";

}

