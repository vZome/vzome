import { java, javaemul } from "../../../../../candies/j4ts-2.1.0-SNAPSHOT/bundle.js";
import { Command } from "../commands/Command.js";
import { XmlSaveFormat } from "../commands/XmlSaveFormat.js";
import { Context } from "./api/Context.js";
import { UndoableEdit } from "./api/UndoableEdit.js";
import { Document } from "../../../../org/w3c/dom/Document.js";
import { Element } from "../../../../org/w3c/dom/Element.js";
import { Node } from "../../../../org/w3c/dom/Node.js";
import { NodeList } from "../../../../org/w3c/dom/NodeList.js";

export class Branch extends UndoableEdit {
    /*private*/ context: Context;

    public constructor(context: Context) {
        super();
        if (this.context === undefined) { this.context = null; }
        this.edits = <any>(new java.util.ArrayList<any>());
        if (this.format === undefined) { this.format = null; }
        if (this.xml === undefined) { this.xml = null; }
        this.context = context;
    }

    /**
     * 
     * @return {boolean}
     */
    public isNoOp(): boolean {
        return false;
    }

    /*private*/ edits: java.util.List<UndoableEdit>;

    /*private*/ format: XmlSaveFormat;

    /*private*/ xml: Element;

    public addEdit(edit: UndoableEdit) {
        this.edits.add(edit);
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
    public perform() {
        const toUndo: java.util.Stack<UndoableEdit> = <any>(new java.util.Stack<any>());
        const nodes: NodeList = this.xml.getChildNodes();
        for(let i: number = 0; i < nodes.getLength(); i++) {{
            const kid: Node = nodes.item(i);
            if (kid != null && (kid.constructor != null && kid.constructor["__interfaces"] != null && kid.constructor["__interfaces"].indexOf("org.w3c.dom.Element") >= 0)){
                const editElem: Element = <Element><any>kid;
                const edit: UndoableEdit = this.context.createEdit(editElem);
                this.addEdit(edit);
                edit.loadAndPerform(editElem, this.format, new Branch.Branch$0(this, toUndo));
            }
        };}
        while((!toUndo.isEmpty())) {{
            const edit: UndoableEdit = toUndo.pop();
            edit.undo();
        }};
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
     * @param {*} doc
     * @return {*}
     */
    public getXml(doc: Document): Element {
        const branch: Element = doc.createElement("Branch");
        for(let index=this.edits.iterator();index.hasNext();) {
            let edit = index.next();
            {
                branch.appendChild(edit.getXml(doc));
            }
        }
        return branch;
    }

    /**
     * 
     * @param {*} doc
     * @return {*}
     */
    public getDetailXml(doc: Document): Element {
        const branch: Element = doc.createElement("Branch");
        for(let index=this.edits.iterator();index.hasNext();) {
            let edit = index.next();
            {
                branch.appendChild(edit.getDetailXml(doc));
            }
        }
        return branch;
    }

    /**
     * 
     * @param {*} xml
     * @param {XmlSaveFormat} format
     * @param {*} context
     */
    public loadAndPerform(xml: Element, format: XmlSaveFormat, context: Context) {
        this.xml = xml;
        this.format = format;
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
Branch["__class"] = "com.vzome.core.editor.Branch";


export namespace Branch {

    export class Branch$0 implements Context {
        public __parent: any;
        /**
         * 
         * @param {UndoableEdit} edit
         */
        public performAndRecord(edit: UndoableEdit) {
            try {
                edit.perform();
            } catch(e) {
                throw new java.lang.RuntimeException(e);
            }
            this.toUndo.push(edit);
        }

        /**
         * 
         * @param {*} xml
         * @return {UndoableEdit}
         */
        public createEdit(xml: Element): UndoableEdit {
            return this.__parent.context.createEdit(xml);
        }

        /**
         * 
         * @param {string} cmdName
         * @return {*}
         */
        public createLegacyCommand(cmdName: string): Command {
            return this.__parent.context.createLegacyCommand(cmdName);
        }

        /**
         * 
         * @param {string} action
         * @param {*} props
         * @return {boolean}
         */
        public doEdit(action: string, props: java.util.Map<string, any>): boolean {
            return false;
        }

        constructor(__parent: any, private toUndo: any) {
            this.__parent = __parent;
        }
    }
    Branch$0["__interfaces"] = ["com.vzome.core.editor.api.Context"];


}
