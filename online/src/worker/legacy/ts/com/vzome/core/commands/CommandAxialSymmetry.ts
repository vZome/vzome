/* Generated from Java with JSweet 3.2.0-SNAPSHOT - http://www.jsweet.org */
namespace com.vzome.core.commands {
    /**
     * @author Scott Vorthmann
     * @param {*} symmetry
     * @class
     * @extends com.vzome.core.commands.CommandSymmetry
     */
    export class CommandAxialSymmetry extends com.vzome.core.commands.CommandSymmetry {
        public constructor(symmetry: com.vzome.core.math.symmetry.Symmetry = null) {
            super(symmetry);
        }

        /**
         * 
         * @param {com.vzome.core.construction.ConstructionList} parameters
         * @param {com.vzome.core.commands.AttributeMap} attributes
         * @param {*} effects
         * @return {com.vzome.core.construction.ConstructionList}
         */
        public apply(parameters: com.vzome.core.construction.ConstructionList, attributes: com.vzome.core.commands.AttributeMap, effects: com.vzome.core.construction.ConstructionChanges): com.vzome.core.construction.ConstructionList {
            this.setSymmetry(attributes);
            const norm: com.vzome.core.construction.Segment = <com.vzome.core.construction.Segment>attributes.get(com.vzome.core.commands.CommandTransform.SYMMETRY_AXIS_ATTR_NAME);
            if (norm == null){
                throw new com.vzome.core.commands.Command.Failure("no symmetry axis provided");
            }
            const output: com.vzome.core.construction.ConstructionList = new com.vzome.core.construction.ConstructionList();
            let vector: com.vzome.core.algebra.AlgebraicVector = norm.getOffset();
            vector = norm.getField().projectTo3d(vector, true);
            const axis: com.vzome.core.math.symmetry.Axis = this.mSymmetry['getAxis$com_vzome_core_algebra_AlgebraicVector'](vector);
            const rotation: com.vzome.core.math.symmetry.Permutation = axis.getRotationPermutation();
            if (rotation == null){
                throw new com.vzome.core.commands.Command.Failure("symmetry axis does not support axial symmetry");
            }
            const order: number = rotation.getOrder();
            const rotate: com.vzome.core.commands.CommandRotate = new com.vzome.core.commands.CommandRotate();
            for(let i: number = 1; i < order; i++) {{
                for(let index=parameters.iterator();index.hasNext();) {
                    let param = index.next();
                    {
                        output.addConstruction(param);
                    }
                }
                parameters = rotate.apply(parameters, attributes, effects);
            };}
            for(let index=parameters.iterator();index.hasNext();) {
                let param = index.next();
                {
                    output.addConstruction(param);
                }
            }
            return output;
        }
    }
    CommandAxialSymmetry["__class"] = "com.vzome.core.commands.CommandAxialSymmetry";
    CommandAxialSymmetry["__interfaces"] = ["com.vzome.core.commands.Command"];


}

