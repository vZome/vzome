import { AlgebraicField } from "./AlgebraicField.js";
import { AlgebraicNumber } from "./AlgebraicNumber.js";
import { AlgebraicVector } from "./AlgebraicVector.js";
import { Bivector3dHomogeneous } from "./Bivector3dHomogeneous.js";

export class Vector3dHomogeneous {
    e1: AlgebraicNumber;

    e2: AlgebraicNumber;

    e3: AlgebraicNumber;

    e0: AlgebraicNumber;

    /*private*/ field: AlgebraicField;

    public constructor(e1?: any, e2?: any, e3?: any, e0?: any, field?: any) {
        if (((e1 != null && (e1.constructor != null && e1.constructor["__interfaces"] != null && e1.constructor["__interfaces"].indexOf("com.vzome.core.algebra.AlgebraicNumber") >= 0)) || e1 === null) && ((e2 != null && (e2.constructor != null && e2.constructor["__interfaces"] != null && e2.constructor["__interfaces"].indexOf("com.vzome.core.algebra.AlgebraicNumber") >= 0)) || e2 === null) && ((e3 != null && (e3.constructor != null && e3.constructor["__interfaces"] != null && e3.constructor["__interfaces"].indexOf("com.vzome.core.algebra.AlgebraicNumber") >= 0)) || e3 === null) && ((e0 != null && (e0.constructor != null && e0.constructor["__interfaces"] != null && e0.constructor["__interfaces"].indexOf("com.vzome.core.algebra.AlgebraicNumber") >= 0)) || e0 === null) && ((field != null && (field.constructor != null && field.constructor["__interfaces"] != null && field.constructor["__interfaces"].indexOf("com.vzome.core.algebra.AlgebraicField") >= 0)) || field === null)) {
            let __args = arguments;
            if (this.e1 === undefined) { this.e1 = null; } 
            if (this.e2 === undefined) { this.e2 = null; } 
            if (this.e3 === undefined) { this.e3 = null; } 
            if (this.e0 === undefined) { this.e0 = null; } 
            if (this.field === undefined) { this.field = null; } 
            this.e1 = e1;
            this.e2 = e2;
            this.e3 = e3;
            this.e0 = e0;
            this.field = field;
        } else if (((e1 != null && (e1.constructor != null && e1.constructor["__interfaces"] != null && e1.constructor["__interfaces"].indexOf("com.vzome.core.algebra.AlgebraicNumber") >= 0)) || e1 === null) && ((e2 != null && (e2.constructor != null && e2.constructor["__interfaces"] != null && e2.constructor["__interfaces"].indexOf("com.vzome.core.algebra.AlgebraicNumber") >= 0)) || e2 === null) && ((e3 != null && (e3.constructor != null && e3.constructor["__interfaces"] != null && e3.constructor["__interfaces"].indexOf("com.vzome.core.algebra.AlgebraicNumber") >= 0)) || e3 === null) && ((e0 != null && (e0.constructor != null && e0.constructor["__interfaces"] != null && e0.constructor["__interfaces"].indexOf("com.vzome.core.algebra.AlgebraicField") >= 0)) || e0 === null) && field === undefined) {
            let __args = arguments;
            let field: any = __args[3];
            {
                let __args = arguments;
                let e2: any = __args[0];
                let e3: any = __args[0];
                let e0: any = __args[4].one();
                if (this.e1 === undefined) { this.e1 = null; } 
                if (this.e2 === undefined) { this.e2 = null; } 
                if (this.e3 === undefined) { this.e3 = null; } 
                if (this.e0 === undefined) { this.e0 = null; } 
                if (this.field === undefined) { this.field = null; } 
                this.e1 = e1;
                this.e2 = e2;
                this.e3 = e3;
                this.e0 = e0;
                this.field = field;
            }
        } else if (((e1 != null && e1 instanceof <any>AlgebraicVector) || e1 === null) && ((e2 != null && (e2.constructor != null && e2.constructor["__interfaces"] != null && e2.constructor["__interfaces"].indexOf("com.vzome.core.algebra.AlgebraicField") >= 0)) || e2 === null) && e3 === undefined && e0 === undefined && field === undefined) {
            let __args = arguments;
            let v: any = __args[0];
            let field: any = __args[1];
            {
                let __args = arguments;
                let e1: any = v.getComponent(0);
                let e2: any = v.getComponent(1);
                let e3: any = v.getComponent(2);
                {
                    let __args = arguments;
                    let e2: any = __args[0];
                    let e3: any = __args[0];
                    let e0: any = __args[4].one();
                    if (this.e1 === undefined) { this.e1 = null; } 
                    if (this.e2 === undefined) { this.e2 = null; } 
                    if (this.e3 === undefined) { this.e3 = null; } 
                    if (this.e0 === undefined) { this.e0 = null; } 
                    if (this.field === undefined) { this.field = null; } 
                    this.e1 = e1;
                    this.e2 = e2;
                    this.e3 = e3;
                    this.e0 = e0;
                    this.field = field;
                }
            }
        } else throw new Error('invalid overload');
    }

