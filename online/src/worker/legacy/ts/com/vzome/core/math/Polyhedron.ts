/* Generated from Java with JSweet 3.2.0-SNAPSHOT - http://www.jsweet.org */
namespace com.vzome.core.math {
    export class Polyhedron implements java.lang.Cloneable {
        static logger: java.util.logging.Logger; public static logger_$LI$(): java.util.logging.Logger { if (Polyhedron.logger == null) { Polyhedron.logger = java.util.logging.Logger.getLogger("com.vzome.core.math.Polyhedron"); }  return Polyhedron.logger; }

        numVertices: number;

        m_vertices: java.util.Map<com.vzome.core.algebra.AlgebraicVector, number>;

        m_vertexList: java.util.List<com.vzome.core.algebra.AlgebraicVector>;

        m_faces: java.util.Set<Polyhedron.Face>;

        /*private*/ field: com.vzome.core.algebra.AlgebraicField;

        /*private*/ evilTwin: Polyhedron;

        /*private*/ isEvil: boolean;

        /*private*/ __isPanel: boolean;

        /*private*/ guid: java.util.UUID;

        public constructor(field: com.vzome.core.algebra.AlgebraicField) {
            this.numVertices = 0;
            this.m_vertices = <any>(new java.util.HashMap<any, any>());
            this.m_vertexList = <any>(new java.util.ArrayList<any>());
            this.m_faces = <any>(new java.util.HashSet<any>());
            if (this.field === undefined) { this.field = null; }
            if (this.evilTwin === undefined) { this.evilTwin = null; }
            this.isEvil = false;
            this.__isPanel = false;
            this.guid = java.util.UUID.randomUUID();
            if (this.name === undefined) { this.name = null; }
            if (this.orbit === undefined) { this.orbit = null; }
            if (this.length === undefined) { this.length = null; }
            this.field = field;
        }

        /**
         * Get the mirror twin of this Polyhedron.
         * The vertices are transformed by the given reflection.
         * The faces are oriented in reverse, so that when oriented with
         * a mirroring transformation, the face normals will still point
         * outward.
         * @return
         * @param {com.vzome.core.algebra.AlgebraicMatrix} reflection
         * @return {com.vzome.core.math.Polyhedron}
         */
        public getEvilTwin(reflection: com.vzome.core.algebra.AlgebraicMatrix): Polyhedron {
            if (this.evilTwin == null){
                try {
                    this.evilTwin = <Polyhedron>/* clone */((o: any) => { if (o.clone != undefined) { return (<any>o).clone(); } else { let clone = Object.create(o); for(let p in o) { if (o.hasOwnProperty(p)) clone[p] = o[p]; } return clone; } })(this);
                } catch(e) {
                    console.error(e.message, e);
                }
                this.evilTwin.isEvil = true;
                this.evilTwin.guid = java.util.UUID.randomUUID();
                this.evilTwin.m_vertexList = <any>(new java.util.ArrayList<any>());
                for(let index=this.m_vertexList.iterator();index.hasNext();) {
                    let vertex = index.next();
                    {
                        this.evilTwin.addVertex(reflection.timesColumn(vertex));
                    }
                }
                this.evilTwin.m_faces = <any>(new java.util.HashSet<Polyhedron.Face>());
                for(let index=this.m_faces.iterator();index.hasNext();) {
                    let face = index.next();
                    {
                        this.evilTwin.addFace(face.createReverse());
                    }
                }
            }
            return this.evilTwin;
        }

        public getField(): com.vzome.core.algebra.AlgebraicField {
            return this.field;
        }

        /*private*/ name: string;

        /*private*/ orbit: com.vzome.core.math.symmetry.Direction;

        /*private*/ length: com.vzome.core.algebra.AlgebraicNumber;

        public setName(name: string) {
            this.name = name;
        }

        public getName(): string {
            return this.name;
        }

        public addVertex(location: com.vzome.core.algebra.AlgebraicVector) {
            this.m_vertexList.add(location);
        }

