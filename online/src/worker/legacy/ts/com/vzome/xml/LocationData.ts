/* Generated from Java with JSweet 3.2.0-SNAPSHOT - http://www.jsweet.org */
namespace com.vzome.xml {
    export class LocationData {
        public static LOCATION_DATA_KEY: string = "locationDataKey";

        /*private*/ systemId: string;

        /*private*/ startLine: number;

        /*private*/ startColumn: number;

        /*private*/ endLine: number;

        /*private*/ endColumn: number;

        public constructor(systemId: string, startLine: number, startColumn: number, endLine: number, endColumn: number) {
            if (this.systemId === undefined) { this.systemId = null; }
            if (this.startLine === undefined) { this.startLine = 0; }
            if (this.startColumn === undefined) { this.startColumn = 0; }
            if (this.endLine === undefined) { this.endLine = 0; }
            if (this.endColumn === undefined) { this.endColumn = 0; }
            this.systemId = systemId;
            this.startLine = startLine;
            this.startColumn = startColumn;
            this.endLine = endLine;
            this.endColumn = endColumn;
        }

        public getSystemId(): string {
            return this.systemId;
        }

        public getStartLine(): number {
            return this.startLine;
        }

        public getStartColumn(): number {
            return this.startColumn;
        }

        public getEndLine(): number {
            return this.endLine;
        }

        public getEndColumn(): number {
            return this.endColumn;
        }

        /**
         * 
         * @return {string}
         */
        public toString(): string {
            return this.getSystemId() + "[line " + this.startLine + ":" + this.startColumn + " to line " + this.endLine + ":" + this.endColumn + "]";
        }
    }
    LocationData["__class"] = "com.vzome.xml.LocationData";

}

