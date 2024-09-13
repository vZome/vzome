/* Generated from Java with JSweet 3.2.0-SNAPSHOT - http://www.jsweet.org */
namespace com.vzome.core.kinds {
    /**
     * Everything here is stateless, or at worst, a cache (like Shapes).
     * An instance of this can be shared by many DocumentModels.
     * This is why it does not have tool factories, though it does
     * dictate what tool factories will be present.
     * 
     * @author David Hall
     * @param {com.vzome.core.algebra.PolygonField} field
     * @class
     * @extends com.vzome.core.kinds.DefaultFieldApplication
     */
    export class PolygonFieldApplication extends com.vzome.core.kinds.DefaultFieldApplication {
        public constructor(field: com.vzome.core.algebra.PolygonField) {
            super(field);
            this.symmetryPerspectives = <any>(new java.util.ArrayList<any>());
            if (this.icosahedralPerspective === undefined) { this.icosahedralPerspective = null; }
            if (this.H4 === undefined) { this.H4 = null; }
            this.h4Builder = null;
            this.symmetryPerspectives.add(new PolygonFieldApplication.AntiprismSymmetryPerspective(this));
            if (field.polygonSides() === 5){
                this.icosahedralPerspective = new com.vzome.core.kinds.IcosahedralSymmetryPerspective(this.getField());
                this.symmetryPerspectives.add(this.icosahedralPerspective);
                this.H4 = new com.vzome.core.math.symmetry.QuaternionicSymmetry("H_4", "com/vzome/core/math/symmetry/H4roots.vef", this.getField());
            } else {
                this.icosahedralPerspective = null;
                this.H4 = null;
            }
            this.symmetryPerspectives.add(super.getDefaultSymmetryPerspective());
        }

        /**
         * 
         * @return {com.vzome.core.algebra.PolygonField}
         */
        public getField(): com.vzome.core.algebra.PolygonField {
            return <com.vzome.core.algebra.PolygonField><any>super.getField();
        }

        symmetryPerspectives: java.util.ArrayList<com.vzome.core.editor.SymmetryPerspective>;

        /**
         * 
         * @return {*}
         */
        public getSymmetryPerspectives(): java.util.Collection<com.vzome.core.editor.SymmetryPerspective> {
            return this.symmetryPerspectives;
        }

        /*private*/ icosahedralPerspective: com.vzome.core.kinds.IcosahedralSymmetryPerspective;

        /**
         * 
         * @return {*}
         */
        public getDefaultSymmetryPerspective(): com.vzome.core.editor.SymmetryPerspective {
            return (this.icosahedralPerspective == null) ? this.symmetryPerspectives.get(0) : this.icosahedralPerspective;
        }

