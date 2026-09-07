import { Visitor } from "./Visitor.js";
import { ZomicStatement } from "./ZomicStatement.js";

/**
 * Description here.
 * 
 * @author Scott Vorthmann 2003
 * @param {boolean} build
 * @param {boolean} destroy
 * @class
 * @extends ZomicStatement
 */
export class Build extends ZomicStatement {
    /*private*/ m_build: boolean;

    /*private*/ m_destroy: boolean;

    public constructor(build: boolean, destroy: boolean) {
        super();
        if (this.m_build === undefined) { this.m_build = false; }
        if (this.m_destroy === undefined) { this.m_destroy = false; }
        this.m_build = build;
        this.m_destroy = destroy;
    }

    /**
     * 
     * @param {*} visitor
     */
    public accept(visitor: Visitor) {
        visitor.visitBuild(this.m_build, this.m_destroy);
    }

    public setBuild(value: boolean) {
        this.m_build = value;
    }

    public setDestroy(value: boolean) {
        this.m_destroy = value;
    }

    public justMoving(): boolean {
        return this.m_build === this.m_destroy;
    }
}
Build["__class"] = "com.vzome.core.zomic.program.Build";
