/* Generated from Java with JSweet 3.2.0-SNAPSHOT - http://www.jsweet.org */
namespace com.vzome.core.edits {
    export class Delete extends com.vzome.core.editor.api.ChangeManifestations {
        /**
         * 
         */
        public perform() {
            const inputs: java.util.ArrayList<com.vzome.core.model.Manifestation> = <any>(new java.util.ArrayList<any>());
            for(let index=this.mSelection.iterator();index.hasNext();) {
                let man = index.next();
                {
                    inputs.add(man);
                    this.unselect$com_vzome_core_model_Manifestation(man);
                }
            }
            this.redo();
            for(let index=inputs.iterator();index.hasNext();) {
                let man = index.next();
                {
                    this.deleteManifestation(man);
                }
            }
            super.perform();
        }

        public constructor(editorModel: com.vzome.core.editor.api.EditorModel) {
            super(editorModel);
        }

        /**
         * 
         * @return {string}
         */
        getXmlElementName(): string {
            return "Delete";
        }
    }
    Delete["__class"] = "com.vzome.core.edits.Delete";

}

