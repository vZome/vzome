import { java, javaemul } from "../../../../../candies/j4ts-2.1.0-SNAPSHOT/bundle.js";
import { AlgebraicField } from "../algebra/AlgebraicField.js";
import { AlgebraicNumber } from "../algebra/AlgebraicNumber.js";
import { AlgebraicVector } from "../algebra/AlgebraicVector.js";
import { ConstructionChanges } from "./ConstructionChanges.js";
import { FreePoint } from "./FreePoint.js";
import { Point } from "./Point.js";
import { Polygon } from "./Polygon.js";
import { PolygonFromVertices } from "./PolygonFromVertices.js";
import { Segment } from "./Segment.js";
import { SegmentJoiningPoints } from "./SegmentJoiningPoints.js";
import { Projection } from "../math/Projection.js";
import { VefParser } from "../math/VefParser.js";

export class VefToModel extends VefParser {
    offset: AlgebraicVector;

    scale: AlgebraicNumber;

    __com_vzome_core_construction_VefToModel_field: AlgebraicField;

    mProjection: Projection;

    mVertices: Point[];

    mEffects: ConstructionChanges;

    noBallsSection: boolean;

    static logger: java.util.logging.Logger; public static logger_$LI$(): java.util.logging.Logger { if (VefToModel.logger == null) { VefToModel.logger = java.util.logging.Logger.getLogger("com.vzome.core.construction.VefToModel"); }  return VefToModel.logger; }

    public constructor(projection: Projection, effects: ConstructionChanges, scale: AlgebraicNumber, offset: AlgebraicVector) {
        super();
        if (this.offset === undefined) { this.offset = null; }
        if (this.scale === undefined) { this.scale = null; }
        if (this.__com_vzome_core_construction_VefToModel_field === undefined) { this.__com_vzome_core_construction_VefToModel_field = null; }
        if (this.mProjection === undefined) { this.mProjection = null; }
        if (this.mVertices === undefined) { this.mVertices = null; }
        if (this.mEffects === undefined) { this.mEffects = null; }
        this.noBallsSection = true;
        this.mEffects = effects;
        this.__com_vzome_core_construction_VefToModel_field = scale.getField();
        this.scale = scale;
        this.offset = offset;
        this.mProjection = projection == null ? new Projection.Default(this.__com_vzome_core_construction_VefToModel_field) : projection;
        if (projection != null && VefToModel.logger_$LI$().isLoggable(java.util.logging.Level.FINEST)){
            VefToModel.logger_$LI$().finest("projection = " + projection.getProjectionName());
        }
    }

    /**
     * 
     * @param {number} numVertices
     */
    startVertices(numVertices: number) {
        this.mVertices = (s => { let a=[]; while(s-->0) a.push(null); return a; })(numVertices);
    }

    /**
     * 
     * @param {number} index
     * @param {AlgebraicVector} location
     */
    addVertex(index: number, location: AlgebraicVector) {
        VefToModel.logger_$LI$().finest("addVertex location = " + location.getVectorExpression$int(AlgebraicField.VEF_FORMAT));
        if (this.scale != null){
            location = location.scale(this.scale);
            VefToModel.logger_$LI$().finest("scaled = " + location.getVectorExpression$int(AlgebraicField.VEF_FORMAT));
        }
        if (this.wFirst() && location.dimension() === 3){
            location = location.inflateTo4d$();
        }
        location = this.mProjection.projectImage(location, this.wFirst());
        VefToModel.logger_$LI$().finest("projected = " + location.getVectorExpression$int(AlgebraicField.VEF_FORMAT));
        if (this.offset != null){
            location = location.plus(this.offset);
            VefToModel.logger_$LI$().finest("translated = " + location.getVectorExpression$int(AlgebraicField.VEF_FORMAT));
        }
        this.mVertices[index] = new FreePoint(location);
        this.mVertices[index].setIndex(index);
    }

    /**
     * 
     * @param {number} numEdges
     */
    startEdges(numEdges: number) {
    }

    /**
     * 
     * @param {number} index
     * @param {number} v1
     * @param {number} v2
     */
    addEdge(index: number, v1: number, v2: number) {
        const p1: Point = this.mVertices[v1];
        const p2: Point = this.mVertices[v2];
        if (p1 == null || p2 == null)return;
        const seg: Segment = new SegmentJoiningPoints(p1, p2);
        seg.setIndex(index);
        this.mEffects['constructionAdded$com_vzome_core_construction_Construction'](seg);
    }

    /**
     * 
     * @param {number} numFaces
     */
    startFaces(numFaces: number) {
    }

    /**
     * 
     * @param {number} index
     * @param {int[]} verts
     */
    addFace(index: number, verts: number[]) {
        const points: Point[] = (s => { let a=[]; while(s-->0) a.push(null); return a; })(verts.length);
        for(let i: number = 0; i < verts.length; i++) {points[i] = this.mVertices[verts[i]];}
        const panel: Polygon = new PolygonFromVertices(points);
        panel.setIndex(index);
        this.mEffects['constructionAdded$com_vzome_core_construction_Construction'](panel);
    }

    /**
     * 
     * @param {number} index
     * @param {number} vertex
     */
    addBall(index: number, vertex: number) {
        this.mEffects['constructionAdded$com_vzome_core_construction_Construction'](this.mVertices[vertex]);
    }

    /**
     * 
     * @param {number} numVertices
     */
    startBalls(numVertices: number) {
        this.noBallsSection = false;
    }

    /**
     * 
     * @param {java.util.StringTokenizer} tokens
     */
    endFile(tokens: java.util.StringTokenizer) {
        if (this.noBallsSection){
            for(let index = 0; index < this.mVertices.length; index++) {
                let vertex = this.mVertices[index];
                {
                    this.mEffects['constructionAdded$com_vzome_core_construction_Construction'](vertex);
                }
            }
        }
    }
}
VefToModel["__class"] = "com.vzome.core.construction.VefToModel";
