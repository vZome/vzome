/* Generated from Java with JSweet 3.2.0-SNAPSHOT - http://www.jsweet.org */
namespace com.vzome.core.commands {
    /**
     * @author Scott Vorthmann
     * @class
     * @extends com.vzome.core.commands.CommandTransform
     */
    export class CommandTranslate extends com.vzome.core.commands.CommandTransform {
        /**
         * 
         * @param {com.vzome.core.construction.ConstructionList} parameters
         * @param {com.vzome.core.commands.AttributeMap} attributes
         * @param {*} effects
         * @return {com.vzome.core.construction.ConstructionList}
         */
        public apply(parameters: com.vzome.core.construction.ConstructionList, attributes: com.vzome.core.commands.AttributeMap, effects: com.vzome.core.construction.ConstructionChanges): com.vzome.core.construction.ConstructionList {
            const norm: com.vzome.core.construction.Segment = <com.vzome.core.construction.Segment>attributes.get(com.vzome.core.commands.CommandTransform.SYMMETRY_AXIS_ATTR_NAME);
            if (norm == null){
                throw new com.vzome.core.commands.Command.Failure("no symmetry axis provided");
            }
            const params: com.vzome.core.construction.Construction[] = parameters.getConstructions();
            const field: com.vzome.core.algebra.AlgebraicField = norm.getField();
            const offset: com.vzome.core.algebra.AlgebraicVector = field.projectTo3d(norm.getOffset(), true);
            const transform: com.vzome.core.construction.Transformation = new com.vzome.core.construction.Translation(offset);
            return this.transform(params, transform, effects);
        }

        constructor() {
            super();
        }
    }
    CommandTranslate["__class"] = "com.vzome.core.commands.CommandTranslate";
    CommandTranslate["__interfaces"] = ["com.vzome.core.commands.Command"];


}

