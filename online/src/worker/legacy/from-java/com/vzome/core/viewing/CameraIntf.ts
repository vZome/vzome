import { RealVector } from "../math/RealVector.js";

export interface CameraIntf {
    isPerspective(): boolean;

    getFieldOfView(): number;

    getViewDistance(): number;

    getMagnification(): number;

    getLookAtPointRV(): RealVector;

    getLookDirectionRV(): RealVector;

    getUpDirectionRV(): RealVector;

    mapViewToWorld(rv: RealVector): RealVector;
}
