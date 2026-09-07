import { java, javaemul } from "../../../../../candies/j4ts-2.1.0-SNAPSHOT/bundle.js";
import { Tool } from "../../api/Tool.js";
import { AlgebraicField } from "../algebra/AlgebraicField.js";
import { AlgebraicNumber } from "../algebra/AlgebraicNumber.js";
import { Command } from "../commands/Command.js";
import { CommandTauDivision } from "../commands/CommandTauDivision.js";
import { CommandUniformH4Polytope } from "../commands/CommandUniformH4Polytope.js";
import { SymmetryPerspective } from "../editor/SymmetryPerspective.js";
import { ToolsModel } from "../editor/ToolsModel.js";
import { DefaultFieldApplication } from "./DefaultFieldApplication.js";
import { IcosahedralSymmetryPerspective } from "./IcosahedralSymmetryPerspective.js";
import { OctahedralSymmetryPerspective } from "./OctahedralSymmetryPerspective.js";
import { AbstractSymmetry } from "../math/symmetry/AbstractSymmetry.js";
import { IcosahedralSymmetry } from "../math/symmetry/IcosahedralSymmetry.js";
import { QuaternionicSymmetry } from "../math/symmetry/QuaternionicSymmetry.js";
import { Symmetry } from "../math/symmetry/Symmetry.js";
import { WythoffConstruction } from "../math/symmetry/WythoffConstruction.js";
import { AxialStretchTool } from "../tools/AxialStretchTool.js";
import { IcosahedralToolFactory } from "../tools/IcosahedralToolFactory.js";
import { AbstractShapes } from "../viewing/AbstractShapes.js";
import { ExportedVEFShapes } from "../viewing/ExportedVEFShapes.js";

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
export class GoldenFieldApplication extends DefaultFieldApplication {
    /*private*/ icosahedralPerspective: IcosahedralSymmetryPerspective;

    public constructor(field: AlgebraicField) {
        super(field);
        if (this.icosahedralPerspective === undefined) { this.icosahedralPerspective = null; }
        this.h4Builder = null;
        this.cmdTauDivide = new CommandTauDivision();
        this.icosahedralPerspective = new IcosahedralSymmetryPerspective(this.getField());
        const octahedralPerspective: OctahedralSymmetryPerspective = <OctahedralSymmetryPerspective><any>super.getDefaultSymmetryPerspective();
        const symm: AbstractSymmetry = octahedralPerspective.getSymmetry();
        symm.createZoneOrbit$java_lang_String$int$int$int_A_A$boolean$boolean$com_vzome_core_algebra_AlgebraicNumber("yellow", 0, 4, [[0, 1, 1, 1], [0, 1, 1, 1], [0, 1, 1, 1]], true, false, this.getField()['createPower$int'](-1));
        symm.createZoneOrbit$java_lang_String$int$int$int_A_A$boolean$boolean$com_vzome_core_algebra_AlgebraicNumber("green", 1, 8, [[1, 1, 0, 1], [1, 1, 0, 1], [0, 1, 0, 1]], true, true, this.getField()['createRational$long'](2));
        symm.createZoneOrbit$java_lang_String$int$int$int_A_A("lavender", 0, Symmetry.NO_ROTATION, [[2, 1, -1, 1], [0, 1, 1, 1], [2, 1, -1, 1]]);
        symm.createZoneOrbit$java_lang_String$int$int$int_A_A("olive", 0, Symmetry.NO_ROTATION, [[0, 1, 1, 1], [0, 1, 1, 1], [2, 1, -1, 1]]);
        symm.createZoneOrbit$java_lang_String$int$int$int_A_A("maroon", 0, Symmetry.NO_ROTATION, [[-1, 1, 1, 1], [3, 1, -1, 1], [1, 1, -1, 1]]);
        symm.createZoneOrbit$java_lang_String$int$int$int_A_A("brown", 0, Symmetry.NO_ROTATION, [[-1, 1, 1, 1], [-1, 1, 1, 1], [-2, 1, 2, 1]]);
        symm.createZoneOrbit$java_lang_String$int$int$int_A_A("red", 0, Symmetry.NO_ROTATION, [[0, 1, 1, 1], [1, 1, 0, 1], [0, 1, 0, 1]]);
        symm.createZoneOrbit$java_lang_String$int$int$int_A_A$boolean$boolean$com_vzome_core_algebra_AlgebraicNumber("purple", 0, Symmetry.NO_ROTATION, [[1, 1, 1, 1], [0, 1, 0, 1], [-1, 1, 0, 1]], false, false, this.getField()['createPower$int'](-1));
        symm.createZoneOrbit$java_lang_String$int$int$int_A_A$boolean$boolean$com_vzome_core_algebra_AlgebraicNumber("black", 0, Symmetry.NO_ROTATION, [[1, 2, 0, 1], [0, 1, 1, 2], [-1, 2, 1, 2]], false, false, this.getField()['createRational$long'](2));
        symm.createZoneOrbit$java_lang_String$int$int$int_A_A("turquoise", 0, Symmetry.NO_ROTATION, [[1, 1, 2, 1], [3, 1, 4, 1], [3, 1, 4, 1]]);
        const defaultShapes: AbstractShapes = new ExportedVEFShapes(null, "octahedral", "trapezoids", symm, null);
        octahedralPerspective.setDefaultGeometry(defaultShapes);
        octahedralPerspective.addShapes(new ExportedVEFShapes(null, "octahedralFast", "small octahedra", symm, null));
        octahedralPerspective.addShapes(new ExportedVEFShapes(null, "octahedralRealistic", "vZome logo", symm, defaultShapes));
    }

    /**
     * 
     * @return {string}
     */
    public getLabel(): string {
        return "Zome (Golden)";
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
GoldenFieldApplication["__class"] = "com.vzome.core.kinds.GoldenFieldApplication";
GoldenFieldApplication["__interfaces"] = ["com.vzome.core.math.symmetry.Symmetries4D","com.vzome.core.editor.FieldApplication"];
