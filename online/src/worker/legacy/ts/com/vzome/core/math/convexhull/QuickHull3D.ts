/* Generated from Java with JSweet 3.2.0-SNAPSHOT - http://www.jsweet.org */
namespace com.vzome.core.math.convexhull {
    /**
     * Creates an empty convex hull object.
     * @class
     * @author John E. Lloyd, Fall 2004
     */
    export class QuickHull3D {
        /**
         * Specifies that (on output) vertex indices for a face should be listed in
         * clockwise order.
         */
        public static CLOCKWISE: number = 1;

        /**
         * Specifies that (on output) the vertex indices for a face should be numbered
         * starting from 1.
         */
        public static INDEXED_FROM_ONE: number = 2;

        /**
         * Specifies that (on output) the vertex indices for a face should be numbered
         * starting from 0.
         */
        public static INDEXED_FROM_ZERO: number = 4;

        /**
         * Specifies that (on output) the vertex indices for a face should be numbered
         * with respect to the original input points.
         */
        public static POINT_RELATIVE: number = 8;

        findIndex: number;

        debug: boolean;

        pointBuffer: com.vzome.core.math.convexhull.Vertex[];

        vertexPointIndices: number[];

        /*private*/ discardedFaces: com.vzome.core.math.convexhull.Face[];

        /*private*/ maxVtxs: com.vzome.core.math.convexhull.Vertex[];

        /*private*/ minVtxs: com.vzome.core.math.convexhull.Vertex[];

        faces: java.util.Vector<com.vzome.core.math.convexhull.Face>;

        /*private*/ newFaces: java.util.List<com.vzome.core.math.convexhull.Face>;

        /*private*/ unclaimed: com.vzome.core.math.convexhull.VertexList;

        /*private*/ claimed: com.vzome.core.math.convexhull.VertexList;

        numVertices: number;

        numFaces: number;

        numPoints: number;

        /**
         * Returns true if debugging is enabled.
         * 
         * @return {boolean} true is debugging is enabled
         * @see QuickHull3D#setDebug
         */
        public getDebug(): boolean {
            return this.debug;
        }

        /**
         * Enables the printing of debugging diagnostics.
         * 
         * @param {boolean} enable
         * if true, enables debugging
         */
        public setDebug(enable: boolean) {
            this.debug = enable;
        }

        /*private*/ addPointToFace(vtx: com.vzome.core.math.convexhull.Vertex, face: com.vzome.core.math.convexhull.Face) {
            vtx.face = face;
            if (face.outside == null){
                this.claimed.add(vtx);
            } else {
                this.claimed.insertBefore(vtx, face.outside);
            }
            face.outside = vtx;
        }

        /*private*/ removePointFromFace(vtx: com.vzome.core.math.convexhull.Vertex, face: com.vzome.core.math.convexhull.Face) {
            if (vtx === face.outside){
                if (vtx.next != null && vtx.next.face === face){
                    face.outside = vtx.next;
                } else {
                    face.outside = null;
                }
            }
            this.claimed.delete$com_vzome_core_math_convexhull_Vertex(vtx);
        }

        /*private*/ removeAllPointsFromFace(face: com.vzome.core.math.convexhull.Face): com.vzome.core.math.convexhull.Vertex {
            if (face.outside != null){
                let end: com.vzome.core.math.convexhull.Vertex = face.outside;
                while((end.next != null && end.next.face === face)) {{
                    end = end.next;
                }};
                this.claimed.delete$com_vzome_core_math_convexhull_Vertex$com_vzome_core_math_convexhull_Vertex(face.outside, end);
                end.next = null;
                return face.outside;
            } else {
                return null;
            }
        }

        public constructor() {
            this.findIndex = -1;
            this.debug = false;
            this.pointBuffer = [];
            this.vertexPointIndices = [];
            this.discardedFaces = [null, null, null];
            this.maxVtxs = [null, null, null];
            this.minVtxs = [null, null, null];
            this.faces = <any>(new java.util.Vector<any>(16));
            this.newFaces = <any>(new java.util.LinkedList<any>());
            this.unclaimed = new com.vzome.core.math.convexhull.VertexList();
            this.claimed = new com.vzome.core.math.convexhull.VertexList();
            if (this.numVertices === undefined) { this.numVertices = 0; }
            if (this.numFaces === undefined) { this.numFaces = 0; }
            if (this.numPoints === undefined) { this.numPoints = 0; }
        }

        /*private*/ findHalfEdge(tail: com.vzome.core.math.convexhull.Vertex, head: com.vzome.core.math.convexhull.Vertex): com.vzome.core.math.convexhull.HalfEdge {
            for(let index=this.faces.iterator();index.hasNext();) {
                let face = index.next();
                {
                    const he: com.vzome.core.math.convexhull.HalfEdge = face.findEdge(tail, head);
                    if (he != null){
                        return he;
                    }
                }
            }
            return null;
        }

        setHull(coords: com.vzome.core.algebra.AlgebraicVector[], nump: number, faceIndices: number[][], numf: number) {
            this.initBuffers(nump);
            this.setPoints(coords, nump);
            this.computeMaxAndMin();
            for(let i: number = 0; i < numf; i++) {{
                const face: com.vzome.core.math.convexhull.Face = com.vzome.core.math.convexhull.Face.create(this.pointBuffer, faceIndices[i]);
                let he: com.vzome.core.math.convexhull.HalfEdge = face.he0;
                do {{
                    const heOpp: com.vzome.core.math.convexhull.HalfEdge = this.findHalfEdge(he.head(), he.tail());
                    if (heOpp != null){
                        he.setOpposite(heOpp);
                    }
                    he = he.next;
                }} while((he !== face.he0));
                this.faces.add(face);
            };}
        }

