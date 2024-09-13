/* Generated from Java with JSweet 3.2.0-SNAPSHOT - http://www.jsweet.org */
namespace com.vzome.jsweet {
    export class JsStrut extends com.vzome.jsweet.JsManifestation implements com.vzome.core.model.Strut {
        public constructor(field: com.vzome.core.algebra.AlgebraicField, adapter: Object, coords: number[][][]) {
            super(field, adapter, coords);
        }

        /**
         * 
         * @return {com.vzome.core.algebra.AlgebraicVector}
         */
        public getLocation(): com.vzome.core.algebra.AlgebraicVector {
            return (<com.vzome.jsweet.JsAlgebraicField><any>this.field).createVectorFromTDs(this.vectors[0]);
        }

        /**
         * 
         * @return {com.vzome.core.construction.Construction}
         */
        public toConstruction(): com.vzome.core.construction.Construction {
            return new com.vzome.core.construction.SegmentJoiningPoints(new com.vzome.core.construction.FreePoint(this.getLocation()), new com.vzome.core.construction.FreePoint(this.getEnd()));
        }

        /**
         * 
         * @return {com.vzome.core.algebra.AlgebraicVector}
         */
        public getEnd(): com.vzome.core.algebra.AlgebraicVector {
            return (<com.vzome.jsweet.JsAlgebraicField><any>this.field).createVectorFromTDs(this.vectors[1]);
        }

        /**
         * 
         * @return {com.vzome.core.algebra.AlgebraicVector}
         */
        public getOffset(): com.vzome.core.algebra.AlgebraicVector {
            const start: com.vzome.core.algebra.AlgebraicVector = this.getLocation();
            const end: com.vzome.core.algebra.AlgebraicVector = this.getEnd();
            return end.minus(start);
        }

        /**
         * 
         * @param {com.vzome.core.algebra.AlgebraicVector} vector
         */
        public setZoneVector(vector: com.vzome.core.algebra.AlgebraicVector) {
        }

        /**
         * 
         * @return {com.vzome.core.algebra.AlgebraicVector}
         */
        public getZoneVector(): com.vzome.core.algebra.AlgebraicVector {
            return this.getOffset();
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
            const m_end1: com.vzome.core.algebra.AlgebraicVector = this.getLocation();
            const m_end2: com.vzome.core.algebra.AlgebraicVector = this.getEnd();
            return (m_end1.compareTo(m_end2) < 0) ? m_end1 : m_end2;
        }

        public getCanonicalGreaterEnd(): com.vzome.core.algebra.AlgebraicVector {
            const m_end1: com.vzome.core.algebra.AlgebraicVector = this.getLocation();
            const m_end2: com.vzome.core.algebra.AlgebraicVector = this.getEnd();
            return (m_end1.compareTo(m_end2) > 0) ? m_end1 : m_end2;
        }
    }
    JsStrut["__class"] = "com.vzome.jsweet.JsStrut";
    JsStrut["__interfaces"] = ["com.vzome.core.model.GroupElement","com.vzome.core.model.Strut","java.lang.Comparable","com.vzome.core.model.Manifestation"];


}

