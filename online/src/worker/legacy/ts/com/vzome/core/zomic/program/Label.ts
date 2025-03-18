/* Generated from Java with JSweet 3.2.0-SNAPSHOT - http://www.jsweet.org */
namespace com.vzome.core.zomic.program {
    /**
     * @author vorth
     * @param {string} id
     * @class
     * @extends com.vzome.core.zomic.program.ZomicStatement
     */
    export class Label extends com.vzome.core.zomic.program.ZomicStatement {
        mLabel: string;

        public constructor(id: string) {
            super();
            if (this.mLabel === undefined) { this.mLabel = null; }
            this.mLabel = id;
        }

        /**
         * 
         * @param {*} visitor
         */
        public accept(visitor: com.vzome.core.zomic.program.Visitor) {
            visitor.visitLabel(this.mLabel);
        }
    }
    Label["__class"] = "com.vzome.core.zomic.program.Label";

}

