/* Generated from Java with JSweet 3.2.0-SNAPSHOT - http://www.jsweet.org */
namespace com.vzome.core.commands {
    export class CommandSetColor extends com.vzome.core.commands.AbstractCommand {
        public static MANIFESTATION_ATTR: string = "manifestation.context";

        public static COLOR_ATTR: string = "color";

        static PARAM_SIGNATURE: any[][]; public static PARAM_SIGNATURE_$LI$(): any[][] { if (CommandSetColor.PARAM_SIGNATURE == null) { CommandSetColor.PARAM_SIGNATURE = []; }  return CommandSetColor.PARAM_SIGNATURE; }

        /**
         * 
         * @return {java.lang.Object[][]}
         */
        public getParameterSignature(): any[][] {
            return CommandSetColor.PARAM_SIGNATURE_$LI$();
        }

        /**
         * 
         * @return {java.lang.Object[][]}
         */
        public getAttributeSignature(): any[][] {
            return null;
        }

        /**
         * 
         * @param {com.vzome.core.construction.ConstructionList} parameters
         * @param {com.vzome.core.commands.AttributeMap} attributes
         * @param {*} effects
         * @return {com.vzome.core.construction.ConstructionList}
         */
        public apply(parameters: com.vzome.core.construction.ConstructionList, attributes: com.vzome.core.commands.AttributeMap, effects: com.vzome.core.construction.ConstructionChanges): com.vzome.core.construction.ConstructionList {
            return parameters;
        }

        constructor() {
            super();
        }
    }
    CommandSetColor["__class"] = "com.vzome.core.commands.CommandSetColor";
    CommandSetColor["__interfaces"] = ["com.vzome.core.commands.Command"];


}

