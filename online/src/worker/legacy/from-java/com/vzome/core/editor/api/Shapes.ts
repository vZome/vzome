import { java, javaemul } from "../../../../../../candies/j4ts-2.1.0-SNAPSHOT/bundle.js";
import { AlgebraicNumber } from "../../algebra/AlgebraicNumber.js";
import { AlgebraicVector } from "../../algebra/AlgebraicVector.js";
import { Color } from "../../construction/Color.js";
import { Polyhedron } from "../../math/Polyhedron.js";
import { Axis } from "../../math/symmetry/Axis.js";
import { Direction } from "../../math/symmetry/Direction.js";
import { Symmetry } from "../../math/symmetry/Symmetry.js";

export interface Shapes {
    getName(): string;

    getAlias(): string;

    getConnectorShape(): Polyhedron;

    getStrutShape(dir: Direction, length: AlgebraicNumber): Polyhedron;

    getPanelShape(vertexCount: number, quadrea: AlgebraicNumber, zone: Axis, vertices: java.lang.Iterable<AlgebraicVector>, oneSidedPanels: boolean): Polyhedron;

    getSymmetry(): Symmetry;

    getPackage(): string;

    getColor(dir: Direction): Color;

    hasColors(): boolean;

    getCmScaling(): number;
}
