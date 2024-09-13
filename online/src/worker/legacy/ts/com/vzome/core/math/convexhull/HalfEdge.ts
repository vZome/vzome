/* Generated from Java with JSweet 3.2.0-SNAPSHOT - http://www.jsweet.org */
namespace com.vzome.core.math.convexhull {
    /**
     * Constructs a HalfEdge with head vertex <code>v</code> and left-hand
     * triangular face <code>f</code>.
     * 
     * @param {com.vzome.core.math.convexhull.Vertex} v
     * head vertex
     * @param {com.vzome.core.math.convexhull.Face} f
     * left-hand triangular face
     * @class
     * @author John E. Lloyd, Fall 2004
     */
    export class HalfEdge {
        /**
         * The vertex associated with the head of this half-edge.
         */
        vertex: com.vzome.core.math.convexhull.Vertex;

        /**
         * Triangular face associated with this half-edge.
         */
        face: com.vzome.core.math.convexhull.Face;

        /**
         * Next half-edge in the triangle.
         */
        next: HalfEdge;

        /**
         * Previous half-edge in the triangle.
         */
        prev: HalfEdge;

        /**
         * Half-edge associated with the opposite triangle adjacent to this edge.
         */
        opposite: HalfEdge;

        public constructor(v?: any, f?: any) {
            if (((v != null && v instanceof <any>com.vzome.core.math.convexhull.Vertex) || v === null) && ((f != null && f instanceof <any>com.vzome.core.math.convexhull.Face) || f === null)) {
                let __args = arguments;
                if (this.vertex === undefined) { this.vertex = null; } 
                if (this.face === undefined) { this.face = null; } 
                if (this.next === undefined) { this.next = null; } 
                if (this.prev === undefined) { this.prev = null; } 
                if (this.opposite === undefined) { this.opposite = null; } 
                this.vertex = v;
                this.face = f;
            } else if (v === undefined && f === undefined) {
                let __args = arguments;
                if (this.vertex === undefined) { this.vertex = null; } 
                if (this.face === undefined) { this.face = null; } 
                if (this.next === undefined) { this.next = null; } 
                if (this.prev === undefined) { this.prev = null; } 
                if (this.opposite === undefined) { this.opposite = null; } 
            } else throw new Error('invalid overload');
        }

        /**
         * Sets the value of the next edge adjacent (counter-clockwise) to this one
         * within the triangle.
         * 
         * @param {com.vzome.core.math.convexhull.HalfEdge} edge
         * next adjacent edge
         */
        public setNext(edge: HalfEdge) {
            this.next = edge;
        }

        /**
         * Gets the value of the next edge adjacent (counter-clockwise) to this one
         * within the triangle.
         * 
         * @return {com.vzome.core.math.convexhull.HalfEdge} next adjacent edge
         */
        public getNext(): HalfEdge {
            return this.next;
        }

        /**
         * Sets the value of the previous edge adjacent (clockwise) to this one within
         * the triangle.
         * 
         * @param {com.vzome.core.math.convexhull.HalfEdge} edge
         * previous adjacent edge
         */
        public setPrev(edge: HalfEdge) {
            this.prev = edge;
        }

        /**
         * Gets the value of the previous edge adjacent (clockwise) to this one within
         * the triangle.
         * 
         * @return {com.vzome.core.math.convexhull.HalfEdge} previous adjacent edge
         */
        public getPrev(): HalfEdge {
            return this.prev;
        }

        /**
         * Returns the triangular face located to the left of this half-edge.
         * 
         * @return {com.vzome.core.math.convexhull.Face} left-hand triangular face
         */
        public getFace(): com.vzome.core.math.convexhull.Face {
            return this.face;
        }

        /**
         * Returns the half-edge opposite to this half-edge.
         * 
         * @return {com.vzome.core.math.convexhull.HalfEdge} opposite half-edge
         */
        public getOpposite(): HalfEdge {
            return this.opposite;
        }

        /**
         * Sets the half-edge opposite to this half-edge.
         * 
         * @param {com.vzome.core.math.convexhull.HalfEdge} edge
         * opposite half-edge
         */
        public setOpposite(edge: HalfEdge) {
            this.opposite = edge;
            edge.opposite = this;
        }

        /**
         * Returns the head vertex associated with this half-edge.
         * 
         * @return {com.vzome.core.math.convexhull.Vertex} head vertex
         */
        public head(): com.vzome.core.math.convexhull.Vertex {
            return this.vertex;
        }

        /**
         * Returns the tail vertex associated with this half-edge.
         * 
         * @return {com.vzome.core.math.convexhull.Vertex} tail vertex
         */
        public tail(): com.vzome.core.math.convexhull.Vertex {
            return this.prev != null ? this.prev.vertex : null;
        }

        /**
         * Returns the opposite triangular face associated with this half-edge.
         * 
         * @return {com.vzome.core.math.convexhull.Face} opposite triangular face
         */
        public oppositeFace(): com.vzome.core.math.convexhull.Face {
            return this.opposite == null ? null : this.opposite.face;
        }

        /**
         * Produces a string identifying this half-edge by the point index values of its
         * tail and head vertices.
         * 
         * @return {string} identifying string
         */
        public getVertexString(): string {
            if (this.tail() != null){
                return "" + this.tail().index + "-" + this.head().index;
            } else {
                return "?-" + this.head().index;
            }
        }

        /**
         * Returns the length squared of this half-edge.
         * 
         * @return {*} half-edge length squared
         */
        public lengthSquared(): com.vzome.core.algebra.AlgebraicNumber {
            if (this.tail() == null){
                return this.head().pnt.getField()['createRational$long'](-1);
            }
            const offset: com.vzome.core.algebra.AlgebraicVector = this.head().pnt.minus(this.tail().pnt);
            return offset.dot(offset);
        }
    }
    HalfEdge["__class"] = "com.vzome.core.math.convexhull.HalfEdge";

}

