import { java, javaemul } from "../../../../../candies/j4ts-2.1.0-SNAPSHOT/bundle.js";
import { Command } from "../commands/Command.js";
import { CentroidPoint } from "../construction/CentroidPoint.js";
import { Construction } from "../construction/Construction.js";
import { Point } from "../construction/Point.js";
import { ChangeManifestations } from "../editor/api/ChangeManifestations.js";
import { EditorModel } from "../editor/api/EditorModel.js";

export class NewCentroid extends ChangeManifestations {
    /**
     * 
     */
    public perform() {
        const verticesList: java.util.List<Point> = <any>(new java.util.ArrayList<any>());
        for(let index=this.mSelection.iterator();index.hasNext();) {
            let man = index.next();
            {
                this.unselect$com_vzome_core_model_Manifestation(man);
                const construction: Construction = man.toConstruction();
                if (construction != null && construction instanceof <any>Point){
                    const nextPoint: Point = <Point>construction;
                    verticesList.add(nextPoint);
                }
            }
        }
        if (verticesList.size() < 2)throw new Command.Failure("Select at least two balls to compute the centroid.");
        const points: Point[] = [];
        const centroid: CentroidPoint = new CentroidPoint(verticesList.toArray<any>(points));
        this.manifestConstruction(centroid);
        this.redo();
    }

    public constructor(editorModel: EditorModel) {
        super(editorModel);
    }

    /**
     * 
     * @return {boolean}
     */
    groupingAware(): boolean {
        return true;
    }

    /**
     * 
     * @return {string}
     */
    getXmlElementName(): string {
        return "NewCentroid";
    }
}
NewCentroid["__class"] = "com.vzome.core.edits.NewCentroid";
