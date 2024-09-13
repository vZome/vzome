/* Generated from Java with JSweet 3.2.0-SNAPSHOT - http://www.jsweet.org */
namespace com.vzome.core.kinds {
    export class OctahedralSymmetryPerspective extends com.vzome.core.kinds.AbstractSymmetryPerspective {
        public constructor(field: com.vzome.core.algebra.AlgebraicField) {
            super(new com.vzome.core.math.symmetry.OctahedralSymmetry(field));
            this.modelResourcePath = "org/vorthmann/zome/app/octahedral-vef.vZome";
            this.setDefaultGeometry(new com.vzome.core.viewing.OctahedralShapes("octahedral", "octahedra", this.symmetry));
        }

        /**
         * 
         * @return {com.vzome.core.math.symmetry.OctahedralSymmetry}
         */
        public getSymmetry(): com.vzome.core.math.symmetry.OctahedralSymmetry {
            return <com.vzome.core.math.symmetry.OctahedralSymmetry><any>this.symmetry;
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
                result.add(new com.vzome.core.tools.OctahedralToolFactory(tools, this.symmetry));
                result.add(new com.vzome.core.tools.TetrahedralToolFactory(tools, this.symmetry));
                result.add(new com.vzome.core.tools.InversionToolFactory(tools));
                result.add(new com.vzome.core.tools.LineReflectionToolFactory(tools));
                result.add(new com.vzome.core.tools.MirrorToolFactory(tools));
                result.add(new com.vzome.core.tools.AxialSymmetryToolFactory(tools, this.symmetry));
                break;
            case com.vzome.api.Tool.Kind.TRANSFORM:
                result.add(new com.vzome.core.tools.ScalingToolFactory(tools, this.symmetry));
                result.add(new com.vzome.core.tools.RotationToolFactory(tools, this.symmetry));
                result.add(new com.vzome.core.tools.TranslationToolFactory(tools));
                result.add(new com.vzome.core.tools.ProjectionToolFactory(tools));
                result.add(new com.vzome.core.tools.PerspectiveProjectionToolFactory(tools));
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
                result.add(new com.vzome.core.tools.OctahedralToolFactory(tools, this.symmetry).createPredefinedTool("octahedral around origin"));
                result.add(new com.vzome.core.tools.TetrahedralToolFactory(tools, this.symmetry).createPredefinedTool("tetrahedral around origin"));
                result.add(new com.vzome.core.tools.InversionToolFactory(tools).createPredefinedTool("reflection through origin"));
                result.add(new com.vzome.core.tools.MirrorToolFactory(tools).createPredefinedTool("reflection through XY plane"));
                result.add(new com.vzome.core.tools.MirrorToolFactory(tools).createPredefinedTool("reflection through X=Y green plane"));
                result.add(new com.vzome.core.tools.AxialSymmetryToolFactory(tools, this.symmetry).createPredefinedTool("symmetry around green through origin"));
                break;
            case com.vzome.api.Tool.Kind.TRANSFORM:
                result.add(new com.vzome.core.tools.ScalingToolFactory(tools, this.symmetry).createPredefinedTool("scale down"));
                result.add(new com.vzome.core.tools.ScalingToolFactory(tools, this.symmetry).createPredefinedTool("scale up"));
                result.add(new com.vzome.core.tools.RotationToolFactory(tools, this.symmetry, true).createPredefinedTool("rotate around green through origin"));
                result.add(new com.vzome.core.tools.TranslationToolFactory(tools).createPredefinedTool("b1 move along +X"));
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


}

