/* Generated from Java with JSweet 3.2.0-SNAPSHOT - http://www.jsweet.org */
namespace com.vzome.core.construction {
    /**
     * @author Scott Vorthmann
     * @class
     */
    export abstract class Construction {
        field: com.vzome.core.algebra.AlgebraicField;

        /**
         * true for "impossible" constructions
         */
        /*private*/ mImpossible: boolean;

        constructor(field: com.vzome.core.algebra.AlgebraicField) {
            if (this.field === undefined) { this.field = null; }
            this.mImpossible = false;
            this.mIndex = -1;
            this.__failed = false;
            if (this.color === undefined) { this.color = null; }
            this.field = field;
        }

        public getField(): com.vzome.core.algebra.AlgebraicField {
            return this.field;
        }

        /*private*/ mIndex: number;

        public setIndex(index: number) {
            this.mIndex = index;
        }

        public getIndex(): number {
            return this.mIndex;
        }

        public isImpossible(): boolean {
            return this.mImpossible;
        }

        public setImpossible(value: boolean) {
            this.mImpossible = value;
        }

        public abstract is3d(): boolean;

        /**
         * Update the state variables (like location) of this construction
         * according to the current parameters and attributes.
         * 
         * This function does NOT propagate updates to derivatives,
         * nor does it notify listeners or otherwise drive rendering.
         * 
         * @return {boolean} true if the state changed.
         */
        abstract mapParamsToState(): boolean;

        public abstract getXml(doc: org.w3c.dom.Document): org.w3c.dom.Element;

        /*private*/ __failed: boolean;

        /*private*/ color: com.vzome.core.construction.Color;

        public setFailed() {
            this.__failed = true;
        }

        public failed(): boolean {
            return this.__failed;
        }

        public setColor(color: com.vzome.core.construction.Color) {
            this.color = color;
        }

        public getColor(): com.vzome.core.construction.Color {
            return this.color;
        }

        public getSignature(): string {
            return "";
        }
    }
    Construction["__class"] = "com.vzome.core.construction.Construction";

}