        public build$java_util_Collection(points: java.util.Collection<com.vzome.core.algebra.AlgebraicVector>) {
            this.build$com_vzome_core_algebra_AlgebraicVector_A(points.toArray<any>((s => { let a=[]; while(s-->0) a.push(null); return a; })(points.size())));
        }

        public build$com_vzome_core_algebra_AlgebraicVector_A(points: com.vzome.core.algebra.AlgebraicVector[]) {
            this.build$com_vzome_core_algebra_AlgebraicVector_A$int(points, points.length);
        }

        public build$com_vzome_core_algebra_AlgebraicVector_A$int(points: com.vzome.core.algebra.AlgebraicVector[], nump: number) {
            if (nump < 4){
                throw new com.vzome.core.commands.Command.Failure("At least four input points are required for a 3d convex hull.\n\n" + nump + " specified.");
            }
            if (points.length < nump){
                throw new com.vzome.core.commands.Command.Failure("Point array too small for specified number of points");
            }
            this.printPointSet(points, nump);
            this.initBuffers(nump);
            this.setPoints(points, nump);
            this.buildHull();
        }

        /**
         * Constructs the convex hull of an array of points.
         * 
         * @param {com.vzome.core.algebra.AlgebraicVector[]} points
         * input points
         * @param {number} nump
         * number of input points
         * @throws Failure
         * if the number of input points is less than four or greater than the
         * length of <code>points</code>, or the points are
         * coincident, collinear, or coplanar.
         */
        public build(points?: any, nump?: any) {
            if (((points != null && points instanceof <any>Array && (points.length == 0 || points[0] == null ||(points[0] != null && points[0] instanceof <any>com.vzome.core.algebra.AlgebraicVector))) || points === null) && ((typeof nump === 'number') || nump === null)) {
                return <any>this.build$com_vzome_core_algebra_AlgebraicVector_A$int(points, nump);
            } else if (((points != null && (points.constructor != null && points.constructor["__interfaces"] != null && points.constructor["__interfaces"].indexOf("java.util.Collection") >= 0)) || points === null) && nump === undefined) {
                return <any>this.build$java_util_Collection(points);
            } else if (((points != null && points instanceof <any>Array && (points.length == 0 || points[0] == null ||(points[0] != null && points[0] instanceof <any>com.vzome.core.algebra.AlgebraicVector))) || points === null) && nump === undefined) {
                return <any>this.build$com_vzome_core_algebra_AlgebraicVector_A(points);
            } else throw new Error('invalid overload');
        }

        /**
         * prints the initial set of points.
         * 
         * @param {com.vzome.core.algebra.AlgebraicVector[]} points
         * input points
         * @param {number} nump
         * number of input points
         */
        public printPointSet(points: com.vzome.core.algebra.AlgebraicVector[], nump: number) {
            if (this.debug){
                console.info("initial point set:");
                for(let i: number = 0; i < nump; i++) {{
                    console.info(i + ": " + points[i]);
                };}
            }
        }

        /**
         * Triangulates any non-triangular hull faces. In some cases, due to precision
         * issues, the resulting triangles may be very thin or small, and hence appear
         * to be non-convex (this same limitation is present in <a href=http://www.qhull.org>qhull</a>).
         * @throws Failure
         */
        public triangulate() {
            this.newFaces.clear();
            for(let index=this.faces.iterator();index.hasNext();) {
                let face = index.next();
                {
                    if (face.mark === com.vzome.core.math.convexhull.Face.VISIBLE){
                        face.triangulate(this.newFaces);
                    }
                }
            }
            for(let index=this.newFaces.iterator();index.hasNext();) {
                let face = index.next();
                {
                    this.faces.add(face);
                }
            }
        }

        initBuffers(nump: number) {
            if (this.pointBuffer.length < nump){
                const newBuffer: com.vzome.core.math.convexhull.Vertex[] = (s => { let a=[]; while(s-->0) a.push(null); return a; })(nump);
                this.vertexPointIndices = (s => { let a=[]; while(s-->0) a.push(0); return a; })(nump);
                for(let i: number = 0; i < this.pointBuffer.length; i++) {{
                    newBuffer[i] = this.pointBuffer[i];
                };}
                for(let i: number = this.pointBuffer.length; i < nump; i++) {{
                    newBuffer[i] = null;
                };}
                this.pointBuffer = newBuffer;
            }
            this.faces.clear();
            this.claimed.clear();
            this.numFaces = 0;
            this.numPoints = nump;
        }

        setPoints(pnts: com.vzome.core.algebra.AlgebraicVector[], nump: number) {
            this.pointBuffer = (s => { let a=[]; while(s-->0) a.push(null); return a; })(nump);
            for(let i: number = 0; i < nump; i++) {{
                this.pointBuffer[i] = new com.vzome.core.math.convexhull.Vertex(pnts[i], i);
            };}
        }

