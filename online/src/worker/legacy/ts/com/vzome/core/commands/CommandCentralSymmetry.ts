/* Generated from Java with JSweet 3.2.0-SNAPSHOT - http://www.jsweet.org */
namespace com.vzome.core.commands {
    /**
     * @author Scott Vorthmann
     * @class
     * @extends com.vzome.core.commands.CommandTransform
     */
    export class CommandCentralSymmetry extends com.vzome.core.commands.CommandTransform {
        /**
         * 
         * @return {java.lang.Object[][]}
         */
        public getAttributeSignature(): any[][] {
            return com.vzome.core.commands.CommandTransform.ATTR_SIGNATURE_$LI$();
        }

        /**
         * 
         * @param {com.vzome.core.construction.ConstructionList} parameters
         * @param {com.vzome.core.commands.AttributeMap} attributes
         * @param {*} effects
         * @return {com.vzome.core.construction.ConstructionList}
         */
        public apply(parameters: com.vzome.core.construction.ConstructionList, attributes: com.vzome.core.commands.AttributeMap, effects: com.vzome.core.construction.ConstructionChanges): com.vzome.core.construction.ConstructionList {
            const output: com.vzome.core.construction.ConstructionList = new com.vzome.core.construction.ConstructionList();
            const center: com.vzome.core.construction.Point = <com.vzome.core.construction.Point>attributes.get(com.vzome.core.commands.CommandTransform.SYMMETRY_CENTER_ATTR_NAME);
            const params: com.vzome.core.construction.Construction[] = parameters.getConstructions();
            for(let index = 0; index < params.length; index++) {
                let param = params[index];
                {
                    output.addConstruction(param);
                }
            }
            const transform: com.vzome.core.construction.Transformation = new com.vzome.core.construction.PointReflection(center);
            effects['constructionAdded$com_vzome_core_construction_Construction'](transform);
            return this.transform(params, transform, effects);
        }

        constructor() {
            super();
        }
    }
    CommandCentralSymmetry["__class"] = "com.vzome.core.commands.CommandCentralSymmetry";
    CommandCentralSymmetry["__interfaces"] = ["com.vzome.core.commands.Command"];


}

