import { java, javaemul } from "../../../../../candies/j4ts-2.1.0-SNAPSHOT/bundle.js";
import { AlgebraicVector } from "../algebra/AlgebraicVector.js";
import { AlgebraicVectors } from "../algebra/AlgebraicVectors.js";
import { FreePoint } from "../construction/FreePoint.js";
import { Point } from "../construction/Point.js";
import { Polygon } from "../construction/Polygon.js";
import { PolygonFromVertices } from "../construction/PolygonFromVertices.js";
import { PolygonPolygonProjectionToSegment } from "../construction/PolygonPolygonProjectionToSegment.js";
import { Segment } from "../construction/Segment.js";
import { ChangeManifestations } from "../editor/api/ChangeManifestations.js";
import { EditorModel } from "../editor/api/EditorModel.js";
import { Panel } from "../model/Panel.js";

/**
 * @author David Hall
 * @param {*} editor
 * @class
 * @extends ChangeManifestations
 */
export class PanelPanelIntersection extends ChangeManifestations {
    public constructor(editor: EditorModel) {
        super(editor);
    }

    /**
     * 
     */
    public perform() {
        let panel0: Panel = null;
        let panel1: Panel = null;
        let nPanels: number = 0;
        for(let index=this.mSelection.iterator();index.hasNext();) {
            let man = index.next();
            {
                this.unselect$com_vzome_core_model_Manifestation(man);
                if (man != null && (man.constructor != null && man.constructor["__interfaces"] != null && man.constructor["__interfaces"].indexOf("com.vzome.core.model.Panel") >= 0)){
                    switch((nPanels++)) {
                    case 0:
                        panel0 = <Panel><any>man;
                        break;
                    case 1:
                        panel1 = <Panel><any>man;
                        break;
                    default:
                        break;
                    }
                }
            }
        }
        if (nPanels !== 2){
            let msg: string;
            switch((nPanels)) {
            case 0:
                msg = "No panels are selected.";
                break;
            case 1:
                msg = "One panel is selected.";
                break;
            default:
                msg = nPanels + " panels are selected.";
                break;
            }
            this.fail(msg + " Two are required.");
        }
        if (AlgebraicVectors.areParallel(panel0['getNormal$'](), panel1['getNormal$']())){
            const vertices: java.util.List<AlgebraicVector> = <any>(new java.util.ArrayList<any>());
            {
                let array = [panel0, panel1];
                for(let index = 0; index < array.length; index++) {
                    let panel = array[index];
                    {
                        for(let index1=panel.iterator();index1.hasNext();) {
                            let v = index1.next();
                            {
                                vertices.add(v);
                            }
                        }
                    }
                }
            }
            this.fail("Panels are " + (AlgebraicVectors.areCoplanar(vertices) ? "coplanar" : "parallel") + ".");
        }
        this.redo();
        const segment: Segment = new PolygonPolygonProjectionToSegment(PanelPanelIntersection.polygonFromPanel(panel0), PanelPanelIntersection.polygonFromPanel(panel1));
        const start: Point = new FreePoint(segment.getStart());
        const end: Point = new FreePoint(segment.getEnd());
        this.select$com_vzome_core_model_Manifestation(this.manifestConstruction(segment));
        this.select$com_vzome_core_model_Manifestation(this.manifestConstruction(start));
        this.select$com_vzome_core_model_Manifestation(this.manifestConstruction(end));
        this.redo();
    }

    /*private*/ static polygonFromPanel(panel: Panel): Polygon {
        const vertices: java.util.List<Point> = <any>(new java.util.ArrayList<any>(panel.getVertexCount()));
        for(let index=panel.iterator();index.hasNext();) {
            let vector = index.next();
            {
                vertices.add(new FreePoint(vector));
            }
        }
        return new PolygonFromVertices(vertices);
    }

    /**
     * 
     * @return {string}
     */
    getXmlElementName(): string {
        return /* getSimpleName */(c => typeof c === 'string' ? (<any>c).substring((<any>c).lastIndexOf('.')+1) : c["__class"] ? c["__class"].substring(c["__class"].lastIndexOf('.')+1) : c["name"].substring(c["name"].lastIndexOf('.')+1))((<any>this.constructor));
    }
}
PanelPanelIntersection["__class"] = "com.vzome.core.edits.PanelPanelIntersection";
