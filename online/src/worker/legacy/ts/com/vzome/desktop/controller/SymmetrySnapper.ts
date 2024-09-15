/* Generated from Java with JSweet 3.2.0-SNAPSHOT - http://www.jsweet.org */
namespace com.vzome.desktop.controller {
    export class SymmetrySnapper implements com.vzome.desktop.controller.OrbitSnapper {
        /*private*/ orbitSet: com.vzome.core.math.symmetry.OrbitSet;

        /*private*/ embedding: com.vzome.core.math.symmetry.Embedding;

        public constructor(orbitSet: com.vzome.core.math.symmetry.OrbitSet) {
            if (this.orbitSet === undefined) { this.orbitSet = null; }
            if (this.embedding === undefined) { this.embedding = null; }
            this.orbitSet = orbitSet;
            this.embedding = orbitSet.getSymmetry();
        }

        /**
         * 
         * @param {com.vzome.core.math.RealVector} zIn
         * @return {com.vzome.core.math.RealVector}
         */
        public snapZ(zIn: com.vzome.core.math.RealVector): com.vzome.core.math.RealVector {
            const lookZone: com.vzome.core.math.symmetry.Axis = this.orbitSet.getAxis(zIn);
            if (lookZone == null)return zIn;
            return this.embedding.embedInR3(lookZone.normal());
        }

        /**
         * 
         * @param {com.vzome.core.math.RealVector} zOut
         * @param {com.vzome.core.math.RealVector} yIn
         * @return {com.vzome.core.math.RealVector}
         */
        public snapY(zOut: com.vzome.core.math.RealVector, yIn: com.vzome.core.math.RealVector): com.vzome.core.math.RealVector {
            const upZone: com.vzome.core.math.symmetry.Axis = this.orbitSet.getAxis(yIn);
            if (upZone == null)return yIn;
            yIn = this.embedding.embedInR3(upZone.normal());
            const left: com.vzome.core.math.RealVector = zOut.cross(yIn);
            return left.cross(zOut);
        }
    }
    SymmetrySnapper["__class"] = "com.vzome.desktop.controller.SymmetrySnapper";
    SymmetrySnapper["__interfaces"] = ["com.vzome.desktop.controller.OrbitSnapper"];


}

