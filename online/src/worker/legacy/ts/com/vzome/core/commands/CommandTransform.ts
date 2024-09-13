/* Generated from Java with JSweet 3.2.0-SNAPSHOT - http://www.jsweet.org */
namespace com.vzome.core.commands {
    /**
     * @author Scott Vorthmann
     * @class
     * @extends com.vzome.core.commands.AbstractCommand
     */
    export abstract class CommandTransform extends com.vzome.core.commands.AbstractCommand {
        /**
         * 
         * @param {com.vzome.core.commands.AttributeMap} attributes
         * @param {com.vzome.core.commands.XmlSaveFormat} format
         */
        public setFixedAttributes(attributes: com.vzome.core.commands.AttributeMap, format: com.vzome.core.commands.XmlSaveFormat) {
            if (format.getScale() !== 0)attributes.put(CommandTransform.SCALE_ATTR_NAME, format.getScale());
            super.setFixedAttributes(attributes, format);
        }

        /**
         * 
         * @param {com.vzome.core.construction.ConstructionList} parameters
         * @param {com.vzome.core.commands.AttributeMap} attributes
         * @param {*} effects
         * @return {com.vzome.core.construction.ConstructionList}
         */
        public apply(parameters: com.vzome.core.construction.ConstructionList, attributes: com.vzome.core.commands.AttributeMap, effects: com.vzome.core.construction.ConstructionChanges): com.vzome.core.construction.ConstructionList {
            return null;
        }

        public static SYMMETRY_GROUP_ATTR_NAME: string = "symmetry.group";

        public static SYMMETRY_CENTER_ATTR_NAME: string = "symmetry.center";

        public static SYMMETRY_AXIS_ATTR_NAME: string = "symmetry.axis.segment";

        public static SCALE_ATTR_NAME: string = "scale.factor";

        static PARAM_SIGNATURE: any[][]; public static PARAM_SIGNATURE_$LI$(): any[][] { if (CommandTransform.PARAM_SIGNATURE == null) { CommandTransform.PARAM_SIGNATURE = [[com.vzome.core.commands.Command.GENERIC_PARAM_NAME, com.vzome.core.construction.Construction]]; }  return CommandTransform.PARAM_SIGNATURE; }

        static ATTR_SIGNATURE: any[][]; public static ATTR_SIGNATURE_$LI$(): any[][] { if (CommandTransform.ATTR_SIGNATURE == null) { CommandTransform.ATTR_SIGNATURE = [[CommandTransform.SYMMETRY_CENTER_ATTR_NAME, com.vzome.core.construction.Point]]; }  return CommandTransform.ATTR_SIGNATURE; }

        static AXIS_ATTR_SIGNATURE: any[][]; public static AXIS_ATTR_SIGNATURE_$LI$(): any[][] { if (CommandTransform.AXIS_ATTR_SIGNATURE == null) { CommandTransform.AXIS_ATTR_SIGNATURE = [[CommandTransform.SYMMETRY_CENTER_ATTR_NAME, com.vzome.core.construction.Point], [CommandTransform.SYMMETRY_AXIS_ATTR_NAME, com.vzome.core.construction.Segment]]; }  return CommandTransform.AXIS_ATTR_SIGNATURE; }

        static GROUP_ATTR_SIGNATURE: any[][]; public static GROUP_ATTR_SIGNATURE_$LI$(): any[][] { if (CommandTransform.GROUP_ATTR_SIGNATURE == null) { CommandTransform.GROUP_ATTR_SIGNATURE = [[CommandTransform.SYMMETRY_CENTER_ATTR_NAME, com.vzome.core.construction.Point], [CommandTransform.SYMMETRY_GROUP_ATTR_NAME, "com.vzome.core.math.symmetry.Symmetry"]]; }  return CommandTransform.GROUP_ATTR_SIGNATURE; }

        /**
         * 
         * @return {java.lang.Object[][]}
         */
        public getParameterSignature(): any[][] {
            return CommandTransform.PARAM_SIGNATURE_$LI$();
        }

        /**
         * 
         * @return {java.lang.Object[][]}
         */
        public getAttributeSignature(): any[][] {
            return CommandTransform.AXIS_ATTR_SIGNATURE_$LI$();
        }

        transform(params: com.vzome.core.construction.Construction[], transform: com.vzome.core.construction.Transformation, effects: com.vzome.core.construction.ConstructionChanges): com.vzome.core.construction.ConstructionList {
            const output: com.vzome.core.construction.ConstructionList = new com.vzome.core.construction.ConstructionList();
            effects['constructionAdded$com_vzome_core_construction_Construction'](transform);
            for(let index = 0; index < params.length; index++) {
                let param = params[index];
                {
                    const result: com.vzome.core.construction.Construction = transform.transform$com_vzome_core_construction_Construction(param);
                    if (result == null)continue;
                    effects['constructionAdded$com_vzome_core_construction_Construction'](result);
                    output.addConstruction(result);
                }
            }
            return output;
        }

        constructor() {
            super();
        }
    }
    CommandTransform["__class"] = "com.vzome.core.commands.CommandTransform";
    CommandTransform["__interfaces"] = ["com.vzome.core.commands.Command"];


}

