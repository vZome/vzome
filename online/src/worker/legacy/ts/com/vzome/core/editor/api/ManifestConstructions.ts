/* Generated from Java with JSweet 3.2.0-SNAPSHOT - http://www.jsweet.org */
namespace com.vzome.core.editor.api {
    export class ManifestConstructions extends java.util.ArrayList<com.vzome.core.construction.Construction> implements com.vzome.core.construction.ConstructionChanges {
        /*private*/ edit: com.vzome.core.editor.api.ChangeManifestations;

        public constructor(edit: com.vzome.core.editor.api.ChangeManifestations) {
            super();
            if (this.edit === undefined) { this.edit = null; }
            this.edit = edit;
        }

        public constructionAdded$com_vzome_core_construction_Construction(c: com.vzome.core.construction.Construction) {
            this.edit.manifestConstruction(c);
            this.edit.redo();
        }

        public constructionAdded$com_vzome_core_construction_Construction$com_vzome_core_construction_Color(c: com.vzome.core.construction.Construction, color: com.vzome.core.construction.Color) {
            const manifestation: com.vzome.core.model.Manifestation = this.edit.manifestConstruction(c);
            if (color != null)this.edit.colorManifestation(manifestation, color);
            this.edit.select$com_vzome_core_model_Manifestation(manifestation);
            this.edit.redo();
        }

        /**
         * 
         * @param {com.vzome.core.construction.Construction} c
         * @param {com.vzome.core.construction.Color} color
         */
        public constructionAdded(c?: any, color?: any) {
            if (((c != null && c instanceof <any>com.vzome.core.construction.Construction) || c === null) && ((color != null && color instanceof <any>com.vzome.core.construction.Color) || color === null)) {
                return <any>this.constructionAdded$com_vzome_core_construction_Construction$com_vzome_core_construction_Color(c, color);
            } else if (((c != null && c instanceof <any>com.vzome.core.construction.Construction) || c === null) && color === undefined) {
                return <any>this.constructionAdded$com_vzome_core_construction_Construction(c);
            } else throw new Error('invalid overload');
        }
    }
    ManifestConstructions["__class"] = "com.vzome.core.editor.api.ManifestConstructions";
    ManifestConstructions["__interfaces"] = ["java.util.RandomAccess","java.util.List","java.lang.Cloneable","com.vzome.core.construction.ConstructionChanges","java.util.Collection","java.lang.Iterable","java.io.Serializable"];


}

