import { Nested } from "./Nested.js";
import { Visitor } from "./Visitor.js";

export class Repeat extends Nested {
    /*private*/ repetitions: number;

    public constructor(repetitions: number) {
        super();
        if (this.repetitions === undefined) { this.repetitions = 0; }
        this.repetitions = repetitions;
    }

    /**
     * 
     * @param {*} visitor
     */
    public accept(visitor: Visitor) {
        visitor.visitRepeat(this, this.repetitions);
    }
}
Repeat["__class"] = "com.vzome.core.zomic.program.Repeat";
