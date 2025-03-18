/* Generated from Java with JSweet 3.2.0-SNAPSHOT - http://www.jsweet.org */
namespace com.vzome.core.edits {
    export class CrossProduct extends com.vzome.core.editor.api.ChangeManifestations {
        /**
         * 
         */
        public perform() {
            let p1: com.vzome.core.construction.Point = null;
            let p2: com.vzome.core.construction.Point = null;
            let s1: com.vzome.core.construction.Segment = null;
            let success: boolean = false;
            this.setOrderedSelection(true);
            for(let index=this.mSelection.iterator();index.hasNext();) {
                let man = index.next();
                {
                    if (success){
                        this.recordSelected(man);
                    } else {
                        this.unselect$com_vzome_core_model_Manifestation(man);
                        if (man != null && (man.constructor != null && man.constructor["__interfaces"] != null && man.constructor["__interfaces"].indexOf("com.vzome.core.model.Connector") >= 0)){
                            const nextPoint: com.vzome.core.construction.Point = <com.vzome.core.construction.Point>(<com.vzome.core.model.Connector><any>man).getFirstConstruction();
                            if (p1 == null){
                                p1 = nextPoint;
                            } else if (s1 == null){
                                p2 = nextPoint;
                                s1 = new com.vzome.core.construction.SegmentJoiningPoints(p1, nextPoint);
                            } else if (!success){
                                let segment: com.vzome.core.construction.Segment = new com.vzome.core.construction.SegmentJoiningPoints(p2, nextPoint);
                                segment = new com.vzome.core.construction.SegmentCrossProduct(s1, segment);
                                this.select$com_vzome_core_model_Manifestation(this.manifestConstruction(segment));
                                const endpt: com.vzome.core.construction.Point = new com.vzome.core.construction.SegmentEndPoint(segment);
                                this.manifestConstruction(endpt);
                                success = true;
                            } else this.recordSelected(man);
                        }
                    }
                }
            }
            if (!success)throw new com.vzome.core.commands.Command.Failure("cross-product requires three selected vertices");
            this.redo();
        }

        public constructor(editorModel: com.vzome.core.editor.api.EditorModel) {
            super(editorModel);
        }

        /**
         * 
         * @return {string}
         */
        getXmlElementName(): string {
            return "CrossProduct";
        }
    }
    CrossProduct["__class"] = "com.vzome.core.edits.CrossProduct";

}