        /**
         * Only used in ZomicPolyhedronModelInterpreter.
         * This used to be the implementation of addVertex, but all other callers
         * don't use the return value, and have already assigned their own indices,
         * so the collisions here are a bad idea.
         * @param halfLoc
         * @return
         * @param {com.vzome.core.algebra.AlgebraicVector} location
         * @return {number}
         */
        public addIndexedVertex(location: com.vzome.core.algebra.AlgebraicVector): number {
            let vertexObj: number = this.m_vertices.get(location);
            if (vertexObj == null){
                this.m_vertexList.add(location);
                this.m_vertices.put(location, vertexObj = this.numVertices++);
            }
            return vertexObj;
        }

        public addFace(face: Polyhedron.Face) {
            face.canonicallyOrder();
            if (!this.m_faces.contains(face)){
                this.m_faces.add(face);
            }
        }

        public newFace(): Polyhedron.Face {
            return new Polyhedron.Face(this);
        }

        /**
         * 
         * @return {number}
         */
        public hashCode(): number {
            const prime: number = 31;
            let result: number = 1;
            result = prime * result + (this.isEvil ? 1231 : 1237);
            result = prime * result + ((this.length == null) ? 0 : /* hashCode */(<any>((o: any) => { if (o.hashCode) { return o.hashCode(); } else { return o.toString().split('').reduce((prevHash, currVal) => (((prevHash << 5) - prevHash) + currVal.charCodeAt(0))|0, 0); }})(this.length)));
            result = prime * result + ((this.m_faces == null) ? 0 : /* hashCode */(<any>((o: any) => { if (o.hashCode) { return o.hashCode(); } else { return o.toString().split('').reduce((prevHash, currVal) => (((prevHash << 5) - prevHash) + currVal.charCodeAt(0))|0, 0); }})(this.m_faces)));
            result = prime * result + ((this.m_vertexList == null) ? 0 : /* hashCode */(<any>((o: any) => { if (o.hashCode) { return o.hashCode(); } else { return o.toString().split('').reduce((prevHash, currVal) => (((prevHash << 5) - prevHash) + currVal.charCodeAt(0))|0, 0); }})(this.m_vertexList)));
            result = prime * result + this.numVertices;
            result = prime * result + ((this.orbit == null) ? 0 : /* hashCode */(<any>((o: any) => { if (o.hashCode) { return o.hashCode(); } else { return o.toString().split('').reduce((prevHash, currVal) => (((prevHash << 5) - prevHash) + currVal.charCodeAt(0))|0, 0); }})(this.orbit)));
            return result;
        }

        /**
         * 
         * @param {*} obj
         * @return {boolean}
         */
        public equals(obj: any): boolean {
            if (this === obj){
                return true;
            }
            if (obj == null){
                return false;
            }
            if ((<any>this.constructor) !== (<any>obj.constructor)){
                return false;
            }
            const other: Polyhedron = <Polyhedron>obj;
            if (this.isEvil !== other.isEvil){
                return false;
            }
            if (this.length == null){
                if (other.length != null){
                    return false;
                }
            } else if (!/* equals */(<any>((o1: any, o2: any) => { if (o1 && o1.equals) { return o1.equals(o2); } else { return o1 === o2; } })(this.length,other.length))){
                return false;
            }
            if (this.m_faces == null){
                if (other.m_faces != null){
                    return false;
                }
            } else if (!/* equals */(<any>((o1: any, o2: any) => { if (o1 && o1.equals) { return o1.equals(o2); } else { return o1 === o2; } })(this.m_faces,other.m_faces))){
                return false;
            }
            if (this.m_vertexList == null){
                if (other.m_vertexList != null){
                    return false;
                }
            } else if (!/* equals */(<any>((o1: any, o2: any) => { if (o1 && o1.equals) { return o1.equals(o2); } else { return o1 === o2; } })(this.m_vertexList,other.m_vertexList))){
                return false;
            }
            if (this.numVertices !== other.numVertices){
                return false;
            }
            if (this.orbit == null){
                if (other.orbit != null){
                    return false;
                }
            } else if (!this.orbit.equals(other.orbit)){
                return false;
            }
            return true;
        }

        public setOrbit(orbit: com.vzome.core.math.symmetry.Direction) {
            this.orbit = orbit;
        }

