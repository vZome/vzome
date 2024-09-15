/* Generated from Java with JSweet 3.2.0-SNAPSHOT - http://www.jsweet.org */
namespace com.vzome.core.zomic.program {
    export abstract class ZomicStatement {
        public abstract accept(visitor: com.vzome.core.zomic.program.Visitor);

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

}

