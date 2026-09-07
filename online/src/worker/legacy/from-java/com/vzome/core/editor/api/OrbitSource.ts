import { AlgebraicVector } from "../../algebra/AlgebraicVector.js";
import { Color } from "../../construction/Color.js";
import { Shapes } from "./Shapes.js";
import { Axis } from "../../math/symmetry/Axis.js";
import { Direction } from "../../math/symmetry/Direction.js";
import { OrbitSet } from "../../math/symmetry/OrbitSet.js";
import { Symmetry } from "../../math/symmetry/Symmetry.js";

export interface OrbitSource {
    getSymmetry(): Symmetry;

    getAxis(vector: AlgebraicVector): Axis;

    getColor(orbit: Direction): Color;

    getVectorColor(vector: AlgebraicVector): Color;

    getOrbits(): OrbitSet;

    getShapes(): Shapes;

    getName(): string;

    getZone(orbit: string, orientation: number): Axis;

    getEmbedding(): number[];

    getOrientations(rowMajor?: any): number[][];
}
