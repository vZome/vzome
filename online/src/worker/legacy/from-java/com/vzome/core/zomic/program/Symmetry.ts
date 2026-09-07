import { Nested } from "./Nested.js";
import { Permute } from "./Permute.js";
import { Visitor } from "./Visitor.js";

export class Symmetry extends Nested {
    /*private*/ permute: Permute;

    /**
     * 
     * @param {*} visitor
     */
    public accept(visitor: Visitor) {
        visitor.visitSymmetry(this, this.permute);
    }

    public setPermute(permute: Permute) {
        this.permute = permute;
    }

    public getPermute(): Permute {
        return this.permute;
    }

    constructor() {
        super();
        if (this.permute === undefined) { this.permute = null; }
    }
}
Symmetry["__class"] = "com.vzome.core.zomic.program.Symmetry";
