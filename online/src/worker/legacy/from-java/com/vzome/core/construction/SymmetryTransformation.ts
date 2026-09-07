import { AlgebraicMatrix } from "../algebra/AlgebraicMatrix.js";
import { AlgebraicVector } from "../algebra/AlgebraicVector.js";
import { Point } from "./Point.js";
import { Transformation } from "./Transformation.js";
import { Symmetry } from "../math/symmetry/Symmetry.js";

/**
 * @param prototype
 * @param {*} symm
 * @param {number} orientation
 * @param {Point} center
 * @class
 * @extends Transformation
 * @author Scott Vorthmann
 */
export class SymmetryTransformation extends Transformation {
    /*private*/ mCenter: Point;

    mSymmetry: Symmetry;

    mOrientation: number;

    public constructor(symm: Symmetry, orientation: number, center: Point) {
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
        const loc: AlgebraicVector = this.mCenter.getLocation().projectTo3d(true);
        const matrix: AlgebraicMatrix = this.mSymmetry.getMatrix(this.mOrientation);
        return this.setStateVariables(matrix, loc, false);
    }
}
SymmetryTransformation["__class"] = "com.vzome.core.construction.SymmetryTransformation";
