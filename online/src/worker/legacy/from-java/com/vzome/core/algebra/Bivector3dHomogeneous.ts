import { AlgebraicField } from "./AlgebraicField.js";
import { AlgebraicNumber } from "./AlgebraicNumber.js";
import { Trivector3dHomogeneous } from "./Trivector3dHomogeneous.js";
import { Vector3dHomogeneous } from "./Vector3dHomogeneous.js";

export class Bivector3dHomogeneous {
    e12: AlgebraicNumber;

    e23: AlgebraicNumber;

    e31: AlgebraicNumber;

    e10: AlgebraicNumber;

    e20: AlgebraicNumber;

    e30: AlgebraicNumber;

    /*private*/ field: AlgebraicField;

    public constructor(e12: AlgebraicNumber, e23: AlgebraicNumber, e31: AlgebraicNumber, e10: AlgebraicNumber, e20: AlgebraicNumber, e30: AlgebraicNumber, field: AlgebraicField) {
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

    public outer(that: Vector3dHomogeneous): Trivector3dHomogeneous {
        const e123: AlgebraicNumber = this.e12['times$com_vzome_core_algebra_AlgebraicNumber'](that.e3)['plus$com_vzome_core_algebra_AlgebraicNumber'](this.e23['times$com_vzome_core_algebra_AlgebraicNumber'](that.e1))['plus$com_vzome_core_algebra_AlgebraicNumber'](this.e31['times$com_vzome_core_algebra_AlgebraicNumber'](that.e2));
        const e310: AlgebraicNumber = this.e10['times$com_vzome_core_algebra_AlgebraicNumber'](that.e3)['plus$com_vzome_core_algebra_AlgebraicNumber'](this.e31['times$com_vzome_core_algebra_AlgebraicNumber'](that.e0))['minus$com_vzome_core_algebra_AlgebraicNumber'](this.e30['times$com_vzome_core_algebra_AlgebraicNumber'](that.e1));
        const e320: AlgebraicNumber = this.e20['times$com_vzome_core_algebra_AlgebraicNumber'](that.e3)['minus$com_vzome_core_algebra_AlgebraicNumber'](this.e30['times$com_vzome_core_algebra_AlgebraicNumber'](that.e2))['minus$com_vzome_core_algebra_AlgebraicNumber'](this.e23['times$com_vzome_core_algebra_AlgebraicNumber'](that.e0));
        const e120: AlgebraicNumber = this.e12['times$com_vzome_core_algebra_AlgebraicNumber'](that.e0)['plus$com_vzome_core_algebra_AlgebraicNumber'](this.e20['times$com_vzome_core_algebra_AlgebraicNumber'](that.e1))['minus$com_vzome_core_algebra_AlgebraicNumber'](this.e10['times$com_vzome_core_algebra_AlgebraicNumber'](that.e2));
        return new Trivector3dHomogeneous(e123, e310, e320, e120, this.field);
    }
}
Bivector3dHomogeneous["__class"] = "com.vzome.core.algebra.Bivector3dHomogeneous";
