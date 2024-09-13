/* Generated from Java with JSweet 3.2.0-SNAPSHOT - http://www.jsweet.org */
namespace com.vzome.core.model {
    /**
     * @author Scott Vorthmann
     * @param {com.vzome.core.algebra.AlgebraicVector} end1
     * @param {com.vzome.core.algebra.AlgebraicVector} end2
     * @class
     * @extends com.vzome.core.model.ManifestationImpl
     */
    export class StrutImpl extends com.vzome.core.model.ManifestationImpl implements com.vzome.core.model.Strut {
        /*private*/ m_end1: com.vzome.core.algebra.AlgebraicVector;

        /*private*/ m_end2: com.vzome.core.algebra.AlgebraicVector;

        /*private*/ zoneVector: com.vzome.core.algebra.AlgebraicVector;

        /*private*/ label: string;

        public constructor(end1: com.vzome.core.algebra.AlgebraicVector, end2: com.vzome.core.algebra.AlgebraicVector) {
            super();
            if (this.m_end1 === undefined) { this.m_end1 = null; }
            if (this.m_end2 === undefined) { this.m_end2 = null; }
            if (this.zoneVector === undefined) { this.zoneVector = null; }
            if (this.label === undefined) { this.label = null; }
            this.m_end1 = end1;
            this.m_end2 = end2;
        }

        public getZoneVector(): com.vzome.core.algebra.AlgebraicVector {
            if (this.zoneVector != null)return this.zoneVector; else return this.getOffset();
        }

        public setZoneVector(vector: com.vzome.core.algebra.AlgebraicVector) {
            this.zoneVector = vector;
        }

        /**
         * 
         * @return {number}
         */
        public hashCode(): number {
            const result: number = /* hashCode */(<any>((o: any) => { if (o.hashCode) { return o.hashCode(); } else { return o.toString().split('').reduce((prevHash, currVal) => (((prevHash << 5) - prevHash) + currVal.charCodeAt(0))|0, 0); }})(this.m_end1)) ^ /* hashCode */(<any>((o: any) => { if (o.hashCode) { return o.hashCode(); } else { return o.toString().split('').reduce((prevHash, currVal) => (((prevHash << 5) - prevHash) + currVal.charCodeAt(0))|0, 0); }})(this.m_end2));
            return result;
        }

        /**
         * 
         * @param {*} obj
         * @return {boolean}
         */
        public equals(obj: any): boolean {
            if (obj == null)return false;
            if (obj === this)return true;
            if (!(obj != null && obj instanceof <any>com.vzome.core.model.StrutImpl))return false;
            const other: StrutImpl = <StrutImpl>obj;
            const otherStart: com.vzome.core.algebra.AlgebraicVector = other.m_end1;
            const otherEnd: com.vzome.core.algebra.AlgebraicVector = other.m_end2;
            if (otherStart.equals(this.m_end1))return otherEnd.equals(this.m_end2); else if (otherEnd.equals(this.m_end1))return otherStart.equals(this.m_end2); else return false;
        }

        /**
         * 
         * @param {*} other
         * @return {number}
         */
        public compareTo(other: com.vzome.core.model.Strut): number {
            if (this === other){
                return 0;
            }
            if (/* equals */(<any>((o1: any, o2: any) => { if (o1 && o1.equals) { return o1.equals(o2); } else { return o1 === o2; } })(other,this))){
                return 0;
            }
            const thisFirst: com.vzome.core.algebra.AlgebraicVector = this.getCanonicalLesserEnd();
            const thisLast: com.vzome.core.algebra.AlgebraicVector = this.getCanonicalGreaterEnd();
            const otherFirst: com.vzome.core.algebra.AlgebraicVector = other.getCanonicalLesserEnd();
            const otherLast: com.vzome.core.algebra.AlgebraicVector = other.getCanonicalGreaterEnd();
            const comparison: number = thisFirst.compareTo(otherFirst);
            return (comparison === 0) ? thisLast.compareTo(otherLast) : comparison;
        }

        public getCanonicalLesserEnd(): com.vzome.core.algebra.AlgebraicVector {
            return (this.m_end1.compareTo(this.m_end2) < 0) ? this.m_end1 : this.m_end2;
        }

        public getCanonicalGreaterEnd(): com.vzome.core.algebra.AlgebraicVector {
            return (this.m_end1.compareTo(this.m_end2) > 0) ? this.m_end1 : this.m_end2;
        }

        /**
         * 
         * @return {com.vzome.core.algebra.AlgebraicVector}
         */
        public getLocation(): com.vzome.core.algebra.AlgebraicVector {
            return this.m_end1;
        }

        /**
         * 
         * @return {com.vzome.core.algebra.AlgebraicVector}
         */
        public getCentroid(): com.vzome.core.algebra.AlgebraicVector {
            return com.vzome.core.algebra.AlgebraicVectors.getCentroid([this.m_end1, this.m_end2]);
        }

        /**
         * 
         * @return {com.vzome.core.construction.Construction}
         */
        public toConstruction(): com.vzome.core.construction.Construction {
            const first: com.vzome.core.construction.Construction = this.getFirstConstruction();
            if (first != null && first.is3d())return first;
            const field: com.vzome.core.algebra.AlgebraicField = this.m_end1.getField();
            const pt1: com.vzome.core.construction.Point = new com.vzome.core.construction.FreePoint(field.projectTo3d(this.m_end1, true));
            const pt2: com.vzome.core.construction.Point = new com.vzome.core.construction.FreePoint(field.projectTo3d(this.m_end2, true));
            return new com.vzome.core.construction.SegmentJoiningPoints(pt1, pt2);
        }

        public getEnd(): com.vzome.core.algebra.AlgebraicVector {
            return this.m_end2;
        }

        public getOffset(): com.vzome.core.algebra.AlgebraicVector {
            return this.m_end2.minus(this.m_end1);
        }

        /**
         * 
         * @return {string}
         */
        public toString(): string {
            return "strut from " + this.m_end1.toString() + " to " + this.m_end2.toString();
        }

        /**
         * 
         * @param {string} label
         */
        public setLabel(label: string) {
            this.label = label;
        }

        /**
         * 
         * @return {string}
         */
        public getLabel(): string {
            return this.label;
        }
    }
    StrutImpl["__class"] = "com.vzome.core.model.StrutImpl";
    StrutImpl["__interfaces"] = ["com.vzome.core.model.HasRenderedObject","com.vzome.core.model.GroupElement","com.vzome.core.model.Strut","java.lang.Comparable","com.vzome.core.model.Manifestation"];


}

