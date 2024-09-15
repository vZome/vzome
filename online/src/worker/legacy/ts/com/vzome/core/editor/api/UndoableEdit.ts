/* Generated from Java with JSweet 3.2.0-SNAPSHOT - http://www.jsweet.org */
namespace com.vzome.core.editor.api {
    export abstract class UndoableEdit {
        public abstract configure(props: java.util.Map<string, any>);

        public abstract perform();

        public abstract undo();

        public abstract redo();

        public abstract isNoOp(): boolean;

        public abstract isVisible(): boolean;

        public abstract getXml(doc: org.w3c.dom.Document): org.w3c.dom.Element;

        public abstract loadAndPerform(xml: org.w3c.dom.Element, format: com.vzome.core.commands.XmlSaveFormat, context: com.vzome.core.editor.api.Context);

        /**
         * True when this edit must cause a persistent history branch.
         * @return
         * @return {boolean}
         */
        public abstract isSticky(): boolean;

        /**
         * True when this edit invalidates redoable edits.
         * @return
         * @return {boolean}
         */
        public abstract isDestructive(): boolean;

        public abstract getDetailXml(doc: org.w3c.dom.Document): org.w3c.dom.Element;

        /*private*/ __com_vzome_core_editor_EditHistory_DeferredEdit_hasBreakpoint: boolean;

        /*private*/ lineNumber: number;

        public hasBreakpoint(): boolean {
            return this.__com_vzome_core_editor_EditHistory_DeferredEdit_hasBreakpoint;
        }

        public setBreakpoint(value: boolean) {
            this.__com_vzome_core_editor_EditHistory_DeferredEdit_hasBreakpoint = value;
        }

        public getLineNumber(): number {
            return this.lineNumber;
        }

        public setLineNumber(lineNumber: number) {
            this.lineNumber = lineNumber;
        }

        constructor() {
            this.__com_vzome_core_editor_EditHistory_DeferredEdit_hasBreakpoint = false;
            this.lineNumber = -1;
        }
    }
    UndoableEdit["__class"] = "com.vzome.core.editor.api.UndoableEdit";

}

