import { AlgebraicField } from "../algebra/AlgebraicField.js";
import { AlgebraicNumber } from "../algebra/AlgebraicNumber.js";
import { AlgebraicVector } from "../algebra/AlgebraicVector.js";
import { Point } from "../construction/Point.js";
import { Segment } from "../construction/Segment.js";
import { SegmentEndPoint } from "../construction/SegmentEndPoint.js";
import { SegmentJoiningPoints } from "../construction/SegmentJoiningPoints.js";
import { TransformedPoint } from "../construction/TransformedPoint.js";
import { Translation } from "../construction/Translation.js";
import { ChangeManifestations } from "../editor/api/ChangeManifestations.js";
import { EditorModel } from "../editor/api/EditorModel.js";
import { Connector } from "../model/Connector.js";
import { Strut } from "../model/Strut.js";

/**
 * @author David Hall
 * @param {*} editorModel
 * @class
 * @extends ChangeManifestations
 */
export class AffineHeptagon extends ChangeManifestations {
    /**
     * 
     */
    public perform() {
        const errorMsg: string = "Affine heptagon command requires two selected struts with a common vertex.";
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
                    } else {
                        this.fail(errorMsg);
                    }
                }
            }
        }
        if (strut1 == null || strut2 == null){
            this.fail(errorMsg);
        }
        const field: AlgebraicField = strut1.getLocation().getField();
        this.redo();
        const s1: Segment = <Segment>strut1.getFirstConstruction();
        const s2: Segment = <Segment>strut2.getFirstConstruction();
        this.manifestConstruction(new SegmentEndPoint(s1, true));
        this.manifestConstruction(new SegmentEndPoint(s1, false));
        this.manifestConstruction(new SegmentEndPoint(s2, true));
        this.manifestConstruction(new SegmentEndPoint(s2, false));
        this.redo();
        let offset1: AlgebraicVector = s1.getOffset();
        let offset2: AlgebraicVector = s2.getOffset();
        let v0: AlgebraicVector = null;
        let v1: AlgebraicVector = null;
        let v2: AlgebraicVector = null;
        {
            const s1s: AlgebraicVector = s1.getStart();
            const s1e: AlgebraicVector = s1.getEnd();
            const s2s: AlgebraicVector = s2.getStart();
            const s2e: AlgebraicVector = s2.getEnd();
            if (s1s.equals(s2s)){
                v0 = s1s;
                v1 = s1e;
                v2 = s2e;
            } else if (s1e.equals(s2s)){
                v0 = s1e;
                v1 = s1s;
                v2 = s2e;
                offset1 = offset1.negate();
            } else if (s1e.equals(s2e)){
                v0 = s1e;
                v1 = s1s;
                v2 = s2s;
                offset2 = offset2.negate();
                offset1 = offset1.negate();
            } else if (s1s.equals(s2e)){
                v0 = s1s;
                v1 = s1e;
                v2 = s2s;
                offset2 = offset2.negate();
            } else {
                this.fail(errorMsg);
            }
        };
        let c0: Connector = null;
        let c1: Connector = null;
        let c2: Connector = null;
        let p1: Point = null;
        let p2: Point = null;
        for(let index=this.mManifestations.iterator();index.hasNext();) {
            let man = index.next();
            {
                if (man != null && (man.constructor != null && man.constructor["__interfaces"] != null && man.constructor["__interfaces"].indexOf("com.vzome.core.model.Connector") >= 0)){
                    const loc: AlgebraicVector = man.getLocation();
                    if (loc.equals(v0)){
                        c0 = <Connector><any>man;
                    } else if (loc.equals(v1)){
                        c1 = <Connector><any>man;
                        p1 = <Point>man.getFirstConstruction();
                    } else if (loc.equals(v2)){
                        c2 = <Connector><any>man;
                        p2 = <Point>man.getFirstConstruction();
                    }
                }
            }
        }
        const sigma: AlgebraicNumber = field['createAlgebraicNumber$int_A']([0, 0, 1]);
        const rho: AlgebraicNumber = field['createAlgebraicNumber$int_A']([0, 1, 0]);
        const p3: Point = new TransformedPoint(new Translation(offset1.scale(sigma)), p2);
        const p4: Point = new TransformedPoint(new Translation(offset2.scale(sigma)), p1);
        const p5: Point = new TransformedPoint(new Translation(offset1.scale(rho)), p4);
        const p6: Point = new TransformedPoint(new Translation(offset2.scale(rho)), p3);
        this.select$com_vzome_core_model_Manifestation(this.manifestConstruction(new SegmentJoiningPoints(p1, p3)));
        this.select$com_vzome_core_model_Manifestation(this.manifestConstruction(new SegmentJoiningPoints(p3, p5)));
        this.select$com_vzome_core_model_Manifestation(this.manifestConstruction(new SegmentJoiningPoints(p5, p6)));
        this.select$com_vzome_core_model_Manifestation(this.manifestConstruction(new SegmentJoiningPoints(p6, p4)));
        this.select$com_vzome_core_model_Manifestation(this.manifestConstruction(new SegmentJoiningPoints(p4, p2)));
        this.select$com_vzome_core_model_Manifestation(c0);
        this.select$com_vzome_core_model_Manifestation(c1);
        this.select$com_vzome_core_model_Manifestation(this.manifestConstruction(p3));
        this.select$com_vzome_core_model_Manifestation(this.manifestConstruction(p5));
        this.select$com_vzome_core_model_Manifestation(this.manifestConstruction(p6));
        this.select$com_vzome_core_model_Manifestation(this.manifestConstruction(p4));
        this.select$com_vzome_core_model_Manifestation(c2);
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
        return "AffineHeptagon";
    }
}
AffineHeptagon["__class"] = "com.vzome.core.edits.AffineHeptagon";
