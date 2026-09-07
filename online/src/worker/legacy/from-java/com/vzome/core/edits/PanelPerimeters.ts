import { AlgebraicVector } from "../algebra/AlgebraicVector.js";
import { FreePoint } from "../construction/FreePoint.js";
import { Polygon } from "../construction/Polygon.js";
import { SegmentJoiningPoints } from "../construction/SegmentJoiningPoints.js";
import { ChangeManifestations } from "../editor/api/ChangeManifestations.js";
import { EditorModel } from "../editor/api/EditorModel.js";
import { Panel } from "../model/Panel.js";

export class PanelPerimeters extends ChangeManifestations {
    public constructor(editorModel: EditorModel) {
        super(editorModel);
    }

    /**
     * 
     */
    public perform() {
        for(let index=this.mSelection.iterator();index.hasNext();) {
            let man = index.next();
            {
                if (!(man != null && (man.constructor != null && man.constructor["__interfaces"] != null && man.constructor["__interfaces"].indexOf("com.vzome.core.model.Panel") >= 0))){
                    this.unselect$com_vzome_core_model_Manifestation(man);
                }
            }
        }
        this.redo();
        for(let index=this.mSelection.iterator();index.hasNext();) {
            let man = index.next();
            {
                if (man != null && (man.constructor != null && man.constructor["__interfaces"] != null && man.constructor["__interfaces"].indexOf("com.vzome.core.model.Panel") >= 0)){
                    this.unselect$com_vzome_core_model_Manifestation(man);
                    const panel: Panel = <Panel><any>man;
                    const polygon: Polygon = <Polygon>panel.getFirstConstruction();
                    const vertices: AlgebraicVector[] = polygon.getVertices();
                    const first: FreePoint = new FreePoint(vertices[0]);
                    let start: FreePoint = first;
                    for(let i: number = 1; i < vertices.length; i++) {{
                        const end: FreePoint = new FreePoint(vertices[i]);
                        this.select$com_vzome_core_model_Manifestation(this.manifestConstruction(start));
                        this.select$com_vzome_core_model_Manifestation(this.manifestConstruction(new SegmentJoiningPoints(start, end)));
                        start = end;
                    };}
                    this.select$com_vzome_core_model_Manifestation(this.manifestConstruction(start));
                    this.select$com_vzome_core_model_Manifestation(this.manifestConstruction(new SegmentJoiningPoints(start, first)));
                }
            }
        }
        this.redo();
    }

    /**
     * 
     * @return {string}
     */
    getXmlElementName(): string {
        return "PanelPerimeters";
    }
}
PanelPerimeters["__class"] = "com.vzome.core.edits.PanelPerimeters";
