/* Generated from Java with JSweet 3.2.0-SNAPSHOT - http://www.jsweet.org */
namespace com.vzome.core.math.convexhull {
    /**
     * Basic triangular face used to form the hull.
     * 
     * <p>
     * The information stored for each face consists of a planar normal, a planar
     * offset, and a doubly-linked list of three <a href=HalfEdge>HalfEdges</a>
     * which surround the face in a counter-clockwise direction.
     * 
     * @author John E. Lloyd, Fall 2004
     * @class
     */
    export class Face {
        he0: com.vzome.core.math.convexhull.HalfEdge;

        /*private*/ normal: com.vzome.core.algebra.AlgebraicVector;

        area: number;

        /*private*/ centroid: com.vzome.core.algebra.AlgebraicVector;

        planeOffset: com.vzome.core.algebra.AlgebraicNumber;

        index: number;

        numVerts: number;

        next: Face;

        static VISIBLE: number = 1;

        static NON_CONVEX: number = 2;

        static DELETED: number = 3;

        mark: number;

        outside: com.vzome.core.math.convexhull.Vertex;

        public computeCentroid(): com.vzome.core.algebra.AlgebraicVector {
            const vectors: java.util.Set<com.vzome.core.algebra.AlgebraicVector> = <any>(new java.util.HashSet<any>());
            let he: com.vzome.core.math.convexhull.HalfEdge = this.he0;
            do {{
                vectors.add(he.head().pnt);
                he = he.next;
            }} while((he !== this.he0));
            this.centroid = com.vzome.core.algebra.AlgebraicVectors.calculateCentroid(vectors);
            return this.centroid;
        }

        public computeNormal(): com.vzome.core.algebra.AlgebraicVector {
            let he1: com.vzome.core.math.convexhull.HalfEdge = this.he0.next;
            let he2: com.vzome.core.math.convexhull.HalfEdge = he1.next;
            const p0: com.vzome.core.algebra.AlgebraicVector = this.he0.head().pnt;
            let p2: com.vzome.core.algebra.AlgebraicVector = he1.head().pnt;
            let d2: com.vzome.core.algebra.AlgebraicVector = p2.minus(p0);
            this.normal = p0.getField().origin(3);
            this.numVerts = 2;
            while((he2 !== this.he0)) {{
                const d1: com.vzome.core.algebra.AlgebraicVector = d2;
                p2 = he2.head().pnt;
                d2 = p2.minus(p0);
                this.normal = this.normal.plus(d1.cross(d2));
                he1 = he2;
                he2 = he2.next;
                this.numVerts++;
            }};
            return this.normal;
        }

        /*private*/ computeNormalAndCentroid$() {
            this.normal = this.computeNormal();
            this.centroid = this.computeCentroid();
            this.planeOffset = this.normal.dot(this.centroid);
            let numv: number = 0;
            let he: com.vzome.core.math.convexhull.HalfEdge = this.he0;
            do {{
                numv++;
                he = he.next;
            }} while((he !== this.he0));
            if (numv !== this.numVerts){
                this.fail("face " + this.getVertexString() + " numVerts=" + this.numVerts + " should be " + numv);
            }
        }

        public computeNormalAndCentroid$double(minArea: number) {
            this.normal = this.computeNormal();
            this.centroid = this.computeCentroid();
            this.planeOffset = this.normal.dot(this.centroid);
        }

        public computeNormalAndCentroid(minArea?: any) {
            if (((typeof minArea === 'number') || minArea === null)) {
                return <any>this.computeNormalAndCentroid$double(minArea);
            } else if (minArea === undefined) {
                return <any>this.computeNormalAndCentroid$();
            } else throw new Error('invalid overload');
        }

        public static createTriangle$com_vzome_core_math_convexhull_Vertex$com_vzome_core_math_convexhull_Vertex$com_vzome_core_math_convexhull_Vertex(v0: com.vzome.core.math.convexhull.Vertex, v1: com.vzome.core.math.convexhull.Vertex, v2: com.vzome.core.math.convexhull.Vertex): Face {
            return Face.createTriangle$com_vzome_core_math_convexhull_Vertex$com_vzome_core_math_convexhull_Vertex$com_vzome_core_math_convexhull_Vertex$double(v0, v1, v2, 0);
        }

