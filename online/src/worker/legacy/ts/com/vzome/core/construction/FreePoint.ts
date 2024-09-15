/* Generated from Java with JSweet 3.2.0-SNAPSHOT - http://www.jsweet.org */
namespace com.vzome.core.construction {
    /**
     * @param {com.vzome.core.algebra.AlgebraicVector} loc
     * @class
     * @extends com.vzome.core.construction.Point
     * @author Scott Vorthmann
     */
    export class FreePoint extends com.vzome.core.construction.Point {
        public constructor(loc: com.vzome.core.algebra.AlgebraicVector) {
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

}

