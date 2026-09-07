import { java, javaemul } from "../../../../../candies/j4ts-2.1.0-SNAPSHOT/bundle.js";
import { Tool } from "../../api/Tool.js";
import { AlgebraicField } from "../algebra/AlgebraicField.js";
import { AlgebraicNumber } from "../algebra/AlgebraicNumber.js";
import { AlgebraicVector } from "../algebra/AlgebraicVector.js";
import { Command } from "../commands/Command.js";
import { CommandTauDivision } from "../commands/CommandTauDivision.js";
import { CommandUniformH4Polytope } from "../commands/CommandUniformH4Polytope.js";
import { SymmetryPerspective } from "../editor/SymmetryPerspective.js";
import { ToolsModel } from "../editor/ToolsModel.js";
import { DefaultFieldApplication } from "./DefaultFieldApplication.js";
import { IcosahedralSymmetryPerspective } from "./IcosahedralSymmetryPerspective.js";
import { IcosahedralSymmetry } from "../math/symmetry/IcosahedralSymmetry.js";
import { QuaternionicSymmetry } from "../math/symmetry/QuaternionicSymmetry.js";
import { Symmetry } from "../math/symmetry/Symmetry.js";
import { WythoffConstruction } from "../math/symmetry/WythoffConstruction.js";
import { AxialStretchTool } from "../tools/AxialStretchTool.js";
import { IcosahedralToolFactory } from "../tools/IcosahedralToolFactory.js";

/**
 * Everything here is stateless, or at worst, a cache (like Shapes).
 * An instance of this can be shared by many DocumentModels.
 * This is why it does not have tool factories, though it does
 * dictate what tool factories will be present.
 * 
 * @author vorth
 * @param {*} field
 * @class
 * @extends DefaultFieldApplication
 */
export class SnubDodecFieldApplication extends DefaultFieldApplication {
    /*private*/ icosahedralPerspective: IcosahedralSymmetryPerspective;

    public constructor(field: AlgebraicField) {
        super(field);
        if (this.icosahedralPerspective === undefined) { this.icosahedralPerspective = null; }
        this.h4Builder = null;
        this.cmdTauDivide = new CommandTauDivision();
        const icosaSymm: IcosahedralSymmetry = new SnubDodecFieldApplication.SnubDodecFieldApplication$0(this, field);
        this.icosahedralPerspective = new IcosahedralSymmetryPerspective(icosaSymm);
    }

    /**
     * 
     * @return {string}
     */
    public getName(): string {
        return this.getField().getName();
    }

    /**
     * 
     * @return {string}
     */
    public getLabel(): string {
        return "Snub Dodecahedron";
    }

    /**
     * 
     * @return {*}
     */
    public getSymmetryPerspectives(): java.util.Collection<SymmetryPerspective> {
        return java.util.Arrays.asList<any>(this.icosahedralPerspective, super.getDefaultSymmetryPerspective());
    }

    /**
     * 
     * @return {*}
     */
    public getDefaultSymmetryPerspective(): SymmetryPerspective {
        return this.icosahedralPerspective;
    }

    /**
     * 
     * @param {string} symmName
     * @return {*}
     */
    public getSymmetryPerspective(symmName: string): SymmetryPerspective {
        switch((symmName)) {
        case "icosahedral":
            return this.icosahedralPerspective;
        default:
            return super.getSymmetryPerspective(symmName);
        }
    }

    /**
     * 
     * @param {string} name
     * @return {QuaternionicSymmetry}
     */
    public getQuaternionSymmetry(name: string): QuaternionicSymmetry {
        return this.icosahedralPerspective.getQuaternionSymmetry(name);
    }

    /**
     * 
     * @param {*} toolFactories
     * @param {ToolsModel} tools
     */
    public registerToolFactories(toolFactories: java.util.Map<string, Tool.Factory>, tools: ToolsModel) {
        super.registerToolFactories(toolFactories, tools);
        const symm: IcosahedralSymmetry = this.icosahedralPerspective.getSymmetry();
        toolFactories.put("AxialStretchTool", new AxialStretchTool.Factory(tools, symm, false, false, false));
        toolFactories.put("SymmetryTool", new IcosahedralToolFactory(tools, symm));
    }

    /*private*/ h4Builder: CommandUniformH4Polytope;

    /**
     * 
     * @param {string} groupName
     * @param {number} index
     * @param {number} edgesToRender
     * @param {AlgebraicNumber[]} edgeScales
     * @param {*} listener
     */
    public constructPolytope(groupName: string, index: number, edgesToRender: number, edgeScales: AlgebraicNumber[], listener: WythoffConstruction.Listener) {
        switch((groupName)) {
        case "H4":
            if (this.h4Builder == null){
                const qsymm: QuaternionicSymmetry = new QuaternionicSymmetry("H_4", "com/vzome/core/math/symmetry/H4roots.vef", this.getField());
                this.h4Builder = new CommandUniformH4Polytope(this.getField(), qsymm, 0);
            }
            this.h4Builder.generate(index, edgesToRender, edgeScales, listener);
            break;
        default:
            super.constructPolytope(groupName, index, edgesToRender, edgeScales, listener);
            break;
        }
    }