        public static createTriangle$com_vzome_core_math_convexhull_Vertex$com_vzome_core_math_convexhull_Vertex$com_vzome_core_math_convexhull_Vertex$double(v0: com.vzome.core.math.convexhull.Vertex, v1: com.vzome.core.math.convexhull.Vertex, v2: com.vzome.core.math.convexhull.Vertex, minArea: number): Face {
            const face: Face = new Face();
            const he0: com.vzome.core.math.convexhull.HalfEdge = new com.vzome.core.math.convexhull.HalfEdge(v0, face);
            const he1: com.vzome.core.math.convexhull.HalfEdge = new com.vzome.core.math.convexhull.HalfEdge(v1, face);
            const he2: com.vzome.core.math.convexhull.HalfEdge = new com.vzome.core.math.convexhull.HalfEdge(v2, face);
            he0.prev = he2;
            he0.next = he1;
            he1.prev = he0;
            he1.next = he2;
            he2.prev = he1;
            he2.next = he0;
            face.he0 = he0;
            face.computeNormalAndCentroid$double(minArea);
            return face;
        }

        /**
         * Constructs a triangule Face from vertices v0, v1, and v2.
         * 
         * @param {com.vzome.core.math.convexhull.Vertex} v0
         * first vertex
         * @param {com.vzome.core.math.convexhull.Vertex} v1
         * second vertex
         * @param {com.vzome.core.math.convexhull.Vertex} v2
         * third vertex
         * @param {number} minArea
         * @return {com.vzome.core.math.convexhull.Face}
         */
        public static createTriangle(v0?: any, v1?: any, v2?: any, minArea?: any): Face {
            if (((v0 != null && v0 instanceof <any>com.vzome.core.math.convexhull.Vertex) || v0 === null) && ((v1 != null && v1 instanceof <any>com.vzome.core.math.convexhull.Vertex) || v1 === null) && ((v2 != null && v2 instanceof <any>com.vzome.core.math.convexhull.Vertex) || v2 === null) && ((typeof minArea === 'number') || minArea === null)) {
                return <any>com.vzome.core.math.convexhull.Face.createTriangle$com_vzome_core_math_convexhull_Vertex$com_vzome_core_math_convexhull_Vertex$com_vzome_core_math_convexhull_Vertex$double(v0, v1, v2, minArea);
            } else if (((v0 != null && v0 instanceof <any>com.vzome.core.math.convexhull.Vertex) || v0 === null) && ((v1 != null && v1 instanceof <any>com.vzome.core.math.convexhull.Vertex) || v1 === null) && ((v2 != null && v2 instanceof <any>com.vzome.core.math.convexhull.Vertex) || v2 === null) && minArea === undefined) {
                return <any>com.vzome.core.math.convexhull.Face.createTriangle$com_vzome_core_math_convexhull_Vertex$com_vzome_core_math_convexhull_Vertex$com_vzome_core_math_convexhull_Vertex(v0, v1, v2);
            } else throw new Error('invalid overload');
        }

        public static create(vtxArray: com.vzome.core.math.convexhull.Vertex[], indices: number[]): Face {
            const face: Face = new Face();
            let hePrev: com.vzome.core.math.convexhull.HalfEdge = null;
            for(let i: number = 0; i < indices.length; i++) {{
                const he: com.vzome.core.math.convexhull.HalfEdge = new com.vzome.core.math.convexhull.HalfEdge(vtxArray[indices[i]], face);
                if (hePrev != null){
                    he.setPrev(hePrev);
                    hePrev.setNext(he);
                } else {
                    face.he0 = he;
                }
                hePrev = he;
            };}
            face.he0.setPrev(hePrev);
            hePrev.setNext(face.he0);
            face.computeNormalAndCentroid$();
            return face;
        }

        constructor() {
            if (this.he0 === undefined) { this.he0 = null; }
            if (this.normal === undefined) { this.normal = null; }
            if (this.area === undefined) { this.area = 0; }
            if (this.centroid === undefined) { this.centroid = null; }
            if (this.planeOffset === undefined) { this.planeOffset = null; }
            if (this.index === undefined) { this.index = 0; }
            if (this.numVerts === undefined) { this.numVerts = 0; }
            if (this.next === undefined) { this.next = null; }
            this.mark = Face.VISIBLE;
            if (this.outside === undefined) { this.outside = null; }
            this.mark = Face.VISIBLE;
        }

        /**
         * Gets the i-th half-edge associated with the face.
         * 
         * @param {number} i
         * the half-edge index, in the range 0-2.
         * @return {com.vzome.core.math.convexhull.HalfEdge} the half-edge
         */
        public getEdge(i: number): com.vzome.core.math.convexhull.HalfEdge {
            let he: com.vzome.core.math.convexhull.HalfEdge = this.he0;
            while((i > 0)) {{
                he = he.next;
                i--;
            }};
            while((i < 0)) {{
                he = he.prev;
                i++;
            }};
            return he;
        }

        public getFirstEdge(): com.vzome.core.math.convexhull.HalfEdge {
            return this.he0;
        }

