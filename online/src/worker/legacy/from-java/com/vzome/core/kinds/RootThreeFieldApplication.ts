import { java, javaemul } from "../../../../../candies/j4ts-2.1.0-SNAPSHOT/bundle.js";
import { Tool } from "../../api/Tool.js";
import { AlgebraicField } from "../algebra/AlgebraicField.js";
import { AlgebraicNumber } from "../algebra/AlgebraicNumber.js";
import { Command } from "../commands/Command.js";
import { CommandSymmetry } from "../commands/CommandSymmetry.js";
import { SymmetryPerspective } from "../editor/SymmetryPerspective.js";
import { ToolsModel } from "../editor/ToolsModel.js";
import { AbstractSymmetryPerspective } from "./AbstractSymmetryPerspective.js";
import { DefaultFieldApplication } from "./DefaultFieldApplication.js";
import { OctahedralSymmetryPerspective } from "./OctahedralSymmetryPerspective.js";
import { AbstractSymmetry } from "../math/symmetry/AbstractSymmetry.js";
import { Direction } from "../math/symmetry/Direction.js";
import { DodecagonalSymmetry } from "../math/symmetry/DodecagonalSymmetry.js";
import { Symmetry } from "../math/symmetry/Symmetry.js";
import { AxialSymmetryToolFactory } from "../tools/AxialSymmetryToolFactory.js";
import { InversionToolFactory } from "../tools/InversionToolFactory.js";
import { LineReflectionToolFactory } from "../tools/LineReflectionToolFactory.js";
import { LinearMapToolFactory } from "../tools/LinearMapToolFactory.js";
import { MirrorToolFactory } from "../tools/MirrorToolFactory.js";
import { ProjectionToolFactory } from "../tools/ProjectionToolFactory.js";
import { RotationToolFactory } from "../tools/RotationToolFactory.js";
import { ScalingToolFactory } from "../tools/ScalingToolFactory.js";
import { SymmetryToolFactory } from "../tools/SymmetryToolFactory.js";
import { TranslationToolFactory } from "../tools/TranslationToolFactory.js";
import { AbstractShapes } from "../viewing/AbstractShapes.js";
import { DodecagonalShapes } from "../viewing/DodecagonalShapes.js";
import { ExportedVEFShapes } from "../viewing/ExportedVEFShapes.js";

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
export class RootThreeFieldApplication extends DefaultFieldApplication {
    public constructor(field: AlgebraicField) {
        super(field);
        this.dodecagonalPerspective = new RootThreeFieldApplication.RootThreeFieldApplication$0(this, new DodecagonalSymmetry(this.getField()));
        const octahedralPerspective: OctahedralSymmetryPerspective = <OctahedralSymmetryPerspective><any>super.getDefaultSymmetryPerspective();
        const symm: AbstractSymmetry = octahedralPerspective.getSymmetry();
        symm.createZoneOrbit$java_lang_String$int$int$int_A_A$boolean("red", 0, Symmetry.NO_ROTATION, [[1, 1, 1, 2], [1, 2, 0, 1], [0, 1, 0, 1]], true);
        symm.createZoneOrbit$java_lang_String$int$int$int_A_A("brown", 0, Symmetry.NO_ROTATION, [[1, 1, 0, 1], [1, 1, 0, 1], [2, 1, 0, 1]]);
        const defaultShapes: AbstractShapes = new ExportedVEFShapes(null, "rootThreeOctaSmall", "small octahedra", "small connectors", symm);
        octahedralPerspective.setDefaultGeometry(defaultShapes);
    }

    /**
     * 
     * @return {string}
     */
    public getLabel(): string {
        return "\u221a3";
    }

    /*private*/ dodecagonalPerspective: SymmetryPerspective;

    /**
     * 
     * @return {*}
     */
    public getDefaultSymmetryPerspective(): SymmetryPerspective {
        return this.dodecagonalPerspective;
    }

    /**
     * 
     * @return {*}
     */
    public getSymmetryPerspectives(): java.util.Collection<SymmetryPerspective> {
        return java.util.Arrays.asList<any>(super.getDefaultSymmetryPerspective(), this.dodecagonalPerspective);
    }

    /**
     * 
     * @param {string} symmName
     * @return {*}
     */
    public getSymmetryPerspective(symmName: string): SymmetryPerspective {
        switch((symmName)) {
        case "dodecagonal":
            return this.dodecagonalPerspective;
        default:
            return super.getSymmetryPerspective(symmName);
        }
    }
}
RootThreeFieldApplication["__class"] = "com.vzome.core.kinds.RootThreeFieldApplication";
RootThreeFieldApplication["__interfaces"] = ["com.vzome.core.math.symmetry.Symmetries4D","com.vzome.core.editor.FieldApplication"];



export namespace RootThreeFieldApplication {

    export class RootThreeFieldApplication$0 extends AbstractSymmetryPerspective {
        public __parent: any;
        /**
         * 
         * @return {string}
         */
        public getName(): string {
            return "dodecagonal";
        }

        /**
         * 
         * @param {Direction} orbit
         * @return {boolean}
         */
        public orbitIsBuildDefault(orbit: Direction): boolean {
            switch((orbit.getName())) {
            case "blue":
            case "green":
                return true;
            default:
                return false;
            }
        }

        /**
         * 
         * @param {Direction} orbit
         * @return {*}
         */
        public getOrbitUnitLength(orbit: Direction): AlgebraicNumber {
            switch((orbit.getName())) {
            case "blue":
            case "green":
                return this.__parent.getField()['createPower$int'](2);
            default:
                return super.getOrbitUnitLength(orbit);
            }
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
                result.add(new InversionToolFactory(tools));
                result.add(new LineReflectionToolFactory(tools));
                result.add(new MirrorToolFactory(tools));
                result.add(new AxialSymmetryToolFactory(tools, this.symmetry));
                break;
            case Tool.Kind.TRANSFORM:
                result.add(new ScalingToolFactory(tools, this.symmetry));
                result.add(new RotationToolFactory(tools, this.symmetry));
                result.add(new TranslationToolFactory(tools));
                result.add(new ProjectionToolFactory(tools));
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
                result.add(new SymmetryToolFactory(tools, this.symmetry).createPredefinedTool("dodecagonal antiprism around origin"));
                result.add(new InversionToolFactory(tools).createPredefinedTool("reflection through origin"));
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

        dodecagonsymm: Command;

        /**
         * 
         * @param {string} action
         * @return {*}
         */
        public getLegacyCommand(action: string): Command {
            switch((action)) {
            case "dodecagonsymm":
                return this.dodecagonsymm;
            default:
                return super.getLegacyCommand(action);
            }
        }

        /**
         * 
         * @return {string}
         */
        public getModelResourcePath(): string {
            return "org/vorthmann/zome/app/12-gon-trackball-vef.vZome";
        }

        constructor(__parent: any, __arg0: any) {
            super(__arg0);
            this.__parent = __parent;
            (() => {
                const defaultShapes: AbstractShapes = new ExportedVEFShapes(null, "dodecagon3d", "prisms", this.symmetry);
                const hexagonShapes: AbstractShapes = new DodecagonalShapes("dodecagonal", "hexagons", "flat hexagons", this.symmetry);
                this.setDefaultGeometry(defaultShapes);
                this.addShapes(hexagonShapes);
            })();
            this.dodecagonsymm = new CommandSymmetry(this.symmetry);
        }
    }
    RootThreeFieldApplication$0["__interfaces"] = ["com.vzome.core.editor.SymmetryPerspective"];


}
