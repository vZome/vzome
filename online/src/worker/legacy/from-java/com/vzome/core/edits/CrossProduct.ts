import { Command } from "../commands/Command.js";
import { Point } from "../construction/Point.js";
import { Segment } from "../construction/Segment.js";
import { SegmentCrossProduct } from "../construction/SegmentCrossProduct.js";
import { SegmentEndPoint } from "../construction/SegmentEndPoint.js";
import { SegmentJoiningPoints } from "../construction/SegmentJoiningPoints.js";
import { ChangeManifestations } from "../editor/api/ChangeManifestations.js";
import { EditorModel } from "../editor/api/EditorModel.js";
import { Connector } from "../model/Connector.js";

export class CrossProduct extends ChangeManifestations {
    /**
     * 
     */
    public perform() {
        let p1: Point = null;
        let p2: Point = null;
        let s1: Segment = null;
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
                        const nextPoint: Point = <Point>(<Connector><any>man).getFirstConstruction();
                        if (p1 == null){
                            p1 = nextPoint;
                        } else if (s1 == null){
                            p2 = nextPoint;
                            s1 = new SegmentJoiningPoints(p1, nextPoint);
                        } else if (!success){
                            let segment: Segment = new SegmentJoiningPoints(p2, nextPoint);
                            segment = new SegmentCrossProduct(s1, segment);
                            this.select$com_vzome_core_model_Manifestation(this.manifestConstruction(segment));
                            const endpt: Point = new SegmentEndPoint(segment);
                            this.manifestConstruction(endpt);
                            success = true;
                        } else this.recordSelected(man);
                    }
                }
            }
        }
        if (!success)throw new Command.Failure("cross-product requires three selected vertices");
        this.redo();
    }

    public constructor(editorModel: EditorModel) {
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
