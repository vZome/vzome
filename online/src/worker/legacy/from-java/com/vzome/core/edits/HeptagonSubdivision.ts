import { AlgebraicField } from "../algebra/AlgebraicField.js";
import { AlgebraicNumber } from "../algebra/AlgebraicNumber.js";
import { AlgebraicVector } from "../algebra/AlgebraicVector.js";
import { FreePoint } from "../construction/FreePoint.js";
import { Point } from "../construction/Point.js";
import { Segment } from "../construction/Segment.js";
import { SegmentJoiningPoints } from "../construction/SegmentJoiningPoints.js";
import { ChangeManifestations } from "../editor/api/ChangeManifestations.js";
import { EditorModel } from "../editor/api/EditorModel.js";

export class HeptagonSubdivision extends ChangeManifestations {
    /**
     * 
     */
    public perform() {
        let p1: Point = null;
        this.setOrderedSelection(true);
        for(let index=this.mSelection.iterator();index.hasNext();) {
            let man = index.next();
            {
                this.unselect$com_vzome_core_model_Manifestation(man);
                if (man != null && (man.constructor != null && man.constructor["__interfaces"] != null && man.constructor["__interfaces"].indexOf("com.vzome.core.model.Connector") >= 0)){
                    const nextPoint: Point = <Point>man.getFirstConstruction();
                    if (p1 == null)p1 = nextPoint; else {
                        const segment: Segment = new SegmentJoiningPoints(p1, nextPoint);
                        const field: AlgebraicField = segment.getField();
                        const scaleFactor: AlgebraicNumber = field.getUnitTerm(2).reciprocal();
                        const offset: AlgebraicVector = segment.getOffset();
                        const off2: AlgebraicVector = offset.scale(scaleFactor);
                        const off1: AlgebraicVector = off2.scale(scaleFactor);
                        const v1: AlgebraicVector = p1.getLocation().plus(off1);
                        const firstPoint: Point = new FreePoint(v1);
                        this.select$com_vzome_core_model_Manifestation(this.manifestConstruction(firstPoint));
                        const v2: AlgebraicVector = v1.plus(off2);
                        const secondPoint: Point = new FreePoint(v2);
                        this.select$com_vzome_core_model_Manifestation(this.manifestConstruction(secondPoint));
                        break;
                    }
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
        return "HeptagonSubdivision";
    }
}
HeptagonSubdivision["__class"] = "com.vzome.core.edits.HeptagonSubdivision";
