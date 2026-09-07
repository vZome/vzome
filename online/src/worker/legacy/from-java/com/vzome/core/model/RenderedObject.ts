import { AlgebraicMatrix } from "../algebra/AlgebraicMatrix.js";
import { AlgebraicVector } from "../algebra/AlgebraicVector.js";
import { Polyhedron } from "../math/Polyhedron.js";

export interface RenderedObject {
    getShape(): Polyhedron;

    getOrientation(): AlgebraicMatrix;

    getLocationAV(): AlgebraicVector;

    getSymmetryShapes(): string;

    resetAttributes(oneSidedPanels: boolean, colorPanels: boolean);
}
