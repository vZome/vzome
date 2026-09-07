import { AlgebraicNumber } from "./AlgebraicNumber.js";
import { AlgebraicVector } from "./AlgebraicVector.js";
import { Bivector3d } from "./Bivector3d.js";

export class Vector3d {
    a: AlgebraicNumber;

    b: AlgebraicNumber;

    c: AlgebraicNumber;

    public constructor(a?: any, b?: any, c?: any) {
        if (((a != null && (a.constructor != null && a.constructor["__interfaces"] != null && a.constructor["__interfaces"].indexOf("com.vzome.core.algebra.AlgebraicNumber") >= 0)) || a === null) && ((b != null && (b.constructor != null && b.constructor["__interfaces"] != null && b.constructor["__interfaces"].indexOf("com.vzome.core.algebra.AlgebraicNumber") >= 0)) || b === null) && ((c != null && (c.constructor != null && c.constructor["__interfaces"] != null && c.constructor["__interfaces"].indexOf("com.vzome.core.algebra.AlgebraicNumber") >= 0)) || c === null)) {
            let __args = arguments;
            if (this.a === undefined) { this.a = null; } 
            if (this.b === undefined) { this.b = null; } 
            if (this.c === undefined) { this.c = null; } 
            this.a = a;
            this.b = b;
            this.c = c;
        } else if (((a != null && a instanceof <any>AlgebraicVector) || a === null) && b === undefined && c === undefined) {
            let __args = arguments;
            let v: any = __args[0];
            if (this.a === undefined) { this.a = null; } 
            if (this.b === undefined) { this.b = null; } 
            if (this.c === undefined) { this.c = null; } 
            this.a = v.getComponent(0);
            this.b = v.getComponent(1);
            this.c = v.getComponent(2);
        } else throw new Error('invalid overload');
    }

    public outer(that: Vector3d): Bivector3d {
        const a: AlgebraicNumber = this.a['times$com_vzome_core_algebra_AlgebraicNumber'](that.b)['minus$com_vzome_core_algebra_AlgebraicNumber'](this.b['times$com_vzome_core_algebra_AlgebraicNumber'](that.a));
        const b: AlgebraicNumber = this.b['times$com_vzome_core_algebra_AlgebraicNumber'](that.c)['minus$com_vzome_core_algebra_AlgebraicNumber'](this.c['times$com_vzome_core_algebra_AlgebraicNumber'](that.b));
        const c: AlgebraicNumber = this.c['times$com_vzome_core_algebra_AlgebraicNumber'](that.a)['minus$com_vzome_core_algebra_AlgebraicNumber'](this.a['times$com_vzome_core_algebra_AlgebraicNumber'](that.c));
        return new Bivector3d(a, b, c);
    }
}
Vector3d["__class"] = "com.vzome.core.algebra.Vector3d";
