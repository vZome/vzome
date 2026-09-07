import { java, javaemul } from "../../../../../candies/j4ts-2.1.0-SNAPSHOT/bundle.js";
import { Tool } from "../../api/Tool.js";
import { AlgebraicNumber } from "../algebra/AlgebraicNumber.js";
import { PolygonField } from "../algebra/PolygonField.js";
import { Command } from "../commands/Command.js";
import { CommandAxialSymmetry } from "../commands/CommandAxialSymmetry.js";
import { CommandUniformH4Polytope } from "../commands/CommandUniformH4Polytope.js";
import { SymmetryPerspective } from "../editor/SymmetryPerspective.js";
import { ToolsModel } from "../editor/ToolsModel.js";
import { AbstractSymmetryPerspective } from "./AbstractSymmetryPerspective.js";
import { DefaultFieldApplication } from "./DefaultFieldApplication.js";
import { IcosahedralSymmetryPerspective } from "./IcosahedralSymmetryPerspective.js";
import { AntiprismSymmetry } from "../math/symmetry/AntiprismSymmetry.js";
import { IcosahedralSymmetry } from "../math/symmetry/IcosahedralSymmetry.js";
import { QuaternionicSymmetry } from "../math/symmetry/QuaternionicSymmetry.js";
import { WythoffConstruction } from "../math/symmetry/WythoffConstruction.js";
import { AxialStretchTool } from "../tools/AxialStretchTool.js";
import { AxialSymmetryToolFactory } from "../tools/AxialSymmetryToolFactory.js";
import { IcosahedralToolFactory } from "../tools/IcosahedralToolFactory.js";
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
import { AntiprismShapes } from "../viewing/AntiprismShapes.js";
import { OctahedralShapes } from "../viewing/OctahedralShapes.js";

/**
 * Everything here is stateless, or at worst, a cache (like Shapes).
 * An instance of this can be shared by many DocumentModels.
 * This is why it does not have tool factories, though it does
 * dictate what tool factories will be present.
 * 
 * @author David Hall
 * @param {PolygonField} field
 * @class
 * @extends DefaultFieldApplication
 */
export class PolygonFieldApplication extends DefaultFieldApplication {
    public constructor(field: PolygonField) {
        super(field);
        this.symmetryPerspectives = <any>(new java.util.ArrayList<any>());
        if (this.icosahedralPerspective === undefined) { this.icosahedralPerspective = null; }
        if (this.H4 === undefined) { this.H4 = null; }
        this.h4Builder = null;
        this.symmetryPerspectives.add(new PolygonFieldApplication.AntiprismSymmetryPerspective(this));
        if (field.polygonSides() === 5){
            this.icosahedralPerspective = new IcosahedralSymmetryPerspective(this.getField());
            this.symmetryPerspectives.add(this.icosahedralPerspective);
            this.H4 = new QuaternionicSymmetry("H_4", "com/vzome/core/math/symmetry/H4roots.vef", this.getField());
        } else {
            this.icosahedralPerspective = null;
            this.H4 = null;
        }
        this.symmetryPerspectives.add(super.getDefaultSymmetryPerspective());
    }

    /**
     * 
     * @return {PolygonField}
     */
    public getField(): PolygonField {
        return <PolygonField><any>super.getField();
    }

    symmetryPerspectives: java.util.ArrayList<SymmetryPerspective>;

    /**
     * 
     * @return {*}
     */
    public getSymmetryPerspectives(): java.util.Collection<SymmetryPerspective> {
        return this.symmetryPerspectives;
    }

    /*private*/ icosahedralPerspective: IcosahedralSymmetryPerspective;

    /**
     * 
     * @return {*}
     */
    public getDefaultSymmetryPerspective(): SymmetryPerspective {
        return (this.icosahedralPerspective == null) ? this.symmetryPerspectives.get(0) : this.icosahedralPerspective;
    }

    /**
     * 
     * @param {string} symmName
     * @return {*}
     */
    public getSymmetryPerspective(symmName: string): SymmetryPerspective {
        for(let index=this.symmetryPerspectives.iterator();index.hasNext();) {
            let sp = index.next();
            {
                if (sp.getName() === symmName){
                    return sp;
                }
            }
        }
        return super.getSymmetryPerspective(symmName);
    }