        public setLength(length: com.vzome.core.algebra.AlgebraicNumber) {
            this.length = length;
        }

        public getOrbit(): com.vzome.core.math.symmetry.Direction {
            return this.orbit;
        }

        public getLength(): com.vzome.core.algebra.AlgebraicNumber {
            return this.length;
        }

        public isPanel(): boolean {
            return this.__isPanel;
        }

        public setPanel(isPanel: boolean) {
            this.__isPanel = isPanel;
        }

        public getVertexList(): java.util.List<com.vzome.core.algebra.AlgebraicVector> {
            return this.m_vertexList;
        }

        public getFaceSet(): java.util.Set<Polyhedron.Face> {
            return this.m_faces;
        }

        public getGuid(): java.util.UUID {
            return this.guid;
        }

        public getTriangleFaces(): java.util.List<Polyhedron.Face.Triangle> {
            const result: java.util.ArrayList<Polyhedron.Face.Triangle> = <any>(new java.util.ArrayList<any>());
            for(let index=this.m_faces.iterator();index.hasNext();) {
                let face = index.next();
                {
                    result.addAll(face.getTriangles());
                }
            }
            return result;
        }

        public getTriangles(): java.util.List<number> {
            let index: number = 0;
            const result: java.util.ArrayList<number> = <any>(new java.util.ArrayList<any>());
            for(let index1=this.m_faces.iterator();index1.hasNext();) {
                let face = index1.next();
                {
                    for(let index1=face.getTriangles().iterator();index1.hasNext();) {
                        let triangle = index1.next();
                        {
                            result.add(index++);
                            result.add(index++);
                            result.add(index++);
                        }
                    }
                }
            }
            return result;
        }

        public getTriangleVertices(): java.util.List<com.vzome.core.math.RealVector> {
            const result: java.util.ArrayList<com.vzome.core.math.RealVector> = <any>(new java.util.ArrayList<any>());
            for(let index=this.m_faces.iterator();index.hasNext();) {
                let face = index.next();
                {
                    for(let index=face.getTriangles().iterator();index.hasNext();) {
                        let triangle = index.next();
                        {
                            for(let loopIndex = 0; loopIndex < triangle.vertices.length; loopIndex++) {
                                let index = triangle.vertices[loopIndex];
                                {
                                    const vertex: com.vzome.core.algebra.AlgebraicVector = this.m_vertexList.get(index);
                                    result.add(vertex.toRealVector());
                                }
                            }
                        }
                    }
                }
            }
            return result;
        }

        public getNormals(): java.util.List<com.vzome.core.math.RealVector> {
            const result: java.util.ArrayList<com.vzome.core.math.RealVector> = <any>(new java.util.ArrayList<any>());
            for(let index=this.m_faces.iterator();index.hasNext();) {
                let face = index.next();
                {
                    const normal: com.vzome.core.math.RealVector = face.getNormal(this.m_vertexList).toRealVector();
                    for(let index=face.getTriangles().iterator();index.hasNext();) {
                        let triangle = index.next();
                        {
                            result.add(normal);
                            result.add(normal);
                            result.add(normal);
                        }
                    }
                }
            }
            return result;
        }
    }
    Polyhedron["__class"] = "com.vzome.core.math.Polyhedron";
    Polyhedron["__interfaces"] = ["java.lang.Cloneable"];



    export namespace Polyhedron {

        export class Face extends java.util.ArrayList<number> {
            public __parent: any;
            constructor(__parent: any) {
                super();
                this.__parent = __parent;
            }

            public createReverse(): Polyhedron.Face {
                const vertices: java.util.ArrayList<number> = <java.util.ArrayList<number>>/* clone */((o: any) => { if (o.clone != undefined) { return (<any>o).clone(); } else { let clone = Object.create(o); for(let p in o) { if (o.hasOwnProperty(p)) clone[p] = o[p]; } return clone; } })(this);
                java.util.Collections.reverse(vertices);
                const mirrorFace: Polyhedron.Face = new Polyhedron.Face(this.__parent);
                mirrorFace.addAll(vertices);
                return mirrorFace;
            }

