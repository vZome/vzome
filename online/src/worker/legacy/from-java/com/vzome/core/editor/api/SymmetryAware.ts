import { OrbitSource } from "./OrbitSource.js";
import { Symmetries4D } from "../../math/symmetry/Symmetries4D.js";

export interface SymmetryAware {
    getSymmetrySystem(name?: any): OrbitSource;

    get4dSymmetries(): Symmetries4D;
}
