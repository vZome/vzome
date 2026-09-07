import { Visitor } from "./Visitor.js";

export abstract class ZomicStatement {
    public abstract accept(visitor: Visitor);

    public setErrors(errors: string[]) {
        this.mErrors = errors;
    }

    public getErrors(): string[] {
        return this.mErrors;
    }

    mErrors: string[];

    constructor() {
        if (this.mErrors === undefined) { this.mErrors = null; }
    }
}
ZomicStatement["__class"] = "com.vzome.core.zomic.program.ZomicStatement";