        computeMaxAndMin() {
            for(let i: number = 0; i < 3; i++) {{
                this.maxVtxs[i] = this.minVtxs[i] = this.pointBuffer[0];
            };}
            const max: com.vzome.core.algebra.AlgebraicVector = this.pointBuffer[0].pnt;
            let maxx: number = max.getComponent(com.vzome.core.algebra.AlgebraicVector.X).evaluate();
            let maxy: number = max.getComponent(com.vzome.core.algebra.AlgebraicVector.Y).evaluate();
            let maxz: number = max.getComponent(com.vzome.core.algebra.AlgebraicVector.Z).evaluate();
            let minx: number = maxx;
            let miny: number = maxy;
            let minz: number = maxz;
            for(let i: number = 1; i < this.numPoints; i++) {{
                const pnt: com.vzome.core.algebra.AlgebraicVector = this.pointBuffer[i].pnt;
                const pntx: number = pnt.getComponent(com.vzome.core.algebra.AlgebraicVector.X).evaluate();
                const pnty: number = pnt.getComponent(com.vzome.core.algebra.AlgebraicVector.Y).evaluate();
                const pntz: number = pnt.getComponent(com.vzome.core.algebra.AlgebraicVector.Z).evaluate();
                if (pntx > maxx){
                    maxx = pntx;
                    this.maxVtxs[0] = this.pointBuffer[i];
                } else if (pntx < minx){
                    minx = pntx;
                    this.minVtxs[0] = this.pointBuffer[i];
                }
                if (pnty > maxy){
                    maxy = pnty;
                    this.maxVtxs[1] = this.pointBuffer[i];
                } else if (pnty < miny){
                    miny = pnty;
                    this.minVtxs[1] = this.pointBuffer[i];
                }
                if (pntz > maxz){
                    maxz = pntz;
                    this.maxVtxs[2] = this.pointBuffer[i];
                } else if (pntz < minz){
                    minz = pntz;
                    this.minVtxs[2] = this.pointBuffer[i];
                }
            };}
        }

        /**
         * Creates the initial simplex from which the hull will be built.
         */
        createInitialSimplex() {
            let max: number = 0;
            let imax: number = 0;
            for(let i: number = 0; i < 3; i++) {{
                const diff: number = this.maxVtxs[i].pnt.getComponent(i)['minus$com_vzome_core_algebra_AlgebraicNumber'](this.minVtxs[i].pnt.getComponent(i)).evaluate();
                if (diff > max){
                    max = diff;
                    imax = i;
                }
            };}
            if (max <= 0){
                throw new com.vzome.core.commands.Command.Failure("Input points are coincident");
            }
            const vtx: com.vzome.core.math.convexhull.Vertex[] = [null, null, null, null];
            vtx[0] = this.maxVtxs[imax];
            vtx[1] = this.minVtxs[imax];
            let diff02: com.vzome.core.algebra.AlgebraicVector;
            let nrml: com.vzome.core.algebra.AlgebraicVector = null;
            let maxSqr: number = 0;
            const u01: com.vzome.core.algebra.AlgebraicVector = vtx[1].pnt.minus(vtx[0].pnt);
            for(let i: number = 0; i < this.numPoints; i++) {{
                diff02 = this.pointBuffer[i].pnt.minus(vtx[0].pnt);
                const xprod: com.vzome.core.algebra.AlgebraicVector = u01.cross(diff02);
                const lenSqr: com.vzome.core.algebra.AlgebraicNumber = xprod.dot(xprod);
                if (lenSqr.evaluate() > maxSqr && this.pointBuffer[i] !== vtx[0] && this.pointBuffer[i] !== vtx[1]){
                    maxSqr = lenSqr.evaluate();
                    vtx[2] = this.pointBuffer[i];
                    nrml = xprod;
                }
            };}
            if (maxSqr === 0){
                throw new com.vzome.core.commands.Command.Failure("Input points are collinear");
            }
            const res: com.vzome.core.algebra.AlgebraicVector = u01.scale(nrml.dot(u01));
            nrml = nrml.minus(res);
            let maxDist: number = 0.0;
            const d0: com.vzome.core.algebra.AlgebraicNumber = vtx[2].pnt.dot(nrml);
            for(let i: number = 0; i < this.numPoints; i++) {{
                const dist: number = Math.abs(this.pointBuffer[i].pnt.dot(nrml)['minus$com_vzome_core_algebra_AlgebraicNumber'](d0).evaluate());
                if (dist > maxDist && this.pointBuffer[i] !== vtx[0] && this.pointBuffer[i] !== vtx[1] && this.pointBuffer[i] !== vtx[2]){
                    maxDist = dist;
                    vtx[3] = this.pointBuffer[i];
                }
            };}
            if (maxDist === 0.0){
                throw new com.vzome.core.commands.Command.Failure("Input points are coplanar");
            }
            if (this.debug){
                console.info("initial vertices:");
                console.info(vtx[0].index + ": " + vtx[0].pnt);
                console.info(vtx[1].index + ": " + vtx[1].pnt);
                console.info(vtx[2].index + ": " + vtx[2].pnt);
                console.info(vtx[3].index + ": " + vtx[3].pnt);
            }
            const tris: com.vzome.core.math.convexhull.Face[] = [null, null, null, null];
            if (vtx[3].pnt.dot(nrml)['minus$com_vzome_core_algebra_AlgebraicNumber'](d0).evaluate() < 0){
                tris[0] = com.vzome.core.math.convexhull.Face.createTriangle$com_vzome_core_math_convexhull_Vertex$com_vzome_core_math_convexhull_Vertex$com_vzome_core_math_convexhull_Vertex(vtx[0], vtx[1], vtx[2]);
                tris[1] = com.vzome.core.math.convexhull.Face.createTriangle$com_vzome_core_math_convexhull_Vertex$com_vzome_core_math_convexhull_Vertex$com_vzome_core_math_convexhull_Vertex(vtx[3], vtx[1], vtx[0]);
                tris[2] = com.vzome.core.math.convexhull.Face.createTriangle$com_vzome_core_math_convexhull_Vertex$com_vzome_core_math_convexhull_Vertex$com_vzome_core_math_convexhull_Vertex(vtx[3], vtx[2], vtx[1]);
                tris[3] = com.vzome.core.math.convexhull.Face.createTriangle$com_vzome_core_math_convexhull_Vertex$com_vzome_core_math_convexhull_Vertex$com_vzome_core_math_convexhull_Vertex(vtx[3], vtx[0], vtx[2]);
                for(let i: number = 0; i < 3; i++) {{
                    const k: number = (i + 1) % 3;
                    tris[i + 1].getEdge(1).setOpposite(tris[k + 1].getEdge(0));
                    tris[i + 1].getEdge(2).setOpposite(tris[0].getEdge(k));
                };}
            } else {
                tris[0] = com.vzome.core.math.convexhull.Face.createTriangle$com_vzome_core_math_convexhull_Vertex$com_vzome_core_math_convexhull_Vertex$com_vzome_core_math_convexhull_Vertex(vtx[0], vtx[2], vtx[1]);
                tris[1] = com.vzome.core.math.convexhull.Face.createTriangle$com_vzome_core_math_convexhull_Vertex$com_vzome_core_math_convexhull_Vertex$com_vzome_core_math_convexhull_Vertex(vtx[3], vtx[0], vtx[1]);
                tris[2] = com.vzome.core.math.convexhull.Face.createTriangle$com_vzome_core_math_convexhull_Vertex$com_vzome_core_math_convexhull_Vertex$com_vzome_core_math_convexhull_Vertex(vtx[3], vtx[1], vtx[2]);
                tris[3] = com.vzome.core.math.convexhull.Face.createTriangle$com_vzome_core_math_convexhull_Vertex$com_vzome_core_math_convexhull_Vertex$com_vzome_core_math_convexhull_Vertex(vtx[3], vtx[2], vtx[0]);
                for(let i: number = 0; i < 3; i++) {{
                    const k: number = (i + 1) % 3;
                    tris[i + 1].getEdge(0).setOpposite(tris[k + 1].getEdge(1));
                    tris[i + 1].getEdge(2).setOpposite(tris[0].getEdge((3 - i) % 3));
                };}
            }
            for(let i: number = 0; i < 4; i++) {{
                this.faces.add(tris[i]);
            };}
            for(let i: number = 0; i < this.numPoints; i++) {{
                const v: com.vzome.core.math.convexhull.Vertex = this.pointBuffer[i];
                if (v === vtx[0] || v === vtx[1] || v === vtx[2] || v === vtx[3]){
                    continue;
                }
                maxDist = 0.0;
                let maxFace: com.vzome.core.math.convexhull.Face = null;
                for(let k: number = 0; k < 4; k++) {{
                    const dist: number = tris[k].distanceToPlane(v.pnt).evaluate();
                    if (dist > maxDist){
                        maxFace = tris[k];
                        maxDist = dist;
                    }
                };}
                if (maxFace != null){
                    this.addPointToFace(v, maxFace);
                }
            };}
        }

