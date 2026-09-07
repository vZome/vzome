import { Command } from "../commands/Command.js";
import { Line } from "../construction/Line.js";
import { LineFromPointAndVector } from "../construction/LineFromPointAndVector.js";
import { LineLineIntersectionPoint } from "../construction/LineLineIntersectionPoint.js";
import { Point } from "../construction/Point.js";
import { ChangeManifestations } from "../editor/api/ChangeManifestations.js";
import { EditorModel } from "../editor/api/EditorModel.js";
import { Manifestation } from "../model/Manifestation.js";
import { Strut } from "../model/Strut.js";

export class StrutIntersection extends ChangeManifestations {
    public constructor(editor: EditorModel) {
        super(editor);
    }

    /**
     * 
     */
    public perform() {
        let s1: Strut = null;
        let s2: Strut = null;
        for(let index=this.mSelection.iterator();index.hasNext();) {
            let man = index.next();
            {
                this.unselect$com_vzome_core_model_Manifestation(man);
                if (man != null && (man.constructor != null && man.constructor["__interfaces"] != null && man.constructor["__interfaces"].indexOf("com.vzome.core.model.Strut") >= 0))if (s1 == null)s1 = <Strut><any>man; else if (s2 == null)s2 = <Strut><any>man; else throw new Command.Failure("only two struts are allowed");
            }
        }
        if (s1 == null || s2 == null)throw new Command.Failure("two struts are required");
        const l1: Line = new LineFromPointAndVector(s1.getLocation(), s1.getZoneVector());
        const l2: Line = new LineFromPointAndVector(s2.getLocation(), s2.getZoneVector());
        const point: Point = new LineLineIntersectionPoint(l1, l2);
        if (point.isImpossible())throw new Command.Failure("lines are parallel or non-intersecting");
        const ball: Manifestation = this.manifestConstruction(point);
        this.select$com_vzome_core_model_Manifestation(ball);
        this.redo();
    }

    /**
     * 
     * @return {string}
     */
    getXmlElementName(): string {
        return "StrutIntersection";
    }
}
StrutIntersection["__class"] = "com.vzome.core.edits.StrutIntersection";
