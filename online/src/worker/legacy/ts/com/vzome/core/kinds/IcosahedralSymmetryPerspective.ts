/* Generated from Java with JSweet 3.2.0-SNAPSHOT - http://www.jsweet.org */
namespace com.vzome.core.kinds {
    export class IcosahedralSymmetryPerspective extends com.vzome.core.kinds.AbstractSymmetryPerspective {
        /*private*/ qSymmH4: com.vzome.core.math.symmetry.QuaternionicSymmetry;

        /*private*/ qSymmH4_ROT: com.vzome.core.math.symmetry.QuaternionicSymmetry;

        /*private*/ qSymmT2: com.vzome.core.math.symmetry.QuaternionicSymmetry;

        /*private*/ cmdIcosasymm: com.vzome.core.commands.Command;

        /*private*/ cmdTetrasymm: com.vzome.core.commands.Command;

        /*private*/ cmdAxialsymm: com.vzome.core.commands.Command;

        /*private*/ cmdH4symmetry: com.vzome.core.commands.Command;

        /*private*/ cmdH4rotations: com.vzome.core.commands.Command;

        /*private*/ cmdIxTsymmetry: com.vzome.core.commands.Command;

        /*private*/ cmdTxTsymmetry: com.vzome.core.commands.Command;

        /*private*/ cmdVanOss600cell: com.vzome.core.commands.Command;

