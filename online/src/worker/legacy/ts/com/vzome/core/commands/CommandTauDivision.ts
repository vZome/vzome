/* Generated from Java with JSweet 3.2.0-SNAPSHOT - http://www.jsweet.org */
namespace com.vzome.core.commands {
    /**
     * @author Scott Vorthmann
     * @class
     * @extends com.vzome.core.commands.AbstractCommand
     */
    export class CommandTauDivision extends com.vzome.core.commands.AbstractCommand {
        static PARAM_SIGNATURE: any[][]; public static PARAM_SIGNATURE_$LI$(): any[][] { if (CommandTauDivision.PARAM_SIGNATURE == null) { CommandTauDivision.PARAM_SIGNATURE = [["start", com.vzome.core.construction.Point], ["end", com.vzome.core.construction.Point]]; }  return CommandTauDivision.PARAM_SIGNATURE; }

        static ATTR_SIGNATURE: any[][]; public static ATTR_SIGNATURE_$LI$(): any[][] { if (CommandTauDivision.ATTR_SIGNATURE == null) { CommandTauDivision.ATTR_SIGNATURE = []; }  return CommandTauDivision.ATTR_SIGNATURE; }

        /**
         * 
         * @return {java.lang.Object[][]}
         */
        public getParameterSignature(): any[][] {
            return CommandTauDivision.PARAM_SIGNATURE_$LI$();
        }

        /**
         * 
         * @return {java.lang.Object[][]}
         */
        public getAttributeSignature(): any[][] {
            return CommandTauDivision.ATTR_SIGNATURE_$LI$();
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
            if (parameters == null || parameters.size() !== 2)throw new Command.Failure("Tau division applies to two balls.");
            try {
                const start: com.vzome.core.construction.Point = <com.vzome.core.construction.Point>parameters.get(0);
                const end: com.vzome.core.construction.Point = <com.vzome.core.construction.Point>parameters.get(1);
                const join: com.vzome.core.construction.Segment = new com.vzome.core.construction.SegmentJoiningPoints(start, end);
                const midpoint: com.vzome.core.construction.Point = new com.vzome.core.construction.SegmentTauDivision(join);
                result.addConstruction(midpoint);
                effects['constructionAdded$com_vzome_core_construction_Construction'](midpoint);
            } catch(e) {
                throw new Command.Failure("Tau division applies to two balls.");
            }
            return result;
        }

        constructor() {
            super();
        }
    }
    CommandTauDivision["__class"] = "com.vzome.core.commands.CommandTauDivision";
    CommandTauDivision["__interfaces"] = ["com.vzome.core.commands.Command"];


}

