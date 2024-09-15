/* Generated from Java with JSweet 3.2.0-SNAPSHOT - http://www.jsweet.org */
namespace com.vzome.desktop.controller {
    export interface OrbitSnapper {
        snapZ(zIn: com.vzome.core.math.RealVector): com.vzome.core.math.RealVector;

        /**
         * Snap the Y-axis.
         * @param {com.vzome.core.math.RealVector} zOut already-snapped Z-axis
         * @param {com.vzome.core.math.RealVector} yIn
         * @return
         * @return {com.vzome.core.math.RealVector}
         */
        snapY(zOut: com.vzome.core.math.RealVector, yIn: com.vzome.core.math.RealVector): com.vzome.core.math.RealVector;
    }
}

