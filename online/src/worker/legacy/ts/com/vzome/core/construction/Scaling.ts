/* Generated from Java with JSweet 3.2.0-SNAPSHOT - http://www.jsweet.org */
namespace com.vzome.core.construction {
    export class Scaling extends com.vzome.core.construction.Transformation {
        /*private*/ s1: com.vzome.core.construction.Segment;

        /*private*/ s2: com.vzome.core.construction.Segment;

        /*private*/ center: com.vzome.core.construction.Point;

        /*private*/ symmetry: com.vzome.core.math.symmetry.Symmetry;

        public constructor(s1: com.vzome.core.construction.Segment, s2: com.vzome.core.construction.Segment, center: com.vzome.core.construction.Point, symmetry: com.vzome.core.math.symmetry.Symmetry) {
            super(s1.field);
            if (this.s1 === undefined) { this.s1 = null; }
            if (this.s2 === undefined) { this.s2 = null; }
            if (this.center === undefined) { this.center = null; }
            if (this.symmetry === undefined) { this.symmetry = null; }
            this.mOffset = this.field.projectTo3d(center.getLocation(), true);
            this.s1 = s1;
            this.s2 = s2;
            this.center = center;
            this.symmetry = symmetry;
            this.mapParamsToState();
        }

        /**
         * 
         * @return {boolean}
         */
        mapParamsToState(): boolean {
            const zone1: com.vzome.core.math.symmetry.Axis = this.symmetry['getAxis$com_vzome_core_algebra_AlgebraicVector'](this.s1.getOffset());
            const zone2: com.vzome.core.math.symmetry.Axis = this.symmetry['getAxis$com_vzome_core_algebra_AlgebraicVector'](this.s2.getOffset());
            const orbit: com.vzome.core.math.symmetry.Direction = zone1.getDirection();
            if (orbit !== zone2.getDirection())return this.setStateVariables(null, null, true);
            const len1: com.vzome.core.algebra.AlgebraicNumber = zone1.getLength(this.s1.getOffset());
            const len2: com.vzome.core.algebra.AlgebraicNumber = zone2.getLength(this.s2.getOffset());
            const scale: com.vzome.core.algebra.AlgebraicNumber = len2.dividedBy(len1);
            const transform: com.vzome.core.algebra.AlgebraicMatrix = new com.vzome.core.algebra.AlgebraicMatrix(this.field.basisVector(3, com.vzome.core.algebra.AlgebraicVector.X).scale(scale), this.field.basisVector(3, com.vzome.core.algebra.AlgebraicVector.Y).scale(scale), this.field.basisVector(3, com.vzome.core.algebra.AlgebraicVector.Z).scale(scale));
            return this.setStateVariables(transform, this.center.getLocation(), false);
        }
    }
    Scaling["__class"] = "com.vzome.core.construction.Scaling";

}

