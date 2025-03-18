/* Generated from Java with JSweet 3.2.0-SNAPSHOT - http://www.jsweet.org */
namespace com.vzome.core.commands {
    /**
     * @author Scott Vorthmann
     * @class
     * @extends com.vzome.core.commands.AbstractCommand
     */
    export class CommandMidpoint extends com.vzome.core.commands.AbstractCommand {
        static PARAM_SIGNATURE: any[][]; public static PARAM_SIGNATURE_$LI$(): any[][] { if (CommandMidpoint.PARAM_SIGNATURE == null) { CommandMidpoint.PARAM_SIGNATURE = [["segment", com.vzome.core.construction.Segment]]; }  return CommandMidpoint.PARAM_SIGNATURE; }

        static ATTR_SIGNATURE: any[][]; public static ATTR_SIGNATURE_$LI$(): any[][] { if (CommandMidpoint.ATTR_SIGNATURE == null) { CommandMidpoint.ATTR_SIGNATURE = []; }  return CommandMidpoint.ATTR_SIGNATURE; }

        /**
         * 
         * @return {java.lang.Object[][]}
         */
        public getParameterSignature(): any[][] {
            return CommandMidpoint.PARAM_SIGNATURE_$LI$();
        }

        /**
         * 
         * @return {java.lang.Object[][]}
         */
        public getAttributeSignature(): any[][] {
            return CommandMidpoint.ATTR_SIGNATURE_$LI$();
        }

        /**
         * 
         * @param {com.vzome.core.construction.ConstructionList} parameters
         * @param {com.vzome.core.commands.AttributeMap} attrs
         * @param {*} effects
         * @return {com.vzome.core.construction.ConstructionList}
         */
        public apply(parameters: com.vzome.core.construction.ConstructionList, attrs: com.vzome.core.commands.AttributeMap, effects: com.vzome.core.construction.ConstructionChanges): com.vzome.core.construction.ConstructionList {
            const result: com.vzome.core.construction.ConstructionList = new com.vzome.core.construction.ConstructionList();
            if (parameters == null || parameters.size() !== 1)throw new Command.Failure("Midpoint can only apply to a single strut.");
            try {
                const segment: com.vzome.core.construction.Segment = <com.vzome.core.construction.Segment>parameters.get(0);
                const midpoint: com.vzome.core.construction.Point = new com.vzome.core.construction.SegmentMidpoint(segment);
                result.addConstruction(midpoint);
                effects['constructionAdded$com_vzome_core_construction_Construction'](midpoint);
            } catch(e) {
                throw new Command.Failure("Midpoint can only apply to a strut.");
            }
            return result;
        }

        constructor() {
            super();
        }
    }
    CommandMidpoint["__class"] = "com.vzome.core.commands.CommandMidpoint";
    CommandMidpoint["__interfaces"] = ["com.vzome.core.commands.Command"];


}

