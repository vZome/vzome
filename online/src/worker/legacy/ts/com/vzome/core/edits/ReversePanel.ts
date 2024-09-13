/* Generated from Java with JSweet 3.2.0-SNAPSHOT - http://www.jsweet.org */
namespace com.vzome.core.edits {
    /**
     * Work in progress, to help someone create correctly oriented surfaces for vZome part export,
     * or for StL 3D printing export.
     * 
     * @author Scott Vorthmann
     * @param {*} singlePanel
     * @param {*} editor
     * @class
     * @extends com.vzome.core.editor.api.ChangeManifestations
     */
    export class ReversePanel extends com.vzome.core.editor.api.ChangeManifestations {
        /**
         * 
         */
        public perform() {
            if (this.panel != null){
                if (this.mSelection.manifestationSelected(this.panel))this.unselect$com_vzome_core_model_Manifestation(this.panel);
                const polygon: com.vzome.core.construction.Polygon = <com.vzome.core.construction.Polygon>this.panel.getFirstConstruction();
                this.unmanifestConstruction(polygon);
            }
            this.redo();
        }

        /*private*/ panel: com.vzome.core.model.Panel;

        public constructor(singlePanel: com.vzome.core.model.Manifestation, editor: com.vzome.core.editor.api.EditorModel) {
            super(editor);
            if (this.panel === undefined) { this.panel = null; }
            if (singlePanel != null)this.panel = <com.vzome.core.model.Panel><any>singlePanel; else this.panel = null;
        }

        /**
         * 
         * @return {string}
         */
        getXmlElementName(): string {
            return "ReversePanel";
        }
    }
    ReversePanel["__class"] = "com.vzome.core.edits.ReversePanel";

}

