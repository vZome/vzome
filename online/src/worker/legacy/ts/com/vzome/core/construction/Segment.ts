/* Generated from Java with JSweet 3.2.0-SNAPSHOT - http://www.jsweet.org */
namespace com.vzome.core.construction {
    /**
     * @author Scott Vorthmann
     * @extends com.vzome.core.construction.Construction
     * @class
     */
    export abstract class Segment extends com.vzome.core.construction.Construction {
        /*private*/ mStart: com.vzome.core.algebra.AlgebraicVector;

        /*private*/ mOffset: com.vzome.core.algebra.AlgebraicVector;

        /*private*/ mEnd: com.vzome.core.algebra.AlgebraicVector;

        constructor(field: com.vzome.core.algebra.AlgebraicField) {
            super(field);
            if (this.mStart === undefined) { this.mStart = null; }
            if (this.mOffset === undefined) { this.mOffset = null; }
            if (this.mEnd === undefined) { this.mEnd = null; }
        }

        public getSignature(): string {
            const start: string = this.mStart.projectTo3d(true).toString();
            const end: string = this.getEnd().projectTo3d(true).toString();
            if (/* compareTo */start.localeCompare(end) <= 0)return start + "," + end; else return end + "," + start;
        }

        /**
         * 
         * @return {boolean}
         */
        public is3d(): boolean {
            return this.mStart.dimension() === 3 && this.mOffset.dimension() === 3;
        }

        setStateVariables(start: com.vzome.core.algebra.AlgebraicVector, offset: com.vzome.core.algebra.AlgebraicVector, impossible: boolean): boolean {
            if (impossible){
                if (this.isImpossible())return false;
                this.setImpossible(true);
                return true;
            }
            if (offset.equals(this.mOffset) && !this.isImpossible() && start.equals(this.mStart))return false;
            this.mOffset = offset;
            this.mStart = start;
            this.mEnd = null;
            this.setImpossible(false);
            return true;
        }

        public getStart(): com.vzome.core.algebra.AlgebraicVector {
            return this.mStart;
        }

        public getEnd(): com.vzome.core.algebra.AlgebraicVector {
            if (this.mEnd == null)this.mEnd = this.mStart.plus(this.mOffset);
            return this.mEnd;
        }

        public getOffset(): com.vzome.core.algebra.AlgebraicVector {
            return this.mOffset;
        }

        public getCentroid(): com.vzome.core.algebra.AlgebraicVector {
            return com.vzome.core.algebra.AlgebraicVectors.getCentroid([this.mStart, this.mEnd]);
        }

        /**
         * 
         * @param {*} doc
         * @return {*}
         */
        public getXml(doc: org.w3c.dom.Document): org.w3c.dom.Element {
            const result: org.w3c.dom.Element = doc.createElement("segment");
            result.setAttribute("start", this.mStart.getVectorExpression$int(com.vzome.core.algebra.AlgebraicField.ZOMIC_FORMAT));
            result.setAttribute("end", this.getEnd().getVectorExpression$int(com.vzome.core.algebra.AlgebraicField.ZOMIC_FORMAT));
            return result;
        }

        /**
         * 
         * @return {string}
         */
        public toString(): string {
            return "segment from " + this.mStart + " to " + this.getEnd();
        }
    }
    Segment["__class"] = "com.vzome.core.construction.Segment";

}

