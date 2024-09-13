/* Generated from Java with JSweet 3.2.0-SNAPSHOT - http://www.jsweet.org */
namespace com.vzome.core.editor {
    export class Branch extends com.vzome.core.editor.api.UndoableEdit {
        /*private*/ context: com.vzome.core.editor.api.Context;

        public constructor(context: com.vzome.core.editor.api.Context) {
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

        /*private*/ edits: java.util.List<com.vzome.core.editor.api.UndoableEdit>;

        /*private*/ format: com.vzome.core.commands.XmlSaveFormat;

        /*private*/ xml: org.w3c.dom.Element;

        public addEdit(edit: com.vzome.core.editor.api.UndoableEdit) {
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
            const toUndo: java.util.Stack<com.vzome.core.editor.api.UndoableEdit> = <any>(new java.util.Stack<any>());
            const nodes: org.w3c.dom.NodeList = this.xml.getChildNodes();
            for(let i: number = 0; i < nodes.getLength(); i++) {{
                const kid: org.w3c.dom.Node = nodes.item(i);
                if (kid != null && (kid.constructor != null && kid.constructor["__interfaces"] != null && kid.constructor["__interfaces"].indexOf("org.w3c.dom.Element") >= 0)){
                    const editElem: org.w3c.dom.Element = <org.w3c.dom.Element><any>kid;
                    const edit: com.vzome.core.editor.api.UndoableEdit = this.context.createEdit(editElem);
                    this.addEdit(edit);
                    edit.loadAndPerform(editElem, this.format, new Branch.Branch$0(this, toUndo));
                }
            };}
            while((!toUndo.isEmpty())) {{
                const edit: com.vzome.core.editor.api.UndoableEdit = toUndo.pop();
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
        public getXml(doc: org.w3c.dom.Document): org.w3c.dom.Element {
            const branch: org.w3c.dom.Element = doc.createElement("Branch");
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
        public getDetailXml(doc: org.w3c.dom.Document): org.w3c.dom.Element {
            const branch: org.w3c.dom.Element = doc.createElement("Branch");
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
         * @param {com.vzome.core.commands.XmlSaveFormat} format
         * @param {*} context
         */
        public loadAndPerform(xml: org.w3c.dom.Element, format: com.vzome.core.commands.XmlSaveFormat, context: com.vzome.core.editor.api.Context) {
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

        export class Branch$0 implements com.vzome.core.editor.api.Context {
            public __parent: any;
            /**
             * 
             * @param {com.vzome.core.editor.api.UndoableEdit} edit
             */
            public performAndRecord(edit: com.vzome.core.editor.api.UndoableEdit) {
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
             * @return {com.vzome.core.editor.api.UndoableEdit}
             */
            public createEdit(xml: org.w3c.dom.Element): com.vzome.core.editor.api.UndoableEdit {
                return this.__parent.context.createEdit(xml);
            }

            /**
             * 
             * @param {string} cmdName
             * @return {*}
             */
            public createLegacyCommand(cmdName: string): com.vzome.core.commands.Command {
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

}

