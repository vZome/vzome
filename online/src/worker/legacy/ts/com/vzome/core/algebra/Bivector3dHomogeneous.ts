/* Generated from Java with JSweet 3.2.0-SNAPSHOT - http://www.jsweet.org */
namespace com.vzome.core.algebra {
    export class Bivector3dHomogeneous {
        e12: com.vzome.core.algebra.AlgebraicNumber;

        e23: com.vzome.core.algebra.AlgebraicNumber;

        e31: com.vzome.core.algebra.AlgebraicNumber;

        e10: com.vzome.core.algebra.AlgebraicNumber;

        e20: com.vzome.core.algebra.AlgebraicNumber;

        e30: com.vzome.core.algebra.AlgebraicNumber;

        /*private*/ field: com.vzome.core.algebra.AlgebraicField;

        public constructor(e12: com.vzome.core.algebra.AlgebraicNumber, e23: com.vzome.core.algebra.AlgebraicNumber, e31: com.vzome.core.algebra.AlgebraicNumber, e10: com.vzome.core.algebra.AlgebraicNumber, e20: com.vzome.core.algebra.AlgebraicNumber, e30: com.vzome.core.algebra.AlgebraicNumber, field: com.vzome.core.algebra.AlgebraicField) {
            if (this.e12 === undefined) { this.e12 = null; }
            if (this.e23 === undefined) { this.e23 = null; }
            if (this.e31 === undefined) { this.e31 = null; }
            if (this.e10 === undefined) { this.e10 = null; }
            if (this.e20 === undefined) { this.e20 = null; }
            if (this.e30 === undefined) { this.e30 = null; }
            if (this.field === undefined) { this.field = null; }
            this.e12 = e12;
            this.e23 = e23;
            this.e31 = e31;
            this.e10 = e10;
            this.e20 = e20;
            this.e30 = e30;
            this.field = field;
        }

        public outer(that: com.vzome.core.algebra.Vector3dHomogeneous): com.vzome.core.algebra.Trivector3dHomogeneous {
            const e123: com.vzome.core.algebra.AlgebraicNumber = this.e12['times$com_vzome_core_algebra_AlgebraicNumber'](that.e3)['plus$com_vzome_core_algebra_AlgebraicNumber'](this.e23['times$com_vzome_core_algebra_AlgebraicNumber'](that.e1))['plus$com_vzome_core_algebra_AlgebraicNumber'](this.e31['times$com_vzome_core_algebra_AlgebraicNumber'](that.e2));
            const e310: com.vzome.core.algebra.AlgebraicNumber = this.e10['times$com_vzome_core_algebra_AlgebraicNumber'](that.e3)['plus$com_vzome_core_algebra_AlgebraicNumber'](this.e31['times$com_vzome_core_algebra_AlgebraicNumber'](that.e0))['minus$com_vzome_core_algebra_AlgebraicNumber'](this.e30['times$com_vzome_core_algebra_AlgebraicNumber'](that.e1));
            const e320: com.vzome.core.algebra.AlgebraicNumber = this.e20['times$com_vzome_core_algebra_AlgebraicNumber'](that.e3)['minus$com_vzome_core_algebra_AlgebraicNumber'](this.e30['times$com_vzome_core_algebra_AlgebraicNumber'](that.e2))['minus$com_vzome_core_algebra_AlgebraicNumber'](this.e23['times$com_vzome_core_algebra_AlgebraicNumber'](that.e0));
            const e120: com.vzome.core.algebra.AlgebraicNumber = this.e12['times$com_vzome_core_algebra_AlgebraicNumber'](that.e0)['plus$com_vzome_core_algebra_AlgebraicNumber'](this.e20['times$com_vzome_core_algebra_AlgebraicNumber'](that.e1))['minus$com_vzome_core_algebra_AlgebraicNumber'](this.e10['times$com_vzome_core_algebra_AlgebraicNumber'](that.e2));
            return new com.vzome.core.algebra.Trivector3dHomogeneous(e123, e310, e320, e120, this.field);
        }
    }
    Bivector3dHomogeneous["__class"] = "com.vzome.core.algebra.Bivector3dHomogeneous";

}

