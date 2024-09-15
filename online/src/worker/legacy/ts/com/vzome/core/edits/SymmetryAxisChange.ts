/* Generated from Java with JSweet 3.2.0-SNAPSHOT - http://www.jsweet.org */
namespace com.vzome.core.edits {
    /**
     * Used by CommandEdit.
     * @param {*} editor
     * @param m
     * @param {com.vzome.core.construction.Segment} newAxis
     * @class
     * @extends com.vzome.core.editor.api.UndoableEdit
     */
    export class SymmetryAxisChange extends com.vzome.core.editor.api.UndoableEdit {
        /*private*/ mOldAxis: com.vzome.core.construction.Segment;

        /*private*/ mNewAxis: com.vzome.core.construction.Segment;

        /*private*/ mEditor: com.vzome.core.editor.api.ImplicitSymmetryParameters;

        public constructor(editor?: any, newAxis?: any) {
            if (((editor != null && (editor.constructor != null && editor.constructor["__interfaces"] != null && editor.constructor["__interfaces"].indexOf("com.vzome.core.editor.api.ImplicitSymmetryParameters") >= 0)) || editor === null) && ((newAxis != null && newAxis instanceof <any>com.vzome.core.construction.Segment) || newAxis === null)) {
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
            const man: com.vzome.core.model.Manifestation = <com.vzome.core.model.Manifestation><any>props.get("picked");
            if (man != null)this.mNewAxis = <com.vzome.core.construction.Segment>man.getFirstConstruction();
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
        public getXml(doc: org.w3c.dom.Document): org.w3c.dom.Element {
            const result: org.w3c.dom.Element = doc.createElement("SymmetryAxisChange");
            com.vzome.core.commands.XmlSaveFormat.serializeSegment(result, "start", "end", this.mNewAxis);
            return result;
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
         * @param {*} xml
         * @param {com.vzome.core.commands.XmlSaveFormat} format
         * @param {*} context
         */
        public loadAndPerform(xml: org.w3c.dom.Element, format: com.vzome.core.commands.XmlSaveFormat, context: com.vzome.core.editor.api.Context) {
            if (format.rationalVectors()){
                this.mNewAxis = format.parseSegment$org_w3c_dom_Element$java_lang_String$java_lang_String(xml, "start", "end");
            } else {
                const attrs: com.vzome.core.commands.AttributeMap = format.loadCommandAttributes$org_w3c_dom_Element(xml);
                this.mNewAxis = <com.vzome.core.construction.Segment>attrs.get("new");
            }
            context.performAndRecord(this);
        }

        /**
         * 
         */
        public perform() {
            if (this.mNewAxis == null){
                this.mNewAxis = <com.vzome.core.construction.Segment>this.mEditor.getSelectedConstruction(com.vzome.core.construction.Segment);
                if (this.mNewAxis == null)throw new com.vzome.core.commands.Command.Failure("Selection is not a single strut.");
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

}

