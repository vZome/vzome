/* Generated from Java with JSweet 3.2.0-SNAPSHOT - http://www.jsweet.org */
namespace com.vzome.core.edits {
    export class GroupSelection extends com.vzome.core.editor.api.UndoableEdit {
        mSelection: com.vzome.core.editor.api.Selection;

        /*private*/ mGrouping: boolean;

        /*private*/ recursiveGroups: boolean;

        /*private*/ unnecessary: boolean;

        public constructor(editor: com.vzome.core.editor.api.EditorModel) {
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
        public getXml(doc: org.w3c.dom.Document): org.w3c.dom.Element {
            const elem: org.w3c.dom.Element = doc.createElement("GroupSelection");
            if (!this.mGrouping)com.vzome.xml.DomUtils.addAttribute(elem, "grouping", "false");
            return elem;
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

}

