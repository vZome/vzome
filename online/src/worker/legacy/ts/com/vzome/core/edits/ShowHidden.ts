/* Generated from Java with JSweet 3.2.0-SNAPSHOT - http://www.jsweet.org */
namespace com.vzome.core.edits {
    export class ShowHidden extends com.vzome.core.editor.api.ChangeManifestations {
        public constructor(editor: com.vzome.core.editor.api.EditorModel) {
            super(editor);
        }

        /**
         * 
         */
        public perform() {
            for(let index=this.mManifestations.iterator();index.hasNext();) {
                let m = index.next();
                {
                    if (m.isHidden()){
                        this.showManifestation(m);
                        this.select$com_vzome_core_model_Manifestation(m);
                    } else if (this.mSelection.manifestationSelected(m))this.unselect$com_vzome_core_model_Manifestation(m);
                }
            }
            this.redo();
        }

        /**
         * 
         * @return {string}
         */
        getXmlElementName(): string {
            return "ShowHidden";
        }
    }
    ShowHidden["__class"] = "com.vzome.core.edits.ShowHidden";

}

