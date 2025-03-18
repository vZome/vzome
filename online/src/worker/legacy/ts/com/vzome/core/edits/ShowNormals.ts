/* Generated from Java with JSweet 3.2.0-SNAPSHOT - http://www.jsweet.org */
namespace com.vzome.core.edits {
    export class ShowNormals extends com.vzome.core.editor.api.ChangeManifestations {
        public static NAME: string = "ShowNormals";

        /**
         * 
         */
        public perform() {
            const SCALE_DOWN: com.vzome.core.algebra.AlgebraicNumber = this.mManifestations.getField()['createAlgebraicNumber$int$int$int$int'](1, 0, 2, -3);
            this.unselectConnectors();
            this.unselectStruts();
            for(let index=com.vzome.core.editor.api.Manifestations.getPanels$java_lang_Iterable(this.mSelection).iterator();index.hasNext();) {
                let panel = index.next();
                {
                    this.unselect$com_vzome_core_model_Manifestation(panel);
                    const centroid: com.vzome.core.algebra.AlgebraicVector = panel.getCentroid();
                    const tip: com.vzome.core.algebra.AlgebraicVector = centroid.plus(panel['getNormal$']().scale(SCALE_DOWN));
                    const p1: com.vzome.core.construction.Point = new com.vzome.core.construction.FreePoint(centroid);
                    const p2: com.vzome.core.construction.Point = new com.vzome.core.construction.FreePoint(tip);
                    const s: com.vzome.core.construction.Segment = new com.vzome.core.construction.SegmentJoiningPoints(p1, p2);
                    this.select$com_vzome_core_model_Manifestation(this.manifestConstruction(p1));
                    this.select$com_vzome_core_model_Manifestation(this.manifestConstruction(p2));
                    this.select$com_vzome_core_model_Manifestation(this.manifestConstruction(s));
                }
            }
            this.redo();
        }

        public constructor(editor: com.vzome.core.editor.api.EditorModel) {
            super(editor);
        }

        /**
         * 
         * @return {string}
         */
        getXmlElementName(): string {
            return ShowNormals.NAME;
        }
    }
    ShowNormals["__class"] = "com.vzome.core.edits.ShowNormals";

}

