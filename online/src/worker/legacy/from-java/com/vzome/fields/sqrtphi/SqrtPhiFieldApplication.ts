import { java, javaemul } from "../../../../../candies/j4ts-2.1.0-SNAPSHOT/bundle.js";
import { Tool } from "../../api/Tool.js";
import { AlgebraicField } from "../../core/algebra/AlgebraicField.js";
import { AlgebraicNumber } from "../../core/algebra/AlgebraicNumber.js";
import { AlgebraicVector } from "../../core/algebra/AlgebraicVector.js";
import { Command } from "../../core/commands/Command.js";
import { CommandAxialSymmetry } from "../../core/commands/CommandAxialSymmetry.js";
import { CommandUniformH4Polytope } from "../../core/commands/CommandUniformH4Polytope.js";
import { SymmetryPerspective } from "../../core/editor/SymmetryPerspective.js";
import { ToolsModel } from "../../core/editor/ToolsModel.js";
import { AbstractSymmetryPerspective } from "../../core/kinds/AbstractSymmetryPerspective.js";
import { DefaultFieldApplication } from "../../core/kinds/DefaultFieldApplication.js";
import { IcosahedralSymmetryPerspective } from "../../core/kinds/IcosahedralSymmetryPerspective.js";
import { OctahedralSymmetryPerspective } from "../../core/kinds/OctahedralSymmetryPerspective.js";
import { IcosahedralSymmetry } from "../../core/math/symmetry/IcosahedralSymmetry.js";
import { OctahedralSymmetry } from "../../core/math/symmetry/OctahedralSymmetry.js";
import { QuaternionicSymmetry } from "../../core/math/symmetry/QuaternionicSymmetry.js";
import { Symmetry } from "../../core/math/symmetry/Symmetry.js";
import { WythoffConstruction } from "../../core/math/symmetry/WythoffConstruction.js";
import { AxialSymmetryToolFactory } from "../../core/tools/AxialSymmetryToolFactory.js";
import { InversionToolFactory } from "../../core/tools/InversionToolFactory.js";
import { LineReflectionToolFactory } from "../../core/tools/LineReflectionToolFactory.js";
import { LinearMapToolFactory } from "../../core/tools/LinearMapToolFactory.js";
import { MirrorToolFactory } from "../../core/tools/MirrorToolFactory.js";
import { ProjectionToolFactory } from "../../core/tools/ProjectionToolFactory.js";
import { RotationToolFactory } from "../../core/tools/RotationToolFactory.js";
import { ScalingToolFactory } from "../../core/tools/ScalingToolFactory.js";
import { SymmetryToolFactory } from "../../core/tools/SymmetryToolFactory.js";
import { TranslationToolFactory } from "../../core/tools/TranslationToolFactory.js";
import { AbstractShapes } from "../../core/viewing/AbstractShapes.js";
import { ExportedVEFShapes } from "../../core/viewing/ExportedVEFShapes.js";
import { OctahedralShapes } from "../../core/viewing/OctahedralShapes.js";
import { PentagonalAntiprismSymmetry } from "./PentagonalAntiprismSymmetry.js";

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
export class SqrtPhiFieldApplication extends DefaultFieldApplication {
    public constructor(field: AlgebraicField) {
        super(field);
        this.icosahedralPerspective = new SqrtPhiFieldApplication.SqrtPhiFieldApplication$0(this, new IcosahedralSymmetry(this.getField()));
        this.pentagonalPerspective = new SqrtPhiFieldApplication.SqrtPhiFieldApplication$1(this, new PentagonalAntiprismSymmetry(this.getField(), null));
        this.H4 = new QuaternionicSymmetry("H_4", "com/vzome/core/math/symmetry/H4roots.vef", this.getField());
        this.h4Builder = null;
        const octahedralPerspective: OctahedralSymmetryPerspective = <OctahedralSymmetryPerspective><any>super.getDefaultSymmetryPerspective();
        const symm: OctahedralSymmetry = octahedralPerspective.getSymmetry();
        const scale: AlgebraicNumber = field['createPower$int'](6);
        symm.getDirection("blue").setUnitLength(scale);
        symm.getDirection("green").setUnitLength(scale);
        symm.getDirection("yellow").setUnitLength(scale);
        let x: AlgebraicNumber = field['createAlgebraicNumber$int_A']([0, -1, 0, 0]);
        let y: AlgebraicNumber = field['createAlgebraicNumber$int_A']([-1, 0, 0, 0]);
        let z: AlgebraicNumber = field.zero();
        const unitLength: AlgebraicNumber = field['createPower$int'](4);
        let norm: AlgebraicVector = new AlgebraicVector(x, y, z);
        symm.createZoneOrbit$java_lang_String$int$int$com_vzome_core_algebra_AlgebraicVector$boolean$boolean$com_vzome_core_algebra_AlgebraicNumber("slate", 0, Symmetry.NO_ROTATION, norm, true, false, unitLength);
        x = field['createAlgebraicNumber$int_A']([0, 1, 0, -1]);
        y = field.one();
        z = field.one();
        norm = new AlgebraicVector(x, y, z);
        symm.createZoneOrbit$java_lang_String$int$int$com_vzome_core_algebra_AlgebraicVector$boolean$boolean$com_vzome_core_algebra_AlgebraicNumber("mauve", 0, Symmetry.NO_ROTATION, norm, true, false, unitLength);
        x = field['createAlgebraicNumber$int_A']([1, 0, -1, 0]);
        y = field['createAlgebraicNumber$int_A']([0, -1, 0, 0]);
        z = field['createAlgebraicNumber$int_A']([0, -1, 0, 1]);
        norm = new AlgebraicVector(x, y, z);
        symm.createZoneOrbit$java_lang_String$int$int$com_vzome_core_algebra_AlgebraicVector$boolean$boolean$com_vzome_core_algebra_AlgebraicNumber("ivory", 0, Symmetry.NO_ROTATION, norm, true, false, unitLength);
        const defaultShapes: AbstractShapes = new OctahedralShapes("octahedral", "octahedra", symm);
        octahedralPerspective.setDefaultGeometry(defaultShapes);
    }

