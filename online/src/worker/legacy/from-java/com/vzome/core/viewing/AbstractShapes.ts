import { java, javaemul } from "../../../../../candies/j4ts-2.1.0-SNAPSHOT/bundle.js";
import { AlgebraicField } from "../algebra/AlgebraicField.js";
import { AlgebraicMatrix } from "../algebra/AlgebraicMatrix.js";
import { AlgebraicNumber } from "../algebra/AlgebraicNumber.js";
import { AlgebraicVector } from "../algebra/AlgebraicVector.js";
import { Color } from "../construction/Color.js";
import { Shapes } from "../editor/api/Shapes.js";
import { Polyhedron } from "../math/Polyhedron.js";
import { Axis } from "../math/symmetry/Axis.js";
import { Direction } from "../math/symmetry/Direction.js";
import { Permutation } from "../math/symmetry/Permutation.js";
import { Symmetry } from "../math/symmetry/Symmetry.js";
import { FastDefaultStrutGeometry } from "../parts/FastDefaultStrutGeometry.js";
import { StrutGeometry } from "../parts/StrutGeometry.js";
import { RealZomeScaling } from "../render/RealZomeScaling.js";

export abstract class AbstractShapes implements Shapes {
    /*private*/ strutShapesByLengthAndOrbit: java.util.Map<Direction, java.util.Map<AlgebraicNumber, Polyhedron>>;

    /*private*/ strutGeometriesByOrbit: java.util.Map<Direction, StrutGeometry>;

    /*private*/ panelShapes: java.util.Map<number, java.util.Map<AlgebraicNumber, java.util.Map<Direction, java.util.HashMap<java.util.List<AlgebraicVector>, Polyhedron>>>>;

    mPkgName: string;

    mName: string;

    alias: string;

    mSymmetry: Symmetry;

    mConnectorGeometry: Polyhedron;

    public constructor(pkgName: string, name: string, alias: string, symm: Symmetry) {
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
     * @param {Direction} dir
     * @return {Color}
     */
    public getColor(dir: Direction): Color {
        return null;
    }

    /**
     * 
     * @return {boolean}
     */
    public hasColors(): boolean {
        return false;
    }

    createStrutGeometry(dir: Direction): StrutGeometry {
        return new FastDefaultStrutGeometry(dir);
    }

    /*private*/ getStrutGeometry(orbit: Direction): StrutGeometry {
        let orbitStrutGeometry: StrutGeometry = this.strutGeometriesByOrbit.get(orbit);
        if (orbitStrutGeometry == null){
            orbitStrutGeometry = this.createStrutGeometry(orbit);
            this.strutGeometriesByOrbit.put(orbit, orbitStrutGeometry);
        }
        return orbitStrutGeometry;
    }

    public getStrutGeometries(): java.util.Map<string, StrutGeometry> {
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
     * @return {Polyhedron}
     */
    public getConnectorShape(): Polyhedron {
        if (this.mConnectorGeometry == null){
            this.mConnectorGeometry = this.buildConnectorShape(this.mPkgName);
            this.mConnectorGeometry.setName("ball");
            this.mConnectorGeometry.setShapeKey(this.mSymmetry.getName() + ":" + this.mPkgName + ":ball");
        }
        return this.mConnectorGeometry;
    }

    abstract buildConnectorShape(pkgName: string): Polyhedron;

    /**
     * 
     * @param {Direction} orbit
     * @param {*} length
     * @return {Polyhedron}
     */
    public getStrutShape(orbit: Direction, length: AlgebraicNumber): Polyhedron {
        let strutShapesByLength: java.util.Map<AlgebraicNumber, Polyhedron> = this.strutShapesByLengthAndOrbit.get(orbit);
        if (strutShapesByLength == null){
            strutShapesByLength = <any>(new java.util.HashMap<any, any>());
            this.strutShapesByLengthAndOrbit.put(orbit, strutShapesByLength);
        }
        let lengthShape: Polyhedron = strutShapesByLength.get(length);
        if (lengthShape == null){
            const orbitStrutGeometry: StrutGeometry = this.getStrutGeometry(orbit);
            lengthShape = orbitStrutGeometry.getStrutPolyhedron(length);
            strutShapesByLength.put(length, lengthShape);
            if (lengthShape != null){
                lengthShape.setName(orbit.getName() + strutShapesByLength.size());
                lengthShape.setOrbit(orbit);
                lengthShape.setLength(orbit.getLengthInUnits(length));
                lengthShape.setShapeKey(this.mSymmetry.getName() + ":" + orbit.getName() + ":" + length.toString(AlgebraicField.DEFAULT_FORMAT));
            }
        }
        return lengthShape;
    }

    /**
     * 
     * @return {*}
     */
    public getSymmetry(): Symmetry {
        return this.mSymmetry;
    }

    /*private*/ makePanelPolyhedron(vertices: java.lang.Iterable<AlgebraicVector>, oneSided: boolean): Polyhedron {
        const poly: Polyhedron = new Polyhedron(this.mSymmetry.getField());
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
        const front: Polyhedron.Face = poly.newFace();
        const back: Polyhedron.Face = poly.newFace();
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
     * @param {Axis} zone
     * @param {*} vertices
     * @param {boolean} oneSidedPanels
     * @return {Polyhedron}
     */
    public getPanelShape(vertexCount: number, quadrea: AlgebraicNumber, zone: Axis, vertices: java.lang.Iterable<AlgebraicVector>, oneSidedPanels: boolean): Polyhedron {
        let map1: java.util.Map<AlgebraicNumber, java.util.Map<Direction, java.util.HashMap<java.util.List<AlgebraicVector>, Polyhedron>>> = this.panelShapes.get(vertexCount);
        if (map1 == null){
            map1 = <any>(new java.util.HashMap<any, any>());
            this.panelShapes.put(vertexCount, map1);
        }
        let map2: java.util.Map<Direction, java.util.HashMap<java.util.List<AlgebraicVector>, Polyhedron>> = map1.get(quadrea);
        if (map2 == null){
            map2 = <any>(new java.util.HashMap<any, any>());
            map1.put(quadrea, map2);
        }
        const orbit: Direction = zone.getDirection();
        let map3: java.util.HashMap<java.util.List<AlgebraicVector>, Polyhedron> = map2.get(orbit);
        if (map3 == null){
            map3 = <any>(new java.util.HashMap<any, any>());
            map2.put(orbit, map3);
        }
        const orientation: number = zone.getOrientation();
        const perm: Permutation = this.mSymmetry.getPermutation(orientation).inverse();
        const inverseOrientation: number = perm.mapIndex(0);
        const inverseTrans: AlgebraicMatrix = this.mSymmetry.getMatrix(inverseOrientation);
        const canonicalVertices: java.util.List<AlgebraicVector> = <any>(new java.util.ArrayList<any>());
        for(let index=vertices.iterator();index.hasNext();) {
            let vertex = index.next();
            {
                canonicalVertices.add(inverseTrans.timesColumn(vertex));
            }
        }
        let shape: Polyhedron = map3.get(canonicalVertices);
        if (shape == null){
            shape = this.makePanelPolyhedron(canonicalVertices, oneSidedPanels);
            if (shape != null)shape.setShapeKey(shape.deriveGeometricKey());
            map3.put(canonicalVertices, shape);
        }
        return shape;
    }

    /**
     * 
     * @return {number}
     */
    public getCmScaling(): number {
        return RealZomeScaling.RZOME_CM_SCALING;
    }
}
AbstractShapes["__class"] = "com.vzome.core.viewing.AbstractShapes";
AbstractShapes["__interfaces"] = ["com.vzome.core.editor.api.Shapes"];
