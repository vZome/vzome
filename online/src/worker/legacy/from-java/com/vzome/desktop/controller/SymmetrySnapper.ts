import { RealVector } from "../../core/math/RealVector.js";
import { Axis } from "../../core/math/symmetry/Axis.js";
import { Embedding } from "../../core/math/symmetry/Embedding.js";
import { OrbitSet } from "../../core/math/symmetry/OrbitSet.js";
import { OrbitSnapper } from "./OrbitSnapper.js";

export class SymmetrySnapper implements OrbitSnapper {
    /*private*/ orbitSet: OrbitSet;

    /*private*/ embedding: Embedding;

    public constructor(orbitSet: OrbitSet) {
        if (this.orbitSet === undefined) { this.orbitSet = null; }
        if (this.embedding === undefined) { this.embedding = null; }
        this.orbitSet = orbitSet;
        this.embedding = orbitSet.getSymmetry();
    }

    /**
     * 
     * @param {RealVector} zIn
     * @return {RealVector}
     */
    public snapZ(zIn: RealVector): RealVector {
        const lookZone: Axis = this.orbitSet.getAxis(zIn);
        if (lookZone == null)return zIn;
        return this.embedding.embedInR3(lookZone.normal());
    }

    /**
     * 
     * @param {RealVector} zOut
     * @param {RealVector} yIn
     * @return {RealVector}
     */
    public snapY(zOut: RealVector, yIn: RealVector): RealVector {
        const upZone: Axis = this.orbitSet.getAxis(yIn);
        if (upZone == null)return yIn;
        yIn = this.embedding.embedInR3(upZone.normal());
        const left: RealVector = zOut.cross(yIn);
        return left.cross(zOut);
    }
}
SymmetrySnapper["__class"] = "com.vzome.desktop.controller.SymmetrySnapper";
SymmetrySnapper["__interfaces"] = ["com.vzome.desktop.controller.OrbitSnapper"];
