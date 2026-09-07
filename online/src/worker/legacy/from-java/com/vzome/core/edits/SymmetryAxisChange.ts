import { java, javaemul } from "../../../../../candies/j4ts-2.1.0-SNAPSHOT/bundle.js";
import { AttributeMap } from "../commands/AttributeMap.js";
import { Command } from "../commands/Command.js";
import { XmlSaveFormat } from "../commands/XmlSaveFormat.js";
import { Segment } from "../construction/Segment.js";
import { Context } from "../editor/api/Context.js";
import { ImplicitSymmetryParameters } from "../editor/api/ImplicitSymmetryParameters.js";
import { UndoableEdit } from "../editor/api/UndoableEdit.js";
import { Manifestation } from "../model/Manifestation.js";
import { Document } from "../../../../org/w3c/dom/Document.js";
import { Element } from "../../../../org/w3c/dom/Element.js";

/**
 * Used by CommandEdit.
 * @param {*} editor
 * @param m
 * @param {Segment} newAxis
 * @class
 * @extends UndoableEdit
 */
export class SymmetryAxisChange extends UndoableEdit {
    /*private*/ mOldAxis: Segment;

    /*private*/ mNewAxis: Segment;

    /*private*/ mEditor: ImplicitSymmetryParameters;

    public constructor(editor?: any, newAxis?: any) {
        if (((editor != null && (editor.constructor != null && editor.constructor["__interfaces"] != null && editor.constructor["__interfaces"].indexOf("com.vzome.core.editor.api.ImplicitSymmetryParameters") >= 0)) || editor === null) && ((newAxis != null && newAxis instanceof <any>Segment) || newAxis === null)) {
            let __args = arguments;
            super();
            if (this.mOldAxis === undefined) { this.mOldAxis = null; } 
            if (this.mNewAxis === undefined) { this.mNewAxis = null; } 
            if (this.mEditor === undefined) { this.mEditor = null; } 
            this.mOldAxis = editor.getSymmetrySegment();
            this.mNewAxis = newAxis;
            this.mEditor = editor;
        } else if (((editor != null && (editor.constructor != null && editor.constructor["__interfaces"] != null && editor.constructor["__interfaces"].indexOf("com.vzome.core.editor.api.ImplicitSymmetryParameters") >= 0)) || editor === null) && newAxis === undefined) {
            let __args = arguments;
            {
                let __args = arguments;
                let newAxis: any = null;
                super();
                if (this.mOldAxis === undefined) { this.mOldAxis = null; } 
                if (this.mNewAxis === undefined) { this.mNewAxis = null; } 
                if (this.mEditor === undefined) { this.mEditor = null; } 
                this.mOldAxis = editor.getSymmetrySegment();
                this.mNewAxis = newAxis;
                this.mEditor = editor;
            }
        } else throw new Error('invalid overload');
    }

    public configure(props: java.util.Map<string, any>) {
        const man: Manifestation = <Manifestation><any>props.get("picked");
        if (man != null)this.mNewAxis = <Segment>man.getFirstConstruction();
    }

    /**
     * 
     * @return {boolean}
     */
    public isVisible(): boolean {
        return false;
    }

    /**
     * 
     * @return {boolean}
     */
    public isNoOp(): boolean {
        return this.mNewAxis == null || (this.mOldAxis != null && this.mNewAxis.getStart().equals(this.mOldAxis.getStart()) && this.mNewAxis.getEnd().equals(this.mOldAxis.getEnd()));
    }

    /**
     * 
     */
    public redo() {
        if (this.isNoOp())return;
        this.mEditor.setSymmetrySegment(this.mNewAxis);
    }

    /**
     * 
     */
    public undo() {
        if (this.isNoOp())return;
        this.mEditor.setSymmetrySegment(this.mOldAxis);
    }

    /**
     * 
     * @param {*} doc
     * @return {*}
     */
    public getXml(doc: Document): Element {
        const result: Element = doc.createElement("SymmetryAxisChange");
        XmlSaveFormat.serializeSegment(result, "start", "end", this.mNewAxis);
        return result;
    }

    /**
     * 
     * @param {*} doc
     * @return {*}
     */
    public getDetailXml(doc: Document): Element {
        return this.getXml(doc);
    }

    /**
     * 
     * @param {*} xml
     * @param {XmlSaveFormat} format
     * @param {*} context
     */
    public loadAndPerform(xml: Element, format: XmlSaveFormat, context: Context) {
        if (format.rationalVectors()){
            this.mNewAxis = format.parseSegment$org_w3c_dom_Element$java_lang_String$java_lang_String(xml, "start", "end");
        } else {
            const attrs: AttributeMap = format.loadCommandAttributes$org_w3c_dom_Element(xml);
            this.mNewAxis = <Segment>attrs.get("new");
        }
        context.performAndRecord(this);
    }

    /**
     * 
     */
    public perform() {
        if (this.mNewAxis == null){
            this.mNewAxis = <Segment>this.mEditor.getSelectedConstruction(Segment);
            if (this.mNewAxis == null)throw new Command.Failure("Selection is not a single strut.");
        }
        this.redo();
    }

    /**
     * 
     * @return {boolean}
     */
    public isDestructive(): boolean {
        return true;
    }

    /**
     * 
     * @return {boolean}
     */
    public isSticky(): boolean {
        return false;
    }
}
SymmetryAxisChange["__class"] = "com.vzome.core.edits.SymmetryAxisChange";
