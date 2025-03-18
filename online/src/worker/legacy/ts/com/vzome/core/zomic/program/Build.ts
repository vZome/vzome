/* Generated from Java with JSweet 3.2.0-SNAPSHOT - http://www.jsweet.org */
namespace com.vzome.core.zomic.program {
    /**
     * Description here.
     * 
     * @author Scott Vorthmann 2003
     * @param {boolean} build
     * @param {boolean} destroy
     * @class
     * @extends com.vzome.core.zomic.program.ZomicStatement
     */
    export class Build extends com.vzome.core.zomic.program.ZomicStatement {
        /*private*/ m_build: boolean;

        /*private*/ m_destroy: boolean;

        public constructor(build: boolean, destroy: boolean) {
            super();
            if (this.m_build === undefined) { this.m_build = false; }
            if (this.m_destroy === undefined) { this.m_destroy = false; }
            this.m_build = build;
            this.m_destroy = destroy;
        }

        /**
         * 
         * @param {*} visitor
         */
        public accept(visitor: com.vzome.core.zomic.program.Visitor) {
            visitor.visitBuild(this.m_build, this.m_destroy);
        }

        public setBuild(value: boolean) {
            this.m_build = value;
        }

        public setDestroy(value: boolean) {
            this.m_destroy = value;
        }

        public justMoving(): boolean {
            return this.m_build === this.m_destroy;
        }
    }
    Build["__class"] = "com.vzome.core.zomic.program.Build";

}

