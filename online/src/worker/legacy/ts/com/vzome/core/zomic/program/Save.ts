/* Generated from Java with JSweet 3.2.0-SNAPSHOT - http://www.jsweet.org */
namespace com.vzome.core.zomic.program {
    /**
     * Description here.
     * 
     * @author Scott Vorthmann 2003
     * @param {number} state
     * @class
     * @extends com.vzome.core.zomic.program.Nested
     */
    export class Save extends com.vzome.core.zomic.program.Nested {
        /*private*/ m_state: number;

        public constructor(state: number) {
            super();
            if (this.m_state === undefined) { this.m_state = 0; }
            this.m_state = state;
        }

        /**
         * 
         * @param {*} visitor
         */
        public accept(visitor: com.vzome.core.zomic.program.Visitor) {
            visitor.visitSave(this, this.m_state);
        }

        public setState(state: number) {
            this.m_state = state;
        }
    }
    Save["__class"] = "com.vzome.core.zomic.program.Save";

}

