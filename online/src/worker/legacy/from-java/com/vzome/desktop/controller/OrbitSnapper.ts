import { RealVector } from "../../core/math/RealVector.js";

export interface OrbitSnapper {
    snapZ(zIn: RealVector): RealVector;

    /**
     * Snap the Y-axis.
     * @param {RealVector} zOut already-snapped Z-axis
     * @param {RealVector} yIn
     * @return
     * @return {RealVector}
     */
    snapY(zOut: RealVector, yIn: RealVector): RealVector;
}
