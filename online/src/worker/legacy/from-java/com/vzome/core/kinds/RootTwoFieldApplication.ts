import { java, javaemul } from "../../../../../candies/j4ts-2.1.0-SNAPSHOT/bundle.js";
import { Tool } from "../../api/Tool.js";
import { AlgebraicField } from "../algebra/AlgebraicField.js";
import { AlgebraicNumber } from "../algebra/AlgebraicNumber.js";
import { AlgebraicVector } from "../algebra/AlgebraicVector.js";
import { SymmetryPerspective } from "../editor/SymmetryPerspective.js";
import { ToolsModel } from "../editor/ToolsModel.js";
import { AbstractSymmetryPerspective } from "./AbstractSymmetryPerspective.js";
import { DefaultFieldApplication } from "./DefaultFieldApplication.js";
import { OctahedralSymmetryPerspective } from "./OctahedralSymmetryPerspective.js";
import { AbstractSymmetry } from "../math/symmetry/AbstractSymmetry.js";
import { Direction } from "../math/symmetry/Direction.js";
import { OctahedralSymmetry } from "../math/symmetry/OctahedralSymmetry.js";
import { SpecialOrbit } from "../math/symmetry/SpecialOrbit.js";
import { Symmetry } from "../math/symmetry/Symmetry.js";
import { AxialSymmetryToolFactory } from "../tools/AxialSymmetryToolFactory.js";
import { InversionToolFactory } from "../tools/InversionToolFactory.js";
import { LineReflectionToolFactory } from "../tools/LineReflectionToolFactory.js";
import { LinearMapToolFactory } from "../tools/LinearMapToolFactory.js";
import { MirrorToolFactory } from "../tools/MirrorToolFactory.js";
import { OctahedralToolFactory } from "../tools/OctahedralToolFactory.js";
import { ProjectionToolFactory } from "../tools/ProjectionToolFactory.js";
import { RotationToolFactory } from "../tools/RotationToolFactory.js";
import { ScalingToolFactory } from "../tools/ScalingToolFactory.js";
import { TetrahedralToolFactory } from "../tools/TetrahedralToolFactory.js";
import { TranslationToolFactory } from "../tools/TranslationToolFactory.js";
import { AbstractShapes } from "../viewing/AbstractShapes.js";
import { ExportedVEFShapes } from "../viewing/ExportedVEFShapes.js";
import { SchochShapes } from "../viewing/SchochShapes.js";

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
export class RootTwoFieldApplication extends DefaultFieldApplication {
    public constructor(field: AlgebraicField) {
        super(field);
        this.synestructicsSymmetry = new RootTwoFieldApplication.RootTwoFieldApplication$0(this, this.getField(), "orange");
        this.synestructicsPerspective = new RootTwoFieldApplication.RootTwoFieldApplication$1(this, this.synestructicsSymmetry);
        const octahedralPerspective: OctahedralSymmetryPerspective = <OctahedralSymmetryPerspective><any>super.getDefaultSymmetryPerspective();
        const symmetry: AbstractSymmetry = octahedralPerspective.getSymmetry();
        symmetry.createZoneOrbit$java_lang_String$int$int$int_A_A$boolean("yellow", 0, 4, [[1, 1, 0, 1], [1, 1, 0, 1], [1, 1, 0, 1]], true);
        symmetry.createZoneOrbit$java_lang_String$int$int$int_A_A$boolean("green", 1, 8, [[0, 1, 1, 2], [0, 1, 1, 2], [0, 1, 0, 1]], true);
        symmetry.createZoneOrbit$java_lang_String$int$int$int_A_A$boolean("brown", 0, Symmetry.NO_ROTATION, [[1, 1, 0, 1], [1, 1, 0, 1], [2, 1, 0, 1]], true);
        const defaultShapes: AbstractShapes = new ExportedVEFShapes(null, "rootTwoSmall", "small octahedra", "small connectors", symmetry);
        octahedralPerspective.setDefaultGeometry(defaultShapes);
        octahedralPerspective.addShapes(new ExportedVEFShapes(null, "rootTwoBig", "ornate", symmetry, defaultShapes));
        const rootTwoShapes: AbstractShapes = new SchochShapes(null, "rootTwo", "Schoch solid", symmetry, defaultShapes);
        octahedralPerspective.addShapes(rootTwoShapes);
        octahedralPerspective.addShapes(new SchochShapes(null, "root2Lifelike", "Schoch lifelike", symmetry, rootTwoShapes));
    }

    /**
     * 
     * @return {string}
     */
    public getLabel(): string {
        return "\u221a2";
    }

    /*private*/ synestructicsSymmetry: Symmetry;

