/* Generated from Java with JSweet 3.2.0-SNAPSHOT - http://www.jsweet.org */
namespace com.vzome.core.editor {
    export class Snapshot extends com.vzome.core.editor.api.UndoableEdit {
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
        public getXml(doc: org.w3c.dom.Document): org.w3c.dom.Element {
            const xml: org.w3c.dom.Element = doc.createElement("Snapshot");
            com.vzome.xml.DomUtils.addAttribute(xml, "id", /* toString */(''+(this.id)));
            return xml;
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
            actOnSnapshot(snapshot: com.vzome.core.render.RenderedModel);
        }
    }

}

