/* Generated from Java with JSweet 3.2.0-SNAPSHOT - http://www.jsweet.org */
namespace com.vzome.core.edits {
    export class LoadVEF extends com.vzome.core.edits.ImportMesh {
        public constructor(editor: com.vzome.core.editor.api.EditorModel) {
            super(editor);
        }

        deselectInputs(): boolean {
            return false;
        }

        /**
         * 
         * @param {com.vzome.core.algebra.AlgebraicVector} offset
         * @param {com.vzome.core.editor.api.ManifestConstructions} events
         * @param {*} registry
         */
        parseMeshData(offset: com.vzome.core.algebra.AlgebraicVector, events: com.vzome.core.editor.api.ManifestConstructions, registry: com.vzome.core.algebra.AlgebraicField.Registry) {
            const v2m: com.vzome.core.construction.VefToModel = new com.vzome.core.construction.VefToModel(this.projection, events, this.scale, offset);
            v2m.parseVEF(this.meshData, this.mManifestations.getField());
        }

        /**
         * 
         * @return {string}
         */
        getXmlElementName(): string {
            return "LoadVEF";
        }
    }
    LoadVEF["__class"] = "com.vzome.core.edits.LoadVEF";

}

