/* Generated from Java with JSweet 3.2.0-SNAPSHOT - http://www.jsweet.org */
namespace com.vzome.core.math {
    export class Line {
        /*private*/ origin: com.vzome.core.math.RealVector;

        /*private*/ direction: com.vzome.core.math.RealVector;

        public constructor(origin: com.vzome.core.math.RealVector, direction: com.vzome.core.math.RealVector) {
            if (this.origin === undefined) { this.origin = null; }
            if (this.direction === undefined) { this.direction = null; }
            this.direction = direction;
            this.origin = origin;
        }

        public getOrigin(): com.vzome.core.math.RealVector {
            return this.origin;
        }

        public getDirection(): com.vzome.core.math.RealVector {
            return this.direction;
        }
    }
    Line["__class"] = "com.vzome.core.math.Line";

}

