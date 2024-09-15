/* Generated from Java with JSweet 3.2.0-SNAPSHOT - http://www.jsweet.org */
namespace com.vzome.core.zomic.program {
    export class Scale extends com.vzome.core.zomic.program.ZomicStatement {
        /*private*/ m_scale: com.vzome.core.algebra.AlgebraicNumber;

        public constructor(size: com.vzome.core.algebra.AlgebraicNumber) {
            super();
            if (this.m_scale === undefined) { this.m_scale = null; }
            this.m_scale = size;
        }

        /**
         * 
         * @param {*} visitor
         */
        public accept(visitor: com.vzome.core.zomic.program.Visitor) {
            visitor.visitScale(this.m_scale);
        }
    }
    Scale["__class"] = "com.vzome.core.zomic.program.Scale";

}

