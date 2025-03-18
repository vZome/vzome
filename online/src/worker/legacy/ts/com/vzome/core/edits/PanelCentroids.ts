/* Generated from Java with JSweet 3.2.0-SNAPSHOT - http://www.jsweet.org */
namespace com.vzome.core.edits {
    export class PanelCentroids extends com.vzome.core.editor.api.ChangeManifestations {
        public constructor(editorModel: com.vzome.core.editor.api.EditorModel) {
            super(editorModel);
        }

        /**
         * 
         */
        public perform() {
            for(let index=this.mSelection.iterator();index.hasNext();) {
                let man = index.next();
                {
                    this.unselect$com_vzome_core_model_Manifestation(man);
                    if (man != null && (man.constructor != null && man.constructor["__interfaces"] != null && man.constructor["__interfaces"].indexOf("com.vzome.core.model.Panel") >= 0)){
                        const panel: com.vzome.core.model.Panel = <com.vzome.core.model.Panel><any>man;
                        this.select$com_vzome_core_model_Manifestation(this.manifestConstruction(new com.vzome.core.construction.FreePoint(panel.getCentroid())));
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
            return "PanelCentroids";
        }
    }
    PanelCentroids["__class"] = "com.vzome.core.edits.PanelCentroids";

}

