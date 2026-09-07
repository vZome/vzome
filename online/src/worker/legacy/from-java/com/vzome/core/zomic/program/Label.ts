import { Visitor } from "./Visitor.js";
import { ZomicStatement } from "./ZomicStatement.js";

/**
 * @author vorth
 * @param {string} id
 * @class
 * @extends ZomicStatement
 */
export class Label extends ZomicStatement {
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
    public accept(visitor: Visitor) {
        visitor.visitLabel(this.mLabel);
    }
}
Label["__class"] = "com.vzome.core.zomic.program.Label";
