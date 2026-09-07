import { AlgebraicMatrix } from "../algebra/AlgebraicMatrix.js";
import { AlgebraicNumber } from "../algebra/AlgebraicNumber.js";
import { AlgebraicVector } from "../algebra/AlgebraicVector.js";
import { Point } from "./Point.js";
import { Segment } from "./Segment.js";
import { Transformation } from "./Transformation.js";
import { Axis } from "../math/symmetry/Axis.js";
import { Direction } from "../math/symmetry/Direction.js";
import { Symmetry } from "../math/symmetry/Symmetry.js";

export class Scaling extends Transformation {
    /*private*/ s1: Segment;

    /*private*/ s2: Segment;

    /*private*/ center: Point;

    /*private*/ symmetry: Symmetry;

    public constructor(s1: Segment, s2: Segment, center: Point, symmetry: Symmetry) {
        super(s1.field);
        if (this.s1 === undefined) { this.s1 = null; }
        if (this.s2 === undefined) { this.s2 = null; }
        if (this.center === undefined) { this.center = null; }
        if (this.symmetry === undefined) { this.symmetry = null; }
        this.mOffset = this.field.projectTo3d(center.getLocation(), true);
        this.s1 = s1;
        this.s2 = s2;
        this.center = center;
        this.symmetry = symmetry;
        this.mapParamsToState();
    }

    /**
     * 
     * @return {boolean}
     */
    mapParamsToState(): boolean {
        const zone1: Axis = this.symmetry['getAxis$com_vzome_core_algebra_AlgebraicVector'](this.s1.getOffset());
        const zone2: Axis = this.symmetry['getAxis$com_vzome_core_algebra_AlgebraicVector'](this.s2.getOffset());
        const orbit: Direction = zone1.getDirection();
        if (orbit !== zone2.getDirection())return this.setStateVariables(null, null, true);
        const len1: AlgebraicNumber = zone1.getLength(this.s1.getOffset());
        const len2: AlgebraicNumber = zone2.getLength(this.s2.getOffset());
        const scale: AlgebraicNumber = len2.dividedBy(len1);
        const transform: AlgebraicMatrix = new AlgebraicMatrix(this.field.basisVector(3, AlgebraicVector.X).scale(scale), this.field.basisVector(3, AlgebraicVector.Y).scale(scale), this.field.basisVector(3, AlgebraicVector.Z).scale(scale));
        return this.setStateVariables(transform, this.center.getLocation(), false);
    }
}
Scaling["__class"] = "com.vzome.core.construction.Scaling";
