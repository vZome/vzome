import { Command } from "../commands/Command.js";
import { ChangeOfBasis } from "../construction/ChangeOfBasis.js";
import { Construction } from "../construction/Construction.js";
import { Point } from "../construction/Point.js";
import { Segment } from "../construction/Segment.js";
import { Transformation } from "../construction/Transformation.js";
import { ChangeManifestations } from "../editor/api/ChangeManifestations.js";
import { EditorModel } from "../editor/api/EditorModel.js";
import { ImplicitSymmetryParameters } from "../editor/api/ImplicitSymmetryParameters.js";

export class AffineTransformAll extends ChangeManifestations {
    /*private*/ center: Point;

    public constructor(editorModel: EditorModel) {
        super(editorModel);
        if (this.center === undefined) { this.center = null; }
        this.center = (<ImplicitSymmetryParameters><any>editorModel).getCenterPoint();
    }

    /**
     * 
     */
    public perform() {
        let s1: Segment = null;
        let s2: Segment = null;
        let s3: Segment = null;
        for(let index=this.mSelection.iterator();index.hasNext();) {
            let man = index.next();
            {
                this.unselect$com_vzome_core_model_Manifestation(man);
                if (man != null && (man.constructor != null && man.constructor["__interfaces"] != null && man.constructor["__interfaces"].indexOf("com.vzome.core.model.Strut") >= 0)){
                    if (s1 == null)s1 = <Segment>man.getFirstConstruction(); else if (s2 == null)s2 = <Segment>man.getFirstConstruction(); else if (s3 == null)s3 = <Segment>man.getFirstConstruction();
                }
            }
        }
        if (s3 == null || s2 == null || s1 == null)throw new Command.Failure("three struts required");
        this.redo();
        const transform: Transformation = new ChangeOfBasis(s1, s2, s3, this.center, true);
        for(let index=this.mManifestations.iterator();index.hasNext();) {
            let m = index.next();
            {
                if (!m.isRendered())continue;
                const c: Construction = m.getFirstConstruction();
                const result: Construction = transform.transform$com_vzome_core_construction_Construction(c);
                this.select$com_vzome_core_model_Manifestation(this.manifestConstruction(result));
            }
        }
        this.redo();
    }

    /**
     * 
     * @return {string}
     */
    getXmlElementName(): string {
        return "AffineTransformAll";
    }
}
AffineTransformAll["__class"] = "com.vzome.core.edits.AffineTransformAll";
