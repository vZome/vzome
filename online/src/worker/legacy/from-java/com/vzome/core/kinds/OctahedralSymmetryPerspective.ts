import { java, javaemul } from "../../../../../candies/j4ts-2.1.0-SNAPSHOT/bundle.js";
import { Tool } from "../../api/Tool.js";
import { AlgebraicField } from "../algebra/AlgebraicField.js";
import { ToolsModel } from "../editor/ToolsModel.js";
import { AbstractSymmetryPerspective } from "./AbstractSymmetryPerspective.js";
import { OctahedralSymmetry } from "../math/symmetry/OctahedralSymmetry.js";
import { AxialSymmetryToolFactory } from "../tools/AxialSymmetryToolFactory.js";
import { InversionToolFactory } from "../tools/InversionToolFactory.js";
import { LineReflectionToolFactory } from "../tools/LineReflectionToolFactory.js";
import { LinearMapToolFactory } from "../tools/LinearMapToolFactory.js";
import { MirrorToolFactory } from "../tools/MirrorToolFactory.js";
import { OctahedralToolFactory } from "../tools/OctahedralToolFactory.js";
import { PerspectiveProjectionToolFactory } from "../tools/PerspectiveProjectionToolFactory.js";
import { ProjectionToolFactory } from "../tools/ProjectionToolFactory.js";
import { RotationToolFactory } from "../tools/RotationToolFactory.js";
import { ScalingToolFactory } from "../tools/ScalingToolFactory.js";
import { TetrahedralToolFactory } from "../tools/TetrahedralToolFactory.js";
import { TranslationToolFactory } from "../tools/TranslationToolFactory.js";
import { OctahedralShapes } from "../viewing/OctahedralShapes.js";

export class OctahedralSymmetryPerspective extends AbstractSymmetryPerspective {
    public constructor(field: AlgebraicField) {
        super(new OctahedralSymmetry(field));
        this.modelResourcePath = "org/vorthmann/zome/app/octahedral-vef.vZome";
        this.setDefaultGeometry(new OctahedralShapes("octahedral", "octahedra", this.symmetry));
    }

    /**
     * 
     * @return {OctahedralSymmetry}
     */
    public getSymmetry(): OctahedralSymmetry {
        return <OctahedralSymmetry><any>this.symmetry;
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
            result.add(new OctahedralToolFactory(tools, this.symmetry));
            result.add(new TetrahedralToolFactory(tools, this.symmetry));
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
            result.add(new PerspectiveProjectionToolFactory(tools));
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
            result.add(new OctahedralToolFactory(tools, this.symmetry).createPredefinedTool("octahedral around origin"));
            result.add(new TetrahedralToolFactory(tools, this.symmetry).createPredefinedTool("tetrahedral around origin"));
            result.add(new InversionToolFactory(tools).createPredefinedTool("reflection through origin"));
            result.add(new MirrorToolFactory(tools).createPredefinedTool("reflection through XY plane"));
            result.add(new MirrorToolFactory(tools).createPredefinedTool("reflection through X=Y green plane"));
            result.add(new AxialSymmetryToolFactory(tools, this.symmetry).createPredefinedTool("symmetry around green through origin"));
            break;
        case Tool.Kind.TRANSFORM:
            result.add(new ScalingToolFactory(tools, this.symmetry).createPredefinedTool("scale down"));
            result.add(new ScalingToolFactory(tools, this.symmetry).createPredefinedTool("scale up"));
            result.add(new RotationToolFactory(tools, this.symmetry, true).createPredefinedTool("rotate around green through origin"));
            result.add(new TranslationToolFactory(tools).createPredefinedTool("b1 move along +X"));
            break;
        default:
            break;
        }
        return result;
    }

    /**
     * 
     * @return {string}
     */
    public getModelResourcePath(): string {
        return this.modelResourcePath;
    }

    /*private*/ modelResourcePath: string;

    public setModelResourcePath(resourcePath: string) {
        this.modelResourcePath = resourcePath;
    }
}
OctahedralSymmetryPerspective["__class"] = "com.vzome.core.kinds.OctahedralSymmetryPerspective";
OctahedralSymmetryPerspective["__interfaces"] = ["com.vzome.core.editor.SymmetryPerspective"];
