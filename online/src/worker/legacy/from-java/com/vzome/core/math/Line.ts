import { RealVector } from "./RealVector.js";

export class Line {
    /*private*/ origin: RealVector;

    /*private*/ direction: RealVector;

    public constructor(origin: RealVector, direction: RealVector) {
        if (this.origin === undefined) { this.origin = null; }
        if (this.direction === undefined) { this.direction = null; }
        this.direction = direction;
        this.origin = origin;
    }

    public getOrigin(): RealVector {
        return this.origin;
    }

    public getDirection(): RealVector {
        return this.direction;
    }
}
Line["__class"] = "com.vzome.core.math.Line";
