import { java, javaemul } from "../../../../../candies/j4ts-2.1.0-SNAPSHOT/bundle.js";
import { AttributeMap } from "../commands/AttributeMap.js";
import { XmlSaveFormat } from "../commands/XmlSaveFormat.js";
import { Construction } from "../construction/Construction.js";
import { Point } from "../construction/Point.js";
import { Polygon } from "../construction/Polygon.js";
import { Segment } from "../construction/Segment.js";
import { ChangeSelection } from "../editor/api/ChangeSelection.js";
import { SideEffects } from "../editor/api/SideEffects.js";
import { Manifestation } from "../model/Manifestation.js";
import { RealizedModel } from "../model/RealizedModel.js";
import { DomUtils } from "../../xml/DomUtils.js";
import { Element } from "../../../../org/w3c/dom/Element.js";

/**
 * Used by CommandEdit.
 * @param {*} editor
 * @param {*} m
 * @class
 * @extends ChangeSelection
 */
export class SelectManifestation extends ChangeSelection {
    /*private*/ mManifestation: Manifestation;

    /*private*/ construction: Construction;

    /*private*/ mRealized: RealizedModel;

    /*private*/ mReplace: boolean;

    /**
     * 
     * @return {boolean}
     */
    groupingAware(): boolean {
        return true;
    }

    public constructor(editor?: any, m?: any) {
        if (((editor != null && (editor.constructor != null && editor.constructor["__interfaces"] != null && editor.constructor["__interfaces"].indexOf("com.vzome.core.editor.api.EditorModel") >= 0)) || editor === null) && ((m != null && (m.constructor != null && m.constructor["__interfaces"] != null && m.constructor["__interfaces"].indexOf("com.vzome.core.model.Manifestation") >= 0)) || m === null)) {
            let __args = arguments;
            {
                let __args = arguments;
                super(editor.getSelection());
                if (this.mManifestation === undefined) { this.mManifestation = null; } 
                if (this.construction === undefined) { this.construction = null; } 
                if (this.mRealized === undefined) { this.mRealized = null; } 
                if (this.mReplace === undefined) { this.mReplace = false; } 
                this.mRealized = editor.getRealizedModel();
            }
            (() => {
                this.mManifestation = m;
                if (this.mManifestation != null){
                    this.construction = this.mManifestation.toConstruction();
                }
            })();
        } else if (((editor != null && (editor.constructor != null && editor.constructor["__interfaces"] != null && editor.constructor["__interfaces"].indexOf("com.vzome.core.editor.api.EditorModel") >= 0)) || editor === null) && m === undefined) {
            let __args = arguments;
            super(editor.getSelection());
            if (this.mManifestation === undefined) { this.mManifestation = null; } 
            if (this.construction === undefined) { this.construction = null; } 
            if (this.mRealized === undefined) { this.mRealized = null; } 
            if (this.mReplace === undefined) { this.mReplace = false; } 
            this.mRealized = editor.getRealizedModel();
        } else throw new Error('invalid overload');
    }

    public configure(props: java.util.Map<string, any>) {
        const mode: string = <string>props.get("mode");
        this.mReplace = "replace" === mode;
        this.mManifestation = <Manifestation><any>props.get("picked");
        if (this.mManifestation != null){
            this.construction = this.mManifestation.toConstruction();
        }
    }

    /**
     * 
     */
    public perform() {
        if (this.mReplace){
            for(let index=this.mSelection.iterator();index.hasNext();) {
                let man = index.next();
                {
                    this.unselect$com_vzome_core_model_Manifestation$boolean(man, true);
                }
            }
            this.select$com_vzome_core_model_Manifestation(this.mManifestation);
        } else if (this.mSelection.manifestationSelected(this.mManifestation))this.unselect$com_vzome_core_model_Manifestation(this.mManifestation); else this.select$com_vzome_core_model_Manifestation(this.mManifestation);
        this.redo();
    }

    /**
     * 
     * @param {*} result
     */
    getXmlAttributes(result: Element) {
        if (this.construction != null && this.construction instanceof <any>Point)XmlSaveFormat.serializePoint(result, "point", <Point>this.construction); else if (this.construction != null && this.construction instanceof <any>Segment)XmlSaveFormat.serializeSegment(result, "startSegment", "endSegment", <Segment>this.construction); else if (this.construction != null && this.construction instanceof <any>Polygon)XmlSaveFormat.serializePolygon(result, "polygonVertex", <Polygon>this.construction);
        if (this.mReplace)DomUtils.addAttribute(result, "replace", "true");
    }

    /**
     * 
     * @param {*} xml
     * @param {XmlSaveFormat} format
     */
    public setXmlAttributes(xml: Element, format: XmlSaveFormat) {
        if (format.rationalVectors()){
            this.construction = format.parsePoint$org_w3c_dom_Element$java_lang_String(xml, "point");
            if (this.construction == null)this.construction = format.parseSegment$org_w3c_dom_Element$java_lang_String$java_lang_String(xml, "startSegment", "endSegment");
            if (this.construction == null){
                const kid: Element = DomUtils.getFirstChildElement$org_w3c_dom_Element$java_lang_String(xml, "polygon");
                if (kid != null)this.construction = format.parsePolygon$org_w3c_dom_Element$java_lang_String(kid, "vertex"); else this.construction = format.parsePolygon$org_w3c_dom_Element$java_lang_String(xml, "polygonVertex");
            }
        } else {
            const attrs: AttributeMap = format.loadCommandAttributes$org_w3c_dom_Element(xml);
            this.construction = <Construction>attrs.get("manifestation");
            const replaceVal: boolean = <boolean>attrs.get("replace");
            if (replaceVal != null && replaceVal)this.mReplace = true;
        }
        this.mManifestation = this.mRealized.getManifestation(this.construction);
        if (this.mManifestation == null && format.rationalVectors() && (this.construction != null && this.construction instanceof <any>Polygon)){
            this.construction = format.parsePolygonReversed(xml, "polygonVertex");
            this.mManifestation = this.mRealized.getManifestation(this.construction);
            if (this.mManifestation != null)SideEffects.logBugAccommodation("reverse-oriented polygon");
        }
    }

    /**
     * 
     * @return {string}
     */
    getXmlElementName(): string {
        return "SelectManifestation";
    }
}
SelectManifestation["__class"] = "com.vzome.core.edits.SelectManifestation";
