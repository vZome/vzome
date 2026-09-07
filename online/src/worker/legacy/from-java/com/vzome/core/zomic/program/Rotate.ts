import { Axis } from "../../math/symmetry/Axis.js";
import { Permute } from "./Permute.js";
import { Visitor } from "./Visitor.js";

export class Rotate extends Permute {
    /*private*/ steps: number;

    /**
     * 
     * @param {*} visitor
     */
    public accept(visitor: Visitor) {
        visitor.visitRotate(this.getAxis(), this.steps);
    }

    public constructor(axis: Axis, steps: number) {
        super(axis);
        if (this.steps === undefined) { this.steps = 0; }
        this.steps = steps;
    }

    public setSteps(steps: number) {
        this.steps = steps;
    }
}
Rotate["__class"] = "com.vzome.core.zomic.program.Rotate";
