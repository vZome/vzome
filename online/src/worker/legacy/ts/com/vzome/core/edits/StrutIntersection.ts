/* Generated from Java with JSweet 3.2.0-SNAPSHOT - http://www.jsweet.org */
namespace com.vzome.core.edits {
    export class StrutIntersection extends com.vzome.core.editor.api.ChangeManifestations {
        public constructor(editor: com.vzome.core.editor.api.EditorModel) {
            super(editor);
        }

        /**
         * 
         */
        public perform() {
            let s1: com.vzome.core.model.Strut = null;
            let s2: com.vzome.core.model.Strut = null;
            for(let index=this.mSelection.iterator();index.hasNext();) {
                let man = index.next();
                {
                    this.unselect$com_vzome_core_model_Manifestation(man);
                    if (man != null && (man.constructor != null && man.constructor["__interfaces"] != null && man.constructor["__interfaces"].indexOf("com.vzome.core.model.Strut") >= 0))if (s1 == null)s1 = <com.vzome.core.model.Strut><any>man; else if (s2 == null)s2 = <com.vzome.core.model.Strut><any>man; else throw new com.vzome.core.commands.Command.Failure("only two struts are allowed");
                }
            }
            if (s1 == null || s2 == null)throw new com.vzome.core.commands.Command.Failure("two struts are required");
            const l1: com.vzome.core.construction.Line = new com.vzome.core.construction.LineFromPointAndVector(s1.getLocation(), s1.getZoneVector());
            const l2: com.vzome.core.construction.Line = new com.vzome.core.construction.LineFromPointAndVector(s2.getLocation(), s2.getZoneVector());
            const point: com.vzome.core.construction.Point = new com.vzome.core.construction.LineLineIntersectionPoint(l1, l2);
            if (point.isImpossible())throw new com.vzome.core.commands.Command.Failure("lines are parallel or non-intersecting");
            const ball: com.vzome.core.model.Manifestation = this.manifestConstruction(point);
            this.select$com_vzome_core_model_Manifestation(ball);
            this.redo();
        }

        /**
         * 
         * @return {string}
         */
        getXmlElementName(): string {
            return "StrutIntersection";
        }
    }
    StrutIntersection["__class"] = "com.vzome.core.edits.StrutIntersection";

}

