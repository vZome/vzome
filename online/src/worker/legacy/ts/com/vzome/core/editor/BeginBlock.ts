/* Generated from Java with JSweet 3.2.0-SNAPSHOT - http://www.jsweet.org */
namespace com.vzome.core.editor {
    /**
     * Just a marker in the history.
     * @author Scott Vorthmann
     * @param {*} editor
     * @class
     * @extends com.vzome.core.editor.api.UndoableEdit
     */
    export class BeginBlock extends com.vzome.core.editor.api.UndoableEdit {
        public constructor(editor: com.vzome.core.editor.api.EditorModel) {
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
        public getXml(doc: org.w3c.dom.Document): org.w3c.dom.Element {
            return doc.createElement("BeginBlock");
        }

        /**
         * 
         * @param {*} doc
         * @return {*}
         */
        public getDetailXml(doc: org.w3c.dom.Document): org.w3c.dom.Element {
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
         * @param {com.vzome.core.commands.XmlSaveFormat} format
         * @param {*} context
         */
        public loadAndPerform(xml: org.w3c.dom.Element, format: com.vzome.core.commands.XmlSaveFormat, context: com.vzome.core.editor.api.Context) {
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

}