        /**
         * Finds the half-edge within this face which has tail <code>vt</code> and head
         * <code>vh</code>.
         * 
         * @param {com.vzome.core.math.convexhull.Vertex} vt
         * tail point
         * @param {com.vzome.core.math.convexhull.Vertex} vh
         * head point
         * @return {com.vzome.core.math.convexhull.HalfEdge} the half-edge, or null if none is found.
         */
        public findEdge(vt: com.vzome.core.math.convexhull.Vertex, vh: com.vzome.core.math.convexhull.Vertex): com.vzome.core.math.convexhull.HalfEdge {
            let he: com.vzome.core.math.convexhull.HalfEdge = this.he0;
            do {{
                if (he.head() === vh && he.tail() === vt){
                    return he;
                }
                he = he.next;
            }} while((he !== this.he0));
            return null;
        }

        /**
         * Computes the distance from a point p to the plane of this face.
         * 
         * @param {com.vzome.core.algebra.AlgebraicVector} p
         * the point
         * @return {*} distance from the point to the plane
         */
        public distanceToPlane(p: com.vzome.core.algebra.AlgebraicVector): com.vzome.core.algebra.AlgebraicNumber {
            return this.normal.dot(p)['minus$com_vzome_core_algebra_AlgebraicNumber'](this.planeOffset);
        }

        /**
         * Returns the normal of the plane associated with this face.
         * 
         * @return {com.vzome.core.algebra.AlgebraicVector} the planar normal
         */
        public getNormal(): com.vzome.core.algebra.AlgebraicVector {
            return this.normal;
        }

        public getCentroid(): com.vzome.core.algebra.AlgebraicVector {
            return this.centroid;
        }

        public numVertices(): number {
            return this.numVerts;
        }

        public getVertexString(): string {
            let s: string = null;
            let he: com.vzome.core.math.convexhull.HalfEdge = this.he0;
            do {{
                if (s == null){
                    s = "" + he.head().index;
                } else {
                    s += " " + he.head().index;
                }
                he = he.next;
            }} while((he !== this.he0));
            return s;
        }

        public getVertexIndices(idxs: number[]) {
            let he: com.vzome.core.math.convexhull.HalfEdge = this.he0;
            let i: number = 0;
            do {{
                idxs[i++] = he.head().index;
                he = he.next;
            }} while((he !== this.he0));
        }

        /*private*/ connectHalfEdges(hedgePrev: com.vzome.core.math.convexhull.HalfEdge, hedge: com.vzome.core.math.convexhull.HalfEdge): Face {
            let discardedFace: Face = null;
            if (hedgePrev.oppositeFace() === hedge.oppositeFace()){
                const oppFace: Face = hedge.oppositeFace();
                let hedgeOpp: com.vzome.core.math.convexhull.HalfEdge;
                if (hedgePrev === this.he0){
                    this.he0 = hedge;
                }
                if (oppFace.numVertices() === 3){
                    hedgeOpp = hedge.getOpposite().prev.getOpposite();
                    oppFace.mark = Face.DELETED;
                    discardedFace = oppFace;
                } else {
                    hedgeOpp = hedge.getOpposite().next;
                    if (oppFace.he0 === hedgeOpp.prev){
                        oppFace.he0 = hedgeOpp;
                    }
                    hedgeOpp.prev = hedgeOpp.prev.prev;
                    hedgeOpp.prev.next = hedgeOpp;
                }
                hedge.prev = hedgePrev.prev;
                hedge.prev.next = hedge;
                hedge.opposite = hedgeOpp;
                hedgeOpp.opposite = hedge;
                oppFace.computeNormalAndCentroid$();
            } else {
                hedgePrev.next = hedge;
                hedge.prev = hedgePrev;
            }
            return discardedFace;
        }

        /*private*/ fail(msg: string) {
            throw new com.vzome.core.commands.Command.Failure(msg);
        }

        checkConsistency() {
            let hedge: com.vzome.core.math.convexhull.HalfEdge = this.he0;
            let maxd: number = 0;
            let numv: number = 0;
            if (this.numVerts < 3){
                this.fail("degenerate face: " + this.getVertexString());
            }
            do {{
                const hedgeOpp: com.vzome.core.math.convexhull.HalfEdge = hedge.getOpposite();
                if (hedgeOpp == null){
                    this.fail("face " + this.getVertexString() + ": unreflected half edge " + hedge.getVertexString());
                } else if (hedgeOpp.getOpposite() !== hedge){
                    this.fail("face " + this.getVertexString() + ": opposite half edge " + hedgeOpp.getVertexString() + " has opposite " + hedgeOpp.getOpposite().getVertexString());
                }
                if (hedgeOpp.head() !== hedge.tail() || hedge.head() !== hedgeOpp.tail()){
                    this.fail("face " + this.getVertexString() + ": half edge " + hedge.getVertexString() + " reflected by " + hedgeOpp.getVertexString());
                }
                const oppFace: Face = hedgeOpp.face;
                if (oppFace == null){
                    this.fail("face " + this.getVertexString() + ": no face on half edge " + hedgeOpp.getVertexString());
                } else if (oppFace.mark === Face.DELETED){
                    this.fail("face " + this.getVertexString() + ": opposite face " + oppFace.getVertexString() + " not on hull");
                }
                const d: number = Math.abs(this.distanceToPlane(hedge.head().pnt).evaluate());
                if (d > maxd){
                    maxd = d;
                }
                numv++;
                hedge = hedge.next;
            }} while((hedge !== this.he0));
            if (numv !== this.numVerts){
                this.fail("face " + this.getVertexString() + " numVerts=" + this.numVerts + " should be " + numv);
            }
        }

