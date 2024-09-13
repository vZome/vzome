/* Generated from Java with JSweet 3.2.0-SNAPSHOT - http://www.jsweet.org */
namespace com.vzome.core.math {
    export class VefToPolyhedron extends com.vzome.core.math.VefParser {
        polyhedron: com.vzome.core.math.Polyhedron;

        public static importPolyhedron(field: com.vzome.core.algebra.AlgebraicField, vef: string): com.vzome.core.math.Polyhedron {
            const result: com.vzome.core.math.Polyhedron = new com.vzome.core.math.Polyhedron(field);
            const parser: VefToPolyhedron = new VefToPolyhedron(result);
            parser.parseVEF(vef, field);
            return result;
        }

        public constructor(polyhedron: com.vzome.core.math.Polyhedron) {
            super();
            if (this.polyhedron === undefined) { this.polyhedron = null; }
            this.polyhedron = polyhedron;
        }

        /**
         * 
         * @param {number} index
         * @param {com.vzome.core.algebra.AlgebraicVector} location
         */
        addVertex(index: number, location: com.vzome.core.algebra.AlgebraicVector) {
            this.polyhedron.addVertex(this.getField().projectTo3d(location, true));
        }

        /**
         * 
         * @param {number} index
         * @param {int[]} verts
         */
        addFace(index: number, verts: number[]) {
            const face: com.vzome.core.math.Polyhedron.Face = this.polyhedron.newFace();
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

}

