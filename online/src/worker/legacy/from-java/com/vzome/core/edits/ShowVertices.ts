import { Polygon } from "../construction/Polygon.js";
import { PolygonVertex } from "../construction/PolygonVertex.js";
import { Segment } from "../construction/Segment.js";
import { SegmentEndPoint } from "../construction/SegmentEndPoint.js";
import { ChangeManifestations } from "../editor/api/ChangeManifestations.js";
import { EditorModel } from "../editor/api/EditorModel.js";
import { Panel } from "../model/Panel.js";

export class ShowVertices extends ChangeManifestations {
    public static NAME: string = "ShowVertices";

    /**
     * 
     */
    public perform() {
        for(let index=this.mSelection.iterator();index.hasNext();) {
            let man = index.next();
            {
                this.unselect$com_vzome_core_model_Manifestation(man);
                if (man != null && (man.constructor != null && man.constructor["__interfaces"] != null && man.constructor["__interfaces"].indexOf("com.vzome.core.model.Strut") >= 0)){
                    const s: Segment = <Segment>man.getFirstConstruction();
                    const start: SegmentEndPoint = new SegmentEndPoint(s, true);
                    this.manifestConstruction(start);
                    const end: SegmentEndPoint = new SegmentEndPoint(s, false);
                    this.select$com_vzome_core_model_Manifestation(this.manifestConstruction(end));
                } else if (man != null && (man.constructor != null && man.constructor["__interfaces"] != null && man.constructor["__interfaces"].indexOf("com.vzome.core.model.Panel") >= 0)){
                    const polygon: Polygon = <Polygon>(<Panel><any>man).getFirstConstruction();
                    for(let i: number = 0; i < polygon.getVertexCount(); i++) {{
                        const v: PolygonVertex = new PolygonVertex(polygon, i);
                        this.select$com_vzome_core_model_Manifestation(this.manifestConstruction(v));
                    };}
                }
            }
        }
        this.redo();
    }

    public constructor(editor: EditorModel) {
        super(editor);
    }

    /**
     * 
     * @return {string}
     */
    getXmlElementName(): string {
        return ShowVertices.NAME;
    }
}
ShowVertices["__class"] = "com.vzome.core.edits.ShowVertices";