    /*private*/ cmdTauDivide: Command;

    /**
     * 
     * @param {string} action
     * @return {*}
     */
    public getLegacyCommand(action: string): Command {
        switch((action)) {
        case "tauDivide":
            return this.cmdTauDivide;
        default:
            return super.getLegacyCommand(action);
        }
    }
}
SnubDodecFieldApplication["__class"] = "com.vzome.core.kinds.SnubDodecFieldApplication";
SnubDodecFieldApplication["__interfaces"] = ["com.vzome.core.math.symmetry.Symmetries4D","com.vzome.core.editor.FieldApplication"];



export namespace SnubDodecFieldApplication {

    export class SnubDodecFieldApplication$0 extends IcosahedralSymmetry {
        public __parent: any;
        /**
         * 
         */
        createOtherOrbits() {
            super.createOtherOrbits();
            const vSnubPentagon: AlgebraicVector = this.mField.createIntegerVector([[4, -4, 0, 0, -2, 2], [-4, 0, 0, 0, 2, 0], [0, 0, 0, 0, 0, 2]]);
            const vSnubTriangle: AlgebraicVector = this.mField.createIntegerVector([[0, -4, -2, 0, 0, 2], [-4, 4, 0, -2, 2, -2], [-4, 0, -2, -2, 2, 0]]);
            const vSnubDiagonal: AlgebraicVector = this.mField.createIntegerVector([[8, 0, 0, 4, -4, 0], [0, -4, 0, 0, 0, 0], [0, 0, 0, 0, 0, 0]]);
            const vSnubFaceNorm: AlgebraicVector = this.mField.createIntegerVector([[-1, 0, 1, -1, 1, 0], [1, 0, 0, 0, 0, 0], [1, 0, -1, 2, 0, 1]]);
            const vSnubVertex: AlgebraicVector = this.mField.createIntegerVector([[1, 0, 0, 0, 0, 0], [1, 0, -1, 1, -1, 0], [1, 0, 0, 1, -1, 1]]);
            const scale: AlgebraicNumber = this.mField['createPower$int'](-3);
            let scaleFaceNorm: AlgebraicNumber;
            let scaleVertex: AlgebraicNumber = this.mField.one();
            scaleFaceNorm = this.mField['createAlgebraicNumber$int_A']([-3, 2, 2, -1, 5, -3]).reciprocal();
            scaleVertex = this.mField['createAlgebraicNumber$int_A$int']([-3, 2, 7, -4, 2, -1], 3).reciprocal();
            this.createZoneOrbit$java_lang_String$int$int$com_vzome_core_algebra_AlgebraicVector$boolean$boolean$com_vzome_core_algebra_AlgebraicNumber("snubPentagon", 0, Symmetry.NO_ROTATION, vSnubPentagon, false, false, scale).withCorrection();
            this.createZoneOrbit$java_lang_String$int$int$com_vzome_core_algebra_AlgebraicVector$boolean$boolean$com_vzome_core_algebra_AlgebraicNumber("snubTriangle", 0, Symmetry.NO_ROTATION, vSnubTriangle, false, false, scale).withCorrection();
            this.createZoneOrbit$java_lang_String$int$int$com_vzome_core_algebra_AlgebraicVector$boolean$boolean$com_vzome_core_algebra_AlgebraicNumber("snubDiagonal", 0, Symmetry.NO_ROTATION, vSnubDiagonal, false, false, scale).withCorrection();
            this.createZoneOrbit$java_lang_String$int$int$com_vzome_core_algebra_AlgebraicVector$boolean$boolean$com_vzome_core_algebra_AlgebraicNumber("snubFaceNormal", 0, Symmetry.NO_ROTATION, vSnubFaceNorm, false, false, scale['times$com_vzome_core_algebra_AlgebraicNumber'](scaleFaceNorm)).withCorrection();
            this.createZoneOrbit$java_lang_String$int$int$com_vzome_core_algebra_AlgebraicVector$boolean$boolean$com_vzome_core_algebra_AlgebraicNumber("snubVertex", 0, Symmetry.NO_ROTATION, vSnubVertex, true, false, scale['times$com_vzome_core_algebra_AlgebraicNumber'](scaleVertex)).withCorrection();
        }

        constructor(__parent: any, __arg0: any) {
            super(__arg0);
            this.__parent = __parent;
        }
    }
    SnubDodecFieldApplication$0["__interfaces"] = ["com.vzome.core.math.symmetry.Symmetry","com.vzome.core.math.symmetry.Embedding"];


}
