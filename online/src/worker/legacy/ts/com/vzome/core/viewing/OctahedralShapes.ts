/* Generated from Java with JSweet 3.2.0-SNAPSHOT - http://www.jsweet.org */
namespace com.vzome.core.viewing {
    export class OctahedralShapes extends com.vzome.core.viewing.AbstractShapes {
        public constructor(pkgName: string, name: string, symm: com.vzome.core.math.symmetry.Symmetry) {
            super(pkgName, name, null, symm);
        }

        /**
         * 
         * @param {string} pkgName
         * @return {com.vzome.core.math.Polyhedron}
         */
        buildConnectorShape(pkgName: string): com.vzome.core.math.Polyhedron {
            const field: com.vzome.core.algebra.AlgebraicField = this.mSymmetry.getField();
            const cube: com.vzome.core.math.Polyhedron = new com.vzome.core.math.Polyhedron(field);
            let scale: com.vzome.core.algebra.AlgebraicNumber = field['createPower$int'](-2);
            scale = field['createRational$long'](2)['times$com_vzome_core_algebra_AlgebraicNumber'](scale);
            const x: com.vzome.core.algebra.AlgebraicVector = field.basisVector(3, com.vzome.core.algebra.AlgebraicVector.X);
            const y: com.vzome.core.algebra.AlgebraicVector = field.basisVector(3, com.vzome.core.algebra.AlgebraicVector.Y);
            const z: com.vzome.core.algebra.AlgebraicVector = field.basisVector(3, com.vzome.core.algebra.AlgebraicVector.Z);
            cube.addVertex(x.scale(scale));
            cube.addVertex(x.negate().scale(scale));
            cube.addVertex(y.scale(scale));
            cube.addVertex(y.negate().scale(scale));
            cube.addVertex(z.scale(scale));
            cube.addVertex(z.negate().scale(scale));
            let face: com.vzome.core.math.Polyhedron.Face = cube.newFace();
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


}