    /**
     * 
     * @return {string}
     */
    public getLabel(): string {
        return "\u221a\u03c6";
    }

    /*private*/ icosahedralPerspective: IcosahedralSymmetryPerspective;

    /*private*/ pentagonalPerspective: SymmetryPerspective;

    /*private*/ H4: QuaternionicSymmetry;

    /**
     * 
     * @return {*}
     */
    public getSymmetryPerspectives(): java.util.Collection<SymmetryPerspective> {
        return java.util.Arrays.asList<any>(this.pentagonalPerspective, super.getDefaultSymmetryPerspective(), this.icosahedralPerspective);
    }

    /**
     * 
     * @return {*}
     */
    public getDefaultSymmetryPerspective(): SymmetryPerspective {
        return this.pentagonalPerspective;
    }

    /**
     * 
     * @param {string} symmName
     * @return {*}
     */
    public getSymmetryPerspective(symmName: string): SymmetryPerspective {
        switch((symmName)) {
        case "pentagonal":
            return this.pentagonalPerspective;
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
SqrtPhiFieldApplication["__class"] = "com.vzome.fields.sqrtphi.SqrtPhiFieldApplication";
SqrtPhiFieldApplication["__interfaces"] = ["com.vzome.core.math.symmetry.Symmetries4D","com.vzome.core.editor.FieldApplication"];



export namespace SqrtPhiFieldApplication {

    export class SqrtPhiFieldApplication$0 extends IcosahedralSymmetryPerspective {
        public __parent: any;
        constructor(__parent: any, __arg0: any) {
            super(__arg0);
            this.__parent = __parent;
            (() => {
                const icosaSymm: IcosahedralSymmetry = this.getSymmetry();
                const tinyIcosaShapes: AbstractShapes = new ExportedVEFShapes(null, "sqrtPhi/tinyIcosahedra", "tiny icosahedra", null, icosaSymm);
                const icosahedralShapes: AbstractShapes = new ExportedVEFShapes(null, "sqrtPhi/zome", "solid Zome", icosaSymm, tinyIcosaShapes);
                this.clearShapes();
                this.addShapes(icosahedralShapes);
                this.setDefaultGeometry(tinyIcosaShapes);
            })();
        }
    }
    SqrtPhiFieldApplication$0["__interfaces"] = ["com.vzome.core.editor.SymmetryPerspective"];



    export class SqrtPhiFieldApplication$1 extends AbstractSymmetryPerspective {
        public __parent: any;
        /**
         * 
         * @return {PentagonalAntiprismSymmetry}
         */
        public getSymmetry(): PentagonalAntiprismSymmetry {
            return <PentagonalAntiprismSymmetry><any>super.getSymmetry();
        }

        /**
         * 
         * @param {Tool.Kind} kind
         * @param {ToolsModel} tools
         * @return {*}
         */
        public createToolFactories(kind: Tool.Kind, tools: ToolsModel): java.util.List<Tool.Factory> {
            const result: java.util.List<Tool.Factory> = <any>(new java.util.ArrayList<any>());
            const pentaSymm: PentagonalAntiprismSymmetry = this.getSymmetry();
            switch((kind)) {
            case Tool.Kind.SYMMETRY:
                result.add(new SymmetryToolFactory(tools, pentaSymm));
                result.add(new InversionToolFactory(tools));
                result.add(new LineReflectionToolFactory(tools));
                result.add(new MirrorToolFactory(tools));
                result.add(new AxialSymmetryToolFactory(tools, pentaSymm));
                break;
            case Tool.Kind.TRANSFORM:
                result.add(new ScalingToolFactory(tools, pentaSymm));
                result.add(new RotationToolFactory(tools, pentaSymm));
                result.add(new TranslationToolFactory(tools));
                result.add(new ProjectionToolFactory(tools));
                break;
            case Tool.Kind.LINEAR_MAP:
                result.add(new LinearMapToolFactory(tools, pentaSymm, false));
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
            const pentaSymm: PentagonalAntiprismSymmetry = this.getSymmetry();
            switch((kind)) {
            case Tool.Kind.SYMMETRY:
                result.add(new SymmetryToolFactory(tools, pentaSymm).createPredefinedTool("pentagonal antiprism around origin"));
                result.add(new AxialSymmetryToolFactory(tools, pentaSymm).createPredefinedTool("fivefold symmetry through origin"));
                result.add(new MirrorToolFactory(tools).createPredefinedTool("reflection through red plane"));
                break;
            case Tool.Kind.TRANSFORM:
                result.add(new ScalingToolFactory(tools, pentaSymm).createPredefinedTool("scale down"));
                result.add(new ScalingToolFactory(tools, pentaSymm).createPredefinedTool("scale up"));
                result.add(new RotationToolFactory(tools, pentaSymm, false).createPredefinedTool("fivefold rotation through origin"));
                result.add(new RotationToolFactory(tools, pentaSymm, true).createPredefinedTool("fivefold rotation through origin"));
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
            return "org/vorthmann/zome/app/pentagonal.vZome";
        }

        constructor(__parent: any, __arg0: any) {
            super(__arg0);
            this.__parent = __parent;
            (() => {
                const pentaSymm: PentagonalAntiprismSymmetry = this.getSymmetry();
                pentaSymm.createStandardOrbits("blue");
                const octahedralShapes: AbstractShapes = new OctahedralShapes("octahedral", "octahedra", pentaSymm);
                const kostickShapes: AbstractShapes = new ExportedVEFShapes(null, "sqrtPhi/fivefold", "Kostick", pentaSymm, octahedralShapes);
                this.setDefaultGeometry(kostickShapes);
                this.addShapes(octahedralShapes);
                this.axialsymm = new CommandAxialSymmetry(pentaSymm);
            })();
            if (this.axialsymm === undefined) { this.axialsymm = null; }
        }
    }
    SqrtPhiFieldApplication$1["__interfaces"] = ["com.vzome.core.editor.SymmetryPerspective"];


}
