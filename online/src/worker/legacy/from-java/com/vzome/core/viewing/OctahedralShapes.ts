import { AlgebraicField } from "../algebra/AlgebraicField.js";
import { AlgebraicNumber } from "../algebra/AlgebraicNumber.js";
import { AlgebraicVector } from "../algebra/AlgebraicVector.js";
import { Polyhedron } from "../math/Polyhedron.js";
import { Symmetry } from "../math/symmetry/Symmetry.js";
import { AbstractShapes } from "./AbstractShapes.js";

export class OctahedralShapes extends AbstractShapes {
    public constructor(pkgName: string, name: string, symm: Symmetry) {
        super(pkgName, name, null, symm);
    }

    /**
     * 
     * @param {string} pkgName
     * @return {Polyhedron}
     */
    buildConnectorShape(pkgName: string): Polyhedron {
        const field: AlgebraicField = this.mSymmetry.getField();
        const cube: Polyhedron = new Polyhedron(field);
        let scale: AlgebraicNumber = field['createPower$int'](-2);
        scale = field['createRational$long'](2)['times$com_vzome_core_algebra_AlgebraicNumber'](scale);
        const x: AlgebraicVector = field.basisVector(3, AlgebraicVector.X);
        const y: AlgebraicVector = field.basisVector(3, AlgebraicVector.Y);
        const z: AlgebraicVector = field.basisVector(3, AlgebraicVector.Z);
        cube.addVertex(x.scale(scale));
        cube.addVertex(x.negate().scale(scale));
        cube.addVertex(y.scale(scale));
        cube.addVertex(y.negate().scale(scale));
        cube.addVertex(z.scale(scale));
        cube.addVertex(z.negate().scale(scale));
        let face: Polyhedron.Face = cube.newFace();
        face.add(0);
        face.add(2);
        face.add(4);
        cube.addFace(face);
        face = cube.newFace();
        face.add(0);
        face.add(5);
        face.add(2);
        cube.addFace(face);
        face = cube.newFace();
        face.add(0);
        face.add(3);
        face.add(5);
        cube.addFace(face);
        face = cube.newFace();
        face.add(0);
        face.add(4);
        face.add(3);
        cube.addFace(face);
        face = cube.newFace();
        face.add(1);
        face.add(4);
        face.add(2);
        cube.addFace(face);
        face = cube.newFace();
        face.add(1);
        face.add(2);
        face.add(5);
        cube.addFace(face);
        face = cube.newFace();
        face.add(1);
        face.add(5);
        face.add(3);
        cube.addFace(face);
        face = cube.newFace();
        face.add(1);
        face.add(3);
        face.add(4);
        cube.addFace(face);
        return cube;
    }
}
OctahedralShapes["__class"] = "com.vzome.core.viewing.OctahedralShapes";
OctahedralShapes["__interfaces"] = ["com.vzome.core.editor.api.Shapes"];
