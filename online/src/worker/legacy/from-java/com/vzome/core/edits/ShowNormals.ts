import { AlgebraicNumber } from "../algebra/AlgebraicNumber.js";
import { AlgebraicVector } from "../algebra/AlgebraicVector.js";
import { FreePoint } from "../construction/FreePoint.js";
import { Point } from "../construction/Point.js";
import { Segment } from "../construction/Segment.js";
import { SegmentJoiningPoints } from "../construction/SegmentJoiningPoints.js";
import { ChangeManifestations } from "../editor/api/ChangeManifestations.js";
import { EditorModel } from "../editor/api/EditorModel.js";
import { Manifestations } from "../editor/api/Manifestations.js";

export class ShowNormals extends ChangeManifestations {
    public static NAME: string = "ShowNormals";

    /**
     * 
     */
    public perform() {
        const SCALE_DOWN: AlgebraicNumber = this.mManifestations.getField()['createAlgebraicNumber$int$int$int$int'](1, 0, 2, -3);
        this.unselectConnectors();
        this.unselectStruts();
        for(let index=Manifestations.getPanels$java_lang_Iterable(this.mSelection).iterator();index.hasNext();) {
            let panel = index.next();
            {
                this.unselect$com_vzome_core_model_Manifestation(panel);
                const centroid: AlgebraicVector = panel.getCentroid();
                const tip: AlgebraicVector = centroid.plus(panel['getNormal$']().scale(SCALE_DOWN));
                const p1: Point = new FreePoint(centroid);
                const p2: Point = new FreePoint(tip);
                const s: Segment = new SegmentJoiningPoints(p1, p2);
                this.select$com_vzome_core_model_Manifestation(this.manifestConstruction(p1));
                this.select$com_vzome_core_model_Manifestation(this.manifestConstruction(p2));
                this.select$com_vzome_core_model_Manifestation(this.manifestConstruction(s));
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
        return ShowNormals.NAME;
    }
}
ShowNormals["__class"] = "com.vzome.core.edits.ShowNormals";
