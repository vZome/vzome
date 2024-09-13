/* Generated from Java with JSweet 3.2.0-SNAPSHOT - http://www.jsweet.org */
namespace com.vzome.core.edits {
    export class JoinSkewLines extends com.vzome.core.editor.api.ChangeManifestations {
        public static NAME: string = "JoinSkewLines";

        public constructor(editor: com.vzome.core.editor.api.EditorModel) {
            super(editor);
        }

        /**
         * 
         */
        public perform() {
            const errorMsg: java.lang.StringBuilder = new java.lang.StringBuilder();
            errorMsg.append("This command requires two non-parallel struts.\n");
            let s0: com.vzome.core.model.Strut = null;
            let s1: com.vzome.core.model.Strut = null;
            let qty: number = 0;
            for(let index=this.mSelection.iterator();index.hasNext();) {
                let man = index.next();
                {
                    if (man != null && (man.constructor != null && man.constructor["__interfaces"] != null && man.constructor["__interfaces"].indexOf("com.vzome.core.model.Strut") >= 0)){
                        switch((qty)) {
                        case 0:
                            s0 = <com.vzome.core.model.Strut><any>man;
                            break;
                        case 1:
                            s1 = <com.vzome.core.model.Strut><any>man;
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
            const u: com.vzome.core.algebra.AlgebraicVector = s0.getOffset();
            const v: com.vzome.core.algebra.AlgebraicVector = s1.getOffset();
            const p0: com.vzome.core.algebra.AlgebraicVector = s0.getLocation();
            const q0: com.vzome.core.algebra.AlgebraicVector = s1.getLocation();
            const uuA: com.vzome.core.algebra.AlgebraicNumber = u.dot(u);
            const uvB: com.vzome.core.algebra.AlgebraicNumber = u.dot(v);
            const vvC: com.vzome.core.algebra.AlgebraicNumber = v.dot(v);
            const denD: com.vzome.core.algebra.AlgebraicNumber = uuA['times$com_vzome_core_algebra_AlgebraicNumber'](vvC)['minus$com_vzome_core_algebra_AlgebraicNumber'](uvB['times$com_vzome_core_algebra_AlgebraicNumber'](uvB));
            if (denD.isZero()){
                errorMsg.append("\nstruts are parallel.");
                this.fail(errorMsg.toString());
            }
            this.redo();
            const w: com.vzome.core.algebra.AlgebraicVector = p0.minus(q0);
            const uwD: com.vzome.core.algebra.AlgebraicNumber = u.dot(w);
            const vwE: com.vzome.core.algebra.AlgebraicNumber = v.dot(w);
            const sc: com.vzome.core.algebra.AlgebraicNumber = (uvB['times$com_vzome_core_algebra_AlgebraicNumber'](vwE)['minus$com_vzome_core_algebra_AlgebraicNumber'](vvC['times$com_vzome_core_algebra_AlgebraicNumber'](uwD))).dividedBy(denD);
            const tc: com.vzome.core.algebra.AlgebraicNumber = (uuA['times$com_vzome_core_algebra_AlgebraicNumber'](vwE)['minus$com_vzome_core_algebra_AlgebraicNumber'](uvB['times$com_vzome_core_algebra_AlgebraicNumber'](uwD))).dividedBy(denD);
            const w0: com.vzome.core.algebra.AlgebraicVector = p0.plus(u.scale(sc));
            const pw0: com.vzome.core.construction.Point = new com.vzome.core.construction.FreePoint(w0);
            this.select$com_vzome_core_model_Manifestation(this.manifestConstruction(pw0));
            const w1: com.vzome.core.algebra.AlgebraicVector = q0.plus(v.scale(tc));
            if (!w1.equals(w0)){
                const pw1: com.vzome.core.construction.Point = new com.vzome.core.construction.FreePoint(w1);
                this.select$com_vzome_core_model_Manifestation(this.manifestConstruction(pw1));
                this.select$com_vzome_core_model_Manifestation(this.manifestConstruction(new com.vzome.core.construction.SegmentJoiningPoints(pw0, pw1)));
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

}

