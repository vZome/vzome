/* Generated from Java with JSweet 3.2.0-SNAPSHOT - http://www.jsweet.org */
namespace com.vzome.core.construction {
    /**
     * @param prototype
     * @param {com.vzome.core.construction.Plane} mirror
     * @class
     * @extends com.vzome.core.construction.Transformation
     * @author Scott Vorthmann
     */
    export class PlaneReflection extends com.vzome.core.construction.Transformation {
        /*private*/ mMirror: com.vzome.core.construction.Plane;

        /*private*/ mNormal: com.vzome.core.algebra.AlgebraicVector;

        /*private*/ mBase: com.vzome.core.algebra.AlgebraicVector;

        /*private*/ mNormDotReciprocal: com.vzome.core.algebra.AlgebraicNumber;

        public constructor(mirror: com.vzome.core.construction.Plane) {
            super(mirror.field);
            if (this.mMirror === undefined) { this.mMirror = null; }
            if (this.mNormal === undefined) { this.mNormal = null; }
            if (this.mBase === undefined) { this.mBase = null; }
            if (this.mNormDotReciprocal === undefined) { this.mNormDotReciprocal = null; }
            this.mMirror = mirror;
            this.mNormal = mirror.getNormal();
            this.mBase = mirror.getBase();
            this.mNormDotReciprocal = this.mNormal.dot(this.mNormal).reciprocal();
            this.mapParamsToState();
        }

        /**
         * 
         * @return {boolean}
         */
        mapParamsToState(): boolean {
            if (this.mMirror.isImpossible())this.setStateVariables(null, null, true);
            const loc: com.vzome.core.algebra.AlgebraicVector = this.mMirror.getBase();
            return this.setStateVariables(null, loc, false);
        }

        public transform$com_vzome_core_algebra_AlgebraicVector(arg: com.vzome.core.algebra.AlgebraicVector): com.vzome.core.algebra.AlgebraicVector {
            arg = arg.minus(this.mBase);
            let xy: com.vzome.core.algebra.AlgebraicNumber = arg.dot(this.mNormal);
            xy = xy['times$com_vzome_core_algebra_AlgebraicNumber'](this.field['createRational$long'](2));
            arg = arg.minus(this.mNormal.scale(xy['times$com_vzome_core_algebra_AlgebraicNumber'](this.mNormDotReciprocal)));
            return arg.plus(this.mBase);
        }

        /**
         * 
         * @param {com.vzome.core.algebra.AlgebraicVector} arg
         * @return {com.vzome.core.algebra.AlgebraicVector}
         */
        public transform(arg?: any): any {
            if (((arg != null && arg instanceof <any>com.vzome.core.algebra.AlgebraicVector) || arg === null)) {
                return <any>this.transform$com_vzome_core_algebra_AlgebraicVector(arg);
            } else if (((arg != null && arg instanceof <any>com.vzome.core.construction.Construction) || arg === null)) {
                return <any>this.transform$com_vzome_core_construction_Construction(arg);
            } else throw new Error('invalid overload');
        }
    }
    PlaneReflection["__class"] = "com.vzome.core.construction.PlaneReflection";

}

