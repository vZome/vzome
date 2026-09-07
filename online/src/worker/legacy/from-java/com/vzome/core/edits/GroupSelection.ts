import { java, javaemul } from "../../../../../candies/j4ts-2.1.0-SNAPSHOT/bundle.js";
import { XmlSaveFormat } from "../commands/XmlSaveFormat.js";
import { Context } from "../editor/api/Context.js";
import { EditorModel } from "../editor/api/EditorModel.js";
import { Selection } from "../editor/api/Selection.js";
import { UndoableEdit } from "../editor/api/UndoableEdit.js";
import { DomUtils } from "../../xml/DomUtils.js";
import { Document } from "../../../../org/w3c/dom/Document.js";
import { Element } from "../../../../org/w3c/dom/Element.js";

export class GroupSelection extends UndoableEdit {
    mSelection: Selection;

    /*private*/ mGrouping: boolean;

    /*private*/ recursiveGroups: boolean;

    /*private*/ unnecessary: boolean;

    public constructor(editor: EditorModel) {
        super();
        if (this.mSelection === undefined) { this.mSelection = null; }
        this.mGrouping = false;
        this.recursiveGroups = true;
        this.unnecessary = false;
        this.mSelection = editor.getSelection();
    }

    /**
     * 
     * @param {*} props
     */
    public configure(props: java.util.Map<string, any>) {
        const mode: string = <string>props.get("mode");
        this.mGrouping = (mode == null) || /* isEmpty */(mode.length === 0) || (mode === ("group"));
        this.unnecessary = this.mGrouping === this.mSelection.isSelectionAGroup();
    }

    /**
     * 
     * @return {boolean}
     */
    public isNoOp(): boolean {
        return this.unnecessary;
    }

    /**
     * 
     * @param {*} doc
     * @return {*}
     */
    public getXml(doc: Document): Element {
        const elem: Element = doc.createElement("GroupSelection");
        if (!this.mGrouping)DomUtils.addAttribute(elem, "grouping", "false");
        return elem;
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
        const grouping: string = xml.getAttribute("grouping");
        this.mGrouping = (grouping == null) || /* isEmpty */(grouping.length === 0) || (grouping === ("true"));
        this.recursiveGroups = format.groupingRecursive();
        context.performAndRecord(this);
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
    public isVisible(): boolean {
        return true;
    }

    /**
     * 
     */
    public redo() {
        if (this.mGrouping)if (this.recursiveGroups)this.mSelection.gatherGroup(); else this.mSelection.gatherGroup211(); else if (this.recursiveGroups)this.mSelection.scatterGroup(); else this.mSelection.scatterGroup211();
    }

    /**
     * 
     */
    public undo() {
        if (!this.mGrouping)if (this.recursiveGroups)this.mSelection.gatherGroup(); else this.mSelection.gatherGroup211(); else if (this.recursiveGroups)this.mSelection.scatterGroup(); else this.mSelection.scatterGroup211();
    }

    /**
     * 
     */
    public perform() {
        if (this.unnecessary)return;
        this.redo();
    }

    /**
     * 
     * @return {boolean}
     */
    public isSticky(): boolean {
        return false;
    }
}
GroupSelection["__class"] = "com.vzome.core.edits.GroupSelection";
