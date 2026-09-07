import { java, javaemul } from "../../../../../candies/j4ts-2.1.0-SNAPSHOT/bundle.js";
import { AlgebraicField } from "../algebra/AlgebraicField.js";
import { AlgebraicVector } from "../algebra/AlgebraicVector.js";
import { AbstractCommand } from "./AbstractCommand.js";
import { AttributeMap } from "./AttributeMap.js";
import { Command } from "./Command.js";
import { CommandTransform } from "./CommandTransform.js";
import { XmlSaveFormat } from "./XmlSaveFormat.js";
import { Construction } from "../construction/Construction.js";
import { ConstructionChanges } from "../construction/ConstructionChanges.js";
import { ConstructionList } from "../construction/ConstructionList.js";
import { FreePoint } from "../construction/FreePoint.js";
import { Point } from "../construction/Point.js";
import { Segment } from "../construction/Segment.js";
import { SegmentJoiningPoints } from "../construction/SegmentJoiningPoints.js";
import { VefToModel } from "../construction/VefToModel.js";
import { Projection } from "../math/Projection.js";
import { QuaternionProjection } from "../math/QuaternionProjection.js";
import { DomUtils } from "../../xml/DomUtils.js";
import { Element } from "../../../../org/w3c/dom/Element.js";

/**
 * @author Scott Vorthmann
 * @param {*} projection
 * @class
 * @extends AbstractCommand
 */
export class CommandImportVEFData extends AbstractCommand {
    public static X: number = 0;

    public static Y: number = 1;

    public static Z: number = 2;

    public static W: number = 3;

    public static VEF_STRING_ATTR_NAME: string = "org.vorthmann.zome.commands.CommandImportVEFData.vef.string";

    public static FIELD_ATTR_NAME: string = "org.vorthmann.zome.commands.CommandImportVEFData.field";

    public static NO_INVERSION_ATTR_NAME: string = "org.vorthmann.zome.commands.CommandImportVEFData.no.inversion";

    static PARAM_SIGNATURE: any[][]; public static PARAM_SIGNATURE_$LI$(): any[][] { if (CommandImportVEFData.PARAM_SIGNATURE == null) { CommandImportVEFData.PARAM_SIGNATURE = [[Command.GENERIC_PARAM_NAME, Construction]]; }  return CommandImportVEFData.PARAM_SIGNATURE; }

    static ATTR_SIGNATURE: any[][]; public static ATTR_SIGNATURE_$LI$(): any[][] { if (CommandImportVEFData.ATTR_SIGNATURE == null) { CommandImportVEFData.ATTR_SIGNATURE = [[CommandImportVEFData.VEF_STRING_ATTR_NAME, String], [Command.FIELD_ATTR_NAME, java.io.InputStream], [CommandImportVEFData.NO_INVERSION_ATTR_NAME, java.io.InputStream]]; }  return CommandImportVEFData.ATTR_SIGNATURE; }

    /*private*/ mProjection: Projection;

    public constructor(projection?: any) {
        if (((projection != null && (projection.constructor != null && projection.constructor["__interfaces"] != null && projection.constructor["__interfaces"].indexOf("com.vzome.core.math.Projection") >= 0)) || projection === null)) {
            let __args = arguments;
            super();
            if (this.mProjection === undefined) { this.mProjection = null; } 
            this.quaternionVector = null;
            this.mProjection = projection;
        } else if (projection === undefined) {
            let __args = arguments;
            {
                let __args = arguments;
                let projection: any = null;
                super();
                if (this.mProjection === undefined) { this.mProjection = null; } 
                this.quaternionVector = null;
                this.mProjection = projection;
            }
        } else throw new Error('invalid overload');
    }

    /**
     * 
     * @return {java.lang.Object[][]}
     */
    public getParameterSignature(): any[][] {
        return CommandImportVEFData.PARAM_SIGNATURE_$LI$();
    }

    /**
     * 
     * @return {java.lang.Object[][]}
     */
    public getAttributeSignature(): any[][] {
        return CommandImportVEFData.ATTR_SIGNATURE_$LI$();
    }

    /**
     * 
     * @param {string} attrName
     * @return {boolean}
     */
    public attributeIs3D(attrName: string): boolean {
        return !("symmetry.axis.segment" === attrName);
    }

    /*private*/ quaternionVector: AlgebraicVector;

    /**
     * Only called when migrating a 2.0 model file.
     * @param {AlgebraicVector} offset
     */
    public setQuaternion(offset: AlgebraicVector) {
        this.quaternionVector = offset;
    }

    /**
     * 
     * @param {*} xml
     * @param {XmlSaveFormat} format
     * @return {AttributeMap}
     */
    public setXml(xml: Element, format: XmlSaveFormat): AttributeMap {
        const attrs: AttributeMap = super.setXml(xml, format);
        this.quaternionVector = format.parseRationalVector(xml, "quaternion");
        return attrs;
    }

    /**
     * 
     * @param {*} result
     * @param {AttributeMap} attributes
     */
    public getXml(result: Element, attributes: AttributeMap) {
        if (this.quaternionVector != null)DomUtils.addAttribute(result, "quaternion", this.quaternionVector.toParsableString());
        super.getXml(result, attributes);
    }

