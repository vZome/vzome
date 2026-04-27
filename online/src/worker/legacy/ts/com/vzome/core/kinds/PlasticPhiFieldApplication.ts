/* Generated from Java with JSweet 3.2.0-SNAPSHOT - http://www.jsweet.org */
namespace com.vzome.core.kinds {
    export class PlasticPhiFieldApplication extends com.vzome.core.kinds.DefaultFieldApplication {
        /*private*/ icosahedralPerspective: com.vzome.core.kinds.IcosahedralSymmetryPerspective;

        symmetryPerspectives: java.util.List<com.vzome.core.editor.SymmetryPerspective>;

        /*private*/ H4: com.vzome.core.math.symmetry.QuaternionicSymmetry;

        public constructor(field: com.vzome.core.algebra.PlasticPhiField) {
            super(field);
            if (this.icosahedralPerspective === undefined) { this.icosahedralPerspective = null; }
            this.symmetryPerspectives = <any>(new java.util.ArrayList<any>());
            if (this.H4 === undefined) { this.H4 = null; }
            this.h4Builder = null;
            this.icosahedralPerspective = new com.vzome.core.kinds.IcosahedralSymmetryPerspective(this.getField());
            this.symmetryPerspectives.add(this.icosahedralPerspective);
            this.symmetryPerspectives.add(super.getDefaultSymmetryPerspective());
            this.H4 = new com.vzome.core.math.symmetry.QuaternionicSymmetry("H_4", "com/vzome/core/math/symmetry/H4roots.vef", this.getField());
        }

        /**
         * 
         * @return {com.vzome.core.algebra.PlasticPhiField}
         */
        public getField(): com.vzome.core.algebra.PlasticPhiField {
            return <com.vzome.core.algebra.PlasticPhiField><any>super.getField();
        }

        /**
         * 
         * @return {*}
         */
        public getSymmetryPerspectives(): java.util.Collection<com.vzome.core.editor.SymmetryPerspective> {
            return this.symmetryPerspectives;
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
            const symm: com.vzome.core.math.symmetry.IcosahedralSymmetry = this.icosahedralPerspective.getSymmetry();
            toolFactories.put("AxialStretchTool", new com.vzome.core.tools.AxialStretchTool.Factory(tools, symm, false, false, false));
            toolFactories.put("SymmetryTool", new com.vzome.core.tools.IcosahedralToolFactory(tools, symm));
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
    PlasticPhiFieldApplication["__class"] = "com.vzome.core.kinds.PlasticPhiFieldApplication";
    PlasticPhiFieldApplication["__interfaces"] = ["com.vzome.core.math.symmetry.Symmetries4D","com.vzome.core.editor.FieldApplication"];


}

