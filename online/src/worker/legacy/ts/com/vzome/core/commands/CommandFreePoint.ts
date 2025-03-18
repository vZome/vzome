/* Generated from Java with JSweet 3.2.0-SNAPSHOT - http://www.jsweet.org */
namespace com.vzome.core.commands {
    /**
     * @author Scott Vorthmann
     * @class
     * @extends com.vzome.core.commands.AbstractCommand
     */
    export class CommandFreePoint extends com.vzome.core.commands.AbstractCommand {
        static PARAMS: any[][]; public static PARAMS_$LI$(): any[][] { if (CommandFreePoint.PARAMS == null) { CommandFreePoint.PARAMS = []; }  return CommandFreePoint.PARAMS; }

        /**
         * 
         * @return {java.lang.Object[][]}
         */
        public getParameterSignature(): any[][] {
            return CommandFreePoint.PARAMS_$LI$();
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
            const result: com.vzome.core.construction.ConstructionList = new com.vzome.core.construction.ConstructionList();
            const loc: com.vzome.core.algebra.AlgebraicVector = <com.vzome.core.algebra.AlgebraicVector>attributes.get("where");
            const pt2: com.vzome.core.construction.Point = new com.vzome.core.construction.FreePoint(loc);
            effects['constructionAdded$com_vzome_core_construction_Construction'](pt2);
            result.addConstruction(pt2);
            return result;
        }

        constructor() {
            super();
        }
    }
    CommandFreePoint["__class"] = "com.vzome.core.commands.CommandFreePoint";
    CommandFreePoint["__interfaces"] = ["com.vzome.core.commands.Command"];


}

