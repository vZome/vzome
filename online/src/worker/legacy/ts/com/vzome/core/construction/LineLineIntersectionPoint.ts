/* Generated from Java with JSweet 3.2.0-SNAPSHOT - http://www.jsweet.org */
namespace com.vzome.core.construction {
    export class LineLineIntersectionPoint extends com.vzome.core.construction.Point {
        /*private*/ line1: com.vzome.core.construction.Line;

        /*private*/ line2: com.vzome.core.construction.Line;

        public constructor(line1: com.vzome.core.construction.Line, line2: com.vzome.core.construction.Line) {
            super(line1.field);
            if (this.line1 === undefined) { this.line1 = null; }
            if (this.line2 === undefined) { this.line2 = null; }
            this.line1 = line1;
            this.line2 = line2;
            this.mapParamsToState();
        }

        /**
         * 
         * @return {boolean}
         */
        mapParamsToState(): boolean {
            const p1: com.vzome.core.algebra.AlgebraicVector = this.line1.getStart();
            const p21: com.vzome.core.algebra.AlgebraicVector = this.line1.getDirection();
            const p3: com.vzome.core.algebra.AlgebraicVector = this.line2.getStart();
            const p43: com.vzome.core.algebra.AlgebraicVector = this.line2.getDirection();
            if (p1.equals(p3))return this.setStateVariable(p1, false);
            const p2: com.vzome.core.algebra.AlgebraicVector = p1.plus(p21);
            if (p2.equals(p3))return this.setStateVariable(p2, false);
            const p4: com.vzome.core.algebra.AlgebraicVector = p3.plus(p43);
            if (p1.equals(p4))return this.setStateVariable(p1, false);
            if (p2.equals(p4))return this.setStateVariable(p2, false);
            const p13: com.vzome.core.algebra.AlgebraicVector = p1.minus(p3);
            const d1343: com.vzome.core.algebra.AlgebraicNumber = p13.dot(p43);
            const d4321: com.vzome.core.algebra.AlgebraicNumber = p43.dot(p21);
            const d1321: com.vzome.core.algebra.AlgebraicNumber = p13.dot(p21);
            const d4343: com.vzome.core.algebra.AlgebraicNumber = p43.dot(p43);
            const d2121: com.vzome.core.algebra.AlgebraicNumber = p21.dot(p21);
            const denom: com.vzome.core.algebra.AlgebraicNumber = d2121['times$com_vzome_core_algebra_AlgebraicNumber'](d4343)['minus$com_vzome_core_algebra_AlgebraicNumber'](d4321['times$com_vzome_core_algebra_AlgebraicNumber'](d4321));
            if (denom.isZero())return this.setStateVariable(null, true);
            const numer: com.vzome.core.algebra.AlgebraicNumber = d1343['times$com_vzome_core_algebra_AlgebraicNumber'](d4321)['minus$com_vzome_core_algebra_AlgebraicNumber'](d1321['times$com_vzome_core_algebra_AlgebraicNumber'](d4343));
            const mua: com.vzome.core.algebra.AlgebraicNumber = numer.dividedBy(denom);
            const mub: com.vzome.core.algebra.AlgebraicNumber = d1343['plus$com_vzome_core_algebra_AlgebraicNumber'](d4321['times$com_vzome_core_algebra_AlgebraicNumber'](mua)).dividedBy(d4343);
            const pa: com.vzome.core.algebra.AlgebraicVector = p1.plus(p21.scale(mua));
            const pb: com.vzome.core.algebra.AlgebraicVector = p3.plus(p43.scale(mub));
            if (!pa.equals(pb))return this.setStateVariable(null, true);
            return this.setStateVariable(pb, false);
        }
    }
    LineLineIntersectionPoint["__class"] = "com.vzome.core.construction.LineLineIntersectionPoint";

}

