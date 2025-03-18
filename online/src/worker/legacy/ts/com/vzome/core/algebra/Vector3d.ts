/* Generated from Java with JSweet 3.2.0-SNAPSHOT - http://www.jsweet.org */
namespace com.vzome.core.algebra {
    export class Vector3d {
        a: com.vzome.core.algebra.AlgebraicNumber;

        b: com.vzome.core.algebra.AlgebraicNumber;

        c: com.vzome.core.algebra.AlgebraicNumber;

        public constructor(a?: any, b?: any, c?: any) {
            if (((a != null && (a.constructor != null && a.constructor["__interfaces"] != null && a.constructor["__interfaces"].indexOf("com.vzome.core.algebra.AlgebraicNumber") >= 0)) || a === null) && ((b != null && (b.constructor != null && b.constructor["__interfaces"] != null && b.constructor["__interfaces"].indexOf("com.vzome.core.algebra.AlgebraicNumber") >= 0)) || b === null) && ((c != null && (c.constructor != null && c.constructor["__interfaces"] != null && c.constructor["__interfaces"].indexOf("com.vzome.core.algebra.AlgebraicNumber") >= 0)) || c === null)) {
                let __args = arguments;
                if (this.a === undefined) { this.a = null; } 
                if (this.b === undefined) { this.b = null; } 
                if (this.c === undefined) { this.c = null; } 
                this.a = a;
                this.b = b;
                this.c = c;
            } else if (((a != null && a instanceof <any>com.vzome.core.algebra.AlgebraicVector) || a === null) && b === undefined && c === undefined) {
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

        public outer(that: Vector3d): com.vzome.core.algebra.Bivector3d {
            const a: com.vzome.core.algebra.AlgebraicNumber = this.a['times$com_vzome_core_algebra_AlgebraicNumber'](that.b)['minus$com_vzome_core_algebra_AlgebraicNumber'](this.b['times$com_vzome_core_algebra_AlgebraicNumber'](that.a));
            const b: com.vzome.core.algebra.AlgebraicNumber = this.b['times$com_vzome_core_algebra_AlgebraicNumber'](that.c)['minus$com_vzome_core_algebra_AlgebraicNumber'](this.c['times$com_vzome_core_algebra_AlgebraicNumber'](that.b));
            const c: com.vzome.core.algebra.AlgebraicNumber = this.c['times$com_vzome_core_algebra_AlgebraicNumber'](that.a)['minus$com_vzome_core_algebra_AlgebraicNumber'](this.a['times$com_vzome_core_algebra_AlgebraicNumber'](that.c));
            return new com.vzome.core.algebra.Bivector3d(a, b, c);
        }
    }
    Vector3d["__class"] = "com.vzome.core.algebra.Vector3d";

}

