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
    export class RootThreeFieldApplication extends com.vzome.core.kinds.DefaultFieldApplication {
        public constructor(field: com.vzome.core.algebra.AlgebraicField) {
            super(field);
            this.dodecagonalPerspective = new RootThreeFieldApplication.RootThreeFieldApplication$0(this, new com.vzome.core.math.symmetry.DodecagonalSymmetry(this.getField()));
            const octahedralPerspective: com.vzome.core.kinds.OctahedralSymmetryPerspective = <com.vzome.core.kinds.OctahedralSymmetryPerspective><any>super.getDefaultSymmetryPerspective();
            const symm: com.vzome.core.math.symmetry.AbstractSymmetry = octahedralPerspective.getSymmetry();
            symm.createZoneOrbit$java_lang_String$int$int$int_A_A$boolean("red", 0, com.vzome.core.math.symmetry.Symmetry.NO_ROTATION, [[1, 1, 1, 2], [1, 2, 0, 1], [0, 1, 0, 1]], true);
            symm.createZoneOrbit$java_lang_String$int$int$int_A_A("brown", 0, com.vzome.core.math.symmetry.Symmetry.NO_ROTATION, [[1, 1, 0, 1], [1, 1, 0, 1], [2, 1, 0, 1]]);
            const defaultShapes: com.vzome.core.viewing.AbstractShapes = new com.vzome.core.viewing.ExportedVEFShapes(null, "rootThreeOctaSmall", "small octahedra", "small connectors", symm);
            octahedralPerspective.setDefaultGeometry(defaultShapes);
        }

        /**
         * 
         * @return {string}
         */
        public getLabel(): string {
            return "\u221a3";
        }

        /*private*/ dodecagonalPerspective: com.vzome.core.editor.SymmetryPerspective;

        /**
         * 
         * @return {*}
         */
        public getDefaultSymmetryPerspective(): com.vzome.core.editor.SymmetryPerspective {
            return this.dodecagonalPerspective;
        }

        /**
         * 
         * @return {*}
         */
        public getSymmetryPerspectives(): java.util.Collection<com.vzome.core.editor.SymmetryPerspective> {
            return java.util.Arrays.asList<any>(super.getDefaultSymmetryPerspective(), this.dodecagonalPerspective);
        }

        /**
         * 
         * @param {string} symmName
         * @return {*}
         */
        public getSymmetryPerspective(symmName: string): com.vzome.core.editor.SymmetryPerspective {
            switch((symmName)) {
            case "dodecagonal":
                return this.dodecagonalPerspective;
            default:
                return super.getSymmetryPerspective(symmName);
            }
        }
    }
    RootThreeFieldApplication["__class"] = "com.vzome.core.kinds.RootThreeFieldApplication";
    RootThreeFieldApplication["__interfaces"] = ["com.vzome.core.math.symmetry.Symmetries4D","com.vzome.core.editor.FieldApplication"];



    export namespace RootThreeFieldApplication {

        export class RootThreeFieldApplication$0 extends com.vzome.core.kinds.AbstractSymmetryPerspective {
            public __parent: any;
            /**
             * 
             * @return {string}
             */
            public getName(): string {
                return "dodecagonal";
            }

            /**
             * 
             * @param {com.vzome.core.math.symmetry.Direction} orbit
             * @return {boolean}
             */
            public orbitIsBuildDefault(orbit: com.vzome.core.math.symmetry.Direction): boolean {
                switch((orbit.getName())) {
                case "blue":
                case "green":
                    return true;
                default:
                    return false;
                }
            }

            /**
             * 
             * @param {com.vzome.core.math.symmetry.Direction} orbit
             * @return {*}
             */
            public getOrbitUnitLength(orbit: com.vzome.core.math.symmetry.Direction): com.vzome.core.algebra.AlgebraicNumber {
                switch((orbit.getName())) {
                case "blue":
                case "green":
                    return this.__parent.getField()['createPower$int'](2);
                default:
                    return super.getOrbitUnitLength(orbit);
                }
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
                    result.add(new com.vzome.core.tools.SymmetryToolFactory(tools, this.symmetry).createPredefinedTool("dodecagonal antiprism around origin"));
                    result.add(new com.vzome.core.tools.InversionToolFactory(tools).createPredefinedTool("reflection through origin"));
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

            dodecagonsymm: com.vzome.core.commands.Command;

            /**
             * 
             * @param {string} action
             * @return {*}
             */
            public getLegacyCommand(action: string): com.vzome.core.commands.Command {
                switch((action)) {
                case "dodecagonsymm":
                    return this.dodecagonsymm;
                default:
                    return super.getLegacyCommand(action);
                }
            }

            /**
             * 
             * @return {string}
             */
            public getModelResourcePath(): string {
                return "org/vorthmann/zome/app/12-gon-trackball-vef.vZome";
            }

            constructor(__parent: any, __arg0: any) {
                super(__arg0);
                this.__parent = __parent;
                (() => {
                    const defaultShapes: com.vzome.core.viewing.AbstractShapes = new com.vzome.core.viewing.ExportedVEFShapes(null, "dodecagon3d", "prisms", this.symmetry);
                    const hexagonShapes: com.vzome.core.viewing.AbstractShapes = new com.vzome.core.viewing.DodecagonalShapes("dodecagonal", "hexagons", "flat hexagons", this.symmetry);
                    this.setDefaultGeometry(defaultShapes);
                    this.addShapes(hexagonShapes);
                })();
                this.dodecagonsymm = new com.vzome.core.commands.CommandSymmetry(this.symmetry);
            }
        }
        RootThreeFieldApplication$0["__interfaces"] = ["com.vzome.core.editor.SymmetryPerspective"];


    }

}