        /**
         * Returns the number of vertices in this hull.
         * 
         * @return {number} number of vertices
         */
        public getNumVertices(): number {
            return this.numVertices;
        }

        public getVertices$(): com.vzome.core.algebra.AlgebraicVector[] {
            const vtxs: com.vzome.core.algebra.AlgebraicVector[] = (s => { let a=[]; while(s-->0) a.push(null); return a; })(this.numVertices);
            for(let i: number = 0; i < this.numVertices; i++) {{
                vtxs[i] = this.pointBuffer[this.vertexPointIndices[i]].pnt;
            };}
            return vtxs;
        }

        public getVertices$com_vzome_core_algebra_AlgebraicNumber_A(coords: com.vzome.core.algebra.AlgebraicNumber[]): number {
            for(let i: number = 0; i < this.numVertices; i++) {{
                const pnt: com.vzome.core.algebra.AlgebraicVector = this.pointBuffer[this.vertexPointIndices[i]].pnt;
                coords[i * 3 + 0] = pnt.getComponent(com.vzome.core.algebra.AlgebraicVector.X);
                coords[i * 3 + 1] = pnt.getComponent(com.vzome.core.algebra.AlgebraicVector.Y);
                coords[i * 3 + 2] = pnt.getComponent(com.vzome.core.algebra.AlgebraicVector.Z);
            };}
            return this.numVertices;
        }

        /**
         * Returns the coordinates of the vertex points of this hull.
         * 
         * @param {com.vzome.core.algebra.AlgebraicNumber[]} coords
         * returns the x, y, z coordinates of each vertex. This length of
         * this array must be at least three times the number of vertices.
         * @return {number} the number of vertices
         * @see QuickHull3D#getVertices()
         * @see QuickHull3D#getFaces()
         */
        public getVertices(coords?: any): any {
            if (((coords != null && coords instanceof <any>Array && (coords.length == 0 || coords[0] == null ||(coords[0] != null && (coords[0].constructor != null && coords[0].constructor["__interfaces"] != null && coords[0].constructor["__interfaces"].indexOf("com.vzome.core.algebra.AlgebraicNumber") >= 0)))) || coords === null)) {
                return <any>this.getVertices$com_vzome_core_algebra_AlgebraicNumber_A(coords);
            } else if (coords === undefined) {
                return <any>this.getVertices$();
            } else throw new Error('invalid overload');
        }

