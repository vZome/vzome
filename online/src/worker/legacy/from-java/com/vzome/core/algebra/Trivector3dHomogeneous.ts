import { AlgebraicField } from "./AlgebraicField.js";
import { AlgebraicNumber } from "./AlgebraicNumber.js";
import { Vector3dHomogeneous } from "./Vector3dHomogeneous.js";

export class Trivector3dHomogeneous {
    e123: AlgebraicNumber;

    e310: AlgebraicNumber;

    e320: AlgebraicNumber;

    e120: AlgebraicNumber;

    /*private*/ field: AlgebraicField;

    public constructor(e123: AlgebraicNumber, e310: AlgebraicNumber, e320: AlgebraicNumber, e120: AlgebraicNumber, field: AlgebraicField) {
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

    public dual(): Vector3dHomogeneous {
        return new Vector3dHomogeneous(this.e320.negate(), this.e310, this.e120, this.e123.negate(), this.field);
    }
}
Trivector3dHomogeneous["__class"] = "com.vzome.core.algebra.Trivector3dHomogeneous";