        public constructor(af?: any) {
            if (((af != null && (af.constructor != null && af.constructor["__interfaces"] != null && af.constructor["__interfaces"].indexOf("com.vzome.core.algebra.AlgebraicField") >= 0)) || af === null)) {
                let __args = arguments;
                {
                    let __args = arguments;
                    let symm: any = new com.vzome.core.math.symmetry.IcosahedralSymmetry(af);
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
                    const icosadefaultShapes: com.vzome.core.viewing.AbstractShapes = new com.vzome.core.viewing.ExportedVEFShapes(null, "default", "solid connectors", this.symmetry);
                    const printableShapes: com.vzome.core.viewing.AbstractShapes = new com.vzome.core.viewing.ExportedVEFShapes(null, "printable", "printable", this.symmetry, icosadefaultShapes);
                    const lifelikeShapes: com.vzome.core.viewing.AbstractShapes = new com.vzome.core.viewing.ExportedVEFShapes(null, "lifelike", "lifelike", this.symmetry, icosadefaultShapes);
                    const tinyShapes: com.vzome.core.viewing.AbstractShapes = new com.vzome.core.viewing.ExportedVEFShapes(null, "tiny", "tiny connectors", this.symmetry);
                    const tinyDodecs: com.vzome.core.viewing.AbstractShapes = new com.vzome.core.viewing.ExportedVEFShapes(null, "dodecs", "small dodecahedra", "tiny dodecahedra", this.symmetry, tinyShapes);
                    const bigZome: com.vzome.core.viewing.AbstractShapes = new com.vzome.core.viewing.ExportedVEFShapes(null, "bigzome", "Big Zome", this.symmetry, tinyShapes);
                    const noTwist: com.vzome.core.viewing.AbstractShapes = new com.vzome.core.viewing.ExportedVEFShapes(null, "noTwist", "no-twist 121 zone", null, this.symmetry);
                    const vienne2: com.vzome.core.viewing.AbstractShapes = new com.vzome.core.viewing.ExportedVEFShapes(null, "vienne2", "Vienne", this.symmetry, icosadefaultShapes);
                    const vienne3: com.vzome.core.viewing.AbstractShapes = new com.vzome.core.viewing.ExportedVEFShapes(null, "vienne3", "Vienne lifelike", this.symmetry, vienne2);
                    const vienne: com.vzome.core.viewing.AbstractShapes = new com.vzome.core.viewing.ExportedVEFShapes(null, "vienne", "Vienne 121 zone", null, this.symmetry);
                    const dimtoolShapes: com.vzome.core.viewing.AbstractShapes = new com.vzome.core.viewing.ExportedVEFShapes(null, "dimtool", "dimtool", this.symmetry, icosadefaultShapes);
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
                    const field: com.vzome.core.algebra.AlgebraicField = this.symmetry.getField();
                    this.qSymmH4 = new com.vzome.core.math.symmetry.QuaternionicSymmetry("H_4", "com/vzome/core/math/symmetry/H4roots.vef", field);
                    this.qSymmH4_ROT = new com.vzome.core.math.symmetry.QuaternionicSymmetry("H4_ROT", "com/vzome/core/math/symmetry/H4roots-rotationalSubgroup.vef", field);
                    this.qSymmT2 = new com.vzome.core.math.symmetry.QuaternionicSymmetry("2T", "com/vzome/core/math/symmetry/binaryTetrahedralGroup.vef", field);
                    this.cmdIcosasymm = new com.vzome.core.commands.CommandSymmetry(this.symmetry);
                    this.cmdTetrasymm = new com.vzome.core.commands.CommandTetrahedralSymmetry(this.symmetry);
                    this.cmdAxialsymm = new com.vzome.core.commands.CommandAxialSymmetry(this.symmetry);
                    this.cmdH4symmetry = new com.vzome.core.commands.CommandQuaternionSymmetry(this.qSymmH4, this.qSymmH4);
                    this.cmdH4rotations = new com.vzome.core.commands.CommandQuaternionSymmetry(this.qSymmH4_ROT, this.qSymmH4_ROT);
                    this.cmdIxTsymmetry = new com.vzome.core.commands.CommandQuaternionSymmetry(this.qSymmH4, this.qSymmT2);
                    this.cmdTxTsymmetry = new com.vzome.core.commands.CommandQuaternionSymmetry(this.qSymmT2, this.qSymmT2);
                    this.cmdVanOss600cell = new com.vzome.core.commands.CommandVanOss600Cell();
                }
            } else if (((af != null && af instanceof <any>com.vzome.core.math.symmetry.IcosahedralSymmetry) || af === null)) {
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
                const icosadefaultShapes: com.vzome.core.viewing.AbstractShapes = new com.vzome.core.viewing.ExportedVEFShapes(null, "default", "solid connectors", this.symmetry);
                const printableShapes: com.vzome.core.viewing.AbstractShapes = new com.vzome.core.viewing.ExportedVEFShapes(null, "printable", "printable", this.symmetry, icosadefaultShapes);
                const lifelikeShapes: com.vzome.core.viewing.AbstractShapes = new com.vzome.core.viewing.ExportedVEFShapes(null, "lifelike", "lifelike", this.symmetry, icosadefaultShapes);
                const tinyShapes: com.vzome.core.viewing.AbstractShapes = new com.vzome.core.viewing.ExportedVEFShapes(null, "tiny", "tiny connectors", this.symmetry);
                const tinyDodecs: com.vzome.core.viewing.AbstractShapes = new com.vzome.core.viewing.ExportedVEFShapes(null, "dodecs", "small dodecahedra", "tiny dodecahedra", this.symmetry, tinyShapes);
                const bigZome: com.vzome.core.viewing.AbstractShapes = new com.vzome.core.viewing.ExportedVEFShapes(null, "bigzome", "Big Zome", this.symmetry, tinyShapes);
                const noTwist: com.vzome.core.viewing.AbstractShapes = new com.vzome.core.viewing.ExportedVEFShapes(null, "noTwist", "no-twist 121 zone", null, this.symmetry);
                const vienne2: com.vzome.core.viewing.AbstractShapes = new com.vzome.core.viewing.ExportedVEFShapes(null, "vienne2", "Vienne", this.symmetry, icosadefaultShapes);
                const vienne3: com.vzome.core.viewing.AbstractShapes = new com.vzome.core.viewing.ExportedVEFShapes(null, "vienne3", "Vienne lifelike", this.symmetry, vienne2);
                const vienne: com.vzome.core.viewing.AbstractShapes = new com.vzome.core.viewing.ExportedVEFShapes(null, "vienne", "Vienne 121 zone", null, this.symmetry);
                const dimtoolShapes: com.vzome.core.viewing.AbstractShapes = new com.vzome.core.viewing.ExportedVEFShapes(null, "dimtool", "dimtool", this.symmetry, icosadefaultShapes);
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
                const field: com.vzome.core.algebra.AlgebraicField = this.symmetry.getField();
                this.qSymmH4 = new com.vzome.core.math.symmetry.QuaternionicSymmetry("H_4", "com/vzome/core/math/symmetry/H4roots.vef", field);
                this.qSymmH4_ROT = new com.vzome.core.math.symmetry.QuaternionicSymmetry("H4_ROT", "com/vzome/core/math/symmetry/H4roots-rotationalSubgroup.vef", field);
                this.qSymmT2 = new com.vzome.core.math.symmetry.QuaternionicSymmetry("2T", "com/vzome/core/math/symmetry/binaryTetrahedralGroup.vef", field);
                this.cmdIcosasymm = new com.vzome.core.commands.CommandSymmetry(this.symmetry);
                this.cmdTetrasymm = new com.vzome.core.commands.CommandTetrahedralSymmetry(this.symmetry);
                this.cmdAxialsymm = new com.vzome.core.commands.CommandAxialSymmetry(this.symmetry);
                this.cmdH4symmetry = new com.vzome.core.commands.CommandQuaternionSymmetry(this.qSymmH4, this.qSymmH4);
                this.cmdH4rotations = new com.vzome.core.commands.CommandQuaternionSymmetry(this.qSymmH4_ROT, this.qSymmH4_ROT);
                this.cmdIxTsymmetry = new com.vzome.core.commands.CommandQuaternionSymmetry(this.qSymmH4, this.qSymmT2);
                this.cmdTxTsymmetry = new com.vzome.core.commands.CommandQuaternionSymmetry(this.qSymmT2, this.qSymmT2);
                this.cmdVanOss600cell = new com.vzome.core.commands.CommandVanOss600Cell();
            } else throw new Error('invalid overload');
        }

        /**
         * 
         * @return {com.vzome.core.math.symmetry.IcosahedralSymmetry}
         */
        public getSymmetry(): com.vzome.core.math.symmetry.IcosahedralSymmetry {
            return <com.vzome.core.math.symmetry.IcosahedralSymmetry><any>this.symmetry;
        }

