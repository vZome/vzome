/* Generated from Java with JSweet 3.2.0-SNAPSHOT - http://www.jsweet.org */
namespace com.vzome.core.viewing {
    export abstract class AbstractShapes implements com.vzome.core.editor.api.Shapes {
        /*private*/ strutShapesByLengthAndOrbit: java.util.Map<com.vzome.core.math.symmetry.Direction, java.util.Map<com.vzome.core.algebra.AlgebraicNumber, com.vzome.core.math.Polyhedron>>;

        /*private*/ strutGeometriesByOrbit: java.util.Map<com.vzome.core.math.symmetry.Direction, com.vzome.core.parts.StrutGeometry>;

        /*private*/ panelShapes: java.util.Map<number, java.util.Map<com.vzome.core.algebra.AlgebraicNumber, java.util.Map<com.vzome.core.math.symmetry.Direction, java.util.HashMap<java.util.List<com.vzome.core.algebra.AlgebraicVector>, com.vzome.core.math.Polyhedron>>>>;

        mPkgName: string;

        mName: string;

        alias: string;

        mSymmetry: com.vzome.core.math.symmetry.Symmetry;

        mConnectorGeometry: com.vzome.core.math.Polyhedron;

        public constructor(pkgName: string, name: string, alias: string, symm: com.vzome.core.math.symmetry.Symmetry) {
            this.strutShapesByLengthAndOrbit = <any>(new java.util.HashMap<any, any>());
            this.strutGeometriesByOrbit = <any>(new java.util.HashMap<any, any>());
            this.panelShapes = <any>(new java.util.HashMap<any, any>());
            if (this.mPkgName === undefined) { this.mPkgName = null; }
            if (this.mName === undefined) { this.mName = null; }
            if (this.alias === undefined) { this.alias = null; }
            if (this.mSymmetry === undefined) { this.mSymmetry = null; }
            if (this.mConnectorGeometry === undefined) { this.mConnectorGeometry = null; }
            this.mPkgName = pkgName;
            this.mName = name;
            this.alias = alias;
            this.mConnectorGeometry = null;
            this.mSymmetry = symm;
        }

        /**
         * 
         * @return {string}
         */
        public toString(): string {
            return /* getSimpleName */(c => typeof c === 'string' ? (<any>c).substring((<any>c).lastIndexOf('.')+1) : c["__class"] ? c["__class"].substring(c["__class"].lastIndexOf('.')+1) : c["name"].substring(c["name"].lastIndexOf('.')+1))((<any>this.constructor)) + "( Symmetry:" + this.mSymmetry.getName() + ", PkgName:" + this.mPkgName + ", Name:" + this.mName + (this.alias == null ? "" : (", Alias:" + this.alias)) + " )";
        }

        /**
         * 
         * @param {com.vzome.core.math.symmetry.Direction} dir
         * @return {com.vzome.core.construction.Color}
         */
        public getColor(dir: com.vzome.core.math.symmetry.Direction): com.vzome.core.construction.Color {
            return null;
        }

        /**
         * 
         * @return {boolean}
         */
        public hasColors(): boolean {
            return false;
        }

        createStrutGeometry(dir: com.vzome.core.math.symmetry.Direction): com.vzome.core.parts.StrutGeometry {
            return new com.vzome.core.parts.FastDefaultStrutGeometry(dir);
        }

        /*private*/ getStrutGeometry(orbit: com.vzome.core.math.symmetry.Direction): com.vzome.core.parts.StrutGeometry {
            let orbitStrutGeometry: com.vzome.core.parts.StrutGeometry = this.strutGeometriesByOrbit.get(orbit);
            if (orbitStrutGeometry == null){
                orbitStrutGeometry = this.createStrutGeometry(orbit);
                this.strutGeometriesByOrbit.put(orbit, orbitStrutGeometry);
            }
            return orbitStrutGeometry;
        }

        public getStrutGeometries(): java.util.Map<string, com.vzome.core.parts.StrutGeometry> {
            return <any>(java.util.Arrays.stream<any>(this.mSymmetry.getDirectionNames()).collect<any, any>(java.util.stream.Collectors.toMap<any, any, any>(<any>(((funcInst: any) => { if (funcInst == null || typeof funcInst == 'function') { return funcInst } return (arg0) =>  (funcInst['apply'] ? funcInst['apply'] : funcInst) .call(funcInst, arg0)})((x=>x))), (name) => this.getStrutGeometry(this.mSymmetry.getDirection(name)))));
        }

        /**
         * 
         * @return {string}
         */
        public getName(): string {
            return this.mName;
        }

        /**
         * 
         * @return {string}
         */
        public getAlias(): string {
            return this.alias;
        }

        /**
         * 
         * @return {string}
         */
        public getPackage(): string {
            return this.mPkgName;
        }

        /**
         * 
         * @return {com.vzome.core.math.Polyhedron}
         */
        public getConnectorShape(): com.vzome.core.math.Polyhedron {
            if (this.mConnectorGeometry == null){
                this.mConnectorGeometry = this.buildConnectorShape(this.mPkgName);
                this.mConnectorGeometry.setName("ball");
            }
            return this.mConnectorGeometry;
        }

        abstract buildConnectorShape(pkgName: string): com.vzome.core.math.Polyhedron;

