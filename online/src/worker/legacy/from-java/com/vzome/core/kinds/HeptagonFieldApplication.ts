import { java, javaemul } from "../../../../../candies/j4ts-2.1.0-SNAPSHOT/bundle.js";
import { Tool } from "../../api/Tool.js";
import { AlgebraicField } from "../algebra/AlgebraicField.js";
import { Command } from "../commands/Command.js";
import { CommandAxialSymmetry } from "../commands/CommandAxialSymmetry.js";
import { SymmetryPerspective } from "../editor/SymmetryPerspective.js";
import { ToolsModel } from "../editor/ToolsModel.js";
import { AbstractSymmetryPerspective } from "./AbstractSymmetryPerspective.js";
import { DefaultFieldApplication } from "./DefaultFieldApplication.js";
import { AxialSymmetryToolFactory } from "../tools/AxialSymmetryToolFactory.js";
import { LineReflectionToolFactory } from "../tools/LineReflectionToolFactory.js";
import { LinearMapToolFactory } from "../tools/LinearMapToolFactory.js";
import { MirrorToolFactory } from "../tools/MirrorToolFactory.js";
import { RotationToolFactory } from "../tools/RotationToolFactory.js";
import { ScalingToolFactory } from "../tools/ScalingToolFactory.js";
import { SymmetryToolFactory } from "../tools/SymmetryToolFactory.js";
import { TranslationToolFactory } from "../tools/TranslationToolFactory.js";
import { AbstractShapes } from "../viewing/AbstractShapes.js";
import { ExportedVEFShapes } from "../viewing/ExportedVEFShapes.js";
import { OctahedralShapes } from "../viewing/OctahedralShapes.js";
import { HeptagonalAntiprismSymmetry } from "../../fields/heptagon/HeptagonalAntiprismSymmetry.js";

/**
 * Everything here is stateless, or at worst, a cache (like Shapes).
 * An instance of this can be shared by many DocumentModels.
 * This is why it does not have tool factories, though it does
 * dictate what tool factories will be present.
 * 
 * @author Scott Vorthmann
 * @param {*} field
 * @class
 * @extends DefaultFieldApplication
 */
export class HeptagonFieldApplication extends DefaultFieldApplication {
    public constructor(field: AlgebraicField) {
        super(field);
        this.correctedAntiprismPerspective = new HeptagonFieldApplication.HeptagonalSymmetryPerspective(this, true);
        this.originalAntiprismPerspective = new HeptagonFieldApplication.HeptagonalSymmetryPerspective(this, false);
    }

    /**
     * 
     * @return {string}
     */
    public getLabel(): string {
        return "Heptagon";
    }

    /*private*/ correctedAntiprismPerspective: SymmetryPerspective;

    /*private*/ originalAntiprismPerspective: SymmetryPerspective;

    /**
     * 
     * @return {*}
     */
    public getSymmetryPerspectives(): java.util.Collection<SymmetryPerspective> {
        return java.util.Arrays.asList<any>(this.correctedAntiprismPerspective, super.getDefaultSymmetryPerspective(), this.originalAntiprismPerspective);
    }

    /**
     * 
     * @return {*}
     */
    public getDefaultSymmetryPerspective(): SymmetryPerspective {
        return this.correctedAntiprismPerspective;
    }

    /**
     * 
     * @param {string} symmName
     * @return {*}
     */
    public getSymmetryPerspective(symmName: string): SymmetryPerspective {
        switch((symmName)) {
        case "heptagonal antiprism corrected":
            return this.correctedAntiprismPerspective;
        case "heptagonal antiprism":
            return this.originalAntiprismPerspective;
        default:
            return super.getSymmetryPerspective(symmName);
        }
    }
}
HeptagonFieldApplication["__class"] = "com.vzome.core.kinds.HeptagonFieldApplication";
HeptagonFieldApplication["__interfaces"] = ["com.vzome.core.math.symmetry.Symmetries4D","com.vzome.core.editor.FieldApplication"];



export namespace HeptagonFieldApplication {

    export class HeptagonalSymmetryPerspective extends AbstractSymmetryPerspective {
        public __parent: any;
        corrected: boolean;