        /**
         * Returns an array specifing the index of each hull vertex with respect to the
         * original input points.
         * 
         * @return {int[]} vertex indices with respect to the original points
         */
        public getVertexPointIndices(): number[] {
            const indices: number[] = (s => { let a=[]; while(s-->0) a.push(0); return a; })(this.numVertices);
            for(let i: number = 0; i < this.numVertices; i++) {{
                indices[i] = this.vertexPointIndices[i];
            };}
            return indices;
        }

        /**
         * Returns the number of edges in this hull.
         * 
         * @return {number} number of edges
         */
        public getNumEdges(): number {
            let count: number = 0;
            for(let index=this.faces.iterator();index.hasNext();) {
                let face = index.next();
                {
                    count += face.numVertices();
                }
            }
            return (count / 2|0);
        }

        /**
         * Returns the number of faces in this hull.
         * 
         * @return {number} number of faces
         */
        public getNumFaces(): number {
            return this.faces.size();
        }

        public getFaces$(): number[][] {
            return this.getFaces$int(0);
        }

        public getFaces$int(indexFlags: number): number[][] {
            const allFaces: number[][] = (s => { let a=[]; while(s-->0) a.push(null); return a; })(this.faces.size());
            let k: number = 0;
            for(let index=this.faces.iterator();index.hasNext();) {
                let face = index.next();
                {
                    allFaces[k] = (s => { let a=[]; while(s-->0) a.push(0); return a; })(face.numVertices());
                    this.getFaceIndices(allFaces[k], face, indexFlags);
                    k++;
                }
            }
            return allFaces;
        }

        /**
         * Returns the faces associated with this hull.
         * 
         * <p>
         * Each face is represented by an integer array which gives the indices of the
         * vertices. By default, these indices are numbered with respect to the hull
         * vertices (as opposed to the input points), are zero-based, and are arranged
         * counter-clockwise. However, this can be changed by setting
         * {@link #POINT_RELATIVE}, {@link #INDEXED_FROM_ONE}, or {@link #CLOCKWISE} in the indexFlags
         * parameter.
         * 
         * @param {number} indexFlags
         * specifies index characteristics (0 results in the default)
         * @return {int[][]} array of integer arrays, giving the vertex indices for each face.
         * @see QuickHull3D#getVertices()
         */
        public getFaces(indexFlags?: any): number[][] {
            if (((typeof indexFlags === 'number') || indexFlags === null)) {
                return <any>this.getFaces$int(indexFlags);
            } else if (indexFlags === undefined) {
                return <any>this.getFaces$();
            } else throw new Error('invalid overload');
        }

        public print$java_io_PrintStream(ps: java.io.PrintStream) {
            this.print$java_io_PrintStream$int(ps, 0);
        }

        public print$java_io_PrintStream$int(ps: java.io.PrintStream, indexFlags: number) {
            if ((indexFlags & QuickHull3D.INDEXED_FROM_ZERO) === 0){
                indexFlags |= QuickHull3D.INDEXED_FROM_ONE;
            }
            for(let i: number = 0; i < this.numVertices; i++) {{
                const pnt: com.vzome.core.algebra.AlgebraicVector = this.pointBuffer[this.vertexPointIndices[i]].pnt;
                ps.println(pnt);
            };}
            for(let index=this.faces.iterator();index.hasNext();) {
                let face = index.next();
                {
                    const indices: number[] = (s => { let a=[]; while(s-->0) a.push(0); return a; })(face.numVertices());
                    this.getFaceIndices(indices, face, indexFlags);
                    ps.print("f");
                    for(let k: number = 0; k < indices.length; k++) {{
                        ps.print(" " + indices[k]);
                    };}
                    ps.println("");
                }
            }
        }

        /**
         * Prints the vertices and faces of this hull to the stream ps.
         * 
         * <p>
         * This is done using the Alias Wavefront .obj file format, with the vertices
         * printed first (each preceding by the letter <code>v</code>), followed by the
         * vertex indices for each face (each preceded by the letter <code>f</code>).
         * 
         * <p>
         * By default, the face indices are numbered with respect to the hull vertices
         * (as opposed to the input points), with a lowest index of 1, and are arranged
         * counter-clockwise. However, this can be changed by setting
         * {@link #POINT_RELATIVE}, {@link #INDEXED_FROM_ONE}, or {@link #CLOCKWISE} in the indexFlags
         * parameter.
         * 
         * @param {java.io.PrintStream} ps
         * stream used for printing
         * @param {number} indexFlags
         * specifies index characteristics (0 results in the default).
         * @see QuickHull3D#getVertices()
         * @see QuickHull3D#getFaces()
         */
        public print(ps?: any, indexFlags?: any) {
            if (((ps != null && ps instanceof <any>java.io.PrintStream) || ps === null) && ((typeof indexFlags === 'number') || indexFlags === null)) {
                return <any>this.print$java_io_PrintStream$int(ps, indexFlags);
            } else if (((ps != null && ps instanceof <any>java.io.PrintStream) || ps === null) && indexFlags === undefined) {
                return <any>this.print$java_io_PrintStream(ps);
            } else throw new Error('invalid overload');
        }