    /**
     * 
     * @param {*} toolFactories
     * @param {ToolsModel} tools
     */
    public registerToolFactories(toolFactories: java.util.Map<string, Tool.Factory>, tools: ToolsModel) {
        super.registerToolFactories(toolFactories, tools);
        if (this.icosahedralPerspective != null){
            const symm: IcosahedralSymmetry = this.icosahedralPerspective.getSymmetry();
            toolFactories.put("AxialStretchTool", new AxialStretchTool.Factory(tools, symm, false, false, false));
            toolFactories.put("SymmetryTool", new IcosahedralToolFactory(tools, symm));
        }
    }

    /*private*/ H4: QuaternionicSymmetry;

    /**
     * 
     * @param {string} name
     * @return {QuaternionicSymmetry}
     */
    public getQuaternionSymmetry(name: string): QuaternionicSymmetry {
        switch((name)) {
        case "H_4":
            return this.H4;
        default:
            return null;
        }
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
}
PolygonFieldApplication["__class"] = "com.vzome.core.kinds.PolygonFieldApplication";
PolygonFieldApplication["__interfaces"] = ["com.vzome.core.math.symmetry.Symmetries4D","com.vzome.core.editor.FieldApplication"];



export namespace PolygonFieldApplication {

    export class AntiprismSymmetryPerspective extends AbstractSymmetryPerspective {
        public __parent: any;
        constructor(__parent: any) {
            super(new AntiprismSymmetry(__parent.getField()).createStandardOrbits("blue"));
            this.__parent = __parent;
            this.axialsymm = new CommandAxialSymmetry(this.symmetry);
            const thinAntiprismShapes: AbstractShapes = new AntiprismShapes("thin", "thin antiprism", this.getSymmetry());
            const antiprismShapes: AbstractShapes = new AntiprismShapes("antiprism", "antiprism", this.getSymmetry());
            const octahedralShapes: AbstractShapes = new OctahedralShapes("octahedral", "octahedral", this.symmetry);
            this.setDefaultGeometry(thinAntiprismShapes);
            this.addShapes(antiprismShapes);
            this.addShapes(octahedralShapes);
        }

        /**
         * 
         * @return {string}
         */
        public getLabel(): string {
            return "antiprism";
        }

        /**
         * 
         * @return {AntiprismSymmetry}
         */
        public getSymmetry(): AntiprismSymmetry {
            return <AntiprismSymmetry><any>super.getSymmetry();
        }

        /**
         * 
         * @param {Tool.Kind} kind
         * @param {ToolsModel} tools
         * @return {*}
         */
        public createToolFactories(kind: Tool.Kind, tools: ToolsModel): java.util.List<Tool.Factory> {
            const isTrivial: boolean = this.symmetry.isTrivial();
            const result: java.util.List<Tool.Factory> = <any>(new java.util.ArrayList<any>());
            switch((kind)) {
            case Tool.Kind.SYMMETRY:
                result.add(new SymmetryToolFactory(tools, this.symmetry));
                if (isTrivial){
                    result.add(new InversionToolFactory(tools));
                }
                result.add(new LineReflectionToolFactory(tools));
                result.add(new MirrorToolFactory(tools));
                result.add(new AxialSymmetryToolFactory(tools, this.symmetry));
                break;
            case Tool.Kind.TRANSFORM:
                result.add(new ScalingToolFactory(tools, this.symmetry));
                result.add(new RotationToolFactory(tools, this.symmetry));
                result.add(new TranslationToolFactory(tools));
                if (isTrivial){
                    result.add(new ProjectionToolFactory(tools));
                }
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
                result.add(new SymmetryToolFactory(tools, this.symmetry).createPredefinedTool("polygonal antiprism around origin"));
                result.add(new MirrorToolFactory(tools).createPredefinedTool("reflection through XY plane"));
                result.add(new AxialSymmetryToolFactory(tools, this.symmetry, true).createPredefinedTool("symmetry around red through origin"));
                break;
            case Tool.Kind.TRANSFORM:
                result.add(new ScalingToolFactory(tools, this.symmetry).createPredefinedTool("scale down"));
                result.add(new ScalingToolFactory(tools, this.symmetry).createPredefinedTool("scale up"));
                result.add(new RotationToolFactory(tools, this.symmetry, true).createPredefinedTool("rotate around red through origin"));
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
            return "org/vorthmann/zome/app/antiprism-trackball-template.vZome";
        }
    }
    AntiprismSymmetryPerspective["__class"] = "com.vzome.core.kinds.PolygonFieldApplication.AntiprismSymmetryPerspective";
    AntiprismSymmetryPerspective["__interfaces"] = ["com.vzome.core.editor.SymmetryPerspective"];


}
