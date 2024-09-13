/* Generated from Java with JSweet 3.2.0-SNAPSHOT - http://www.jsweet.org */
namespace com.vzome.core.commands {
    /**
     * @author Scott Vorthmann
     * @class
     * @extends com.vzome.core.commands.AbstractCommand
     */
    export class CommandJoinPoints extends com.vzome.core.commands.AbstractCommand {
        static PARAM_SIGNATURE: any[][]; public static PARAM_SIGNATURE_$LI$(): any[][] { if (CommandJoinPoints.PARAM_SIGNATURE == null) { CommandJoinPoints.PARAM_SIGNATURE = [["start", com.vzome.core.construction.Point], ["end", com.vzome.core.construction.Point]]; }  return CommandJoinPoints.PARAM_SIGNATURE; }

        static ATTR_SIGNATURE: any[][]; public static ATTR_SIGNATURE_$LI$(): any[][] { if (CommandJoinPoints.ATTR_SIGNATURE == null) { CommandJoinPoints.ATTR_SIGNATURE = []; }  return CommandJoinPoints.ATTR_SIGNATURE; }

        /**
         * 
         * @return {java.lang.Object[][]}
         */
        public getParameterSignature(): any[][] {
            return CommandJoinPoints.PARAM_SIGNATURE_$LI$();
        }

        /**
         * 
         * @return {java.lang.Object[][]}
         */
        public getAttributeSignature(): any[][] {
            return CommandJoinPoints.ATTR_SIGNATURE_$LI$();
        }

        /**
         * 
         * @return {boolean}
         */
        public ordersSelection(): boolean {
            return true;
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
            if (parameters == null || parameters.size() !== 2)throw new Command.Failure("parameters must be two points");
            try {
                const pt1: com.vzome.core.construction.Point = <com.vzome.core.construction.Point>parameters.get(0);
                const pt2: com.vzome.core.construction.Point = <com.vzome.core.construction.Point>parameters.get(1);
                const segment: com.vzome.core.construction.Segment = new com.vzome.core.construction.SegmentJoiningPoints(pt1, pt2);
                result.addConstruction(segment);
                effects['constructionAdded$com_vzome_core_construction_Construction'](segment);
            } catch(e) {
                throw new Command.Failure("parameters must be two points");
            }
            return result;
        }

        constructor() {
            super();
        }
    }
    CommandJoinPoints["__class"] = "com.vzome.core.commands.CommandJoinPoints";
    CommandJoinPoints["__interfaces"] = ["com.vzome.core.commands.Command"];


}