        /*private*/ getFaceIndices(indices: number[], face: com.vzome.core.math.convexhull.Face, flags: number) {
            const ccw: boolean = ((flags & QuickHull3D.CLOCKWISE) === 0);
            const indexedFromOne: boolean = ((flags & QuickHull3D.INDEXED_FROM_ONE) !== 0);
            const pointRelative: boolean = ((flags & QuickHull3D.POINT_RELATIVE) !== 0);
            let hedge: com.vzome.core.math.convexhull.HalfEdge = face.he0;
            let k: number = 0;
            do {{
                let idx: number = hedge.head().index;
                if (pointRelative){
                    idx = this.vertexPointIndices[idx];
                }
                if (indexedFromOne){
                    idx++;
                }
                indices[k++] = idx;
                hedge = (ccw ? hedge.next : hedge.prev);
            }} while((hedge !== face.he0));
        }

        resolveUnclaimedPoints(newFaces: java.util.List<com.vzome.core.math.convexhull.Face>) {
            let vtxNext: com.vzome.core.math.convexhull.Vertex = this.unclaimed.first();
            for(let vtx: com.vzome.core.math.convexhull.Vertex = vtxNext; vtx != null; vtx = vtxNext) {{
                vtxNext = vtx.next;
                let maxDist: number = 0;
                let maxFace: com.vzome.core.math.convexhull.Face = null;
                for(let index=newFaces.iterator();index.hasNext();) {
                    let newFace = index.next();
                    {
                        if (newFace.mark === com.vzome.core.math.convexhull.Face.VISIBLE){
                            const dist: number = newFace.distanceToPlane(vtx.pnt).evaluate();
                            if (dist > maxDist){
                                maxDist = dist;
                                maxFace = newFace;
                            }
                            if (maxDist > 0){
                                break;
                            }
                        }
                    }
                }
                if (maxFace != null){
                    this.addPointToFace(vtx, maxFace);
                    if (this.debug && vtx.index === this.findIndex){
                        console.info(this.findIndex + " CLAIMED BY " + maxFace.getVertexString());
                    }
                } else {
                    if (this.debug && vtx.index === this.findIndex){
                        console.info(this.findIndex + " DISCARDED");
                    }
                }
            };}
        }

        deleteFacePoints(face: com.vzome.core.math.convexhull.Face, absorbingFace: com.vzome.core.math.convexhull.Face) {
            const faceVtxs: com.vzome.core.math.convexhull.Vertex = this.removeAllPointsFromFace(face);
            if (faceVtxs != null){
                if (absorbingFace == null){
                    this.unclaimed.addAll(faceVtxs);
                } else {
                    let vtxNext: com.vzome.core.math.convexhull.Vertex = faceVtxs;
                    for(let vtx: com.vzome.core.math.convexhull.Vertex = vtxNext; vtx != null; vtx = vtxNext) {{
                        vtxNext = vtx.next;
                        const dist: number = absorbingFace.distanceToPlane(vtx.pnt).evaluate();
                        if (dist > 0){
                            this.addPointToFace(vtx, absorbingFace);
                        } else {
                            this.unclaimed.add(vtx);
                        }
                    };}
                }
            }
        }

        static NONCONVEX_WRT_LARGER_FACE: number = 1;

        static NONCONVEX: number = 2;

        oppFaceDistance(he: com.vzome.core.math.convexhull.HalfEdge): com.vzome.core.algebra.AlgebraicNumber {
            return he.face.distanceToPlane(he.opposite.face.getCentroid());
        }

        /*private*/ doAdjacentMerge(face: com.vzome.core.math.convexhull.Face, mergeType: number): boolean {
            let hedge: com.vzome.core.math.convexhull.HalfEdge = face.he0;
            let convex: boolean = true;
            do {{
                const oppFace: com.vzome.core.math.convexhull.Face = hedge.oppositeFace();
                let merge: boolean = false;
                const tolerance: number = 0;
                if (mergeType === QuickHull3D.NONCONVEX){
                    if (this.oppFaceDistance(hedge).evaluate() > -tolerance || this.oppFaceDistance(hedge.opposite).evaluate() > -tolerance){
                        merge = true;
                    }
                } else {
                    if (face.area > oppFace.area){
                        if (this.oppFaceDistance(hedge).evaluate() > -tolerance){
                            merge = true;
                        } else if (this.oppFaceDistance(hedge.opposite).evaluate() > -tolerance){
                            convex = false;
                        }
                    } else {
                        if (this.oppFaceDistance(hedge.opposite).evaluate() >= -tolerance){
                            merge = true;
                        } else if (this.oppFaceDistance(hedge).evaluate() >= -tolerance){
                            convex = false;
                        }
                    }
                }
                if (merge){
                    if (this.debug){
                        console.info("  merging " + face.getVertexString() + "  and  " + oppFace.getVertexString());
                    }
                    const numd: number = face.mergeAdjacentFace(hedge, this.discardedFaces);
                    for(let i: number = 0; i < numd; i++) {{
                        this.deleteFacePoints(this.discardedFaces[i], face);
                    };}
                    if (this.debug){
                        console.info("  result: " + face.getVertexString());
                    }
                    return true;
                }
                hedge = hedge.next;
            }} while((hedge !== face.he0));
            if (!convex){
                face.mark = com.vzome.core.math.convexhull.Face.NON_CONVEX;
            }
            return false;
        }

