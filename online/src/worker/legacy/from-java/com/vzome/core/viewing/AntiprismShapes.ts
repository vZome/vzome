import { AlgebraicMatrix } from "../algebra/AlgebraicMatrix.js";
import { AlgebraicNumber } from "../algebra/AlgebraicNumber.js";
import { AlgebraicVector } from "../algebra/AlgebraicVector.js";
import { PolygonField } from "../algebra/PolygonField.js";
import { Polyhedron } from "../math/Polyhedron.js";
import { AntiprismSymmetry } from "../math/symmetry/AntiprismSymmetry.js";
import { AbstractShapes } from "./AbstractShapes.js";

/**
 * @author David Hall
 * @param {string} pkgName
 * @param {string} name
 * @param {AntiprismSymmetry} symm
 * @class
 * @extends AbstractShapes
 */
export class AntiprismShapes extends AbstractShapes {
    public constructor(pkgName: string, name: string, symm: AntiprismSymmetry) {
        super(pkgName, name, null, symm);
    }

    /**
     * 
     * @return {AntiprismSymmetry}
     */
    public getSymmetry(): AntiprismSymmetry {
        return <AntiprismSymmetry><any>super.getSymmetry();
    }

    /**
     * 
     * @param {string} pkgName
     * @return {Polyhedron}
     */
    buildConnectorShape(pkgName: string): Polyhedron {
        const symm: AntiprismSymmetry = this.getSymmetry();
        const field: PolygonField = symm.getField();
        const nSides: number = field.polygonSides();
        const antiprism: Polyhedron = new Polyhedron(field);
        const topX: AlgebraicNumber = field.one();
        const topY: AlgebraicNumber = field.zero();
        const maxTerm: AlgebraicNumber = field.getUnitDiagonal(field.diagonalCount() - 1);
        const botX: AlgebraicNumber = field.getUnitDiagonal(field.diagonalCount() - 2).dividedBy(maxTerm);
        const botY: AlgebraicNumber = maxTerm.reciprocal();
        const halfHeight: AlgebraicNumber = (pkgName === "thin") ? field.getUnitDiagonal(field.diagonalCount() - 1).reciprocal() : field.one();
        const rotationMatrix: AlgebraicMatrix = symm.getRotationMatrix();
        let vTop: AlgebraicVector = new AlgebraicVector(topX, topY, halfHeight);
        let vBot: AlgebraicVector = new AlgebraicVector(botX, botY, halfHeight.negate());
        for(let i: number = 0; i < nSides; i++) {{
            antiprism.addVertex(vTop);
            antiprism.addVertex(vBot);
            vTop = rotationMatrix.timesColumn(vTop);
            vBot = rotationMatrix.timesColumn(vBot);
        };}
        const topFace: Polyhedron.Face = antiprism.newFace();
        const botFace: Polyhedron.Face = antiprism.newFace();
        for(let i: number = 0; i < nSides * 2; i += 2) {{
            topFace.add(i);
        };}
        for(let i: number = nSides * 2 - 1; i >= 0; i -= 2) {{
            botFace.add(i);
        };}
        antiprism.addFace(topFace);
        antiprism.addFace(botFace);
        const nVertices: number = nSides * 2;
        for(let i: number = 0; i < nSides * 2; i += 2) {{
            const face: Polyhedron.Face = antiprism.newFace();
            face.add(i);
            face.add((i + 1) % nVertices);
            face.add((i + 2) % nVertices);
            antiprism.addFace(face);
        };}
        for(let i: number = 1; i < nSides * 2; i += 2) {{
            const face: Polyhedron.Face = antiprism.newFace();
            face.add(i);
            face.add((i + 2) % nVertices);
            face.add((i + 1) % nVertices);
            antiprism.addFace(face);
        };}
        return antiprism;
    }
}
AntiprismShapes["__class"] = "com.vzome.core.viewing.AntiprismShapes";
AntiprismShapes["__interfaces"] = ["com.vzome.core.editor.api.Shapes"];
