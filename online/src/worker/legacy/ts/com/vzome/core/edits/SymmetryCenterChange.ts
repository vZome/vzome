/* Generated from Java with JSweet 3.2.0-SNAPSHOT - http://www.jsweet.org */
namespace com.vzome.core.edits {
    /**
     * Used by CommandEdit.
     * @param {*} editor
     * @param m
     * @param {com.vzome.core.construction.Point} newCenter
     * @class
     * @extends com.vzome.core.editor.api.UndoableEdit
     */
    export class SymmetryCenterChange extends com.vzome.core.editor.api.UndoableEdit {
        /*private*/ mOldCenter: com.vzome.core.construction.Point;

        /*private*/ mNewCenter: com.vzome.core.construction.Point;

        /*private*/ editor: com.vzome.core.editor.api.ImplicitSymmetryParameters;

        public constructor(editor?: any, newCenter?: any) {
            if (((editor != null && (editor.constructor != null && editor.constructor["__interfaces"] != null && editor.constructor["__interfaces"].indexOf("com.vzome.core.editor.api.ImplicitSymmetryParameters") >= 0)) || editor === null) && ((newCenter != null && newCenter instanceof <any>com.vzome.core.construction.Point) || newCenter === null)) {
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
            const man: com.vzome.core.model.Manifestation = <com.vzome.core.model.Manifestation><any>props.get("picked");
            if (man != null)this.mNewCenter = <com.vzome.core.construction.Point>man.getFirstConstruction();
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
        public getXml(doc: org.w3c.dom.Document): org.w3c.dom.Element {
            const result: org.w3c.dom.Element = doc.createElement("SymmetryCenterChange");
            com.vzome.core.commands.XmlSaveFormat.serializePoint(result, "new", this.mNewCenter);
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
                this.mNewCenter = format.parsePoint$org_w3c_dom_Element$java_lang_String(xml, "new");
            } else {
                const attrs: com.vzome.core.commands.AttributeMap = format.loadCommandAttributes$org_w3c_dom_Element(xml);
                const center: com.vzome.core.construction.Point = <com.vzome.core.construction.Point>attrs.get("new");
                this.mNewCenter = new com.vzome.core.construction.FreePoint(center.getLocation().projectTo3d(true));
            }
            context.performAndRecord(this);
        }

        /**
         * 
         */
        public perform() {
            if (this.mNewCenter == null){
                this.mNewCenter = <com.vzome.core.construction.Point>this.editor.getSelectedConstruction(com.vzome.core.construction.Point);
                if (this.mNewCenter == null)throw new com.vzome.core.commands.Command.Failure("Selection is not a single ball.");
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

}

