/* Generated from Java with JSweet 3.2.0-SNAPSHOT - http://www.jsweet.org */
namespace com.vzome.core.commands {
    /**
     * @author Scott Vorthmann
     * @class
     * @extends com.vzome.core.commands.CommandSymmetry
     */
    export class CommandRotate extends com.vzome.core.commands.CommandSymmetry {
        /**
         * 
         * @param {com.vzome.core.construction.ConstructionList} parameters
         * @param {com.vzome.core.commands.AttributeMap} attributes
         * @param {*} effects
         * @return {com.vzome.core.construction.ConstructionList}
         */
        public apply(parameters: com.vzome.core.construction.ConstructionList, attributes: com.vzome.core.commands.AttributeMap, effects: com.vzome.core.construction.ConstructionChanges): com.vzome.core.construction.ConstructionList {
            const center: com.vzome.core.construction.Point = this.setSymmetry(attributes);
            const norm: com.vzome.core.construction.Segment = <com.vzome.core.construction.Segment>attributes.get(com.vzome.core.commands.CommandTransform.SYMMETRY_AXIS_ATTR_NAME);
            if (norm == null){
                throw new com.vzome.core.commands.Command.Failure("no symmetry axis provided");
            }
            const params: com.vzome.core.construction.Construction[] = parameters.getConstructions();
            const output: com.vzome.core.construction.ConstructionList = new com.vzome.core.construction.ConstructionList();
            let vector: com.vzome.core.algebra.AlgebraicVector = norm.getOffset();
            vector = norm.getField().projectTo3d(vector, true);
            const axis: com.vzome.core.math.symmetry.Axis = this.mSymmetry['getAxis$com_vzome_core_algebra_AlgebraicVector'](vector);
            const rotation: number = axis.getRotation();
            const transform: com.vzome.core.construction.Transformation = new com.vzome.core.construction.SymmetryTransformation(this.mSymmetry, rotation, center);
            effects['constructionAdded$com_vzome_core_construction_Construction'](transform);
            output.addAll(this.transform(params, transform, effects));
            return output;
        }

        constructor() {
            super();
        }
    }
    CommandRotate["__class"] = "com.vzome.core.commands.CommandRotate";
    CommandRotate["__interfaces"] = ["com.vzome.core.commands.Command"];


}

