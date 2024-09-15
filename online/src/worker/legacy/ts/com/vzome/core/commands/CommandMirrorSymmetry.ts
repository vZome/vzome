/* Generated from Java with JSweet 3.2.0-SNAPSHOT - http://www.jsweet.org */
namespace com.vzome.core.commands {
    /**
     * @author Scott Vorthmann
     * @class
     * @extends com.vzome.core.commands.CommandTransform
     */
    export class CommandMirrorSymmetry extends com.vzome.core.commands.CommandTransform {
        /**
         * 
         * @param {com.vzome.core.construction.ConstructionList} parameters
         * @param {com.vzome.core.commands.AttributeMap} attributes
         * @param {*} effects
         * @return {com.vzome.core.construction.ConstructionList}
         */
        public apply(parameters: com.vzome.core.construction.ConstructionList, attributes: com.vzome.core.commands.AttributeMap, effects: com.vzome.core.construction.ConstructionChanges): com.vzome.core.construction.ConstructionList {
            const center: com.vzome.core.construction.Point = <com.vzome.core.construction.Point>attributes.get(com.vzome.core.commands.CommandTransform.SYMMETRY_CENTER_ATTR_NAME);
            const norm: com.vzome.core.construction.Segment = <com.vzome.core.construction.Segment>attributes.get(com.vzome.core.commands.CommandTransform.SYMMETRY_AXIS_ATTR_NAME);
            if (norm == null){
                throw new com.vzome.core.commands.Command.Failure("no symmetry axis provided");
            }
            const params: com.vzome.core.construction.Construction[] = parameters.getConstructions();
            const mirror: com.vzome.core.construction.Plane = new com.vzome.core.construction.PlaneFromNormalSegment(center, norm);
            effects['constructionAdded$com_vzome_core_construction_Construction'](mirror);
            const transform: com.vzome.core.construction.Transformation = new com.vzome.core.construction.PlaneReflection(mirror);
            return this.transform(params, transform, effects);
        }

        constructor() {
            super();
        }
    }
    CommandMirrorSymmetry["__class"] = "com.vzome.core.commands.CommandMirrorSymmetry";
    CommandMirrorSymmetry["__interfaces"] = ["com.vzome.core.commands.Command"];


}