            public getVertex(index: number): number {
                if (index >= this.size()){
                    const msg: string = "index larger than Face size";
                    com.vzome.core.math.Polyhedron.logger_$LI$().severe(msg);
                    throw new java.lang.IllegalStateException(msg);
                }
                return this.get(index);
            }

            public getTriangles(): java.util.List<com.vzome.core.math.Polyhedron.Face.Triangle> {
                const arity: number = this.size();
                const result: java.util.ArrayList<com.vzome.core.math.Polyhedron.Face.Triangle> = <any>(new java.util.ArrayList<any>());
                let v0: number = -1;
                let v1: number = -1;
                for(let j: number = 0; j < arity; j++) {{
                    const index: number = this.get(j);
                    if (v0 === -1){
                        v0 = index;
                    } else if (v1 === -1){
                        v1 = index;
                    } else {
                        const triangle: com.vzome.core.math.Polyhedron.Face.Triangle = new com.vzome.core.math.Polyhedron.Face.Triangle(this, v0, v1, index);
                        result.add(triangle);
                        v1 = index;
                    }
                };}
                return result;
            }

            public getNormal(vertices: java.util.List<com.vzome.core.algebra.AlgebraicVector>): com.vzome.core.algebra.AlgebraicVector {
                return com.vzome.core.algebra.AlgebraicVectors.getNormal$com_vzome_core_algebra_AlgebraicVector$com_vzome_core_algebra_AlgebraicVector$com_vzome_core_algebra_AlgebraicVector(vertices.get(this.getVertex(0)), vertices.get(this.getVertex(1)), vertices.get(this.getVertex(2)));
            }

            canonicallyOrder() {
                let minIndex: number = -1;
                let minVertex: number = javaemul.internal.IntegerHelper.MAX_VALUE;
                const sz: number = this.size();
                for(let i: number = 0; i < sz; i++) {if (this.getVertex(i) <= minVertex){
                    minVertex = this.getVertex(i);
                    minIndex = i;
                };}
                const temp: number[] = (s => { let a=[]; while(s-->0) a.push(null); return a; })(sz);
                for(let j: number = 0; j < sz; j++) {{
                    temp[j] = this.get((j + minIndex) % sz);
                };}
                for(let k: number = 0; k < sz; k++) {this.set(k, temp[k]);}
            }

            /**
             * 
             * @return {number}
             */
            public hashCode(): number {
                let tot: number = 0;
                for(let i: number = 0; i < this.size(); i++) {tot += this.getVertex(i);}
                return tot;
            }

            /**
             * 
             * @param {*} other
             * @return {boolean}
             */
            public equals(other: any): boolean {
                if (other == null)return false;
                if (other === this)return true;
                if (!(other != null && other instanceof <any>com.vzome.core.math.Polyhedron.Face))return false;
                const otherFace: Polyhedron.Face = <Polyhedron.Face>other;
                if (otherFace.size() !== this.size())return false;
                for(let i: number = 0; i < this.size(); i++) {if (!(this.get(i) === otherFace.get(i)))return false;;}
                return true;
            }
        }
        Face["__class"] = "com.vzome.core.math.Polyhedron.Face";
        Face["__interfaces"] = ["java.util.RandomAccess","java.util.List","java.lang.Cloneable","java.util.Collection","java.lang.Iterable","java.io.Serializable"];



        export namespace Face {

            export class Triangle {
                public __parent: any;
                public vertices: number[];

                public constructor(__parent: any, v0: number, v1: number, v2: number) {
                    this.__parent = __parent;
                    this.vertices = [0, 0, 0];
                    this.vertices[0] = v0;
                    this.vertices[1] = v1;
                    this.vertices[2] = v2;
                }
            }
            Triangle["__class"] = "com.vzome.core.math.Polyhedron.Face.Triangle";

        }


        export class Views {
            constructor() {
            }
        }
        Views["__class"] = "com.vzome.core.math.Polyhedron.Views";


        export namespace Views {

            export interface UnityMesh {            }

            export interface Triangles {            }

            export interface Polygons {            }
        }

    }

}

