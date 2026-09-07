import { Axis } from "../../math/symmetry/Axis.js";
import { Permute } from "./Permute.js";
import { Visitor } from "./Visitor.js";

export class Reflect extends Permute {
    /**
     * 
     * @param {*} visitor
     */
    public accept(visitor: Visitor) {
        visitor.visitReflect(this.getAxis());
    }

    public constructor() {
        super(null);
    }

    /**
     * 
     * @param {Axis} axis
     */
    public setAxis(axis: Axis) {
        super.setAxis(axis);
    }
}
Reflect["__class"] = "com.vzome.core.zomic.program.Reflect";
