import { java, javaemul } from "../../../../../candies/j4ts-2.1.0-SNAPSHOT/bundle.js";
import { Tool } from "../../api/Tool.js";
import { AlgebraicField } from "../algebra/AlgebraicField.js";
import { Command } from "../commands/Command.js";
import { CommandAxialSymmetry } from "../commands/CommandAxialSymmetry.js";
import { CommandQuaternionSymmetry } from "../commands/CommandQuaternionSymmetry.js";
import { CommandSymmetry } from "../commands/CommandSymmetry.js";
import { CommandTetrahedralSymmetry } from "../commands/CommandTetrahedralSymmetry.js";
import { CommandVanOss600Cell } from "../commands/CommandVanOss600Cell.js";
import { ToolsModel } from "../editor/ToolsModel.js";
import { AbstractSymmetryPerspective } from "./AbstractSymmetryPerspective.js";
import { IcosahedralSymmetry } from "../math/symmetry/IcosahedralSymmetry.js";
import { QuaternionicSymmetry } from "../math/symmetry/QuaternionicSymmetry.js";
import { AxialStretchTool } from "../tools/AxialStretchTool.js";
import { AxialSymmetryToolFactory } from "../tools/AxialSymmetryToolFactory.js";
import { IcosahedralToolFactory } from "../tools/IcosahedralToolFactory.js";
import { InversionToolFactory } from "../tools/InversionToolFactory.js";
import { LineReflectionToolFactory } from "../tools/LineReflectionToolFactory.js";
import { LinearMapToolFactory } from "../tools/LinearMapToolFactory.js";
import { MirrorToolFactory } from "../tools/MirrorToolFactory.js";
import { PerspectiveProjectionToolFactory } from "../tools/PerspectiveProjectionToolFactory.js";
import { ProjectionToolFactory } from "../tools/ProjectionToolFactory.js";
import { RotationToolFactory } from "../tools/RotationToolFactory.js";
import { ScalingToolFactory } from "../tools/ScalingToolFactory.js";
import { TetrahedralToolFactory } from "../tools/TetrahedralToolFactory.js";
import { TranslationToolFactory } from "../tools/TranslationToolFactory.js";
import { AbstractShapes } from "../viewing/AbstractShapes.js";
import { ExportedVEFShapes } from "../viewing/ExportedVEFShapes.js";

export class IcosahedralSymmetryPerspective extends AbstractSymmetryPerspective {
    /*private*/ qSymmH4: QuaternionicSymmetry;

    /*private*/ qSymmH4_ROT: QuaternionicSymmetry;

    /*private*/ qSymmT2: QuaternionicSymmetry;

    /*private*/ cmdIcosasymm: Command;

    /*private*/ cmdTetrasymm: Command;

    /*private*/ cmdAxialsymm: Command;

    /*private*/ cmdH4symmetry: Command;

    /*private*/ cmdH4rotations: Command;

    /*private*/ cmdIxTsymmetry: Command;

    /*private*/ cmdTxTsymmetry: Command;

    /*private*/ cmdVanOss600cell: Command;

