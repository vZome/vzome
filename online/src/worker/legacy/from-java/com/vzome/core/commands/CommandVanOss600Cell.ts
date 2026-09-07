import { java, javaemul } from "../../../../../candies/j4ts-2.1.0-SNAPSHOT/bundle.js";
import { AlgebraicField } from "../algebra/AlgebraicField.js";
import { AlgebraicNumber } from "../algebra/AlgebraicNumber.js";
import { AlgebraicVector } from "../algebra/AlgebraicVector.js";
import { Quaternion } from "../algebra/Quaternion.js";
import { AttributeMap } from "./AttributeMap.js";
import { Command } from "./Command.js";
import { CommandImportVEFData } from "./CommandImportVEFData.js";
import { ConstructionChanges } from "../construction/ConstructionChanges.js";
import { ConstructionList } from "../construction/ConstructionList.js";
import { FreePoint } from "../construction/FreePoint.js";
import { Point } from "../construction/Point.js";
import { Segment } from "../construction/Segment.js";
import { SegmentJoiningPoints } from "../construction/SegmentJoiningPoints.js";
import { QuaternionProjection } from "../math/QuaternionProjection.js";
import { VefParser } from "../math/VefParser.js";

export class CommandVanOss600Cell extends CommandImportVEFData {
    /**
     * 
     * @param {ConstructionList} parameters
     * @param {AttributeMap} attributes
     * @param {*} effects
     * @return {ConstructionList}
     */
    public apply(parameters: ConstructionList, attributes: AttributeMap, effects: ConstructionChanges): ConstructionList {
        try {
            const input: java.io.InputStream = (<any>this.constructor).getClassLoader().getResourceAsStream("com/vzome/core/commands/600cell.vef");
            const out: java.io.ByteArrayOutputStream = new java.io.ByteArrayOutputStream();
            const buf: number[] = (s => { let a=[]; while(s-->0) a.push(0); return a; })(1024);
            let num: number;
            while(((num = input.read(buf, 0, 1024)) > 0)) {out.write(buf, 0, num)};
            const vefData: string = <string>new String(out.toByteArray());
            const result: ConstructionList = new ConstructionList();
            let field: AlgebraicField = <AlgebraicField><any>attributes.get(CommandImportVEFData.FIELD_ATTR_NAME);
            if (field == null)field = <AlgebraicField><any>attributes.get(Command.FIELD_ATTR_NAME);
            new CommandVanOss600Cell.VefToModel(this, null, effects).parseVEF(vefData, field);
            return result;
        } catch(exc) {
            throw new Command.Failure(exc);
        }
    }

    constructor() {
        super();
    }
}
CommandVanOss600Cell["__class"] = "com.vzome.core.commands.CommandVanOss600Cell";
CommandVanOss600Cell["__interfaces"] = ["com.vzome.core.commands.Command"];



export namespace CommandVanOss600Cell {

    export class VefToModel extends VefParser {
        public __parent: any;
        mProjection: QuaternionProjection;

        mVertices: Point[];

        mEffects: ConstructionChanges;

        mLocations: AlgebraicVector[];

        public constructor(__parent: any, quaternion: Quaternion, effects: ConstructionChanges) {
            super();
            this.__parent = __parent;
            if (this.mProjection === undefined) { this.mProjection = null; }
            if (this.mVertices === undefined) { this.mVertices = null; }
            if (this.mEffects === undefined) { this.mEffects = null; }
            if (this.mLocations === undefined) { this.mLocations = null; }
            this.mEffects = effects;
        }

        /**
         * 
         * @param {number} numVertices
         */
        startVertices(numVertices: number) {
            this.mVertices = (s => { let a=[]; while(s-->0) a.push(null); return a; })(numVertices);
            this.mLocations = (s => { let a=[]; while(s-->0) a.push(null); return a; })(numVertices);
            this.mProjection = null;
        }

        /**
         * 
         * @param {number} index
         * @param {AlgebraicVector} location
         */
        addVertex(index: number, location: AlgebraicVector) {
            this.mLocations[index] = location;
        }

        /**
         * 
         */
        endVertices() {
            const field: AlgebraicField = this.getField();
            const half: AlgebraicNumber = field['createRational$long$long'](1, 2);
            const quarter: AlgebraicNumber = field['createRational$long$long'](1, 4);
            const centroid: AlgebraicVector = this.mLocations[0].plus(this.mLocations[48]).plus(this.mLocations[50]).plus(this.mLocations[64]).scale(quarter);
            const edgeCenter: AlgebraicVector = this.mLocations[0].plus(this.mLocations[48]).scale(half);
            const vertex: AlgebraicVector = this.mLocations[50];
            const edgeToVertex: AlgebraicVector = vertex.minus(edgeCenter);
            const edgeToCenter: AlgebraicVector = centroid.minus(edgeCenter);
            const symmCenter1: AlgebraicVector = edgeCenter.plus(edgeToCenter.scale(field['createAlgebraicNumber$int$int$int$int'](0, 3, 5, 0)));
            const symmCenter2: AlgebraicVector = edgeCenter.plus(edgeToVertex.scale(field['createAlgebraicNumber$int$int$int$int'](0, 2, 5, 0)));
            const direction: AlgebraicVector = symmCenter2.minus(symmCenter1);
            const target: AlgebraicVector = symmCenter1.plus(direction.scale(field['createAlgebraicNumber$int$int$int$int'](0, 1, 1, 0)));
            this.mProjection = new QuaternionProjection(field, null, target);
            const power5: AlgebraicNumber = field['createPower$int'](5);
            for(let i: number = 0; i < this.mLocations.length; i++) {{
                let location: AlgebraicVector = this.mLocations[i].scale(power5);
                location = this.mProjection.projectImage(location, this.wFirst());
                this.mVertices[i] = new FreePoint(location);
                this.mEffects['constructionAdded$com_vzome_core_construction_Construction'](this.mVertices[i]);
            };}
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
            if (p1 == null || p2 == null){
                console.info("skipping " + v1 + " " + v2);
                return;
            }
            const seg: Segment = new SegmentJoiningPoints(p1, p2);
            this.mEffects['constructionAdded$com_vzome_core_construction_Construction'](seg);
        }

        /**
         * 
         * @param {number} numEdges
         */
        startEdges(numEdges: number) {
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
        }

        /**
         * 
         * @param {number} index
         * @param {number} vertex
         */
        addBall(index: number, vertex: number) {
        }

        /**
         * 
         * @param {number} numVertices
         */
        startBalls(numVertices: number) {
        }
    }
    VefToModel["__class"] = "com.vzome.core.commands.CommandVanOss600Cell.VefToModel";

}
