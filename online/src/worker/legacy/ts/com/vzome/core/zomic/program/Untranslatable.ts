/* Generated from Java with JSweet 3.2.0-SNAPSHOT - http://www.jsweet.org */
namespace com.vzome.core.zomic.program {
    /**
     * @author vorth
     * @param {string} msg
     * @class
     * @extends com.vzome.core.zomic.program.ZomicStatement
     */
    export class Untranslatable extends com.vzome.core.zomic.program.ZomicStatement {
        message: string;

        public constructor(msg: string) {
            super();
            if (this.message === undefined) { this.message = null; }
            this.message = msg;
        }

        public setMessage(msg: string) {
            this.message = msg;
        }

        /**
         * 
         * @param {*} visitor
         */
        public accept(visitor: com.vzome.core.zomic.program.Visitor) {
            visitor.visitUntranslatable(this.message == null ? "" : this.message);
        }
    }
    Untranslatable["__class"] = "com.vzome.core.zomic.program.Untranslatable";

}

