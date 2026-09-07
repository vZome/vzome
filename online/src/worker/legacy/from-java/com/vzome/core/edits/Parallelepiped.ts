import { AlgebraicVector } from "../algebra/AlgebraicVector.js";
import { FreePoint } from "../construction/FreePoint.js";
import { Point } from "../construction/Point.js";
import { PolygonFromVertices } from "../construction/PolygonFromVertices.js";
import { Segment } from "../construction/Segment.js";
import { SegmentJoiningPoints } from "../construction/SegmentJoiningPoints.js";
import { ChangeManifestations } from "../editor/api/ChangeManifestations.js";
import { EditorModel } from "../editor/api/EditorModel.js";
import { Strut } from "../model/Strut.js";

export class Parallelepiped extends ChangeManifestations {
    public constructor(editor: EditorModel) {
        super(editor);
    }

    /**
     * 
     * @return {string}
     */
    getXmlElementName(): string {
        return "Parallelepiped";
    }

    /**
     * 
     */
    public perform() {
        const errorMsg: string = "Parallelepiped command requires three selected struts with a common vertex.";
        let strut1: Strut = null;
        let strut2: Strut = null;
        let strut3: Strut = null;
        for(let index=this.mSelection.iterator();index.hasNext();) {
            let man = index.next();
            {
                this.unselect$com_vzome_core_model_Manifestation(man);
                if (man != null && (man.constructor != null && man.constructor["__interfaces"] != null && man.constructor["__interfaces"].indexOf("com.vzome.core.model.Strut") >= 0)){
                    if (strut1 == null){
                        strut1 = <Strut><any>(man);
                    } else if (strut2 == null){
                        strut2 = <Strut><any>(man);
                    } else if (strut3 == null){
                        strut3 = <Strut><any>(man);
                    } else this.fail(errorMsg);
                } else this.fail(errorMsg);
            }
        }
        if (strut1 == null || strut2 == null || strut3 == null){
            this.fail(errorMsg);
        }
        const s1: Segment = <Segment>(strut1.getFirstConstruction());
        const s2: Segment = <Segment>(strut2.getFirstConstruction());
        const s3: Segment = <Segment>(strut3.getFirstConstruction());
        let offset1: AlgebraicVector = s1.getOffset();
        let offset2: AlgebraicVector = s2.getOffset();
        let offset3: AlgebraicVector = s3.getOffset();
        let v0: AlgebraicVector = null;
        let v1: AlgebraicVector = null;
        let v2: AlgebraicVector = null;
        let v3: AlgebraicVector = null;
        {
            const s1s: AlgebraicVector = s1.getStart();
            const s1e: AlgebraicVector = s1.getEnd();
            const s2s: AlgebraicVector = s2.getStart();
            const s2e: AlgebraicVector = s2.getEnd();
            if (s1s.equals(s2s)){
                v1 = s1e;
                v2 = s2e;
                v0 = s2s;
            } else if (s1e.equals(s2s)){
                v1 = s1s;
                v2 = s2e;
                v0 = s2s;
                offset1 = offset1.negate();
            } else if (s1e.equals(s2e)){
                v1 = s1s;
                v2 = s2s;
                v0 = s2e;
                offset2 = offset2.negate();
                offset1 = offset1.negate();
            } else if (s1s.equals(s2e)){
                v1 = s1e;
                v2 = s2s;
                v0 = s2e;
                offset2 = offset2.negate();
            } else {
                this.fail(errorMsg);
            }
            const s3s: AlgebraicVector = s3.getStart();
            const s3e: AlgebraicVector = s3.getEnd();
            if (s3s.equals(v0)){
                v3 = s3e;
            } else if (s3e.equals(v0)){
                v3 = s3s;
                offset3 = offset3.negate();
            } else {
                this.fail(errorMsg);
            }
        };
        this.redo();
        const p0: Point = new FreePoint(v0);
        const p1: Point = new FreePoint(v1);
        const p2: Point = new FreePoint(v2);
        const p3: Point = new FreePoint(v3);
        this.manifestConstruction(p0);
        this.manifestConstruction(p1);
        this.manifestConstruction(p2);
        this.manifestConstruction(p3);
        this.redo();
        const v4: AlgebraicVector = v2.plus(offset3);
        const p4: Point = new FreePoint(v4);
        this.select$com_vzome_core_model_Manifestation(this.manifestConstruction(p4));
        this.select$com_vzome_core_model_Manifestation(this.manifestConstruction(new SegmentJoiningPoints(p2, p4)));
        this.select$com_vzome_core_model_Manifestation(this.manifestConstruction(new SegmentJoiningPoints(p3, p4)));
        const v5: AlgebraicVector = v3.plus(offset1);
        const p5: Point = new FreePoint(v5);
        this.select$com_vzome_core_model_Manifestation(this.manifestConstruction(p5));
        this.select$com_vzome_core_model_Manifestation(this.manifestConstruction(new SegmentJoiningPoints(p1, p5)));
        this.select$com_vzome_core_model_Manifestation(this.manifestConstruction(new SegmentJoiningPoints(p3, p5)));
        const v6: AlgebraicVector = v1.plus(offset2);
        const p6: Point = new FreePoint(v6);
        this.select$com_vzome_core_model_Manifestation(this.manifestConstruction(p6));
        this.select$com_vzome_core_model_Manifestation(this.manifestConstruction(new SegmentJoiningPoints(p1, p6)));
        this.select$com_vzome_core_model_Manifestation(this.manifestConstruction(new SegmentJoiningPoints(p2, p6)));
        const v7: AlgebraicVector = v4.plus(offset1);
        const p7: Point = new FreePoint(v7);
        this.select$com_vzome_core_model_Manifestation(this.manifestConstruction(p7));
        this.select$com_vzome_core_model_Manifestation(this.manifestConstruction(new SegmentJoiningPoints(p4, p7)));
        this.select$com_vzome_core_model_Manifestation(this.manifestConstruction(new SegmentJoiningPoints(p5, p7)));
        this.select$com_vzome_core_model_Manifestation(this.manifestConstruction(new SegmentJoiningPoints(p6, p7)));
        this.select$com_vzome_core_model_Manifestation(this.manifestConstruction(new PolygonFromVertices([p0, p3, p4, p2])));
        this.select$com_vzome_core_model_Manifestation(this.manifestConstruction(new PolygonFromVertices([p0, p1, p5, p3])));
        this.select$com_vzome_core_model_Manifestation(this.manifestConstruction(new PolygonFromVertices([p0, p2, p6, p1])));
        this.select$com_vzome_core_model_Manifestation(this.manifestConstruction(new PolygonFromVertices([p7, p4, p3, p5])));
        this.select$com_vzome_core_model_Manifestation(this.manifestConstruction(new PolygonFromVertices([p7, p6, p2, p4])));
        this.select$com_vzome_core_model_Manifestation(this.manifestConstruction(new PolygonFromVertices([p7, p5, p1, p6])));
        this.redo();
    }
}
Parallelepiped["__class"] = "com.vzome.core.edits.Parallelepiped";
