import { Visitor } from "./Visitor.js";
import { ZomicStatement } from "./ZomicStatement.js";

export class Nested extends ZomicStatement {
    m_body: ZomicStatement;

    /**
     * 
     * @param {*} visitor
     */
    public accept(visitor: Visitor) {
        visitor.visitNested(this);
    }

    public setBody(body: ZomicStatement) {
        this.m_body = body;
    }

    public getBody(): ZomicStatement {
        return this.m_body;
    }

    constructor() {
        super();
        if (this.m_body === undefined) { this.m_body = null; }
    }
}
Nested["__class"] = "com.vzome.core.zomic.program.Nested";