    /**
     * 
     * @param {AttributeMap} attributes
     * @param {XmlSaveFormat} format
     */
    public setFixedAttributes(attributes: AttributeMap, format: XmlSaveFormat) {
        if (!attributes.containsKey(CommandImportVEFData.FIELD_ATTR_NAME))attributes.put(CommandImportVEFData.FIELD_ATTR_NAME, format.getField());
        super.setFixedAttributes(attributes, format);
    }

    /**
     * 
     * @param {ConstructionList} parameters
     * @param {AttributeMap} attributes
     * @param {*} effects
     * @return {ConstructionList}
     */
    public apply(parameters: ConstructionList, attributes: AttributeMap, effects: ConstructionChanges): ConstructionList {
        const result: ConstructionList = new ConstructionList();
        let field: AlgebraicField = <AlgebraicField><any>attributes.get(CommandImportVEFData.FIELD_ATTR_NAME);
        if (field == null)field = <AlgebraicField><any>attributes.get(Command.FIELD_ATTR_NAME);
        const symmAxis: Segment = <Segment>attributes.get(CommandTransform.SYMMETRY_AXIS_ATTR_NAME);
        const vefData: string = <string>attributes.get(CommandImportVEFData.VEF_STRING_ATTR_NAME);
        const noInversion: boolean = <boolean>attributes.get(CommandImportVEFData.NO_INVERSION_ATTR_NAME);
        let projection: Projection = this.mProjection;
        if (projection == null){
            let quaternion: AlgebraicVector = this.quaternionVector;
            if (quaternion == null)quaternion = (symmAxis == null) ? null : symmAxis.getOffset();
            if (quaternion != null)quaternion = quaternion.scale(field['createPower$int'](-5));
            projection = quaternion == null ? null : new QuaternionProjection(field, null, quaternion);
        }
        if (noInversion != null && noInversion)new CommandImportVEFData.VefToModelNoInversion(this, projection, field, effects).parseVEF(vefData, field); else new VefToModel(projection, effects, field['createPower$int'](5), null).parseVEF(vefData, field);
        return result;
    }
}
CommandImportVEFData["__class"] = "com.vzome.core.commands.CommandImportVEFData";
CommandImportVEFData["__interfaces"] = ["com.vzome.core.commands.Command"];



export namespace CommandImportVEFData {

    export class VefToModelNoInversion extends VefToModel {
        public __parent: any;
        mProjected: AlgebraicVector[][];

        mUsedPoints: java.util.Set<Point>;

        public constructor(__parent: any, projection: Projection, field: AlgebraicField, effects: ConstructionChanges) {
            super(projection, effects, field['createPower$int'](5), null);
            this.__parent = __parent;
            if (this.mProjected === undefined) { this.mProjected = null; }
            this.mUsedPoints = <any>(new java.util.HashSet<any>());
        }

        /**
         * 
         * @param {number} index
         * @param {AlgebraicVector} location
         */
        addVertex(index: number, location: AlgebraicVector) {
            if (this.scale != null){
                location = location.scale(this.scale);
            }
            if (this.mProjection != null)location = this.mProjection.projectImage(location, this.wFirst());
            this.mVertices[index] = new FreePoint(location);
        }

        /**
         * 
         * @param {number} numEdges
         */
        startEdges(numEdges: number) {
            this.mProjected = <any> (function(dims) { let allocate = function(dims) { if (dims.length === 0) { return null; } else { let array = []; for(let i = 0; i < dims[0]; i++) { array.push(allocate(dims.slice(1))); } return array; }}; return allocate(dims);})([numEdges, 2]);
        }

        /**
         * 
         * @param {number} index
         * @param {number} v1
         * @param {number} v2
         */
        addEdge(index: number, v1: number, v2: number) {
            const p1: Point = this.mVertices[v1];
            const p2: Point = this.mVertices[v2];
            if (p1 == null || p2 == null)return;
            const seg: Segment = new SegmentJoiningPoints(p1, p2);
            const pr1: AlgebraicVector = p1.getLocation().projectTo3d(this.wFirst()).negate();
            const pr2: AlgebraicVector = p2.getLocation().projectTo3d(this.wFirst()).negate();
            for(let i: number = 0; i < index; i++) {{
                if (pr1.equals(this.mProjected[i][0]) && pr2.equals(this.mProjected[i][1]))return;
                if (pr2.equals(this.mProjected[i][0]) && pr1.equals(this.mProjected[i][1]))return;
            };}
            this.mProjected[index][0] = pr1.negate();
            this.mProjected[index][1] = pr2.negate();
            this.mEffects['constructionAdded$com_vzome_core_construction_Construction'](seg);
            this.mUsedPoints.add(p1);
            this.mUsedPoints.add(p2);
        }

        /**
         * 
         */
        endEdges() {
            for(let index=this.mUsedPoints.iterator();index.hasNext();) {
                let point = index.next();
                {
                    this.mEffects['constructionAdded$com_vzome_core_construction_Construction'](point);
                }
            }
        }
    }
    VefToModelNoInversion["__class"] = "com.vzome.core.commands.CommandImportVEFData.VefToModelNoInversion";

}
