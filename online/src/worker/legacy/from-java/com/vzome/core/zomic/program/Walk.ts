import { java, javaemul } from "../../../../../../candies/j4ts-2.1.0-SNAPSHOT/bundle.js";
import { Visitor } from "./Visitor.js";
import { ZomicStatement } from "./ZomicStatement.js";

export class Walk extends ZomicStatement implements java.lang.Iterable<ZomicStatement> {
    /*private*/ stmts: java.util.List<ZomicStatement>;

    /**
     * 
     * @param {*} visitor
     */
    public accept(visitor: Visitor) {
        visitor.visitWalk(this);
    }

    public addStatement(stmt: ZomicStatement) {
        this.stmts.add(stmt);
    }

    /**
     * 
     * @return {*}
     */
    public iterator(): java.util.Iterator<ZomicStatement> {
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
