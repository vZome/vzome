import { AlgebraicVector } from "../algebra/AlgebraicVector.js";
import { Point } from "./Point.js";

/**
 * @param {AlgebraicVector} loc
 * @class
 * @extends Point
 * @author Scott Vorthmann
 */
export class FreePoint extends Point {
    public constructor(loc: AlgebraicVector) {
        super(loc.getField());
        this.setStateVariable(loc, false);
    }

    /**
     * 
     * @return {boolean}
     */
    mapParamsToState(): boolean {
        return true;
    }
}
FreePoint["__class"] = "com.vzome.core.construction.FreePoint";
