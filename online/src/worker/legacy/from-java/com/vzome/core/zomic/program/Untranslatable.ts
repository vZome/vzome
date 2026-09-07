import { Visitor } from "./Visitor.js";
import { ZomicStatement } from "./ZomicStatement.js";

/**
 * @author vorth
 * @param {string} msg
 * @class
 * @extends ZomicStatement
 */
export class Untranslatable extends ZomicStatement {
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
    public accept(visitor: Visitor) {
        visitor.visitUntranslatable(this.message == null ? "" : this.message);
    }
}
Untranslatable["__class"] = "com.vzome.core.zomic.program.Untranslatable";
