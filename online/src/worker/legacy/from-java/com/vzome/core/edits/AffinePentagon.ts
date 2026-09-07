import { AlgebraicField } from "../algebra/AlgebraicField.js";
import { AlgebraicNumber } from "../algebra/AlgebraicNumber.js";
import { AlgebraicVector } from "../algebra/AlgebraicVector.js";
import { Point } from "../construction/Point.js";
import { Segment } from "../construction/Segment.js";
import { SegmentEndPoint } from "../construction/SegmentEndPoint.js";
import { SegmentJoiningPoints } from "../construction/SegmentJoiningPoints.js";
import { Transformation } from "../construction/Transformation.js";
import { TransformedPoint } from "../construction/TransformedPoint.js";
import { Translation } from "../construction/Translation.js";
import { ChangeManifestations } from "../editor/api/ChangeManifestations.js";
import { EditorModel } from "../editor/api/EditorModel.js";
import { Strut } from "../model/Strut.js";

export class AffinePentagon extends ChangeManifestations {
    /**
     * 
     */
    public perform() {
        const errorMsg: string = "Affine pentagon command requires two selected struts with a common vertex.";
        let strut1: Strut = null;
        let strut2: Strut = null;
        for(let index=this.mSelection.iterator();index.hasNext();) {
            let man = index.next();
            {
                this.unselect$com_vzome_core_model_Manifestation(man);
                if (man != null && (man.constructor != null && man.constructor["__interfaces"] != null && man.constructor["__interfaces"].indexOf("com.vzome.core.model.Strut") >= 0)){
                    if (strut1 == null){
                        strut1 = <Strut><any>man;
                    } else if (strut2 == null){
                        strut2 = <Strut><any>man;
                    }
                }
            }
        }
        this.redo();
        if (strut1 == null || strut2 == null){
            this.fail(errorMsg);
        }
        const field: AlgebraicField = strut1.getLocation().getField();
        const s1: Segment = <Segment>strut1.getFirstConstruction();
        const s2: Segment = <Segment>strut2.getFirstConstruction();
        this.manifestConstruction(new SegmentEndPoint(s1, true));
        this.manifestConstruction(new SegmentEndPoint(s1, false));
        this.manifestConstruction(new SegmentEndPoint(s2, true));
        this.manifestConstruction(new SegmentEndPoint(s2, false));
        this.redo();
        let offset1: AlgebraicVector = s1.getOffset();
        let offset2: AlgebraicVector = s2.getOffset();
        let v1: AlgebraicVector = null;
        let v2: AlgebraicVector = null;
        {
            const s1s: AlgebraicVector = s1.getStart();
            const s1e: AlgebraicVector = s1.getEnd();
            const s2s: AlgebraicVector = s2.getStart();
            const s2e: AlgebraicVector = s2.getEnd();
            if (s1s.equals(s2s)){
                v1 = s1e;
                v2 = s2e;
            } else if (s1e.equals(s2s)){
                v1 = s1s;
                v2 = s2e;
                offset1 = offset1.negate();
            } else if (s1e.equals(s2e)){
                v1 = s1s;
                v2 = s2s;
                offset2 = offset2.negate();
                offset1 = offset1.negate();
            } else if (s1s.equals(s2e)){
                v1 = s1e;
                v2 = s2s;
                offset2 = offset2.negate();
            } else {
                this.fail(errorMsg);
            }
        };
        let p1: Point = null;
        let p2: Point = null;
        for(let index=this.mManifestations.iterator();index.hasNext();) {
            let m = index.next();
            {
                if (m != null && (m.constructor != null && m.constructor["__interfaces"] != null && m.constructor["__interfaces"].indexOf("com.vzome.core.model.Connector") >= 0)){
                    const loc: AlgebraicVector = m.getLocation();
                    if (loc.equals(v1))p1 = <Point>m.getFirstConstruction(); else if (loc.equals(v2))p2 = <Point>m.getFirstConstruction();
                }
            }
        }
        const phi: AlgebraicNumber = field['createPower$int'](1);
        let transform: Transformation = new Translation(offset1.scale(phi));
        const p3: Point = new TransformedPoint(transform, p2);
        this.manifestConstruction(p3);
        transform = new Translation(offset2.scale(phi));
        const p4: Point = new TransformedPoint(transform, p1);
        this.manifestConstruction(p4);
        let segment: Segment = new SegmentJoiningPoints(p1, p3);
        this.select$com_vzome_core_model_Manifestation(this.manifestConstruction(segment));
        segment = new SegmentJoiningPoints(p2, p4);
        this.select$com_vzome_core_model_Manifestation(this.manifestConstruction(segment));
        segment = new SegmentJoiningPoints(p3, p4);
        this.select$com_vzome_core_model_Manifestation(this.manifestConstruction(segment));
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
        return "AffinePentagon";
    }
}
AffinePentagon["__class"] = "com.vzome.core.edits.AffinePentagon";
