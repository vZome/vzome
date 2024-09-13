/* Generated from Java with JSweet 3.2.0-SNAPSHOT - http://www.jsweet.org */
namespace com.vzome.core.math.convexhull {
    /**
     * Constructs a vertex with the specified coordinates and index.
     * @param {com.vzome.core.algebra.AlgebraicVector} v
     * @param {number} idx
     * @class
     * @author John E. Lloyd, Fall 2004
     */
    export class Vertex {
        /**
         * Spatial point associated with this vertex.
         */
        pnt: com.vzome.core.algebra.AlgebraicVector;

        /**
         * Back index into an array.
         */
        index: number;

        /**
         * List forward link.
         */
        prev: Vertex;

        /**
         * List backward link.
         */
        next: Vertex;

        /**
         * Current face that this vertex is outside of.
         */
        face: com.vzome.core.math.convexhull.Face;

        public constructor(v: com.vzome.core.algebra.AlgebraicVector, idx: number) {
            if (this.pnt === undefined) { this.pnt = null; }
            if (this.index === undefined) { this.index = 0; }
            if (this.prev === undefined) { this.prev = null; }
            if (this.next === undefined) { this.next = null; }
            if (this.face === undefined) { this.face = null; }
            this.pnt = v;
            this.index = idx;
        }
    }
    Vertex["__class"] = "com.vzome.core.math.convexhull.Vertex";

}

