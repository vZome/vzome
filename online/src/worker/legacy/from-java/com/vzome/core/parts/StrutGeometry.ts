import { AlgebraicNumber } from "../algebra/AlgebraicNumber.js";
import { Polyhedron } from "../math/Polyhedron.js";

/**
 * @author Scott Vorthmann
 * @class
 */
export interface StrutGeometry {
    getStrutPolyhedron(length: AlgebraicNumber): Polyhedron;
}