    /*private*/ synestructicsPerspective: SymmetryPerspective;

    /**
     * 
     * @return {*}
     */
    public getSymmetryPerspectives(): java.util.Collection<SymmetryPerspective> {
        return java.util.Arrays.asList<any>(super.getDefaultSymmetryPerspective(), this.synestructicsPerspective);
    }

    /**
     * 
     * @param {string} symmName
     * @return {*}
     */
    public getSymmetryPerspective(symmName: string): SymmetryPerspective {
        switch((symmName)) {
        case "synestructics":
            return this.synestructicsPerspective;
        default:
            return super.getSymmetryPerspective(symmName);
        }
    }
}
RootTwoFieldApplication["__class"] = "com.vzome.core.kinds.RootTwoFieldApplication";
RootTwoFieldApplication["__interfaces"] = ["com.vzome.core.math.symmetry.Symmetries4D","com.vzome.core.editor.FieldApplication"];



export namespace RootTwoFieldApplication {

    export class RootTwoFieldApplication$0 extends OctahedralSymmetry {
        public __parent: any;
        /**
         * 
         * @return {string}
         */
        public getName(): string {
            return "synestructics";
        }

        /**
         * 
         * @param {SpecialOrbit} which
         * @return {Direction}
         */
        public getSpecialOrbit(which: SpecialOrbit): Direction {
            switch((which)) {
            case SpecialOrbit.BLUE:
                return this.getDirection(this.frameColor);
            case SpecialOrbit.RED:
                return this.getDirection("magenta");
            case SpecialOrbit.YELLOW:
                return this.getDirection("yellow");
            default:
                return null;
            }
        }

        /**
         * 
         * @return {AlgebraicVector[]}
         */
        public getOrbitTriangle(): AlgebraicVector[] {
            const magentaVertex: AlgebraicVector = this.getDirection("magenta").getPrototype();
            const orangeVertex: AlgebraicVector = this.getDirection(this.frameColor).getPrototype();
            const yellowVertex: AlgebraicVector = this.getDirection("yellow").getPrototype();
            return [magentaVertex, orangeVertex, yellowVertex];
        }

        /**
         * 
         */
        createOtherOrbits() {
            let v: AlgebraicVector = new AlgebraicVector(this.mField.one(), this.mField.one(), this.mField.one());
            this.createZoneOrbit$java_lang_String$int$int$com_vzome_core_algebra_AlgebraicVector$boolean("yellow", 0, 4, v, true);
            const sqrt2: AlgebraicNumber = this.mField['createPower$int'](1);
            const half: AlgebraicNumber = this.mField['createRational$long$long'](1, 2);
            v = new AlgebraicVector(sqrt2, sqrt2, this.mField.zero()).scale(half);
            this.createZoneOrbit$java_lang_String$int$int$com_vzome_core_algebra_AlgebraicVector$boolean("magenta", 1, 8, v, true);
            v = new AlgebraicVector(this.mField.one(), this.mField.one(), this.mField.one()['plus$com_vzome_core_algebra_AlgebraicNumber'](this.mField.one()));
            this.createZoneOrbit$java_lang_String$int$int$com_vzome_core_algebra_AlgebraicVector$boolean("brown", 0, Symmetry.NO_ROTATION, v, true);
        }

        constructor(__parent: any, __arg0: any, __arg1: any) {
            super(__arg0, __arg1);
            this.__parent = __parent;
        }
    }
    RootTwoFieldApplication$0["__interfaces"] = ["com.vzome.core.math.symmetry.Symmetry","com.vzome.core.math.symmetry.Embedding"];



    export class RootTwoFieldApplication$1 extends AbstractSymmetryPerspective {
        public __parent: any;
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
            return "org/vorthmann/zome/app/octahedral-vef.vZome";
        }

        constructor(__parent: any, __arg0: any) {
            super(__arg0);
            this.__parent = __parent;
            (() => {
                const defaultShapes: AbstractShapes = new ExportedVEFShapes(null, "rootTwoSmall", "small octahedra", this.symmetry, null);
                const synestructicsShapes: AbstractShapes = new ExportedVEFShapes(null, "rootTwo", "Synestructics", this.symmetry, defaultShapes);
                const ornateShapes: AbstractShapes = new ExportedVEFShapes(null, "rootTwoBig", "ornate", this.symmetry, defaultShapes);
                this.setDefaultGeometry(defaultShapes);
                this.addShapes(synestructicsShapes);
                this.addShapes(ornateShapes);
            })();
        }
    }
    RootTwoFieldApplication$1["__interfaces"] = ["com.vzome.core.editor.SymmetryPerspective"];


}
