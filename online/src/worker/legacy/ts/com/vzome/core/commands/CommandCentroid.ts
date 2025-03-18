/* Generated from Java with JSweet 3.2.0-SNAPSHOT - http://www.jsweet.org */
namespace com.vzome.core.commands {
    /**
     * @author Scott Vorthmann
     * @class
     * @extends com.vzome.core.commands.AbstractCommand
     */
    export class CommandCentroid extends com.vzome.core.commands.AbstractCommand {
        static PARAM_SIGNATURE: any[][]; public static PARAM_SIGNATURE_$LI$(): any[][] { if (CommandCentroid.PARAM_SIGNATURE == null) { CommandCentroid.PARAM_SIGNATURE = [[com.vzome.core.commands.Command.GENERIC_PARAM_NAME, com.vzome.core.construction.Point]]; }  return CommandCentroid.PARAM_SIGNATURE; }

        static ATTR_SIGNATURE: any[][]; public static ATTR_SIGNATURE_$LI$(): any[][] { if (CommandCentroid.ATTR_SIGNATURE == null) { CommandCentroid.ATTR_SIGNATURE = []; }  return CommandCentroid.ATTR_SIGNATURE; }

        /**
         * 
         * @return {java.lang.Object[][]}
         */
        public getParameterSignature(): any[][] {
            return CommandCentroid.PARAM_SIGNATURE_$LI$();
        }

        /**
         * 
         * @return {java.lang.Object[][]}
         */
        public getAttributeSignature(): any[][] {
            return CommandCentroid.ATTR_SIGNATURE_$LI$();
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
            if (parameters == null || parameters.size() === 0)throw new Command.Failure("Select two or more balls to compute their centroid.");
            const params: com.vzome.core.construction.Construction[] = parameters.getConstructions();
            const verticesList: java.util.List<com.vzome.core.construction.Point> = <any>(new java.util.ArrayList<any>());
            for(let index = 0; index < params.length; index++) {
                let param = params[index];
                {
                    if (param != null && param instanceof <any>com.vzome.core.construction.Point){
                        verticesList.add(<com.vzome.core.construction.Point>param);
                    }
                }
            }
            if (verticesList.isEmpty())throw new Command.Failure("Select two or more balls to compute their centroid.");
            const points: com.vzome.core.construction.Point[] = [];
            const centroid: com.vzome.core.construction.CentroidPoint = new com.vzome.core.construction.CentroidPoint(verticesList.toArray<any>(points));
            effects['constructionAdded$com_vzome_core_construction_Construction'](centroid);
            result.addConstruction(centroid);
            return result;
        }

        constructor() {
            super();
        }
    }
    CommandCentroid["__class"] = "com.vzome.core.commands.CommandCentroid";
    CommandCentroid["__interfaces"] = ["com.vzome.core.commands.Command"];


}

