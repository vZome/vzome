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
    export class SnubDodecFieldApplication extends com.vzome.core.kinds.DefaultFieldApplication {
        /*private*/ icosahedralPerspective: com.vzome.core.kinds.IcosahedralSymmetryPerspective;

        public constructor(field: com.vzome.core.algebra.AlgebraicField) {
            super(field);
            if (this.icosahedralPerspective === undefined) { this.icosahedralPerspective = null; }
            this.h4Builder = null;
            this.cmdTauDivide = new com.vzome.core.commands.CommandTauDivision();
            const icosaSymm: com.vzome.core.math.symmetry.IcosahedralSymmetry = new SnubDodecFieldApplication.SnubDodecFieldApplication$0(this, field);
            this.icosahedralPerspective = new com.vzome.core.kinds.IcosahedralSymmetryPerspective(icosaSymm);
        }

        /**
         * 
         * @return {string}
         */
        public getName(): string {
            return this.getField().getName();
        }

        /**
         * 
         * @return {string}
         */
        public getLabel(): string {
            return "Snub Dodecahedron";
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
    SnubDodecFieldApplication["__class"] = "com.vzome.core.kinds.SnubDodecFieldApplication";
    SnubDodecFieldApplication["__interfaces"] = ["com.vzome.core.math.symmetry.Symmetries4D","com.vzome.core.editor.FieldApplication"];



    export namespace SnubDodecFieldApplication {

        export class SnubDodecFieldApplication$0 extends com.vzome.core.math.symmetry.IcosahedralSymmetry {
            public __parent: any;
            /**
             * 
             */
            createOtherOrbits() {
                super.createOtherOrbits();
                const vSnubPentagon: com.vzome.core.algebra.AlgebraicVector = this.mField.createIntegerVector([[4, -4, 0, 0, -2, 2], [-4, 0, 0, 0, 2, 0], [0, 0, 0, 0, 0, 2]]);
                const vSnubTriangle: com.vzome.core.algebra.AlgebraicVector = this.mField.createIntegerVector([[0, -4, -2, 0, 0, 2], [-4, 4, 0, -2, 2, -2], [-4, 0, -2, -2, 2, 0]]);
                const vSnubDiagonal: com.vzome.core.algebra.AlgebraicVector = this.mField.createIntegerVector([[8, 0, 0, 4, -4, 0], [0, -4, 0, 0, 0, 0], [0, 0, 0, 0, 0, 0]]);
                const vSnubFaceNorm: com.vzome.core.algebra.AlgebraicVector = this.mField.createIntegerVector([[-1, 0, 1, -1, 1, 0], [1, 0, 0, 0, 0, 0], [1, 0, -1, 2, 0, 1]]);
                const vSnubVertex: com.vzome.core.algebra.AlgebraicVector = this.mField.createIntegerVector([[1, 0, 0, 0, 0, 0], [1, 0, -1, 1, -1, 0], [1, 0, 0, 1, -1, 1]]);
                const scale: com.vzome.core.algebra.AlgebraicNumber = this.mField['createPower$int'](-3);
                let scaleFaceNorm: com.vzome.core.algebra.AlgebraicNumber;
                let scaleVertex: com.vzome.core.algebra.AlgebraicNumber = this.mField.one();
                scaleFaceNorm = this.mField['createAlgebraicNumber$int_A']([-3, 2, 2, -1, 5, -3]).reciprocal();
                scaleVertex = this.mField['createAlgebraicNumber$int_A$int']([-3, 2, 7, -4, 2, -1], 3).reciprocal();
                this.createZoneOrbit$java_lang_String$int$int$com_vzome_core_algebra_AlgebraicVector$boolean$boolean$com_vzome_core_algebra_AlgebraicNumber("snubPentagon", 0, com.vzome.core.math.symmetry.Symmetry.NO_ROTATION, vSnubPentagon, false, false, scale).withCorrection();
                this.createZoneOrbit$java_lang_String$int$int$com_vzome_core_algebra_AlgebraicVector$boolean$boolean$com_vzome_core_algebra_AlgebraicNumber("snubTriangle", 0, com.vzome.core.math.symmetry.Symmetry.NO_ROTATION, vSnubTriangle, false, false, scale).withCorrection();
                this.createZoneOrbit$java_lang_String$int$int$com_vzome_core_algebra_AlgebraicVector$boolean$boolean$com_vzome_core_algebra_AlgebraicNumber("snubDiagonal", 0, com.vzome.core.math.symmetry.Symmetry.NO_ROTATION, vSnubDiagonal, false, false, scale).withCorrection();
                this.createZoneOrbit$java_lang_String$int$int$com_vzome_core_algebra_AlgebraicVector$boolean$boolean$com_vzome_core_algebra_AlgebraicNumber("snubFaceNormal", 0, com.vzome.core.math.symmetry.Symmetry.NO_ROTATION, vSnubFaceNorm, false, false, scale['times$com_vzome_core_algebra_AlgebraicNumber'](scaleFaceNorm)).withCorrection();
                this.createZoneOrbit$java_lang_String$int$int$com_vzome_core_algebra_AlgebraicVector$boolean$boolean$com_vzome_core_algebra_AlgebraicNumber("snubVertex", 0, com.vzome.core.math.symmetry.Symmetry.NO_ROTATION, vSnubVertex, true, false, scale['times$com_vzome_core_algebra_AlgebraicNumber'](scaleVertex)).withCorrection();
            }

            constructor(__parent: any, __arg0: any) {
                super(__arg0);
                this.__parent = __parent;
            }
        }
        SnubDodecFieldApplication$0["__interfaces"] = ["com.vzome.core.math.symmetry.Symmetry","com.vzome.core.math.symmetry.Embedding"];


    }

}

