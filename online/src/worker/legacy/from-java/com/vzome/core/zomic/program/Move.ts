import { AlgebraicNumber } from "../../algebra/AlgebraicNumber.js";
import { Axis } from "../../math/symmetry/Axis.js";
import { Visitor } from "./Visitor.js";
import { ZomicStatement } from "./ZomicStatement.js";

export class Move extends ZomicStatement {
    axis: Axis;

    length: AlgebraicNumber;

    public constructor(axis: Axis, len: AlgebraicNumber) {
        super();
        if (this.axis === undefined) { this.axis = null; }
        if (this.length === undefined) { this.length = null; }
        this.axis = axis;
        this.length = len;
    }

    /**
     * 
     * @param {*} visitor
     */
    public accept(visitor: Visitor) {
        visitor.visitMove(this.axis, this.length);
    }

    /**
     * @return
     * @return {*}
     */
    public getLength(): AlgebraicNumber {
        return this.length;
    }

    public getAxis(): Axis {
        return this.axis;
    }

    /**
     * Needed only for Zomic XMLS2AST.  TODO: remove this by
     * rearranging the XML?
     * @param axis2
     * @param {*} len
     * @param {Axis} axis
     */
    public reset(axis: Axis, len: AlgebraicNumber) {
        this.axis = axis;
        this.length = len;
    }
}
Move["__class"] = "com.vzome.core.zomic.program.Move";
