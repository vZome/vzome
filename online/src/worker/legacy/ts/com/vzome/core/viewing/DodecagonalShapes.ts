/* Generated from Java with JSweet 3.2.0-SNAPSHOT - http://www.jsweet.org */
namespace com.vzome.core.viewing {
    export class DodecagonalShapes extends com.vzome.core.viewing.AbstractShapes {
        public constructor(pkgName: string, name: string, alias: string, symm: com.vzome.core.math.symmetry.Symmetry) {
            super(pkgName, name, alias, symm);
            if (this.V0 === undefined) { this.V0 = null; }
            if (this.V1 === undefined) { this.V1 = null; }
            if (this.V2 === undefined) { this.V2 = null; }
            if (this.V3 === undefined) { this.V3 = null; }
            if (this.V4 === undefined) { this.V4 = null; }
            if (this.V5 === undefined) { this.V5 = null; }
            if (this.TWO === undefined) { this.TWO = null; }
            if (this.GREEN === undefined) { this.GREEN = null; }
            if (this.V6 === undefined) { this.V6 = null; }
            if (this.V7 === undefined) { this.V7 = null; }
            const field: com.vzome.core.algebra.AlgebraicField = symm.getField();
            this.V0 = field.createVector([[1, 1, 0, 1], [0, 1, 0, 1], [0, 1, 0, 1]]);
            this.V1 = field.createVector([[1, 2, 0, 1], [0, 1, 1, 2], [0, 1, 0, 1]]);
            this.V2 = field.createVector([[-1, 2, 0, 1], [0, 1, 1, 2], [0, 1, 0, 1]]);
            this.V3 = field.createVector([[-1, 1, 0, 1], [0, 1, 0, 1], [0, 1, 0, 1]]);
            this.V4 = field.createVector([[-1, 2, 0, 1], [0, 1, -1, 2], [0, 1, 0, 1]]);
            this.V5 = field.createVector([[1, 2, 0, 1], [0, 1, -1, 2], [0, 1, 0, 1]]);
            this.TWO = field.createVector([[2, 1, 0, 1], [0, 1, 0, 1], [0, 1, 0, 1]]);
            this.GREEN = field.createVector([[1, 1, 1, 2], [1, 2, 0, 1], [0, 1, 0, 1]]);
            this.V6 = field.createVector([[1, 1, 0, 1], [0, 1, 1, 3], [0, 1, 0, 1]]);
            this.V7 = field.createVector([[0, 1, 2, 3], [0, 1, 0, 1], [0, 1, 0, 1]]);
        }

        /*private*/ V0: com.vzome.core.algebra.AlgebraicVector;

        /*private*/ V1: com.vzome.core.algebra.AlgebraicVector;

        /*private*/ V2: com.vzome.core.algebra.AlgebraicVector;

        /*private*/ V3: com.vzome.core.algebra.AlgebraicVector;

        /*private*/ V4: com.vzome.core.algebra.AlgebraicVector;

        /*private*/ V5: com.vzome.core.algebra.AlgebraicVector;

        /*private*/ TWO: com.vzome.core.algebra.AlgebraicVector;

        /*private*/ GREEN: com.vzome.core.algebra.AlgebraicVector;

        /*private*/ V6: com.vzome.core.algebra.AlgebraicVector;

        /*private*/ V7: com.vzome.core.algebra.AlgebraicVector;

        /**
         * 
         * @param {string} pkgName
         * @return {com.vzome.core.math.Polyhedron}
         */
        buildConnectorShape(pkgName: string): com.vzome.core.math.Polyhedron {
            const hex: com.vzome.core.math.Polyhedron = new com.vzome.core.math.Polyhedron(this.mSymmetry.getField());
            hex.addVertex(this.V0);
            hex.addVertex(this.V1);
            hex.addVertex(this.V2);
            hex.addVertex(this.V3);
            hex.addVertex(this.V4);
            hex.addVertex(this.V5);
            let face: com.vzome.core.math.Polyhedron.Face = hex.newFace();
            face.add(0);
            face.add(1);
            face.add(2);
            face.add(3);
            face.add(4);
            face.add(5);
            hex.addFace(face);
            face = hex.newFace();
            face.add(5);
            face.add(4);
            face.add(3);
            face.add(2);
            face.add(1);
            face.add(0);
            hex.addFace(face);
            return hex;
        }

