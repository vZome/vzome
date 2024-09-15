/* Generated from Java with JSweet 3.2.0-SNAPSHOT - http://www.jsweet.org */
namespace com.vzome.core.edits {
    export class AffineTransformAll extends com.vzome.core.editor.api.ChangeManifestations {
        /*private*/ center: com.vzome.core.construction.Point;

        public constructor(editorModel: com.vzome.core.editor.api.EditorModel) {
            super(editorModel);
            if (this.center === undefined) { this.center = null; }
            this.center = (<com.vzome.core.editor.api.ImplicitSymmetryParameters><any>editorModel).getCenterPoint();
        }

        /**
         * 
         */
        public perform() {
            let s1: com.vzome.core.construction.Segment = null;
            let s2: com.vzome.core.construction.Segment = null;
            let s3: com.vzome.core.construction.Segment = null;
            for(let index=this.mSelection.iterator();index.hasNext();) {
                let man = index.next();
                {
                    this.unselect$com_vzome_core_model_Manifestation(man);
                    if (man != null && (man.constructor != null && man.constructor["__interfaces"] != null && man.constructor["__interfaces"].indexOf("com.vzome.core.model.Strut") >= 0)){
                        if (s1 == null)s1 = <com.vzome.core.construction.Segment>man.getFirstConstruction(); else if (s2 == null)s2 = <com.vzome.core.construction.Segment>man.getFirstConstruction(); else if (s3 == null)s3 = <com.vzome.core.construction.Segment>man.getFirstConstruction();
                    }
                }
            }
            if (s3 == null || s2 == null || s1 == null)throw new com.vzome.core.commands.Command.Failure("three struts required");
            this.redo();
            const transform: com.vzome.core.construction.Transformation = new com.vzome.core.construction.ChangeOfBasis(s1, s2, s3, this.center, true);
            for(let index=this.mManifestations.iterator();index.hasNext();) {
                let m = index.next();
                {
                    if (!m.isRendered())continue;
                    const c: com.vzome.core.construction.Construction = m.getFirstConstruction();
                    const result: com.vzome.core.construction.Construction = transform.transform$com_vzome_core_construction_Construction(c);
                    this.select$com_vzome_core_model_Manifestation(this.manifestConstruction(result));
                }
            }
            this.redo();
        }

        /**
         * 
         * @return {string}
         */
        getXmlElementName(): string {
            return "AffineTransformAll";
        }
    }
    AffineTransformAll["__class"] = "com.vzome.core.edits.AffineTransformAll";

}

