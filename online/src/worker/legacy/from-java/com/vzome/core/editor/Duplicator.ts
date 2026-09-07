import { java, javaemul } from "../../../../../candies/j4ts-2.1.0-SNAPSHOT/bundle.js";
import { AlgebraicVector } from "../algebra/AlgebraicVector.js";
import { Construction } from "../construction/Construction.js";
import { FreePoint } from "../construction/FreePoint.js";
import { Point } from "../construction/Point.js";
import { PolygonFromVertices } from "../construction/PolygonFromVertices.js";
import { SegmentJoiningPoints } from "../construction/SegmentJoiningPoints.js";
import { ChangeManifestations } from "./api/ChangeManifestations.js";
import { Connector } from "../model/Connector.js";
import { Manifestation } from "../model/Manifestation.js";
import { Panel } from "../model/Panel.js";
import { Strut } from "../model/Strut.js";

export class Duplicator {
    /*private*/ vertexData: java.util.Map<AlgebraicVector, Point>;

    /*private*/ edit: ChangeManifestations;

    /*private*/ offset: AlgebraicVector;

    public constructor(edit: ChangeManifestations, offset: AlgebraicVector) {
        this.vertexData = <any>(new java.util.HashMap<any, any>());
        if (this.edit === undefined) { this.edit = null; }
        if (this.offset === undefined) { this.offset = null; }
        this.edit = edit;
        this.offset = offset;
    }

    public duplicateManifestation(man: Manifestation) {
        const constr: Construction = this.duplicateConstruction(man);
        this.edit.manifestConstruction(constr);
    }

    public duplicateConstruction(man: Manifestation): Construction {
        if (man != null && (man.constructor != null && man.constructor["__interfaces"] != null && man.constructor["__interfaces"].indexOf("com.vzome.core.model.Connector") >= 0)){
            const vector: AlgebraicVector = (<Connector><any>man).getLocation();
            return this.getVertex(vector);
        } else if (man != null && (man.constructor != null && man.constructor["__interfaces"] != null && man.constructor["__interfaces"].indexOf("com.vzome.core.model.Strut") >= 0)){
            const strut: Strut = <Strut><any>man;
            const p1: Point = this.getVertex(strut.getLocation());
            const p2: Point = this.getVertex(strut.getEnd());
            return new SegmentJoiningPoints(p1, p2);
        } else if (man != null && (man.constructor != null && man.constructor["__interfaces"] != null && man.constructor["__interfaces"].indexOf("com.vzome.core.model.Panel") >= 0)){
            const vs: java.util.List<Point> = <any>(new java.util.ArrayList<any>());
            for(let index=(<Panel><any>man).iterator();index.hasNext();) {
                let v = index.next();
                {
                    vs.add(this.getVertex(v));
                }
            }
            return new PolygonFromVertices(vs.toArray<any>([]));
        }
        return null;
    }

    getVertex(vertexVector: AlgebraicVector): Point {
        let result: Point = this.vertexData.get(vertexVector);
        if (result == null){
            const key: AlgebraicVector = vertexVector;
            if (this.offset != null)vertexVector = vertexVector.plus(this.offset);
            result = new FreePoint(vertexVector);
            this.vertexData.put(key, result);
        }
        return result;
    }
}
Duplicator["__class"] = "com.vzome.core.editor.Duplicator";
