import { AlgebraicField } from "../algebra/AlgebraicField.js";
import { AlgebraicVector } from "../algebra/AlgebraicVector.js";
import { Polyhedron } from "./Polyhedron.js";
import { VefParser } from "./VefParser.js";

export class VefToPolyhedron extends VefParser {
    polyhedron: Polyhedron;

    public static importPolyhedron(field: AlgebraicField, vef: string): Polyhedron {
        const result: Polyhedron = new Polyhedron(field);
        const parser: VefToPolyhedron = new VefToPolyhedron(result);
        parser.parseVEF(vef, field);
        return result;
    }

    public constructor(polyhedron: Polyhedron) {
        super();
        if (this.polyhedron === undefined) { this.polyhedron = null; }
        this.polyhedron = polyhedron;
    }

    /**
     * 
     * @param {number} index
     * @param {AlgebraicVector} location
     */
    addVertex(index: number, location: AlgebraicVector) {
        this.polyhedron.addVertex(this.getField().projectTo3d(location, true));
    }

    /**
     * 
     * @param {number} index
     * @param {int[]} verts
     */
    addFace(index: number, verts: number[]) {
        const face: Polyhedron.Face = this.polyhedron.newFace();
        for(let index1 = 0; index1 < verts.length; index1++) {
            let i = verts[index1];
            face.add(i)
        }
        this.polyhedron.addFace(face);
    }

    /**
     * 
     * @param {number} numVertices
     */
    startVertices(numVertices: number) {
    }

    /**
     * 
     * @param {number} numFaces
     */
    startFaces(numFaces: number) {
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
    }

    /**
     * 
     * @param {number} numVertices
     */
    startBalls(numVertices: number) {
    }

    /**
     * 
     * @param {number} index
     * @param {number} vertex
     */
    addBall(index: number, vertex: number) {
    }
}
VefToPolyhedron["__class"] = "com.vzome.core.math.VefToPolyhedron";
