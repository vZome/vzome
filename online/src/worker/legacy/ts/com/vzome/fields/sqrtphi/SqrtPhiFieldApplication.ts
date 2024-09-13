/* Generated from Java with JSweet 3.2.0-SNAPSHOT - http://www.jsweet.org */
namespace com.vzome.fields.sqrtphi {
    /**
     * Everything here is stateless, or at worst, a cache (like Shapes).
     * An instance of this can be shared by many DocumentModels.
     * This is why it does not have tool factories, though it does
     * dictate what tool factories will be present.
     * 
     * @author vorth
     * @param {*} field
     * @class
     * @extends com.vzome.core.kinds.DefaultFieldApplication
     */
    export class SqrtPhiFieldApplication extends com.vzome.core.kinds.DefaultFieldApplication {
        public constructor(field: com.vzome.core.algebra.AlgebraicField) {
            super(field);
            this.icosahedralPerspective = new SqrtPhiFieldApplication.SqrtPhiFieldApplication$0(this, new com.vzome.core.math.symmetry.IcosahedralSymmetry(this.getField()));
            this.pentagonalPerspective = new SqrtPhiFieldApplication.SqrtPhiFieldApplication$1(this, new com.vzome.fields.sqrtphi.PentagonalAntiprismSymmetry(this.getField(), null));
            this.H4 = new com.vzome.core.math.symmetry.QuaternionicSymmetry("H_4", "com/vzome/core/math/symmetry/H4roots.vef", this.getField());
            this.h4Builder = null;
            const octahedralPerspective: com.vzome.core.kinds.OctahedralSymmetryPerspective = <com.vzome.core.kinds.OctahedralSymmetryPerspective><any>super.getDefaultSymmetryPerspective();
            const symm: com.vzome.core.math.symmetry.OctahedralSymmetry = octahedralPerspective.getSymmetry();
            const scale: com.vzome.core.algebra.AlgebraicNumber = field['createPower$int'](6);
            symm.getDirection("blue").setUnitLength(scale);
            symm.getDirection("green").setUnitLength(scale);
            symm.getDirection("yellow").setUnitLength(scale);
            let x: com.vzome.core.algebra.AlgebraicNumber = field['createAlgebraicNumber$int_A']([0, -1, 0, 0]);
            let y: com.vzome.core.algebra.AlgebraicNumber = field['createAlgebraicNumber$int_A']([-1, 0, 0, 0]);
            let z: com.vzome.core.algebra.AlgebraicNumber = field.zero();
            const unitLength: com.vzome.core.algebra.AlgebraicNumber = field['createPower$int'](4);
            let norm: com.vzome.core.algebra.AlgebraicVector = new com.vzome.core.algebra.AlgebraicVector(x, y, z);
            symm.createZoneOrbit$java_lang_String$int$int$com_vzome_core_algebra_AlgebraicVector$boolean$boolean$com_vzome_core_algebra_AlgebraicNumber("slate", 0, com.vzome.core.math.symmetry.Symmetry.NO_ROTATION, norm, true, false, unitLength);
            x = field['createAlgebraicNumber$int_A']([0, 1, 0, -1]);
            y = field.one();
            z = field.one();
            norm = new com.vzome.core.algebra.AlgebraicVector(x, y, z);
            symm.createZoneOrbit$java_lang_String$int$int$com_vzome_core_algebra_AlgebraicVector$boolean$boolean$com_vzome_core_algebra_AlgebraicNumber("mauve", 0, com.vzome.core.math.symmetry.Symmetry.NO_ROTATION, norm, true, false, unitLength);
            x = field['createAlgebraicNumber$int_A']([1, 0, -1, 0]);
            y = field['createAlgebraicNumber$int_A']([0, -1, 0, 0]);
            z = field['createAlgebraicNumber$int_A']([0, -1, 0, 1]);
            norm = new com.vzome.core.algebra.AlgebraicVector(x, y, z);
            symm.createZoneOrbit$java_lang_String$int$int$com_vzome_core_algebra_AlgebraicVector$boolean$boolean$com_vzome_core_algebra_AlgebraicNumber("ivory", 0, com.vzome.core.math.symmetry.Symmetry.NO_ROTATION, norm, true, false, unitLength);
            const defaultShapes: com.vzome.core.viewing.AbstractShapes = new com.vzome.core.viewing.OctahedralShapes("octahedral", "octahedra", symm);
            octahedralPerspective.setDefaultGeometry(defaultShapes);
        }

