/* Generated from Java with JSweet 3.2.0-SNAPSHOT - http://www.jsweet.org */
namespace com.vzome.core.kinds {
    /**
     * Everything here is stateless, or at worst, a cache (like Shapes).
     * An instance of this can be shared by many DocumentModels.
     * This is why it does not have tool factories, though it does
     * dictate what tool factories will be present.
     * 
     * @author Scott Vorthmann
     * @param {*} field
     * @class
     * @extends com.vzome.core.kinds.DefaultFieldApplication
     */
    export class HeptagonFieldApplication extends com.vzome.core.kinds.DefaultFieldApplication {
        public constructor(field: com.vzome.core.algebra.AlgebraicField) {
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

        /*private*/ correctedAntiprismPerspective: com.vzome.core.editor.SymmetryPerspective;

        /*private*/ originalAntiprismPerspective: com.vzome.core.editor.SymmetryPerspective;

        /**
         * 
         * @return {*}
         */
        public getSymmetryPerspectives(): java.util.Collection<com.vzome.core.editor.SymmetryPerspective> {
            return java.util.Arrays.asList<any>(this.correctedAntiprismPerspective, super.getDefaultSymmetryPerspective(), this.originalAntiprismPerspective);
        }

        /**
         * 
         * @return {*}
         */
        public getDefaultSymmetryPerspective(): com.vzome.core.editor.SymmetryPerspective {
            return this.correctedAntiprismPerspective;
        }

        /**
         * 
         * @param {string} symmName
         * @return {*}
         */
        public getSymmetryPerspective(symmName: string): com.vzome.core.editor.SymmetryPerspective {
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

        export class HeptagonalSymmetryPerspective extends com.vzome.core.kinds.AbstractSymmetryPerspective {
            public __parent: any;
            corrected: boolean;

            constructor(__parent: any, corrected: boolean) {
                super(new com.vzome.fields.heptagon.HeptagonalAntiprismSymmetry(__parent.getField(), "blue", corrected).createStandardOrbits("blue"));
                this.__parent = __parent;
                if (this.corrected === undefined) { this.corrected = false; }
                this.axialsymm = new com.vzome.core.commands.CommandAxialSymmetry(this.symmetry);
                this.corrected = corrected;
                const octahedralShapes: com.vzome.core.viewing.AbstractShapes = new com.vzome.core.viewing.OctahedralShapes("octahedral", "triangular antiprism", this.symmetry);
                const antiprismShapes: com.vzome.core.viewing.AbstractShapes = new com.vzome.core.viewing.ExportedVEFShapes(null, "heptagon/antiprism", "heptagonal antiprism", this.symmetry, octahedralShapes);
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
             * @param {com.vzome.api.Tool.Kind} kind
             * @param {com.vzome.core.editor.ToolsModel} tools
             * @return {*}
             */
            public createToolFactories(kind: com.vzome.api.Tool.Kind, tools: com.vzome.core.editor.ToolsModel): java.util.List<com.vzome.api.Tool.Factory> {
                const result: java.util.List<com.vzome.api.Tool.Factory> = <any>(new java.util.ArrayList<any>());
                switch((kind)) {
                case com.vzome.api.Tool.Kind.SYMMETRY:
                    result.add(new com.vzome.core.tools.SymmetryToolFactory(tools, this.symmetry));
                    result.add(new com.vzome.core.tools.LineReflectionToolFactory(tools));
                    result.add(new com.vzome.core.tools.MirrorToolFactory(tools));
                    result.add(new com.vzome.core.tools.AxialSymmetryToolFactory(tools, this.symmetry));
                    break;
                case com.vzome.api.Tool.Kind.TRANSFORM:
                    result.add(new com.vzome.core.tools.ScalingToolFactory(tools, this.symmetry));
                    result.add(new com.vzome.core.tools.RotationToolFactory(tools, this.symmetry));
                    result.add(new com.vzome.core.tools.TranslationToolFactory(tools));
                    break;
                case com.vzome.api.Tool.Kind.LINEAR_MAP:
                    result.add(new com.vzome.core.tools.LinearMapToolFactory(tools, this.symmetry, false));
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
                switch((kind)) {
                case com.vzome.api.Tool.Kind.SYMMETRY:
                    result.add(new com.vzome.core.tools.SymmetryToolFactory(tools, this.symmetry).createPredefinedTool("heptagonal antiprism around origin"));
                    result.add(new com.vzome.core.tools.MirrorToolFactory(tools).createPredefinedTool("reflection through XY plane"));
                    result.add(new com.vzome.core.tools.AxialSymmetryToolFactory(tools, this.symmetry).createPredefinedTool("symmetry around red through origin"));
                    break;
                case com.vzome.api.Tool.Kind.TRANSFORM:
                    result.add(new com.vzome.core.tools.ScalingToolFactory(tools, this.symmetry).createPredefinedTool("scale down"));
                    result.add(new com.vzome.core.tools.ScalingToolFactory(tools, this.symmetry).createPredefinedTool("scale up"));
                    result.add(new com.vzome.core.tools.RotationToolFactory(tools, this.symmetry, true).createPredefinedTool("rotate around red through origin"));
                    result.add(new com.vzome.core.tools.TranslationToolFactory(tools).createPredefinedTool("b1 move along +X"));
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
                return "org/vorthmann/zome/app/heptagonal antiprism.vZome";
            }
        }
        HeptagonalSymmetryPerspective["__class"] = "com.vzome.core.kinds.HeptagonFieldApplication.HeptagonalSymmetryPerspective";
        HeptagonalSymmetryPerspective["__interfaces"] = ["com.vzome.core.editor.SymmetryPerspective"];


    }

}

