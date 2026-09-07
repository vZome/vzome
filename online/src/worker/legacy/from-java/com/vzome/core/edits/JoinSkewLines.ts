import { java, javaemul } from "../../../../../candies/j4ts-2.1.0-SNAPSHOT/bundle.js";
import { AlgebraicNumber } from "../algebra/AlgebraicNumber.js";
import { AlgebraicVector } from "../algebra/AlgebraicVector.js";
import { FreePoint } from "../construction/FreePoint.js";
import { Point } from "../construction/Point.js";
import { SegmentJoiningPoints } from "../construction/SegmentJoiningPoints.js";
import { ChangeManifestations } from "../editor/api/ChangeManifestations.js";
import { EditorModel } from "../editor/api/EditorModel.js";
import { Strut } from "../model/Strut.js";

export class JoinSkewLines extends ChangeManifestations {
    public static NAME: string = "JoinSkewLines";

    public constructor(editor: EditorModel) {
        super(editor);
    }

    /**
     * 
     */
    public perform() {
        const errorMsg: java.lang.StringBuilder = new java.lang.StringBuilder();
        errorMsg.append("This command requires two non-parallel struts.\n");
        let s0: Strut = null;
        let s1: Strut = null;
        let qty: number = 0;
        for(let index=this.mSelection.iterator();index.hasNext();) {
            let man = index.next();
            {
                if (man != null && (man.constructor != null && man.constructor["__interfaces"] != null && man.constructor["__interfaces"].indexOf("com.vzome.core.model.Strut") >= 0)){
                    switch((qty)) {
                    case 0:
                        s0 = <Strut><any>man;
                        break;
                    case 1:
                        s1 = <Strut><any>man;
                        break;
                    default:
                        errorMsg.append("\ntoo many struts are selected.");
                        this.fail(errorMsg.toString());
                    }
                    qty++;
                }
                this.unselect$com_vzome_core_model_Manifestation(man);
            }
        }
        if (qty < 2){
            errorMsg.append(qty === 1 ? "\nonly one strut is selected." : "\nno struts are selected.");
            this.fail(errorMsg.toString());
        }
        const u: AlgebraicVector = s0.getOffset();
        const v: AlgebraicVector = s1.getOffset();
        const p0: AlgebraicVector = s0.getLocation();
        const q0: AlgebraicVector = s1.getLocation();
        const uuA: AlgebraicNumber = u.dot(u);
        const uvB: AlgebraicNumber = u.dot(v);
        const vvC: AlgebraicNumber = v.dot(v);
        const denD: AlgebraicNumber = uuA['times$com_vzome_core_algebra_AlgebraicNumber'](vvC)['minus$com_vzome_core_algebra_AlgebraicNumber'](uvB['times$com_vzome_core_algebra_AlgebraicNumber'](uvB));
        if (denD.isZero()){
            errorMsg.append("\nstruts are parallel.");
            this.fail(errorMsg.toString());
        }
        this.redo();
        const w: AlgebraicVector = p0.minus(q0);
        const uwD: AlgebraicNumber = u.dot(w);
        const vwE: AlgebraicNumber = v.dot(w);
        const sc: AlgebraicNumber = (uvB['times$com_vzome_core_algebra_AlgebraicNumber'](vwE)['minus$com_vzome_core_algebra_AlgebraicNumber'](vvC['times$com_vzome_core_algebra_AlgebraicNumber'](uwD))).dividedBy(denD);
        const tc: AlgebraicNumber = (uuA['times$com_vzome_core_algebra_AlgebraicNumber'](vwE)['minus$com_vzome_core_algebra_AlgebraicNumber'](uvB['times$com_vzome_core_algebra_AlgebraicNumber'](uwD))).dividedBy(denD);
        const w0: AlgebraicVector = p0.plus(u.scale(sc));
        const pw0: Point = new FreePoint(w0);
        this.select$com_vzome_core_model_Manifestation(this.manifestConstruction(pw0));
        const w1: AlgebraicVector = q0.plus(v.scale(tc));
        if (!w1.equals(w0)){
            const pw1: Point = new FreePoint(w1);
            this.select$com_vzome_core_model_Manifestation(this.manifestConstruction(pw1));
            this.select$com_vzome_core_model_Manifestation(this.manifestConstruction(new SegmentJoiningPoints(pw0, pw1)));
        }
        this.redo();
    }

    /**
     * 
     * @return {string}
     */
    getXmlElementName(): string {
        return JoinSkewLines.NAME;
    }
}
JoinSkewLines["__class"] = "com.vzome.core.edits.JoinSkewLines";
