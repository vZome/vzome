/* Generated from Java with JSweet 3.2.0-SNAPSHOT - http://www.jsweet.org */
namespace com.vzome.core.zomic.program {
    export class Walk extends com.vzome.core.zomic.program.ZomicStatement implements java.lang.Iterable<com.vzome.core.zomic.program.ZomicStatement> {
        /*private*/ stmts: java.util.List<com.vzome.core.zomic.program.ZomicStatement>;

        /**
         * 
         * @param {*} visitor
         */
        public accept(visitor: com.vzome.core.zomic.program.Visitor) {
            visitor.visitWalk(this);
        }

        public addStatement(stmt: com.vzome.core.zomic.program.ZomicStatement) {
            this.stmts.add(stmt);
        }

        /**
         * 
         * @return {*}
         */
        public iterator(): java.util.Iterator<com.vzome.core.zomic.program.ZomicStatement> {
            return this.stmts.iterator();
        }

        public size(): number {
            return this.stmts.size();
        }

        constructor() {
            super();
            this.stmts = <any>(new java.util.ArrayList<any>());
        }
    }
    Walk["__class"] = "com.vzome.core.zomic.program.Walk";
    Walk["__interfaces"] = ["java.lang.Iterable"];


}

