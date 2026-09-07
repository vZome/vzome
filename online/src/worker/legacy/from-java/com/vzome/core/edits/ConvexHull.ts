import { java, javaemul } from "../../../../../candies/j4ts-2.1.0-SNAPSHOT/bundle.js";
import { AlgebraicVector } from "../algebra/AlgebraicVector.js";
import { ChangeManifestations } from "../editor/api/ChangeManifestations.js";
import { EditorModel } from "../editor/api/EditorModel.js";
import { Panel } from "../model/Panel.js";
import { Strut } from "../model/Strut.js";

export abstract class ConvexHull extends ChangeManifestations {
    public constructor(editorModel: EditorModel) {
        super(editorModel);
    }

    getSelectedVertexSet(unselectAll: boolean): java.util.Set<AlgebraicVector> {
        const vertexSet: java.util.Set<AlgebraicVector> = <any>(new java.util.HashSet<any>());
        for(let index=this.mSelection.iterator();index.hasNext();) {
            let man = index.next();
            {
                if (man != null && (man.constructor != null && man.constructor["__interfaces"] != null && man.constructor["__interfaces"].indexOf("com.vzome.core.model.Connector") >= 0)){
                    vertexSet.add(man.getLocation());
                } else if (man != null && (man.constructor != null && man.constructor["__interfaces"] != null && man.constructor["__interfaces"].indexOf("com.vzome.core.model.Strut") >= 0)){
                    vertexSet.add(man.getLocation());
                    vertexSet.add((<Strut><any>man).getEnd());
                } else if (man != null && (man.constructor != null && man.constructor["__interfaces"] != null && man.constructor["__interfaces"].indexOf("com.vzome.core.model.Panel") >= 0)){
                    for(let index=(<Panel><any>man).iterator();index.hasNext();) {
                        let vertex = index.next();
                        {
                            vertexSet.add(vertex);
                        }
                    }
                }
                if (unselectAll){
                    this.unselect$com_vzome_core_model_Manifestation(man);
                }
            }
        }
        return vertexSet;
    }
}
ConvexHull["__class"] = "com.vzome.core.edits.ConvexHull";