        calculateHorizon(eyePnt: com.vzome.core.algebra.AlgebraicVector, edge0: com.vzome.core.math.convexhull.HalfEdge, face: com.vzome.core.math.convexhull.Face, horizon: java.util.Vector<com.vzome.core.math.convexhull.HalfEdge>) {
            this.deleteFacePoints(face, null);
            face.mark = com.vzome.core.math.convexhull.Face.DELETED;
            if (this.debug){
                console.info("  visiting face " + face.getVertexString());
            }
            let edge: com.vzome.core.math.convexhull.HalfEdge;
            if (edge0 == null){
                edge0 = face.getEdge(0);
                edge = edge0;
            } else {
                edge = edge0.getNext();
            }
            const tolerance: number = 0;
            do {{
                const oppFace: com.vzome.core.math.convexhull.Face = edge.oppositeFace();
                if (oppFace.mark === com.vzome.core.math.convexhull.Face.VISIBLE){
                    if (oppFace.distanceToPlane(eyePnt).evaluate() > tolerance){
                        this.calculateHorizon(eyePnt, edge.getOpposite(), oppFace, horizon);
                    } else {
                        horizon.add(edge);
                        if (this.debug){
                            console.info("  adding horizon edge " + edge.getVertexString());
                        }
                    }
                }
                edge = edge.getNext();
            }} while((edge !== edge0));
        }

        /*private*/ addAdjoiningFace(eyeVtx: com.vzome.core.math.convexhull.Vertex, he: com.vzome.core.math.convexhull.HalfEdge): com.vzome.core.math.convexhull.HalfEdge {
            const face: com.vzome.core.math.convexhull.Face = com.vzome.core.math.convexhull.Face.createTriangle$com_vzome_core_math_convexhull_Vertex$com_vzome_core_math_convexhull_Vertex$com_vzome_core_math_convexhull_Vertex(eyeVtx, he.tail(), he.head());
            this.faces.add(face);
            face.getEdge(-1).setOpposite(he.getOpposite());
            return face.getEdge(0);
        }

        addNewFaces(newFaces: java.util.List<com.vzome.core.math.convexhull.Face>, eyeVtx: com.vzome.core.math.convexhull.Vertex, horizon: java.util.Vector<com.vzome.core.math.convexhull.HalfEdge>) {
            newFaces.clear();
            let hedgeSidePrev: com.vzome.core.math.convexhull.HalfEdge = null;
            let hedgeSideBegin: com.vzome.core.math.convexhull.HalfEdge = null;
            for(let index=horizon.iterator();index.hasNext();) {
                let horizonHe = index.next();
                {
                    const hedgeSide: com.vzome.core.math.convexhull.HalfEdge = this.addAdjoiningFace(eyeVtx, horizonHe);
                    if (this.debug){
                        console.info("new face: " + hedgeSide.face.getVertexString());
                    }
                    if (hedgeSidePrev != null){
                        hedgeSide.next.setOpposite(hedgeSidePrev);
                    } else {
                        hedgeSideBegin = hedgeSide;
                    }
                    newFaces.add(hedgeSide.getFace());
                    hedgeSidePrev = hedgeSide;
                }
            }
            hedgeSideBegin.next.setOpposite(hedgeSidePrev);
        }

        nextPointToAdd(): com.vzome.core.math.convexhull.Vertex {
            if (!this.claimed.isEmpty()){
                const eyeFace: com.vzome.core.math.convexhull.Face = this.claimed.first().face;
                let eyeVtx: com.vzome.core.math.convexhull.Vertex = null;
                let maxDist: number = 0;
                for(let vtx: com.vzome.core.math.convexhull.Vertex = eyeFace.outside; vtx != null && vtx.face === eyeFace; vtx = vtx.next) {{
                    const dist: number = eyeFace.distanceToPlane(vtx.pnt).evaluate();
                    if (dist > maxDist){
                        maxDist = dist;
                        eyeVtx = vtx;
                    }
                };}
                return eyeVtx;
            } else {
                return null;
            }
        }

        addPointToHull(eyeVtx: com.vzome.core.math.convexhull.Vertex) {
            const horizon: java.util.Vector<com.vzome.core.math.convexhull.HalfEdge> = <any>(new java.util.Vector<any>());
            this.unclaimed.clear();
            if (this.debug){
                console.info("Adding point: " + eyeVtx.index);
                console.info(" which is " + eyeVtx.face.distanceToPlane(eyeVtx.pnt) + " above face " + eyeVtx.face.getVertexString());
            }
            this.removePointFromFace(eyeVtx, eyeVtx.face);
            this.calculateHorizon(eyeVtx.pnt, null, eyeVtx.face, horizon);
            this.newFaces.clear();
            this.addNewFaces(this.newFaces, eyeVtx, horizon);
            for(let index=this.newFaces.iterator();index.hasNext();) {
                let face = index.next();
                {
                    if (face.mark === com.vzome.core.math.convexhull.Face.VISIBLE){
                        while((this.doAdjacentMerge(face, QuickHull3D.NONCONVEX_WRT_LARGER_FACE))) {};
                    }
                }
            }
            for(let index=this.newFaces.iterator();index.hasNext();) {
                let face = index.next();
                {
                    if (face.mark === com.vzome.core.math.convexhull.Face.NON_CONVEX){
                        face.mark = com.vzome.core.math.convexhull.Face.VISIBLE;
                        while((this.doAdjacentMerge(face, QuickHull3D.NONCONVEX))) {};
                    }
                }
            }
            this.resolveUnclaimedPoints(this.newFaces);
        }

