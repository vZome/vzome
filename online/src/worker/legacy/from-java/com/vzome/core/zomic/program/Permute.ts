import { Axis } from "../../math/symmetry/Axis.js";
import { ZomicStatement } from "./ZomicStatement.js";

/**
 * @author vorth
 * @param {Axis} axis
 * @class
 * @extends ZomicStatement
 */
export abstract class Permute extends ZomicStatement {
    /*private*/ m_axis: Axis;

    public constructor(axis: Axis) {
        super();
        if (this.m_axis === undefined) { this.m_axis = null; }
        this.m_axis = axis;
    }

    public setAxis(axis: Axis) {
        this.m_axis = axis;
    }

    public getOrder(): number {
        if (this.m_axis == null)return 2;
        return this.m_axis.getRotationPermutation().getOrder();
    }

    public getAxis(): Axis {
        return this.m_axis;
    }
}
Permute["__class"] = "com.vzome.core.zomic.program.Permute";
