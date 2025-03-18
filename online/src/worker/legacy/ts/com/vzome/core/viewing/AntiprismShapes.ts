/* Generated from Java with JSweet 3.2.0-SNAPSHOT - http://www.jsweet.org */
namespace com.vzome.core.viewing {
    /**
     * @author David Hall
     * @param {string} pkgName
     * @param {string} name
     * @param {com.vzome.core.math.symmetry.AntiprismSymmetry} symm
     * @class
     * @extends com.vzome.core.viewing.AbstractShapes
     */
    export class AntiprismShapes extends com.vzome.core.viewing.AbstractShapes {
        public constructor(pkgName: string, name: string, symm: com.vzome.core.math.symmetry.AntiprismSymmetry) {
            super(pkgName, name, null, symm);
        }

        /**
         * 
         * @return {com.vzome.core.math.symmetry.AntiprismSymmetry}
         */
        public getSymmetry(): com.vzome.core.math.symmetry.AntiprismSymmetry {
            return <com.vzome.core.math.symmetry.AntiprismSymmetry><any>super.getSymmetry();
        }

        /**
         * 
         * @param {string} pkgName
         * @return {com.vzome.core.math.Polyhedron}
         */
        buildConnectorShape(pkgName: string): com.vzome.core.math.Polyhedron {
            const symm: com.vzome.core.math.symmetry.AntiprismSymmetry = this.getSymmetry();
            const field: com.vzome.core.algebra.PolygonField = symm.getField();
            const nSides: number = field.polygonSides();
            const antiprism: com.vzome.core.math.Polyhedron = new com.vzome.core.math.Polyhedron(field);
            const topX: com.vzome.core.algebra.AlgebraicNumber = field.one();
            const topY: com.vzome.core.algebra.AlgebraicNumber = field.zero();
            const maxTerm: com.vzome.core.algebra.AlgebraicNumber = field.getUnitDiagonal(field.diagonalCount() - 1);
            const botX: com.vzome.core.algebra.AlgebraicNumber = field.getUnitDiagonal(field.diagonalCount() - 2).dividedBy(maxTerm);
            const botY: com.vzome.core.algebra.AlgebraicNumber = maxTerm.reciprocal();
            const halfHeight: com.vzome.core.algebra.AlgebraicNumber = (pkgName === "thin") ? field.getUnitDiagonal(field.diagonalCount() - 1).reciprocal() : field.one();
            const rotationMatrix: com.vzome.core.algebra.AlgebraicMatrix = symm.getRotationMatrix();
            let vTop: com.vzome.core.algebra.AlgebraicVector = new com.vzome.core.algebra.AlgebraicVector(topX, topY, halfHeight);
            let vBot: com.vzome.core.algebra.AlgebraicVector = new com.vzome.core.algebra.AlgebraicVector(botX, botY, halfHeight.negate());
            for(let i: number = 0; i < nSides; i++) {{
                antiprism.addVertex(vTop);
                antiprism.addVertex(vBot);
                vTop = rotationMatrix.timesColumn(vTop);
                vBot = rotationMatrix.timesColumn(vBot);
            };}
            const topFace: com.vzome.core.math.Polyhedron.Face = antiprism.newFace();
            const botFace: com.vzome.core.math.Polyhedron.Face = antiprism.newFace();
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
                const face: com.vzome.core.math.Polyhedron.Face = antiprism.newFace();
                face.add(i);
                face.add((i + 1) % nVertices);
                face.add((i + 2) % nVertices);
                antiprism.addFace(face);
            };}
            for(let i: number = 1; i < nSides * 2; i += 2) {{
                const face: com.vzome.core.math.Polyhedron.Face = antiprism.newFace();
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


}

