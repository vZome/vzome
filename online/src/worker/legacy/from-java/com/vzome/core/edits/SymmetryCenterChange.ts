import { java, javaemul } from "../../../../../candies/j4ts-2.1.0-SNAPSHOT/bundle.js";
import { AttributeMap } from "../commands/AttributeMap.js";
import { Command } from "../commands/Command.js";
import { XmlSaveFormat } from "../commands/XmlSaveFormat.js";
import { FreePoint } from "../construction/FreePoint.js";
import { Point } from "../construction/Point.js";
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
 * @param {Point} newCenter
 * @class
 * @extends UndoableEdit
 */
export class SymmetryCenterChange extends UndoableEdit {
    /*private*/ mOldCenter: Point;

    /*private*/ mNewCenter: Point;

    /*private*/ editor: ImplicitSymmetryParameters;

    public constructor(editor?: any, newCenter?: any) {
        if (((editor != null && (editor.constructor != null && editor.constructor["__interfaces"] != null && editor.constructor["__interfaces"].indexOf("com.vzome.core.editor.api.ImplicitSymmetryParameters") >= 0)) || editor === null) && ((newCenter != null && newCenter instanceof <any>Point) || newCenter === null)) {
            let __args = arguments;
            super();
            if (this.mOldCenter === undefined) { this.mOldCenter = null; } 
            if (this.mNewCenter === undefined) { this.mNewCenter = null; } 
            if (this.editor === undefined) { this.editor = null; } 
            this.mOldCenter = editor.getCenterPoint();
            this.mNewCenter = newCenter;
            this.editor = editor;
        } else if (((editor != null && (editor.constructor != null && editor.constructor["__interfaces"] != null && editor.constructor["__interfaces"].indexOf("com.vzome.core.editor.api.ImplicitSymmetryParameters") >= 0)) || editor === null) && newCenter === undefined) {
            let __args = arguments;
            {
                let __args = arguments;
                let newCenter: any = null;
                super();
                if (this.mOldCenter === undefined) { this.mOldCenter = null; } 
                if (this.mNewCenter === undefined) { this.mNewCenter = null; } 
                if (this.editor === undefined) { this.editor = null; } 
                this.mOldCenter = editor.getCenterPoint();
                this.mNewCenter = newCenter;
                this.editor = editor;
            }
        } else throw new Error('invalid overload');
    }

    public configure(props: java.util.Map<string, any>) {
        const man: Manifestation = <Manifestation><any>props.get("picked");
        if (man != null)this.mNewCenter = <Point>man.getFirstConstruction();
    }

    /**
     * 
     * @return {boolean}
     */
    public isNoOp(): boolean {
        return this.mNewCenter == null || this.mNewCenter.getLocation().equals(this.mOldCenter.getLocation());
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
     */
    public redo() {
        if (this.isNoOp())return;
        this.editor.setCenterPoint(this.mNewCenter);
    }

    /**
     * 
     */
    public undo() {
        if (this.isNoOp())return;
        this.editor.setCenterPoint(this.mOldCenter);
    }

    /**
     * 
     * @param {*} doc
     * @return {*}
     */
    public getXml(doc: Document): Element {
        const result: Element = doc.createElement("SymmetryCenterChange");
        XmlSaveFormat.serializePoint(result, "new", this.mNewCenter);
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
            this.mNewCenter = format.parsePoint$org_w3c_dom_Element$java_lang_String(xml, "new");
        } else {
            const attrs: AttributeMap = format.loadCommandAttributes$org_w3c_dom_Element(xml);
            const center: Point = <Point>attrs.get("new");
            this.mNewCenter = new FreePoint(center.getLocation().projectTo3d(true));
        }
        context.performAndRecord(this);
    }

    /**
     * 
     */
    public perform() {
        if (this.mNewCenter == null){
            this.mNewCenter = <Point>this.editor.getSelectedConstruction(Point);
            if (this.mNewCenter == null)throw new Command.Failure("Selection is not a single ball.");
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
SymmetryCenterChange["__class"] = "com.vzome.core.edits.SymmetryCenterChange";
