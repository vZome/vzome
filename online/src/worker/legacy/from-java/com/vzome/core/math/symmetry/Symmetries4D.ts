import { AlgebraicNumber } from "../../algebra/AlgebraicNumber.js";
import { QuaternionicSymmetry } from "./QuaternionicSymmetry.js";
import { WythoffConstruction } from "./WythoffConstruction.js";

export interface Symmetries4D {
    constructPolytope(groupName: string, index: number, edgesToRender: number, edgeScales: AlgebraicNumber[], listener: WythoffConstruction.Listener);

    getQuaternionSymmetry(name: string): QuaternionicSymmetry;
}