        constructor(__parent: any, corrected: boolean) {
            super(new HeptagonalAntiprismSymmetry(__parent.getField(), "blue", corrected).createStandardOrbits("blue"));
            this.__parent = __parent;
            if (this.corrected === undefined) { this.corrected = false; }
            this.axialsymm = new CommandAxialSymmetry(this.symmetry);
            this.corrected = corrected;
            const octahedralShapes: AbstractShapes = new OctahedralShapes("octahedral", "triangular antiprism", this.symmetry);
            const antiprismShapes: AbstractShapes = new ExportedVEFShapes(null, "heptagon/antiprism", "heptagonal antiprism", this.symmetry, octahedralShapes);
            this.setDefaultGeometry(antiprismShapes);
            this.addShapes(octahedralShapes);
        }

        /**
         * 
         * @return {string}
         */
        public getLabel(): string {
            return this.corrected ? "heptagonal antiprism" : null;
        }

        /**
         * 
         * @param {Tool.Kind} kind
         * @param {ToolsModel} tools
         * @return {*}
         */
        public createToolFactories(kind: Tool.Kind, tools: ToolsModel): java.util.List<Tool.Factory> {
            const result: java.util.List<Tool.Factory> = <any>(new java.util.ArrayList<any>());
            switch((kind)) {
            case Tool.Kind.SYMMETRY:
                result.add(new SymmetryToolFactory(tools, this.symmetry));
                result.add(new LineReflectionToolFactory(tools));
                result.add(new MirrorToolFactory(tools));
                result.add(new AxialSymmetryToolFactory(tools, this.symmetry));
                break;
            case Tool.Kind.TRANSFORM:
                result.add(new ScalingToolFactory(tools, this.symmetry));
                result.add(new RotationToolFactory(tools, this.symmetry));
                result.add(new TranslationToolFactory(tools));
                break;
            case Tool.Kind.LINEAR_MAP:
                result.add(new LinearMapToolFactory(tools, this.symmetry, false));
                break;
            default:
                break;
            }
            return result;
        }

        /**
         * 
         * @param {Tool.Kind} kind
         * @param {ToolsModel} tools
         * @return {*}
         */
        public predefineTools(kind: Tool.Kind, tools: ToolsModel): java.util.List<Tool> {
            const result: java.util.List<Tool> = <any>(new java.util.ArrayList<any>());
            switch((kind)) {
            case Tool.Kind.SYMMETRY:
                result.add(new SymmetryToolFactory(tools, this.symmetry).createPredefinedTool("heptagonal antiprism around origin"));
                result.add(new MirrorToolFactory(tools).createPredefinedTool("reflection through XY plane"));
                result.add(new AxialSymmetryToolFactory(tools, this.symmetry).createPredefinedTool("symmetry around red through origin"));
                break;
            case Tool.Kind.TRANSFORM:
                result.add(new ScalingToolFactory(tools, this.symmetry).createPredefinedTool("scale down"));
                result.add(new ScalingToolFactory(tools, this.symmetry).createPredefinedTool("scale up"));
                result.add(new RotationToolFactory(tools, this.symmetry, true).createPredefinedTool("rotate around red through origin"));
                result.add(new TranslationToolFactory(tools).createPredefinedTool("b1 move along +X"));
                break;
            default:
                break;
            }
            return result;
        }

        axialsymm: Command;

        /**
         * 
         * @param {string} action
         * @return {*}
         */
        public getLegacyCommand(action: string): Command {
            switch((action)) {
            case "axialsymm":
                return this.axialsymm;
            default:
                return super.getLegacyCommand(action);
            }
        }

        /**
         * 
         * @return {string}
         */
        public getModelResourcePath(): string {
            return "org/vorthmann/zome/app/heptagonal antiprism.vZome";
        }
    }
    HeptagonalSymmetryPerspective["__class"] = "com.vzome.core.kinds.HeptagonFieldApplication.HeptagonalSymmetryPerspective";
    HeptagonalSymmetryPerspective["__interfaces"] = ["com.vzome.core.editor.SymmetryPerspective"];


}
