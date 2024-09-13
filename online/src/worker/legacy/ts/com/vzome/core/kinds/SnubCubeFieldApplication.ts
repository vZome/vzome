/* Generated from Java with JSweet 3.2.0-SNAPSHOT - http://www.jsweet.org */
namespace com.vzome.core.kinds {
    export class SnubCubeFieldApplication extends com.vzome.core.kinds.DefaultFieldApplication {
        public constructor(field: com.vzome.core.algebra.SnubCubeField) {
            super(field);
            const symmPerspective: com.vzome.core.kinds.OctahedralSymmetryPerspective = <com.vzome.core.kinds.OctahedralSymmetryPerspective><any>super.getDefaultSymmetryPerspective();
            symmPerspective.setModelResourcePath("org/vorthmann/zome/app/snubCubeTrackball-vef.vZome");
            const symm: com.vzome.core.math.symmetry.AbstractSymmetry = symmPerspective.getSymmetry();
            const vSnubSquare: com.vzome.core.algebra.AlgebraicVector = field.createVector([[1, 1, 0, 1, 0, 1], [0, 1, 2, 1, -1, 1], [0, 1, 0, 1, 0, 1]]);
            const vSnubTriangle: com.vzome.core.algebra.AlgebraicVector = field.createVector([[1, 1, 0, 1, 0, 1], [1, 2, 1, 1, -1, 2], [1, 2, -1, 1, 1, 2]]);
            const vSnubDiagonal: com.vzome.core.algebra.AlgebraicVector = field.createVector([[1, 1, 0, 1, 0, 1], [0, 1, -1, 2, 1, 2], [0, 1, -1, 2, 1, 2]]);
            const vSnubFaceNormal: com.vzome.core.algebra.AlgebraicVector = field.createVector([[1, 1, 0, 1, 0, 1], [-2, 7, 2, 7, 1, 7], [-1, 7, -6, 7, 4, 7]]);
            const vSnubVertex: com.vzome.core.algebra.AlgebraicVector = field.createVector([[1, 1, 0, 1, 0, 1], [-1, 1, -1, 1, 1, 1], [0, 1, 2, 1, -1, 1]]);
            const vSnubSquareMidEdge: com.vzome.core.algebra.AlgebraicVector = field.createVector([[1, 1, 0, 1, 0, 1], [-1, 2, 1, 2, 0, 1], [-1, 2, -3, 2, 1, 1]]);
            const vSnubTriangleMidEdge: com.vzome.core.algebra.AlgebraicVector = field.createVector([[1, 1, 0, 1, 0, 1], [-1, 1, 1, 1, 0, 1], [-1, 1, -1, 1, 1, 1]]);
            const nSnubSquare: com.vzome.core.algebra.AlgebraicNumber = field.createAlgebraicNumber$int_A([-1, 1, 0]);
            const nSnubTriangle: com.vzome.core.algebra.AlgebraicNumber = field.createAlgebraicNumber$int_A([1, -2, 1]);
            const nSnubDiagonal: com.vzome.core.algebra.AlgebraicNumber = field.createAlgebraicNumber$int_A([0, 4, -2]);
            const nSnubFaceNormal: com.vzome.core.algebra.AlgebraicNumber = field.createAlgebraicNumber$int_A([2, 3, -2]);
            const nSnubVertex: com.vzome.core.algebra.AlgebraicNumber = field.one();
            const nSnubSquareMidEdge: com.vzome.core.algebra.AlgebraicNumber = field.one();
            const nSnubTriangleMidEdge: com.vzome.core.algebra.AlgebraicNumber = field.createAlgebraicNumber$int_A([0, -1, 1]).dividedBy(field.createRational$long(2));
            symm.createZoneOrbit$java_lang_String$int$int$com_vzome_core_algebra_AlgebraicVector$boolean$boolean$com_vzome_core_algebra_AlgebraicNumber("snubSquare", 0, com.vzome.core.math.symmetry.Symmetry.NO_ROTATION, vSnubSquare, false, false, nSnubSquare);
            symm.createZoneOrbit$java_lang_String$int$int$com_vzome_core_algebra_AlgebraicVector$boolean$boolean$com_vzome_core_algebra_AlgebraicNumber("snubTriangle", 0, com.vzome.core.math.symmetry.Symmetry.NO_ROTATION, vSnubTriangle, false, false, nSnubTriangle);
            symm.createZoneOrbit$java_lang_String$int$int$com_vzome_core_algebra_AlgebraicVector$boolean$boolean$com_vzome_core_algebra_AlgebraicNumber("snubDiagonal", 0, com.vzome.core.math.symmetry.Symmetry.NO_ROTATION, vSnubDiagonal, false, false, nSnubDiagonal);
            symm.createZoneOrbit$java_lang_String$int$int$com_vzome_core_algebra_AlgebraicVector$boolean$boolean$com_vzome_core_algebra_AlgebraicNumber("snubFaceNormal", 0, com.vzome.core.math.symmetry.Symmetry.NO_ROTATION, vSnubFaceNormal, true, false, nSnubFaceNormal);
            symm.createZoneOrbit$java_lang_String$int$int$com_vzome_core_algebra_AlgebraicVector$boolean$boolean$com_vzome_core_algebra_AlgebraicNumber("snubVertex", 0, com.vzome.core.math.symmetry.Symmetry.NO_ROTATION, vSnubVertex, true, false, nSnubVertex);
            symm.createZoneOrbit$java_lang_String$int$int$com_vzome_core_algebra_AlgebraicVector$boolean$boolean$com_vzome_core_algebra_AlgebraicNumber("snubSquareMid", 0, com.vzome.core.math.symmetry.Symmetry.NO_ROTATION, vSnubSquareMidEdge, false, false, nSnubSquareMidEdge);
            symm.createZoneOrbit$java_lang_String$int$int$com_vzome_core_algebra_AlgebraicVector$boolean$boolean$com_vzome_core_algebra_AlgebraicNumber("snubTriangleMid", 0, com.vzome.core.math.symmetry.Symmetry.NO_ROTATION, vSnubTriangleMidEdge, false, false, nSnubTriangleMidEdge);
            const defaultShapes: com.vzome.core.viewing.AbstractShapes = <com.vzome.core.viewing.AbstractShapes><any>symmPerspective.getDefaultGeometry();
            let resPath: string = "snubCube";
            const snubCubeRhShapes: com.vzome.core.viewing.AbstractShapes = new com.vzome.core.viewing.ExportedVEFShapes(null, resPath, "snub cube right", null, symm, defaultShapes, false);
            const snubCubeLhShapes: com.vzome.core.viewing.AbstractShapes = new com.vzome.core.viewing.ExportedVEFShapes(null, resPath, "snub cube left", null, symm, defaultShapes, true);
            resPath = "snubCube/dual";
            const snubDualRhShapes: com.vzome.core.viewing.AbstractShapes = new com.vzome.core.viewing.ExportedVEFShapes(null, resPath, "snub cube dual right", null, symm, defaultShapes, false);
            const snubDualLhShapes: com.vzome.core.viewing.AbstractShapes = new com.vzome.core.viewing.ExportedVEFShapes(null, resPath, "snub cube dual left", null, symm, defaultShapes, true);
            resPath = "snubCube/disdyakisDodec";
            const disdyakisDodec: com.vzome.core.viewing.AbstractShapes = new com.vzome.core.viewing.ExportedVEFShapes(null, resPath, "disdyakis dodec", null, symm, defaultShapes, false);
            symmPerspective.addShapes(disdyakisDodec);
            symmPerspective.addShapes(snubCubeRhShapes);
            symmPerspective.addShapes(snubCubeRhShapes);
            symmPerspective.addShapes(snubCubeLhShapes);
            symmPerspective.addShapes(snubDualRhShapes);
            symmPerspective.addShapes(snubDualLhShapes);
        }

        /**
         * 
         * @return {string}
         */
        public getLabel(): string {
            return "Snub Cube";
        }
    }
    SnubCubeFieldApplication["__class"] = "com.vzome.core.kinds.SnubCubeFieldApplication";
    SnubCubeFieldApplication["__interfaces"] = ["com.vzome.core.math.symmetry.Symmetries4D","com.vzome.core.editor.FieldApplication"];


}