        /**
         * 
         * @param {com.vzome.core.math.symmetry.Direction} orbit
         * @param {*} length
         * @return {com.vzome.core.math.Polyhedron}
         */
        public getStrutShape(orbit: com.vzome.core.math.symmetry.Direction, length: com.vzome.core.algebra.AlgebraicNumber): com.vzome.core.math.Polyhedron {
            let strutShapesByLength: java.util.Map<com.vzome.core.algebra.AlgebraicNumber, com.vzome.core.math.Polyhedron> = this.strutShapesByLengthAndOrbit.get(orbit);
            if (strutShapesByLength == null){
                strutShapesByLength = <any>(new java.util.HashMap<any, any>());
                this.strutShapesByLengthAndOrbit.put(orbit, strutShapesByLength);
            }
            let lengthShape: com.vzome.core.math.Polyhedron = strutShapesByLength.get(length);
            if (lengthShape == null){
                const orbitStrutGeometry: com.vzome.core.parts.StrutGeometry = this.getStrutGeometry(orbit);
                lengthShape = orbitStrutGeometry.getStrutPolyhedron(length);
                strutShapesByLength.put(length, lengthShape);
                if (lengthShape != null){
                    lengthShape.setName(orbit.getName() + strutShapesByLength.size());
                    lengthShape.setOrbit(orbit);
                    lengthShape.setLength(orbit.getLengthInUnits(length));
                }
            }
            return lengthShape;
        }

        /**
         * 
         * @return {*}
         */
        public getSymmetry(): com.vzome.core.math.symmetry.Symmetry {
            return this.mSymmetry;
        }

        /*private*/ makePanelPolyhedron(vertices: java.lang.Iterable<com.vzome.core.algebra.AlgebraicVector>, oneSided: boolean): com.vzome.core.math.Polyhedron {
            const poly: com.vzome.core.math.Polyhedron = new com.vzome.core.math.Polyhedron(this.mSymmetry.getField());
            poly.setPanel(true);
            let arity: number = 0;
            for(let index=vertices.iterator();index.hasNext();) {
                let gv = index.next();
                {
                    arity++;
                    poly.addVertex(gv);
                }
            }
            if (poly.getVertexList().size() < arity)return null;
            const front: com.vzome.core.math.Polyhedron.Face = poly.newFace();
            const back: com.vzome.core.math.Polyhedron.Face = poly.newFace();
            for(let i: number = 0; i < arity; i++) {{
                const j: number = i;
                front.add(j);
                back.add(0, j);
            };}
            poly.addFace(front);
            if (!oneSided)poly.addFace(back);
            return poly;
        }

        /**
         * 
         * @param {number} vertexCount
         * @param {*} quadrea
         * @param {com.vzome.core.math.symmetry.Axis} zone
         * @param {*} vertices
         * @param {boolean} oneSidedPanels
         * @return {com.vzome.core.math.Polyhedron}
         */
        public getPanelShape(vertexCount: number, quadrea: com.vzome.core.algebra.AlgebraicNumber, zone: com.vzome.core.math.symmetry.Axis, vertices: java.lang.Iterable<com.vzome.core.algebra.AlgebraicVector>, oneSidedPanels: boolean): com.vzome.core.math.Polyhedron {
            let map1: java.util.Map<com.vzome.core.algebra.AlgebraicNumber, java.util.Map<com.vzome.core.math.symmetry.Direction, java.util.HashMap<java.util.List<com.vzome.core.algebra.AlgebraicVector>, com.vzome.core.math.Polyhedron>>> = this.panelShapes.get(vertexCount);
            if (map1 == null){
                map1 = <any>(new java.util.HashMap<any, any>());
                this.panelShapes.put(vertexCount, map1);
            }
            let map2: java.util.Map<com.vzome.core.math.symmetry.Direction, java.util.HashMap<java.util.List<com.vzome.core.algebra.AlgebraicVector>, com.vzome.core.math.Polyhedron>> = map1.get(quadrea);
            if (map2 == null){
                map2 = <any>(new java.util.HashMap<any, any>());
                map1.put(quadrea, map2);
            }
            const orbit: com.vzome.core.math.symmetry.Direction = zone.getDirection();
            let map3: java.util.HashMap<java.util.List<com.vzome.core.algebra.AlgebraicVector>, com.vzome.core.math.Polyhedron> = map2.get(orbit);
            if (map3 == null){
                map3 = <any>(new java.util.HashMap<any, any>());
                map2.put(orbit, map3);
            }
            const orientation: number = zone.getOrientation();
            const perm: com.vzome.core.math.symmetry.Permutation = this.mSymmetry.getPermutation(orientation).inverse();
            const inverseOrientation: number = perm.mapIndex(0);
            const inverseTrans: com.vzome.core.algebra.AlgebraicMatrix = this.mSymmetry.getMatrix(inverseOrientation);
            const canonicalVertices: java.util.List<com.vzome.core.algebra.AlgebraicVector> = <any>(new java.util.ArrayList<any>());
            for(let index=vertices.iterator();index.hasNext();) {
                let vertex = index.next();
                {
                    canonicalVertices.add(inverseTrans.timesColumn(vertex));
                }
            }
            let shape: com.vzome.core.math.Polyhedron = map3.get(canonicalVertices);
            if (shape == null){
                shape = this.makePanelPolyhedron(canonicalVertices, oneSidedPanels);
                map3.put(canonicalVertices, shape);
            }
            return shape;
        }
    }
    AbstractShapes["__class"] = "com.vzome.core.viewing.AbstractShapes";
    AbstractShapes["__interfaces"] = ["com.vzome.core.editor.api.Shapes"];


}

