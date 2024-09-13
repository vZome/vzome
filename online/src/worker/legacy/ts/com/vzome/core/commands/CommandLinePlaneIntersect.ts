/* Generated from Java with JSweet 3.2.0-SNAPSHOT - http://www.jsweet.org */
namespace com.vzome.core.commands {
    /**
     * @author Scott Vorthmann
     * @class
     * @extends com.vzome.core.commands.AbstractCommand
     */
    export class CommandLinePlaneIntersect extends com.vzome.core.commands.AbstractCommand {
        static PARAM_SIGNATURE: any[][]; public static PARAM_SIGNATURE_$LI$(): any[][] { if (CommandLinePlaneIntersect.PARAM_SIGNATURE == null) { CommandLinePlaneIntersect.PARAM_SIGNATURE = [["panel", com.vzome.core.construction.Polygon], ["segment", com.vzome.core.construction.Segment]]; }  return CommandLinePlaneIntersect.PARAM_SIGNATURE; }

        static ATTR_SIGNATURE: any[][]; public static ATTR_SIGNATURE_$LI$(): any[][] { if (CommandLinePlaneIntersect.ATTR_SIGNATURE == null) { CommandLinePlaneIntersect.ATTR_SIGNATURE = []; }  return CommandLinePlaneIntersect.ATTR_SIGNATURE; }

        /**
         * 
         * @return {java.lang.Object[][]}
         */
        public getParameterSignature(): any[][] {
            return CommandLinePlaneIntersect.PARAM_SIGNATURE_$LI$();
        }

        /**
         * 
         * @return {java.lang.Object[][]}
         */
        public getAttributeSignature(): any[][] {
            return CommandLinePlaneIntersect.ATTR_SIGNATURE_$LI$();
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
            if (parameters == null || parameters.size() !== 2)throw new Command.Failure("Intersection requires a panel and a strut.");
            try {
                let panel: com.vzome.core.construction.Polygon;
                let segment: com.vzome.core.construction.Segment;
                const first: com.vzome.core.construction.Construction = parameters.get(0);
                if (first != null && first instanceof <any>com.vzome.core.construction.Polygon){
                    panel = <com.vzome.core.construction.Polygon>first;
                    segment = <com.vzome.core.construction.Segment>parameters.get(1);
                } else {
                    segment = <com.vzome.core.construction.Segment>first;
                    panel = <com.vzome.core.construction.Polygon>parameters.get(1);
                }
                const plane: com.vzome.core.construction.Plane = new com.vzome.core.construction.PlaneExtensionOfPolygon(panel);
                const line: com.vzome.core.construction.Line = new com.vzome.core.construction.LineExtensionOfSegment(segment);
                const point: com.vzome.core.construction.Point = new com.vzome.core.construction.LinePlaneIntersectionPoint(plane, line);
                result.addConstruction(point);
                effects['constructionAdded$com_vzome_core_construction_Construction'](point);
            } catch(e) {
                throw new Command.Failure("Intersection requires a panel and a strut.");
            }
            return result;
        }

        constructor() {
            super();
        }
    }
    CommandLinePlaneIntersect["__class"] = "com.vzome.core.commands.CommandLinePlaneIntersect";
    CommandLinePlaneIntersect["__interfaces"] = ["com.vzome.core.commands.Command"];


}