    public constructor(af?: any) {
        if (((af != null && (af.constructor != null && af.constructor["__interfaces"] != null && af.constructor["__interfaces"].indexOf("com.vzome.core.algebra.AlgebraicField") >= 0)) || af === null)) {
            let __args = arguments;
            {
                let __args = arguments;
                let symm: any = new IcosahedralSymmetry(af);
                super(symm);
                if (this.qSymmH4 === undefined) { this.qSymmH4 = null; } 
                if (this.qSymmH4_ROT === undefined) { this.qSymmH4_ROT = null; } 
                if (this.qSymmT2 === undefined) { this.qSymmT2 = null; } 
                if (this.cmdIcosasymm === undefined) { this.cmdIcosasymm = null; } 
                if (this.cmdTetrasymm === undefined) { this.cmdTetrasymm = null; } 
                if (this.cmdAxialsymm === undefined) { this.cmdAxialsymm = null; } 
                if (this.cmdH4symmetry === undefined) { this.cmdH4symmetry = null; } 
                if (this.cmdH4rotations === undefined) { this.cmdH4rotations = null; } 
                if (this.cmdIxTsymmetry === undefined) { this.cmdIxTsymmetry = null; } 
                if (this.cmdTxTsymmetry === undefined) { this.cmdTxTsymmetry = null; } 
                if (this.cmdVanOss600cell === undefined) { this.cmdVanOss600cell = null; } 
                const icosadefaultShapes: AbstractShapes = new ExportedVEFShapes(null, "default", "solid connectors", this.symmetry);
                const printableShapes: AbstractShapes = new ExportedVEFShapes(null, "printable", "printable", this.symmetry, icosadefaultShapes);
                const lifelikeShapes: AbstractShapes = new ExportedVEFShapes(null, "lifelike", "lifelike", this.symmetry, icosadefaultShapes);
                const tinyShapes: AbstractShapes = new ExportedVEFShapes(null, "tiny", "tiny connectors", this.symmetry);
                const tinyDodecs: AbstractShapes = new ExportedVEFShapes(null, "dodecs", "small dodecahedra", "tiny dodecahedra", this.symmetry, tinyShapes);
                const bigZome: AbstractShapes = new ExportedVEFShapes(null, "bigzome", "Big Zome", this.symmetry, tinyShapes);
                const noTwist: AbstractShapes = new ExportedVEFShapes(null, "noTwist", "no-twist 121 zone", null, this.symmetry);
                const vienne2: AbstractShapes = new ExportedVEFShapes(null, "vienne2", "Vienne", this.symmetry, icosadefaultShapes);
                const vienne3: AbstractShapes = new ExportedVEFShapes(null, "vienne3", "Vienne lifelike", this.symmetry, vienne2);
                const vienne: AbstractShapes = new ExportedVEFShapes(null, "vienne", "Vienne 121 zone", null, this.symmetry);
                const dimtoolShapes: AbstractShapes = new ExportedVEFShapes(null, "dimtool", "dimtool", this.symmetry, icosadefaultShapes);
                this.setDefaultGeometry(printableShapes);
                this.addShapes(icosadefaultShapes);
                this.addShapes(lifelikeShapes);
                this.addShapes(tinyShapes);
                this.addShapes(tinyDodecs);
                this.addShapes(bigZome);
                this.addShapes(noTwist);
                this.addShapes(vienne2);
                this.addShapes(vienne3);
                this.addShapes(vienne);
                this.addShapes(dimtoolShapes);
                const field: AlgebraicField = this.symmetry.getField();
                this.qSymmH4 = new QuaternionicSymmetry("H_4", "com/vzome/core/math/symmetry/H4roots.vef", field);
                this.qSymmH4_ROT = new QuaternionicSymmetry("H4_ROT", "com/vzome/core/math/symmetry/H4roots-rotationalSubgroup.vef", field);
                this.qSymmT2 = new QuaternionicSymmetry("2T", "com/vzome/core/math/symmetry/binaryTetrahedralGroup.vef", field);
                this.cmdIcosasymm = new CommandSymmetry(this.symmetry);
                this.cmdTetrasymm = new CommandTetrahedralSymmetry(this.symmetry);
                this.cmdAxialsymm = new CommandAxialSymmetry(this.symmetry);
                this.cmdH4symmetry = new CommandQuaternionSymmetry(this.qSymmH4, this.qSymmH4);
                this.cmdH4rotations = new CommandQuaternionSymmetry(this.qSymmH4_ROT, this.qSymmH4_ROT);
                this.cmdIxTsymmetry = new CommandQuaternionSymmetry(this.qSymmH4, this.qSymmT2);
                this.cmdTxTsymmetry = new CommandQuaternionSymmetry(this.qSymmT2, this.qSymmT2);
                this.cmdVanOss600cell = new CommandVanOss600Cell();
            }
        } else if (((af != null && af instanceof <any>IcosahedralSymmetry) || af === null)) {
            let __args = arguments;
            let symm: any = __args[0];
            super(symm);
            if (this.qSymmH4 === undefined) { this.qSymmH4 = null; } 
            if (this.qSymmH4_ROT === undefined) { this.qSymmH4_ROT = null; } 
            if (this.qSymmT2 === undefined) { this.qSymmT2 = null; } 
            if (this.cmdIcosasymm === undefined) { this.cmdIcosasymm = null; } 
            if (this.cmdTetrasymm === undefined) { this.cmdTetrasymm = null; } 
            if (this.cmdAxialsymm === undefined) { this.cmdAxialsymm = null; } 
            if (this.cmdH4symmetry === undefined) { this.cmdH4symmetry = null; } 
            if (this.cmdH4rotations === undefined) { this.cmdH4rotations = null; } 
            if (this.cmdIxTsymmetry === undefined) { this.cmdIxTsymmetry = null; } 
            if (this.cmdTxTsymmetry === undefined) { this.cmdTxTsymmetry = null; } 
            if (this.cmdVanOss600cell === undefined) { this.cmdVanOss600cell = null; } 
            const icosadefaultShapes: AbstractShapes = new ExportedVEFShapes(null, "default", "solid connectors", this.symmetry);
            const printableShapes: AbstractShapes = new ExportedVEFShapes(null, "printable", "printable", this.symmetry, icosadefaultShapes);
            const lifelikeShapes: AbstractShapes = new ExportedVEFShapes(null, "lifelike", "lifelike", this.symmetry, icosadefaultShapes);
            const tinyShapes: AbstractShapes = new ExportedVEFShapes(null, "tiny", "tiny connectors", this.symmetry);
            const tinyDodecs: AbstractShapes = new ExportedVEFShapes(null, "dodecs", "small dodecahedra", "tiny dodecahedra", this.symmetry, tinyShapes);
            const bigZome: AbstractShapes = new ExportedVEFShapes(null, "bigzome", "Big Zome", this.symmetry, tinyShapes);
            const noTwist: AbstractShapes = new ExportedVEFShapes(null, "noTwist", "no-twist 121 zone", null, this.symmetry);
            const vienne2: AbstractShapes = new ExportedVEFShapes(null, "vienne2", "Vienne", this.symmetry, icosadefaultShapes);
            const vienne3: AbstractShapes = new ExportedVEFShapes(null, "vienne3", "Vienne lifelike", this.symmetry, vienne2);
            const vienne: AbstractShapes = new ExportedVEFShapes(null, "vienne", "Vienne 121 zone", null, this.symmetry);
            const dimtoolShapes: AbstractShapes = new ExportedVEFShapes(null, "dimtool", "dimtool", this.symmetry, icosadefaultShapes);
            this.setDefaultGeometry(printableShapes);
            this.addShapes(icosadefaultShapes);
            this.addShapes(lifelikeShapes);
            this.addShapes(tinyShapes);
            this.addShapes(tinyDodecs);
            this.addShapes(bigZome);
            this.addShapes(noTwist);
            this.addShapes(vienne2);
            this.addShapes(vienne3);
            this.addShapes(vienne);
            this.addShapes(dimtoolShapes);
            const field: AlgebraicField = this.symmetry.getField();
            this.qSymmH4 = new QuaternionicSymmetry("H_4", "com/vzome/core/math/symmetry/H4roots.vef", field);
            this.qSymmH4_ROT = new QuaternionicSymmetry("H4_ROT", "com/vzome/core/math/symmetry/H4roots-rotationalSubgroup.vef", field);
            this.qSymmT2 = new QuaternionicSymmetry("2T", "com/vzome/core/math/symmetry/binaryTetrahedralGroup.vef", field);
            this.cmdIcosasymm = new CommandSymmetry(this.symmetry);
            this.cmdTetrasymm = new CommandTetrahedralSymmetry(this.symmetry);
            this.cmdAxialsymm = new CommandAxialSymmetry(this.symmetry);
            this.cmdH4symmetry = new CommandQuaternionSymmetry(this.qSymmH4, this.qSymmH4);
            this.cmdH4rotations = new CommandQuaternionSymmetry(this.qSymmH4_ROT, this.qSymmH4_ROT);
            this.cmdIxTsymmetry = new CommandQuaternionSymmetry(this.qSymmH4, this.qSymmT2);
            this.cmdTxTsymmetry = new CommandQuaternionSymmetry(this.qSymmT2, this.qSymmT2);
            this.cmdVanOss600cell = new CommandVanOss600Cell();
        } else throw new Error('invalid overload');
    }

