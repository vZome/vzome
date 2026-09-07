import { java, javaemul } from "../../../../../candies/j4ts-2.1.0-SNAPSHOT/bundle.js";
import { AlgebraicNumber } from "../algebra/AlgebraicNumber.js";
import { AbstractCommand } from "./AbstractCommand.js";
import { AttributeMap } from "./AttributeMap.js";
import { Command } from "./Command.js";
import { XmlSaveFormat } from "./XmlSaveFormat.js";
import { XmlSymmetryFormat } from "./XmlSymmetryFormat.js";
import { AnchoredSegment } from "../construction/AnchoredSegment.js";
import { ConstructionChanges } from "../construction/ConstructionChanges.js";
import { ConstructionList } from "../construction/ConstructionList.js";
import { Point } from "../construction/Point.js";
import { Segment } from "../construction/Segment.js";
import { SegmentEndPoint } from "../construction/SegmentEndPoint.js";
import { Axis } from "../math/symmetry/Axis.js";
import { Element } from "../../../../org/w3c/dom/Element.js";

/**
 * @author Scott Vorthmann
 * @class
 * @extends AbstractCommand
 */
export class CommandBuildAnchoredSegment extends AbstractCommand {
    /**
     * 
     * @param {*} xml
     * @param {AttributeMap} attributes
     */
    public getXml(xml: Element, attributes: AttributeMap) {
        XmlSymmetryFormat.serializeAxis(xml, "symm", "dir", "index", "sense", <Axis>attributes.get("axis"));
        XmlSaveFormat.serializeNumber(xml, "len", <AlgebraicNumber><any>attributes.get("length"));
    }

    /**
     * 
     * @param {*} xml
     * @param {XmlSaveFormat} format
     * @return {AttributeMap}
     */
    public setXml(xml: Element, format: XmlSaveFormat): AttributeMap {
        const attrs: AttributeMap = super.setXml(xml, format);
        if (format.commandEditsCompacted()){
            attrs.put("axis", (<XmlSymmetryFormat>format).parseAxis(xml, "symm", "dir", "index", "sense"));
            attrs.put("length", format.parseNumber(xml, "len"));
        }
        return attrs;
    }

    static AXIS_ATTR: string = "axis";

    static LENGTH_ATTR: string = "length";

    static PARAM_SIGNATURE: any[][]; public static PARAM_SIGNATURE_$LI$(): any[][] { if (CommandBuildAnchoredSegment.PARAM_SIGNATURE == null) { CommandBuildAnchoredSegment.PARAM_SIGNATURE = [["start", Point]]; }  return CommandBuildAnchoredSegment.PARAM_SIGNATURE; }

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
     * @param {ConstructionList} parameters
     * @param {AttributeMap} attrs
     * @param {*} effects
     * @return {ConstructionList}
     */
    public apply(parameters: ConstructionList, attrs: AttributeMap, effects: ConstructionChanges): ConstructionList {
        const result: ConstructionList = new ConstructionList();
        if (parameters == null || parameters.size() !== 1)throw new Command.Failure("start parameter must be a single point");
        const c: any = parameters.get(0);
        if (!(c != null && c instanceof <any>Point))throw new Command.Failure("start parameter must be a single point");
        const pt1: Point = <Point>c;
        const axis: Axis = <Axis>attrs.get(CommandBuildAnchoredSegment.AXIS_ATTR);
        const len: AlgebraicNumber = <AlgebraicNumber><any>attrs.get(CommandBuildAnchoredSegment.LENGTH_ATTR);
        const segment: Segment = new AnchoredSegment(axis, len, pt1);
        effects['constructionAdded$com_vzome_core_construction_Construction'](segment);
        result.addConstruction(segment);
        const pt2: Point = new SegmentEndPoint(segment);
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