        /**
         * 
         * @param {string} symmName
         * @return {*}
         */
        public getSymmetryPerspective(symmName: string): com.vzome.core.editor.SymmetryPerspective {
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
         * @param {com.vzome.core.editor.ToolsModel} tools
         */
        public registerToolFactories(toolFactories: java.util.Map<string, com.vzome.api.Tool.Factory>, tools: com.vzome.core.editor.ToolsModel) {
            super.registerToolFactories(toolFactories, tools);
            if (this.icosahedralPerspective != null){
                const symm: com.vzome.core.math.symmetry.IcosahedralSymmetry = this.icosahedralPerspective.getSymmetry();
                toolFactories.put("AxialStretchTool", new com.vzome.core.tools.AxialStretchTool.Factory(tools, symm, false, false, false));
                toolFactories.put("SymmetryTool", new com.vzome.core.tools.IcosahedralToolFactory(tools, symm));
            }
        }

        /*private*/ H4: com.vzome.core.math.symmetry.QuaternionicSymmetry;

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
    PolygonFieldApplication["__class"] = "com.vzome.core.kinds.PolygonFieldApplication";
    PolygonFieldApplication["__interfaces"] = ["com.vzome.core.math.symmetry.Symmetries4D","com.vzome.core.editor.FieldApplication"];



    export namespace PolygonFieldApplication {

        export class AntiprismSymmetryPerspective extends com.vzome.core.kinds.AbstractSymmetryPerspective {
            public __parent: any;
            constructor(__parent: any) {
                super(new com.vzome.core.math.symmetry.AntiprismSymmetry(__parent.getField()).createStandardOrbits("blue"));
                this.__parent = __parent;
                this.axialsymm = new com.vzome.core.commands.CommandAxialSymmetry(this.symmetry);
                const thinAntiprismShapes: com.vzome.core.viewing.AbstractShapes = new com.vzome.core.viewing.AntiprismShapes("thin", "thin antiprism", this.getSymmetry());
                const antiprismShapes: com.vzome.core.viewing.AbstractShapes = new com.vzome.core.viewing.AntiprismShapes("antiprism", "antiprism", this.getSymmetry());
                const octahedralShapes: com.vzome.core.viewing.AbstractShapes = new com.vzome.core.viewing.OctahedralShapes("octahedral", "octahedral", this.symmetry);
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
             * @return {com.vzome.core.math.symmetry.AntiprismSymmetry}
             */
            public getSymmetry(): com.vzome.core.math.symmetry.AntiprismSymmetry {
                return <com.vzome.core.math.symmetry.AntiprismSymmetry><any>super.getSymmetry();
            }

            /**
             * 
             * @param {com.vzome.api.Tool.Kind} kind
             * @param {com.vzome.core.editor.ToolsModel} tools
             * @return {*}
             */
            public createToolFactories(kind: com.vzome.api.Tool.Kind, tools: com.vzome.core.editor.ToolsModel): java.util.List<com.vzome.api.Tool.Factory> {
                const isTrivial: boolean = this.symmetry.isTrivial();
                const result: java.util.List<com.vzome.api.Tool.Factory> = <any>(new java.util.ArrayList<any>());
                switch((kind)) {
                case com.vzome.api.Tool.Kind.SYMMETRY:
                    result.add(new com.vzome.core.tools.SymmetryToolFactory(tools, this.symmetry));
                    if (isTrivial){
                        result.add(new com.vzome.core.tools.InversionToolFactory(tools));
                    }
                    result.add(new com.vzome.core.tools.LineReflectionToolFactory(tools));
                    result.add(new com.vzome.core.tools.MirrorToolFactory(tools));
                    result.add(new com.vzome.core.tools.AxialSymmetryToolFactory(tools, this.symmetry));
                    break;
                case com.vzome.api.Tool.Kind.TRANSFORM:
                    result.add(new com.vzome.core.tools.ScalingToolFactory(tools, this.symmetry));
                    result.add(new com.vzome.core.tools.RotationToolFactory(tools, this.symmetry));
                    result.add(new com.vzome.core.tools.TranslationToolFactory(tools));
                    if (isTrivial){
                        result.add(new com.vzome.core.tools.ProjectionToolFactory(tools));
                    }
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
                    result.add(new com.vzome.core.tools.SymmetryToolFactory(tools, this.symmetry).createPredefinedTool("polygonal antiprism around origin"));
                    result.add(new com.vzome.core.tools.MirrorToolFactory(tools).createPredefinedTool("reflection through XY plane"));
                    result.add(new com.vzome.core.tools.AxialSymmetryToolFactory(tools, this.symmetry, true).createPredefinedTool("symmetry around red through origin"));
                    break;
                case com.vzome.api.Tool.Kind.TRANSFORM:
                    result.add(new com.vzome.core.tools.ScalingToolFactory(tools, this.symmetry).createPredefinedTool("scale down"));
                    result.add(new com.vzome.core.tools.ScalingToolFactory(tools, this.symmetry).createPredefinedTool("scale up"));
                    result.add(new com.vzome.core.tools.RotationToolFactory(tools, this.symmetry, true).createPredefinedTool("rotate around red through origin"));
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
                return "org/vorthmann/zome/app/antiprism-trackball-template.vZome";
            }
        }
        AntiprismSymmetryPerspective["__class"] = "com.vzome.core.kinds.PolygonFieldApplication.AntiprismSymmetryPerspective";
        AntiprismSymmetryPerspective["__interfaces"] = ["com.vzome.core.editor.SymmetryPerspective"];


    }

}

