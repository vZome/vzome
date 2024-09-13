/* Generated from Java with JSweet 3.2.0-SNAPSHOT - http://www.jsweet.org */
namespace com.vzome.core.commands {
    /**
     * @author Scott Vorthmann
     * @class
     * @extends com.vzome.core.commands.AbstractCommand
     */
    export class CommandBuildAnchoredSegment extends com.vzome.core.commands.AbstractCommand {
        /**
         * 
         * @param {*} xml
         * @param {com.vzome.core.commands.AttributeMap} attributes
         */
        public getXml(xml: org.w3c.dom.Element, attributes: com.vzome.core.commands.AttributeMap) {
            com.vzome.core.commands.XmlSymmetryFormat.serializeAxis(xml, "symm", "dir", "index", "sense", <com.vzome.core.math.symmetry.Axis>attributes.get("axis"));
            com.vzome.core.commands.XmlSaveFormat.serializeNumber(xml, "len", <com.vzome.core.algebra.AlgebraicNumber><any>attributes.get("length"));
        }

        /**
         * 
         * @param {*} xml
         * @param {com.vzome.core.commands.XmlSaveFormat} format
         * @return {com.vzome.core.commands.AttributeMap}
         */
        public setXml(xml: org.w3c.dom.Element, format: com.vzome.core.commands.XmlSaveFormat): com.vzome.core.commands.AttributeMap {
            const attrs: com.vzome.core.commands.AttributeMap = super.setXml(xml, format);
            if (format.commandEditsCompacted()){
                attrs.put("axis", (<com.vzome.core.commands.XmlSymmetryFormat>format).parseAxis(xml, "symm", "dir", "index", "sense"));
                attrs.put("length", format.parseNumber(xml, "len"));
            }
            return attrs;
        }

        static AXIS_ATTR: string = "axis";

        static LENGTH_ATTR: string = "length";

        static PARAM_SIGNATURE: any[][]; public static PARAM_SIGNATURE_$LI$(): any[][] { if (CommandBuildAnchoredSegment.PARAM_SIGNATURE == null) { CommandBuildAnchoredSegment.PARAM_SIGNATURE = [["start", com.vzome.core.construction.Point]]; }  return CommandBuildAnchoredSegment.PARAM_SIGNATURE; }

        /**
         * 
         * @return {java.lang.Object[][]}
         */
        public getParameterSignature(): any[][] {
            return CommandBuildAnchoredSegment.PARAM_SIGNATURE_$LI$();
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
         * @param {com.vzome.core.commands.AttributeMap} attrs
         * @param {*} effects
         * @return {com.vzome.core.construction.ConstructionList}
         */
        public apply(parameters: com.vzome.core.construction.ConstructionList, attrs: com.vzome.core.commands.AttributeMap, effects: com.vzome.core.construction.ConstructionChanges): com.vzome.core.construction.ConstructionList {
            const result: com.vzome.core.construction.ConstructionList = new com.vzome.core.construction.ConstructionList();
            if (parameters == null || parameters.size() !== 1)throw new Command.Failure("start parameter must be a single point");
            const c: any = parameters.get(0);
            if (!(c != null && c instanceof <any>com.vzome.core.construction.Point))throw new Command.Failure("start parameter must be a single point");
            const pt1: com.vzome.core.construction.Point = <com.vzome.core.construction.Point>c;
            const axis: com.vzome.core.math.symmetry.Axis = <com.vzome.core.math.symmetry.Axis>attrs.get(CommandBuildAnchoredSegment.AXIS_ATTR);
            const len: com.vzome.core.algebra.AlgebraicNumber = <com.vzome.core.algebra.AlgebraicNumber><any>attrs.get(CommandBuildAnchoredSegment.LENGTH_ATTR);
            const segment: com.vzome.core.construction.Segment = new com.vzome.core.construction.AnchoredSegment(axis, len, pt1);
            effects['constructionAdded$com_vzome_core_construction_Construction'](segment);
            result.addConstruction(segment);
            const pt2: com.vzome.core.construction.Point = new com.vzome.core.construction.SegmentEndPoint(segment);
            effects['constructionAdded$com_vzome_core_construction_Construction'](pt2);
            result.addConstruction(pt2);
            return result;
        }

        constructor() {
            super();
        }
    }
    CommandBuildAnchoredSegment["__class"] = "com.vzome.core.commands.CommandBuildAnchoredSegment";
    CommandBuildAnchoredSegment["__interfaces"] = ["com.vzome.core.commands.Command"];


}

