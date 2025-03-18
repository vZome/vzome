/* Generated from Java with JSweet 3.2.0-SNAPSHOT - http://www.jsweet.org */
namespace com.vzome.core.edits {
    export class NewCentroid extends com.vzome.core.editor.api.ChangeManifestations {
        /**
         * 
         */
        public perform() {
            const verticesList: java.util.List<com.vzome.core.construction.Point> = <any>(new java.util.ArrayList<any>());
            for(let index=this.mSelection.iterator();index.hasNext();) {
                let man = index.next();
                {
                    this.unselect$com_vzome_core_model_Manifestation(man);
                    const construction: com.vzome.core.construction.Construction = man.toConstruction();
                    if (construction != null && construction instanceof <any>com.vzome.core.construction.Point){
                        const nextPoint: com.vzome.core.construction.Point = <com.vzome.core.construction.Point>construction;
                        verticesList.add(nextPoint);
                    }
                }
            }
            if (verticesList.size() < 2)throw new com.vzome.core.commands.Command.Failure("Select at least two balls to compute the centroid.");
            const points: com.vzome.core.construction.Point[] = [];
            const centroid: com.vzome.core.construction.CentroidPoint = new com.vzome.core.construction.CentroidPoint(verticesList.toArray<any>(points));
            this.manifestConstruction(centroid);
            this.redo();
        }

        public constructor(editorModel: com.vzome.core.editor.api.EditorModel) {
            super(editorModel);
        }

        /**
         * 
         * @return {boolean}
         */
        groupingAware(): boolean {
            return true;
        }

        /**
         * 
         * @return {string}
         */
        getXmlElementName(): string {
            return "NewCentroid";
        }
    }
    NewCentroid["__class"] = "com.vzome.core.edits.NewCentroid";

}

