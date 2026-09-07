import { Line } from "../construction/Line.js";
import { LineFromPointAndVector } from "../construction/LineFromPointAndVector.js";
import { LinePlaneIntersectionPoint } from "../construction/LinePlaneIntersectionPoint.js";
import { Plane } from "../construction/Plane.js";
import { PlaneExtensionOfPolygon } from "../construction/PlaneExtensionOfPolygon.js";
import { PlaneFromPointAndNormal } from "../construction/PlaneFromPointAndNormal.js";
import { Point } from "../construction/Point.js";
import { Polygon } from "../construction/Polygon.js";
import { PolygonFromVertices } from "../construction/PolygonFromVertices.js";
import { ChangeManifestations } from "../editor/api/ChangeManifestations.js";
import { EditorModel } from "../editor/api/EditorModel.js";
import { Connector } from "../model/Connector.js";
import { Panel } from "../model/Panel.js";
import { Strut } from "../model/Strut.js";

export class LinePlaneIntersect extends ChangeManifestations {
    public constructor(editor: EditorModel) {
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
        let panel: Panel = null;
        let strut: Strut = null;
        let p0: Point = null;
        let p1: Point = null;
        let p2: Point = null;
        for(let index=this.mSelection.iterator();index.hasNext();) {
            let man = index.next();
            {
                this.unselect$com_vzome_core_model_Manifestation(man);
                if ((man != null && (man.constructor != null && man.constructor["__interfaces"] != null && man.constructor["__interfaces"].indexOf("com.vzome.core.model.Connector") >= 0)) && (p2 == null)){
                    const nextPoint: Point = <Point>(<Connector><any>man).getFirstConstruction();
                    if (p0 == null)p0 = nextPoint; else if (p1 == null)p1 = nextPoint; else if (p2 == null)p2 = nextPoint;
                } else if ((man != null && (man.constructor != null && man.constructor["__interfaces"] != null && man.constructor["__interfaces"].indexOf("com.vzome.core.model.Strut") >= 0)) && (strut == null)){
                    strut = (<Strut><any>man);
                } else if ((man != null && (man.constructor != null && man.constructor["__interfaces"] != null && man.constructor["__interfaces"].indexOf("com.vzome.core.model.Panel") >= 0)) && panel == null){
                    panel = (<Panel><any>man);
                }
            }
        }
        if (strut == null){
            return;
        }
        let point: Point = null;
        let plane: Plane = null;
        const line: Line = new LineFromPointAndVector(strut.getLocation(), strut.getZoneVector());
        if (p2 != null && panel == null){
            const points: Point[] = [p0, p1, p2];
            const polygon: Polygon = new PolygonFromVertices(points);
            plane = new PlaneExtensionOfPolygon(polygon);
        } else if (strut != null && panel != null){
            plane = new PlaneFromPointAndNormal(panel.getFirstVertex(), panel.getZoneVector());
        }
        if (plane != null && !plane.isImpossible()){
            point = new LinePlaneIntersectionPoint(plane, line);
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
