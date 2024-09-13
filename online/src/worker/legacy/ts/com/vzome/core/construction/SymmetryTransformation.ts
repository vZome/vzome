/* Generated from Java with JSweet 3.2.0-SNAPSHOT - http://www.jsweet.org */
namespace com.vzome.core.construction {
    /**
     * @param prototype
     * @param {*} symm
     * @param {number} orientation
     * @param {com.vzome.core.construction.Point} center
     * @class
     * @extends com.vzome.core.construction.Transformation
     * @author Scott Vorthmann
     */
    export class SymmetryTransformation extends com.vzome.core.construction.Transformation {
        /*private*/ mCenter: com.vzome.core.construction.Point;

        mSymmetry: com.vzome.core.math.symmetry.Symmetry;

        mOrientation: number;

        public constructor(symm: com.vzome.core.math.symmetry.Symmetry, orientation: number, center: com.vzome.core.construction.Point) {
            super(center.field);
            if (this.mCenter === undefined) { this.mCenter = null; }
            if (this.mSymmetry === undefined) { this.mSymmetry = null; }
            if (this.mOrientation === undefined) { this.mOrientation = 0; }
            this.mSymmetry = symm;
            this.mOrientation = orientation;
            this.mCenter = center;
            this.mapParamsToState();
        }

        /**
         * 
         * @return {boolean}
         */
        mapParamsToState(): boolean {
            if (this.mCenter.isImpossible())return this.setStateVariables(null, null, true);
            const loc: com.vzome.core.algebra.AlgebraicVector = this.mCenter.getLocation().projectTo3d(true);
            const matrix: com.vzome.core.algebra.AlgebraicMatrix = this.mSymmetry.getMatrix(this.mOrientation);
            return this.setStateVariables(matrix, loc, false);
        }
    }
    SymmetryTransformation["__class"] = "com.vzome.core.construction.SymmetryTransformation";

}

