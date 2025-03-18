/* Generated from Java with JSweet 3.2.0-SNAPSHOT - http://www.jsweet.org */
namespace com.vzome.core.commands {
    /**
     * @author Scott Vorthmann
     * @class
     * @extends com.vzome.core.commands.AbstractCommand
     */
    export class CommandLoad extends com.vzome.core.commands.AbstractCommand {
        public static XML_ATTR: string = "xml";

        static PARAM_SIGNATURE: any[][]; public static PARAM_SIGNATURE_$LI$(): any[][] { if (CommandLoad.PARAM_SIGNATURE == null) { CommandLoad.PARAM_SIGNATURE = []; }  return CommandLoad.PARAM_SIGNATURE; }

        static ATTR_SIGNATURE: any[][]; public static ATTR_SIGNATURE_$LI$(): any[][] { if (CommandLoad.ATTR_SIGNATURE == null) { CommandLoad.ATTR_SIGNATURE = [[CommandLoad.XML_ATTR, "org.w3c.dom.Element"]]; }  return CommandLoad.ATTR_SIGNATURE; }

        /**
         * 
         * @return {java.lang.Object[][]}
         */
        public getParameterSignature(): any[][] {
            return CommandLoad.PARAM_SIGNATURE_$LI$();
        }

        /**
         * 
         * @return {java.lang.Object[][]}
         */
        public getAttributeSignature(): any[][] {
            return CommandLoad.ATTR_SIGNATURE_$LI$();
        }

        /**
         * 
         * @param {com.vzome.core.construction.ConstructionList} parameters
         * @param {com.vzome.core.commands.AttributeMap} attributes
         * @param {*} effects
         * @return {com.vzome.core.construction.ConstructionList}
         */
        public apply(parameters: com.vzome.core.construction.ConstructionList, attributes: com.vzome.core.commands.AttributeMap, effects: com.vzome.core.construction.ConstructionChanges): com.vzome.core.construction.ConstructionList {
            const result: com.vzome.core.construction.ConstructionList = new com.vzome.core.construction.ConstructionList();
            return result;
        }

        constructor() {
            super();
        }
    }
    CommandLoad["__class"] = "com.vzome.core.commands.CommandLoad";
    CommandLoad["__interfaces"] = ["com.vzome.core.commands.Command"];


}

