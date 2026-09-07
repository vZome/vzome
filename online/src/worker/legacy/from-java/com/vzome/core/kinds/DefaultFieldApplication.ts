import { java, javaemul } from "../../../../../candies/j4ts-2.1.0-SNAPSHOT/bundle.js";
import { Tool } from "../../api/Tool.js";
import { AlgebraicField } from "../algebra/AlgebraicField.js";
import { AlgebraicNumber } from "../algebra/AlgebraicNumber.js";
import { Command } from "../commands/Command.js";
import { CommandCentralSymmetry } from "../commands/CommandCentralSymmetry.js";
import { CommandCentroid } from "../commands/CommandCentroid.js";
import { CommandHide } from "../commands/CommandHide.js";
import { CommandMidpoint } from "../commands/CommandMidpoint.js";
import { CommandMirrorSymmetry } from "../commands/CommandMirrorSymmetry.js";
import { CommandPolygon } from "../commands/CommandPolygon.js";
import { CommandTranslate } from "../commands/CommandTranslate.js";
import { FieldApplication } from "../editor/FieldApplication.js";
import { SymmetryPerspective } from "../editor/SymmetryPerspective.js";
import { ToolsModel } from "../editor/ToolsModel.js";
import { OctahedralSymmetryPerspective } from "./OctahedralSymmetryPerspective.js";
import { A4Group } from "../math/symmetry/A4Group.js";
import { B4Group } from "../math/symmetry/B4Group.js";
import { CoxeterGroup } from "../math/symmetry/CoxeterGroup.js";
import { D4Group } from "../math/symmetry/D4Group.js";
import { F4Group } from "../math/symmetry/F4Group.js";
import { QuaternionicSymmetry } from "../math/symmetry/QuaternionicSymmetry.js";
import { WythoffConstruction } from "../math/symmetry/WythoffConstruction.js";
import { BookmarkToolFactory } from "../tools/BookmarkToolFactory.js";
import { InversionToolFactory } from "../tools/InversionToolFactory.js";
import { LineReflectionToolFactory } from "../tools/LineReflectionToolFactory.js";
import { LinearMapToolFactory } from "../tools/LinearMapToolFactory.js";
import { MirrorToolFactory } from "../tools/MirrorToolFactory.js";
import { ModuleToolFactory } from "../tools/ModuleToolFactory.js";
import { OctahedralToolFactory } from "../tools/OctahedralToolFactory.js";
import { PerspectiveProjectionToolFactory } from "../tools/PerspectiveProjectionToolFactory.js";
import { PlaneSelectionToolFactory } from "../tools/PlaneSelectionToolFactory.js";
import { ProjectionToolFactory } from "../tools/ProjectionToolFactory.js";
import { RotationToolFactory } from "../tools/RotationToolFactory.js";
import { ScalingToolFactory } from "../tools/ScalingToolFactory.js";
import { TranslationToolFactory } from "../tools/TranslationToolFactory.js";

export class DefaultFieldApplication implements FieldApplication {
    /*private*/ field: AlgebraicField;

    /*private*/ octahedralPerspective: SymmetryPerspective;

    /*private*/ groups4d: java.util.Map<string, CoxeterGroup>;

    /*private*/ pointsymm: Command;

    /*private*/ mirrorsymm: Command;

    /*private*/ translate: Command;

    /*private*/ centroid: Command;

    /*private*/ hideball: Command;

    /*private*/ hide: Command;

    /*private*/ panel: Command;

    /*private*/ midpoint: Command;

    public constructor(field: AlgebraicField) {
        if (this.field === undefined) { this.field = null; }
        if (this.octahedralPerspective === undefined) { this.octahedralPerspective = null; }
        this.groups4d = <any>(new java.util.HashMap<string, CoxeterGroup>());
        this.pointsymm = new CommandCentralSymmetry();
        this.mirrorsymm = new CommandMirrorSymmetry();
        this.translate = new CommandTranslate();
        this.centroid = new CommandCentroid();
        this.hideball = new CommandHide();
        this.hide = new CommandHide();
        this.panel = new CommandPolygon();
        this.midpoint = new CommandMidpoint();
        this.field = field;
    }

    /**
     * 
     * @return {string}
     */
    public getName(): string {
        return this.field.getName();
    }

    /**
     * 
     * @return {string}
     */
    public getLabel(): string {
        return null;
    }