        /**
         * 
         * @return {string}
         */
        public getLabel(): string {
            return "\u221a\u03c6";
        }

        /*private*/ icosahedralPerspective: com.vzome.core.kinds.IcosahedralSymmetryPerspective;

        /*private*/ pentagonalPerspective: com.vzome.core.editor.SymmetryPerspective;

        /*private*/ H4: com.vzome.core.math.symmetry.QuaternionicSymmetry;

        /**
         * 
         * @return {*}
         */
        public getSymmetryPerspectives(): java.util.Collection<com.vzome.core.editor.SymmetryPerspective> {
            return java.util.Arrays.asList<any>(this.pentagonalPerspective, super.getDefaultSymmetryPerspective(), this.icosahedralPerspective);
        }

        /**
         * 
         * @return {*}
         */
        public getDefaultSymmetryPerspective(): com.vzome.core.editor.SymmetryPerspective {
            return this.pentagonalPerspective;
        }

        /**
         * 
         * @param {string} symmName
         * @return {*}
         */
        public getSymmetryPerspective(symmName: string): com.vzome.core.editor.SymmetryPerspective {
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
         * @return {com.vzome.core.math.symmetry.QuaternionicSymmetry}
         */
        public getQuaternionSymmetry(name: string): com.vzome.core.math.symmetry.QuaternionicSymmetry {
            switch((name)) {
            case "H_4":
                return this.H4;
            default:
                return null;
            }
        }

        /*private*/ h4Builder: com.vzome.core.commands.CommandUniformH4Polytope;

        /**
         * 
         * @param {string} groupName
         * @param {number} index
         * @param {number} edgesToRender
         * @param {com.vzome.core.algebra.AlgebraicNumber[]} edgeScales
         * @param {*} listener
         */
        public constructPolytope(groupName: string, index: number, edgesToRender: number, edgeScales: com.vzome.core.algebra.AlgebraicNumber[], listener: com.vzome.core.math.symmetry.WythoffConstruction.Listener) {
            switch((groupName)) {
            case "H4":
                if (this.h4Builder == null){
                    const qsymm: com.vzome.core.math.symmetry.QuaternionicSymmetry = new com.vzome.core.math.symmetry.QuaternionicSymmetry("H_4", "com/vzome/core/math/symmetry/H4roots.vef", this.getField());
                    this.h4Builder = new com.vzome.core.commands.CommandUniformH4Polytope(this.getField(), qsymm, 0);
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

        export class SqrtPhiFieldApplication$0 extends com.vzome.core.kinds.IcosahedralSymmetryPerspective {
            public __parent: any;
            constructor(__parent: any, __arg0: any) {
                super(__arg0);
                this.__parent = __parent;
                (() => {
                    const icosaSymm: com.vzome.core.math.symmetry.IcosahedralSymmetry = this.getSymmetry();
                    const tinyIcosaShapes: com.vzome.core.viewing.AbstractShapes = new com.vzome.core.viewing.ExportedVEFShapes(null, "sqrtPhi/tinyIcosahedra", "tiny icosahedra", null, icosaSymm);
                    const icosahedralShapes: com.vzome.core.viewing.AbstractShapes = new com.vzome.core.viewing.ExportedVEFShapes(null, "sqrtPhi/zome", "solid Zome", icosaSymm, tinyIcosaShapes);
                    this.clearShapes();
                    this.addShapes(icosahedralShapes);
                    this.setDefaultGeometry(tinyIcosaShapes);
                })();
            }
        }
        SqrtPhiFieldApplication$0["__interfaces"] = ["com.vzome.core.editor.SymmetryPerspective"];



        export class SqrtPhiFieldApplication$1 extends com.vzome.core.kinds.AbstractSymmetryPerspective {
            public __parent: any;
            /**
             * 
             * @return {com.vzome.fields.sqrtphi.PentagonalAntiprismSymmetry}
             */
            public getSymmetry(): com.vzome.fields.sqrtphi.PentagonalAntiprismSymmetry {
                return <com.vzome.fields.sqrtphi.PentagonalAntiprismSymmetry><any>super.getSymmetry();
            }

            /**
             * 
             * @param {com.vzome.api.Tool.Kind} kind
             * @param {com.vzome.core.editor.ToolsModel} tools
             * @return {*}
             */
            public createToolFactories(kind: com.vzome.api.Tool.Kind, tools: com.vzome.core.editor.ToolsModel): java.util.List<com.vzome.api.Tool.Factory> {
                const result: java.util.List<com.vzome.api.Tool.Factory> = <any>(new java.util.ArrayList<any>());
                const pentaSymm: com.vzome.fields.sqrtphi.PentagonalAntiprismSymmetry = this.getSymmetry();
                switch((kind)) {
                case com.vzome.api.Tool.Kind.SYMMETRY:
                    result.add(new com.vzome.core.tools.SymmetryToolFactory(tools, pentaSymm));
                    result.add(new com.vzome.core.tools.InversionToolFactory(tools));
                    result.add(new com.vzome.core.tools.LineReflectionToolFactory(tools));
                    result.add(new com.vzome.core.tools.MirrorToolFactory(tools));
                    result.add(new com.vzome.core.tools.AxialSymmetryToolFactory(tools, pentaSymm));
                    break;
                case com.vzome.api.Tool.Kind.TRANSFORM:
                    result.add(new com.vzome.core.tools.ScalingToolFactory(tools, pentaSymm));
                    result.add(new com.vzome.core.tools.RotationToolFactory(tools, pentaSymm));
                    result.add(new com.vzome.core.tools.TranslationToolFactory(tools));
                    result.add(new com.vzome.core.tools.ProjectionToolFactory(tools));
                    break;
                case com.vzome.api.Tool.Kind.LINEAR_MAP:
                    result.add(new com.vzome.core.tools.LinearMapToolFactory(tools, pentaSymm, false));
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
                const pentaSymm: com.vzome.fields.sqrtphi.PentagonalAntiprismSymmetry = this.getSymmetry();
                switch((kind)) {
                case com.vzome.api.Tool.Kind.SYMMETRY:
                    result.add(new com.vzome.core.tools.SymmetryToolFactory(tools, pentaSymm).createPredefinedTool("pentagonal antiprism around origin"));
                    result.add(new com.vzome.core.tools.AxialSymmetryToolFactory(tools, pentaSymm).createPredefinedTool("fivefold symmetry through origin"));
                    result.add(new com.vzome.core.tools.MirrorToolFactory(tools).createPredefinedTool("reflection through red plane"));
                    break;
                case com.vzome.api.Tool.Kind.TRANSFORM:
                    result.add(new com.vzome.core.tools.ScalingToolFactory(tools, pentaSymm).createPredefinedTool("scale down"));
                    result.add(new com.vzome.core.tools.ScalingToolFactory(tools, pentaSymm).createPredefinedTool("scale up"));
                    result.add(new com.vzome.core.tools.RotationToolFactory(tools, pentaSymm, true).createPredefinedTool("fivefold rotation through origin"));
                    break;
                default:
                    break;
                }
                return result;
            }

            axialsymm: com.vzome.core.commands.Command;

            /**
             * 
             * @param {string} action
             * @return {*}
             */
            public getLegacyCommand(action: string): com.vzome.core.commands.Command {
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
                    const pentaSymm: com.vzome.fields.sqrtphi.PentagonalAntiprismSymmetry = this.getSymmetry();
                    pentaSymm.createStandardOrbits("blue");
                    const octahedralShapes: com.vzome.core.viewing.AbstractShapes = new com.vzome.core.viewing.OctahedralShapes("octahedral", "octahedra", pentaSymm);
                    const kostickShapes: com.vzome.core.viewing.AbstractShapes = new com.vzome.core.viewing.ExportedVEFShapes(null, "sqrtPhi/fivefold", "Kostick", pentaSymm, octahedralShapes);
                    this.setDefaultGeometry(kostickShapes);
                    this.addShapes(octahedralShapes);
                    this.axialsymm = new com.vzome.core.commands.CommandAxialSymmetry(pentaSymm);
                })();
                if (this.axialsymm === undefined) { this.axialsymm = null; }
            }
        }
        SqrtPhiFieldApplication$1["__interfaces"] = ["com.vzome.core.editor.SymmetryPerspective"];


    }

}