        /**
         * 
         * @param {com.vzome.core.math.symmetry.Direction} dir
         * @return {*}
         */
        createStrutGeometry(dir: com.vzome.core.math.symmetry.Direction): com.vzome.core.parts.StrutGeometry {
            if (dir.getName() === ("blue"))return new DodecagonalShapes.BlueStrutGeometry(this); else if (dir.getName() === ("green"))return new DodecagonalShapes.GreenStrutGeometry(this); else return super.createStrutGeometry(dir);
        }
    }
    DodecagonalShapes["__class"] = "com.vzome.core.viewing.DodecagonalShapes";
    DodecagonalShapes["__interfaces"] = ["com.vzome.core.editor.api.Shapes"];



    export namespace DodecagonalShapes {

        export class BlueStrutGeometry implements com.vzome.core.parts.StrutGeometry {
            public __parent: any;
            /**
             * 
             * @param {*} length
             * @return {com.vzome.core.math.Polyhedron}
             */
            public getStrutPolyhedron(length: com.vzome.core.algebra.AlgebraicNumber): com.vzome.core.math.Polyhedron {
                const field: com.vzome.core.algebra.AlgebraicField = this.__parent.mSymmetry.getField();
                const hex: com.vzome.core.math.Polyhedron = new com.vzome.core.math.Polyhedron(field);
                const strut: com.vzome.core.algebra.AlgebraicVector = field.basisVector(3, 0).scale(length);
                const THIRD: com.vzome.core.algebra.AlgebraicNumber = field['createRational$long$long'](1, 3);
                hex.addVertex(strut.plus(this.__parent.V5.scale(THIRD).minus(this.__parent.TWO)));
                hex.addVertex(strut.plus(this.__parent.V0.scale(THIRD).minus(this.__parent.TWO)));
                hex.addVertex(strut.plus(this.__parent.V1.scale(THIRD).minus(this.__parent.TWO)));
                hex.addVertex(this.__parent.V2.scale(THIRD).plus(this.__parent.TWO));
                hex.addVertex(this.__parent.V3.scale(THIRD).plus(this.__parent.TWO));
                hex.addVertex(this.__parent.V4.scale(THIRD).plus(this.__parent.TWO));
                let face: com.vzome.core.math.Polyhedron.Face = hex.newFace();
                face.add(0);
                face.add(1);
                face.add(2);
                face.add(3);
                face.add(4);
                face.add(5);
                hex.addFace(face);
                face = hex.newFace();
                face.add(5);
                face.add(4);
                face.add(3);
                face.add(2);
                face.add(1);
                face.add(0);
                hex.addFace(face);
                return hex;
            }

            constructor(__parent: any) {
                this.__parent = __parent;
            }
        }
        BlueStrutGeometry["__class"] = "com.vzome.core.viewing.DodecagonalShapes.BlueStrutGeometry";
        BlueStrutGeometry["__interfaces"] = ["com.vzome.core.parts.StrutGeometry"];



        export class GreenStrutGeometry implements com.vzome.core.parts.StrutGeometry {
            public __parent: any;
            /**
             * 
             * @param {*} length
             * @return {com.vzome.core.math.Polyhedron}
             */
            public getStrutPolyhedron(length: com.vzome.core.algebra.AlgebraicNumber): com.vzome.core.math.Polyhedron {
                const field: com.vzome.core.algebra.AlgebraicField = this.__parent.mSymmetry.getField();
                const vector: com.vzome.core.algebra.AlgebraicVector = this.__parent.GREEN.scale(length);
                const hex: com.vzome.core.math.Polyhedron = new com.vzome.core.math.Polyhedron(field);
                hex.addVertex(vector.plus(this.__parent.GREEN.plus(this.__parent.V6)).negate());
                hex.addVertex(vector.plus(this.__parent.GREEN.negate()));
                hex.addVertex(vector.plus(this.__parent.GREEN.plus(this.__parent.V7).negate()));
                hex.addVertex(this.__parent.V6.plus(this.__parent.GREEN));
                hex.addVertex(this.__parent.GREEN);
                hex.addVertex(this.__parent.V7.plus(this.__parent.GREEN));
                let face: com.vzome.core.math.Polyhedron.Face = hex.newFace();
                face.add(0);
                face.add(1);
                face.add(2);
                face.add(3);
                face.add(4);
                face.add(5);
                hex.addFace(face);
                face = hex.newFace();
                face.add(5);
                face.add(4);
                face.add(3);
                face.add(2);
                face.add(1);
                face.add(0);
                hex.addFace(face);
                return hex;
            }

            constructor(__parent: any) {
                this.__parent = __parent;
            }
        }
        GreenStrutGeometry["__class"] = "com.vzome.core.viewing.DodecagonalShapes.GreenStrutGeometry";
        GreenStrutGeometry["__interfaces"] = ["com.vzome.core.parts.StrutGeometry"];


    }

}