    public outer(that: Vector3dHomogeneous): Bivector3dHomogeneous {
        const e12: AlgebraicNumber = this.e1['times$com_vzome_core_algebra_AlgebraicNumber'](that.e2)['minus$com_vzome_core_algebra_AlgebraicNumber'](this.e2['times$com_vzome_core_algebra_AlgebraicNumber'](that.e1));
        const e23: AlgebraicNumber = this.e2['times$com_vzome_core_algebra_AlgebraicNumber'](that.e3)['minus$com_vzome_core_algebra_AlgebraicNumber'](this.e3['times$com_vzome_core_algebra_AlgebraicNumber'](that.e2));
        const e31: AlgebraicNumber = this.e3['times$com_vzome_core_algebra_AlgebraicNumber'](that.e1)['minus$com_vzome_core_algebra_AlgebraicNumber'](this.e1['times$com_vzome_core_algebra_AlgebraicNumber'](that.e3));
        const e10: AlgebraicNumber = this.e1['times$com_vzome_core_algebra_AlgebraicNumber'](that.e0)['minus$com_vzome_core_algebra_AlgebraicNumber'](this.e0['times$com_vzome_core_algebra_AlgebraicNumber'](that.e1));
        const e20: AlgebraicNumber = this.e2['times$com_vzome_core_algebra_AlgebraicNumber'](that.e0)['minus$com_vzome_core_algebra_AlgebraicNumber'](this.e0['times$com_vzome_core_algebra_AlgebraicNumber'](that.e2));
        const e30: AlgebraicNumber = this.e3['times$com_vzome_core_algebra_AlgebraicNumber'](that.e0)['minus$com_vzome_core_algebra_AlgebraicNumber'](this.e0['times$com_vzome_core_algebra_AlgebraicNumber'](that.e3));
        return new Bivector3dHomogeneous(e12, e23, e31, e10, e20, e30, this.field);
    }

    public getVector(): AlgebraicVector {
        return new AlgebraicVector(this.e1.dividedBy(this.e0), this.e2.dividedBy(this.e0), this.e3.dividedBy(this.e0));
    }

    public dot(v: Bivector3dHomogeneous): Vector3dHomogeneous {
        const e1: AlgebraicNumber = this.e3['times$com_vzome_core_algebra_AlgebraicNumber'](v.e31)['minus$com_vzome_core_algebra_AlgebraicNumber'](this.e2['times$com_vzome_core_algebra_AlgebraicNumber'](v.e12))['minus$com_vzome_core_algebra_AlgebraicNumber'](this.e0['times$com_vzome_core_algebra_AlgebraicNumber'](v.e10));
        const e2: AlgebraicNumber = this.e1['times$com_vzome_core_algebra_AlgebraicNumber'](v.e12)['minus$com_vzome_core_algebra_AlgebraicNumber'](this.e3['times$com_vzome_core_algebra_AlgebraicNumber'](v.e23))['minus$com_vzome_core_algebra_AlgebraicNumber'](this.e0['times$com_vzome_core_algebra_AlgebraicNumber'](v.e20));
        const e3: AlgebraicNumber = this.e2['times$com_vzome_core_algebra_AlgebraicNumber'](v.e23)['minus$com_vzome_core_algebra_AlgebraicNumber'](this.e1['times$com_vzome_core_algebra_AlgebraicNumber'](v.e31))['minus$com_vzome_core_algebra_AlgebraicNumber'](this.e0['times$com_vzome_core_algebra_AlgebraicNumber'](v.e30));
        const e0: AlgebraicNumber = this.e1['times$com_vzome_core_algebra_AlgebraicNumber'](v.e10)['plus$com_vzome_core_algebra_AlgebraicNumber'](this.e2['times$com_vzome_core_algebra_AlgebraicNumber'](v.e20))['plus$com_vzome_core_algebra_AlgebraicNumber'](this.e3['times$com_vzome_core_algebra_AlgebraicNumber'](v.e30));
        return new Vector3dHomogeneous(e1, e2, e3, e0, this.field);
    }

    public exists(): boolean {
        return !this.e0.isZero();
    }
}
Vector3dHomogeneous["__class"] = "com.vzome.core.algebra.Vector3dHomogeneous";
