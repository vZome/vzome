/* Generated from Java with JSweet 3.2.0-SNAPSHOT - http://www.jsweet.org */
namespace com.vzome.core.zomic.program {
    export class Nested extends com.vzome.core.zomic.program.ZomicStatement {
        m_body: com.vzome.core.zomic.program.ZomicStatement;

        /**
         * 
         * @param {*} visitor
         */
        public accept(visitor: com.vzome.core.zomic.program.Visitor) {
            visitor.visitNested(this);
        }

        public setBody(body: com.vzome.core.zomic.program.ZomicStatement) {
            this.m_body = body;
        }

        public getBody(): com.vzome.core.zomic.program.ZomicStatement {
            return this.m_body;
        }

        constructor() {
            super();
            if (this.m_body === undefined) { this.m_body = null; }
        }
    }
    Nested["__class"] = "com.vzome.core.zomic.program.Nested";

}