    /**
     * 
     * @return {*}
     */
    public getField(): AlgebraicField {
        return this.field;
    }

    /**
     * 
     * @return {*}
     */
    public getDefaultSymmetryPerspective(): SymmetryPerspective {
        return this.getSymmetryPerspective("octahedral");
    }

    /**
     * 
     * @return {*}
     */
    public getSymmetryPerspectives(): java.util.Collection<SymmetryPerspective> {
        return java.util.Arrays.asList<any>(this.getDefaultSymmetryPerspective());
    }

    /**
     * 
     * @param {string} symmName
     * @return {*}
     */
    public getSymmetryPerspective(symmName: string): SymmetryPerspective {
        switch((symmName)) {
        case "octahedral":
            if (this.octahedralPerspective == null){
                this.octahedralPerspective = new OctahedralSymmetryPerspective(this.field);
            }
            return this.octahedralPerspective;
        default:
            return null;
        }
    }

    /**
     * 
     * @param {string} name
     * @return {QuaternionicSymmetry}
     */
    public getQuaternionSymmetry(name: string): QuaternionicSymmetry {
        return null;
    }

    /**
     * 
     * @param {*} toolFactories
     * @param {ToolsModel} tools
     */
    public registerToolFactories(toolFactories: java.util.Map<string, Tool.Factory>, tools: ToolsModel) {
        toolFactories.put("SymmetryTool", new OctahedralToolFactory(tools, null));
        toolFactories.put("RotationTool", new RotationToolFactory(tools, null));
        toolFactories.put("ScalingTool", new ScalingToolFactory(tools, null));
        toolFactories.put("InversionTool", new InversionToolFactory(tools));
        toolFactories.put("LineReflectionTool", new LineReflectionToolFactory(tools));
        toolFactories.put("MirrorTool", new MirrorToolFactory(tools));
        toolFactories.put("TranslationTool", new TranslationToolFactory(tools));
        toolFactories.put("ProjectionTool", new ProjectionToolFactory(tools));
        toolFactories.put("PerspectiveProjectionTool", new PerspectiveProjectionToolFactory(tools));
        toolFactories.put("BookmarkTool", new BookmarkToolFactory(tools));
        toolFactories.put("LinearTransformTool", new LinearMapToolFactory(tools, null, false));
        toolFactories.put("LinearMapTool", new LinearMapToolFactory(tools, null, true));
        toolFactories.put("ModuleTool", new ModuleToolFactory(tools));
        toolFactories.put("PlaneSelectionTool", new PlaneSelectionToolFactory(tools));
    }

    /**
     * 
     * @param {string} groupName
     * @param {number} index
     * @param {number} edgesToRender
     * @param {AlgebraicNumber[]} edgeScales
     * @param {*} listener
     */
    public constructPolytope(groupName: string, index: number, edgesToRender: number, edgeScales: AlgebraicNumber[], listener: WythoffConstruction.Listener) {
        let group: CoxeterGroup = this.groups4d.get(groupName);
        if (group == null){
            switch((groupName)) {
            case "A4":
                group = new A4Group(this.field);
                break;
            case "D4":
                group = new D4Group(this.field);
                break;
            case "F4":
                group = new F4Group(this.field);
                break;
            default:
                group = new B4Group(this.field);
                break;
            }
            this.groups4d.put(groupName, group);
        }
        WythoffConstruction.constructPolytope(group, index, edgesToRender, edgeScales, group, listener);
    }

    /**
     * 
     * @param {string} action
     * @return {*}
     */
    public getLegacyCommand(action: string): Command {
        switch((action)) {
        case "pointsymm":
            return this.pointsymm;
        case "mirrorsymm":
            return this.mirrorsymm;
        case "translate":
            return this.translate;
        case "centroid":
            return this.centroid;
        case "hideball":
            return this.hideball;
        case "hide":
            return this.hide;
        case "panel":
            return this.panel;
        case "midpoint":
            return this.midpoint;
        case "octasymm":
            return this.getDefaultSymmetryPerspective().getLegacyCommand(action);
        default:
            return null;
        }
    }
}
DefaultFieldApplication["__class"] = "com.vzome.core.kinds.DefaultFieldApplication";
DefaultFieldApplication["__interfaces"] = ["com.vzome.core.math.symmetry.Symmetries4D","com.vzome.core.editor.FieldApplication"];