        public mergeAdjacentFace(hedgeAdj: com.vzome.core.math.convexhull.HalfEdge, discarded: Face[]): number {
            const oppFace: Face = hedgeAdj.oppositeFace();
            let numDiscarded: number = 0;
            discarded[numDiscarded++] = oppFace;
            oppFace.mark = Face.DELETED;
            const hedgeOpp: com.vzome.core.math.convexhull.HalfEdge = hedgeAdj.getOpposite();
            let hedgeAdjPrev: com.vzome.core.math.convexhull.HalfEdge = hedgeAdj.prev;
            let hedgeAdjNext: com.vzome.core.math.convexhull.HalfEdge = hedgeAdj.next;
            let hedgeOppPrev: com.vzome.core.math.convexhull.HalfEdge = hedgeOpp.prev;
            let hedgeOppNext: com.vzome.core.math.convexhull.HalfEdge = hedgeOpp.next;
            let foo: number = 0;
            while((hedgeAdjPrev.oppositeFace() === oppFace)) {{
                hedgeAdjPrev = hedgeAdjPrev.prev;
                hedgeOppNext = hedgeOppNext.next;
                if (++foo > 1000){
                    this.fail("Oops, it\'s hung.");
                }
            }};
            foo = 0;
            while((hedgeAdjNext.oppositeFace() === oppFace)) {{
                hedgeOppPrev = hedgeOppPrev.prev;
                hedgeAdjNext = hedgeAdjNext.next;
                if (++foo > 1000){
                    this.fail("Oops, it\'s hung.");
                }
            }};
            let hedge: com.vzome.core.math.convexhull.HalfEdge;
            foo = 0;
            for(hedge = hedgeOppNext; hedge !== hedgeOppPrev.next; hedge = hedge.next) {{
                hedge.face = this;
                if (++foo > 1000){
                    this.fail("Oops, it\'s hung.");
                }
            };}
            if (hedgeAdj === this.he0){
                this.he0 = hedgeAdjNext;
            }
            let discardedFace: Face;
            discardedFace = this.connectHalfEdges(hedgeOppPrev, hedgeAdjNext);
            if (discardedFace != null){
                discarded[numDiscarded++] = discardedFace;
            }
            discardedFace = this.connectHalfEdges(hedgeAdjPrev, hedgeOppNext);
            if (discardedFace != null){
                discarded[numDiscarded++] = discardedFace;
            }
            this.computeNormalAndCentroid$();
            this.checkConsistency();
            return numDiscarded;
        }

        public triangulate(newFaces: java.util.List<Face>) {
            const minArea: number = 0;
            let hedge: com.vzome.core.math.convexhull.HalfEdge;
            if (this.numVertices() < 4){
                return;
            }
            const v0: com.vzome.core.math.convexhull.Vertex = this.he0.head();
            hedge = this.he0.next;
            let oppPrev: com.vzome.core.math.convexhull.HalfEdge = hedge.opposite;
            let face0: Face = null;
            for(hedge = hedge.next; hedge !== this.he0.prev; hedge = hedge.next) {{
                const face: Face = Face.createTriangle$com_vzome_core_math_convexhull_Vertex$com_vzome_core_math_convexhull_Vertex$com_vzome_core_math_convexhull_Vertex$double(v0, hedge.prev.head(), hedge.head(), minArea);
                face.he0.next.setOpposite(oppPrev);
                face.he0.prev.setOpposite(hedge.opposite);
                oppPrev = face.he0;
                newFaces.add(face);
                if (face0 == null){
                    face0 = face;
                }
            };}
            hedge = new com.vzome.core.math.convexhull.HalfEdge(this.he0.prev.prev.head(), this);
            hedge.setOpposite(oppPrev);
            hedge.prev = this.he0;
            hedge.prev.next = hedge;
            hedge.next = this.he0.prev;
            hedge.next.prev = hedge;
            this.computeNormalAndCentroid$double(minArea);
            this.checkConsistency();
            for(let face: Face = face0; face != null; face = face.next) {{
                face.checkConsistency();
            };}
        }
    }
    Face["__class"] = "com.vzome.core.math.convexhull.Face";

}

