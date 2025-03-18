/* Generated from Java with JSweet 3.2.0-SNAPSHOT - http://www.jsweet.org */
namespace com.vzome.core.edits {
    export class InvertSelection extends com.vzome.core.editor.api.ChangeSelection {
        mManifestations: com.vzome.core.model.RealizedModel;

        public constructor(editor: com.vzome.core.editor.api.EditorModel) {
            super(editor.getSelection());
            if (this.mManifestations === undefined) { this.mManifestations = null; }
            this.mManifestations = editor.getRealizedModel();
        }

        /**
         * 
         */
        public perform() {
            for(let index=this.mManifestations.iterator();index.hasNext();) {
                let m = index.next();
                {
                    if (m.isRendered()){
                        if (this.mSelection.manifestationSelected(m))this.unselect$com_vzome_core_model_Manifestation(m); else this.select$com_vzome_core_model_Manifestation(m);
                    }
                }
            }
            this.redo();
        }

        /**
         * 
         * @return {string}
         */
        getXmlElementName(): string {
            return "InvertSelection";
        }
    }
    InvertSelection["__class"] = "com.vzome.core.edits.InvertSelection";

}

