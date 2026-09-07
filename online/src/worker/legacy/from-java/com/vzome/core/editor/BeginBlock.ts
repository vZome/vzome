import { java, javaemul } from "../../../../../candies/j4ts-2.1.0-SNAPSHOT/bundle.js";
import { XmlSaveFormat } from "../commands/XmlSaveFormat.js";
import { Context } from "./api/Context.js";
import { EditorModel } from "./api/EditorModel.js";
import { UndoableEdit } from "./api/UndoableEdit.js";
import { Document } from "../../../../org/w3c/dom/Document.js";
import { Element } from "../../../../org/w3c/dom/Element.js";

/**
 * Just a marker in the history.
 * @author Scott Vorthmann
 * @param {*} editor
 * @class
 * @extends UndoableEdit
 */
export class BeginBlock extends UndoableEdit {
    public constructor(editor: EditorModel) {
        super();
    }

    /**
     * 
     * @return {boolean}
     */
    public isNoOp(): boolean {
        return false;
    }

    /**
     * 
     * @param {*} doc
     * @return {*}
     */
    public getXml(doc: Document): Element {
        return doc.createElement("BeginBlock");
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
     * @return {boolean}
     */
    public isVisible(): boolean {
        return false;
    }

    /**
     * 
     * @return {boolean}
     */
    public isDestructive(): boolean {
        return false;
    }

    /**
     * 
     */
    public redo() {
    }

    /**
     * 
     * @param {*} xml
     * @param {XmlSaveFormat} format
     * @param {*} context
     */
    public loadAndPerform(xml: Element, format: XmlSaveFormat, context: Context) {
        context.performAndRecord(this);
    }

    /**
     * 
     */
    public undo() {
    }

    /**
     * 
     */
    public perform() {
    }

    /**
     * 
     * @return {boolean}
     */
    public isSticky(): boolean {
        return false;
    }

    /**
     * 
     * @param {*} props
     */
    public configure(props: java.util.Map<string, any>) {
    }
}
BeginBlock["__class"] = "com.vzome.core.editor.BeginBlock";
