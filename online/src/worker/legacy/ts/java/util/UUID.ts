/* Generated from Java with JSweet 3.2.0-SNAPSHOT - http://www.jsweet.org */
namespace java.util {
    export class UUID {
        /*private*/ value: string;

        constructor(s: string) {
            if (this.value === undefined) { this.value = null; }
            this.value = s;
        }

        public static randomUUID(): UUID {
            return new UUID(/* toString */(''+(Math.random())));
        }

        public toString(): string {
            return this.value;
        }

        public static fromString(s: string): UUID {
            return new UUID(s);
        }
    }
    UUID["__class"] = "java.util.UUID";

}

