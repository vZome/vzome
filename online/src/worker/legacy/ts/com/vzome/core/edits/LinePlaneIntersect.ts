/* Generated from Java with JSweet 3.2.0-SNAPSHOT - http://www.jsweet.org */
namespace com.vzome.core.edits {
    export class LinePlaneIntersect extends com.vzome.core.editor.api.ChangeManifestations {
        public constructor(editor: com.vzome.core.editor.api.EditorModel) {
            super(editor);
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
         */
        public perform() {
            let panel: com.vzome.core.model.Panel = null;
            let strut: com.vzome.core.model.Strut = null;
            let p0: com.vzome.core.construction.Point = null;
            let p1: com.vzome.core.construction.Point = null;
            let p2: com.vzome.core.construction.Point = null;
            for(let index=this.mSelection.iterator();index.hasNext();) {
                let man = index.next();
                {
                    this.unselect$com_vzome_core_model_Manifestation(man);
                    if ((man != null && (man.constructor != null && man.constructor["__interfaces"] != null && man.constructor["__interfaces"].indexOf("com.vzome.core.model.Connector") >= 0)) && (p2 == null)){
                        const nextPoint: com.vzome.core.construction.Point = <com.vzome.core.construction.Point>(<com.vzome.core.model.Connector><any>man).getFirstConstruction();
                        if (p0 == null)p0 = nextPoint; else if (p1 == null)p1 = nextPoint; else if (p2 == null)p2 = nextPoint;
                    } else if ((man != null && (man.constructor != null && man.constructor["__interfaces"] != null && man.constructor["__interfaces"].indexOf("com.vzome.core.model.Strut") >= 0)) && (strut == null)){
                        strut = (<com.vzome.core.model.Strut><any>man);
                    } else if ((man != null && (man.constructor != null && man.constructor["__interfaces"] != null && man.constructor["__interfaces"].indexOf("com.vzome.core.model.Panel") >= 0)) && panel == null){
                        panel = (<com.vzome.core.model.Panel><any>man);
                    }
                }
            }
            if (strut == null){
                return;
            }
            let point: com.vzome.core.construction.Point = null;
            let plane: com.vzome.core.construction.Plane = null;
            const line: com.vzome.core.construction.Line = new com.vzome.core.construction.LineFromPointAndVector(strut.getLocation(), strut.getZoneVector());
            if (p2 != null && panel == null){
                const points: com.vzome.core.construction.Point[] = [p0, p1, p2];
                const polygon: com.vzome.core.construction.Polygon = new com.vzome.core.construction.PolygonFromVertices(points);
                plane = new com.vzome.core.construction.PlaneExtensionOfPolygon(polygon);
            } else if (strut != null && panel != null){
                plane = new com.vzome.core.construction.PlaneFromPointAndNormal(panel.getFirstVertex(), panel.getZoneVector());
            }
            if (plane != null && !plane.isImpossible()){
                point = new com.vzome.core.construction.LinePlaneIntersectionPoint(plane, line);
                if (!point.isImpossible())this.select$com_vzome_core_model_Manifestation(this.manifestConstruction(point));
            }
            this.redo();
        }

        /**
         * 
         * @return {string}
         */
        getXmlElementName(): string {
            return "LinePlaneIntersect";
        }
    }
    LinePlaneIntersect["__class"] = "com.vzome.core.edits.LinePlaneIntersect";

}

