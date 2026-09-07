import { AlgebraicNumber } from "../../algebra/AlgebraicNumber.js";
import { Visitor } from "./Visitor.js";
import { ZomicStatement } from "./ZomicStatement.js";

export class Scale extends ZomicStatement {
    /*private*/ m_scale: AlgebraicNumber;

    public constructor(size: AlgebraicNumber) {
        super();
        if (this.m_scale === undefined) { this.m_scale = null; }
        this.m_scale = size;
    }

    /**
     * 
     * @param {*} visitor
     */
    public accept(visitor: Visitor) {
        visitor.visitScale(this.m_scale);
    }
}
Scale["__class"] = "com.vzome.core.zomic.program.Scale";
