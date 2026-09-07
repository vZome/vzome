import { java, javaemul } from "../../../../../candies/j4ts-2.1.0-SNAPSHOT/bundle.js";
import { AlgebraicField } from "../algebra/AlgebraicField.js";
import { AlgebraicMatrix } from "../algebra/AlgebraicMatrix.js";
import { AlgebraicNumber } from "../algebra/AlgebraicNumber.js";
import { AlgebraicVector } from "../algebra/AlgebraicVector.js";
import { FreePoint } from "../construction/FreePoint.js";
import { Point } from "../construction/Point.js";
import { Polygon } from "../construction/Polygon.js";
import { PolygonFromVertices } from "../construction/PolygonFromVertices.js";
import { ChangeManifestations } from "../editor/api/ChangeManifestations.js";
import { EditorModel } from "../editor/api/EditorModel.js";
import { Polyhedron } from "../math/Polyhedron.js";
import { HasRenderedObject } from "../model/HasRenderedObject.js";
import { RenderedObject } from "../model/RenderedObject.js";

export class RealizeMetaParts extends ChangeManifestations {
    public static NAME: string = "realizeMetaParts";

    /**
     * 
     */
    public perform() {
        let scale: AlgebraicNumber = null;
        for(let index=this.mSelection.iterator();index.hasNext();) {
            let man = index.next();
            {
                this.unselect$com_vzome_core_model_Manifestation(man);
                const rm: RenderedObject = (<HasRenderedObject><any>man).getRenderedObject();
                if (rm != null){
                    const shape: Polyhedron = rm.getShape();
                    if (scale == null){
                        const field: AlgebraicField = shape.getField();
                        scale = field['createPower$int'](5);
                    }
                    const orientation: AlgebraicMatrix = rm.getOrientation();
                    const vertexList: java.util.List<AlgebraicVector> = shape.getVertexList();
                    for(let index=shape.getVertexList().iterator();index.hasNext();) {
                        let vertex = index.next();
                        {
                            const vertexPt: Point = this.transformVertex(vertex, man.getLocation(), scale, orientation);
                            this.select$com_vzome_core_model_Manifestation(this.manifestConstruction(vertexPt));
                        }
                    }
                    for(let index=shape.getFaceSet().iterator();index.hasNext();) {
                        let face = index.next();
                        {
                            const vertices: Point[] = (s => { let a=[]; while(s-->0) a.push(null); return a; })(face.size());
                            for(let i: number = 0; i < vertices.length; i++) {{
                                const vertexIndex: number = face.getVertex(i);
                                const vertex: AlgebraicVector = vertexList.get(vertexIndex);
                                vertices[i] = this.transformVertex(vertex, man.getLocation(), scale, orientation);
                            };}
                            const polygon: Polygon = new PolygonFromVertices(vertices);
                            this.select$com_vzome_core_model_Manifestation(this.manifestConstruction(polygon));
                        }
                    }
                }
            }
        }
        this.redo();
    }

    /*private*/ transformVertex(vertex: AlgebraicVector, offset: AlgebraicVector, scale: AlgebraicNumber, orientation: AlgebraicMatrix): Point {
        if (orientation != null)vertex = orientation.timesColumn(vertex);
        if (offset != null)vertex = vertex.plus(offset);
        return new FreePoint(vertex.scale(scale));
    }

    public constructor(editor: EditorModel) {
        super(editor);
    }

    /**
     * 
     * @return {string}
     */
    getXmlElementName(): string {
        return RealizeMetaParts.NAME;
    }
}
RealizeMetaParts["__class"] = "com.vzome.core.edits.RealizeMetaParts";
