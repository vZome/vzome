/* Generated from Java with JSweet 3.2.0-SNAPSHOT - http://www.jsweet.org */
namespace com.vzome.core.commands {
    /**
     * @author Scott Vorthmann
     * @param {*} symmetry
     * @class
     * @extends com.vzome.core.commands.CommandSymmetry
     */
    export class CommandTetrahedralSymmetry extends com.vzome.core.commands.CommandSymmetry {
        public static SUBGROUP_ATTR_NAME: string = "symmetry.permutations";

        static ATTR_SIGNATURE: any[][]; public static ATTR_SIGNATURE_$LI$(): any[][] { if (CommandTetrahedralSymmetry.ATTR_SIGNATURE == null) { CommandTetrahedralSymmetry.ATTR_SIGNATURE = [[com.vzome.core.commands.CommandTransform.SYMMETRY_CENTER_ATTR_NAME, com.vzome.core.construction.Point], [com.vzome.core.commands.CommandTransform.SYMMETRY_GROUP_ATTR_NAME, "com.vzome.core.math.symmetry.Symmetry"], [CommandTetrahedralSymmetry.SUBGROUP_ATTR_NAME, (<any>[].constructor)]]; }  return CommandTetrahedralSymmetry.ATTR_SIGNATURE; }

        public constructor(symmetry: com.vzome.core.math.symmetry.Symmetry = null) {
            super(symmetry);
        }

        /**
         * 
         * @return {java.lang.Object[][]}
         */
        public getAttributeSignature(): any[][] {
            return CommandTetrahedralSymmetry.ATTR_SIGNATURE_$LI$();
        }

        /**
         * 
         * @param {com.vzome.core.construction.ConstructionList} parameters
         * @param {com.vzome.core.commands.AttributeMap} attributes
         * @param {*} effects
         * @return {com.vzome.core.construction.ConstructionList}
         */
        public apply(parameters: com.vzome.core.construction.ConstructionList, attributes: com.vzome.core.commands.AttributeMap, effects: com.vzome.core.construction.ConstructionChanges): com.vzome.core.construction.ConstructionList {
            const center: com.vzome.core.construction.Point = this.setSymmetry(attributes);
            const closure: number[] = this.mSymmetry.subgroup(com.vzome.core.math.symmetry.Symmetry.TETRAHEDRAL);
            const params: com.vzome.core.construction.Construction[] = parameters.getConstructions();
            const output: com.vzome.core.construction.ConstructionList = new com.vzome.core.construction.ConstructionList();
            for(let index = 0; index < params.length; index++) {
                let param = params[index];
                {
                    output.addConstruction(param);
                }
            }
            for(let i: number = 1; i < closure.length; i++) {{
                const transform: com.vzome.core.construction.Transformation = new com.vzome.core.construction.SymmetryTransformation(this.mSymmetry, closure[i], center);
                output.addAll(this.transform(params, transform, effects));
            };}
            return output;
        }
    }
    CommandTetrahedralSymmetry["__class"] = "com.vzome.core.commands.CommandTetrahedralSymmetry";
    CommandTetrahedralSymmetry["__interfaces"] = ["com.vzome.core.commands.Command"];


}

