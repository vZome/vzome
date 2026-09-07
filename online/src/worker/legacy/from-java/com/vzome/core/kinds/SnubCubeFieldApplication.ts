import { AlgebraicNumber } from "../algebra/AlgebraicNumber.js";
import { AlgebraicVector } from "../algebra/AlgebraicVector.js";
import { SnubCubeField } from "../algebra/SnubCubeField.js";
import { DefaultFieldApplication } from "./DefaultFieldApplication.js";
import { OctahedralSymmetryPerspective } from "./OctahedralSymmetryPerspective.js";
import { AbstractSymmetry } from "../math/symmetry/AbstractSymmetry.js";
import { Symmetry } from "../math/symmetry/Symmetry.js";
import { AbstractShapes } from "../viewing/AbstractShapes.js";
import { ExportedVEFShapes } from "../viewing/ExportedVEFShapes.js";

export class SnubCubeFieldApplication extends DefaultFieldApplication {
    public constructor(field: SnubCubeField) {
        super(field);
        const symmPerspective: OctahedralSymmetryPerspective = <OctahedralSymmetryPerspective><any>super.getDefaultSymmetryPerspective();
        symmPerspective.setModelResourcePath("org/vorthmann/zome/app/snubCubeTrackball-vef.vZome");
        const symm: AbstractSymmetry = symmPerspective.getSymmetry();
        const vSnubSquare: AlgebraicVector = field.createVector([[1, 1, 0, 1, 0, 1], [0, 1, 2, 1, -1, 1], [0, 1, 0, 1, 0, 1]]);
        const vSnubTriangle: AlgebraicVector = field.createVector([[1, 1, 0, 1, 0, 1], [1, 2, 1, 1, -1, 2], [1, 2, -1, 1, 1, 2]]);
        const vSnubDiagonal: AlgebraicVector = field.createVector([[1, 1, 0, 1, 0, 1], [0, 1, -1, 2, 1, 2], [0, 1, -1, 2, 1, 2]]);
        const vSnubFaceNormal: AlgebraicVector = field.createVector([[1, 1, 0, 1, 0, 1], [-2, 7, 2, 7, 1, 7], [-1, 7, -6, 7, 4, 7]]);
        const vSnubVertex: AlgebraicVector = field.createVector([[1, 1, 0, 1, 0, 1], [-1, 1, -1, 1, 1, 1], [0, 1, 2, 1, -1, 1]]);
        const vSnubSquareMidEdge: AlgebraicVector = field.createVector([[1, 1, 0, 1, 0, 1], [-1, 2, 1, 2, 0, 1], [-1, 2, -3, 2, 1, 1]]);
        const vSnubTriangleMidEdge: AlgebraicVector = field.createVector([[1, 1, 0, 1, 0, 1], [-1, 1, 1, 1, 0, 1], [-1, 1, -1, 1, 1, 1]]);
        const nSnubSquare: AlgebraicNumber = field.createAlgebraicNumber$int_A([-1, 1, 0]);
        const nSnubTriangle: AlgebraicNumber = field.createAlgebraicNumber$int_A([1, -2, 1]);
        const nSnubDiagonal: AlgebraicNumber = field.createAlgebraicNumber$int_A([0, 4, -2]);
        const nSnubFaceNormal: AlgebraicNumber = field.createAlgebraicNumber$int_A([2, 3, -2]);
        const nSnubVertex: AlgebraicNumber = field.one();
        const nSnubSquareMidEdge: AlgebraicNumber = field.one();
        const nSnubTriangleMidEdge: AlgebraicNumber = field.createAlgebraicNumber$int_A([0, -1, 1]).dividedBy(field.createRational$long(2));
        symm.createZoneOrbit$java_lang_String$int$int$com_vzome_core_algebra_AlgebraicVector$boolean$boolean$com_vzome_core_algebra_AlgebraicNumber("snubSquare", 0, Symmetry.NO_ROTATION, vSnubSquare, false, false, nSnubSquare);
        symm.createZoneOrbit$java_lang_String$int$int$com_vzome_core_algebra_AlgebraicVector$boolean$boolean$com_vzome_core_algebra_AlgebraicNumber("snubTriangle", 0, Symmetry.NO_ROTATION, vSnubTriangle, false, false, nSnubTriangle);
        symm.createZoneOrbit$java_lang_String$int$int$com_vzome_core_algebra_AlgebraicVector$boolean$boolean$com_vzome_core_algebra_AlgebraicNumber("snubDiagonal", 0, Symmetry.NO_ROTATION, vSnubDiagonal, false, false, nSnubDiagonal);
        symm.createZoneOrbit$java_lang_String$int$int$com_vzome_core_algebra_AlgebraicVector$boolean$boolean$com_vzome_core_algebra_AlgebraicNumber("snubFaceNormal", 0, Symmetry.NO_ROTATION, vSnubFaceNormal, true, false, nSnubFaceNormal);
        symm.createZoneOrbit$java_lang_String$int$int$com_vzome_core_algebra_AlgebraicVector$boolean$boolean$com_vzome_core_algebra_AlgebraicNumber("snubVertex", 0, Symmetry.NO_ROTATION, vSnubVertex, true, false, nSnubVertex);
        symm.createZoneOrbit$java_lang_String$int$int$com_vzome_core_algebra_AlgebraicVector$boolean$boolean$com_vzome_core_algebra_AlgebraicNumber("snubSquareMid", 0, Symmetry.NO_ROTATION, vSnubSquareMidEdge, false, false, nSnubSquareMidEdge);
        symm.createZoneOrbit$java_lang_String$int$int$com_vzome_core_algebra_AlgebraicVector$boolean$boolean$com_vzome_core_algebra_AlgebraicNumber("snubTriangleMid", 0, Symmetry.NO_ROTATION, vSnubTriangleMidEdge, false, false, nSnubTriangleMidEdge);
        const defaultShapes: AbstractShapes = <AbstractShapes><any>symmPerspective.getDefaultGeometry();
        let resPath: string = "snubCube";
        const snubCubeRhShapes: AbstractShapes = new ExportedVEFShapes(null, resPath, "snub cube right", null, symm, defaultShapes, false);
        const snubCubeLhShapes: AbstractShapes = new ExportedVEFShapes(null, resPath, "snub cube left", null, symm, defaultShapes, true);
        resPath = "snubCube/dual";
        const snubDualRhShapes: AbstractShapes = new ExportedVEFShapes(null, resPath, "snub cube dual right", null, symm, defaultShapes, false);
        const snubDualLhShapes: AbstractShapes = new ExportedVEFShapes(null, resPath, "snub cube dual left", null, symm, defaultShapes, true);
        resPath = "snubCube/disdyakisDodec";
        const disdyakisDodec: AbstractShapes = new ExportedVEFShapes(null, resPath, "disdyakis dodec", null, symm, defaultShapes, false);
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