        /**
         * 
         * @param {com.vzome.api.Tool.Kind} kind
         * @param {com.vzome.core.editor.ToolsModel} tools
         * @return {*}
         */
        public createToolFactories(kind: com.vzome.api.Tool.Kind, tools: com.vzome.core.editor.ToolsModel): java.util.List<com.vzome.api.Tool.Factory> {
            const result: java.util.List<com.vzome.api.Tool.Factory> = <any>(new java.util.ArrayList<any>());
            const icosaSymm: com.vzome.core.math.symmetry.IcosahedralSymmetry = this.getSymmetry();
            switch((kind)) {
            case com.vzome.api.Tool.Kind.SYMMETRY:
                result.add(new com.vzome.core.tools.IcosahedralToolFactory(tools, icosaSymm));
                result.add(new com.vzome.core.tools.TetrahedralToolFactory(tools, icosaSymm));
                result.add(new com.vzome.core.tools.InversionToolFactory(tools));
                result.add(new com.vzome.core.tools.LineReflectionToolFactory(tools));
                result.add(new com.vzome.core.tools.MirrorToolFactory(tools));
                result.add(new com.vzome.core.tools.AxialSymmetryToolFactory(tools, icosaSymm));
                break;
            case com.vzome.api.Tool.Kind.TRANSFORM:
                result.add(new com.vzome.core.tools.ScalingToolFactory(tools, icosaSymm));
                result.add(new com.vzome.core.tools.RotationToolFactory(tools, icosaSymm));
                result.add(new com.vzome.core.tools.TranslationToolFactory(tools));
                result.add(new com.vzome.core.tools.ProjectionToolFactory(tools));
                result.add(new com.vzome.core.tools.PerspectiveProjectionToolFactory(tools));
                break;
            case com.vzome.api.Tool.Kind.LINEAR_MAP:
                result.add(new com.vzome.core.tools.AxialStretchTool.Factory(tools, icosaSymm, true, true, true));
                result.add(new com.vzome.core.tools.AxialStretchTool.Factory(tools, icosaSymm, true, false, true));
                result.add(new com.vzome.core.tools.AxialStretchTool.Factory(tools, icosaSymm, true, true, false));
                result.add(new com.vzome.core.tools.AxialStretchTool.Factory(tools, icosaSymm, true, false, false));
                result.add(new com.vzome.core.tools.AxialStretchTool.Factory(tools, icosaSymm, false, true, false));
                result.add(new com.vzome.core.tools.AxialStretchTool.Factory(tools, icosaSymm, false, false, false));
                result.add(new com.vzome.core.tools.LinearMapToolFactory(tools, icosaSymm, false));
                break;
            default:
                break;
            }
            return result;
        }

        /**
         * 
         * @param {com.vzome.api.Tool.Kind} kind
         * @param {com.vzome.core.editor.ToolsModel} tools
         * @return {*}
         */
        public predefineTools(kind: com.vzome.api.Tool.Kind, tools: com.vzome.core.editor.ToolsModel): java.util.List<com.vzome.api.Tool> {
            const result: java.util.List<com.vzome.api.Tool> = <any>(new java.util.ArrayList<any>());
            const icosaSymm: com.vzome.core.math.symmetry.IcosahedralSymmetry = this.getSymmetry();
            switch((kind)) {
            case com.vzome.api.Tool.Kind.SYMMETRY:
                result.add(new com.vzome.core.tools.IcosahedralToolFactory(tools, icosaSymm).createPredefinedTool("icosahedral around origin"));
                result.add(new com.vzome.core.tools.TetrahedralToolFactory(tools, icosaSymm).createPredefinedTool("tetrahedral around origin"));
                result.add(new com.vzome.core.tools.InversionToolFactory(tools).createPredefinedTool("reflection through origin"));
                result.add(new com.vzome.core.tools.MirrorToolFactory(tools).createPredefinedTool("reflection through XY plane"));
                result.add(new com.vzome.core.tools.MirrorToolFactory(tools).createPredefinedTool("reflection through X=Y green plane"));
                result.add(new com.vzome.core.tools.AxialSymmetryToolFactory(tools, icosaSymm).createPredefinedTool("symmetry around red through origin"));
                break;
            case com.vzome.api.Tool.Kind.TRANSFORM:
                result.add(new com.vzome.core.tools.ScalingToolFactory(tools, icosaSymm).createPredefinedTool("scale down"));
                result.add(new com.vzome.core.tools.ScalingToolFactory(tools, icosaSymm).createPredefinedTool("scale up"));
                result.add(new com.vzome.core.tools.RotationToolFactory(tools, icosaSymm, true).createPredefinedTool("rotate around red through origin"));
                result.add(new com.vzome.core.tools.TranslationToolFactory(tools).createPredefinedTool("b1 move along +X"));
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
        public getLegacyCommand(action: string): com.vzome.core.commands.Command {
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

        public getQuaternionSymmetry(name: string): com.vzome.core.math.symmetry.QuaternionicSymmetry {
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


}

