/* Generated from Java with JSweet 3.2.0-SNAPSHOT - http://www.jsweet.org */
namespace com.vzome.core.kinds {
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
    export class GoldenFieldApplication extends com.vzome.core.kinds.DefaultFieldApplication {
        /*private*/ icosahedralPerspective: com.vzome.core.kinds.IcosahedralSymmetryPerspective;

        public constructor(field: com.vzome.core.algebra.AlgebraicField) {
            super(field);
            if (this.icosahedralPerspective === undefined) { this.icosahedralPerspective = null; }
            this.h4Builder = null;
            this.cmdTauDivide = new com.vzome.core.commands.CommandTauDivision();
            this.icosahedralPerspective = new com.vzome.core.kinds.IcosahedralSymmetryPerspective(this.getField());
            const octahedralPerspective: com.vzome.core.kinds.OctahedralSymmetryPerspective = <com.vzome.core.kinds.OctahedralSymmetryPerspective><any>super.getDefaultSymmetryPerspective();
            const symm: com.vzome.core.math.symmetry.AbstractSymmetry = octahedralPerspective.getSymmetry();
            symm.createZoneOrbit$java_lang_String$int$int$int_A_A$boolean$boolean$com_vzome_core_algebra_AlgebraicNumber("yellow", 0, 4, [[0, 1, 1, 1], [0, 1, 1, 1], [0, 1, 1, 1]], true, false, this.getField()['createPower$int'](-1));
            symm.createZoneOrbit$java_lang_String$int$int$int_A_A$boolean$boolean$com_vzome_core_algebra_AlgebraicNumber("green", 1, 8, [[1, 1, 0, 1], [1, 1, 0, 1], [0, 1, 0, 1]], true, true, this.getField()['createRational$long'](2));
            symm.createZoneOrbit$java_lang_String$int$int$int_A_A("lavender", 0, com.vzome.core.math.symmetry.Symmetry.NO_ROTATION, [[2, 1, -1, 1], [0, 1, 1, 1], [2, 1, -1, 1]]);
            symm.createZoneOrbit$java_lang_String$int$int$int_A_A("olive", 0, com.vzome.core.math.symmetry.Symmetry.NO_ROTATION, [[0, 1, 1, 1], [0, 1, 1, 1], [2, 1, -1, 1]]);
            symm.createZoneOrbit$java_lang_String$int$int$int_A_A("maroon", 0, com.vzome.core.math.symmetry.Symmetry.NO_ROTATION, [[-1, 1, 1, 1], [3, 1, -1, 1], [1, 1, -1, 1]]);
            symm.createZoneOrbit$java_lang_String$int$int$int_A_A("brown", 0, com.vzome.core.math.symmetry.Symmetry.NO_ROTATION, [[-1, 1, 1, 1], [-1, 1, 1, 1], [-2, 1, 2, 1]]);
            symm.createZoneOrbit$java_lang_String$int$int$int_A_A("red", 0, com.vzome.core.math.symmetry.Symmetry.NO_ROTATION, [[0, 1, 1, 1], [1, 1, 0, 1], [0, 1, 0, 1]]);
            symm.createZoneOrbit$java_lang_String$int$int$int_A_A$boolean$boolean$com_vzome_core_algebra_AlgebraicNumber("purple", 0, com.vzome.core.math.symmetry.Symmetry.NO_ROTATION, [[1, 1, 1, 1], [0, 1, 0, 1], [-1, 1, 0, 1]], false, false, this.getField()['createPower$int'](-1));
            symm.createZoneOrbit$java_lang_String$int$int$int_A_A$boolean$boolean$com_vzome_core_algebra_AlgebraicNumber("black", 0, com.vzome.core.math.symmetry.Symmetry.NO_ROTATION, [[1, 2, 0, 1], [0, 1, 1, 2], [-1, 2, 1, 2]], false, false, this.getField()['createRational$long'](2));
            symm.createZoneOrbit$java_lang_String$int$int$int_A_A("turquoise", 0, com.vzome.core.math.symmetry.Symmetry.NO_ROTATION, [[1, 1, 2, 1], [3, 1, 4, 1], [3, 1, 4, 1]]);
            const defaultShapes: com.vzome.core.viewing.AbstractShapes = new com.vzome.core.viewing.ExportedVEFShapes(null, "octahedral", "trapezoids", symm, null);
            octahedralPerspective.setDefaultGeometry(defaultShapes);
            octahedralPerspective.addShapes(new com.vzome.core.viewing.ExportedVEFShapes(null, "octahedralFast", "small octahedra", symm, null));
            octahedralPerspective.addShapes(new com.vzome.core.viewing.ExportedVEFShapes(null, "octahedralRealistic", "vZome logo", symm, defaultShapes));
        }

        /**
         * 
         * @return {string}
         */
        public getLabel(): string {
            return "Zome (Golden)";
        }

        /**
         * 
         * @return {*}
         */
        public getSymmetryPerspectives(): java.util.Collection<com.vzome.core.editor.SymmetryPerspective> {
            return java.util.Arrays.asList<any>(this.icosahedralPerspective, super.getDefaultSymmetryPerspective());
        }

        /**
         * 
         * @return {*}
         */
        public getDefaultSymmetryPerspective(): com.vzome.core.editor.SymmetryPerspective {
            return this.icosahedralPerspective;
        }

        /**
         * 
         * @param {string} symmName
         * @return {*}
         */
        public getSymmetryPerspective(symmName: string): com.vzome.core.editor.SymmetryPerspective {
            switch((symmName)) {
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
            return this.icosahedralPerspective.getQuaternionSymmetry(name);
        }

        /**
         * 
         * @param {*} toolFactories
         * @param {com.vzome.core.editor.ToolsModel} tools
         */
        public registerToolFactories(toolFactories: java.util.Map<string, com.vzome.api.Tool.Factory>, tools: com.vzome.core.editor.ToolsModel) {
            super.registerToolFactories(toolFactories, tools);
            const symm: com.vzome.core.math.symmetry.IcosahedralSymmetry = this.icosahedralPerspective.getSymmetry();
            toolFactories.put("AxialStretchTool", new com.vzome.core.tools.AxialStretchTool.Factory(tools, symm, false, false, false));
            toolFactories.put("SymmetryTool", new com.vzome.core.tools.IcosahedralToolFactory(tools, symm));
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

        /*private*/ cmdTauDivide: com.vzome.core.commands.Command;

        /**
         * 
         * @param {string} action
         * @return {*}
         */
        public getLegacyCommand(action: string): com.vzome.core.commands.Command {
            switch((action)) {
            case "tauDivide":
                return this.cmdTauDivide;
            default:
                return super.getLegacyCommand(action);
            }
        }
    }
    GoldenFieldApplication["__class"] = "com.vzome.core.kinds.GoldenFieldApplication";
    GoldenFieldApplication["__interfaces"] = ["com.vzome.core.math.symmetry.Symmetries4D","com.vzome.core.editor.FieldApplication"];


}

