/* Generated from Java with JSweet 3.2.0-SNAPSHOT - http://www.jsweet.org */
namespace com.vzome.core.edits {
    export class TransformSelection extends com.vzome.core.editor.api.ChangeManifestations {
        transform: com.vzome.core.construction.Transformation;

        public constructor(editor: com.vzome.core.editor.api.EditorModel, transform: com.vzome.core.construction.Transformation) {
            super(editor);
            if (this.transform === undefined) { this.transform = null; }
            this.transform = transform;
        }

        /**
         * 
         */
        public perform() {
            const inputs: java.util.List<com.vzome.core.model.Manifestation> = <any>(new java.util.ArrayList<any>());
            for(let index=this.mSelection.iterator();index.hasNext();) {
                let man = index.next();
                {
                    this.unselect$com_vzome_core_model_Manifestation(man);
                    inputs.add(man);
                }
            }
            this.redo();
            for(let index=inputs.iterator();index.hasNext();) {
                let m = index.next();
                {
                    if (!m.isRendered())continue;
                    const c: com.vzome.core.construction.Construction = m.getFirstConstruction();
                    const result: com.vzome.core.construction.Construction = this.transform.transform$com_vzome_core_construction_Construction(c);
                    this.select$com_vzome_core_model_Manifestation$boolean(this.manifestConstruction(result), true);
                }
            }
            this.redo();
        }

        /**
         * 
         * @return {string}
         */
        getXmlElementName(): string {
            return "TransformSelection";
        }
    }
    TransformSelection["__class"] = "com.vzome.core.edits.TransformSelection";

}

