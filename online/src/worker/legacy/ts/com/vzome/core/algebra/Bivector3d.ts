/* Generated from Java with JSweet 3.2.0-SNAPSHOT - http://www.jsweet.org */
namespace com.vzome.core.algebra {
    export class Bivector3d {
        /*private*/ a: com.vzome.core.algebra.AlgebraicNumber;

        /*private*/ b: com.vzome.core.algebra.AlgebraicNumber;

        /*private*/ c: com.vzome.core.algebra.AlgebraicNumber;

        public constructor(a: com.vzome.core.algebra.AlgebraicNumber, b: com.vzome.core.algebra.AlgebraicNumber, c: com.vzome.core.algebra.AlgebraicNumber) {
            if (this.a === undefined) { this.a = null; }
            if (this.b === undefined) { this.b = null; }
            if (this.c === undefined) { this.c = null; }
            this.a = a;
            this.b = b;
            this.c = c;
        }

        /**
         * 
         * @return {number}
         */
        public hashCode(): number {
            const prime: number = 31;
            let result: number = 1;
            result = prime * result + ((this.a == null) ? 0 : /* hashCode */(<any>((o: any) => { if (o.hashCode) { return o.hashCode(); } else { return o.toString().split('').reduce((prevHash, currVal) => (((prevHash << 5) - prevHash) + currVal.charCodeAt(0))|0, 0); }})(this.a)));
            result = prime * result + ((this.b == null) ? 0 : /* hashCode */(<any>((o: any) => { if (o.hashCode) { return o.hashCode(); } else { return o.toString().split('').reduce((prevHash, currVal) => (((prevHash << 5) - prevHash) + currVal.charCodeAt(0))|0, 0); }})(this.b)));
            result = prime * result + ((this.c == null) ? 0 : /* hashCode */(<any>((o: any) => { if (o.hashCode) { return o.hashCode(); } else { return o.toString().split('').reduce((prevHash, currVal) => (((prevHash << 5) - prevHash) + currVal.charCodeAt(0))|0, 0); }})(this.c)));
            return result;
        }

        /**
         * 
         * @param {*} obj
         * @return {boolean}
         */
        public equals(obj: any): boolean {
            if (this === obj){
                return true;
            }
            if (obj == null){
                return false;
            }
            if ((<any>this.constructor) !== (<any>obj.constructor)){
                return false;
            }
            const other: Bivector3d = <Bivector3d>obj;
            if (this.a == null){
                if (other.a != null){
                    return false;
                }
            } else if (!/* equals */(<any>((o1: any, o2: any) => { if (o1 && o1.equals) { return o1.equals(o2); } else { return o1 === o2; } })(this.a,other.a))){
                return false;
            }
            if (this.b == null){
                if (other.b != null){
                    return false;
                }
            } else if (!/* equals */(<any>((o1: any, o2: any) => { if (o1 && o1.equals) { return o1.equals(o2); } else { return o1 === o2; } })(this.b,other.b))){
                return false;
            }
            if (this.c == null){
                if (other.c != null){
                    return false;
                }
            } else if (!/* equals */(<any>((o1: any, o2: any) => { if (o1 && o1.equals) { return o1.equals(o2); } else { return o1 === o2; } })(this.c,other.c))){
                return false;
            }
            return true;
        }

        /**
         * The pseudoscalar is implied in the result.
         * @param {com.vzome.core.algebra.Vector3d} v
         * @return
         * @return {*}
         */
        public outer(v: com.vzome.core.algebra.Vector3d): com.vzome.core.algebra.AlgebraicNumber {
            const a: com.vzome.core.algebra.AlgebraicNumber = this.a['times$com_vzome_core_algebra_AlgebraicNumber'](v.c);
            const b: com.vzome.core.algebra.AlgebraicNumber = this.b['times$com_vzome_core_algebra_AlgebraicNumber'](v.a);
            const c: com.vzome.core.algebra.AlgebraicNumber = this.c['times$com_vzome_core_algebra_AlgebraicNumber'](v.b);
            return a['plus$com_vzome_core_algebra_AlgebraicNumber'](b)['plus$com_vzome_core_algebra_AlgebraicNumber'](c);
        }
    }
    Bivector3d["__class"] = "com.vzome.core.algebra.Bivector3d";

}

