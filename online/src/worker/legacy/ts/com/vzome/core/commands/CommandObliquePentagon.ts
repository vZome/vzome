/* Generated from Java with JSweet 3.2.0-SNAPSHOT - http://www.jsweet.org */
namespace com.vzome.core.commands {
    export class CommandObliquePentagon extends com.vzome.core.commands.AbstractCommand {
        static PARAM_SIGNATURE: any[][]; public static PARAM_SIGNATURE_$LI$(): any[][] { if (CommandObliquePentagon.PARAM_SIGNATURE == null) { CommandObliquePentagon.PARAM_SIGNATURE = [["segment1", com.vzome.core.construction.Segment], ["segment2", com.vzome.core.construction.Segment]]; }  return CommandObliquePentagon.PARAM_SIGNATURE; }

        static ATTR_SIGNATURE: any[][]; public static ATTR_SIGNATURE_$LI$(): any[][] { if (CommandObliquePentagon.ATTR_SIGNATURE == null) { CommandObliquePentagon.ATTR_SIGNATURE = []; }  return CommandObliquePentagon.ATTR_SIGNATURE; }

        /**
         * 
         * @return {java.lang.Object[][]}
         */
        public getParameterSignature(): any[][] {
            return CommandObliquePentagon.PARAM_SIGNATURE_$LI$();
        }

        /**
         * 
         * @return {java.lang.Object[][]}
         */
        public getAttributeSignature(): any[][] {
            return CommandObliquePentagon.ATTR_SIGNATURE_$LI$();
        }

        /**
         * 
         * @param {com.vzome.core.construction.ConstructionList} parameters
         * @param {com.vzome.core.commands.AttributeMap} attributes
         * @param {*} effects
         * @return {com.vzome.core.construction.ConstructionList}
         */
        public apply(parameters: com.vzome.core.construction.ConstructionList, attributes: com.vzome.core.commands.AttributeMap, effects: com.vzome.core.construction.ConstructionChanges): com.vzome.core.construction.ConstructionList {
            throw new Command.Failure("Oblique pentagon should never be called.");
        }

        constructor() {
            super();
        }
    }
    CommandObliquePentagon["__class"] = "com.vzome.core.commands.CommandObliquePentagon";
    CommandObliquePentagon["__interfaces"] = ["com.vzome.core.commands.Command"];


}

