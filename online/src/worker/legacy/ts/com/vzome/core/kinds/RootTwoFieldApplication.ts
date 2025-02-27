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
    export class RootTwoFieldApplication extends com.vzome.core.kinds.DefaultFieldApplication {
        public constructor(field: com.vzome.core.algebra.AlgebraicField) {
            super(field);
            this.synestructicsSymmetry = new RootTwoFieldApplication.RootTwoFieldApplication$0(this, this.getField(), "orange");
            this.synestructicsPerspective = new RootTwoFieldApplication.RootTwoFieldApplication$1(this, this.synestructicsSymmetry);
            const octahedralPerspective: com.vzome.core.kinds.OctahedralSymmetryPerspective = <com.vzome.core.kinds.OctahedralSymmetryPerspective><any>super.getDefaultSymmetryPerspective();
            const symmetry: com.vzome.core.math.symmetry.AbstractSymmetry = octahedralPerspective.getSymmetry();
            symmetry.createZoneOrbit$java_lang_String$int$int$int_A_A$boolean("yellow", 0, 4, [[1, 1, 0, 1], [1, 1, 0, 1], [1, 1, 0, 1]], true);
            symmetry.createZoneOrbit$java_lang_String$int$int$int_A_A$boolean("green", 1, 8, [[0, 1, 1, 2], [0, 1, 1, 2], [0, 1, 0, 1]], true);
            symmetry.createZoneOrbit$java_lang_String$int$int$int_A_A$boolean("brown", 0, com.vzome.core.math.symmetry.Symmetry.NO_ROTATION, [[1, 1, 0, 1], [1, 1, 0, 1], [2, 1, 0, 1]], true);
            const defaultShapes: com.vzome.core.viewing.AbstractShapes = new com.vzome.core.viewing.ExportedVEFShapes(null, "rootTwoSmall", "small octahedra", "small connectors", symmetry);
            octahedralPerspective.setDefaultGeometry(defaultShapes);
            octahedralPerspective.addShapes(new com.vzome.core.viewing.ExportedVEFShapes(null, "rootTwoBig", "ornate", symmetry, defaultShapes));
            const rootTwoShapes: com.vzome.core.viewing.AbstractShapes = new com.vzome.core.viewing.ExportedVEFShapes(null, "rootTwo", "Schoch solid", "Tesseractix", symmetry, defaultShapes);
            octahedralPerspective.addShapes(rootTwoShapes);
            octahedralPerspective.addShapes(new com.vzome.core.viewing.ExportedVEFShapes(null, "root2Lifelike", "Schoch lifelike", symmetry, rootTwoShapes));
        }

        /**
         * 
         * @return {string}
         */
        public getLabel(): string {
            return "\u221a2";
        }

        /*private*/ synestructicsSymmetry: com.vzome.core.math.symmetry.Symmetry;

        /*private*/ synestructicsPerspective: com.vzome.core.editor.SymmetryPerspective;

        /**
         * 
         * @return {*}
         */
        public getSymmetryPerspectives(): java.util.Collection<com.vzome.core.editor.SymmetryPerspective> {
            return java.util.Arrays.asList<any>(super.getDefaultSymmetryPerspective(), this.synestructicsPerspective);
        }

        /**
         * 
         * @param {string} symmName
         * @return {*}
         */
        public getSymmetryPerspective(symmName: string): com.vzome.core.editor.SymmetryPerspective {
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

        export class RootTwoFieldApplication$0 extends com.vzome.core.math.symmetry.OctahedralSymmetry {
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
             * @param {com.vzome.core.math.symmetry.SpecialOrbit} which
             * @return {com.vzome.core.math.symmetry.Direction}
             */
            public getSpecialOrbit(which: com.vzome.core.math.symmetry.SpecialOrbit): com.vzome.core.math.symmetry.Direction {
                switch((which)) {
                case com.vzome.core.math.symmetry.SpecialOrbit.BLUE:
                    return this.getDirection(this.frameColor);
                case com.vzome.core.math.symmetry.SpecialOrbit.RED:
                    return this.getDirection("magenta");
                case com.vzome.core.math.symmetry.SpecialOrbit.YELLOW:
                    return this.getDirection("yellow");
                default:
                    return null;
                }
            }

            /**
             * 
             * @return {com.vzome.core.algebra.AlgebraicVector[]}
             */
            public getOrbitTriangle(): com.vzome.core.algebra.AlgebraicVector[] {
                const magentaVertex: com.vzome.core.algebra.AlgebraicVector = this.getDirection("magenta").getPrototype();
                const orangeVertex: com.vzome.core.algebra.AlgebraicVector = this.getDirection(this.frameColor).getPrototype();
                const yellowVertex: com.vzome.core.algebra.AlgebraicVector = this.getDirection("yellow").getPrototype();
                return [magentaVertex, orangeVertex, yellowVertex];
            }

            /**
             * 
             */
            createOtherOrbits() {
                let v: com.vzome.core.algebra.AlgebraicVector = new com.vzome.core.algebra.AlgebraicVector(this.mField.one(), this.mField.one(), this.mField.one());
                this.createZoneOrbit$java_lang_String$int$int$com_vzome_core_algebra_AlgebraicVector$boolean("yellow", 0, 4, v, true);
                const sqrt2: com.vzome.core.algebra.AlgebraicNumber = this.mField['createPower$int'](1);
                const half: com.vzome.core.algebra.AlgebraicNumber = this.mField['createRational$long$long'](1, 2);
                v = new com.vzome.core.algebra.AlgebraicVector(sqrt2, sqrt2, this.mField.zero()).scale(half);
                this.createZoneOrbit$java_lang_String$int$int$com_vzome_core_algebra_AlgebraicVector$boolean("magenta", 1, 8, v, true);
                v = new com.vzome.core.algebra.AlgebraicVector(this.mField.one(), this.mField.one(), this.mField.one()['plus$com_vzome_core_algebra_AlgebraicNumber'](this.mField.one()));
                this.createZoneOrbit$java_lang_String$int$int$com_vzome_core_algebra_AlgebraicVector$boolean("brown", 0, com.vzome.core.math.symmetry.Symmetry.NO_ROTATION, v, true);
            }

            constructor(__parent: any, __arg0: any, __arg1: any) {
                super(__arg0, __arg1);
                this.__parent = __parent;
            }
        }
        RootTwoFieldApplication$0["__interfaces"] = ["com.vzome.core.math.symmetry.Symmetry","com.vzome.core.math.symmetry.Embedding"];



        export class RootTwoFieldApplication$1 extends com.vzome.core.kinds.AbstractSymmetryPerspective {
            public __parent: any;
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
                return "org/vorthmann/zome/app/octahedral-vef.vZome";
            }

            constructor(__parent: any, __arg0: any) {
                super(__arg0);
                this.__parent = __parent;
                (() => {
                    const defaultShapes: com.vzome.core.viewing.AbstractShapes = new com.vzome.core.viewing.ExportedVEFShapes(null, "rootTwoSmall", "small octahedra", this.symmetry, null);
                    const synestructicsShapes: com.vzome.core.viewing.AbstractShapes = new com.vzome.core.viewing.ExportedVEFShapes(null, "rootTwo", "Synestructics", this.symmetry, defaultShapes);
                    const ornateShapes: com.vzome.core.viewing.AbstractShapes = new com.vzome.core.viewing.ExportedVEFShapes(null, "rootTwoBig", "ornate", this.symmetry, defaultShapes);
                    this.setDefaultGeometry(defaultShapes);
                    this.addShapes(synestructicsShapes);
                    this.addShapes(ornateShapes);
                })();
            }
        }
        RootTwoFieldApplication$1["__interfaces"] = ["com.vzome.core.editor.SymmetryPerspective"];


    }

}