        buildHull() {
            let cnt: number = 0;
            let eyeVtx: com.vzome.core.math.convexhull.Vertex;
            this.computeMaxAndMin();
            this.createInitialSimplex();
            while(((eyeVtx = this.nextPointToAdd()) != null)) {{
                this.addPointToHull(eyeVtx);
                cnt++;
                if (this.debug){
                    console.info("iteration " + cnt + " done");
                }
            }};
            this.reindexFacesAndVertices();
            if (this.debug){
                console.info("hull done");
            }
        }

        /*private*/ markFaceVertices(face: com.vzome.core.math.convexhull.Face, mark: number) {
            const he0: com.vzome.core.math.convexhull.HalfEdge = face.getFirstEdge();
            let he: com.vzome.core.math.convexhull.HalfEdge = he0;
            do {{
                he.head().index = mark;
                he = he.next;
            }} while((he !== he0));
        }

        reindexFacesAndVertices() {
            for(let i: number = 0; i < this.numPoints; i++) {{
                this.pointBuffer[i].index = -1;
            };}
            this.numFaces = 0;
            for(const it: java.util.Iterator<com.vzome.core.math.convexhull.Face> = this.faces.iterator(); it.hasNext(); ) {{
                const face: com.vzome.core.math.convexhull.Face = it.next();
                if (face.mark !== com.vzome.core.math.convexhull.Face.VISIBLE){
                    it.remove();
                } else {
                    this.markFaceVertices(face, 0);
                    this.numFaces++;
                }
            };}
            this.numVertices = 0;
            for(let i: number = 0; i < this.numPoints; i++) {{
                const vtx: com.vzome.core.math.convexhull.Vertex = this.pointBuffer[i];
                if (vtx.index === 0){
                    this.vertexPointIndices[this.numVertices] = i;
                    vtx.index = this.numVertices++;
                }
            };}
        }

        checkFaceConvexity(face: com.vzome.core.math.convexhull.Face, ps: java.io.PrintStream): boolean {
            let he: com.vzome.core.math.convexhull.HalfEdge = face.he0;
            do {{
                face.checkConsistency();
                let dist: com.vzome.core.algebra.AlgebraicNumber = this.oppFaceDistance(he);
                if (!dist.isZero()){
                    if (ps != null){
                        ps.println("Edge " + he.getVertexString() + " non-convex by " + dist);
                    }
                    return false;
                }
                dist = this.oppFaceDistance(he.opposite);
                if (!dist.isZero()){
                    if (ps != null){
                        ps.println("Opposite edge " + he.opposite.getVertexString() + " non-convex by " + dist);
                    }
                    return false;
                }
                if (he.next.oppositeFace() === he.oppositeFace()){
                    if (ps != null){
                        ps.println("Redundant vertex " + he.head().index + " in face " + face.getVertexString());
                    }
                    return false;
                }
                he = he.next;
            }} while((he !== face.he0));
            return true;
        }

        checkFaces(ps: java.io.PrintStream): boolean {
            let convex: boolean = true;
            for(let index=this.faces.iterator();index.hasNext();) {
                let face = index.next();
                {
                    if (face.mark === com.vzome.core.math.convexhull.Face.VISIBLE){
                        if (!this.checkFaceConvexity(face, ps)){
                            convex = false;
                        }
                    }
                }
            }
            return convex;
        }

        /**
         * Checks the correctness of the hull. This is done by making sure that no faces
         * are non-convex and that no points are outside any face. These tests are
         * performed using the distance tolerance <i>tol</i>. Faces are considered
         * non-convex if any edge is non-convex, and an edge is non-convex if the
         * centroid of either adjoining face is more than <i>tol</i> above the plane of
         * the other face. Similarly, a point is considered outside a face if its
         * distance to that face's plane is more than 10 times <i>tol</i>.
         * 
         * <p>
         * If the hull has been {@link #triangulate}, then this routine may
         * fail if some of the resulting triangles are very small or thin.
         * 
         * @param {java.io.PrintStream} ps
         * print stream for diagnostic messages; may be set to
         * <code>null</code> if no messages are desired.
         * @return {boolean} true if the hull is valid
         * @throws Failure
         * @see QuickHull3D#check(PrintStream)
         */
        public check(ps: java.io.PrintStream): boolean {
            if (!this.checkFaces(ps)){
                return false;
            }
            for(let i: number = 0; i < this.numPoints; i++) {{
                const pnt: com.vzome.core.algebra.AlgebraicVector = this.pointBuffer[i].pnt;
                for(let index=this.faces.iterator();index.hasNext();) {
                    let face = index.next();
                    {
                        if (face.mark === com.vzome.core.math.convexhull.Face.VISIBLE){
                            const dist: com.vzome.core.algebra.AlgebraicNumber = face.distanceToPlane(pnt);
                            if (!dist.isZero()){
                                if (ps != null){
                                    ps.println("Point " + i + " " + dist + " above face " + face.getVertexString());
                                }
                                return false;
                            }
                        }
                    }
                }
            };}
            return true;
        }
    }
    QuickHull3D["__class"] = "com.vzome.core.math.convexhull.QuickHull3D";

}

