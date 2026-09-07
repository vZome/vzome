import { Nested } from "./Nested.js";
import { Visitor } from "./Visitor.js";

/**
 * Description here.
 * 
 * @author Scott Vorthmann 2003
 * @param {number} state
 * @class
 * @extends Nested
 */
export class Save extends Nested {
    /*private*/ m_state: number;

    public constructor(state: number) {
        super();
        if (this.m_state === undefined) { this.m_state = 0; }
        this.m_state = state;
    }

    /**
     * 
     * @param {*} visitor
     */
    public accept(visitor: Visitor) {
        visitor.visitSave(this, this.m_state);
    }

    public setState(state: number) {
        this.m_state = state;
    }
}
Save["__class"] = "com.vzome.core.zomic.program.Save";