    /**
     * 
     * @return {IcosahedralSymmetry}
     */
    public getSymmetry(): IcosahedralSymmetry {
        return <IcosahedralSymmetry><any>this.symmetry;
    }

    /**
     * 
     * @param {Tool.Kind} kind
     * @param {ToolsModel} tools
     * @return {*}
     */
    public createToolFactories(kind: Tool.Kind, tools: ToolsModel): java.util.List<Tool.Factory> {
        const result: java.util.List<Tool.Factory> = <any>(new java.util.ArrayList<any>());
        const icosaSymm: IcosahedralSymmetry = this.getSymmetry();
        switch((kind)) {
        case Tool.Kind.SYMMETRY:
            result.add(new IcosahedralToolFactory(tools, icosaSymm));
            result.add(new TetrahedralToolFactory(tools, icosaSymm));
            result.add(new InversionToolFactory(tools));
            result.add(new LineReflectionToolFactory(tools));
            result.add(new MirrorToolFactory(tools));
            result.add(new AxialSymmetryToolFactory(tools, icosaSymm));
            break;
        case Tool.Kind.TRANSFORM:
            result.add(new ScalingToolFactory(tools, icosaSymm));
            result.add(new RotationToolFactory(tools, icosaSymm));
            result.add(new TranslationToolFactory(tools));
            result.add(new ProjectionToolFactory(tools));
            result.add(new PerspectiveProjectionToolFactory(tools));
            break;
        case Tool.Kind.LINEAR_MAP:
            result.add(new AxialStretchTool.Factory(tools, icosaSymm, true, true, true));
            result.add(new AxialStretchTool.Factory(tools, icosaSymm, true, false, true));
            result.add(new AxialStretchTool.Factory(tools, icosaSymm, true, true, false));
            result.add(new AxialStretchTool.Factory(tools, icosaSymm, true, false, false));
            result.add(new AxialStretchTool.Factory(tools, icosaSymm, false, true, false));
            result.add(new AxialStretchTool.Factory(tools, icosaSymm, false, false, false));
            result.add(new LinearMapToolFactory(tools, icosaSymm, false));
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
        const icosaSymm: IcosahedralSymmetry = this.getSymmetry();
        switch((kind)) {
        case Tool.Kind.SYMMETRY:
            result.add(new IcosahedralToolFactory(tools, icosaSymm).createPredefinedTool("icosahedral around origin"));
            result.add(new TetrahedralToolFactory(tools, icosaSymm).createPredefinedTool("tetrahedral around origin"));
            result.add(new InversionToolFactory(tools).createPredefinedTool("reflection through origin"));
            result.add(new MirrorToolFactory(tools).createPredefinedTool("reflection through XY plane"));
            result.add(new MirrorToolFactory(tools).createPredefinedTool("reflection through X=Y green plane"));
            result.add(new AxialSymmetryToolFactory(tools, icosaSymm).createPredefinedTool("symmetry around red through origin"));
            break;
        case Tool.Kind.TRANSFORM:
            result.add(new ScalingToolFactory(tools, icosaSymm).createPredefinedTool("scale down"));
            result.add(new ScalingToolFactory(tools, icosaSymm).createPredefinedTool("scale up"));
            result.add(new RotationToolFactory(tools, icosaSymm, true).createPredefinedTool("rotate around red through origin"));
            result.add(new TranslationToolFactory(tools).createPredefinedTool("b1 move along +X"));
            break;
        default:
            break;
        }
        return result;
    }

    /**
     * 
     * @param {string} action
     * @return {*}
     */
    public getLegacyCommand(action: string): Command {
        switch((action)) {
        case "icosasymm":
            return this.cmdIcosasymm;
        case "tetrasymm":
            return this.cmdTetrasymm;
        case "axialsymm":
            return this.cmdAxialsymm;
        case "h4symmetry":
            return this.cmdH4symmetry;
        case "h4rotations":
            return this.cmdH4rotations;
        case "IxTsymmetry":
            return this.cmdIxTsymmetry;
        case "TxTsymmetry":
            return this.cmdTxTsymmetry;
        case "vanOss600cell":
            return this.cmdVanOss600cell;
        default:
            return super.getLegacyCommand(action);
        }
    }

    public getQuaternionSymmetry(name: string): QuaternionicSymmetry {
        switch((name)) {
        case "H_4":
            return this.qSymmH4;
        case "H4_ROT":
            return this.qSymmH4_ROT;
        case "2T":
            return this.qSymmT2;
        default:
            return null;
        }
    }

    /**
     * 
     * @return {string}
     */
    public getModelResourcePath(): string {
        return "org/vorthmann/zome/app/icosahedral-vef.vZome";
    }
}
IcosahedralSymmetryPerspective["__class"] = "com.vzome.core.kinds.IcosahedralSymmetryPerspective";
IcosahedralSymmetryPerspective["__interfaces"] = ["com.vzome.core.editor.SymmetryPerspective"];
