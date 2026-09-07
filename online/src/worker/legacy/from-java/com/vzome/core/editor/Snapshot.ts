import { java, javaemul } from "../../../../../candies/j4ts-2.1.0-SNAPSHOT/bundle.js";
import { XmlSaveFormat } from "../commands/XmlSaveFormat.js";
import { Context } from "./api/Context.js";
import { UndoableEdit } from "./api/UndoableEdit.js";
import { RenderedModel } from "../render/RenderedModel.js";
import { DomUtils } from "../../xml/DomUtils.js";
import { Document } from "../../../../org/w3c/dom/Document.js";
import { Element } from "../../../../org/w3c/dom/Element.js";

export class Snapshot extends UndoableEdit {
    /**
     * 
     * @return {boolean}
     */
    public isNoOp(): boolean {
        return false;
    }

    /*private*/ id: number;

    /*private*/ recorder: Snapshot.Recorder;

    /**
     * 
     */
    public perform() {
        this.recorder.recordSnapshot(this.id);
    }

    public constructor(id: number, controller: Snapshot.Recorder) {
        super();
        if (this.id === undefined) { this.id = 0; }
        if (this.recorder === undefined) { this.recorder = null; }
        this.id = id;
        this.recorder = controller;
    }

    /**
     * 
     */
    public undo() {
    }

    /**
     * 
     */
    public redo() {
    }

    /**
     * 
     * @param {*} props
     */
    public configure(props: java.util.Map<string, any>) {
        const idProp: number = <number>props.get("id");
        if (idProp != null)this.id = /* intValue */(idProp|0);
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
     * @param {*} doc
     * @return {*}
     */
    public getXml(doc: Document): Element {
        const xml: Element = doc.createElement("Snapshot");
        DomUtils.addAttribute(xml, "id", /* toString */(''+(this.id)));
        return xml;
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
        this.id = javaemul.internal.IntegerHelper.parseInt(xml.getAttribute("id"));
        context.performAndRecord(this);
    }

    /**
     * 
     * @return {boolean}
     */
    public isSticky(): boolean {
        return true;
    }
}
Snapshot["__class"] = "com.vzome.core.editor.Snapshot";


export namespace Snapshot {

    export interface Recorder {
        recordSnapshot(id: number);

        actOnSnapshot(id: number, action: Snapshot.SnapshotAction);
    }

    export interface SnapshotAction {
        actOnSnapshot(snapshot: RenderedModel);
    }
}
