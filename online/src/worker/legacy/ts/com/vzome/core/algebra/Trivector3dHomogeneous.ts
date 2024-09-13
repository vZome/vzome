/* Generated from Java with JSweet 3.2.0-SNAPSHOT - http://www.jsweet.org */
namespace com.vzome.core.algebra {
    export class Trivector3dHomogeneous {
        e123: com.vzome.core.algebra.AlgebraicNumber;

        e310: com.vzome.core.algebra.AlgebraicNumber;

        e320: com.vzome.core.algebra.AlgebraicNumber;

        e120: com.vzome.core.algebra.AlgebraicNumber;

        /*private*/ field: com.vzome.core.algebra.AlgebraicField;

        public constructor(e123: com.vzome.core.algebra.AlgebraicNumber, e310: com.vzome.core.algebra.AlgebraicNumber, e320: com.vzome.core.algebra.AlgebraicNumber, e120: com.vzome.core.algebra.AlgebraicNumber, field: com.vzome.core.algebra.AlgebraicField) {
            if (this.e123 === undefined) { this.e123 = null; }
            if (this.e310 === undefined) { this.e310 = null; }
            if (this.e320 === undefined) { this.e320 = null; }
            if (this.e120 === undefined) { this.e120 = null; }
            if (this.field === undefined) { this.field = null; }
            this.e123 = e123;
            this.e310 = e310;
            this.e320 = e320;
            this.e120 = e120;
            this.field = field;
        }

        public dual(): com.vzome.core.algebra.Vector3dHomogeneous {
            return new com.vzome.core.algebra.Vector3dHomogeneous(this.e320.negate(), this.e310, this.e120, this.e123.negate(), this.field);
        }
    }
    Trivector3dHomogeneous["__class"] = "com.vzome.core.algebra.Trivector3dHomogeneous";

}

