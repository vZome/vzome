/* Generated from Java with JSweet 3.2.0-SNAPSHOT - http://www.jsweet.org */
namespace com.vzome.core.math.convexhull {
    /**
     * Maintains a double-linked list of vertices for use by QuickHull3D
     * @class
     */
    export class VertexList {
        /*private*/ head: com.vzome.core.math.convexhull.Vertex;

        /*private*/ tail: com.vzome.core.math.convexhull.Vertex;

        /**
         * Clears this list.
         */
        public clear() {
            this.head = this.tail = null;
        }

        /**
         * Adds a vertex to the end of this list.
         * @param {com.vzome.core.math.convexhull.Vertex} vtx
         */
        public add(vtx: com.vzome.core.math.convexhull.Vertex) {
            if (this.head == null){
                this.head = vtx;
            } else {
                this.tail.next = vtx;
            }
            vtx.prev = this.tail;
            vtx.next = null;
            this.tail = vtx;
        }

        /**
         * Adds a chain of vertices to the end of this list.
         * @param {com.vzome.core.math.convexhull.Vertex} vtx
         */
        public addAll(vtx: com.vzome.core.math.convexhull.Vertex) {
            if (this.head == null){
                this.head = vtx;
            } else {
                this.tail.next = vtx;
            }
            vtx.prev = this.tail;
            while((vtx.next != null)) {{
                vtx = vtx.next;
            }};
            this.tail = vtx;
        }

        public delete$com_vzome_core_math_convexhull_Vertex(vtx: com.vzome.core.math.convexhull.Vertex) {
            if (vtx.prev == null){
                this.head = vtx.next;
            } else {
                vtx.prev.next = vtx.next;
            }
            if (vtx.next == null){
                this.tail = vtx.prev;
            } else {
                vtx.next.prev = vtx.prev;
            }
        }

        public delete$com_vzome_core_math_convexhull_Vertex$com_vzome_core_math_convexhull_Vertex(vtx1: com.vzome.core.math.convexhull.Vertex, vtx2: com.vzome.core.math.convexhull.Vertex) {
            if (vtx1.prev == null){
                this.head = vtx2.next;
            } else {
                vtx1.prev.next = vtx2.next;
            }
            if (vtx2.next == null){
                this.tail = vtx1.prev;
            } else {
                vtx2.next.prev = vtx1.prev;
            }
        }

        /**
         * Deletes a chain of vertices from this list.
         * @param {com.vzome.core.math.convexhull.Vertex} vtx1
         * @param {com.vzome.core.math.convexhull.Vertex} vtx2
         */
        public delete(vtx1?: any, vtx2?: any) {
            if (((vtx1 != null && vtx1 instanceof <any>com.vzome.core.math.convexhull.Vertex) || vtx1 === null) && ((vtx2 != null && vtx2 instanceof <any>com.vzome.core.math.convexhull.Vertex) || vtx2 === null)) {
                return <any>this.delete$com_vzome_core_math_convexhull_Vertex$com_vzome_core_math_convexhull_Vertex(vtx1, vtx2);
            } else if (((vtx1 != null && vtx1 instanceof <any>com.vzome.core.math.convexhull.Vertex) || vtx1 === null) && vtx2 === undefined) {
                return <any>this.delete$com_vzome_core_math_convexhull_Vertex(vtx1);
            } else throw new Error('invalid overload');
        }

        /**
         * Inserts a vertex into this list before another specificed vertex.
         * @param {com.vzome.core.math.convexhull.Vertex} vtx
         * @param {com.vzome.core.math.convexhull.Vertex} next
         */
        public insertBefore(vtx: com.vzome.core.math.convexhull.Vertex, next: com.vzome.core.math.convexhull.Vertex) {
            vtx.prev = next.prev;
            if (next.prev == null){
                this.head = vtx;
            } else {
                next.prev.next = vtx;
            }
            vtx.next = next;
            next.prev = vtx;
        }

        /**
         * Returns the first element in this list.
         * @return {com.vzome.core.math.convexhull.Vertex}
         */
        public first(): com.vzome.core.math.convexhull.Vertex {
            return this.head;
        }

        /**
         * Returns true if this list is empty.
         * @return {boolean}
         */
        public isEmpty(): boolean {
            return this.head == null;
        }

        constructor() {
            if (this.head === undefined) { this.head = null; }
            if (this.tail === undefined) { this.tail = null; }
        }
    }
    VertexList["__class"] = "com.vzome.core.math.convexhull.VertexList";

}

